package com.model.tree;

import com.model.node.BinaryNode;
import com.model.step.StepType;

import java.util.LinkedList;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class BinaryTree extends AbstractTree<BinaryNode> {

    @Override
    public void create(int value) {
        if (!isEmpty()) {
            return;
        }
        fireStep(StepType.INSERT_NODE, value, "Tạo gốc (root) với giá trị " + value);
        this.root = new BinaryNode(value);
    }

    @Override
    public boolean insert(int parentValue, int value) {
        if (this.isEmpty()) {
            fireStep(StepType.NOT_FOUND, parentValue, "Cây rỗng");
            return false;
        }

        BinaryNode parentNode = findNode(this.root, parentValue);

        if (parentNode == null) {
            fireStep(StepType.NOT_FOUND, parentValue, "Không tìm thấy parent " + parentValue);
            return false;
        }

        if (findNode(this.root, value) != null) {
            fireStep(StepType.COMPARE, value, "Giá trị " + value + " đã tồn tại");
            return false;
        }

        if (parentNode.getLeft() == null) {
            fireStep(StepType.INSERT_NODE, value, "Thêm " + value + " làm con trái của " + parentValue);
            parentNode.setLeft(new BinaryNode(value));
            return true;
        }

        if (parentNode.getRight() == null) {
            fireStep(StepType.INSERT_NODE, value, "Thêm " + value + " làm con phải của " + parentValue);
            parentNode.setRight(new BinaryNode(value));
            return true;
        }

        fireStep(StepType.NOT_FOUND, parentValue, "Node " + parentValue + " đã đủ 2 con");
        return false;
    }

    @Override
    public boolean delete(int value) {
        if (isEmpty()) {
            return false;
        }

        fireStep(StepType.COMPARE, this.root.getValue(), "So sánh root với " + value);
        if (this.root.getValue() == value) {
            fireStep(StepType.DELETE_NODE, value, "Xóa gốc (root) " + value);
            this.root = null;
            return true;
        }

        return deleteNode(this.root, value);
    }

    private boolean deleteNode(BinaryNode current, int value) {
        if (current == null) {
            return false;
        }

        if (current.getLeft() != null) {
            fireStep(StepType.COMPARE, current.getLeft().getValue(), "So sánh con trái với " + value);
            if (current.getLeft().getValue() == value) {
                fireStep(StepType.DELETE_NODE, value, "Xóa con trái " + value + " của " + current.getValue());
                current.setLeft(null);
                return true;
            }
        }

        if (current.getRight() != null) {
            fireStep(StepType.COMPARE, current.getRight().getValue(), "So sánh con phải với " + value);
            if (current.getRight().getValue() == value) {
                fireStep(StepType.DELETE_NODE, value, "Xóa con phải " + value + " của " + current.getValue());
                current.setRight(null);
                return true;
            }
        }

        fireStep(StepType.GO_LEFT, current.getValue(), "Duyệt nhánh trái của " + current.getValue() + " để xóa");
        if (deleteNode(current.getLeft(), value)) {
            return true;
        }

        fireStep(StepType.GO_RIGHT, current.getValue(), "Duyệt nhánh phải của " + current.getValue() + " để xóa");
        return deleteNode(current.getRight(), value);
    }

    @Override
    public boolean update(int currentValue, int newValue) {
        if (isEmpty()) {
            return false;
        }
        if (currentValue != newValue && findNode(this.root, newValue) != null) {
            fireStep(StepType.COMPARE, newValue, "Giá trị thay thế " + newValue + " đã tồn tại");
            return false;
        }

        BinaryNode node = findNode(this.root, currentValue);
        if (node == null) {
            fireStep(StepType.NOT_FOUND, currentValue, "Không tìm thấy node " + currentValue + " để cập nhật");
            return false;
        }

        fireStep(StepType.REPLACE_VALUE, currentValue, "Cập nhật giá trị " + currentValue + " thành " + newValue);
        node.setValue(newValue);
        return true;
    }

    @Override
    public boolean search(int value) {
        return findNode(this.root, value) != null;
    }

    protected BinaryNode findNode(BinaryNode current, int value) {
        if (current == null) {
            return null;
        }

        fireStep(StepType.COMPARE, current.getValue(), "So sánh với " + current.getValue());
        if (current.getValue() == value) {
            fireStep(StepType.FOUND, current.getValue(), "Tìm thấy " + current.getValue());
            return current;
        }

        fireStep(StepType.GO_LEFT, current.getValue(), "Tìm nhánh trái của " + current.getValue());
        BinaryNode leftResult = findNode(current.getLeft(), value);
        if (leftResult != null) {
            return leftResult;
        }

        fireStep(StepType.GO_RIGHT, current.getValue(), "Tìm nhánh phải của " + current.getValue());
        return findNode(current.getRight(), value);
    }

    @Override
    public int getHeight() {
        return getHeightRec(this.root);
    }

    protected int getHeightRec(BinaryNode node) {
        if (node == null) {
            return 0;
        }
        return 1 + Math.max(getHeightRec(node.getLeft()), getHeightRec(node.getRight()));
    }

    @Override
    public int getNumberOfNodes() {
        return countNodes(this.root);
    }

    protected int countNodes(BinaryNode node) {
        if (node == null) {
            return 0;
        }
        return 1 + countNodes(node.getLeft()) + countNodes(node.getRight());
    }

    @Override
    public List<Integer> traverse(TraversalType type) {
        if (type == null) {
            throw new IllegalArgumentException("Traversal type cannot be null.");
        }

        List<Integer> result = new ArrayList<>();
        if (isEmpty()) {
            return result;
        }

        switch (type) {
            case IN_ORDER:
                inOrderRec(this.root, result);
                break;
            case PRE_ORDER:
                preOrderRec(this.root, result);
                break;
            case POST_ORDER:
                postOrderRec(this.root, result);
                break;
            case BFS:
                bfsTraverse(this.root, result);
                break;
        }

        return result;
    }

    protected void inOrderRec(BinaryNode node, List<Integer> result) {
        if (node == null) {
            return;
        }

        fireStep(StepType.GO_LEFT, node.getValue(), "In-order: Đi sang nhánh trái của " + node.getValue());
        inOrderRec(node.getLeft(), result);
        
        fireStep(StepType.VISIT, node.getValue(), "In-order: Thăm node " + node.getValue());
        fireStep(StepType.ADD_TO_RESULT, node.getValue(), "Thêm " + node.getValue() + " vào danh sách kết quả");
        result.add(node.getValue());
        
        fireStep(StepType.GO_RIGHT, node.getValue(), "In-order: Đi sang nhánh phải của " + node.getValue());
        inOrderRec(node.getRight(), result);
    }

    protected void preOrderRec(BinaryNode node, List<Integer> result) {
        if (node == null) {
            return;
        }

        fireStep(StepType.VISIT, node.getValue(), "Pre-order: Thăm node " + node.getValue());
        fireStep(StepType.ADD_TO_RESULT, node.getValue(), "Thêm " + node.getValue() + " vào danh sách kết quả");
        result.add(node.getValue());
        
        fireStep(StepType.GO_LEFT, node.getValue(), "Pre-order: Đi sang nhánh trái của " + node.getValue());
        preOrderRec(node.getLeft(), result);
        
        fireStep(StepType.GO_RIGHT, node.getValue(), "Pre-order: Đi sang nhánh phải của " + node.getValue());
        preOrderRec(node.getRight(), result);
    }

    protected void postOrderRec(BinaryNode node, List<Integer> result) {
        if (node == null) {
            return;
        }

        fireStep(StepType.GO_LEFT, node.getValue(), "Post-order: Đi sang nhánh trái của " + node.getValue());
        postOrderRec(node.getLeft(), result);
        
        fireStep(StepType.GO_RIGHT, node.getValue(), "Post-order: Đi sang nhánh phải của " + node.getValue());
        postOrderRec(node.getRight(), result);
        
        fireStep(StepType.VISIT, node.getValue(), "Post-order: Thăm node " + node.getValue());
        fireStep(StepType.ADD_TO_RESULT, node.getValue(), "Thêm " + node.getValue() + " vào danh sách kết quả");
        result.add(node.getValue());
    }

    protected void bfsTraverse(BinaryNode root, List<Integer> result) {
        Queue<BinaryNode> queue = new ArrayDeque<>();
        queue.add(root);

        while (!queue.isEmpty()) {
            BinaryNode current = queue.poll();
            fireStep(StepType.VISIT, current.getValue(), "BFS: Lấy node " + current.getValue() + " từ Queue và thăm");
            fireStep(StepType.ADD_TO_RESULT, current.getValue(), "Thêm " + current.getValue() + " vào danh sách kết quả");
            result.add(current.getValue());

            if (current.getLeft() != null) {
                fireStep(StepType.GO_LEFT, current.getValue(), "Đưa con trái " + current.getLeft().getValue() + " vào Queue");
                queue.add(current.getLeft());
            }
            if (current.getRight() != null) {
                fireStep(StepType.GO_RIGHT, current.getValue(), "Đưa con phải " + current.getRight().getValue() + " vào Queue");
                queue.add(current.getRight());
            }
        }
    }

}
