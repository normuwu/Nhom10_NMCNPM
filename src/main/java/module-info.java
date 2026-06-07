module hust.hedspi.coganhgame {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;

    opens coganhgame to javafx.fxml;
    exports coganhgame;
    exports coganhgame.Controller;
    exports coganhgame.Model;
    exports coganhgame.Model.Game;
    exports coganhgame.Model.Player;
    exports coganhgame.Model.Tile;
    exports coganhgame.Model.Move;
    exports coganhgame.Exception;
    exports coganhgame.Model.Settings;
    opens coganhgame.Controller to javafx.fxml;
    exports coganhgame.Utilities;
    opens coganhgame.Utilities to javafx.fxml;
    exports coganhgame.Service;
}