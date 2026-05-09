package coganhgame.Utilities;

import coganhgame.Model.Settings.GameSettings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.Optional;

public final class ViewUtilities {
    public static final int BOARD_STROKE_WIDTH = 3;
    public static final Color BOARD_STROKE_COLOR = Color.valueOf("#222831");
    public static final Color RED_PIECE_COLOR = Color.valueOf("#E21818");
    public static final Color BUE_PIECE_COLOR = Color.valueOf("#2666CF");
    public static final Font COOR_FONT = new Font("Arial", 20);

    public static void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static boolean showConfirm(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        return alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }

    public static void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Đã viết lại hàm này để an toàn hơn, chống lỗi tên có dấu cách
    public static GameSettings get2PlayersSettings() {
        Dialog<GameSettings> dialog = new Dialog<>();
        dialog.setTitle("Game's settings");
        dialog.setHeaderText(null);

        // Set the button types
        ButtonType saveButtonType = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Create the grid pane
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField player1NameField = new TextField();
        TextField player2NameField = new TextField();
        TextField gameTimeField = new TextField();

        grid.add(new Label("Enter Player 1 Name:"), 0, 0);
        grid.add(player1NameField, 1, 0);
        grid.add(new Label("Enter Player 2 Name:"), 0, 1);
        grid.add(player2NameField, 1, 1);
        grid.add(new Label("Enter Time Limit (in seconds):"), 0, 2);
        grid.add(gameTimeField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        // Enable/Disable save button depending on whether a name was entered
        Button saveButton = (Button) dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.setDisable(true);

        // Validation (disable the save button if any field is empty)
        player1NameField.textProperty().addListener((observable, oldValue, newValue) -> {
            saveButton.setDisable(newValue.trim().isEmpty() || player2NameField.getText().trim().isEmpty() || gameTimeField.getText().trim().isEmpty() || !gameTimeField.getText().matches("\\d+"));
        });

        player2NameField.textProperty().addListener((observable, oldValue, newValue) -> {
            saveButton.setDisable(newValue.trim().isEmpty() || player1NameField.getText().trim().isEmpty() || gameTimeField.getText().trim().isEmpty() || !gameTimeField.getText().matches("\\d+"));
        });

        gameTimeField.textProperty().addListener((observable, oldValue, newValue) -> {
            saveButton.setDisable(newValue.trim().isEmpty() || player1NameField.getText().trim().isEmpty() || player2NameField.getText().trim().isEmpty() || !gameTimeField.getText().matches("\\d+"));
        });

        // Set the result converter
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                GameSettings gameSettings = new GameSettings();
                gameSettings.setPlayer1Name(player1NameField.getText().trim());
                gameSettings.setPlayer2Name(player2NameField.getText().trim());
                gameSettings.setGameTime(Integer.parseInt(gameTimeField.getText().trim()));
                return gameSettings;
            }
            return null;
        });

        // Show the dialog and wait for user input
        Optional<GameSettings> result = dialog.showAndWait();

        return result.orElse(null);
    }
}