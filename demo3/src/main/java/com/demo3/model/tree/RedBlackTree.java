package com.demo3.model.tree;

import com.demo3.model.node.RBNode;
import com.demo3.model.node.RBNode.Color;

import java.util.List;

public class RedBlackTree extends AbstractTree<RBNode> {

    @Override
    public void create(int value) {
        if (!isEmpty()) {
            return;
        }

        RBNode node = new RBNode(value);
        node.setColor(Color.BLACK);
        this.root = node;
    }

    @Override
    public boolean insert(int parentValue, int value) {
        // parentValue is ignored because Red-Black Tree inserts by BST rule.

        if (search(value)) {
            return false;
        }

        RBNode newNode = new RBNode(value);
        newNode.setColor(Color.RED);

        if (this.root == null) {
            newNode.setColor(Color.BLACK);
            this.root = newNode;
            return true;
        }

        insertBST(this.root, newNode);
        fixInsert(newNode);

        return true;
    }

    private void insertBST(RBNode current, RBNode newNode) {
        if (newNode.getValue() < current.getValue()) {
            if (current.getLeft() == null) {
                current.setLeft(newNode);
                newNode.setParent(current);
            } else {
                insertBST((RBNode) current.getLeft(), newNode);
            }
        } else {
            if (current.getRight() == null) {
                current.setRight(newNode);
                newNode.setParent(current);
            } else {
                insertBST((RBNode) current.getRight(), newNode);
            }
        }
    }

    private void fixInsert(RBNode node) {
        while (node != this.root && colorOf(parentOf(node)) == Color.RED) {
            RBNode parent = parentOf(node);
            RBNode grandParent = parentOf(parent);

            if (parent == grandParent.getLeft()) {
                RBNode uncle = (RBNode) grandParent.getRight();

                if (colorOf(uncle) == Color.RED) {
                    parent.setColor(Color.BLACK);
                    uncle.setColor(Color.BLACK);
                    grandParent.setColor(Color.RED);
                    node = grandParent;
                } else {
                    if (node == parent.getRight()) {
                        node = parent;
                        leftRotate(node);
                    }

                    parentOf(node).setColor(Color.BLACK);
                    parentOf(parentOf(node)).setColor(Color.RED);
                    rightRotate(parentOf(parentOf(node)));
                }
            } else {
                RBNode uncle = (RBNode) grandParent.getLeft();

                if (colorOf(uncle) == Color.RED) {
                    parent.setColor(Color.BLACK);
                    uncle.setColor(Color.BLACK);
                    grandParent.setColor(Color.RED);
                    node = grandParent;
                } else {
                    if (node == parent.getLeft()) {
                        node = parent;
                        rightRotate(node);
                    }

                    parentOf(node).setColor(Color.BLACK);
                    parentOf(parentOf(node)).setColor(Color.RED);
                    leftRotate(parentOf(parentOf(node)));
                }
            }
        }

        this.root.setColor(Color.BLACK);
    }

    private void leftRotate(RBNode x) {
        if (x == null || x.getRight() == null) {
            return;
        }

        RBNode y = (RBNode) x.getRight();

        x.setRight(y.getLeft());

        if (y.getLeft() != null) {
            ((RBNode) y.getLeft()).setParent(x);
        }

        y.setParent(x.getParent());

        if (x.getParent() == null) {
            this.root = y;
        } else if (x == x.getParent().getLeft()) {
            x.getParent().setLeft(y);
        } else {
            x.getParent().setRight(y);
        }

        y.setLeft(x);
        x.setParent(y);
    }

    private void rightRotate(RBNode x) {
        if (x == null || x.getLeft() == null) {
            return;
        }

        RBNode y = (RBNode) x.getLeft();

        x.setLeft(y.getRight());

        if (y.getRight() != null) {
            ((RBNode) y.getRight()).setParent(x);
        }

        y.setParent(x.getParent());

        if (x.getParent() == null) {
            this.root = y;
        } else if (x == x.getParent().getRight()) {
            x.getParent().setRight(y);
        } else {
            x.getParent().setLeft(y);
        }

        y.setRight(x);
        x.setParent(y);
    }

    @Override
    public boolean delete(int value) {
        return false;
    }

    @Override
    public boolean search(int value) {
        return findNode(this.root, value) != null;
    }

    private RBNode findNode(RBNode current, int value) {
        if (current == null || current.getValue() == value) {
            return current;
        }

        if (value < current.getValue()) {
            return findNode((RBNode) current.getLeft(), value);
        }

        return findNode((RBNode) current.getRight(), value);
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

    private Color colorOf(RBNode node) {
        if (node == null) {
            return Color.BLACK;
        }

        return node.getColor();
    }

    private RBNode parentOf(RBNode node) {
        if (node == null) {
            return null;
        }

        return node.getParent();
    }
}