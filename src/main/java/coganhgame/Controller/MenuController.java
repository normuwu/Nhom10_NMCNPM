package coganhgame.Controller;

import coganhgame.Exception.GameNotFoundException;

import coganhgame.Model.Game.Game;
import coganhgame.Model.Settings.GameSettings;
import coganhgame.Utilities.ViewUtilities;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class MenuController {
    @FXML
    public Button btnNewGame;
    @FXML
    public Button btnContinue;
    @FXML
    public Button btnHow;
    @FXML
    public Button btnExit;

    @FXML
    protected void onNewGameClick(ActionEvent actionEvent) {

    }

    @FXML
    public void onContinueClick(ActionEvent actionEvent) {

    }

    private void showGameView(Stage currentStage, GameController controller) {

    }

    @FXML
    public void onHowClick(ActionEvent actionEvent) {
    }

    @FXML
    public void onExitClick() {
    }
}