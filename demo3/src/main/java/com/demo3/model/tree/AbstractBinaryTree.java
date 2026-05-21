package com.demo3.model.tree;

import com.demo3.model.node.BinaryNode;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public abstract class AbstractBinaryTree<N extends BinaryNode> extends AbstractTree<N> {

    @Override
    public int getHeight() {
        return getHeightRec(this.root);
    }

    @Override
    public int getNumberOfNodes() {
        return countNodes(this.root);
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
    protected int getHeightRec(N node) {
        if (node == null) {
            return 0;
        }
        return 1 + Math.max(getHeightRec((N) node.getLeft()), getHeightRec((N) node.getRight()));
    }

    @SuppressWarnings("unchecked")
    protected int countNodes(N node) {
        if (node == null) {
            return 0;
        }
        return 1 + countNodes((N) node.getLeft()) + countNodes((N) node.getRight());
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
