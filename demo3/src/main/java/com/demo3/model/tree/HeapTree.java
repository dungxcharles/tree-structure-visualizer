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

    @Override
    public boolean delete(int value) {
        int index = findIndex(value);
        if (index < 0) {
            return false;
        }

        int lastIndex = heap.size() - 1;
        swapValues(index, lastIndex);
        heap.remove(lastIndex);

        if (index < heap.size()) {
            fixHeapAt(index);
        }

        rebuildLinks();
        return true;
    }

    @Override
    public boolean search(int value) {
        return findIndex(value) >= 0;
    }

    private int findIndex(int value) {
        for (int i = 0; i < heap.size(); i++) {
            if (heap.get(i).getValue() == value) {
                return i;
            }
        }
        return -1;
    }

    private void fixHeapAt(int index) {
        if (index > 0 && heap.get(index).getValue() > heap.get(parentIndex(index)).getValue()) {
            heapifyUp(index);
        } else {
            heapifyDown(index);
        }
    }

    private void heapifyDown(int index) {
        while (true) {
            int leftIndex = leftIndex(index);
            int rightIndex = rightIndex(index);
            int largest = index;

            if (leftIndex < heap.size()
                    && heap.get(leftIndex).getValue() > heap.get(largest).getValue()) {
                largest = leftIndex;
            }
            if (rightIndex < heap.size()
                    && heap.get(rightIndex).getValue() > heap.get(largest).getValue()) {
                largest = rightIndex;
            }
            if (largest == index) {
                return;
            }

            swapValues(index, largest);
            index = largest;
        }
    }
}