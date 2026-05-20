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
}