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

        // UC-17: Chơi với Bot
        // Hiển thị hộp thoại để người chơi chọn chế độ chơi
        String gameMode = ViewUtilities.showGameOptions("Choose Game Mode", "Choose Game Mode", "2 Players", "Play With Bot");

        // UC-17 Alternative Flow 17.2.2a
        // Người chơi đóng hộp thoại, quay về màn hình menu chính
        if (gameMode == null || gameMode.isEmpty()) {
            return;
        }

        final GameController[] controller = {null};

        if (gameMode.equals("2 Players")) {
            GameSettings gameSettings = ViewUtilities.get2PlayersSettings();
            if (gameSettings == null) {
                return;
            }
            controller[0] = new GameController(gameSettings.getPlayer1Name(), gameSettings.getPlayer2Name(), gameSettings.getGameTime());

        } else if (gameMode.equals("Play With Bot")) {

            // UC-17 Main Flow 17.1.4
            // Hiển thị hộp thoại nhập tên, thời gian giới hạn và chọn độ khó Bot
            GameSettings gameSettings = ViewUtilities.getPlayWithBotSettings();

            // UC-17 Alternative Flow 17.2.4a
            // Người chơi hủy hộp thoại, quay về màn hình menu chính
            if (gameSettings == null) {
                return;
            }

            // UC-17 Main Flow 17.1.7
            // Khởi tạo GameController với tên người chơi, thời gian và độ khó Bot
            controller[0] = new GameController(gameSettings.getPlayer1Name(), gameSettings.getGameTime(), gameSettings.getBotLevel());
        }

        // UC-17 Main Flow 17.1.8
        // Tải và hiển thị cửa sổ game, ẩn cửa sổ menu
        showGameView(currentStage, controller[0]);
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