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

    @Override
    public boolean delete(int value) {
        deleted = false;
        this.root = deleteRec(this.root, value);
        return deleted;
    }

    @Override
    protected AVLNode deleteRec(AVLNode node, int value) {
        if (node == null) {
            return null;
        }

        if (value < node.getValue()) {
            node.setLeft(deleteRec(node.getLeft(), value));
        } else if (value > node.getValue()) {
            node.setRight(deleteRec(node.getRight(), value));
        } else {
            deleted = true;

            if (node.getLeft() == null) {
                return node.getRight();
            }
            if (node.getRight() == null) {
                return node.getLeft();
            }

            AVLNode successor = minimum(node.getRight());
            node.setValue(successor.getValue());
            node.setRight(deleteRec(node.getRight(), successor.getValue()));
        }

        return rebalance(node);
    }

    @Override
    public boolean update(int currentValue, int newValue) {
        if (currentValue == newValue) {
            return search(currentValue);
        }
        if (!search(currentValue) || search(newValue)) {
            return false;
        }

        delete(currentValue);
        return insert(newValue);
    }

    public int getBalanceFactor(AVLNode node) {
        if (node == null) {
            return 0;
        }
        return height(node.getLeft()) - height(node.getRight());
    }

}