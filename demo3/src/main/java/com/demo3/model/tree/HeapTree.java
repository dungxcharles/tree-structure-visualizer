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

    private void rebuildLinks() {
        for (int i = 0; i < heap.size(); i++) {
            BinaryNode node = heap.get(i);
            int leftIndex = leftIndex(i);
            int rightIndex = rightIndex(i);

            node.setLeft(leftIndex < heap.size() ? heap.get(leftIndex) : null);
            node.setRight(rightIndex < heap.size() ? heap.get(rightIndex) : null);
        }

        this.root = heap.isEmpty() ? null : heap.get(0);
    }

    @Override
    public boolean insert(int parentValue, int value) {
        return insert(value);
    }

    public boolean insert(int value) {
        heap.add(new BinaryNode(value));
        heapifyUp(heap.size() - 1);
        rebuildLinks();
        return true;
    }

    private void heapifyUp(int index) {
        while (index > 0) {
            int parentIndex = parentIndex(index);
            if (heap.get(parentIndex).getValue() >= heap.get(index).getValue()) {
                break;
            }

            swapValues(parentIndex, index);
            index = parentIndex;
        }
    }

    private void swapValues(int firstIndex, int secondIndex) {
        int temp = heap.get(firstIndex).getValue();
        heap.get(firstIndex).setValue(heap.get(secondIndex).getValue());
        heap.get(secondIndex).setValue(temp);
    }
}