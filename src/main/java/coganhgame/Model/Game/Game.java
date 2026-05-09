package coganhgame.Model.Game;

import coganhgame.Exception.GameNotFoundException;
import coganhgame.Model.Move.Move;
import coganhgame.Model.Move.MoveResult;
import coganhgame.Model.Piece;
import coganhgame.Model.Player.HumanPlayer;
import coganhgame.Model.Player.Player;
import coganhgame.Model.Tile.Tile;
import coganhgame.Utilities.Constants;
import coganhgame.Utilities.ViewUtilities;

import java.io.*;
import java.util.ArrayList;

public class Game implements Serializable {
    protected Tile[][] board;
    private final Player player1;
    private final Player player2;
    private Player currentPlayer;
    private final int timeLimit;
    private Tile openingTile = null;

    public Game(String name1, String name2, int timeLimit) {
        this.player1 = new HumanPlayer(name1, true, timeLimit); // player1 is red and turn first
        this.player2 = new HumanPlayer(name2, false, timeLimit); // player2 is blue and turn second
        this.timeLimit = timeLimit;
        this.currentPlayer = this.player1;
        initBoard();
    }

    private void initBoard() {
        // init board
        this.board = new Tile[Constants.WIDTH][Constants.HEIGHT];

        // init pieces
        for (int row = 0; row < Constants.HEIGHT; row++) {
            for (int col = 0; col < Constants.WIDTH; col++) {
                Piece piece = null;
                // if the side is true, the piece is red, otherwise it is blue
                // make the first row of the board red
                if (row == 0) {
                    piece = new Piece(true);
                }
                // make the last row of the board blue
                if (row == Constants.HEIGHT - 1) {
                    piece = new Piece(false);
                }
                // make the first column of the board red
                if (col == 0) {
                    if (row == 1) {
                        piece = new Piece(true);
                    } else if (row == 2 || row == 3) {
                        piece = new Piece(false);
                    }
                }
                // make the last column of the board blue
                if (col == Constants.WIDTH - 1) {
                    if (row == Constants.HEIGHT - 2) {
                        piece = new Piece(false);
                    } else if (row == Constants.HEIGHT - 3 || row == Constants.HEIGHT - 4) {
                        piece = new Piece(true);
                    }
                }

                this.board[row][col] = Tile.getTileType(piece, row, col);
            }
        }
    }

    public Tile[][] getBoard() {
        return this.board;
    }

    public Player getPlayer1() {
        return this.player1;
    }

    public Player getPlayer2() {
        return this.player2;
    }

    public Player getCurrentPlayer() {
        return this.currentPlayer;
    }

    public Player getOpponent() {
        if (this.currentPlayer == this.player1) {
            return this.player2;
        } else {
            return this.player1;
        }
    }

    public int getTimeLimit() {
        return this.timeLimit;
    }

    public Tile getOpeningTile() {
        return this.openingTile;
    }

    public void setOpeningTile(Tile openingTile) {
        this.openingTile = openingTile;
    }

    public boolean isOpening() {
        return this.openingTile != null;
    }

    public void switchPlayer() {
        if (this.currentPlayer == this.player1) {
            this.currentPlayer = this.player2;
        } else {
            this.currentPlayer = this.player1;
        }
    }

    public MoveResult processMove(Move move) {

        return null;
    }

    private ArrayList<Piece> getCarriedPieces(Tile toTile) {
        ArrayList<Piece> carriedPieces = new ArrayList<>();
        int row = toTile.getRow();
        int col = toTile.getCol();

        // Lấy các ô xung quanh ô vừa đi tới
        ArrayList<Tile> connectedTilesOfToTile = toTile.getConnectedTiles(this.board);
        Piece piece = this.board[row][col].getPiece();

        // Dùng 2 vòng lặp để ghép cặp tất cả các ô xung quanh
        for (int i = 0; i < connectedTilesOfToTile.size(); i++) {
            Tile tile1 = connectedTilesOfToTile.get(i);
            for (int j = i + 1; j < connectedTilesOfToTile.size(); j++) {
                Tile tile2 = connectedTilesOfToTile.get(j);

                // CÔNG THỨC ĂN TIỀN: Kiểm tra tính đối xứng qua ô trung tâm (toTile)
                // Nếu thỏa mãn công thức này, tile1 và tile2 chắc chắn nằm đối diện nhau qua toTile
                if (tile1.getRow() + tile2.getRow() != 2 * row || tile1.getCol() + tile2.getCol() != 2 * col) {
                    continue; // Không đối xứng thì bỏ qua
                }

                // Nếu 2 ô đối xứng đều có quân cờ
                if (tile1.hasPiece() && tile2.hasPiece()) {
                    Piece piece1 = tile1.getPiece();
                    Piece piece2 = tile2.getPiece();

                    // Kiểm tra xem 2 quân đó có cùng phe với nhau và KHÁC phe với mình không
                    if (piece1.getSide() == piece2.getSide() && piece1.getSide() != piece.getSide()) {
                        // Nếu đúng -> Thực hiện Gánh (Bắt làm tù binh)
                        carriedPieces.add(piece1);
                        carriedPieces.add(piece2);
                        piece1.flipSide(); // Lật màu
                        piece2.flipSide(); // Lật màu
                    }
                }
            }
        }
        return carriedPieces;
    }


    public ArrayList<Piece> getSurroundedPieces() {
        ArrayList<Piece> surroundedPieces = new ArrayList<>();
        boolean[][] visited = new boolean[Constants.HEIGHT][Constants.WIDTH];
        for (int row = 0; row < Constants.HEIGHT; row++) {
            for (int col = 0; col < Constants.WIDTH; col++) {
                Tile tile = this.board[row][col];
                if (tile.hasPiece() && !visited[row][col] && tile.getPiece().getSide() != this.currentPlayer.getSide()) {
                    // for each piece that has not been visited, we use flood fill algorithm to find the group of pieces
                    // that form a group, if the group is surrounded, we flip the side of the pieces in the group
                    // and add them to the surrounded pieces
                    ArrayList<Piece> group = new ArrayList<>();
                    if (floodFill(row, col, group, visited)) {
                        flipGroup(group);
                        surroundedPieces.addAll(group);
                    }
                }
            }
        }
        return surroundedPieces;
    }

    private boolean floodFill(int row, int col, ArrayList<Piece> group, boolean[][] visited) {
        // this algorithm is used to find the pieces that form a group then check if the group is surrounded
        Piece piece = this.board[row][col].getPiece();
        visited[row][col] = true;
        group.add(piece);

        boolean isSurrounded = true;
        ArrayList<Tile> connectedTiles = board[row][col].getConnectedTiles(this.board);
        for (Tile tile : connectedTiles) {
            if (!tile.hasPiece()) {
                isSurrounded = false;
            } else if (tile.getPiece().getSide() == piece.getSide() && !visited[tile.getRow()][tile.getCol()]) {
                isSurrounded &= floodFill(tile.getRow(), tile.getCol(), group, visited);
            }
        }
        return isSurrounded;
    }


    private void flipGroup(ArrayList<Piece> group) {
    }

    public ArrayList<Tile> checkOpeningTile(Tile tile, boolean side) {

        return null;
    }

    public boolean isGameOver() {
        return false;
    }

    public void resetGame() {

    }

    public void saveGame() {

    }

    public static Game loadGame() {

        return null;
    }
}