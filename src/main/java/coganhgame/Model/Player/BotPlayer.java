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

