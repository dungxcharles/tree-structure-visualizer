package com.model.step;

import com.model.node.Node;

public final class TreeSnapshot<N extends Node> {

    private final N root;
    private final int height;
    private final int numberOfNodes;

    public TreeSnapshot(N root, int height, int numberOfNodes) {
        this.root = root;
        this.height = height;
        this.numberOfNodes = numberOfNodes;
    }

    public N getRoot() {
        return root;
    }

    public boolean isEmpty() {
        return root == null;
    }

    public int getHeight() {
        return height;
    }

    public int getNumberOfNodes() {
        return numberOfNodes;
    }
}
