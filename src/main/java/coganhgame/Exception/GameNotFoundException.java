package coganhgame.Exception;

public class GameNotFoundException extends Exception{
    public GameNotFoundException() {
        super("No saved game found!");
    }
}

public static Game loadGame() throws GameNotFoundException {
    Game game = null;
    try {
        File file = new File("game_state.txt");
        // UC 07: Kích hoạt lỗi nếu không có file
        if (!file.exists() || file.length() == 0) {
            throw new GameNotFoundException();
        }
        // UC 06: Đọc và ép kiểu dữ liệu nhị phân về lại object Game
        FileInputStream fis = new FileInputStream(file);
        ObjectInputStream ois = new ObjectInputStream(fis);
        game = (Game) ois.readObject();
        ois.close();
        fis.close();
    } catch (IOException | ClassNotFoundException ex) {
        ViewUtilities.showAlert("Error", "Error loading game", ex.getMessage());
    }
    return game;
}

