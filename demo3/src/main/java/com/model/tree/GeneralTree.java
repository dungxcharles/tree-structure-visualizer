package com.model.tree;

import com.model.node.GenericNode;
import com.model.step.StepType;

import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class GeneralTree extends AbstractTree<GenericNode> {

    @Override
    public void create(int value) {
        if (!isEmpty()) {
            return;
        }
        fireStep(StepType.INSERT_NODE, value, "Create root with value " + value);
        this.root = new GenericNode(value);
    }

    @Override
    public boolean insert(int parentValue, int childValue) {
        if (this.isEmpty()) {
            throw new NullRootException("Attempting to create a new node with parent when the root is null.");
        }

        GenericNode parentNode = findNode(this.root, parentValue);

        if (parentNode == null) {
            fireStep(StepType.NOT_FOUND, parentValue, "Parent " + parentValue + " not found");
            return false;
        }

        if (findNode(this.root, childValue) != null) {
            fireStep(StepType.COMPARE, childValue, "Value " + childValue + " already exists");
            return false;
        }

        fireStep(StepType.ADD_CHILD, childValue, "Add child " + childValue + " to parent " + parentValue);
        return parentNode.addChild(new GenericNode(childValue));
    }

    private GenericNode findNode(GenericNode current, int value) {
        if (current == null)
            return null;
            
        fireStep(StepType.COMPARE, current.getValue(), "Compare with " + current.getValue());
        if (current.getValue() == value) {
            fireStep(StepType.FOUND, current.getValue(), "Found " + current.getValue());
            return current;
        }

        fireStep(StepType.ITERATE_CHILDREN, current.getValue(), "Iterate children of " + current.getValue());
        for (GenericNode child : current.getChildren()) {
            GenericNode found = findNode(child, value);
            if (found != null)
                return found;
        }
        return null;
    }

    @Override
    public boolean delete(int value) {
        if (isEmpty())
            return false;
            
        fireStep(StepType.COMPARE, this.root.getValue(), "Compare root with " + value);
        if (this.root.getValue() == value) {
            fireStep(StepType.DELETE_NODE, value, "Delete root " + value);
            this.root = null;
            return true;
        }
        return deleteNode(this.root, value);
    }

    private boolean deleteNode(GenericNode current, int value) {
        fireStep(StepType.ITERATE_CHILDREN, current.getValue(), "Iterate children of " + current.getValue() + " to find and delete " + value);
        for (GenericNode child : current.getChildren()) {
            fireStep(StepType.COMPARE, child.getValue(), "Compare child with " + value);
            if (child.getValue() == value) {
                fireStep(StepType.REMOVE_CHILD, value, "Delete child " + value + " from " + current.getValue());
                current.removeChild(child);
                return true;
            }
            if (deleteNode(child, value)) {
                return true;
            }
        }
        return false;
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

        GenericNode node = findNode(this.root, currentValue);
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

    @Override
    public int getHeight() {
        return getTreeHeight(this.root);
    }

    private int getTreeHeight(GenericNode node) {
        if (node == null)
            return 0;
        int maxChildHeight = 0;
        for (GenericNode child : node.getChildren()) {
            maxChildHeight = Math.max(maxChildHeight, getTreeHeight(child));
        }
        return 1 + maxChildHeight;
    }

    // provide an interface for user not to pass the node root
    @Override
    public int getNumberOfNodes() {
        return countNodes(this.root);
    }

    private int countNodes(GenericNode node) {
        if (node == null)
            return 0;
        int count = 1;
        for (GenericNode child : node.getChildren()) {
            count += countNodes(child);
        }
        return count;
    }

    @Override
    public List<Integer> traverse(TraversalType type) {
        if (type == null) {
            throw new IllegalArgumentException("Traversal type cannot be null.");
        }
        if (isEmpty()) {
            throw new TreeEmptyException("Attempting to traverse the tree when there is no nodes.");
        }

        List<Integer> result = new ArrayList<>();

        switch (type) {
            case PRE_ORDER:
                preOrderTraverse(this.root, result);
                break;
            case IN_ORDER:
                throw new UnsupportedOperationException(
                        "In-order traversal is not defined for a general tree");
            case POST_ORDER:
                postOrderTraverse(this.root, result);
                break;
            case BFS:
                bfsTraverse(this.root, result);
                break;
        }
        return result;
    }

    private void preOrderTraverse(GenericNode node, List<Integer> result) {
        if (node == null)
            return;
            
        fireStep(StepType.VISIT, node.getValue(), "Pre-order: Visit node " + node.getValue());
        fireStep(StepType.ADD_TO_RESULT, node.getValue(), "Add " + node.getValue() + " to result list");
        result.add(node.getValue());
        
        fireStep(StepType.ITERATE_CHILDREN, node.getValue(), "Start iterating children of " + node.getValue());
        for (GenericNode child : node.getChildren()) {
            fireStep(StepType.GO_CHILD, child.getValue(), "Go down to child branch " + child.getValue());
            preOrderTraverse(child, result);
        }
    }

    private void postOrderTraverse(GenericNode node, List<Integer> result) {
        if (node == null)
            return;
            
        fireStep(StepType.ITERATE_CHILDREN, node.getValue(), "Start iterating children of " + node.getValue());
        for (GenericNode child : node.getChildren()) {
            fireStep(StepType.GO_CHILD, child.getValue(), "Go down to child branch " + child.getValue());
            postOrderTraverse(child, result);
        }
        
        fireStep(StepType.VISIT, node.getValue(), "Post-order: Visit node " + node.getValue());
        fireStep(StepType.ADD_TO_RESULT, node.getValue(), "Add " + node.getValue() + " to result list");
        result.add(node.getValue());
    }

    private void bfsTraverse(GenericNode root, List<Integer> result) {
        if (root == null)
            return;
        Queue<GenericNode> queue = new LinkedList<>();
        queue.add(root);

        while (!queue.isEmpty()) {
            GenericNode current = queue.poll();
            fireStep(StepType.VISIT, current.getValue(), "BFS: Take node " + current.getValue() + " from Queue and visit");
            fireStep(StepType.ADD_TO_RESULT, current.getValue(), "Add " + current.getValue() + " to result list");
            result.add(current.getValue());
            
            if (!current.getChildren().isEmpty()) {
                fireStep(StepType.ITERATE_CHILDREN, current.getValue(), "Add children of " + current.getValue() + " to Queue");
                for (GenericNode child : current.getChildren()) {
                    fireStep(StepType.GO_CHILD, child.getValue(), "Add child " + child.getValue() + " to Queue");
                    queue.add(child);
                }
            }
        }
    }

    // delete will erase the subtree
}
