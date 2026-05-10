package coganhgame.ComponentView;

import javafx.scene.shape.Ellipse;

public class PieceComp {

    private boolean side;
    private final Ellipse ellipse;

    public PieceComp(boolean side, int row, int col) {
        this.side = side;
        ellipse = null;
    }

    public void setEnablePiece() {
        ellipse.setDisable(false);
    }

    public boolean getSide() {
        return side;
    }

    public void setDisablePiece() {
        ellipse.setDisable(true);
    }
}
