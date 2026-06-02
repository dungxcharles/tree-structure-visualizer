package com.demo3.model.tree;

import com.demo3.model.node.AVLNode;
import com.demo3.model.node.BinaryNode;
import com.demo3.model.pseudocode.PseudocodeTemplate;
import com.demo3.model.step.TreeOperation;
import com.demo3.model.pseudocode.PseudocodeRepository;

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
        if (search(value)) {
            return false;
        }

        nodeInserted = false;
        this.root = insertRec(getRoot(), value);
        return nodeInserted;
    }

    private AVLNode insertRec(AVLNode node, int value) {
        if (node == null) {
            nodeInserted = true;
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
        nodeDeleted = false;
        this.root = deleteRec(getRoot(), value);
        return nodeDeleted;
    }

    private AVLNode deleteRec(AVLNode node, int value) {
        if (node == null) {
            return null;
        }

        if (value < node.getValue()) {
            node.setLeft(deleteRec(node.getLeft(), value));
        } else if (value > node.getValue()) {
            node.setRight(deleteRec(node.getRight(), value));
        } else {
            nodeDeleted = true;

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

    private AVLNode minimum(AVLNode node) {
        while (node.getLeft() != null) {
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

        if (balance > 1) {
            if (getBalanceFactor(node.getLeft()) < 0) {
                node.setLeft(leftRotate(node.getLeft()));
            }
            return rightRotate(node);
        }

        if (balance < -1) {
            if (getBalanceFactor(node.getRight()) > 0) {
                node.setRight(rightRotate(node.getRight()));
            }
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

    @Override
    protected AVLNode cloneSubtree(BinaryNode node) {
        if (node == null) {
            return null;
        }

        AVLNode avlNode = (AVLNode) node;
        AVLNode copy = new AVLNode(avlNode.getValue());
        copy.setStoredHeight(avlNode.getStoredHeight());
        copy.setLeft(cloneSubtree(avlNode.getLeft()));
        copy.setRight(cloneSubtree(avlNode.getRight()));
        return copy;
    }

    @Override
    protected PseudocodeTemplate getPseudocodeTemplate(TreeOperation operation, TraversalType traversalType) {
        if (operation == TreeOperation.INSERT) {
            return PseudocodeRepository.getAVLInsert();
        }
        if (operation == TreeOperation.DELETE) {
            return PseudocodeRepository.getAVLDelete();
        }
        if (operation == TreeOperation.SEARCH) {
            return PseudocodeRepository.getAVLSearch();
        }
        return super.getPseudocodeTemplate(operation, traversalType);
    }
}
