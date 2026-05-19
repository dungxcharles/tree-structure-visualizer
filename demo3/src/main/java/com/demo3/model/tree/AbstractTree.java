package com.demo3.model.tree;

import com.demo3.model.node.Node;

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

}
