package com.model.tree;

public final class TreeFactory {

    private TreeFactory() {
    }

    public static AbstractTree<?> create(TreeType type) {
        if (type == null) {
            throw new IllegalArgumentException("Tree type cannot be null.");
        }

        switch (type) {
            case GENERAL:
                return new GeneralTree();
            case BINARY:
                return new BinaryTree();
            case BINARY_SEARCH:
                return new BinarySearchTree();
            case AVL:
                return new AVLTree();
            case RED_BLACK:
                return new RedBlackTree();
            default:
                throw new IllegalArgumentException("Unsupported tree type: " + type);
        }
    }
}
