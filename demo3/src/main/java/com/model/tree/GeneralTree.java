package com.model.tree;


import com.model.node.GenericNode;

import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class GeneralTree extends AbstractTree<GenericNode> {

    @Override
    public void create(int value) {
        if (!isEmpty()) {
            return;
            // hoặc throw new IllegalArgumentException("Tree is not empty.");
        }
        this.root = new GenericNode(value);
    }

    @Override
    public boolean insert(int parentValue, int childValue) {
        if (this.isEmpty()) {
            return false;
            // hoặc throw new IllegalArgumentException("Tree is empty. Create one first.");
        }

        GenericNode parentNode = findNode(this.root, parentValue);

        if (parentNode == null) {
            return false;
            // hoặc throw new IllegalArgumentException("Parent node with value " +
            // parentValue + " not found.");
        }

        if (findNode(this.root, childValue) != null) {
            return false;
            // hoặc throw new IllegalArgumentException("Node with value " + childValue + "
            // already exists.");
        }

        parentNode.addChild(new GenericNode(childValue));
        return true;
    }

    private GenericNode findNode(GenericNode current, int value) {
        if (current == null)
            return null;
        if (current.getValue() == value)
            return current;

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
        if (this.root.getValue() == value) {
            this.root = null;
            return true;
        }
        return deleteNode(this.root, value);
    }

    private boolean deleteNode(GenericNode current, int value) {
        for (GenericNode child : current.getChildren()) {
            if (child.getValue() == value) {
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
        List<Integer> result = new ArrayList<>();
        if (isEmpty())
            return result;

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
        result.add(node.getValue());
        for (GenericNode child : node.getChildren()) {
            preOrderTraverse(child, result);
        }
    }

    private void postOrderTraverse(GenericNode node, List<Integer> result) {
        if (node == null)
            return;
        for (GenericNode child : node.getChildren()) {
            postOrderTraverse(child, result);
        }
        result.add(node.getValue());
    }

    private void bfsTraverse(GenericNode root, List<Integer> result) {
        if (root == null)
            return;
        Queue<GenericNode> queue = new LinkedList<>();
        queue.add(root);

        while (!queue.isEmpty()) {
            GenericNode current = queue.poll();
            result.add(current.getValue());
            queue.addAll(current.getChildren());
        }
    }
}