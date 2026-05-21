package com.demo3.model.tree;

import com.demo3.model.node.BinaryNode;

public abstract class AbstractBinarySearchTree<N extends BinaryNode> extends AbstractBinaryTree<N> {

    private boolean deleted;

    protected abstract N createNode(int value);

    @Override
    public void create(int value) {
        if (!isEmpty()) {
            return;
        }
        this.root = createNode(value);
    }

    @Override
    public boolean insert(int parentValue, int value) {
        return insert(value);
    }

    public boolean insert(int value) {
        if (search(value)) {
            return false;
        }

        this.root = insertRec(this.root, createNode(value));
        return true;
    }

    @SuppressWarnings("unchecked")
    protected N insertRec(N current, N newNode) {
        if (current == null) {
            return newNode;
        }

        if (newNode.getValue() < current.getValue()) {
            current.setLeft(insertRec((N) current.getLeft(), newNode));
        } else if (newNode.getValue() > current.getValue()) {
            current.setRight(insertRec((N) current.getRight(), newNode));
        }

        return current;
    }

    @Override
    public boolean delete(int value) {
        deleted = false;
        this.root = deleteRec(this.root, value);
        return deleted;
    }

    @SuppressWarnings("unchecked")
    protected N deleteRec(N current, int value) {
        if (current == null) {
            return null;
        }

        if (value < current.getValue()) {
            current.setLeft(deleteRec((N) current.getLeft(), value));
            return current;
        }
        if (value > current.getValue()) {
            current.setRight(deleteRec((N) current.getRight(), value));
            return current;
        }

        deleted = true;
        if (current.getLeft() == null) {
            return (N) current.getRight();
        }
        if (current.getRight() == null) {
            return (N) current.getLeft();
        }

        N successor = minimum((N) current.getRight());
        current.setValue(successor.getValue());
        current.setRight(deleteMinimum((N) current.getRight()));
        return current;
    }

    @SuppressWarnings("unchecked")
    protected N deleteMinimum(N current) {
        if (current.getLeft() == null) {
            return (N) current.getRight();
        }

        current.setLeft(deleteMinimum((N) current.getLeft()));
        return current;
    }

    @SuppressWarnings("unchecked")
    protected N minimum(N node) {
        while (node.getLeft() != null) {
            node = (N) node.getLeft();
        }
        return node;
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

    @Override
    public boolean search(int value) {
        return findNode(this.root, value) != null;
    }

    @SuppressWarnings("unchecked")
    protected N findNode(N current, int value) {
        if (current == null || current.getValue() == value) {
            return current;
        }

        if (value < current.getValue()) {
            return findNode((N) current.getLeft(), value);
        }
        return findNode((N) current.getRight(), value);
    }
}
