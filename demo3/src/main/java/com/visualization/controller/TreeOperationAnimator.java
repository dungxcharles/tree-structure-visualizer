package com.visualization.controller;

import com.model.node.Node;

/**
 * Interface defining the necessary methods for animating tree operations.
 * Applying Abstraction and SOLID principles (Interface Segregation).
 */
public interface TreeOperationAnimator {
    
    void animateInsert(Node node);
    
    void animateRemove(Node node);
    
    void animateHighlight(Node node);
    
    void animateSearch(Node node);
    
    void animateRotate(Node node);
}
