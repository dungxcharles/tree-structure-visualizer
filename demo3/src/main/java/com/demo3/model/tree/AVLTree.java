package com.demo3.model.tree;

import com.demo3.model.node.AVLNode;

public class AVLTree extends BinarySearchTree<AVLNode> {

    private boolean inserted;
    private boolean deleted;

    @Override
    protected AVLNode createNode(int value) {
        return new AVLNode(value);
    }
}