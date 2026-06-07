package coganhgame.Model.Game;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MatchRecordTest {

    // ── Helpers ───────────────────────────────────────────────────────────
    private MatchRecord createRecord(String p1, String p2, String winner,
                                     String mode, int botLevel, long duration) {
        return new MatchRecord(p1, p2, winner, mode, botLevel, duration);
    }

    private MatchRecord createDefault() {
        return createRecord("Alice", "Bob", "Alice", "2 Players", 0, 125);
    }

    // ── UC-23 Main Flow 23.1.6 — toCsvLine() ─────────────────────────────
    @Test
    void toCsvLineHasExactlySixPipeDelimiters() {
        MatchRecord record = createDefault();
        String csv = record.toCsvLine();
        long pipeCount = csv.chars().filter(c -> c == '|').count();
        assertEquals(6, pipeCount);
    }

    @Test
    void toCsvLinePreservesAllFields() {
        MatchRecord record = createRecord("Alice", "Bob", "Alice",
                "2 Players", 0, 125);
        String csv = record.toCsvLine();
        String[] parts = csv.split("\\|");
        assertEquals("Alice",     parts[0]);
        assertEquals("Bob",       parts[1]);
        assertEquals("Alice",     parts[2]);
        assertEquals("2 Players", parts[3]);
        assertEquals("0",         parts[4]);
        assertEquals("125",       parts[5]);
    }

    // ── UC-23 Main Flow 23.1.6 — CSV constructor (deserialize) ───────────
    @Test
    void csvConstructorRestoresAllFields() {
        MatchRecord original = createRecord("Alice", "Bob", "Alice",
                "2 Players", 0, 125);
        MatchRecord restored = new MatchRecord(original.toCsvLine());
        assertEquals(original.getPlayer1Name(),   restored.getPlayer1Name());
        assertEquals(original.getPlayer2Name(),   restored.getPlayer2Name());
        assertEquals(original.getWinnerName(),    restored.getWinnerName());
        assertEquals(original.getGameMode(),      restored.getGameMode());
        assertEquals(original.getBotLevel(),      restored.getBotLevel());
        assertEquals(original.getDurationSeconds(),restored.getDurationSeconds());
        assertEquals(original.getPlayedAt(),      restored.getPlayedAt());
    }

    // ── UC-23 Alternative Flow 23.2.1 — Human vs Human ───────────────────
    @Test
    void humanVsHumanHasBotLevelZeroAndCorrectGameMode() {
        MatchRecord record = createRecord("Alice", "Bob", "Alice",
                "2 Players", 0, 90);
        assertEquals(0,          record.getBotLevel());
        assertEquals("2 Players", record.getGameMode());
    }

    // ── UC-23 Alternative Flow 23.2.2 — Play With Bot ────────────────────
    @Test
    void playWithBotStoresBotLevelAndGameMode() {
        MatchRecord easy   = createRecord("Alice", "Bot", "Bot",
                "Play With Bot", 3, 60);
        MatchRecord medium = createRecord("Alice", "Bot", "Alice",
                "Play With Bot", 5, 75);
        MatchRecord hard   = createRecord("Alice", "Bot", "Bot",
                "Play With Bot", 7, 45);
        assertEquals(3,              easy.getBotLevel());
        assertEquals(5,              medium.getBotLevel());
        assertEquals(7,              hard.getBotLevel());
        assertEquals("Play With Bot", hard.getGameMode());
    }

    // ── getFormattedDuration() ────────────────────────────────────────────
    @Test
    void formattedDurationIsCorrectMmSsFormat() {
        MatchRecord record = createRecord("A", "B", "A", "2 Players", 0, 125);
        assertEquals("2:05", record.getFormattedDuration());
    }

    @Test
    void formattedDurationUnderOneMinute() {
        MatchRecord record = createRecord("A", "B", "A", "2 Players", 0, 45);
        assertEquals("0:45", record.getFormattedDuration());
    }

    // ── getFormattedDate() ────────────────────────────────────────────────
    @Test
    void formattedDateIsValidDateString() {
        MatchRecord record = createDefault();
        String date = record.getFormattedDate();
        // yyyy-MM-dd format: độ dài 10, có 2 dấu gạch
        assertEquals(10, date.length());
        assertEquals('-', date.charAt(4));
        assertEquals('-', date.charAt(7));
    }

    // ── UC-23 Main Flow 23.1.4 — playedAt tự động ghi nhận ───────────────
    @Test
    void playedAtIsNotNullOrEmpty() {
        MatchRecord record = createDefault();
        assertNotNull(record.getPlayedAt());
        assertFalse(record.getPlayedAt().isEmpty());
    }
}