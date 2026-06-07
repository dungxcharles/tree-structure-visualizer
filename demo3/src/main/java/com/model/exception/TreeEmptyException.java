package com.model.exception;

/**
 * Exception thrown when attempting to traverse a tree that has no nodes.
 */
public class TreeEmptyException extends IllegalStateException {
    public TreeEmptyException(String message) {
        super(message);
    }
}
