package com.model.tree;

import com.model.node.BinaryNode;
import com.model.exception.NullParentException;
import com.model.exception.NullRootException;
import com.model.exception.TreeEmptyException;
import com.model.step.StepType;

import java.util.LinkedList;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class BinaryTree extends AbstractTree<BinaryNode> {

    @Override
    public void create(int value) {
        if (!isEmpty()) {
            return;
        }
        fireStep(StepType.INSERT_NODE, value, "Create root with value " + value);
        this.root = new BinaryNode(value);
    }

    @Override
    public boolean insert(int parentValue, int value) {
        if (this.isEmpty()) {
            throw new NullRootException("Cannot create a new node with parent when the root is null.");
        }

        BinaryNode parentNode = findNode(this.root, parentValue);

        if (parentNode == null) {
            throw new NullParentException("Cannot create a new node with parent when the parent is null.");
        }

        if (findNode(this.root, value) != null) {
            fireStep(StepType.COMPARE, value, "Value " + value + " already exists");
            return false;
        }

        if (parentNode.getLeft() == null) {
            fireStep(StepType.INSERT_NODE, value, "Add " + value + " as left child of " + parentValue);
            parentNode.setLeft(new BinaryNode(value));
            return true;
        }

        if (parentNode.getRight() == null) {
            fireStep(StepType.INSERT_NODE, value, "Add " + value + " as right child of " + parentValue);
            parentNode.setRight(new BinaryNode(value));
            return true;
        }

        fireStep(StepType.NOT_FOUND, parentValue, "Node " + parentValue + " already has 2 children");
        return false;
    }

    @Override
    public boolean delete(int value) {
        if (isEmpty()) {
            return false;
        }

        fireStep(StepType.COMPARE, this.root.getValue(), "Compare root with " + value);
        if (this.root.getValue() == value) {
            fireStep(StepType.DELETE_NODE, value, "Delete root " + value);
            this.root = null;
            return true;
        }

        return deleteNode(this.root, value);
    }

    private boolean deleteNode(BinaryNode current, int value) {
        if (current == null) {
            return false;
        }

        if (current.getLeft() != null) {
            fireStep(StepType.COMPARE, current.getLeft().getValue(), "Compare left child with " + value);
            if (current.getLeft().getValue() == value) {
                fireStep(StepType.DELETE_NODE, value, "Delete left child " + value + " of " + current.getValue());
                current.setLeft(null);
                return true;
            }
        }

        if (current.getRight() != null) {
            fireStep(StepType.COMPARE, current.getRight().getValue(), "Compare right child with " + value);
            if (current.getRight().getValue() == value) {
                fireStep(StepType.DELETE_NODE, value, "Delete right child " + value + " of " + current.getValue());
                current.setRight(null);
                return true;
            }
        }

        fireStep(StepType.GO_LEFT, current.getValue(), "Traverse left branch of " + current.getValue() + " to delete");
        if (deleteNode(current.getLeft(), value)) {
            return true;
        }

        fireStep(StepType.GO_RIGHT, current.getValue(), "Traverse right branch of " + current.getValue() + " to delete");
        return deleteNode(current.getRight(), value);
    }

    @Override
    public boolean update(int currentValue, int newValue) {
        if (isEmpty()) {
            return false;
        }
        if (currentValue != newValue && findNode(this.root, newValue) != null) {
            fireStep(StepType.COMPARE, newValue, "Replacement value " + newValue + " already exists");
            return false;
        }

        BinaryNode node = findNode(this.root, currentValue);
        if (node == null) {
            fireStep(StepType.NOT_FOUND, currentValue, "Node " + currentValue + " not found to update");
            return false;
        }

        fireStep(StepType.REPLACE_VALUE, currentValue, "Update value " + currentValue + " to " + newValue);
        node.setValue(newValue);
        return true;
    }

    @Override
    public boolean search(int value) {
        return findNode(this.root, value) != null;
    }

    protected BinaryNode findNode(BinaryNode current, int value) {
        if (current == null) {
            return null;
        }

        fireStep(StepType.COMPARE, current.getValue(), "Compare with " + current.getValue());
        if (current.getValue() == value) {
            fireStep(StepType.FOUND, current.getValue(), "Found " + current.getValue());
            return current;
        }

        fireStep(StepType.GO_LEFT, current.getValue(), "Find left branch of " + current.getValue());
        BinaryNode leftResult = findNode(current.getLeft(), value);
        if (leftResult != null) {
            return leftResult;
        }

        fireStep(StepType.GO_RIGHT, current.getValue(), "Find right branch of " + current.getValue());
        return findNode(current.getRight(), value);
    }

    @Override
    public int getHeight() {
        return getHeightRec(this.root);
    }

    protected int getHeightRec(BinaryNode node) {
        if (node == null) {
            return 0;
        }
        return 1 + Math.max(getHeightRec(node.getLeft()), getHeightRec(node.getRight()));
    }

    @Override
    public int getNumberOfNodes() {
        return countNodes(this.root);
    }

    protected int countNodes(BinaryNode node) {
        if (node == null) {
            return 0;
        }
        return 1 + countNodes(node.getLeft()) + countNodes(node.getRight());
    }

    @Override
    public List<Integer> traverse(TraversalType type) {
        if (type == null) {
            throw new IllegalArgumentException("Traversal type cannot be null.");
        }
        if (isEmpty()) {
            throw new TreeEmptyException("Cannot traverse the tree when there is no nodes.");
        }

        List<Integer> result = new ArrayList<>();

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
        }

        return result;
    }

    protected void inOrderRec(BinaryNode node, List<Integer> result) {
        if (node == null) {
            return;
        }

        if (node.getLeft() != null) {
            fireStep(StepType.GO_LEFT, node.getValue(), "In-order: Go to left branch of " + node.getValue());
        }
        inOrderRec(node.getLeft(), result);

        fireStep(StepType.VISIT, node.getValue(), "In-order: Visit node " + node.getValue());
        fireStep(StepType.ADD_TO_RESULT, node.getValue(), "Add " + node.getValue() + " to result list");
        result.add(node.getValue());

        if (node.getRight() != null) {
            fireStep(StepType.GO_RIGHT, node.getValue(), "In-order: Go to right branch of " + node.getValue());
        }
        inOrderRec(node.getRight(), result);
    }

    protected void preOrderRec(BinaryNode node, List<Integer> result) {
        if (node == null) {
            return;
        }

        fireStep(StepType.VISIT, node.getValue(), "Pre-order: Visit node " + node.getValue());
        fireStep(StepType.ADD_TO_RESULT, node.getValue(), "Add " + node.getValue() + " to result list");
        result.add(node.getValue());

        if (node.getLeft() != null) {
            fireStep(StepType.GO_LEFT, node.getValue(), "Pre-order: Go to left branch of " + node.getValue());
        }
        preOrderRec(node.getLeft(), result);

        if (node.getRight() != null) {
            fireStep(StepType.GO_RIGHT, node.getValue(), "Pre-order: Go to right branch of " + node.getValue());
        }
        preOrderRec(node.getRight(), result);
    }

    protected void postOrderRec(BinaryNode node, List<Integer> result) {
        if (node == null) {
            return;
        }

        if (node.getLeft() != null) {
            fireStep(StepType.GO_LEFT, node.getValue(), "Post-order: Go to left branch of " + node.getValue());
        }
        postOrderRec(node.getLeft(), result);

        if (node.getRight() != null) {
            fireStep(StepType.GO_RIGHT, node.getValue(), "Post-order: Go to right branch of " + node.getValue());
        }
        postOrderRec(node.getRight(), result);

        fireStep(StepType.VISIT, node.getValue(), "Post-order: Visit node " + node.getValue());
        fireStep(StepType.ADD_TO_RESULT, node.getValue(), "Add " + node.getValue() + " to result list");
        result.add(node.getValue());
    }

    protected void bfsTraverse(BinaryNode root, List<Integer> result) {
        Queue<BinaryNode> queue = new ArrayDeque<>();
        queue.add(root);

        while (!queue.isEmpty()) {
            BinaryNode current = queue.poll();
            fireStep(StepType.VISIT, current.getValue(), "BFS: Take node " + current.getValue() + " from Queue and visit");
            fireStep(StepType.ADD_TO_RESULT, current.getValue(),
                    "Add " + current.getValue() + " to result list");
            result.add(current.getValue());

            if (current.getLeft() != null) {
                fireStep(StepType.GO_LEFT, current.getValue(),
                        "Add left child " + current.getLeft().getValue() + " to Queue");
                queue.add(current.getLeft());
            }
            if (current.getRight() != null) {
                fireStep(StepType.GO_RIGHT, current.getValue(),
                        "Add right child " + current.getRight().getValue() + " to Queue");
                queue.add(current.getRight());
            }
        }
    }

}
