package com.model.node;

public class AVLNode extends BinaryNode {

    private int height;

    public AVLNode(int value) {
        super(value);
        this.height = 1;
    }

    @Override
    public AVLNode getLeft() {
        return (AVLNode) super.getLeft();
    }

    @Override
    public AVLNode getRight() {
        return (AVLNode) super.getRight();
    }

    @Override
    public void setLeft(BinaryNode left) {
        if (left != null && !(left instanceof AVLNode)) {
            throw new IllegalArgumentException("AVLNode can only use AVLNode children.");
        }
        super.setLeft(left);
    }

    @Override
    public void setRight(BinaryNode right) {
        if (right != null && !(right instanceof AVLNode)) {
            throw new IllegalArgumentException("AVLNode can only use AVLNode children.");
        }
        super.setRight(right);
    }

    public void setLeft(AVLNode left) {
        super.setLeft(left);
    }

    public void setRight(AVLNode right) {
        super.setRight(right);
    }

    // set and get for avl tree height
    public int getStoredHeight() {
        return this.height;
    }

    public void setStoredHeight(int height) {
        this.height = height;
    }
}
