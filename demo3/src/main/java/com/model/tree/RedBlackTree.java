package com.model.tree;

import com.model.node.RBNode;
import com.model.node.RBNode.Color;

public class RedBlackTree extends BinarySearchTree {

    @Override
    protected RBNode createNode(int value) {
        return new RBNode(value);
    }

    @Override
    public RBNode getRoot() {
        return (RBNode) this.root;
    }

    @Override
    public void create(int value) {
        if (!isEmpty()) {
            return;
        }

        RBNode node = createNode(value);
        node.setColor(Color.BLACK);
        this.root = node;
    }

    @Override
    public boolean insert(int parentValue, int value) {
        return insert(value);
    }

    @Override
    public boolean insert(int value) {
        if (search(value)) {
            return false;
        }

        RBNode newNode = createNode(value);
        newNode.setColor(Color.RED);

        if (this.root == null) {
            newNode.setColor(Color.BLACK);
            this.root = newNode;
            return true;
        }

        insertBST(getRoot(), newNode);
        fixInsert(newNode);
        return true;
    }

    private void insertBST(RBNode current, RBNode newNode) {
        if (newNode.getValue() < current.getValue()) {
            if (current.getLeft() == null) {
                current.setLeft(newNode);
                newNode.setParent(current);
            } else {
                insertBST(current.getLeft(), newNode);
            }
        } else {
            if (current.getRight() == null) {
                current.setRight(newNode);
                newNode.setParent(current);
            } else {
                insertBST(current.getRight(), newNode);
            }
        }
    }

    private void fixInsert(RBNode node) {
        while (node != this.root && colorOf(parentOf(node)) == Color.RED) {
            RBNode parent = parentOf(node);
            RBNode grandParent = parentOf(parent);

            if (parent == grandParent.getLeft()) {
                RBNode uncle = grandParent.getRight();

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
                RBNode uncle = grandParent.getLeft();

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

        getRoot().setColor(Color.BLACK);
    }

    private void leftRotate(RBNode x) {
        if (x == null || x.getRight() == null) {
            return;
        }

        RBNode y = x.getRight();
        x.setRight(y.getLeft());

        if (y.getLeft() != null) {
            y.getLeft().setParent(x);
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

        RBNode y = x.getLeft();
        x.setLeft(y.getRight());

        if (y.getRight() != null) {
            y.getRight().setParent(x);
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
        RBNode z = (RBNode) findNode(this.root, value);
        if (z == null) {
            return false;
        }

        deleteNode(z);
        return true;
    }

    @Override
    public boolean update(int currentValue, int newValue) {
        if (currentValue == newValue) {
            return search(currentValue);
        }
        if (!search(currentValue) || search(newValue)) {
            return false;
        }

        delete(currentValue);
        return insert(newValue);
    }

    private void deleteNode(RBNode z) {
        RBNode y = z;
        Color originalColor = y.getColor();

        RBNode x;
        RBNode xParent;

        if (z.getLeft() == null) {
            x = z.getRight();
            xParent = z.getParent();
            transplant(z, z.getRight());
        } else if (z.getRight() == null) {
            x = z.getLeft();
            xParent = z.getParent();
            transplant(z, z.getLeft());
        } else {
            y = (RBNode) minimum(z.getRight());
            originalColor = y.getColor();
            x = y.getRight();

            if (y.getParent() == z) {
                xParent = y;
                if (x != null) {
                    x.setParent(y);
                }
            } else {
                xParent = y.getParent();
                transplant(y, y.getRight());
                y.setRight(z.getRight());

                if (y.getRight() != null) {
                    y.getRight().setParent(y);
                }
            }

            transplant(z, y);
            y.setLeft(z.getLeft());

            if (y.getLeft() != null) {
                y.getLeft().setParent(y);
            }

            y.setColor(z.getColor());
        }

        if (originalColor == Color.BLACK) {
            fixDelete(x, xParent);
        }

        if (this.root != null) {
            getRoot().setColor(Color.BLACK);
        }
    }

    private void fixDelete(RBNode x, RBNode parent) {
        while (x != this.root && colorOf(x) == Color.BLACK) {
            if (parent == null) {
                break;
            }

            if (x == parent.getLeft()) {
                RBNode sibling = parent.getRight();

                if (colorOf(sibling) == Color.RED) {
                    sibling.setColor(Color.BLACK);
                    parent.setColor(Color.RED);
                    leftRotate(parent);
                    sibling = parent.getRight();
                }

                if (colorOf(leftOf(sibling)) == Color.BLACK
                        && colorOf(rightOf(sibling)) == Color.BLACK) {
                    if (sibling != null) {
                        sibling.setColor(Color.RED);
                    }

                    x = parent;
                    parent = x.getParent();
                } else {
                    if (colorOf(rightOf(sibling)) == Color.BLACK) {
                        if (leftOf(sibling) != null) {
                            leftOf(sibling).setColor(Color.BLACK);
                        }

                        if (sibling != null) {
                            sibling.setColor(Color.RED);
                            rightRotate(sibling);
                        }

                        sibling = parent.getRight();
                    }

                    if (sibling != null) {
                        sibling.setColor(parent.getColor());
                    }

                    parent.setColor(Color.BLACK);

                    if (rightOf(sibling) != null) {
                        rightOf(sibling).setColor(Color.BLACK);
                    }

                    leftRotate(parent);
                    x = getRoot();
                    parent = null;
                }
            } else {
                RBNode sibling = parent.getLeft();

                if (colorOf(sibling) == Color.RED) {
                    sibling.setColor(Color.BLACK);
                    parent.setColor(Color.RED);
                    rightRotate(parent);
                    sibling = parent.getLeft();
                }

                if (colorOf(rightOf(sibling)) == Color.BLACK
                        && colorOf(leftOf(sibling)) == Color.BLACK) {
                    if (sibling != null) {
                        sibling.setColor(Color.RED);
                    }

                    x = parent;
                    parent = x.getParent();
                } else {
                    if (colorOf(leftOf(sibling)) == Color.BLACK) {
                        if (rightOf(sibling) != null) {
                            rightOf(sibling).setColor(Color.BLACK);
                        }

                        if (sibling != null) {
                            sibling.setColor(Color.RED);
                            leftRotate(sibling);
                        }

                        sibling = parent.getLeft();
                    }

                    if (sibling != null) {
                        sibling.setColor(parent.getColor());
                    }

                    parent.setColor(Color.BLACK);

                    if (leftOf(sibling) != null) {
                        leftOf(sibling).setColor(Color.BLACK);
                    }

                    rightRotate(parent);
                    x = getRoot();
                    parent = null;
                }
            }
        }

        if (x != null) {
            x.setColor(Color.BLACK);
        }
    }

    private void transplant(RBNode oldNode, RBNode newNode) {
        if (oldNode.getParent() == null) {
            this.root = newNode;
        } else if (oldNode == oldNode.getParent().getLeft()) {
            oldNode.getParent().setLeft(newNode);
        } else {
            oldNode.getParent().setRight(newNode);
        }

        if (newNode != null) {
            newNode.setParent(oldNode.getParent());
        }
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

    private RBNode leftOf(RBNode node) {
        if (node == null) {
            return null;
        }
        return node.getLeft();
    }

    private RBNode rightOf(RBNode node) {
        if (node == null) {
            return null;
        }
        return node.getRight();
    }

}
