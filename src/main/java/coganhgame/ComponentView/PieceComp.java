package coganhgame.ComponentView;

import coganhgame.Utilities.ViewUtilities;
import javafx.scene.Cursor;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;

import static coganhgame.Utilities.AdaptiveUtilities.PIECE_SIZE;
import static coganhgame.Utilities.AdaptiveUtilities.TILE_SIZE;

public class PieceComp extends StackPane {
    private boolean side; // true: red, false: blue
    private double oldX, oldY;
    private final Ellipse ellipse;
    private final DropShadow highlightEffect = new DropShadow(PIECE_SIZE, Color.YELLOW);

    private static final double PIECE_STROKE_WIDTH = PIECE_SIZE * 0.08;

    public PieceComp(boolean side, int row, int col) {
        this.side = side;
        move(row, col);

        // make a black background (shadow effect for 3D look)
        Ellipse background = new Ellipse(PIECE_SIZE, PIECE_SIZE * 0.832);
        background.setFill(Color.BLACK);
        background.setStroke(Color.BLACK);
        background.setStrokeWidth(PIECE_STROKE_WIDTH);
        background.setTranslateX((TILE_SIZE - PIECE_SIZE * 2) / 2);
        background.setTranslateY((TILE_SIZE - PIECE_SIZE * 0.832 * 2) / 2 + PIECE_SIZE * 0.18);

        // make a red or blue piece
        ellipse = new Ellipse(PIECE_SIZE, PIECE_SIZE * 0.832);
        ellipse.setFill(side ? ViewUtilities.RED_PIECE_COLOR : ViewUtilities.BUE_PIECE_COLOR);
        ellipse.setStroke(Color.BLACK);
        ellipse.setStrokeWidth(PIECE_STROKE_WIDTH);
        ellipse.setTranslateX((TILE_SIZE - PIECE_SIZE * 2) / 2);
        ellipse.setTranslateY((TILE_SIZE - PIECE_SIZE * 0.832 * 2) / 2);
        ellipse.setCursor(Cursor.HAND);

        getChildren().addAll(background, ellipse);
    }

    public double getOldY() {
        return oldY;
    }

    public double getOldX() {
        return oldX;
    }

    public boolean getSide() {
        return side;
    }

    public Ellipse getEllipse() {
        return ellipse;
    }

    public void move(int row, int col) {
        oldY = row * TILE_SIZE;
        oldX = col * TILE_SIZE;
        relocate(oldX, oldY);
    }

    public void flipSide() {
        ellipse.setFill(ellipse.getFill() == ViewUtilities.RED_PIECE_COLOR ? ViewUtilities.BUE_PIECE_COLOR : ViewUtilities.RED_PIECE_COLOR);
        side = !side;
    }

    public void abortMove() {
        relocate(oldX, oldY);
    }

    public void setDisablePiece() {
        ellipse.setDisable(true);
    }

    public void setEnablePiece() {
        ellipse.setDisable(false);
    }

    public void highlightOpen() {
        highlightEffect.setColor(!side ? ViewUtilities.RED_PIECE_COLOR : ViewUtilities.BUE_PIECE_COLOR);
        ellipse.setEffect(highlightEffect);
    }

    public void removeHighlightOpen() {
        ellipse.setEffect(null);
    }
}