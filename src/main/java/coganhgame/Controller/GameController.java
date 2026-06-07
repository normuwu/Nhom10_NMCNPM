package coganhgame;

import coganhgame.Exception.GameNotFoundException;
import coganhgame.Model.Game.Game;
import org.junit.jupiter.api.DisplayName;
import org.testng.annotations.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for UC-16: Không tìm thấy game đã lưu.
 * <p>
 * Kiểm thử chức năng đọc file game_state.txt thông qua {@link Game#loadGame(String)}.
 * Sử dụng {@link TempDir @TempDir} để giả lập file trên RAM,
 * không làm ảnh hưởng đến file game_state.txt thật của project.
 */
class GameTest {

    @TempDir
    Path tempDir;   // JUnit 5 sẽ tạo thư mục tạm trên RAM trước mỗi test

    // ---------------------------------------------------------------
    // Test case 1.1: File không tồn tại
    // ---------------------------------------------------------------
    @Test
    @DisplayName("UC-16-TC1.1: loadGame() khi file không tồn tại -> ném GameNotFoundException")
    void loadGame_WhenFileNotExists_ShouldThrowGameNotFoundException() {
        // Given
        Path nonExistentFile = tempDir.resolve("non_existent_game.txt");

        // When & Then
        assertThrows(GameNotFoundException.class,
                () -> Game.loadGame(nonExistentFile.toString()),
                "Expected GameNotFoundException when file does not exist"
        );
    }

    // ---------------------------------------------------------------
    // Test case 1.2: File tồn tại nhưng rỗng (0 bytes)
    // ---------------------------------------------------------------
    @Test
    @DisplayName("UC-16-TC1.2: loadGame() khi file rỗng (0 bytes) -> ném GameNotFoundException")
    void loadGame_WhenFileIsEmpty_ShouldThrowGameNotFoundException() throws IOException {
        // Given
        Path emptyFile = tempDir.resolve("empty_game.txt");
        Files.createFile(emptyFile);   // tạo file 0 bytes
        assertTrue(Files.exists(emptyFile), "File should exist before test");
        assertEquals(0, Files.size(emptyFile), "File should be empty (0 bytes)");

        // When & Then
        assertThrows(GameNotFoundException.class,
                () -> Game.loadGame(emptyFile.toString()),
                "Expected GameNotFoundException when file is empty (0 bytes)"
        );
    }
}
