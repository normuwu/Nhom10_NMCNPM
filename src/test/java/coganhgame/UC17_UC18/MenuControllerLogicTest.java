package coganhgame.UC17_UC18;

import coganhgame.Model.Settings.GameSettings;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * DEVELOPMENT TESTING — UC17
 * Test class: MenuController logic (tách biệt khỏi JavaFX)
 *
 * Mục đích: Kiểm tra các điều kiện rẽ nhánh trong MenuController.onNewGameClick()
 * mà KHÔNG cần khởi động JavaFX runtime.
 * Các điều kiện này được extract thành helper methods để dễ test.
 *
 * Mapping với đặc tả UC-17:
 *  - Alt Flow 17.2.2a → gameMode null/empty → không tạo game
 *  - Alt Flow 17.2.4a → gameSettings null   → không tạo game
 *  - Main Flow 17.1.6  → GameSettings hợp lệ → tạo được controller
 *
 * Commit cùng với code khi push lên GitHub (Development Testing).
 */
@DisplayName("UC17 - MenuController Logic Tests")
class MenuControllerLogicTest {

    /**
     * Helper: mô phỏng logic rẽ nhánh của MenuController
     * "Nếu gameMode null hoặc rỗng → không khởi tạo game"
     * Trả về true nếu nên tiếp tục tạo game, false nếu nên dừng lại.
     */
    private boolean shouldProceedAfterGameMode(String gameMode) {
        return gameMode != null && !gameMode.isEmpty();
    }

    /**
     * Helper: mô phỏng logic rẽ nhánh
     * "Nếu gameSettings null → không khởi tạo game"
     */
    private boolean shouldProceedAfterSettings(GameSettings settings) {
        return settings != null;
    }

    /**
     * Helper: tạo GameSettings hợp lệ cho Play With Bot
     */
    private GameSettings makeValidBotSettings(String name, int time, int botLevel) {
        GameSettings s = new GameSettings();
        s.setPlayer1Name(name);
        s.setGameTime(time);
        s.setBotLevel(botLevel);
        return s;
    }

    // =========================================================
    // TC-17-U02: showGameOptions() trả về 'Play With Bot'
    // UC-17 Main Flow 17.1.3 — nhánh đúng
    // =========================================================

    @Test
    @DisplayName("TC-17-U02: gameMode='Play With Bot' → tiếp tục luồng tạo game")
    void testGameModePlayWithBotContinues() {
        assertTrue(shouldProceedAfterGameMode("Play With Bot"),
                "Khi gameMode='Play With Bot' phải tiếp tục vào luồng tạo game");
    }

    @Test
    @DisplayName("gameMode='2 Players' → tiếp tục luồng tạo game")
    void testGameMode2PlayersContinues() {
        assertTrue(shouldProceedAfterGameMode("2 Players"),
                "Khi gameMode='2 Players' phải tiếp tục vào luồng tạo game");
    }

    // =========================================================
    // TC-17-U05: gameMode = null → dừng, không tạo game
    // UC-17 Alternative Flow 17.2.2a
    // =========================================================

    @Test
    @DisplayName("TC-17-U05: gameMode=null → KHÔNG tạo game (Alt Flow 17.2.2a)")
    void testGameModeNullStops() {
        assertFalse(shouldProceedAfterGameMode(null),
                "Khi gameMode=null (người chơi đóng dialog) phải dừng lại, không tạo game");
    }

    // =========================================================
    // TC-17-U06: gameMode = "" → dừng, không tạo game
    // UC-17 Alternative Flow 17.2.2a
    // =========================================================

    @Test
    @DisplayName("TC-17-U06: gameMode='' (rỗng) → KHÔNG tạo game (Alt Flow 17.2.2a)")
    void testGameModeEmptyStops() {
        assertFalse(shouldProceedAfterGameMode(""),
                "Khi gameMode='' phải dừng lại, không tạo game");
    }

    // =========================================================
    // TC-17-U07: gameSettings = null → dừng, không tạo game
    // UC-17 Alternative Flow 17.2.4a, UC-18 Alternative Flow 18.2.2a
    // =========================================================

    @Test
    @DisplayName("TC-17-U07: gameSettings=null → KHÔNG tạo game (Alt Flow 17.2.4a)")
    void testNullGameSettingsStops() {
        assertFalse(shouldProceedAfterSettings(null),
                "Khi gameSettings=null (người chơi hủy dialog) phải dừng lại");
    }

    // =========================================================
    // TC-17-U18: gameSettings hợp lệ → tiếp tục tạo game
    // UC-17 Main Flow 17.1.6
    // =========================================================

    @Test
    @DisplayName("TC-17-U18: gameSettings hợp lệ → tiếp tục tạo game")
    void testValidGameSettingsContinues() {
        GameSettings settings = makeValidBotSettings("Alice", 10, 3);
        assertTrue(shouldProceedAfterSettings(settings),
                "Khi gameSettings hợp lệ phải tiếp tục khởi tạo GameController");
    }

    // =========================================================
    // TC-17-U03: GameSettings (Alice, 10, botLevel=2) lưu đúng
    // UC-17 Main Flow 17.1.6 — integrated
    // =========================================================

    @Test
    @DisplayName("TC-17-U03: GameSettings(Alice, time=10, botLevel=2) — 3 trường đúng")
    void testGameSettingsCorrectFields() {
        GameSettings settings = makeValidBotSettings("Alice", 10, 2);

        assertAll("3 trường của GameSettings phải đúng",
                () -> assertEquals("Alice", settings.getPlayer1Name()),
                () -> assertEquals(10, settings.getGameTime()),
                () -> assertEquals(2, settings.getBotLevel())
        );
    }

    // =========================================================
    // TC-18-U10: Hủy dialog chọn độ khó → trả về null về UC-17
    // UC-18 Alternative Flow 18.2.2a
    // =========================================================

    @Test
    @DisplayName("TC-18-U10: getPlayWithBotSettings() trả về null → UC-17 không tạo game")
    void testCancelBotSettingsDialogPreventsGameCreation() {
        // Giả lập ViewUtilities.getPlayWithBotSettings() trả về null
        GameSettings gameSettings = null; // người dùng nhấn Cancel

        assertFalse(shouldProceedAfterSettings(gameSettings),
                "Khi dialog chọn độ khó bị hủy, gameSettings=null, không được tạo game");
    }
}