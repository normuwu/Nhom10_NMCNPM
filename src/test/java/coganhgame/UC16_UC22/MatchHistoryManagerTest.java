package coganhgame.UC16_UC22;

import coganhgame.Model.Game.MatchRecord;
import coganhgame.Service.MatchHistoryManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * DEVELOPMENT TESTING — UC-22
 * Test class: MatchHistoryManager (loadAll)
 *

 * Mapping với đặc tả UC-22:
 *  - Main Flow 22.1.6 + 22.1.7 -> testLoadAllWhenFileNotExists
 *  - Main Flow 22.1.6 + 22.1.7 -> testLoadAllWhenFileIsEmpty
 *
 * Ràng buộc kỹ thuật:
 *  - @TempDir tạo thư mục tạm -> không ảnh hưởng file gốc
 *  - Reflection trỏ MatchHistoryManager.FILE_NAME vào thư mục tạm
 */
@DisplayName("UC-22 - MatchHistoryManager.loadAll() Tests")
class MatchHistoryManagerTest {

    // =========================================================
    // TC-22-U01: File không tồn tại
    // UC-22 Main Flow 22.1.6 (file không tồn tại -> trả về rỗng)
    // =========================================================
    @Test
    @DisplayName("TC-22-U01: loadAll() khi file không tồn tại -> danh sách rỗng (size=0)")
    void testLoadAllWhenFileNotExists(@TempDir Path tempDir) throws Exception {
        // Given
        // Tạo đường dẫn file trong tempDir (chưa tạo file -> không tồn tại)
        File nonExistentFile = tempDir.resolve("match_history.txt").toFile();
        assertFalse(nonExistentFile.exists(), "File chưa được tạo -> không tồn tại");

        // Trỏ FILE_NAME sang đường dẫn tạm bằng reflection
        Field field = MatchHistoryManager.class.getDeclaredField("FILE_NAME");
        field.setAccessible(true);
        String originalFileName = (String) field.get(null);
        field.set(null, nonExistentFile.getAbsolutePath());

        try {
            // When
            List<MatchRecord> records = MatchHistoryManager.loadAll();

            // Then
            // loadAll() kiểm tra !file.exists() -> trả về danh sách rỗng ngay
            assertNotNull(records, "loadAll() không được trả về null");
            assertEquals(0, records.size(), "loadAll() phải trả về danh sách rỗng khi file không tồn tại");
        } finally {
            // Khôi phục FILE_NAME gốc để không ảnh hưởng các test khác
            field.set(null, originalFileName);
        }
    }

    // =========================================================
    // TC-22-U02: File tồn tại nhưng trống (0 bytes)
    // UC-22 Main Flow 22.1.6 (file rỗng -> đọc 0 dòng -> trả về rỗng)
    // =========================================================
    @Test
    @DisplayName("TC-22-U02: loadAll() khi file rỗng (0 bytes) -> danh sách rỗng (size=0)")
    void testLoadAllWhenFileIsEmpty(@TempDir Path tempDir) throws Exception {
        // Given
        // Tạo file rỗng (0 bytes) trong thư mục tạm
        File emptyFile = tempDir.resolve("match_history.txt").toFile();
        assertTrue(emptyFile.createNewFile(), "Phải tạo được file rỗng");
        assertEquals(0, emptyFile.length(), "File phải có dung lượng 0 bytes");

        // Trỏ FILE_NAME sang đường dẫn tạm bằng reflection
        Field field = MatchHistoryManager.class.getDeclaredField("FILE_NAME");
        field.setAccessible(true);
        String originalFileName = (String) field.get(null);
        field.set(null, emptyFile.getAbsolutePath());

        try {
            // When
            List<MatchRecord> records = MatchHistoryManager.loadAll();

            // Then
            // loadAll() đọc file, BufferedReader.readLine() trả về null ngay -> records rỗng
            assertNotNull(records, "loadAll() không được trả về null");
            assertEquals(0, records.size(), "loadAll() phải trả về danh sách rỗng khi file 0 bytes");
        } finally {
            // Khôi phục FILE_NAME gốc để không ảnh hưởng các test khác
            field.set(null, originalFileName);
        }
    }
}
