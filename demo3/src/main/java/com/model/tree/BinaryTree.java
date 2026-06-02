package com.demo3.model.tree;

import com.demo3.model.node.BinaryNode;
import com.demo3.model.pseudocode.PseudocodeTemplate;
import com.demo3.model.step.TreeOperation;
import com.demo3.model.pseudocode.PseudocodeRepository;

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
        this.root = new BinaryNode(value);
    }

    @Override
    public boolean insert(int parentValue, int value) {
        if (isEmpty() || search(value)) {
            return false;
        }

        BinaryNode parentNode = findNode(this.root, parentValue);
        if (parentNode == null) {
            return false;
        }

        if (parentNode.getLeft() == null) {
            parentNode.setLeft(new BinaryNode(value));
            return true;
        }
        if (parentNode.getRight() == null) {
            parentNode.setRight(new BinaryNode(value));
            return true;
        }

        return false;
    }

    @Override
    public boolean delete(int value) {
        if (this.root == null) {
            return false;
        }
        if (this.root.getValue() == value) {
            this.root = null;
            return true;
        }

        return deleteSubtree(this.root, value);
    }

    private boolean deleteSubtree(BinaryNode current, int value) {
        if (current == null) {
            return false;
        }

        if (current.getLeft() != null && current.getLeft().getValue() == value) {
            current.setLeft(null);
            return true;
        }
        if (current.getRight() != null && current.getRight().getValue() == value) {
            current.setRight(null);
            return true;
        }

        return deleteSubtree(current.getLeft(), value) || deleteSubtree(current.getRight(), value);
    }

    @Override
    public boolean update(int currentValue, int newValue) {
        if (isEmpty() || (currentValue != newValue && search(newValue))) {
            return false;
        }

        BinaryNode node = findNode(this.root, currentValue);
        if (node == null) {
            return false;
        }

        node.setValue(newValue);
        return true;
    }

    @Override
    public boolean search(int value) {
        return findNode(this.root, value) != null;
    }

    protected BinaryNode findNode(BinaryNode current, int value) {
        if (current == null || current.getValue() == value) {
            return current;
        }

        BinaryNode leftResult = findNode(current.getLeft(), value);
        if (leftResult != null) {
            return leftResult;
        }

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
        }

        return result;
    }

    protected void inOrderRec(BinaryNode node, List<Integer> result) {
        if (node == null) {
            return;
        }

        inOrderRec(node.getLeft(), result);
        result.add(node.getValue());
        inOrderRec(node.getRight(), result);
    }

    protected void preOrderRec(BinaryNode node, List<Integer> result) {
        if (node == null) {
            return;
        }

        result.add(node.getValue());
        preOrderRec(node.getLeft(), result);
        preOrderRec(node.getRight(), result);
    }

    protected void postOrderRec(BinaryNode node, List<Integer> result) {
        if (node == null) {
            return;
        }

        postOrderRec(node.getLeft(), result);
        postOrderRec(node.getRight(), result);
        result.add(node.getValue());
    }

    protected void bfsTraverse(BinaryNode root, List<Integer> result) {
        Queue<BinaryNode> queue = new ArrayDeque<>();
        queue.add(root);

        while (!queue.isEmpty()) {
            BinaryNode current = queue.poll();
            result.add(current.getValue());

            if (current.getLeft() != null) {
                queue.add(current.getLeft());
            }
            if (current.getRight() != null) {
                queue.add(current.getRight());
            }
        }
    }

    @Override
    protected BinaryNode cloneSubtree(BinaryNode node) {
        if (node == null) {
            return null;
        }

        BinaryNode copy = new BinaryNode(node.getValue());
        copy.setLeft(cloneSubtree(node.getLeft()));
        copy.setRight(cloneSubtree(node.getRight()));
        return copy;
    }

    @Override
    protected List<Integer> getSearchPath(int value) {
        List<Integer> path = new ArrayList<>();
        fillDepthFirstPath(this.root, value, path);
        return path;
    }

    @Override
    protected PseudocodeTemplate getPseudocodeTemplate(TreeOperation operation, TraversalType traversalType) {
        if (operation == TreeOperation.INSERT) {
            return PseudocodeRepository.getBinaryInsert();
        }
        if (operation == TreeOperation.DELETE) {
            return PseudocodeRepository.getBinaryDelete();
        }
        if (operation == TreeOperation.SEARCH) {
            return PseudocodeRepository.getBinarySearch();
        }
        return super.getPseudocodeTemplate(operation, traversalType);
    }

    private boolean fillDepthFirstPath(BinaryNode node, int value, List<Integer> path) {
        if (node == null) {
            return false;
        }

        path.add(node.getValue());
        if (node.getValue() == value) {
            return true;
        }
        if (fillDepthFirstPath(node.getLeft(), value, path)) {
            return true;
        }
        return fillDepthFirstPath(node.getRight(), value, path);
    }
}
