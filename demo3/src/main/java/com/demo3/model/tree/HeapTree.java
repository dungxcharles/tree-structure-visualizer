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
    public boolean insert(int parentValue, int value) {
        return insert(value);
    }

    public boolean insert(int value) {
        heap.add(new BinaryNode(value));
        heapifyUp(heap.size() - 1);
        rebuildLinks();
        return true;
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
    public boolean update(int currentValue, int newValue) {
        int index = findIndex(currentValue);
        if (index < 0) {
            return false;
        }

        int oldValue = heap.get(index).getValue();
        heap.get(index).setValue(newValue);

        if (newValue > oldValue) {
            heapifyUp(index);
        } else if (newValue < oldValue) {
            heapifyDown(index);
        }

        rebuildLinks();
        return true;
    }

    @Override
    public List<Integer> traverse(TraversalType type) {
        if (type == null) {
            throw new IllegalArgumentException("Traversal type cannot be null.");
        }

        List<Integer> result = new ArrayList<>();
        switch (type) {
            case BFS:
                for (BinaryNode node : heap) {
                    result.add(node.getValue());
                }
                break;
            case IN_ORDER:
                inOrderRec(this.root, result);
                break;
            case PRE_ORDER:
                preOrderRec(this.root, result);
                break;
            case POST_ORDER:
                postOrderRec(this.root, result);
                break;
        }
        return result;
    }

    @Override
    public boolean search(int value) {
        return findIndex(value) >= 0;
    }

    @Override
    public int getHeight() {
        return getHeightRec(this.root);
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

    private int findIndex(int value) {
        for (int i = 0; i < heap.size(); i++) {
            if (heap.get(i).getValue() == value) {
                return i;
            }
        }
        return -1;
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

    private void swapValues(int firstIndex, int secondIndex) {
        int temp = heap.get(firstIndex).getValue();
        heap.get(firstIndex).setValue(heap.get(secondIndex).getValue());
        heap.get(secondIndex).setValue(temp);
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

    private int getHeightRec(BinaryNode node) {
        if (node == null) {
            return 0;
        }
        return 1 + Math.max(getHeightRec(node.getLeft()), getHeightRec(node.getRight()));
    }

    private void inOrderRec(BinaryNode node, List<Integer> result) {
        if (node == null) {
            return;
        }

        inOrderRec(node.getLeft(), result);
        result.add(node.getValue());
        inOrderRec(node.getRight(), result);
    }

    private void preOrderRec(BinaryNode node, List<Integer> result) {
        if (node == null) {
            return;
        }

        result.add(node.getValue());
        preOrderRec(node.getLeft(), result);
        preOrderRec(node.getRight(), result);
    }

    private void postOrderRec(BinaryNode node, List<Integer> result) {
        if (node == null) {
            return;
        }

        postOrderRec(node.getLeft(), result);
        postOrderRec(node.getRight(), result);
        result.add(node.getValue());
    }
}
	 