package com.model.tree;

import com.model.node.AVLNode;
import com.model.node.BinaryNode;
import com.model.step.StepType;

import java.util.List;

public class AVLTree extends BinarySearchTree {

    private boolean nodeInserted;
    private boolean nodeDeleted;

    @Override
    protected AVLNode createNode(int value) {
        return new AVLNode(value);
    }

    @Override
    public AVLNode getRoot() {
        return (AVLNode) this.root;
    }

    @Override
    public boolean insert(int value) {

        nodeInserted = false;
        this.root = insertRec(getRoot(), value);
        return nodeInserted;
    }

    private AVLNode insertRec(AVLNode node, int value) {
        if (node == null) {
            fireStep(StepType.INSERT_NODE, value, "Insert node " + value);
            nodeInserted = true;
            return createNode(value);
        }

        fireStep(StepType.COMPARE, node.getValue(), "Compare " + value + " with " + node.getValue());
        if (value == node.getValue()) {
            fireStep(StepType.FOUND, node.getValue(), "Node " + node.getValue() + " has already been inserted!");
            return node;
        } else if (value < node.getValue()) {
            fireStep(StepType.GO_LEFT, node.getValue(), value + " < " + node.getValue() + " -> Go left");
            node.setLeft(insertRec(node.getLeft(), value));
        } else if (value > node.getValue()) {
            fireStep(StepType.GO_RIGHT, node.getValue(), value + " > " + node.getValue() + " -> Go right");
            node.setRight(insertRec(node.getRight(), value));
        }

        return rebalance(node);
    }

    @Override
    public boolean delete(int value) {
        nodeDeleted = false;
        this.root = deleteRec(getRoot(), value);
        return nodeDeleted;
    }

    private AVLNode deleteRec(AVLNode node, int value) {
        if (node == null) {
            return null;
        }

        fireStep(StepType.COMPARE, node.getValue(), "Compare " + value + " with " + node.getValue());
        if (value < node.getValue()) {
            fireStep(StepType.GO_LEFT, node.getValue(), value + " < " + node.getValue() + " -> Go left");
            node.setLeft(deleteRec(node.getLeft(), value));
        } else if (value > node.getValue()) {
            fireStep(StepType.GO_RIGHT, node.getValue(), value + " > " + node.getValue() + " -> Go right");
            node.setRight(deleteRec(node.getRight(), value));
        } else {
            fireStep(StepType.FOUND, node.getValue(), "Found " + value + " to delete");
            nodeDeleted = true;

            if (node.getLeft() == null) {
                fireStep(StepType.DELETE_NODE, value, "Delete node (no left child)");
                return node.getRight();
            }
            if (node.getRight() == null) {
                fireStep(StepType.DELETE_NODE, value, "Delete node (no right child)");
                return node.getLeft();
            }

            AVLNode successor = minimum(node.getRight());
            fireStep(StepType.REPLACE_VALUE, node.getValue(), "Replace " + node.getValue() + " with successor " + successor.getValue());
            node.setValue(successor.getValue());
            node.setRight(deleteRec(node.getRight(), successor.getValue()));
        }

        return rebalance(node);
    }

    @Override
    public boolean update(int currentValue, int newValue) {
        com.model.step.TreeOperationListener temp = this.listener;
        this.listener = null;
        boolean existsCurrent = search(currentValue);
        boolean existsNew = search(newValue);
        this.listener = temp;

        if (currentValue == newValue) {
            if (existsCurrent) {
                fireStep(StepType.FOUND, currentValue, "Node " + currentValue + " does not need to change");
                return true;
            }
            fireStep(StepType.NOT_FOUND, currentValue, "Node " + currentValue + " not found");
            return false;
        }
        if (!existsCurrent) {
            fireStep(StepType.NOT_FOUND, currentValue, "Node " + currentValue + " not found to update");
            return false;
        }
        if (existsNew) {
            fireStep(StepType.FOUND, newValue, "New value " + newValue + " already exists in the tree");
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

    private AVLNode minimum(AVLNode node) {
        while (node.getLeft() != null) {
            fireStep(StepType.GO_LEFT, node.getValue(), "Go left to find min");
            node = node.getLeft();
        }
        return node;
    }

    private void updateHeight(AVLNode node) {
        node.setStoredHeight(1 + Math.max(height(node.getLeft()), height(node.getRight())));
    }

    private int height(AVLNode node) {
        if (node == null) {
            return 0;
        }
        return node.getStoredHeight();
    }

    private AVLNode rebalance(AVLNode node) {
        updateHeight(node);
        int balance = getBalanceFactor(node);
        fireStep(StepType.CHECK_BALANCE, node.getValue(), "Check balance of " + node.getValue() + " = " + balance);

        if (balance > 1) {
            if (getBalanceFactor(node.getLeft()) < 0) {
                fireStep(StepType.ROTATE_LEFT, node.getLeft().getValue(), "Left-Right imbalance -> Rotate left at " + node.getLeft().getValue());
                node.setLeft(leftRotate(node.getLeft()));
            }
            fireStep(StepType.ROTATE_RIGHT, node.getValue(), "Rotate right at " + node.getValue());
            return rightRotate(node);
        }

        if (balance < -1) {
            if (getBalanceFactor(node.getRight()) > 0) {
                fireStep(StepType.ROTATE_RIGHT, node.getRight().getValue(), "Right-Left imbalance -> Rotate right at " + node.getRight().getValue());
                node.setRight(rightRotate(node.getRight()));
            }
            fireStep(StepType.ROTATE_LEFT, node.getValue(), "Rotate left at " + node.getValue());
            return leftRotate(node);
        }

        return node;
    }

    private AVLNode leftRotate(AVLNode x) {
        AVLNode y = x.getRight();
        AVLNode middle = y.getLeft();

        y.setLeft(x);
        x.setRight(middle);

        updateHeight(x);
        updateHeight(y);
        return y;
    }

    private AVLNode rightRotate(AVLNode y) {
        AVLNode x = y.getLeft();
        AVLNode middle = x.getRight();

        x.setRight(y);
        y.setLeft(middle);

        updateHeight(y);
        updateHeight(x);
        return x;
    }

}
