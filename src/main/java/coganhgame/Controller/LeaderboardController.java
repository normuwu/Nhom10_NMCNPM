package coganhgame.Controller;

import coganhgame.Model.Game.MatchRecord;
import coganhgame.Service.MatchHistoryManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LeaderboardController {
    @FXML private TableView<MatchRecord> historyTable;
    @FXML private TableColumn<MatchRecord, String> colDate, colPlayer1, colPlayer2, colWinner, colMode, colDuration;
    @FXML private Label lblStats;

    private final ObservableList<MatchRecord> records = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colDate.setCellValueFactory(new PropertyValueFactory<>("formattedDate"));
        colPlayer1.setCellValueFactory(new PropertyValueFactory<>("player1Name"));
        colPlayer2.setCellValueFactory(new PropertyValueFactory<>("player2Name"));
        colWinner.setCellValueFactory(new PropertyValueFactory<>("winnerName"));
        colMode.setCellValueFactory(new PropertyValueFactory<>("gameMode"));
        colDuration.setCellValueFactory(new PropertyValueFactory<>("formattedDuration"));
        refreshData();
    }

    private void refreshData() {
        List<MatchRecord> all = MatchHistoryManager.loadAll();
        records.setAll(all);
        historyTable.setItems(records);
        if (all.isEmpty()) {
            lblStats.setText("No matches played yet.");
            return;
        }
        Map<String, Long> winCounts = all.stream()
                .filter(r -> !"Play With Bot".equals(r.getGameMode()))
                .collect(Collectors.groupingBy(MatchRecord::getWinnerName, Collectors.counting()));
        String topWinner = winCounts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(e -> e.getKey() + " (" + e.getValue() + " wins)")
                .orElse("N/A");
        String topMode = all.stream()
                .collect(Collectors.groupingBy(MatchRecord::getGameMode, Collectors.counting()))
                .entrySet().stream().max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey).orElse("N/A");
        lblStats.setText(String.format("Total: %d matches  |  Top: %s  |  Mode: %s", all.size(), topWinner, topMode));
    }

    @FXML public void onCloseClick(ActionEvent e) { ((Stage)((Node)e.getSource()).getScene().getWindow()).close(); }

    @FXML
    public void onClearClick() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Clear History");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to clear all match history?");
        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            MatchHistoryManager.clearAll();
            refreshData();
        }
    }
}