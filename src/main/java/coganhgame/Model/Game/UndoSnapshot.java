package coganhgame.Model.Game;    
         
    import coganhgame.Model.Piece;    
    import coganhgame.Model.Tile.Tile;    
    import coganhgame.Utilities.Constants;    
         
    /**    
     * Captures the full game state before a move is executed,    
     * so it can be restored later for Undo functionality.    
     * Stores deep copies of all pieces so the snapshot is independent of live game state.    
     */    
    public class UndoSnapshot {    
        private final Piece[][] pieceGrid;    
        private final int player1PieceCount;    
        private final int player2PieceCount;    
        private final boolean currentPlayerIsPlayer1;    
        private final boolean hasOpeningTile;    
        private final int openingRow;    
        private final int openingCol;    
         
        public UndoSnapshot(Tile[][] board, int player1PieceCount, int player2PieceCount,    
                            boolean currentPlayerIsPlayer1, boolean hasOpeningTile,    
                            int openingRow, int openingCol) {    
            this.pieceGrid = deepCopyPieceGrid(board);    
            this.player1PieceCount = player1PieceCount;    
            this.player2PieceCount = player2PieceCount;    
            this.currentPlayerIsPlayer1 = currentPlayerIsPlayer1;    
            this.hasOpeningTile = hasOpeningTile;    
            this.openingRow = openingRow;    
            this.openingCol = openingCol;    
        }    
         
        /** Restore this snapshot into the given game board */    
        public void restore(Game game, Tile[][] board) {    
            for (int r = 0; r < Constants.HEIGHT; r++) {    
                for (int c = 0; c < Constants.WIDTH; c++) {    
                    board[r][c].removePiece();    
                    if (pieceGrid[r][c] != null) {    
                        board[r][c].setPiece(pieceGrid[r][c]);    
                    }    
                }    
            }    
            game.getPlayer1().increaseTotalPiece(player1PieceCount - game.getPlayer1().getTotalPiece());    
            game.getPlayer2().increaseTotalPiece(player2PieceCount - game.getPlayer2().getTotalPiece());    
            if (currentPlayerIsPlayer1 && game.getCurrentPlayer() != game.getPlayer1()) {    
                game.switchPlayer();    
            } else if (!currentPlayerIsPlayer1 && game.getCurrentPlayer() != game.getPlayer2()) {    
                game.switchPlayer();    
            }    
            if (hasOpeningTile) {    
                game.setOpeningTile(board[openingRow][openingCol]);    
            } else {    
                game.setOpeningTile(null);    
            }    
        }    
         
        private Piece[][] deepCopyPieceGrid(Tile[][] source) {    
            Piece[][] copy = new Piece[Constants.HEIGHT][Constants.WIDTH];    
            for (int r = 0; r < Constants.HEIGHT; r++) {    
                for (int c = 0; c < Constants.WIDTH; c++) {    
                    Piece p = source[r][c].getPiece();    
                    copy[r][c] = (p != null) ? new Piece(p.getSide()) : null;    
                }    
            }    
            return copy;    
        }    
    }    
