package com.demo3.model.tree;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import com.demo3.model.node.BinaryNode;


public abstract class BinarySearchTree<N extends BinaryNode> extends AbstractTree<N> {

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

    @Override
    public int getHeight() {
        return getHeightRec(this.root);
    }

    @SuppressWarnings("unchecked")
    protected int getHeightRec(N node) {
        if (node == null) {
            return 0;
        }

        return 1 + Math.max(getHeightRec((N) node.getLeft()), getHeightRec((N) node.getRight()));
    }

    @Override
    public int getNumberOfNodes() {
        return countNodes(this.root);
    }

    @SuppressWarnings("unchecked")
    protected int countNodes(N node) {
        if (node == null) {
            return 0;
        }

        return 1 + countNodes((N) node.getLeft()) + countNodes((N) node.getRight());
    }

    @Override
    public List<Integer> traverse(TraversalType type) {
        if (type == null) {
            throw new IllegalArgumentException("Traversal type cannot be null.");
        }

        List<Integer> result = new ArrayList<>();
        if (isEmpty()) {
            return result;
        }

        switch (type) {
            case IN_ORDER:
                inOrderRec(this.root, result);
                break;
            case PRE_ORDER:
                preOrderRec(this.root, result);
                break;
            case POST_ORDER:
                postOrderRec(this.root, result);
                break;
            case BFS:
                bfsTraverse(this.root, result);
                break;
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    protected void inOrderRec(N node, List<Integer> result) {
        if (node == null) {
            return;
        }

        inOrderRec((N) node.getLeft(), result);
        result.add(node.getValue());
        inOrderRec((N) node.getRight(), result);
    }

    @SuppressWarnings("unchecked")
    protected void preOrderRec(N node, List<Integer> result) {
        if (node == null) {
            return;
        }

        result.add(node.getValue());
        preOrderRec((N) node.getLeft(), result);
        preOrderRec((N) node.getRight(), result);
    }

    @SuppressWarnings("unchecked")
    protected void postOrderRec(N node, List<Integer> result) {
        if (node == null) {
            return;
        }

        postOrderRec((N) node.getLeft(), result);
        postOrderRec((N) node.getRight(), result);
        result.add(node.getValue());
    }

    @SuppressWarnings("unchecked")
    protected void bfsTraverse(N root, List<Integer> result) {
        Queue<N> queue = new ArrayDeque<>();
        queue.add(root);

        while (!queue.isEmpty()) {
            N current = queue.poll();
            result.add(current.getValue());

            if (current.getLeft() != null) {
                queue.add((N) current.getLeft());
            }
            if (current.getRight() != null) {
                queue.add((N) current.getRight());
            }
        }
    }
}