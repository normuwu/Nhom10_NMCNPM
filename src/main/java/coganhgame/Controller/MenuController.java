package coganhgame.Controller;

import coganhgame.Exception.GameNotFoundException;
import coganhgame.GameApplication;
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
        Node source = (Node) actionEvent.getSource();
        Stage currentStage = (Stage) source.getScene().getWindow();


        GameSettings gameSettings = ViewUtilities.get2PlayersSettings();

        if (gameSettings == null) {

            return;
        }


        GameController controller = new GameController(
                gameSettings.getPlayer1Name(),
                gameSettings.getPlayer2Name(),
                gameSettings.getGameTime()
        );

        showGameView(currentStage, controller);
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
        FXMLLoader fxmlLoader = new FXMLLoader(GameApplication.class.getResource("View/game-view.fxml"));

        Stage newStage = null;
        try {
            fxmlLoader.setControllerFactory(c -> controller);
            newStage = new Stage();
            newStage.setTitle("Co Ganh Game");
            newStage.setScene(new Scene(fxmlLoader.load()));
            newStage.setResizable(false);
        } catch (IOException e) {
            ViewUtilities.showAlert("Error", "Error loading game view", e.getMessage());
        }

        newStage.setOnShown(event -> currentStage.hide());
        newStage.setOnHidden(event -> currentStage.show());

        newStage.show();
    }

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
        if (ViewUtilities.showConfirm("Exit", "Are you sure you want to exit?")) {
            System.exit(0);
        }
    }
}