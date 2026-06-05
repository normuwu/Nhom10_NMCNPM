package coganhgame.Controller;

import coganhgame.ComponentView.PieceComp;
import coganhgame.ComponentView.TileComp;
import coganhgame.Model.Game.Game;
import coganhgame.Model.Move.Move;
import coganhgame.Model.Move.MoveResult;
import coganhgame.Model.Piece;
import coganhgame.Model.Player.HumanPlayer;
import coganhgame.Model.Tile.Tile;
import coganhgame.Utilities.AdaptiveUtilities;
import coganhgame.Utilities.Constants;
import coganhgame.Utilities.ViewUtilities;
import coganhgame.Model.Game.MatchRecord;
import coganhgame.Model.Game.UndoSnapshot;
import coganhgame.Model.Game.GameWithBot;
import coganhgame.Model.Player.BotPlayer;
import coganhgame.Model.Player.Player;
import coganhgame.Service.MatchHistoryManager;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import coganhgame.Service.UndoRedoManager;

public class GameController {
    private final Game game;
    @FXML
    public Pane boardPane;
    @FXML
    public Button btnExit;
    @FXML
    public Label currentNameLabel;
    @FXML
    public VBox vbBoard;
    @FXML
    public Label currentLabel;
    @FXML
    public ProgressBar prbTimeLeft;
    @FXML
    public Label lblTotalPiecesRed;
    @FXML
    public Label lblTotalPiecesBlue;
    @FXML
    public Label lblTotalTimeBlue;
    @FXML
    public Label lblTotalTimeRed;
    @FXML
    public VBox vbRed;
    @FXML
    public VBox vbBlue;

    // Giữ lại các biến FXML này để tránh lỗi nếu file giao diện (.fxml) chưa kịp xóa chúng
    @FXML
    public Label lblBotLevel;
    @FXML
    public HBox hbBotLevel;
    @FXML
    public Label botPositionCountLabel;

    @FXML
    public Label player1NameLabel;
    @FXML
    public Label player2NameLabel;
    @FXML
    public Button btnReset;
    @FXML
    public Button btnOpenRed;
    @FXML
    public Button btnOpenBlue;
    @FXML
    public Button btnPassBlue;
    @FXML
    public Button btnPassRed;
    @FXML
    public HBox hbOpenRed;
    @FXML
    public HBox hbOpenBlue;

    private Tile currentTile;
    private Tile draggedTile;

    private final Group tileCompGroup = new Group();
    private final Group pieceCompGroup = new Group();
    private final TileComp[][] viewBoard = new TileComp[Constants.WIDTH][Constants.HEIGHT];
    private final Map<Piece, PieceComp> pieceMap = new HashMap<>();

    // Undo/Redo
    private final UndoRedoManager undoRedoManager = new UndoRedoManager();

    // Match History
    private long gameStartTime;
    private String gameMode = "2 Players";
    private int botLevel = 0;

    private final ChangeListener<Number> timeLeftListener = (observable, oldValue, newValue) -> {
        if (newValue.intValue() <= 0) {
            clearOpenHighlight();
            switchPlayer();
        }
    };
    private final Timeline timeline = new Timeline();
    private ChangeListener<? super Number> timeLeftListener;


    // Sửa GameController
    public GameController(String player1Name, String player2Name, int timeLimit) {
        this.game = new Game(player1Name, player2Name, timeLimit);
        this.gameMode = "2 Players";
        this.botLevel = 0;
    }

    public GameController(String player1Name, int timeLimit, int botLevel) {
        this.game = new GameWithBot(player1Name, timeLimit, botLevel);
        this.gameMode = "Play With Bot";
        this.botLevel = botLevel;
    }

    public GameController(Game game) {
        this.game = game;
        this.gameMode = (game instanceof GameWithBot) ? "Play With Bot" : "2 Players";
        this.botLevel = (game.getPlayer2() instanceof BotPlayer) ? ((BotPlayer) game.getPlayer2()).getBotLevel() : 0;
    }

    // Thêm initialized
    @FXML
    private boolean initialized = false;

    @FXML
    public void initialize() {
        initViewBoard();
        boardPane.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        boardPane.setPrefSize(AdaptiveUtilities.BOARD_WIDTH, AdaptiveUtilities.BOARD_HEIGHT);
        boardPane.getChildren().addAll(tileCompGroup, pieceCompGroup);

        timeline.getKeyFrames().addAll(
                new KeyFrame(Duration.ZERO, new KeyValue(prbTimeLeft.progressProperty(), 1)),
                new KeyFrame(Duration.seconds(game.getTimeLimit()), new KeyValue(prbTimeLeft.progressProperty(), 0))
        );

        // Ẩn các thành phần giao diện của Bot đi
        if (botPositionCountLabel != null) vbBlue.getChildren().remove(botPositionCountLabel);
        if (hbBotLevel != null) vbBlue.getChildren().remove(hbBotLevel);
        if (player2NameLabel != null) HBox.setMargin(player2NameLabel, new Insets(0, 0, 10, 0));

        vbRed.getChildren().remove(hbOpenRed);
        vbBlue.getChildren().remove(hbOpenBlue);

        if (game.isOpening()) {
            ArrayList<Tile> openTiles = game.checkOpeningTile(game.getOpeningTile(), game.getOpponent().getSide());
            viewBoard[game.getOpeningTile().getRow()][game.getOpeningTile().getCol()].highlight(game.getCurrentPlayer().getSide());
            for (Tile openTile : openTiles) {
                PieceComp openPieceComp = pieceMap.get(openTile.getPiece());
                openPieceComp.highlightOpen();
            }
        }

        // Thêm bind undo/redo buttom
        // Bind Undo/Redo button disable property (only once to avoid IllegalStateException)
        if (!initialized) {
            btnUndo.disableProperty().bind(undoRedoManager.canUndoProperty().not());
            btnRedo.disableProperty().bind(undoRedoManager.canRedoProperty().not());
            initialized = true;
        }
        undoRedoManager.clear();
        gameStartTime = System.currentTimeMillis();

        ((HumanPlayer) game.getCurrentPlayer()).getTimeLeft().addListener(timeLeftListener);
        game.getCurrentPlayer().playTimer();
        updateCurrentPlayerLabel();

        currentLabel.setText("'s turn");
        player1NameLabel.setText(game.getPlayer1().getName());
        player1NameLabel.setTextFill(ViewUtilities.RED_PIECE_COLOR);
        player2NameLabel.setText(game.getPlayer2().getName());
        player2NameLabel.setTextFill(ViewUtilities.BUE_PIECE_COLOR);

        lblTotalPiecesRed.setText("x " + game.getPlayer1().getTotalPiece());
        lblTotalPiecesBlue.setText("x " + game.getPlayer2().getTotalPiece());
        lblTotalTimeRed.setText("Total time: " + ((double) game.getPlayer1().getTotalTime() / 1000) + "s");
        lblTotalTimeBlue.setText("Total time: " + ((double) game.getPlayer2().getTotalTime() / 1000) + "s");
        runTimer();
    }

    private void initViewBoard() {
        Tile[][] modelBoard = game.getBoard();

        for (int row = 0; row < Constants.HEIGHT; row++) {
            for (int col = 0; col < Constants.WIDTH; col++) {
                TileComp tileComp = new TileComp(row, col);
                viewBoard[row][col] = tileComp;

                if (col == 0 || col == Constants.WIDTH - 1) {
                    Label label = new Label(String.valueOf(Constants.HEIGHT - row));
                    label.setTranslateX(col == 0 ? -(AdaptiveUtilities.PIECE_SIZE * 1.5) : (AdaptiveUtilities.PIECE_SIZE * 1.5));
                    label.setFont(ViewUtilities.COOR_FONT);
                    tileComp.getChildren().add(label);
                }
                if (row == 0 || row == Constants.HEIGHT - 1) {
                    Label label = new Label(String.valueOf((char) (col + 65)));
                    label.setTranslateY(row == 0 ? -(AdaptiveUtilities.PIECE_SIZE * 1.5) : (AdaptiveUtilities.PIECE_SIZE * 1.5 + 2));
                    label.setFont(ViewUtilities.COOR_FONT);
                    tileComp.getChildren().add(label);
                }

                Tile modelTile = modelBoard[row][col];
                if (modelTile.hasPiece()) {
                    PieceComp pieceComp = makePieceComp(modelTile.getPiece().getSide(), row, col);
                    pieceCompGroup.getChildren().add(pieceComp);
                    pieceMap.put(modelTile.getPiece(), pieceComp);
                    if (modelTile.getPiece().getSide() != game.getCurrentPlayer().getSide()) {
                        pieceComp.setDisablePiece();
                    }
                }

                tileCompGroup.getChildren().add(tileComp);
            }
        }
    }

    private PieceComp makePieceComp(boolean side, int row, int col) {
        PieceComp pieceComp = new PieceComp(side, row, col);
        AtomicReference<Double> mouseX = new AtomicReference<>((double) 0);
        AtomicReference<Double> mouseY = new AtomicReference<>((double) 0);

        pieceComp.getEllipse().setOnMousePressed(e -> {
            mouseX.set(e.getSceneX());
            mouseY.set(e.getSceneY());
            int rowPressed = toBoardPos(pieceComp.getLayoutY());
            int colPressed = toBoardPos(pieceComp.getLayoutX());
            Tile currentPressedTile = game.getBoard()[rowPressed][colPressed];

            if (currentTile == null) {
                currentTile = currentPressedTile;
            }
            if (!currentTile.equals(currentPressedTile) && !game.isOpening()) {
                for (Tile move : currentTile.getAvailableMoves(game.getBoard())) {
                    viewBoard[move.getRow()][move.getCol()].removeHighlight();
                }
            }

            currentTile = game.getBoard()[rowPressed][colPressed];
            if (!game.isOpening()) {
                for (Tile move : currentTile.getAvailableMoves(game.getBoard())) {
                    viewBoard[move.getRow()][move.getCol()].highlight(currentTile.getPiece().getSide());
                }
            }
            pieceComp.toFront();
        });

        pieceComp.getEllipse().setOnMouseDragged(e -> {
            pieceComp.relocate(e.getSceneX() - mouseX.get() + pieceComp.getOldX(), e.getSceneY() - mouseY.get() + pieceComp.getOldY());
            int rowDragged = toBoardPos(pieceComp.getLayoutY());
            int colDragged = toBoardPos(pieceComp.getLayoutX());
            if (rowDragged < 0 || rowDragged >= Constants.HEIGHT || colDragged < 0 || colDragged >= Constants.WIDTH) {
                return;
            }
            if (draggedTile == null) {
                draggedTile = game.getBoard()[rowDragged][colDragged];
            } else if (rowDragged == draggedTile.getRow() && colDragged == draggedTile.getCol()) {
                return;
            }
            viewBoard[draggedTile.getRow()][draggedTile.getCol()].unfillHighlighter();
            draggedTile = game.getBoard()[rowDragged][colDragged];
            if (draggedTile.equals(currentTile) || currentTile.getAvailableMoves(game.getBoard()).contains(draggedTile)) {
                viewBoard[draggedTile.getRow()][draggedTile.getCol()].fillHighlighter();
            }
        });

        pieceComp.getEllipse().setOnMouseReleased(e -> {
            if (!game.isOpening()) {
                for (Tile move : currentTile.getAvailableMoves(game.getBoard())) {
                    viewBoard[move.getRow()][move.getCol()].removeHighlight();
                }
            }

            int newRow = toBoardPos(pieceComp.getLayoutY());
            int newCol = toBoardPos(pieceComp.getLayoutX());
            int oldRow = toBoardPos(pieceComp.getOldY());
            int oldCol = toBoardPos(pieceComp.getOldX());

            if ((oldRow == newRow && oldCol == newCol) || (newRow < 0 || newRow >= Constants.HEIGHT || newCol < 0 || newCol >= Constants.WIDTH)) {
                pieceComp.abortMove();
                return;
            }

            Move move = new Move(game.getBoard()[oldRow][oldCol], game.getBoard()[newRow][newCol]);
            if (game.isOpening() && move.toTile() != game.getOpeningTile()) {
                pieceComp.abortMove();
                return;
            } else {
                game.setOpeningTile(null);
            }
            clearOpenHighlight();

            // Thêm undosnapshot
            // Capture snapshot BEFORE executing the move
            UndoSnapshot snapshot = captureSnapshot();

            MoveResult moveResult = game.processMove(move);

            if (moveResult.isValidMove()) {
                pieceComp.move(newRow, newCol);
                if (moveResult.capturedPieces() != null) {
                    for (Piece capturedModelPiece : moveResult.capturedPieces()) {
                        PieceComp capturedPieceComp = pieceMap.get(capturedModelPiece);
                        capturedPieceComp.flipSide();
                    }
                    lblTotalPiecesRed.setText("x " + game.getPlayer1().getTotalPiece());
                    lblTotalPiecesBlue.setText("x " + game.getPlayer2().getTotalPiece());
                }

                ArrayList<Tile> openTiles = game.checkOpeningTile(move.fromTile(), game.getCurrentPlayer().getSide());
                if (!openTiles.isEmpty()) {
                    if (game.getCurrentPlayer().getSide() == Constants.RED_SIDE) {
                        vbRed.getChildren().add(hbOpenRed);
                    } else {
                        vbBlue.getChildren().add(hbOpenBlue);
                    }
                    viewBoard[move.fromTile().getRow()][move.fromTile().getCol()].highlight(game.getOpponent().getSide());
                    for (Tile openTile : openTiles) {
                        PieceComp openPieceComp = pieceMap.get(openTile.getPiece());
                        openPieceComp.highlightOpen();
                    }
                    for (PieceComp piece : pieceMap.values()) {
                        if (piece.getSide() == game.getCurrentPlayer().getSide()) {
                            piece.setDisablePiece();
                        }
                    }
                } else {
                    //thêm điều kiện undoredomanager
                    // Push snapshot only after a successful move
                    if (moveResult.isValidMove()) {
                        undoRedoManager.pushSnapshot(snapshot);
                    }
                    switchPlayer();
                }
            } else {
                pieceComp.abortMove();
            }
        });
        return pieceComp;
    }

    private int toBoardPos(double pixel) {
        return (int) ((int) (pixel + AdaptiveUtilities.TILE_SIZE / 2) / AdaptiveUtilities.TILE_SIZE);
    }

    // Đã tối ưu hóa hàm đổi lượt: loại bỏ hoàn toàn các dòng check Bot
    private void switchPlayer() {
        game.getCurrentPlayer().pauseTimer();
        ((HumanPlayer) game.getCurrentPlayer()).getTimeLeft().removeListener(timeLeftListener);
        ((HumanPlayer) game.getCurrentPlayer()).setTimeLeft(game.getTimeLimit() * 1000);

        if (game.getCurrentPlayer().getSide() == Constants.RED_SIDE) {
            lblTotalTimeRed.setText("Total time: " + ((double) game.getCurrentPlayer().getTotalTime() / 1000) + "s");
        } else {
            lblTotalTimeBlue.setText("Total time: " + ((double) game.getCurrentPlayer().getTotalTime() / 1000) + "s");
        }

        if (game.isGameOver()) {
            endGame();
            return;
        }

        game.switchPlayer();
        updateCurrentPlayerLabel();

        ((HumanPlayer) game.getCurrentPlayer()).getTimeLeft().addListener(timeLeftListener);
        game.getCurrentPlayer().playTimer();

        for (PieceComp piece : pieceMap.values()) {
            if (piece.getSide() == game.getCurrentPlayer().getSide()) {
                piece.setEnablePiece();
            } else {
                piece.setDisablePiece();
            }
        }
        runTimer();
    }

    private void updateCurrentPlayerLabel() {
        if (currentNameLabel != null && game != null && game.getCurrentPlayer() != null) {
            currentNameLabel.setText(game.getCurrentPlayer().getName());
            currentNameLabel.setTextFill(game.getCurrentPlayer().getSide() == Constants.RED_SIDE ? ViewUtilities.RED_PIECE_COLOR : ViewUtilities.BUE_PIECE_COLOR);
        }
    }

    private void runTimer() {
        timeline.stop();
        prbTimeLeft.setProgress(1);
        if (game.getCurrentPlayer().getSide() == Constants.RED_SIDE) {
            prbTimeLeft.setRotate(180);
            prbTimeLeft.setStyle("-fx-accent: #E21818;");
        } else {
            prbTimeLeft.setRotate(0);
            prbTimeLeft.setStyle("-fx-accent: #2666CF;");
        }
        timeline.playFromStart();
    }

  private void endGame() {
        prbTimeLeft.setProgress(1);
        timeline.stop();
        for (PieceComp piece : pieceMap.values()) {
            piece.setDisablePiece();
        }
        currentLabel.setText(" win");
        if (game.getCurrentPlayer().getSide() == Constants.RED_SIDE) {
            prbTimeLeft.setStyle("-fx-accent: #E21818;");
        } else {
            prbTimeLeft.setStyle("-fx-accent: #2666CF;");
        }

        // Save match record
         Player winner = game.getCurrentPlayer();
         Player loser = game.getOpponent();
        long duration = (System.currentTimeMillis() - gameStartTime) / 1000;
        MatchRecord record = new MatchRecord(
              game.getPlayer1().getName(),
              game.getPlayer2().getName(),
              winner.getName(),
              gameMode,
              botLevel,
              duration
        );
        MatchHistoryManager.saveRecord(record);
    }


    
  
    private void clearOpenHighlight() {
        if (currentTile == null) {
            return;
        }
        viewBoard[currentTile.getRow()][currentTile.getCol()].removeHighlight();
        for (PieceComp pieceComp : pieceMap.values()) {
            pieceComp.removeHighlightOpen();
        }
    }

    @FXML
    public void onBtnResetClick() {
        game.getCurrentPlayer().pauseTimer();
        timeline.pause();

        if (ViewUtilities.showConfirm("Reset Confirmation", "Are you sure you want to reset the game?")) {
            game.resetGame();
            boardPane.getChildren().clear();
            tileCompGroup.getChildren().clear();
            pieceCompGroup.getChildren().clear();
            pieceMap.clear();
            initialize();
        } else {
            game.getCurrentPlayer().playTimer();
            timeline.play();
        }
    }

    @FXML
    public void onBtnOpenClick() {
        vbRed.getChildren().remove(hbOpenRed);
        vbBlue.getChildren().remove(hbOpenBlue);
        game.setOpeningTile(currentTile);
        switchPlayer();
    }

    @FXML
    public void onBtnPassClick() {
        vbRed.getChildren().remove(hbOpenRed);
        vbBlue.getChildren().remove(hbOpenBlue);
        clearOpenHighlight();
        switchPlayer();
    }

    @FXML
    public void onBtnExitClick(ActionEvent actionEvent) {
        game.getCurrentPlayer().pauseTimer();
        timeline.pause();

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit Confirmation");
        alert.setHeaderText(null);

        ButtonType yesButton = new ButtonType("Yes", ButtonBar.ButtonData.YES);
        ButtonType noButton = new ButtonType("No", ButtonBar.ButtonData.NO);
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        if (!game.isGameOver()) {
            alert.getButtonTypes().setAll(yesButton, noButton, cancelButton);
            alert.setContentText("The game is not over yet. Do you want to save the game before exit?");
            ButtonType result = alert.showAndWait().orElse(cancelButton);
            if (result == yesButton) {
                game.saveGame();
                Node source = (Node) actionEvent.getSource();
                Stage currentStage = (Stage) source.getScene().getWindow();
                currentStage.hide();
            } else if (result == noButton) {
                Node source = (Node) actionEvent.getSource();
                Stage currentStage = (Stage) source.getScene().getWindow();
                currentStage.hide();
            } else {
                game.getCurrentPlayer().playTimer();
                timeline.play();
            }
        } else {
            alert.getButtonTypes().setAll(yesButton, noButton);
            alert.setContentText("Are you sure you want to exit?");
            ButtonType result = alert.showAndWait().orElse(noButton);
            if (result == yesButton) {
                Node source = (Node) actionEvent.getSource();
                Stage currentStage = (Stage) source.getScene().getWindow();
                currentStage.hide();
            }
        }
    }

    //Thêm botmakemove
    private void botMakeMove() {
        Task<Move> botMoveTask = new Task<>() {
            @Override
            protected Move call() {
                BotPlayer botPlayer = (BotPlayer) game.getCurrentPlayer();
                botPlayer.playTimer();
                Move botMove = botPlayer.getBestMove((GameWithBot) game);
                botPlayer.pauseTimer();
                return botMove;
            }
        };

        botMoveTask.setOnSucceeded(event -> {
            Move botMove = botMoveTask.getValue();

            // Push snapshot before bot executes the move
            UndoSnapshot botSnapshot = captureSnapshot();
            undoRedoManager.pushSnapshot(botSnapshot);

            PieceComp botPieceComp = pieceMap.get(botMove.fromTile().getPiece());
            MoveResult botMoveResult = game.processMove(botMove);
            clearOpenHighlight();
            botPieceComp.slowMove(botMove.toTile().getRow(), botMove.toTile().getCol());

            PauseTransition pause = new PauseTransition(Duration.seconds(Constants.BOT_MOVE_DELAY));
            pause.setOnFinished(e -> {
                if (botMoveResult.capturedPieces() != null) {
                    for (Piece capturedModelPiece : botMoveResult.capturedPieces()) {
                        PieceComp capturedPieceComp = pieceMap.get(capturedModelPiece);
                        capturedPieceComp.flipSide();
                    }
                }
                botPositionCount = BotPlayer.positionCount;
                updateBotPositionCountLabel();
                lblTotalTimeBlue.setText("Total time: " + ((double) game.getPlayer2().getTotalTime() / 1000) + "s");
                BotPlayer.positionCount = 0;
                switchPlayer();
            });
            pause.play();
        });

        executor.execute(botMoveTask);
    }

    // UNDO/REDO METHODS
    /** Capture current game state for Undo */
    private UndoSnapshot captureSnapshot() {
        return new UndoSnapshot(
                game.getBoard(),
                game.getPlayer1().getTotalPiece(),
                game.getPlayer2().getTotalPiece(),
                game.getCurrentPlayer() == game.getPlayer1(),
                game.isOpening(),
                game.isOpening() ? game.getOpeningTile().getRow() : -1,
                game.isOpening() ? game.getOpeningTile().getCol() : -1
        );
    }

    /** Restore view after Undo/Redo snapshot restoration */
    private void restoreViewFromSnapshot(UndoSnapshot snapshot) {
        snapshot.restore(game, game.getBoard());
        pieceCompGroup.getChildren().clear();
        pieceMap.clear();
        Tile[][] board = game.getBoard();
        for (int r = 0; r < Constants.HEIGHT; r++) {
            for (int c = 0; c < Constants.WIDTH; c++) {
                if (board[r][c].hasPiece()) {
                    PieceComp pc = makePieceComp(board[r][c].getPiece().getSide(), r, c);
                    pieceCompGroup.getChildren().add(pc);
                    pieceMap.put(board[r][c].getPiece(), pc);
                }
            }
        }
        for (PieceComp pc : pieceMap.values()) {
            if (pc.getSide() == game.getCurrentPlayer().getSide()) pc.setEnablePiece();
            else pc.setDisablePiece();
        }
        lblTotalPiecesRed.setText("x " + game.getPlayer1().getTotalPiece());
        lblTotalPiecesBlue.setText("x " + game.getPlayer2().getTotalPiece());
        lblTotalTimeRed.setText("Total time: " + ((double) game.getPlayer1().getTotalTime() / 1000) + "s");
        lblTotalTimeBlue.setText("Total time: " + ((double) game.getPlayer2().getTotalTime() / 1000) + "s");
        updateCurrentPlayerLabel();
        currentTile = null;
        draggedTile = null;
    }

    @FXML
    public void onBtnUndoClick() {
        UndoSnapshot beforeState = undoRedoManager.popUndo();
        if (beforeState != null) {
            UndoSnapshot afterState = captureSnapshot();
            undoRedoManager.pushRedo(afterState);
            restoreViewFromSnapshot(beforeState);
            timeline.stop();
            runTimer();
        }
    }

    @FXML
    public void onBtnRedoClick() {
        UndoSnapshot afterState = undoRedoManager.popRedo();
        if (afterState != null) {
            UndoSnapshot beforeState = captureSnapshot();
            undoRedoManager.pushUndo(beforeState);
            restoreViewFromSnapshot(afterState);
            timeline.stop();
            runTimer();
        }
    }
}
