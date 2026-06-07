package com.controller.workspace;

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
}
