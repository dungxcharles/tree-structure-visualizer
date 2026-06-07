package com.model.exception;

/**
 * Exception thrown when attempting to insert a node with a parent into a tree
 * whose root is null.
 */
public class NullRootException extends IllegalStateException {
    public NullRootException(String message) {
        super(message);
    }
}
