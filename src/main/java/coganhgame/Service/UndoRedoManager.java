package coganhgame.Service;

import coganhgame.Model.Game.UndoSnapshot;
import java.util.ArrayDeque;
import java.util.Deque;

public class UndoRedoManager {

    private static final int MAX_UNDO = 50;

    private final Deque<UndoSnapshot> undoStack = new ArrayDeque<>();
    private final Deque<UndoSnapshot> redoStack = new ArrayDeque<>();

    public void pushSnapshot(UndoSnapshot snapshot) {
        if (undoStack.size() >= MAX_UNDO) {
            undoStack.removeFirst();
        }

        undoStack.addLast(snapshot);
        redoStack.clear();
    }

    public UndoSnapshot popUndo() {
        if (undoStack.isEmpty()) {
            return null;
        }

        return undoStack.removeLast();
    }

    public boolean canUndo() {
        return !undoStack.isEmpty();
    }
}
