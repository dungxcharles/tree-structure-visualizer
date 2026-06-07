package com.model.exception;

/**
 * Exception thrown when attempting to insert a node with a parent that is null
 * (not found) when the tree is non-empty.
 */
public class NullParentException extends IllegalArgumentException {
    public NullParentException(String message) {
        super(message);
    }
}
