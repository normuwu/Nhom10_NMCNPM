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

        return null;
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