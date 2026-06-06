package com.demo3.model.tree;

import com.demo3.model.node.BinaryNode;

import java.util.ArrayList;
import java.util.List;

public class BinarySearchTree extends BinaryTree {

    protected BinaryNode createNode(int value) {
        return new BinaryNode(value);
    }

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

    protected BinaryNode insertRec(BinaryNode current, BinaryNode newNode) {
        if (current == null) {
            return newNode;
        }

        if (newNode.getValue() < current.getValue()) {
            current.setLeft(insertRec(current.getLeft(), newNode));
        } else if (newNode.getValue() > current.getValue()) {
            current.setRight(insertRec(current.getRight(), newNode));
        }

        return current;
    }

    @Override
    public boolean delete(int value) {
        if (!search(value)) {
            return false;
        }

        this.root = deleteRec(this.root, value);
        return true;
    }

    protected BinaryNode deleteRec(BinaryNode current, int value) {
        if (current == null) {
            return null;
        }

        if (value < current.getValue()) {
            current.setLeft(deleteRec(current.getLeft(), value));
            return current;
        }
        if (value > current.getValue()) {
            current.setRight(deleteRec(current.getRight(), value));
            return current;
        }

        if (current.getLeft() == null) {
            return current.getRight();
        }
        if (current.getRight() == null) {
            return current.getLeft();
        }

        BinaryNode successor = minimum(current.getRight());
        current.setValue(successor.getValue());
        current.setRight(deleteMinimum(current.getRight()));
        return current;
    }

    protected BinaryNode deleteMinimum(BinaryNode current) {
        if (current.getLeft() == null) {
            return current.getRight();
        }

        current.setLeft(deleteMinimum(current.getLeft()));
        return current;
    }

    protected BinaryNode minimum(BinaryNode node) {
        while (node.getLeft() != null) {
            node = node.getLeft();
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

    @Override
    protected BinaryNode findNode(BinaryNode current, int value) {
        if (current == null || current.getValue() == value) {
            return current;
        }

        if (value < current.getValue()) {
            return findNode(current.getLeft(), value);
        }
        return findNode(current.getRight(), value);
    }

    @Override
    protected List<Integer> getSearchPath(int value) {
        List<Integer> path = new ArrayList<>();
        BinaryNode current = this.root;

        while (current != null) {
            path.add(current.getValue());
            if (current.getValue() == value) {
                break;
            }
            current = value < current.getValue() ? current.getLeft() : current.getRight();
        }
        return path;
    }

}
