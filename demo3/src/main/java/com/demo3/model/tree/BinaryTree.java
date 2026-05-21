package com.demo3.model.tree;

import com.demo3.model.node.BinaryNode;

public class BinaryTree extends AbstractBinaryTree<BinaryNode> {

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

}
