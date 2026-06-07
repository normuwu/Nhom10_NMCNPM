package coganhgame.Model.Game;

import coganhgame.Exception.GameNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * DEVELOPMENT TESTING — UC-16
 * Test class: Game (loadGame)
 *
 * Mục đích: Kiểm tra Game.loadGame() khi file game_state.txt
 * không tồn tại hoặc rỗng (0 bytes) — không phụ thuộc JavaFX runtime.
 *
 * Mapping với đặc tả UC-16:
 *  - Main Flow 16.1.6 → testLoadGameWhenFileNotExists
 *  - Alt  Flow 16.2.1 → testLoadGameWhenFileEmpty
 *
 * Ràng buộc kỹ thuật:
 *  - @TempDir tạo thư mục tạm → không ảnh hưởng file gốc
 *  - Reflection trỏ Game.FILE_NAME vào thư mục tạm
 */
@DisplayName("UC-16 - Game.loadGame() Tests")
class GameTest {

    @TempDir
    Path tempDir;

    private String originalFileName;
    private String testFilePath;

    @BeforeEach
    void setUp() throws Exception {
        // Lưu FILE_NAME gốc, tạo đường dẫn file tạm
        Field field = Game.class.getDeclaredField("FILE_NAME");
        field.setAccessible(true);
        originalFileName = (String) field.get(null);
        testFilePath = tempDir.resolve("game_state.txt").toString();
    }

    @AfterEach
    void tearDown() throws Exception {
        // Khôi phục FILE_NAME gốc
        Field field = Game.class.getDeclaredField("FILE_NAME");
        field.setAccessible(true);
        field.set(null, originalFileName);
    }

    // =========================================================
    // TC-16-U01: File không tồn tại
    // UC-16 Main Flow 16.1.6
    // =========================================================
    @Test
    @DisplayName("TC-16-U01: loadGame() khi file không tồn tại → GameNotFoundException")
    void testLoadGameWhenFileNotExists() throws Exception {
        // Given: trỏ FILE_NAME vào tempDir (chưa tạo file)
        Field field = Game.class.getDeclaredField("FILE_NAME");
        field.setAccessible(true);
        field.set(null, testFilePath);

        // When & Then: loadGame() ném GameNotFoundException
        GameNotFoundException ex = assertThrows(
                GameNotFoundException.class,
                Game::loadGame
        );

        // Then: message khớp
        assertEquals("No saved game found!", ex.getMessage());
    }

    // =========================================================
    // TC-16-U02: File tồn tại nhưng rỗng (0 bytes)
    // UC-16 Alternative Flow 16.2.1
    // =========================================================
    @Test
    @DisplayName("TC-16-U02: loadGame() khi file rỗng (0 bytes) → GameNotFoundException")
    void testLoadGameWhenFileEmpty() throws Exception {
        // Given: tạo file rỗng trong tempDir
        Field field = Game.class.getDeclaredField("FILE_NAME");
        field.setAccessible(true);
        field.set(null, testFilePath);

        File emptyFile = new File(testFilePath);
        assertTrue(emptyFile.createNewFile(), "Phải tạo được file rỗng");
        assertEquals(0, emptyFile.length(), "File phải có dung lượng 0 bytes");

        // When & Then: loadGame() ném GameNotFoundException
        GameNotFoundException ex = assertThrows(
                GameNotFoundException.class,
                Game::loadGame
        );

        // Then: message khớp
        assertEquals("No saved game found!", ex.getMessage());
    }
}
