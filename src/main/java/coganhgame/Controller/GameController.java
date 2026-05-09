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
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
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

    /*    private final ChangeListener<Number> timeLeftListener = (observable, oldValue, newValue) -> {
            if (newValue.intValue() <= 0) {
                clearOpenHighlight();
                switchPlayer();
            }
        };*/
    private final Timeline timeline = new Timeline();

    public GameController(String player1Name, String player2Name, int timeLimit) {
        this.game = new Game(player1Name, player2Name, timeLimit);
    }

    public GameController(Game game) {
        this.game = game;
    }

    @FXML
    public void initialize() {

    }

    private void initViewBoard() {

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
                    switchPlayer();
                }
            } else {
                pieceComp.abortMove();
            }
        });
        return pieceComp;
    }


    private int toBoardPos(double pixel) {

        return 0;
    }


    private void switchPlayer() {

    }

    private void updateCurrentPlayerLabel() {

    }

    private void runTimer() {

    }

    private void endGame() {

    }

    private void clearOpenHighlight() {

    }

    @FXML
    public void onBtnResetClick() {
    }

    @FXML
    public void onBtnOpenClick() {

    }

    @FXML
    public void onBtnPassClick() {
    }

    @FXML
    public void onBtnExitClick(ActionEvent actionEvent) {

    }
}