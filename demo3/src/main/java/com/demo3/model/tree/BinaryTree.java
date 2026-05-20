package com.demo3.model.tree;

import com.demo3.model.node.BinaryNode;

import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class BinaryTree extends AbstractTree<BinaryNode> {

    @Override
    public void create(int value) {
        if (!isEmpty()) {
            return; // hoặc throw new IllegalStateException("Tree already has a root.");
        }
        this.root = new BinaryNode(value);
    }

    @Override
    public boolean insert(int parentValue, int value) {
        if (isEmpty()) {
            return false; // hoặc throw new IllegalStateException("Tree is empty. Create one first.");
        }
        if (search(value)) {
            return false; // hoặc throw new IllegalStateException("Value already exists in the tree.");
        }

        BinaryNode parentNode = findNode(this.root, parentValue);

        if (parentNode == null) {
            return false; // Không tìm thấy node cha
        }

        // Chèn vào vị trí trống của parent (ưu tiên Trái, sau đó Phải)
        if (parentNode.getLeft() == null) {
            parentNode.setLeft(new BinaryNode(value));
        } else if (parentNode.getRight() == null) {
            parentNode.setRight(new BinaryNode(value));
        } else {
            // Node cha đã đủ 2 con
            return false; // hoặc throw new IllegalStateException("Parent node already has 2 children.");
        }
        return true;
    }

    @Override
    public boolean delete(int value) {
        if (this.root == null) {
            return false;
        }

        // Nếu root chính là node cần xóa, cắt bỏ toàn bộ cây
        if (this.root.getValue() == value) {
            this.root = null;
            return true;
        }

        return deleteSubtree(this.root, value);
    }

    private boolean deleteSubtree(BinaryNode current, int value) {
        if (current == null) {
            return false;
        }

        // Kiểm tra con trái
        if (current.getLeft() != null && current.getLeft().getValue() == value) {
            current.setLeft(null); // Cắt đứt toàn bộ nhánh trái
            return true;
        }

        // Kiểm tra con phải
        if (current.getRight() != null && current.getRight().getValue() == value) {
            current.setRight(null); // Cắt đứt toàn bộ nhánh phải
            return true;
        }

        // Tiếp tục đệ quy tìm kiếm và xóa ở các nhánh con
        return deleteSubtree(current.getLeft(), value) || deleteSubtree(current.getRight(), value);
    }

    @Override
    public boolean update(int currentValue, int newValue) {
        if (isEmpty()) {
            return false;
        }
        if (currentValue != newValue && search(newValue)) {
            return false;
        }

        BinaryNode node = findNode(this.root, currentValue);
        if (node == null) {
            return false;
        }

        node.setValue(newValue);
        return true;
    }

    @Override
    public boolean search(int value) {
        return findNode(this.root, value) != null;
    }

    private BinaryNode findNode(BinaryNode root, int value) {
        if (root == null || root.getValue() == value) {
            return root;
        }

        BinaryNode leftResult = findNode(root.getLeft(), value);
        if (leftResult != null) {
            return leftResult;
        }

        return findNode(root.getRight(), value);
    }

    @Override
    public int getHeight() {
        return getHeightRec(this.root);
    }

    private int getHeightRec(BinaryNode node) {
        if (node == null)
            return 0;
        return 1 + Math.max(getHeightRec(node.getLeft()), getHeightRec(node.getRight()));
    }

    @Override
    public int getNumberOfNodes() {
        return countNodes(this.root);
    }

    private int countNodes(BinaryNode node) {
        if (node == null)
            return 0;
        return 1 + countNodes(node.getLeft()) + countNodes(node.getRight());
    }

    @Override
    public List<Integer> traverse(TraversalType type) {
        if (type == null) {
            throw new IllegalArgumentException("Traversal type cannot be null.");
        }

        List<Integer> result = new ArrayList<>();
        if (isEmpty())
            return result;

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

    // in , pre , post order
    private void inOrderRec(BinaryNode root, List<Integer> result) {
        if (root == null)
            return;
        inOrderRec(root.getLeft(), result);
        result.add(root.getValue());
        inOrderRec(root.getRight(), result);
    }

    private void preOrderRec(BinaryNode root, List<Integer> result) {
        if (root == null)
            return;
        result.add(root.getValue());
        preOrderRec(root.getLeft(), result);
        preOrderRec(root.getRight(), result);

    }

    private void postOrderRec(BinaryNode root, List<Integer> result) {
        if (root == null)
            return;
        postOrderRec(root.getLeft(), result);
        postOrderRec(root.getRight(), result);
        result.add(root.getValue());
    }

    // levelOrder
    private void bfsTraverse(BinaryNode root, List<Integer> result) {
        if (root == null)
            return;
        Queue<BinaryNode> queue = new LinkedList<>();
        queue.add(root);

        while (!queue.isEmpty()) {
            BinaryNode current = queue.poll();
            result.add(current.getValue());

            if (current.getLeft() != null)
                queue.add(current.getLeft());
            if (current.getRight() != null)
                queue.add(current.getRight());
        }
    }
}
