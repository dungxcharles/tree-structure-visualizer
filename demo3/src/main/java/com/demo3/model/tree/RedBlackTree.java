package com.demo3.model.tree;

import com.demo3.model.node.RBNode;
import com.demo3.model.node.RBNode.Color;

import java.util.List;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Queue;

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
        RBNode z = findNode(this.root, value);

        if (z == null) {
            return false;
        }

        deleteNode(z);
        return true;
    }

    private void deleteNode(RBNode z) {
        RBNode y = z;
        Color originalColor = y.getColor();

        RBNode x;
        RBNode xParent;

        if (z.getLeft() == null) {
            x = (RBNode) z.getRight();
            xParent = z.getParent();
            transplant(z, (RBNode) z.getRight());
        } else if (z.getRight() == null) {
            x = (RBNode) z.getLeft();
            xParent = z.getParent();
            transplant(z, (RBNode) z.getLeft());
        } else {
            y = minimum((RBNode) z.getRight());
            originalColor = y.getColor();

            x = (RBNode) y.getRight();

            if (y.getParent() == z) {
                xParent = y;

                if (x != null) {
                    x.setParent(y);
                }
            } else {
                xParent = y.getParent();

                transplant(y, (RBNode) y.getRight());

                y.setRight(z.getRight());

                if (y.getRight() != null) {
                    ((RBNode) y.getRight()).setParent(y);
                }
            }

            transplant(z, y);

            y.setLeft(z.getLeft());

            if (y.getLeft() != null) {
                ((RBNode) y.getLeft()).setParent(y);
            }

            y.setColor(z.getColor());
        }

        if (originalColor == Color.BLACK) {
            fixDelete(x, xParent);
        }

        if (this.root != null) {
            this.root.setColor(Color.BLACK);
        }
    }

    private void fixDelete(RBNode x, RBNode parent) {
        while (x != this.root && colorOf(x) == Color.BLACK) {
            if (parent == null) {
                break;
            }

            if (x == parent.getLeft()) {
                RBNode sibling = (RBNode) parent.getRight();

                if (colorOf(sibling) == Color.RED) {
                    sibling.setColor(Color.BLACK);
                    parent.setColor(Color.RED);
                    leftRotate(parent);
                    sibling = (RBNode) parent.getRight();
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

                        sibling = (RBNode) parent.getRight();
                    }

                    if (sibling != null) {
                        sibling.setColor(parent.getColor());
                    }

                    parent.setColor(Color.BLACK);

                    if (rightOf(sibling) != null) {
                        rightOf(sibling).setColor(Color.BLACK);
                    }

                    leftRotate(parent);

                    x = this.root;
                    parent = null;
                }
            } else {
                RBNode sibling = (RBNode) parent.getLeft();

                if (colorOf(sibling) == Color.RED) {
                    sibling.setColor(Color.BLACK);
                    parent.setColor(Color.RED);
                    rightRotate(parent);
                    sibling = (RBNode) parent.getLeft();
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

                        sibling = (RBNode) parent.getLeft();
                    }

                    if (sibling != null) {
                        sibling.setColor(parent.getColor());
                    }

                    parent.setColor(Color.BLACK);

                    if (leftOf(sibling) != null) {
                        leftOf(sibling).setColor(Color.BLACK);
                    }

                    rightRotate(parent);

                    x = this.root;
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

    private RBNode minimum(RBNode node) {
        while (node.getLeft() != null) {
            node = (RBNode) node.getLeft();
        }

        return node;
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
        return getHeightRec(this.root);
    }

    private int getHeightRec(RBNode node) {
        if (node == null) {
            return 0;
        }

        return 1 + Math.max(
                getHeightRec((RBNode) node.getLeft()),
                getHeightRec((RBNode) node.getRight()));
    }

    @Override
    public int getNumberOfNodes() {
        return countNodes(this.root);
    }

    private int countNodes(RBNode node) {
        if (node == null) {
            return 0;
        }

        return 1
                + countNodes((RBNode) node.getLeft())
                + countNodes((RBNode) node.getRight());
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

            default:
                throw new UnsupportedOperationException("Unsupported traversal type: " + type);
        }

        return result;
    }

    private void inOrderRec(RBNode node, List<Integer> result) {
        if (node == null) {
            return;
        }

        inOrderRec((RBNode) node.getLeft(), result);
        result.add(node.getValue());
        inOrderRec((RBNode) node.getRight(), result);
    }

    private void preOrderRec(RBNode node, List<Integer> result) {
        if (node == null) {
            return;
        }

        result.add(node.getValue());
        preOrderRec((RBNode) node.getLeft(), result);
        preOrderRec((RBNode) node.getRight(), result);
    }

    private void postOrderRec(RBNode node, List<Integer> result) {
        if (node == null) {
            return;
        }

        postOrderRec((RBNode) node.getLeft(), result);
        postOrderRec((RBNode) node.getRight(), result);
        result.add(node.getValue());
    }

    private void bfsTraverse(RBNode root, List<Integer> result) {
        if (root == null) {
            return;
        }

        Queue<RBNode> queue = new ArrayDeque<>();
        queue.add(root);

        while (!queue.isEmpty()) {
            RBNode current = queue.poll();
            result.add(current.getValue());

            if (current.getLeft() != null) {
                queue.add((RBNode) current.getLeft());
            }

            if (current.getRight() != null) {
                queue.add((RBNode) current.getRight());
            }
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

        return (RBNode) node.getLeft();
    }

    private RBNode rightOf(RBNode node) {
        if (node == null) {
            return null;
        }

        return (RBNode) node.getRight();
    }
}