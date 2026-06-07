package com.model.tree;

/**
 * Exception thrown when a parent value is specified for operations that do not accept a parent.
 */
public class InvalidParentInputException extends IllegalArgumentException {
    public InvalidParentInputException(String message) {
        super(message);
    }
}
