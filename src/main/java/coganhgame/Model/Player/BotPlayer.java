package coganhgame.Model.Player;

import coganhgame.Utilities.Constants;
import coganhgame.Model.Game.GameWithBot;
import coganhgame.Model.Move.Move;
import coganhgame.Model.Move.MoveResult;
import coganhgame.Model.Piece;
import coganhgame.Model.Tile.Tile;
import java.util.ArrayList;

/**
 * Bot AI player using Minimax algorithm with Alpha-Beta pruning.
 * Difficulty levels: Easy (depth=3), Medium (depth=5), Hard (depth=7)
 */
public class BotPlayer extends Player {
    private final int botLevel;
    private long startTime;
    public static int positionCount = 0;

    public BotPlayer(int botLevel) {
        super("Bot", Constants.BLUE_SIDE); // Bot always plays Blue (second player)
        this.botLevel = botLevel;
    }

    public int getBotLevel() { return botLevel; }
    /**
     * Evaluates all possible moves and returns the best one using Minimax + Alpha-Beta.
     * Immediately picks winning moves. Otherwise picks the move with highest score.
     */
    public Move getBestMove(GameWithBot game) {
        Move bestMove;
        int bestScore = -9999;
        ArrayList<Move> allMoves = game.generateMoves();
        int[] scores = new int[allMoves.size()];
        ArrayList<Move> bestMoves = new ArrayList<>();

        for (int i = 0; i < allMoves.size(); i++) {
            Move move = allMoves.get(i);
            MoveResult moveResult = game.makeMove(move);
            if (game.isGameOver()) {
                bestMoves.add(move); // winning move — take it immediately
            } else {
                int score = minimax(game, this.botLevel - 1, -10000, 10000, false);
                scores[i] = score;
                bestScore = Math.max(bestScore, score);
            }
            game.undoMove(move, moveResult);
        }

        if (!bestMoves.isEmpty()) {
            return bestMoves.get((int) (Math.random() * bestMoves.size()));
        }
        for (int i = 0; i < allMoves.size(); i++) {
            if (scores[i] == bestScore) bestMoves.add(allMoves.get(i));
        }
        return bestMoves.get((int) (Math.random() * bestMoves.size()));
    }

    /** Minimax with Alpha-Beta pruning */
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

    /** Position value matrix — center tiles are more valuable */
    private final int[][] favourablePosition = {
            {-1, 0, 1, 0, -1},
            {0, 0, 1, 0, 0},
            {1, 1, 2, 1, 1},
            {0, 0, 1, 0, 0},
            {-1, 0, 1, 0, -1}
    };

    /** Evaluate the board from the Bot's perspective (higher = better for Bot/Blue) */
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


