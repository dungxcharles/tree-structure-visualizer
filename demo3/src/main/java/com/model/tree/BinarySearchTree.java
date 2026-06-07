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
        int initialSize = getNumberOfNodes();
        this.root = insertRec(this.root, createNode(value));
        return getNumberOfNodes() > initialSize;
    }

    protected BinaryNode insertRec(BinaryNode current, BinaryNode newNode) {
        if (current == null) {
            fireStep(StepType.INSERT_NODE, newNode.getValue(), "Chèn node " + newNode.getValue());
            return newNode;
        }

        fireStep(StepType.COMPARE, current.getValue(), "So sánh " + newNode.getValue() + " với " + current.getValue());
        if (newNode.getValue() == current.getValue()) {
            fireStep(StepType.FOUND, current.getValue(), "Node " + current.getValue() + " đã tồn tại!");
            return current;
        } else if (newNode.getValue() < current.getValue()) {
            if (current.getLeft() == null) {
                fireStep(StepType.GO_LEFT, current.getValue(),
                        newNode.getValue() + " < " + current.getValue() + " -> Đi trái (vị trí trống)");
                current.setLeft(newNode);
                fireStep(StepType.INSERT_NODE, newNode.getValue(), "Chèn node " + newNode.getValue());
                return current;
            }
            fireStep(StepType.GO_LEFT, current.getValue(),
                    newNode.getValue() + " < " + current.getValue() + " -> Đi trái");
            current.setLeft(insertRec(current.getLeft(), newNode));
        } else if (newNode.getValue() > current.getValue()) {
            if (current.getRight() == null) {
                fireStep(StepType.GO_RIGHT, current.getValue(),
                        newNode.getValue() + " > " + current.getValue() + " -> Đi phải (vị trí trống)");
                current.setRight(newNode);
                fireStep(StepType.INSERT_NODE, newNode.getValue(), "Chèn node " + newNode.getValue());
                return current;
            }
            fireStep(StepType.GO_RIGHT, current.getValue(),
                    newNode.getValue() + " > " + current.getValue() + " -> Đi phải");
            current.setRight(insertRec(current.getRight(), newNode));
        }

        return current;
    }

    @Override
    public boolean delete(int value) {
        int initialSize = getNumberOfNodes();
        this.root = deleteRec(this.root, value);
        return getNumberOfNodes() < initialSize;
    }

    protected BinaryNode deleteRec(BinaryNode current, int value) {
        if (current == null) {
            fireStep(StepType.NOT_FOUND, value, "Không tìm thấy node " + value + " để xóa");
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

        fireStep(StepType.GO_RIGHT, current.getValue(), "Tìm successor: Đi nhánh phải");
        BinaryNode successor = findAndAnimateSuccessor(current.getRight());
        fireStep(StepType.REPLACE_VALUE, current.getValue(),
                "Thay thế " + current.getValue() + " bằng successor " + successor.getValue());
        current.setValue(successor.getValue());
        current.setRight(deleteMinimumSilentSearch(current.getRight()));
        return current;
    }

    protected BinaryNode findAndAnimateSuccessor(BinaryNode current) {
        fireStep(StepType.COMPARE, current.getValue(), "Đang xét " + current.getValue());
        if (current.getLeft() == null) {
            fireStep(StepType.FOUND, current.getValue(), "Tìm thấy successor: " + current.getValue());
            return current;
        }
        fireStep(StepType.GO_LEFT, current.getValue(), "Đi trái tìm min");
        return findAndAnimateSuccessor(current.getLeft());
    }

    protected BinaryNode deleteMinimumSilentSearch(BinaryNode current) {
        if (current.getLeft() == null) {
            fireStep(StepType.DELETE_NODE, current.getValue(), "Xóa successor " + current.getValue());
            return current.getRight();
        }
        current.setLeft(deleteMinimumSilentSearch(current.getLeft()));
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
        com.model.step.TreeOperationListener temp = this.listener;
        this.listener = null;
        boolean existsCurrent = search(currentValue);
        boolean existsNew = search(newValue);
        this.listener = temp;

        if (currentValue == newValue) {
            if (existsCurrent) {
                fireStep(StepType.FOUND, currentValue, "Node " + currentValue + " không cần đổi");
                return true;
            }
            fireStep(StepType.NOT_FOUND, currentValue, "Không tìm thấy node " + currentValue);
            return false;
        }
        if (!existsCurrent) {
            fireStep(StepType.NOT_FOUND, currentValue, "Không tìm thấy node " + currentValue + " để cập nhật");
            return false;
        }
        if (existsNew) {
            fireStep(StepType.FOUND, newValue, "Giá trị mới " + newValue + " đã tồn tại trong cây");
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
            if (current.getLeft() == null) {
                fireStep(StepType.NOT_FOUND, current.getValue(), "Không tìm thấy " + value + " (nhánh trái rỗng)");
                return null;
            }
            fireStep(StepType.GO_LEFT, current.getValue(), "Đi trái");
            return findNode(current.getLeft(), value);
        }
        if (current.getRight() == null) {
            fireStep(StepType.NOT_FOUND, current.getValue(), "Không tìm thấy " + value + " (nhánh phải rỗng)");
            return null;
        }
        fireStep(StepType.GO_RIGHT, current.getValue(), "Đi phải");
        return findNode(current.getRight(), value);
    }

}
