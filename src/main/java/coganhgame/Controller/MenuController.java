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
        Game game;
        try {
            game = Game.loadGame();
        } catch (GameNotFoundException e) {
            ViewUtilities.showAlert("Error", "No saved game found!");
            return;
        }

        Node source = (Node) actionEvent.getSource();
        Stage currentStage = (Stage) source.getScene().getWindow();
        GameController controller = new GameController(game);
        showGameView(currentStage, controller);
    }
    private void showGameView(Stage currentStage, GameController controller) {

    }

    @FXML
    @FXML
    public void onHowClick(ActionEvent actionEvent) {
        try {
            Node source = (Node) actionEvent.getSource();
            Stage currentStage = (Stage) source.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(GameApplication.class.getResource("View/presentation-view.fxml"));
            Stage presentationStage = new Stage();
            presentationStage.setTitle("How to Play");
            Scene scene = new Scene(fxmlLoader.load());
            presentationStage.setScene(scene);
            presentationStage.setOnShown(event -> currentStage.hide());
            presentationStage.setOnHidden(event -> currentStage.show());

            presentationStage.show();
        } catch (IOException e) {
            ViewUtilities.showAlert("Error", "Error loading presentation view", e.getMessage());
        }
    }
    
    @FXML
    public void onExitClick() {
    }
}