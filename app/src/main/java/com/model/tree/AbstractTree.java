package com.model.tree;

import com.model.node.Node;

import com.model.step.StepType;
import com.model.step.TreeOperationListener;

import java.util.List;

// Base class for all tree implementations in the project.
public abstract class AbstractTree<N extends Node> {

    protected N root;
    protected TreeOperationListener listener = null;

    public AbstractTree() {
        this.root = null;
    }

    public N getRoot() {
        return this.root;
    }

    public void setListener(TreeOperationListener listener) {
        this.listener = listener;
    }

    // Sends one algorithm step to the visualization layer.
    protected void fireStep(StepType type, int nodeValue, String message) {
        if (listener != null) {
            listener.onStep(type, nodeValue, message);
        }
    }

    public boolean isEmpty() {
        return root == null;
    }

    public void clear() {
        this.root = null;
    }

    // Common operations that every tree must support.
    public abstract void create(int value);

    public abstract boolean insert(int parentValue, int value);

    public abstract boolean delete(int value);

    public abstract boolean update(int currentValue, int newValue);

    public abstract List<Integer> traverse(TraversalType type);

    public abstract boolean search(int value);

    public abstract int getHeight();

    public abstract int getNumberOfNodes();

}
