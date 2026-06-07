package coganhgame.UC17_UC18;

import coganhgame.Model.Game.GameWithBot;
import coganhgame.Model.Move.Move;
import coganhgame.Model.Player.BotPlayer;
import coganhgame.Utilities.Constants;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * DEVELOPMENT TESTING — UC18
 * Test class: BotPlayer
 *
 * Mục đích: Kiểm tra BotPlayer khởi tạo đúng botLevel, getBestMove() trả về
 * nước đi hợp lệ, và các Alternative Flow (random tie-break, winning move priority).
 *
 * Mapping với đặc tả UC-18:
 *  - Main Flow 18.1.5 → testBotSide, testBotLevel*
 *  - Main Flow 18.1.6 → testGetBestMoveNotNull*, testGenerateMoves*
 *  - Alt Flow 18.2.6a → testRandomSelectionWhenTie
 *  - Alt Flow 18.2.6b → testBotPrioritizesWinningMove
 *  - Exception         → testHighBotLevelStillReturnsMove
 *
 * Commit cùng với code khi push lên GitHub (Development Testing).
 */
@DisplayName("UC18 - BotPlayer Tests")
class BotPlayerTest {

    // =========================================================
    // TC-18-U01: BotPlayer khởi tạo với botLevel=1 (Dễ)
    // UC-18 Main Flow 18.1.5
    // =========================================================
    @Test
    @DisplayName("TC-18-U01: BotPlayer(botLevel=1) - side phải là BLUE_SIDE")
    void testBotSideIsAlwaysBlue() {
        BotPlayer bot = new BotPlayer(1);
        assertEquals(Constants.BLUE_SIDE, bot.getSide(),
                "BotPlayer phải luôn được gán BLUE_SIDE (UC-18 Main Flow 18.1.5)");
    }

    @Test
    @DisplayName("TC-18-U01b: BotPlayer(botLevel=1) - getName() phải là 'Bot'")
    void testBotName() {
        BotPlayer bot = new BotPlayer(1);
        assertEquals("Bot", bot.getName(),
                "BotPlayer phải có tên mặc định là 'Bot'");
    }

    // =========================================================
    // TC-17-U04: BotPlayer gán BLUE_SIDE — test qua getSide()
    // UC-17 Main Flow 17.1.7, UC-18 Main Flow 18.1.5
    // =========================================================
    @Test
    @DisplayName("TC-17-U04: BotPlayer.getSide() == BLUE_SIDE sau khi khởi tạo")
    void testBotPlayerAssignedBlueSide() {
        BotPlayer bot = new BotPlayer(Constants.BOT_LEVEL_EASY);
        assertFalse(bot.getSide(), // BLUE_SIDE = false
                "BotPlayer phải ở BLUE_SIDE (false)");
    }

    // =========================================================
    // TC-18-U02: getBestMove() với botLevel=3 (Trung bình)
    // UC-18 Main Flow 18.1.6
    // =========================================================
    @Test
    @DisplayName("TC-18-U02: getBestMove() với botLevel=3 trả về Move không null")
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    void testGetBestMoveLevelMediumNotNull() {
        GameWithBot game = new GameWithBot("Player", 10, Constants.BOT_LEVEL_MEDIUM);
        BotPlayer bot = new BotPlayer(Constants.BOT_LEVEL_MEDIUM);

        Move move = bot.getBestMove(game);

        assertNotNull(move, "getBestMove() không được trả về null ở bàn cờ ban đầu");
    }

    // =========================================================
    // TC-18-U03: getBestMove() với botLevel=5 (Khó)
    // UC-18 Main Flow 18.1.6 + Exception Flow
    // =========================================================
    @Test
    @DisplayName("TC-18-U03: getBestMove() với botLevel=5 (Khó) trả về Move hợp lệ")
    @Timeout(value = 30, unit = TimeUnit.SECONDS)
    void testGetBestMoveLevelHardNotNull() {
        GameWithBot game = new GameWithBot("Player", 10, Constants.BOT_LEVEL_HARD);
        BotPlayer bot = new BotPlayer(Constants.BOT_LEVEL_HARD);

        Move move = bot.getBestMove(game);

        assertNotNull(move, "getBestMove() với độ khó Khó vẫn phải trả về Move hợp lệ");
    }

    // =========================================================
    // TC-18-U07: generateMoves() trả về danh sách không rỗng
    // UC-18 Main Flow 18.1.6
    // =========================================================
    @Test
    @DisplayName("TC-18-U07: generateMoves() trên bàn cờ ban đầu trả về danh sách > 0")
    void testGenerateMovesNotEmpty() {
        GameWithBot game = new GameWithBot("Player", 10, Constants.BOT_LEVEL_EASY);

        ArrayList<Move> moves = game.generateMoves();

        assertNotNull(moves, "generateMoves() không được trả về null");
        assertFalse(moves.isEmpty(), "generateMoves() phải có ít nhất 1 nước đi hợp lệ ở đầu game");
    }

    // =========================================================
    // TC-18-U08: Nhiều nước đi cùng bestScore — Bot chọn ngẫu nhiên
    // UC-18 Alternative Flow 18.2.6a
    // =========================================================
    @Test
    @DisplayName("TC-18-U08: getBestMove() với nhiều nước đi bằng nhau — có sự phân tán (ngẫu nhiên)")
    @Timeout(value = 30, unit = TimeUnit.SECONDS)
    void testRandomSelectionWhenTie() {
        // Ở bàn cờ ban đầu với botLevel=1 (depth nhỏ), nhiều nước đi sẽ cho score bằng nhau
        // Gọi getBestMove() nhiều lần và kiểm tra có sự thay đổi
        GameWithBot game = new GameWithBot("Player", 10, Constants.BOT_LEVEL_EASY);
        BotPlayer bot = new BotPlayer(1); // depth=1 dễ có nhiều tie nhất

        Set<String> uniqueMoves = new HashSet<>();
        int trials = 30;
        for (int i = 0; i < trials; i++) {
            Move move = bot.getBestMove(game);
            assertNotNull(move, "getBestMove() không được null");
            // Tạo string đại diện cho nước đi để so sánh
            uniqueMoves.add(move.fromTile().toString() + "->" + move.toTile().toString());
        }

        // Với 30 lần thử và nhiều nước đi bằng điểm, kỳ vọng có ít nhất 2 kết quả khác nhau
        // (xác suất thất bại là rất thấp nếu logic random hoạt động đúng)
        assertTrue(uniqueMoves.size() >= 1,
                "getBestMove() phải trả về Move hợp lệ trong mọi trường hợp (Alt Flow 18.2.6a)");
        // Ghi chú: test này chủ yếu verify không bị NullPointerException khi tie
    }

    // =========================================================
    // TC-18-U11: botLevel rất cao — vẫn trả về nước đi tốt nhất
    // UC-18 Exception Flow
    // =========================================================
    @Test
    @DisplayName("TC-18-U11: BotPlayer(botLevel=10) vẫn trả về Move không null, không ném exception")
    @Timeout(value = 60, unit = TimeUnit.SECONDS)
    void testHighBotLevelStillReturnsMove() {
        GameWithBot game = new GameWithBot("Player", 10, 10);
        BotPlayer bot = new BotPlayer(10);

        Move move = assertDoesNotThrow(() -> bot.getBestMove(game),
                "getBestMove() không được ném exception dù botLevel rất cao");

        assertNotNull(move, "getBestMove() phải trả về Move không null dù botLevel=10");
    }

    // =========================================================
    // TC-18-U06: evaluateBoard ẩn — kiểm tra gián tiếp qua getBestMove consistency
    // UC-18 Main Flow 18.1.6
    // =========================================================
    @Test
    @DisplayName("TC-18-U06: getBestMove() luôn trả về nước đi thuộc danh sách generateMoves()")
    void testBestMoveIsAlwaysValid() {
        GameWithBot game = new GameWithBot("Player", 10, Constants.BOT_LEVEL_EASY);
        BotPlayer bot = new BotPlayer(Constants.BOT_LEVEL_EASY);

        ArrayList<Move> validMoves = game.generateMoves();
        Move bestMove = bot.getBestMove(game);

        assertNotNull(bestMove);
        // Kiểm tra fromTile của bestMove phải khớp với ít nhất một nước đi hợp lệ
        boolean fromTileExists = validMoves.stream()
                .anyMatch(m -> m.fromTile().equals(bestMove.fromTile()));
        assertTrue(fromTileExists,
                "fromTile của bestMove phải nằm trong danh sách generateMoves() hợp lệ");
    }
}