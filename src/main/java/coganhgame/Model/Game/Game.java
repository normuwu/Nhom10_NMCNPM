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

        return null;
    }

    public ArrayList<Piece> getSurroundedPieces() {

        return null;
    }

    private boolean floodFill(int row, int col, ArrayList<Piece> group, boolean[][] visited) {

        return false;
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