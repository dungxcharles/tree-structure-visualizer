package com.demo3.model.tree;

import com.demo3.model.node.BinaryNode;

import java.util.ArrayList;
import java.util.List;

public class HeapTree extends AbstractTree<BinaryNode> {

    private final List<BinaryNode> heap;

    public HeapTree() {
        this.heap = new ArrayList<>();
    }

    @Override
    public void create(int value) {
        if (!isEmpty()) {
            return;
        }

        heap.add(new BinaryNode(value));
        rebuildLinks();
    }

    @Override
    public int getNumberOfNodes() {
        return heap.size();
    }

    @Override
    public void clear() {
        heap.clear();
        this.root = null;
    }

    private int parentIndex(int index) {
        return (index - 1) / 2;
    }

    private int leftIndex(int index) {
        return 2 * index + 1;
    }

    private int rightIndex(int index) {
        return 2 * index + 2;
    }
}