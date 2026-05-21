package com.demo3.model.tree;

import com.demo3.model.node.BinaryNode;

public class BinarySearchTree extends AbstractBinarySearchTree<BinaryNode> {

    @Override
    protected BinaryNode createNode(int value) {
        return new BinaryNode(value);
    }
}
