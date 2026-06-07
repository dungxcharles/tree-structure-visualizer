package com.model.tree;

import com.model.node.BinaryNode;
import com.model.step.StepType;

public class BinarySearchTree extends BinaryTree {

    protected BinaryNode createNode(int value) {
        return new BinaryNode(value);
    }

    @Override
    public void create(int value) {
        if (!isEmpty()) {
            return;
        }
        fireStep(StepType.INSERT_NODE, value, "Create root with value " + value);
        this.root = createNode(value);
    }

    @Override
    public boolean insert(int parentValue, int value) {
        if (this.isEmpty()) {
            throw new NullRootException("Cannot perform: create a new node with parent when the root is null.");
        }
        return insert(value);
    }

    public boolean insert(int value) {
        int initialSize = getNumberOfNodes();
        this.root = insertRec(this.root, createNode(value));
        return getNumberOfNodes() > initialSize;
    }

    protected BinaryNode insertRec(BinaryNode current, BinaryNode newNode) {
        if (current == null) {
            fireStep(StepType.INSERT_NODE, newNode.getValue(), "Insert node " + newNode.getValue());
            return newNode;
        }

        fireStep(StepType.COMPARE, current.getValue(), "Compare " + newNode.getValue() + " with " + current.getValue());
        if (newNode.getValue() == current.getValue()) {
            fireStep(StepType.FOUND, current.getValue(), "Node " + current.getValue() + " has already been inserted!");
            return current;
        } else if (newNode.getValue() < current.getValue()) {
            if (current.getLeft() == null) {
                fireStep(StepType.GO_LEFT, current.getValue(),
                        newNode.getValue() + " < " + current.getValue() + " -> Go left (empty position)");
                current.setLeft(newNode);
                fireStep(StepType.INSERT_NODE, newNode.getValue(), "Insert node " + newNode.getValue());
                return current;
            }
            fireStep(StepType.GO_LEFT, current.getValue(),
                    newNode.getValue() + " < " + current.getValue() + " -> Go left");
            current.setLeft(insertRec(current.getLeft(), newNode));
        } else if (newNode.getValue() > current.getValue()) {
            if (current.getRight() == null) {
                fireStep(StepType.GO_RIGHT, current.getValue(),
                        newNode.getValue() + " > " + current.getValue() + " -> Go right (empty position)");
                current.setRight(newNode);
                fireStep(StepType.INSERT_NODE, newNode.getValue(), "Insert node " + newNode.getValue());
                return current;
            }
            fireStep(StepType.GO_RIGHT, current.getValue(),
                    newNode.getValue() + " > " + current.getValue() + " -> Go right");
            current.setRight(insertRec(current.getRight(), newNode));
        }

        return current;
    }

    @Override
    public boolean delete(int value) {
        int initialSize = getNumberOfNodes();
        this.root = deleteRec(this.root, value);
        return getNumberOfNodes() < initialSize;
    }

    protected BinaryNode deleteRec(BinaryNode current, int value) {
        if (current == null) {
            fireStep(StepType.NOT_FOUND, value, "Cannot find node " + value + " to delete");
            return null;
        }

        fireStep(StepType.COMPARE, current.getValue(), "Compare " + value + " with " + current.getValue());
        if (value < current.getValue()) {
            fireStep(StepType.GO_LEFT, current.getValue(), value + " < " + current.getValue() + " -> Go left");
            current.setLeft(deleteRec(current.getLeft(), value));
            return current;
        }
        if (value > current.getValue()) {
            fireStep(StepType.GO_RIGHT, current.getValue(), value + " > " + current.getValue() + " -> Go right");
            current.setRight(deleteRec(current.getRight(), value));
            return current;
        }

        fireStep(StepType.FOUND, current.getValue(), "Found " + value + " to delete");
        if (current.getLeft() == null) {
            fireStep(StepType.DELETE_NODE, value, "Deleted node (no left child)");
            return current.getRight();
        }
        if (current.getRight() == null) {
            fireStep(StepType.DELETE_NODE, value, "Deleted node (no right child)");
            return current.getLeft();
        }

        fireStep(StepType.GO_RIGHT, current.getValue(), "Find successor: Traverse right branch");
        BinaryNode successor = findAndAnimateSuccessor(current.getRight());
        fireStep(StepType.REPLACE_VALUE, current.getValue(),
                "Replace " + current.getValue() + " with successor " + successor.getValue());
        current.setValue(successor.getValue());
        current.setRight(deleteMinimumSilentSearch(current.getRight()));
        return current;
    }

    protected BinaryNode findAndAnimateSuccessor(BinaryNode current) {
        fireStep(StepType.COMPARE, current.getValue(), "Inspecting " + current.getValue());
        if (current.getLeft() == null) {
            fireStep(StepType.FOUND, current.getValue(), "Found successor: " + current.getValue());
            return current;
        }
        fireStep(StepType.GO_LEFT, current.getValue(), "Go left to find min");
        return findAndAnimateSuccessor(current.getLeft());
    }

    protected BinaryNode deleteMinimumSilentSearch(BinaryNode current) {
        if (current.getLeft() == null) {
            fireStep(StepType.DELETE_NODE, current.getValue(), "Delete successor " + current.getValue());
            return current.getRight();
        }
        current.setLeft(deleteMinimumSilentSearch(current.getLeft()));
        return current;
    }

    protected BinaryNode deleteMinimum(BinaryNode current) {
        if (current.getLeft() == null) {
            fireStep(StepType.DELETE_NODE, current.getValue(), "Delete successor " + current.getValue());
            return current.getRight();
        }

        fireStep(StepType.GO_LEFT, current.getValue(), "Go left to find min");
        current.setLeft(deleteMinimum(current.getLeft()));
        return current;
    }

    protected BinaryNode minimum(BinaryNode node) {
        while (node.getLeft() != null) {
            node = node.getLeft();
        }
        return node;
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
                fireStep(StepType.FOUND, currentValue, "Node " + currentValue + " does not need to change");
                return true;
            }
            fireStep(StepType.NOT_FOUND, currentValue, "Node " + currentValue + " not found");
            return false;
        }
        if (!existsCurrent) {
            fireStep(StepType.NOT_FOUND, currentValue, "Node " + currentValue + " not found to update");
            return false;
        }
        if (existsNew) {
            fireStep(StepType.FOUND, newValue, "New value " + newValue + " already exists in the tree");
            return false;
        }

        delete(currentValue);
        return insert(newValue);
    }

    @Override
    public boolean search(int value) {
        return findNode(this.root, value) != null;
    }

    @Override
    protected BinaryNode findNode(BinaryNode current, int value) {
        if (current == null) {
            return null;
        }

        fireStep(StepType.COMPARE, current.getValue(), "Compare " + value + " with " + current.getValue());
        if (current.getValue() == value) {
            fireStep(StepType.FOUND, current.getValue(), "Found " + value);
            return current;
        }

        if (value < current.getValue()) {
            if (current.getLeft() == null) {
                fireStep(StepType.NOT_FOUND, current.getValue(), "Cannot find " + value + " (left branch empty)");
                return null;
            }
            fireStep(StepType.GO_LEFT, current.getValue(), "Go left");
            return findNode(current.getLeft(), value);
        }
        if (current.getRight() == null) {
            fireStep(StepType.NOT_FOUND, current.getValue(), "Cannot find " + value + " (right branch empty)");
            return null;
        }
        fireStep(StepType.GO_RIGHT, current.getValue(), "Go right");
        return findNode(current.getRight(), value);
    }

}
