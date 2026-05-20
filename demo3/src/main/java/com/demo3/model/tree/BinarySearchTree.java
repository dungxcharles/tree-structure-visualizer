package com.demo3.model.tree;

import com.demo3.model.node.BinaryNode;


public abstract class BinarySearchTree<N extends BinaryNode> extends AbstractTree<N> {

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
}