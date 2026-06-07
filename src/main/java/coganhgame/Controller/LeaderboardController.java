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
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LeaderboardController {
    @FXML
    private TableView<MatchRecord> historyTable;
    @FXML
    private TableColumn<MatchRecord, String> colDate, colPlayer1, colPlayer2, colWinner, colMode, colDuration;
    @FXML
    private Label lblStats;

    @FXML private TableView<MatchRecord> historyTable;
    @FXML private TableColumn<MatchRecord, String> colDate;
    @FXML private TableColumn<MatchRecord, String> colPlayer1;
    @FXML private TableColumn<MatchRecord, String> colPlayer2;
    @FXML private TableColumn<MatchRecord, String> colWinner;
    @FXML private TableColumn<MatchRecord, String> colMode;
    @FXML private TableColumn<MatchRecord, String> colDuration;
    @FXML private Label lblStats;

    private final ObservableList<MatchRecord> records = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // UC-22 Main Flow 22.1.6
        // LeaderboardController.initialize() được gọi tự động ngay khi FXML được load

        // UC-22 Main Flow 22.1.7
        // Hệ thống thiết lập các cột của TableView với PropertyValueFactory
        colDate.setCellValueFactory(new PropertyValueFactory<>("formattedDate"));
        colPlayer1.setCellValueFactory(new PropertyValueFactory<>("player1Name"));
        colPlayer2.setCellValueFactory(new PropertyValueFactory<>("player2Name"));
        colWinner.setCellValueFactory(new PropertyValueFactory<>("winnerName"));
        colMode.setCellValueFactory(new PropertyValueFactory<>("gameMode"));
        colDuration.setCellValueFactory(new PropertyValueFactory<>("formattedDuration"));

        refreshData();
    }

    private void refreshData() {
        // UC-22 Main Flow 22.1.8
        // Hệ thống gọi loadAll() để đọc file match_history.txt và cập nhật lên bảng
        List<MatchRecord> all = MatchHistoryManager.loadAll();
        records.setAll(all);
        historyTable.setItems(records);

        // Chuyển dữ liệu qua hàm updateStats để tính toán thống kê
        updateStats(all);
    }

    private void updateStats(List<MatchRecord> all) {
        // UC-22 Alternative Flow 22.2.8a.1 & 22.2.8a.2
        // Xử lý trường hợp danh sách lịch sử trống (chưa có trận đấu nào)
        if (all.isEmpty()) {
            lblStats.setText("No matches played yet.");
            return;
        }

        // UC-22 Main Flow 22.1.9
        // Hệ thống tính toán thống kê tổng quan (Tổng số trận, Top Player, Most played mode)
        long total = all.size();

        // Lọc ra các trận 2 người chơi (không tính chơi với Bot) để đếm số trận thắng
        Map<String, Long> winCounts = all.stream()
                .filter(r -> !"Play With Bot".equals(r.getGameMode()))
                .collect(Collectors.groupingBy(MatchRecord::getWinnerName, Collectors.counting()));

        String topWinner = winCounts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(e -> e.getKey() + " (" + e.getValue() + " wins)")
                .orElse("N/A");

        // Tìm chế độ chơi được chơi nhiều nhất
        String topMode = all.stream()
                .collect(Collectors.groupingBy(MatchRecord::getGameMode, Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("N/A");

        // UC-22 Main Flow 22.1.10
        // Thông tin thống kê được hiển thị lên giao diện (lblStats)
        lblStats.setText(String.format("Total: %d matches  |  Top player: %s  |  Most played: %s",
                total, topWinner, topMode));
    }

    @FXML
    public void onCloseClick(ActionEvent event) {
        // UC-22 Main Flow 22.1.12
        // Người chơi nhấn nút "Close" để đóng cửa sổ Leaderboard và quay về Menu
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    @FXML
    // UC-24: Clear Match History
    // Người chơi xóa toàn bộ lịch sử trận đấu và làm mới bảng xếp hạng
    public void onClearClick() {
        // UC-24: Clear Match History
        // Hàm kích hoạt Use Case Xóa lịch sử khi người dùng nhấn nút "Clear History"
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Clear History");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to clear all match history?");

        // Nếu người chơi chọn OK (Đồng ý xóa)
        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            // UC-24 Main Flow 24.1.3: Người chơi chọn OK
            // UC-24 Main Flow 24.1.4 → 24.1.6
            // Gọi clearAll() để ghi đè file bằng nội dung rỗng
            MatchHistoryManager.clearAll();
            refreshData(); // Làm mới lại bảng và thống kê sau khi xóa
        }
        // UC-24 Alternative Flow 24.2.1
        // Người chơi chọn Cancel → đóng hộp thoại, dữ liệu giữ nguyên
    }
}