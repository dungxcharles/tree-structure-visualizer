package com.demo3.model.tree;

import com.demo3.model.node.AVLNode;

public class AVLTree extends BinarySearchTree<AVLNode> {

    private boolean inserted;
    private boolean deleted;

    @Override
    protected AVLNode createNode(int value) {
        return new AVLNode(value);
    }

    @Override
    public boolean insert(int value) {
        if (search(value)) {
            return false;
        }

        inserted = false;
        this.root = insertRec(this.root, value);
        return inserted;
    }

    private AVLNode insertRec(AVLNode node, int value) {
        if (node == null) {
            inserted = true;
            return createNode(value);
        }

        if (value < node.getValue()) {
            node.setLeft(insertRec(node.getLeft(), value));
        } else if (value > node.getValue()) {
            node.setRight(insertRec(node.getRight(), value));
        }

        return rebalance(node);
    }
}