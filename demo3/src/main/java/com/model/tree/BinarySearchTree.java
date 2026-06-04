package com.model.tree;

import com.model.node.BinaryNode;
import com.model.step.StepType;

public class BinarySearchTree extends BinaryTree {

    protected BinaryNode createNode(int value) {
        return new BinaryNode(value);
    }


    @Override
    public void create(int value) {
        if (!isEmpty()) {
            return;
        }
        fireStep(StepType.INSERT_NODE, value, "Tạo gốc (root) với giá trị " + value);
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

    protected BinaryNode insertRec(BinaryNode current, BinaryNode newNode) {
        if (current == null) {
            fireStep(StepType.INSERT_NODE, newNode.getValue(), "Chèn node " + newNode.getValue());
            return newNode;
        }

        fireStep(StepType.COMPARE, current.getValue(), "So sánh " + newNode.getValue() + " với " + current.getValue());
        if (newNode.getValue() < current.getValue()) {
            fireStep(StepType.GO_LEFT, current.getValue(), newNode.getValue() + " < " + current.getValue() + " -> Đi trái");
            current.setLeft(insertRec(current.getLeft(), newNode));
        } else if (newNode.getValue() > current.getValue()) {
            fireStep(StepType.GO_RIGHT, current.getValue(), newNode.getValue() + " > " + current.getValue() + " -> Đi phải");
            current.setRight(insertRec(current.getRight(), newNode));
        }

        return current;
    }

    @Override
    public boolean delete(int value) {
        if (!search(value)) {
            return false;
        }

        this.root = deleteRec(this.root, value);
        return true;
    }

    protected BinaryNode deleteRec(BinaryNode current, int value) {
        if (current == null) {
            return null;
        }

        fireStep(StepType.COMPARE, current.getValue(), "So sánh " + value + " với " + current.getValue());
        if (value < current.getValue()) {
            fireStep(StepType.GO_LEFT, current.getValue(), value + " < " + current.getValue() + " -> Đi trái");
            current.setLeft(deleteRec(current.getLeft(), value));
            return current;
        }
        if (value > current.getValue()) {
            fireStep(StepType.GO_RIGHT, current.getValue(), value + " > " + current.getValue() + " -> Đi phải");
            current.setRight(deleteRec(current.getRight(), value));
            return current;
        }

        fireStep(StepType.FOUND, current.getValue(), "Tìm thấy " + value + " để xóa");
        if (current.getLeft() == null) {
            fireStep(StepType.DELETE_NODE, value, "Xóa node (không có con trái)");
            return current.getRight();
        }
        if (current.getRight() == null) {
            fireStep(StepType.DELETE_NODE, value, "Xóa node (không có con phải)");
            return current.getLeft();
        }

        BinaryNode successor = minimum(current.getRight());
        fireStep(StepType.REPLACE_VALUE, current.getValue(), "Thay thế " + current.getValue() + " bằng successor " + successor.getValue());
        current.setValue(successor.getValue());
        current.setRight(deleteMinimum(current.getRight()));
        return current;
    }

    protected BinaryNode deleteMinimum(BinaryNode current) {
        if (current.getLeft() == null) {
            fireStep(StepType.DELETE_NODE, current.getValue(), "Xóa successor " + current.getValue());
            return current.getRight();
        }

        fireStep(StepType.GO_LEFT, current.getValue(), "Đi trái tìm min");
        current.setLeft(deleteMinimum(current.getLeft()));
        return current;
    }

    protected BinaryNode minimum(BinaryNode node) {
        while (node.getLeft() != null) {
            node = node.getLeft();
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

    @Override
    protected BinaryNode findNode(BinaryNode current, int value) {
        if (current == null) {
            return null;
        }

        fireStep(StepType.COMPARE, current.getValue(), "So sánh " + value + " với " + current.getValue());
        if (current.getValue() == value) {
            fireStep(StepType.FOUND, current.getValue(), "Tìm thấy " + value);
            return current;
        }

        if (value < current.getValue()) {
            fireStep(StepType.GO_LEFT, current.getValue(), "Đi trái");
            return findNode(current.getLeft(), value);
        }
        fireStep(StepType.GO_RIGHT, current.getValue(), "Đi phải");
        return findNode(current.getRight(), value);
    }

}
