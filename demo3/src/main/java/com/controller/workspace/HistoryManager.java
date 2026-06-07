package com.controller.workspace;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Manages the history of user operations for Undo/Redo functionality.
 * It uses two stacks to track the active timeline and the undone actions.
 */
public class HistoryManager {
    
    private final Stack<HistoryOperation> historyStack;
    private final Stack<HistoryOperation> redoStack;

    public HistoryManager() {
        this.historyStack = new Stack<>();
        this.redoStack = new Stack<>();
    }

    /**
     * Records a new operation. This clears the redo stack since a new timeline has started.
     */
    public void addOperation(HistoryOperation op) {
        historyStack.push(op);
        redoStack.clear();
    }

    public boolean canUndo() {
        return !historyStack.isEmpty();
    }

    public boolean canRedo() {
        return !redoStack.isEmpty();
    }

    /**
     * Moves the last operation from the history stack to the redo stack.
     */
    public void undo() {
        if (canUndo()) {
            redoStack.push(historyStack.pop());
        }
    }

    /**
     * Moves the last undone operation back to the history stack.
     */
    public void redo() {
        if (canRedo()) {
            historyStack.push(redoStack.pop());
        }
    }

    /**
     * Returns the complete list of active operations in chronological order.
     * This is used to rebuild the tree from scratch.
     */
    public List<HistoryOperation> getActiveHistory() {
        return new ArrayList<>(historyStack);
    }
    
    public void clear() {
        historyStack.clear();
        redoStack.clear();
    }
}
