package coganhgame.Utilities;

public class AdaptiveUtilities {
    public static double TILE_SIZE;
    public static double BOARD_WIDTH;
    public static double BOARD_HEIGHT;
    public static double PIECE_SIZE;

    public static void setProperties(double screenHeight) {
        TILE_SIZE = screenHeight / 7.5;
        BOARD_WIDTH = TILE_SIZE * Constants.WIDTH;
        BOARD_HEIGHT = TILE_SIZE * Constants.HEIGHT;
        PIECE_SIZE = TILE_SIZE * 0.22;
    }
}
