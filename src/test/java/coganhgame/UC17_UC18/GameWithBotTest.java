package coganhgame.UC17_UC18;

import coganhgame.Model.Game.GameWithBot;
import coganhgame.Model.Move.Move;
import coganhgame.Model.Move.MoveResult;
import coganhgame.Model.Tile.Tile;
import coganhgame.Utilities.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * DEVELOPMENT TESTING — UC18
 * Test class: GameWithBot
 *
 * Mục đích: Kiểm tra các phương thức mô phỏng (simulation) của GameWithBot
 * phục vụ thuật toán Minimax: generateMoves(), makeMove(), undoMove(), isGameOver().
 *
 * Mapping với đặc tả UC-18 Main Flow 18.1.6.
 *
 * Commit cùng với code khi push lên GitHub (Development Testing).
 */
@DisplayName("UC18 - GameWithBot Simulation Tests")
class GameWithBotTest {

    private GameWithBot game;

    @BeforeEach
    void setUp() {
        game = new GameWithBot("Player", 10, Constants.BOT_LEVEL_EASY);
    }

    // =========================================================
    // TC-18-U07: generateMoves() trả về danh sách không rỗng
    // UC-18 Main Flow 18.1.6
    // =========================================================

    @Test
    @DisplayName("TC-18-U07a: generateMoves() trên bàn cờ ban đầu không trả về null")
    void testGenerateMovesNotNull() {
        ArrayList<Move> moves = game.generateMoves();
        assertNotNull(moves, "generateMoves() không được trả về null");
    }

    @Test
    @DisplayName("TC-18-U07b: generateMoves() trên bàn cờ ban đầu có ít nhất 1 nước đi")
    void testGenerateMovesNotEmpty() {
        ArrayList<Move> moves = game.generateMoves();
        assertFalse(moves.isEmpty(),
                "Ở đầu game phải có ít nhất 1 nước đi hợp lệ cho current player");
    }

    @Test
    @DisplayName("TC-18-U07c: generateMoves() — mỗi Move có fromTile và toTile không null")
    void testGenerateMovesEachMoveIsValid() {
        ArrayList<Move> moves = game.generateMoves();
        for (Move move : moves) {
            assertNotNull(move.fromTile(), "fromTile không được null");
            assertNotNull(move.toTile(),   "toTile không được null");
            assertTrue(move.fromTile().hasPiece(),
                    "fromTile phải có quân cờ");
            assertFalse(move.toTile().hasPiece(),
                    "toTile phải là ô trống (ô đích)");
        }
    }

    // =========================================================
    // makeMove() và undoMove() — tính toàn vẹn dữ liệu
    // UC-18 Main Flow 18.1.6
    // =========================================================

    @Test
    @DisplayName("makeMove() - quân cờ di chuyển đến ô đích sau khi thực hiện nước đi")
    void testMakeMoveMoviesPiece() {
        ArrayList<Move> moves = game.generateMoves();
        assertFalse(moves.isEmpty());

        Move move = moves.get(0);
        Tile from = move.fromTile();
        Tile to   = move.toTile();

        assertTrue(from.hasPiece(), "from phải có quân trước makeMove");
        assertFalse(to.hasPiece(),  "to phải trống trước makeMove");

        game.makeMove(move);

        assertFalse(from.hasPiece(), "from phải trống SAU makeMove");
        assertTrue(to.hasPiece(),    "to phải có quân SAU makeMove");
    }

    @Test
    @DisplayName("undoMove() - bàn cờ phục hồi về trạng thái ban đầu sau undo")
    void testUndoMoveRestoresBoard() {
        ArrayList<Move> moves = game.generateMoves();
        assertFalse(moves.isEmpty());

        Move move = moves.get(0);
        Tile from = move.fromTile();
        Tile to   = move.toTile();

        // Chụp số quân trước
        int redPiecesBefore  = game.getPlayer1().getTotalPiece();
        int bluePiecesBefore = game.getPlayer2().getTotalPiece();

        MoveResult result = game.makeMove(move);
        game.undoMove(move, result);

        // Kiểm tra bàn cờ phục hồi
        assertTrue(from.hasPiece(),  "from phải có quân lại sau undoMove");
        assertFalse(to.hasPiece(),   "to phải trống lại sau undoMove");

        // Kiểm tra số quân phục hồi
        assertEquals(redPiecesBefore,  game.getPlayer1().getTotalPiece(),
                "Số quân đỏ phải phục hồi sau undoMove");
        assertEquals(bluePiecesBefore, game.getPlayer2().getTotalPiece(),
                "Số quân xanh phải phục hồi sau undoMove");
    }

    @Test
    @DisplayName("makeMove() chuyển lượt player — undoMove() chuyển lại")
    void testMakeAndUndoSwitchesPlayer() {
        String playerBefore = game.getCurrentPlayer().getName();

        ArrayList<Move> moves = game.generateMoves();
        Move move = moves.get(0);
        MoveResult result = game.makeMove(move);

        assertNotEquals(playerBefore, game.getCurrentPlayer().getName(),
                "Sau makeMove() phải đổi lượt sang player khác");

        game.undoMove(move, result);

        assertEquals(playerBefore, game.getCurrentPlayer().getName(),
                "Sau undoMove() phải quay lại lượt của player cũ");
    }

    // =========================================================
    // isGameOver() — chưa kết thúc ở bàn cờ ban đầu
    // UC-18 Main Flow 18.1.6
    // =========================================================

    @Test
    @DisplayName("isGameOver() = false ở bàn cờ ban đầu (chưa ai thắng)")
    void testIsGameOverFalseAtStart() {
        assertFalse(game.isGameOver(),
                "Game chưa kết thúc ở trạng thái ban đầu");
    }

    // =========================================================
    // Kiểm tra makeMove nhiều lượt liên tiếp không crash
    // UC-18 Main Flow 18.1.6 — Minimax traversal
    // =========================================================

    @Test
    @DisplayName("makeMove() và undoMove() lặp 5 lần liên tiếp không gây crash hay lỗi trạng thái")
    void testMultipleMakeAndUndoDoesNotCrash() {
        ArrayList<Move> moves = game.generateMoves();
        assertFalse(moves.isEmpty());

        // Thực hiện và hoàn tác 5 nước đi đầu tiên
        for (int i = 0; i < Math.min(5, moves.size()); i++) {
            Move move = moves.get(i);
            MoveResult result = assertDoesNotThrow(() -> game.makeMove(move),
                    "makeMove() không được ném exception");
            assertDoesNotThrow(() -> game.undoMove(move, result),
                    "undoMove() không được ném exception");
        }
    }
}