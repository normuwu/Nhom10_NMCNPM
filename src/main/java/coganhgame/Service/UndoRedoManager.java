package coganhgame.Service;

import coganhgame.Model.Game.UndoSnapshot;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.util.ArrayDeque;
import java.util.Deque;
public class UndoRedoManager {

    private static final int MAX_UNDO = 50;

    private final Deque<UndoSnapshot> undoStack = new ArrayDeque<>();
    private final Deque<UndoSnapshot> redoStack = new ArrayDeque<>();

    private final BooleanProperty canUndoProperty = new SimpleBooleanProperty(false);
    private final BooleanProperty canRedoProperty = new SimpleBooleanProperty(false);

    /**
     * UC20 Main Flow 20.1.1
     * Lưu trạng thái hiện tại trước khi thực hiện nước đi
     */
    public void pushSnapshot(UndoSnapshot snapshot) {

        // UC20 Business Rule
        // Giới hạn tối đa 50 trạng thái Undo
        if (undoStack.size() >= MAX_UNDO) {
            undoStack.removeFirst();
        }

        // UC20 Main Flow 20.1.1
        // Đưa trạng thái hiện tại vào Undo Stack
        undoStack.addLast(snapshot);

        // UC20 Business Rule
        // Khi có nước đi mới thì xóa toàn bộ Redo Stack
        redoStack.clear();

        updateProperties();
    }

    /**
     * UC20 Main Flow 20.1.2
     * Lấy trạng thái gần nhất từ Undo Stack
     */
    public UndoSnapshot popUndo() {

        // UC20 Alternative Flow 20.2.1
        // Không có trạng thái để Undo
        if (undoStack.isEmpty()) {
            return null;
        }

        // UC20 Main Flow 20.1.2
        // Lấy snapshot gần nhất
        UndoSnapshot snapshot = undoStack.removeLast();

        updateProperties();

        return snapshot;
    }

    /**
     * UC21 Main Flow 21.1.2
     * Lấy trạng thái gần nhất từ Redo Stack
     */
    public UndoSnapshot popRedo() {

        // UC21 Alternative Flow 21.2.1
        // Không có trạng thái để Redo
        if (redoStack.isEmpty()) {
            return null;
        }

        // UC21 Main Flow 21.1.2
        // Lấy snapshot gần nhất
        UndoSnapshot snapshot = redoStack.removeLast();

        updateProperties();

        return snapshot;
    }

    /**
     * UC20 Main Flow 20.1.5
     * Lưu trạng thái hiện tại vào Redo Stack khi Undo
     */
    public void pushRedo(UndoSnapshot snapshot) {

        redoStack.addLast(snapshot);

        updateProperties();
    }

    /**
     * UC21 Main Flow 21.1.4
     * Lưu trạng thái hiện tại vào Undo Stack khi Redo
     */
    public void pushUndo(UndoSnapshot snapshot) {

        // UC21 Business Rule
        // Giới hạn tối đa 50 trạng thái Undo
        if (undoStack.size() >= MAX_UNDO) {
            undoStack.removeFirst();
        }

        undoStack.addLast(snapshot);

        updateProperties();
    }

    /**
     * UC20 Alternative Flow 20.2.1
     * UC21 Alternative Flow 21.2.1
     * Kiểm tra còn trạng thái Undo hay không
     */
    public boolean canUndo() {
        return !undoStack.isEmpty();
    }

    /**
     * UC21 Alternative Flow 21.2.1
     * Kiểm tra còn trạng thái Redo hay không
     */
    public boolean canRedo() {
        return !redoStack.isEmpty();
    }

    public BooleanProperty canUndoProperty() {
        return canUndoProperty;
    }

    public BooleanProperty canRedoProperty() {
        return canRedoProperty;
    }

    /**
     * UC20 - UC21
     * Xóa toàn bộ lịch sử Undo/Redo khi bắt đầu game mới
     */
    public void clear() {

        undoStack.clear();
        redoStack.clear();

        updateProperties();
    }

    /**
     * UC20 - UC21
     * Cập nhật trạng thái Enable/Disable của nút Undo và Redo
     */
    private void updateProperties() {

        canUndoProperty.set(!undoStack.isEmpty());

        canRedoProperty.set(!redoStack.isEmpty());
    }
}
