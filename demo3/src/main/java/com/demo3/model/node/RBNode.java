package com.demo3.model.node;

public class RBNode extends BinaryNode {
    private RBNode parent;
    private Color color;

    public enum Color {
        RED,
        BLACK;
    }

    public RBNode(int value) {
        super(value);
        this.parent = null;
        this.color = Color.RED;
    }

    // getter
    // parent is needed in order to do red black tree manipulation

    @Override
    public RBNode getLeft() {
        return (RBNode) super.getLeft();
    }

    @Override
    public RBNode getRight() {
        return (RBNode) super.getRight();
    }

    @Override
    public void setLeft(BinaryNode left) {
        if (left != null && !(left instanceof RBNode)) {
            throw new IllegalArgumentException("RBNode can only use RBNode children.");
        }
        super.setLeft(left);
    }

    @Override
    public void setRight(BinaryNode right) {
        if (right != null && !(right instanceof RBNode)) {
            throw new IllegalArgumentException("RBNode can only use RBNode children.");
        }
        super.setRight(right);
    }

    public void setLeft(RBNode left) {
        super.setLeft(left);
    }

    public void setRight(RBNode right) {
        super.setRight(right);
    }

    public RBNode getParent() {
        return this.parent;
    }

    public Color getColor() {
        return this.color;
    }

    public void setParent(RBNode parent) {
        this.parent = parent;
    }

    public void setColor(Color color) {
        this.color = color;
    }

}

// exception handling
