package coganhgame.Model.Game;

import coganhgame.Model.Move.Move;
import coganhgame.Model.Move.MoveResult;
import coganhgame.Model.Piece;
import coganhgame.Model.Tile.Tile;
import coganhgame.Utilities.Constants;

import java.util.ArrayList;

/** * Lớp mở rộng của Game, đóng vai trò là Model xử lý dữ liệu cho Use Case "Bot thực hiện nước đi".
 * Cung cấp các phương thức mô phỏng (Simulation) phục vụ cho thuật toán Minimax:
 * - generateMoves(): Luồng quét và sinh toàn bộ nước đi hợp lệ.
 * - makeMove(): Luồng thực thi thử một nước đi.
 * - undoMove(): Luồng hoàn tác (rollback) trạng thái sau khi thử.
 */
public class GameWithBot extends Game {

    public GameWithBot(String playerName, int timeLimit, int botLevel) {
        super(playerName, timeLimit, botLevel);
    }

    /** * Chức năng: Sinh tất cả các nước đi hợp lệ cho người chơi hiện hành.
     * Phục vụ đặc tả: Tách biệt rõ [Luồng cơ bản] và [Luồng rẽ nhánh] (xử lý luật Mở).
     */
    public ArrayList<Move> generateMoves() {
        // 1. Khởi tạo danh sách lưu trữ tập hợp các nước đi có thể thực hiện
        ArrayList<Move> moves = new ArrayList<>();

        /*
         * 2. [Luồng rẽ nhánh] - Kiểm tra điều kiện bàn cờ đang ở thế "Mở" (Opening).
         * Đặc tả quy tắc: Nếu đối phương chủ động "Mở" (rút quân tạo ô trống),
         * người chơi bắt buộc phải đưa quân kề cận vào chính ô trống đó để ăn quân.
         */
        if (isOpening()) {
            // Duyệt qua các ô liền kề với ô đang bị Mở
            for (Tile tile : getOpeningTile().getConnectedTiles(board)) {
                // [Điều kiện]: Ô kề có quân cờ VÀ quân cờ đó thuộc phe đang tới lượt
                if (tile.hasPiece() && tile.getPiece().getSide() == getCurrentPlayer().getSide()) {
                    // Thêm nước đi bắt buộc: Di chuyển quân cờ đó vào ô Mở
                    moves.add(new Move(tile, getOpeningTile()));
                }
            }
            // 3. Tạm thời xóa trạng thái Mở để Bot có thể duyệt cây đệ quy mà không bị kẹt vòng lặp
            setOpeningTile(null);
            return moves;
        }

        /*
         * 4. [Luồng cơ bản] - Bàn cờ ở trạng thái bình thường (Không bị thế Mở).
         * Hệ thống tiến hành quét toàn bộ ma trận bàn cờ để tìm quân cờ của phe mình,
         * sau đó đánh giá các ô kề cận để sinh ra mọi nước đi hợp lệ.
         */
        for (int row = 0; row < Constants.HEIGHT; row++) {
            for (int col = 0; col < Constants.WIDTH; col++) {
                Piece piece = board[row][col].getPiece();

                // [Ràng buộc]: Bỏ qua nếu là ô trống hoặc quân cờ thuộc phe đối phương
                if (piece == null || piece.getSide() != getCurrentPlayer().getSide()) continue;

                // 5. Với mỗi quân cờ thỏa mãn, lấy danh sách các ô kề cận có thể đi tới
                for (Tile tile : board[row][col].getAvailableMoves(board)) {
                    // Ghi nhận nước đi vào danh sách
                    moves.add(new Move(board[row][col], tile));
                }
            }
        }
        return moves;
    }

    /** * Chức năng: Thực thi nước đi giả lập và cập nhật trạng thái hệ thống.
     */
    public MoveResult makeMove(Move move) {
        // 1. Gọi hàm processMove (luồng lõi) để thực hiện tính toán luật Gánh/Vây và cập nhật Model
        MoveResult moveResult = processMove(move);

        // 2. [Hậu điều kiện]: Ép chuyển lượt sang phe đối phương để Bot tiếp tục duyệt lớp Minimax tiếp theo
        switchPlayer();

        return moveResult;
    }

    /** * Chức năng: Hoàn tác (Undo) nước đi giả lập, phục hồi lại dữ liệu trạng thái (State).
     * Phục vụ đặc tả: Đảm bảo tính toàn vẹn dữ liệu của thuật toán quay lui (Backtracking).
     */
    public void undoMove(Move move, MoveResult moveResult) {
        // 1. Phục hồi vị trí: Nhấc quân cờ từ ô đích trả về lại ô xuất phát ban đầu
        Piece piece = move.toTile().getPiece();
        move.toTile().removePiece();
        move.fromTile().setPiece(piece);

        /*
         * 2. Phục hồi quân bị bắt (Hoàn tác luật Gánh/Vây).
         * Trả lại số lượng quân cờ cho các phe và lật lại mặt màu nguyên thủy của quân cờ.
         */
        if (moveResult.capturedPieces() != null) {
            // [Lưu ý logic]: Do hàm makeMove() đã gọi switchPlayer() trước đó, nên currentPlayer
            // hiện tại thực chất là phe vừa bị mất quân. Do đó ta phải cộng quân (increase) cho họ.
            getCurrentPlayer().increaseTotalPiece(moveResult.capturedPieces().size());
            getOpponent().decreaseTotalPiece(moveResult.capturedPieces().size());

            // Duyệt qua danh sách quân bị bắt và lật mặt (flipSide) để trả về màu cũ
            for (Piece capturedPiece : moveResult.capturedPieces()) {
                capturedPiece.flipSide();
            }
        }

        // 3. [Hậu điều kiện]: Đảo lượt chơi về lại cho phe thực hiện nước đi giả lập ban đầu
        switchPlayer();
    }
}