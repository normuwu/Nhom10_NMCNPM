package coganhgame.Model.Game;

import coganhgame.Model.Move.Move;
import coganhgame.Model.Move.MoveResult;
import coganhgame.Model.Piece;
import coganhgame.Model.Tile.Tile;
import coganhgame.Utilities.Constants;

import java.util.ArrayList;

/**
 * Extension of Game that provides methods for Bot AI simulation:
 * - generateMoves() — list all legal moves for the current player
 * - makeMove() — execute a move and switch turn (for simulation)
 * - undoMove() — revert a simulated move
 */
public class GameWithBot extends Game {

    public GameWithBot(String playerName, int timeLimit, int botLevel) {
        super(playerName, timeLimit, botLevel);
    }

    /** Generate all legal moves for the current player */
    public ArrayList<Move> generateMoves() {
        ArrayList<Move> moves = new ArrayList<>();
        if (isOpening()) {
            // Opening: find pieces adjacent to the opening tile
            for (Tile tile : getOpeningTile().getConnectedTiles(board)) {
                if (tile.hasPiece() && tile.getPiece().getSide() == getCurrentPlayer().getSide()) {
                    moves.add(new Move(tile, getOpeningTile()));
                }
            }
            setOpeningTile(null); // temp clear so we don't loop forever
            return moves;
        }
        // Normal move: for each of the player's pieces, list all available destination tiles
        for (int row = 0; row < Constants.HEIGHT; row++) {
            for (int col = 0; col < Constants.WIDTH; col++) {
                Piece piece = board[row][col].getPiece();
                if (piece == null || piece.getSide() != getCurrentPlayer().getSide()) continue;
                for (Tile tile : board[row][col].getAvailableMoves(board)) {
                    moves.add(new Move(board[row][col], tile));
                }
            }
        }
        return moves;
    }

    /** Execute a move during simulation and switch player */
    public MoveResult makeMove(Move move) {
        MoveResult moveResult = processMove(move);
        switchPlayer();
        return moveResult;
    }

    /** Undo a simulated move */
    public void undoMove(Move move, MoveResult moveResult) {
        Piece piece = move.toTile().getPiece();
        move.toTile().removePiece();
        move.fromTile().setPiece(piece);
        if (moveResult.capturedPieces() != null) {
            getCurrentPlayer().increaseTotalPiece(moveResult.capturedPieces().size());
            getOpponent().decreaseTotalPiece(moveResult.capturedPieces().size());
            for (Piece capturedPiece : moveResult.capturedPieces()) {
                capturedPiece.flipSide();
            }
        }
        switchPlayer();
    }
}
