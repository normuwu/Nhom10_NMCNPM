package coganhgame.Model.Move;

import coganhgame.Model.Piece;

import java.util.ArrayList;

public record MoveResult(boolean isValidMove, ArrayList<Piece> capturedPieces) {
}
