package com.model.tree;

import com.model.node.RBNode;
import com.model.node.RBNode.Color;
import com.model.step.StepType;

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
        fireStep(StepType.INSERT_NODE, value, "Tạo gốc (root) màu ĐEN với giá trị " + value);
        this.root = node;
    }

    @Override
    public boolean insert(int parentValue, int value) {
        return insert(value);
    }

    @Override
    public boolean insert(int value) {
        com.model.step.TreeOperationListener temp = this.listener;
        this.listener = null;
        boolean exists = search(value);
        this.listener = temp;

        if (exists) {
            fireStep(StepType.FOUND, value, "Node " + value + " đã tồn tại!");
            return false;
        }

        if (this.root == null) {
            RBNode newNode = createNode(value);
            newNode.setColor(Color.BLACK);
            fireStep(StepType.INSERT_NODE, value, "Tạo gốc (root) màu ĐEN với giá trị " + value);
            this.root = newNode;
            return true;
        }

        RBNode newNode = createNode(value);
        newNode.setColor(Color.RED);
        fireStep(StepType.INSERT_NODE, value, "Tạo node mới màu ĐỎ: " + value);

        insertBST((RBNode) getRoot(), newNode);
        fireStep(StepType.FIX_START, value, "Bắt đầu quá trình Fix-up sau khi chèn");
        fixInsert(newNode);
        return true;
    }

    private void insertBST(RBNode current, RBNode newNode) {
        fireStep(StepType.COMPARE, current.getValue(), "So sánh " + newNode.getValue() + " với " + current.getValue());
        if (newNode.getValue() < current.getValue()) {
            if (current.getLeft() == null) {
                fireStep(StepType.INSERT_NODE, newNode.getValue(),
                        "Chèn " + newNode.getValue() + " làm con trái của " + current.getValue());
                current.setLeft(newNode);
                newNode.setParent(current);
            } else {
                fireStep(StepType.GO_LEFT, current.getValue(), "Đi trái");
                insertBST((RBNode) current.getLeft(), newNode);
            }
        } else {
            if (current.getRight() == null) {
                fireStep(StepType.INSERT_NODE, newNode.getValue(),
                        "Chèn " + newNode.getValue() + " làm con phải của " + current.getValue());
                current.setRight(newNode);
                newNode.setParent(current);
            } else {
                fireStep(StepType.GO_RIGHT, current.getValue(), "Đi phải");
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
                    fireStep(StepType.RECOLOR, parent.getValue(),
                            "Case 1: Uncle màu ĐỎ. Đổi màu Parent, Uncle thành ĐEN, GrandParent thành ĐỎ");
                    parent.setColor(Color.BLACK);
                    uncle.setColor(Color.BLACK);
                    grandParent.setColor(Color.RED);
                    node = grandParent;
                } else {
                    if (node == parent.getRight()) {
                        node = parent;
                        fireStep(StepType.ROTATE_LEFT, node.getValue(),
                                "Case 2: Node là con phải -> Xoay trái tại Parent");
                        leftRotate(node);
                    }

                    fireStep(StepType.RECOLOR, parentOf(node).getValue(),
                            "Case 3: Node là con trái -> Đổi màu Parent (ĐEN), GrandParent (ĐỎ) và Xoay phải");
                    parentOf(node).setColor(Color.BLACK);
                    parentOf(parentOf(node)).setColor(Color.RED);
                    rightRotate(parentOf(parentOf(node)));
                }
            } else {
                RBNode uncle = (RBNode) grandParent.getLeft();

                if (colorOf(uncle) == Color.RED) {
                    fireStep(StepType.RECOLOR, parent.getValue(),
                            "Case 1: Uncle màu ĐỎ. Đổi màu Parent, Uncle thành ĐEN, GrandParent thành ĐỎ");
                    parent.setColor(Color.BLACK);
                    uncle.setColor(Color.BLACK);
                    grandParent.setColor(Color.RED);
                    node = grandParent;
                } else {
                    if (node == parent.getLeft()) {
                        node = parent;
                        fireStep(StepType.ROTATE_RIGHT, node.getValue(),
                                "Case 2: Node là con trái -> Xoay phải tại Parent");
                        rightRotate(node);
                    }

                    fireStep(StepType.RECOLOR, parentOf(node).getValue(),
                            "Case 3: Node là con phải -> Đổi màu Parent (ĐEN), GrandParent (ĐỎ) và Xoay trái");
                    parentOf(node).setColor(Color.BLACK);
                    parentOf(parentOf(node)).setColor(Color.RED);
                    leftRotate(parentOf(parentOf(node)));
                }
            }
        }

        if (getRoot().getColor() != Color.BLACK) {
            fireStep(StepType.RECOLOR, getRoot().getValue(), "Đổi màu Root thành ĐEN");
            getRoot().setColor(Color.BLACK);
        }
    }

    private void leftRotate(RBNode x) {
        if (x == null || x.getRight() == null) {
            return;
        }
        fireStep(StepType.ROTATE_LEFT, x.getValue(), "Xoay trái tại " + x.getValue());

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
        fireStep(StepType.ROTATE_RIGHT, x.getValue(), "Xoay phải tại " + x.getValue());

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
            fireStep(StepType.NOT_FOUND, value, "Không tìm thấy " + value + " để xóa");
            return false;
        }

        fireStep(StepType.DELETE_NODE, z.getValue(), "Bắt đầu xóa node " + z.getValue());
        deleteNode(z);
        return true;
    }

    @Override
    public boolean update(int currentValue, int newValue) {
        com.model.step.TreeOperationListener temp = this.listener;
        this.listener = null;
        boolean existsCurrent = search(currentValue);
        boolean existsNew = search(newValue);
        this.listener = temp;

        if (currentValue == newValue) {
            if (existsCurrent) {
                fireStep(StepType.FOUND, currentValue, "Node " + currentValue + " không cần đổi");
                return true;
            }
            fireStep(StepType.NOT_FOUND, currentValue, "Không tìm thấy node " + currentValue);
            return false;
        }
        if (!existsCurrent) {
            fireStep(StepType.NOT_FOUND, currentValue, "Không tìm thấy node " + currentValue + " để cập nhật");
            return false;
        }
        if (existsNew) {
            fireStep(StepType.FOUND, newValue, "Giá trị mới " + newValue + " đã tồn tại trong cây");
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
            x = (RBNode) z.getRight();
            xParent = z.getParent();
            fireStep(StepType.TRANSPLANT, z.getValue(), "Nhổ node và thay bằng con phải");
            transplant(z, (RBNode) z.getRight());
        } else if (z.getRight() == null) {
            x = (RBNode) z.getLeft();
            xParent = z.getParent();
            fireStep(StepType.TRANSPLANT, z.getValue(), "Nhổ node và thay bằng con trái");
            transplant(z, (RBNode) z.getLeft());
        } else {
            y = (RBNode) minimum(z.getRight());
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
                    y.getRight().setParent(y);
                }
            }

            fireStep(StepType.TRANSPLANT, z.getValue(), "Nhổ node và thay bằng successor " + y.getValue());
            transplant(z, y);
            y.setLeft(z.getLeft());

            if (y.getLeft() != null) {
                y.getLeft().setParent(y);
            }

            y.setColor(z.getColor());
        }

        if (originalColor == Color.BLACK) {
            fireStep(StepType.FIX_START, (x != null) ? x.getValue() : -1,
                    "Node bị xóa/thay thế mang màu ĐEN -> Kích hoạt Fix-up");
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
