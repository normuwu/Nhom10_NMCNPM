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
