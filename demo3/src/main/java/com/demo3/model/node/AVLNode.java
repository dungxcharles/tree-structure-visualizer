package com.demo3.model.node;

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

    public int getStoredHeight() {
        return this.height;
    }

    public void setStoredHeight(int height) {
        this.height = height;
    }
}
