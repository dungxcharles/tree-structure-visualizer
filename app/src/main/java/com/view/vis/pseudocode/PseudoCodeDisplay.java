package com.view.vis.pseudocode;

/**
 * Abstraction for a UI component that displays pseudo code steps.
 * Applies OOP Abstraction and Encapsulation principles.
 */
public interface PseudoCodeDisplay {
    void clear();

    void addAndHighlightStep(String message);
}
