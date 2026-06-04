package coganhgame.Service;

public class UndoRedoManager {


  
package coganhgame.Service;

import coganhgame.Model.Game.UndoSnapshot;
import java.util.ArrayDeque;
import java.util.Deque;

public class UndoRedoManager {

    private static final int MAX_UNDO = 50;

    private final Deque<UndoSnapshot> undoStack = new ArrayDeque<>();
    private final Deque<UndoSnapshot> redoStack = new ArrayDeque<>();

}

  
}




