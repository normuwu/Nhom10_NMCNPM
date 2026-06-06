package coganhgame.Model.Game;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// UC-23: Save Match Record
// Lớp đại diện cho một bản ghi kết quả trận đấu, hỗ trợ serialize/deserialize CSV
public class MatchRecord {

    // Thông tin trận đấu được lưu trữ bất biến (final)
    private final String player1Name;
    private final String player2Name;
    private final String winnerName;
    private final String gameMode;       // "2 Players" hoặc "Play With Bot"
    private final int botLevel;          // 0 nếu Human vs Human (UC-23 Alternative Flow 23.2.1)
    private final long durationSeconds;
    private final String playedAt;       // ISO date-time string

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    // UC-23 Main Flow 23.1.3
    // Constructor tạo MatchRecord mới từ dữ liệu kết thúc game
    public MatchRecord(String player1Name, String player2Name, String winnerName,
                       String gameMode, int botLevel, long durationSeconds) {
        this.player1Name = player1Name;
        this.player2Name = player2Name;
        this.winnerName = winnerName;
        this.gameMode = gameMode;
        this.botLevel = botLevel;
        this.durationSeconds = durationSeconds;

        // UC-23 Main Flow 23.1.4
        // Tự động ghi nhận thời điểm chơi theo định dạng ISO
        this.playedAt = LocalDateTime.now().format(FORMATTER);
    }

    // UC-23 Main Flow 23.1.6 (phục vụ loadAll() của UC-24 / UC-22)
    // Deserialize từ một dòng CSV pipe-delimited đọc từ file lịch sử
    public MatchRecord(String csvLine) {
        String[] parts = csvLine.split("\\|");
        this.player1Name    = parts[0];
        this.player2Name    = parts[1];
        this.winnerName     = parts[2];
        this.gameMode       = parts[3];
        this.botLevel       = Integer.parseInt(parts[4]);
        this.durationSeconds = Long.parseLong(parts[5]);
        this.playedAt       = parts[6];
    }

    // UC-23 Main Flow 23.1.6
    // Serialize MatchRecord thành chuỗi pipe-delimited để ghi vào file CSV
    // Định dạng: p1|p2|winner|mode|botLevel|duration|playedAt
    public String toCsvLine() {
        return String.join("|",
                player1Name, player2Name, winnerName,
                gameMode, String.valueOf(botLevel),
                String.valueOf(durationSeconds), playedAt);
    }

    // Getters — truy xuất thông tin bản ghi (dùng cho UC-22 View Leaderboard)
    public String getPlayer1Name()   { return player1Name; }
    public String getPlayer2Name()   { return player2Name; }
    public String getWinnerName()    { return winnerName; }
    public String getGameMode()      { return gameMode; }
    public int    getBotLevel()      { return botLevel; }
    public long   getDurationSeconds(){ return durationSeconds; }
    public String getPlayedAt()      { return playedAt; }

    // Định dạng thời lượng trận đấu sang dạng mm:ss để hiển thị trên Leaderboard
    public String getFormattedDuration() {
        long mins = durationSeconds / 60;
        long secs = durationSeconds % 60;
        return String.format("%d:%02d", mins, secs);
    }

    // Định dạng thời điểm chơi sang dạng yyyy-MM-dd để hiển thị trên Leaderboard
    public String getFormattedDate() {
        try {
            return LocalDateTime.parse(playedAt, FORMATTER).toLocalDate().toString();
        } catch (Exception e) {
            return playedAt;
        }
    }
}