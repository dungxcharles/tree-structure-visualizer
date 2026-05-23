package com.model.tree;

import com.model.node.Node;

import java.util.List;

public abstract class AbstractTree<N extends Node> {

    protected N root;

    public AbstractTree() {
        this.root = null;
    }

    public N getRoot() {
        return this.root;
    }

    public boolean isEmpty() {
        return root == null;
    }

    // setRoot() : have to check if neccessary

    public void clear() {
        this.root = null;
    }

    public abstract void create(int value);

    public abstract boolean insert(int parentValue, int value);

    public abstract boolean delete(int value);

    public abstract List<Integer> traverse(TraversalType type);

    public abstract boolean search(int value);

    public abstract int getHeight();

    public abstract int getNumberOfNodes();


}
