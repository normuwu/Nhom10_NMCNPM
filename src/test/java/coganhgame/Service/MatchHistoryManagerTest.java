package coganhgame.Service;

import coganhgame.Model.Game.MatchRecord;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MatchHistoryManagerTest {

    private static final String TEST_FILE = "test_match_history.txt";

    // ── Setup / Teardown ──────────────────────────────────────────────────
    @BeforeEach
    void setUp() throws Exception {
        // Trỏ FILE_NAME sang file test tránh ảnh hưởng dữ liệu thật
        Field field = MatchHistoryManager.class.getDeclaredField("FILE_NAME");
        field.setAccessible(true);
        field.set(null, TEST_FILE);
        // Đảm bảo file sạch trước mỗi test
        new File(TEST_FILE).delete();
    }

    @AfterEach
    void tearDown() {
        new File(TEST_FILE).delete();
    }

    // ── Helpers ───────────────────────────────────────────────────────────
    private MatchRecord createRecord(String winner) {
        return new MatchRecord("Alice", "Bob", winner, "2 Players", 0, 90);
    }

    // ── UC-23 Main Flow 23.1.5→23.1.8 — saveRecord() ─────────────────────
    @Test
    void saveRecordCreatesFileIfNotExists() {
        MatchHistoryManager.saveRecord(createRecord("Alice"));
        assertTrue(new File(TEST_FILE).exists());
    }

    @Test
    void saveRecordAppendsEachRecordAsNewLine() {
        MatchHistoryManager.saveRecord(createRecord("Alice"));
        MatchHistoryManager.saveRecord(createRecord("Bob"));
        List<MatchRecord> records = MatchHistoryManager.loadAll();
        assertEquals(2, records.size());
    }

    @Test
    void saveRecordPreservesWinnerName() {
        MatchHistoryManager.saveRecord(createRecord("Alice"));
        List<MatchRecord> records = MatchHistoryManager.loadAll();
        assertEquals("Alice", records.get(0).getWinnerName());
    }

    // ── UC-24 Main Flow 24.1.4→24.1.6 — clearAll() ───────────────────────
    @Test
    void clearAllEmptiesFileContent() {
        MatchHistoryManager.saveRecord(createRecord("Alice"));
        MatchHistoryManager.saveRecord(createRecord("Bob"));
        MatchHistoryManager.clearAll();
        List<MatchRecord> records = MatchHistoryManager.loadAll();
        assertEquals(0, records.size());
    }

    @Test
    void clearAllOnEmptyFileDoesNotThrow() {
        assertDoesNotThrow(() -> MatchHistoryManager.clearAll());
    }

    @Test
    void clearAllThenSaveRecordWorksNormally() {
        MatchHistoryManager.saveRecord(createRecord("Alice"));
        MatchHistoryManager.clearAll();
        MatchHistoryManager.saveRecord(createRecord("Bob"));
        List<MatchRecord> records = MatchHistoryManager.loadAll();
        assertEquals(1, records.size());
        assertEquals("Bob", records.get(0).getWinnerName());
    }

    // ── UC-24 Main Flow 24.1.7→24.1.9 — loadAll() ────────────────────────
    @Test
    void loadAllReturnsEmptyListWhenFileNotExists() {
        List<MatchRecord> records = MatchHistoryManager.loadAll();
        assertNotNull(records);
        assertEquals(0, records.size());
    }

    @Test
    void loadAllReturnsNewestRecordFirst() {
        MatchRecord first  = createRecord("Alice");
        MatchRecord second = createRecord("Bob");
        MatchHistoryManager.saveRecord(first);
        MatchHistoryManager.saveRecord(second);
        List<MatchRecord> records = MatchHistoryManager.loadAll();
        // loadAll() đảo ngược → "Bob" (ghi sau) phải ở index 0
        assertEquals("Bob",   records.get(0).getWinnerName());
        assertEquals("Alice", records.get(1).getWinnerName());
    }

    @Test
    void loadAllCountMatchesSaveCount() {
        for (int i = 0; i < 5; i++) {
            MatchHistoryManager.saveRecord(createRecord("Player" + i));
        }
        assertEquals(5, MatchHistoryManager.loadAll().size());
    }

    @Test
    void loadAllDeserializesAllFieldsCorrectly() {
        MatchRecord original = new MatchRecord(
                "Alice", "Bob", "Alice", "Play With Bot", 5, 120
        );
        MatchHistoryManager.saveRecord(original);
        MatchRecord loaded = MatchHistoryManager.loadAll().get(0);
        assertEquals(original.getPlayer1Name(),    loaded.getPlayer1Name());
        assertEquals(original.getPlayer2Name(),    loaded.getPlayer2Name());
        assertEquals(original.getWinnerName(),     loaded.getWinnerName());
        assertEquals(original.getGameMode(),       loaded.getGameMode());
        assertEquals(original.getBotLevel(),       loaded.getBotLevel());
        assertEquals(original.getDurationSeconds(), loaded.getDurationSeconds());
        assertEquals(original.getPlayedAt(),       loaded.getPlayedAt());
    }

    // ── UC-24 Alternative Flow 24.2.1 — Cancel (dữ liệu giữ nguyên) ──────
    @Test
    void withoutCallingClearAllDataRemainsIntact() {
        MatchHistoryManager.saveRecord(createRecord("Alice"));
        // Giả lập người dùng nhấn Cancel → không gọi clearAll()
        List<MatchRecord> records = MatchHistoryManager.loadAll();
        assertEquals(1, records.size());
        assertEquals("Alice", records.get(0).getWinnerName());
    }
}