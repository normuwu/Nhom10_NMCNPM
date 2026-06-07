package coganhgame.Model.Player;

import coganhgame.Utilities.Constants;
import coganhgame.Model.Game.GameWithBot;
import coganhgame.Model.Move.Move;
import coganhgame.Model.Move.MoveResult;
import coganhgame.Model.Piece;
import coganhgame.Model.Tile.Tile;
import java.util.ArrayList;


public class BotPlayer extends Player {

    // UC-18: Chọn độ khó Bot
    // Độ sâu tìm kiếm của thuật toán Minimax, botLevel càng cao Bot càng mạnh
    private final int botLevel;

    private long startTime;

    // UC-18 Main Flow 18.1.5
    // Khởi tạo Bot với tên "Bot", gán vào BLUE_SIDE và lưu độ khó
    public BotPlayer(int botLevel) {
        super("Bot", Constants.BLUE_SIDE);
        this.botLevel = botLevel;
    }

    // UC-18 Main Flow 18.1.6
    // Tìm nước đi tốt nhất cho Bot bằng cách duyệt toàn bộ nước đi hợp lệ
    public Move getBestMove(GameWithBot game) {
        Move bestMove;
        int bestScore = -9999;
        ArrayList<Move> allMoves = game.generateMoves();
        int[] scores = new int[allMoves.size()];
        ArrayList<Move> bestMoves = new ArrayList<>();

        for (int i = 0; i < allMoves.size(); i++) {
            Move move = allMoves.get(i);
            MoveResult moveResult = game.makeMove(move);

            // UC-18 Alternative Flow 18.2.6b
            // Ưu tiên ngay nước đi kết thúc game, không cần tìm kiếm thêm
            if (game.isGameOver()) {
                bestMoves.add(move);
            } else {
                // UC-18 Main Flow 18.1.6
                // Đánh giá nước đi bằng thuật toán minimax với alpha-beta pruning
                int score = minimax(game, this.botLevel - 1, -10000, 10000, false);
                scores[i] = score;
                bestScore = Math.max(bestScore, score);
            }
            game.undoMove(move, moveResult);
        }

        // UC-18 Alternative Flow 18.2.6b
        // Thực hiện ngay nước đi thắng nếu tồn tại
        if (!bestMoves.isEmpty()) {
            bestMove = bestMoves.get((int) (Math.random() * bestMoves.size()));
            return bestMove;
        }

        // UC-18 Alternative Flow 18.2.6a
        // Thu thập tất cả nước đi có điểm bằng bestScore
        for (int i = 0; i < allMoves.size(); i++) {
            if (scores[i] == bestScore) {
                bestMoves.add(allMoves.get(i));
            }
        }

        // UC-18 Alternative Flow 18.2.6a
        // Chọn ngẫu nhiên một nước đi trong số các nước đi tốt nhất
        bestMove = bestMoves.get((int) (Math.random() * bestMoves.size()));
        return bestMove;
    }

    // UC-18 Main Flow 18.1.6
    // Thuật toán Minimax kết hợp Alpha-Beta Pruning để tìm nước đi tối ưu
    private int minimax(GameWithBot game, int depth, int alpha, int beta, boolean maximizingPlayer) {
        positionCount++;

        // UC-18 Main Flow 18.1.6
        // Trả về điểm đánh giá khi đạt độ sâu tối đa hoặc game kết thúc
        if (depth == 0 || game.isGameOver()) {
            return evaluateBoard(game.getBoard());
        }

        if (maximizingPlayer) {
            // UC-18 Main Flow 18.1.6
            // Lượt Bot: chọn nước đi có điểm cao nhất
            int maxEval = -9999;
            ArrayList<Move> allMoves = game.generateMoves();
            for (Move move : allMoves) {
                MoveResult moveResult = game.makeMove(move);
                int eval = minimax(game, depth - 1, alpha, beta, false);
                game.undoMove(move, moveResult);
                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, eval);
                // Cắt tỉa Alpha-Beta khi không cần xét thêm nhánh
                if (beta <= alpha) break;
            }
            return maxEval;
        } else {
            // UC-18 Main Flow 18.1.6
            // Lượt đối thủ: chọn nước đi có điểm thấp nhất
            int minEval = 9999;
            ArrayList<Move> allMoves = game.generateMoves();
            for (Move move : allMoves) {
                MoveResult moveResult = game.makeMove(move);
                int eval = minimax(game, depth - 1, alpha, beta, true);
                game.undoMove(move, moveResult);
                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, eval);
                // Cắt tỉa Alpha-Beta khi không cần xét thêm nhánh
                if (beta <= alpha) break;
            }
            return minEval;
        }
    }

    // UC-18 Main Flow 18.1.6
    // Ma trận điểm vị trí thuận lợi trên bàn cờ, trung tâm có điểm cao hơn
    private final int[][] favourablePosition = {
            {-1, 0, 1, 0, -1},
            {0, 0, 1, 0, 0},
            {1, 1, 2, 1, 1},
            {0, 0, 1, 0, 0},
            {-1, 0, 1, 0, -1}
    };

    // UC-18 Main Flow 18.1.6
    // Đánh giá bàn cờ: cộng điểm cho BLUE_SIDE (Bot), trừ điểm cho RED_SIDE (đối thủ)
    private int evaluateBoard(Tile[][] board) {
        int totalValue = 0;
        for (int row = 0; row < Constants.HEIGHT; row++) {
            for (int col = 0; col < Constants.WIDTH; col++) {
                if (!board[row][col].hasPiece()) continue;
                Piece piece = board[row][col].getPiece();
                if (piece.getSide() == Constants.RED_SIDE) {
                    totalValue -= 10;
                    totalValue -= favourablePosition[row][col];
                } else {
                    totalValue += 10;
                    totalValue += favourablePosition[row][col];
                }
            }
        }
        return totalValue;
    }


    @Override
    public void playTimer() {
        startTime = System.currentTimeMillis();
    }

    @Override
    public void pauseTimer() {
        long endTime = System.currentTimeMillis();
        int time = (int) (endTime - startTime);
        totalTime += time;
    }
}


