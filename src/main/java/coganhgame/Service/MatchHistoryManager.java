package coganhgame.Service;

import coganhgame.Model.Game.MatchRecord;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/** Reads / writes match history to match_history.txt (pipe-delimited format). */
public class MatchHistoryManager {
    private static final String FILE_NAME = "match_history.txt";

    // UC-23: Save Match Record
    // Ghi một match record vào file lịch sử (chế độ append)
    public static void saveRecord(MatchRecord record) {
        // UC-23 Main Flow 23.1.6
        // Serialize MatchRecord thành chuỗi pipe-delimited (p1|p2|winner|mode|botLevel|duration|playedAt)
        // UC-23 Main Flow 23.1.7
        // Mở file với chế độ append (true), ghi dòng CSV mới vào cuối file
        // UC-23 Main Flow 23.1.8
        // File được đóng tự động qua try-with-resources
        try (FileWriter fw = new FileWriter(FILE_NAME, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            out.println(record.toCsvLine());
        } catch (IOException e) {
            // UC-23 Exception: Ghi file thất bại → in lỗi, không làm gián đoạn luồng End Game
            System.err.println("Failed to save match record: " + e.getMessage());
        }
    }

    // UC-24 Main Flow 24.1.8 (& UC-22: View Leaderboard)
    // Đọc tất cả match records từ file, trả về danh sách mới nhất trước
    public static List<MatchRecord> loadAll() {
        List<MatchRecord> records = new ArrayList<>();
        File file = new File(FILE_NAME);

        // Nếu file chưa tồn tại, trả về danh sách rỗng ngay
        if (!file.exists()) return records;

        // Đọc từng dòng CSV và deserialize thành MatchRecord
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    records.add(new MatchRecord(line));
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to load match records: " + e.getMessage());
        }

        // UC-24 Main Flow 24.1.9
        // Đảo ngược danh sách để bản ghi mới nhất hiển thị đầu tiên trên Leaderboard
        List<MatchRecord> reversed = new ArrayList<>();
        for (int i = records.size() - 1; i >= 0; i--) {
            reversed.add(records.get(i));
        }
        return reversed;
    }

    // UC-24: Clear Match History
    // Xóa toàn bộ lịch sử đấu bằng cách ghi đè file với nội dung rỗng
    public static void clearAll() {
        // UC-24 Main Flow 24.1.5
        // Mở file với PrintWriter (chế độ ghi đè, không append)
        // UC-24 Main Flow 24.1.6
        // Ghi chuỗi rỗng → toàn bộ nội dung file bị xóa
        // File được đóng tự động qua try-with-resources
        try (PrintWriter out = new PrintWriter(FILE_NAME)) {
            out.print("");
        } catch (IOException e) {
            // UC-24 Exception: Ghi đè thất bại → in lỗi, refreshData() vẫn được gọi để đồng bộ UI
            System.err.println("Failed to clear match records: " + e.getMessage());
        }
    }
}
         