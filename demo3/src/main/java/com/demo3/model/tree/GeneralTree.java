package com.demo3.model.tree;


import com.demo3.model.node.GenericNode;

import java.util.List;

public class GeneralTree extends AbstractTree<GenericNode> {

    @Override
    public void create(int value) {
        if (!isEmpty()) {
            return;
            // hoặc throw new IllegalArgumentException("Tree is not empty.");
        }
        this.root = new GenericNode(value);
    }

    @Override
    public boolean insert(int parentValue, int childValue) {
        return false;
    }

    @Override
    public boolean delete(int value) {
        return false;
    }

    @Override
    public boolean search(int value) {
        return false;
    }

    @Override
    public int getHeight() {
        return 0;
    }

    @Override
    public int getNumberOfNodes() {
        return 0;
    }

    @Override
    public List<Integer> traverse(TraversalType type) {
        return List.of();
    }
}