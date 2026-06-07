package com.controller.workspace;

import com.model.tree.AbstractTree;

/**
 * A simple data class representing a single operation performed by the user.
 * Used for Event Sourcing in the Undo/Redo functionality.
 */
public class HistoryOperation {
    
    public enum Type {
        CREATE,
        INSERT,
        DELETE
    }

    private final Type type;
    private final int parentValue;
    private final int value;

    public HistoryOperation(Type type, int parentValue, int value) {
        this.type = type;
        this.parentValue = parentValue;
        this.value = value;
    }

    public Type getType() {
        return type;
    }

    public int getParentValue() {
        return parentValue;
    }

    public int getValue() {
        return value;
    }

    /**
     * Executes this operation on the given logical tree.
     * Encapsulates the execution behavior to satisfy the Open/Closed Principle.
     */
    public void apply(AbstractTree<?> tree) {
        if (tree == null) return;
        switch (type) {
            case CREATE:
                tree.create(value);
                break;
            case INSERT:
                tree.insert(parentValue, value);
                break;
            case DELETE:
                tree.delete(value);
                break;
        }
    }
}
