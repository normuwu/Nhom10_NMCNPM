package coganhgame.Model.Game;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** Stores the result of a completed match for history/leaderboard purposes. */
public class MatchRecord {
    private final String player1Name, player2Name, winnerName, gameMode;
    private final int botLevel;
    private final long durationSeconds;
    private final String playedAt;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public MatchRecord(String p1, String p2, String winner, String mode, int botLvl, long duration) {
        this.player1Name = p1; this.player2Name = p2; this.winnerName = winner;
        this.gameMode = mode; this.botLevel = botLvl; this.durationSeconds = duration;
        this.playedAt = LocalDateTime.now().format(FORMATTER);
    }

    /** Deserialize from pipe-delimited line */
    public MatchRecord(String csvLine) {
        String[] p = csvLine.split("\\|");
        this.player1Name = p[0]; this.player2Name = p[1]; this.winnerName = p[2];
        this.gameMode = p[3]; this.botLevel = Integer.parseInt(p[4]);
        this.durationSeconds = Long.parseLong(p[5]); this.playedAt = p[6];
    }

    public String toCsvLine() {
        return String.join("|", player1Name, player2Name, winnerName, gameMode,
                String.valueOf(botLevel), String.valueOf(durationSeconds), playedAt);
    }

    // Getters used by PropertyValueFactory
    public String getPlayer1Name() { return player1Name; }
    public String getPlayer2Name() { return player2Name; }
    public String getWinnerName() { return winnerName; }
    public String getGameMode() { return gameMode; }
    public int getBotLevel() { return botLevel; }
    public long getDurationSeconds() { return durationSeconds; }
    public String getPlayedAt() { return playedAt; }

    public String getFormattedDuration() {
        return String.format("%d:%02d", durationSeconds / 60, durationSeconds % 60);
    }

    public String getFormattedDate() {
        try { return LocalDateTime.parse(playedAt, FORMATTER).toLocalDate().toString(); }
        catch (Exception e) { return playedAt; }
    }
}
         