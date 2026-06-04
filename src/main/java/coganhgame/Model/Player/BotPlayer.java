package coganhgame.Model.Player;

import coganhgame.Utilities.Constants;
import coganhgame.Model.Game.GameWithBot;
import coganhgame.Model.Move.Move;
import coganhgame.Model.Move.MoveResult;
import coganhgame.Model.Piece;
import coganhgame.Model.Tile.Tile;
import java.util.ArrayList;


public class BotPlayer extends Player {
    private final int botLevel;
    private long startTime;
    public static int positionCount = 0;

    public BotPlayer(int botLevel) {
        super("Bot", Constants.BLUE_SIDE); // Bot always plays Blue (second player)
        this.botLevel = botLevel;
    }

    public int getBotLevel() { return botLevel; }

    public Move getBestMove(GameWithBot game) {
        ArrayList<Move> allMoves = game.generateMoves();
        int bestScore = Integer.MIN_VALUE;
        Move bestMove = null;

        for (Move move : allMoves) {
            MoveResult moveResult = game.makeMove(move);

            if (game.isGameOver()) {
                game.undoMove(move, moveResult);
                return move; // nước thắng → trả về ngay, không cần tìm thêm
            }

            int score = minimax(game, this.botLevel - 1, Integer.MIN_VALUE, Integer.MAX_VALUE, false);
            game.undoMove(move, moveResult);

            if (score > bestScore) {
                bestScore = score;
                bestMove = move;
            }
        }

        return bestMove;
    }

    private int minimax(GameWithBot game, int depth, int alpha, int beta, boolean maximizingPlayer) {
        positionCount++;
        if (depth == 0 || game.isGameOver()) {
            return evaluateBoard(game.getBoard());
        }
        if (maximizingPlayer) {
            int maxEval = -9999;
            for (Move move : game.generateMoves()) {
                MoveResult moveResult = game.makeMove(move);
                int eval = minimax(game, depth - 1, alpha, beta, false);
                game.undoMove(move, moveResult);
                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, eval);
                if (beta <= alpha) break;
            }
            return maxEval;
        } else {
            int minEval = 9999;
            for (Move move : game.generateMoves()) {
                MoveResult moveResult = game.makeMove(move);
                int eval = minimax(game, depth - 1, alpha, beta, true);
                game.undoMove(move, moveResult);
                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, eval);
                if (beta <= alpha) break;
            }
            return minEval;
        }
    }

    private final int[][] favourablePosition = {
            {-1, 0, 1, 0, -1},
            {0, 0, 1, 0, 0},
            {1, 1, 2, 1, 1},
            {0, 0, 1, 0, 0},
            {-1, 0, 1, 0, -1}
    };

    private int evaluateBoard(Tile[][] board) {
        int totalValue = 0;
        for (int row = 0; row < Constants.HEIGHT; row++) {
            for (int col = 0; col < Constants.WIDTH; col++) {
                if (!board[row][col].hasPiece()) continue;
                Piece piece = board[row][col].getPiece();
                if (piece.getSide() == Constants.RED_SIDE) {
                    totalValue -= 10;                     // opponent piece
                    totalValue -= favourablePosition[row][col]; // subtract position value
                } else {
                    totalValue += 10;                     // bot's piece
                    totalValue += favourablePosition[row][col]; // add position value
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


