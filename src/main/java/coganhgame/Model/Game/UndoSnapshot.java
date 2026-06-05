package coganhgame.Model.Game;

import coganhgame.Model.Piece;
import coganhgame.Model.Tile.Tile;
import coganhgame.Utilities.Constants;

/**
 * UC20 Undo Move
 * UC21 Redo Move
 * Lưu trữ trạng thái game để phục hồi khi Undo/Redo
 */
public class UndoSnapshot {

    /** Piece at each board position: null if empty */
    private final Piece[][] pieceGrid;

    /** Total piece count of player 1 before this move */
    private final int player1PieceCount;

    /** Total piece count of player 2 before this move */
    private final int player2PieceCount;

    /** Whether player 1 was the current player before this move */
    private final boolean currentPlayerIsPlayer1;

    /** Whether there was an opening tile */
    private final boolean hasOpeningTile;

    /** Opening tile row (meaningful only if hasOpeningTile is true) */
    private final int openingRow;

    /** Opening tile col (meaningful only if hasOpeningTile is true) */
    private final int openingCol;

    /**
     * UC20 Main Flow 20.1.4
     * UC21 Main Flow 21.1.3
     * Tạo snapshot lưu trạng thái hiện tại của game
     */
    public UndoSnapshot(Tile[][] board, int player1PieceCount, int player2PieceCount,
                        boolean currentPlayerIsPlayer1, boolean hasOpeningTile,
                        int openingRow, int openingCol) {

        // UC20 Main Flow 20.1.4
        // UC21 Main Flow 21.1.3
        // Sao chép toàn bộ trạng thái bàn cờ
        this.pieceGrid = deepCopyPieceGrid(board);

        // UC20 Main Flow 20.1.4
        // UC21 Main Flow 21.1.3
        // Lưu số quân Player 1
        this.player1PieceCount = player1PieceCount;

        // UC20 Main Flow 20.1.4
        // UC21 Main Flow 21.1.3
        // Lưu số quân Player 2
        this.player2PieceCount = player2PieceCount;

        // UC20 Main Flow 20.1.4
        // UC21 Main Flow 21.1.3
        // Lưu người chơi hiện tại
        this.currentPlayerIsPlayer1 = currentPlayerIsPlayer1;

        // UC20 Main Flow 20.1.4
        // UC21 Main Flow 21.1.3
        // Lưu trạng thái Opening Tile
        this.hasOpeningTile = hasOpeningTile;

        this.openingRow = openingRow;
        this.openingCol = openingCol;
    }

    /**
     * UC20 Main Flow 20.1.6
     * UC21 Main Flow 21.1.5
     * Khôi phục trạng thái game từ snapshot
     */
    public void restore(Game game, Tile[][] board) {

        // UC20 Main Flow 20.1.6
        // UC21 Main Flow 21.1.5
        // Khôi phục trạng thái các quân cờ trên bàn cờ
        for (int r = 0; r < Constants.HEIGHT; r++) {
            for (int c = 0; c < Constants.WIDTH; c++) {

                board[r][c].removePiece();

                if (pieceGrid[r][c] != null) {

                    Piece restored = pieceGrid[r][c];

                    board[r][c].setPiece(restored);
                }
            }
        }

        // UC20 Main Flow 20.1.6
        // UC21 Main Flow 21.1.5
        // Khôi phục số lượng quân của Player 1
        game.getPlayer1().increaseTotalPiece(
                player1PieceCount - game.getPlayer1().getTotalPiece());

        // UC20 Main Flow 20.1.6
        // UC21 Main Flow 21.1.5
        // Khôi phục số lượng quân của Player 2
        game.getPlayer2().increaseTotalPiece(
                player2PieceCount - game.getPlayer2().getTotalPiece());

        // UC20 Main Flow 20.1.6
        // UC21 Main Flow 21.1.5
        // Khôi phục lượt chơi
        if (currentPlayerIsPlayer1 &&
                game.getCurrentPlayer() != game.getPlayer1()) {

            game.switchPlayer();

        } else if (!currentPlayerIsPlayer1 &&
                game.getCurrentPlayer() != game.getPlayer2()) {

            game.switchPlayer();
        }

        // UC20 Main Flow 20.1.6
        // UC21 Main Flow 21.1.5
        // Khôi phục Opening Tile
        if (hasOpeningTile) {

            game.setOpeningTile(board[openingRow][openingCol]);

        } else {

            game.setOpeningTile(null);
        }
    }

    /**
     * UC20 Main Flow 20.1.4
     * UC21 Main Flow 21.1.3
     * Tạo bản sao độc lập của bàn cờ
     */
    private Piece[][] deepCopyPieceGrid(Tile[][] source) {

        Piece[][] copy = new Piece[Constants.HEIGHT][Constants.WIDTH];

        for (int r = 0; r < Constants.HEIGHT; r++) {
            for (int c = 0; c < Constants.WIDTH; c++) {

                Piece p = source[r][c].getPiece();

                copy[r][c] =
                        (p != null)
                                ? new Piece(p.getSide())
                                : null;
            }
        }

        return copy;
    }

    // --- Getters (for debugging / display) ---

    public int getPlayer1PieceCount() {
        return player1PieceCount;
    }

    public int getPlayer2PieceCount() {
        return player2PieceCount;
    }

    public boolean isCurrentPlayerPlayer1() {
        return currentPlayerIsPlayer1;
    }

    public boolean hasOpeningTile() {
        return hasOpeningTile;
    }
}
