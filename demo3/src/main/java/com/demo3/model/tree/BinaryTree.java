package com.demo3.model.tree;

import com.demo3.model.node.BinaryNode;

import java.util.List;

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
    public boolean delete(int value) {
        return false;
    }

    @Override
    public int getHeight() {
        return 0;
    }

    @Override
    public int getNumberOfNodes() {
        return 0;
    }

    @Override
    public List<Integer> traverse(TraversalType type) {
        return List.of();
    }
}