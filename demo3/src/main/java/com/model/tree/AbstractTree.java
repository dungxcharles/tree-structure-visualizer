package com.model.tree;

import com.model.node.Node;
import com.model.pseudocode.AlgorithmStep;
import com.model.pseudocode.PseudocodeTemplate;
import com.model.step.OperationStep;
import com.model.step.OperationTrace;
import com.model.step.TreeOperation;
import com.model.step.TreeSnapshot;
import com.model.pseudocode.PseudocodeRepository;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractTree<N extends Node> {

    protected N root;
    private final List<TreeSnapshot<N>> undoHistory;
    private final List<TreeSnapshot<N>> redoHistory;

    public AbstractTree() {
        this.root = null;
        this.undoHistory = new ArrayList<>();
        this.redoHistory = new ArrayList<>();
    }

    public N getRoot() {
        return this.root;
    }

    public boolean isEmpty() {
        return root == null;
    }

    // setRoot() : have to check if neccessary

    public void clear() {
        this.root = null;
    }

    public void createEmpty() {
        this.root = null;
    }

    public TreeSnapshot<N> snapshot() {
        return new TreeSnapshot<>(cloneSubtree(this.root), getHeight(), getNumberOfNodes());
    }

    public void restore(TreeSnapshot<N> snapshot) {
        if (snapshot == null) {
            throw new IllegalArgumentException("Snapshot cannot be null.");
        }
        this.root = cloneSubtree(snapshot.getRoot());
    }

    public boolean canUndo() {
        return !undoHistory.isEmpty();
    }

    public boolean canRedo() {
        return !redoHistory.isEmpty();
    }

    public void clearHistory() {
        undoHistory.clear();
        redoHistory.clear();
    }

    public OperationTrace<N> createEmptyWithSteps() {
        TreeSnapshot<N> before = snapshot();
        List<String> pseudocode = getPseudocode(TreeOperation.CREATE, null);
        List<OperationStep> steps = new ArrayList<>();
        steps.add(step(0, lineOf(TreeOperation.CREATE, null, AlgorithmStep.CHECK_NULL, 1),
                "Start a new empty tree.", List.of(), List.of(), false));

        boolean changed = !isEmpty();
        this.root = null;

        steps.add(step(1, lineOf(TreeOperation.CREATE, null, AlgorithmStep.UPDATE_UI, 2),
                changed ? "Remove the current root." : "Tree is already empty.",
                List.of(), List.of(), changed));
        if (changed) {
            recordMutation(before);
        }
        return trace(TreeOperation.CREATE, pseudocode, steps, true,
                changed ? "Created an empty tree." : "Tree was already empty.", before);
    }

    public OperationTrace<N> createWithSteps(int value) {
        TreeSnapshot<N> before = snapshot();
        List<String> pseudocode = getPseudocode(TreeOperation.CREATE, null);
        List<OperationStep> steps = new ArrayList<>();
        steps.add(step(0, lineOf(TreeOperation.CREATE, null, AlgorithmStep.CHECK_NULL, 1),
                "Check whether the tree is empty.", List.of(), List.of(), false));

        boolean wasEmpty = isEmpty();
        create(value);
        boolean success = wasEmpty && !isEmpty();
        steps.add(step(1, lineOf(TreeOperation.CREATE, null, AlgorithmStep.CREATE_NODE, 2),
                success ? "Create the root node." : "Tree already has a root.",
                success ? List.of(value) : List.of(), List.of(), success));

        if (success) {
            recordMutation(before);
        }
        return trace(TreeOperation.CREATE, pseudocode, steps, success,
                success ? "Created root " + value + "." : "Create failed because the tree is not empty.", before);
    }

    public OperationTrace<N> insertWithSteps(int parentValue, int value) {
        TreeSnapshot<N> before = snapshot();
        List<String> pseudocode = getPseudocode(TreeOperation.INSERT, null);
        List<OperationStep> steps = stepsForPath(getInsertPath(parentValue, value),
                lineOf(TreeOperation.INSERT, null, AlgorithmStep.SEARCH_START, 1), "Visit node ");

        boolean success = insert(parentValue, value);
        steps.add(step(steps.size(), success
                        ? lineOf(TreeOperation.INSERT, null, AlgorithmStep.INSERT_HERE, 3)
                        : lineOf(TreeOperation.INSERT, null, AlgorithmStep.SEARCH_CHECK_NULL, 4),
                success ? "Insert node " + value + "." : "Insert failed.",
                success ? List.of(value) : List.of(), List.of(), success));

        if (success) {
            recordMutation(before);
        }
        return trace(TreeOperation.INSERT, pseudocode, steps, success,
                success ? "Inserted " + value + "." : "Insert failed.", before);
    }

    public OperationTrace<N> deleteWithSteps(int value) {
        TreeSnapshot<N> before = snapshot();
        List<String> pseudocode = getPseudocode(TreeOperation.DELETE, null);
        List<OperationStep> steps = stepsForPath(getDeletePath(value),
                lineOf(TreeOperation.DELETE, null, AlgorithmStep.DEL_SEARCH, 1), "Visit node ");

        boolean success = delete(value);
        steps.add(step(steps.size(), success
                        ? lineOf(TreeOperation.DELETE, null, AlgorithmStep.DEL_FOUND, 3)
                        : lineOf(TreeOperation.DELETE, null, AlgorithmStep.DEL_NOT_FOUND, 4),
                success ? "Delete node " + value + "." : "Delete failed: node not found.",
                success ? List.of(value) : List.of(), List.of(), success));

        if (success) {
            recordMutation(before);
        }
        return trace(TreeOperation.DELETE, pseudocode, steps, success,
                success ? "Deleted " + value + "." : "Node " + value + " was not found.", before);
    }

    public OperationTrace<N> updateWithSteps(int currentValue, int newValue) {
        TreeSnapshot<N> before = snapshot();
        List<String> pseudocode = getPseudocode(TreeOperation.UPDATE, null);
        List<OperationStep> steps = stepsForPath(getUpdatePath(currentValue, newValue),
                lineOf(TreeOperation.UPDATE, null, AlgorithmStep.SEARCH_START, 1), "Visit node ");

        boolean success = update(currentValue, newValue);
        steps.add(step(steps.size(), success
                        ? lineOf(TreeOperation.UPDATE, null, AlgorithmStep.UPDATE_UI, 4)
                        : lineOf(TreeOperation.UPDATE, null, AlgorithmStep.SEARCH_CHECK_NULL, 2),
                success ? "Update " + currentValue + " to " + newValue + "." : "Update failed.",
                success ? List.of(newValue) : List.of(), List.of(), success));

        if (success) {
            recordMutation(before);
        }
        return trace(TreeOperation.UPDATE, pseudocode, steps, success,
                success ? "Updated " + currentValue + " to " + newValue + "." : "Update failed.", before);
    }

    public OperationTrace<N> searchWithSteps(int value) {
        TreeSnapshot<N> before = snapshot();
        List<String> pseudocode = getPseudocode(TreeOperation.SEARCH, null);
        List<Integer> path = getSearchPath(value);
        List<OperationStep> steps = stepsForPath(path,
                lineOf(TreeOperation.SEARCH, null, AlgorithmStep.SEARCH_COMPARE, 1), "Visit node ");
        boolean success = search(value);

        steps.add(step(steps.size(), success
                        ? lineOf(TreeOperation.SEARCH, null, AlgorithmStep.FOUND, 3)
                        : lineOf(TreeOperation.SEARCH, null, AlgorithmStep.CHECK_NULL, 4),
                success ? "Found node " + value + "." : "Node " + value + " was not found.",
                success ? List.of(value) : List.of(), List.of(), false));
        return trace(TreeOperation.SEARCH, pseudocode, steps, success,
                success ? "Found " + value + "." : "Node " + value + " was not found.", before);
    }

    public OperationTrace<N> traverseWithSteps(TraversalType type) {
        TreeSnapshot<N> before = snapshot();
        List<String> pseudocode = getPseudocode(TreeOperation.TRAVERSE, type);
        List<Integer> output = traverse(type);
        List<OperationStep> steps = new ArrayList<>();

        for (int i = 0; i < output.size(); i++) {
            int value = output.get(i);
            steps.add(step(i, lineOf(TreeOperation.TRAVERSE, type, AlgorithmStep.TRAVERSE_PRINT, 2),
                    "Visit node " + value + ".",
                    List.of(value), output.subList(0, i + 1), false));
        }
        if (steps.isEmpty()) {
            steps.add(step(0, lineOf(TreeOperation.TRAVERSE, type, AlgorithmStep.TRAVERSE_CHECK_NULL, 1),
                    "Tree is empty.", List.of(), List.of(), false));
        }

        return trace(TreeOperation.TRAVERSE, pseudocode, steps, true,
                "Traversal completed.", before);
    }

    public OperationTrace<N> undoWithSteps() {
        TreeSnapshot<N> before = snapshot();
        List<String> pseudocode = List.of("if undo stack is empty: stop",
                "push current tree to redo stack", "restore previous tree");
        List<OperationStep> steps = new ArrayList<>();

        if (!canUndo()) {
            steps.add(step(0, 1, "Undo stack is empty.", List.of(), List.of(), false));
            return trace(TreeOperation.UNDO, pseudocode, steps, false, "Nothing to undo.", before);
        }

        TreeSnapshot<N> previous = undoHistory.remove(undoHistory.size() - 1);
        redoHistory.add(before);
        restore(previous);
        steps.add(step(0, 2, "Save current tree for redo.", List.of(), List.of(), false));
        steps.add(step(1, 3, "Restore previous tree snapshot.", List.of(), List.of(), true));
        return trace(TreeOperation.UNDO, pseudocode, steps, true, "Undo completed.", before);
    }

    public OperationTrace<N> redoWithSteps() {
        TreeSnapshot<N> before = snapshot();
        List<String> pseudocode = List.of("if redo stack is empty: stop",
                "push current tree to undo stack", "restore next tree");
        List<OperationStep> steps = new ArrayList<>();

        if (!canRedo()) {
            steps.add(step(0, 1, "Redo stack is empty.", List.of(), List.of(), false));
            return trace(TreeOperation.REDO, pseudocode, steps, false, "Nothing to redo.", before);
        }

        TreeSnapshot<N> next = redoHistory.remove(redoHistory.size() - 1);
        undoHistory.add(before);
        restore(next);
        steps.add(step(0, 2, "Save current tree for undo.", List.of(), List.of(), false));
        steps.add(step(1, 3, "Restore next tree snapshot.", List.of(), List.of(), true));
        return trace(TreeOperation.REDO, pseudocode, steps, true, "Redo completed.", before);
    }

    public abstract void create(int value);

    public abstract boolean insert(int parentValue, int value);

    public abstract boolean delete(int value);

    public abstract boolean update(int currentValue, int newValue);

    public abstract List<Integer> traverse(TraversalType type);

    public abstract boolean search(int value);

    public abstract int getHeight();

    public abstract int getNumberOfNodes();

    protected abstract N cloneSubtree(N node);

    protected List<Integer> getSearchPath(int value) {
        return List.of();
    }

    protected List<Integer> getInsertPath(int parentValue, int value) {
        return getSearchPath(parentValue);
    }

    protected List<Integer> getDeletePath(int value) {
        return getSearchPath(value);
    }

    protected List<Integer> getUpdatePath(int currentValue, int newValue) {
        return getSearchPath(currentValue);
    }

    protected List<String> getPseudocode(TreeOperation operation, TraversalType traversalType) {
        return getPseudocodeTemplate(operation, traversalType).getLines();
    }

    protected PseudocodeTemplate getPseudocodeTemplate(TreeOperation operation, TraversalType traversalType) {
        switch (operation) {
            case CREATE:
                return PseudocodeRepository.getCreate();
            case INSERT:
                return PseudocodeRepository.getBinaryInsert();
            case DELETE:
                return PseudocodeRepository.getBinaryDelete();
            case UPDATE:
                return PseudocodeRepository.getUpdate();
            case SEARCH:
                return PseudocodeRepository.getBinarySearch();
            case TRAVERSE:
                return getTraversalPseudocodeTemplate(traversalType);
            default:
                return PseudocodeRepository.getCreate();
        }
    }

    protected PseudocodeTemplate getTraversalPseudocodeTemplate(TraversalType traversalType) {
        if (traversalType == TraversalType.BFS) {
            return PseudocodeRepository.getBFS();
        }
        if (traversalType == TraversalType.IN_ORDER) {
            return PseudocodeRepository.getInOrder();
        }
        if (traversalType == TraversalType.POST_ORDER) {
            return PseudocodeRepository.getPostOrder();
        }
        return PseudocodeRepository.getPreOrder();
    }

    private int lineOf(TreeOperation operation, TraversalType traversalType,
                       AlgorithmStep algoStep, int fallbackLine) {
        return getPseudocodeTemplate(operation, traversalType).getOneBasedLine(algoStep, fallbackLine);
    }

    private void recordMutation(TreeSnapshot<N> before) {
        undoHistory.add(before);
        redoHistory.clear();
    }

    private OperationTrace<N> trace(TreeOperation operation, List<String> pseudocode,
                                    List<OperationStep> steps, boolean success,
                                    String message, TreeSnapshot<N> before) {
        return new OperationTrace<>(operation, pseudocode, steps, success, message, before, snapshot());
    }

    private List<OperationStep> stepsForPath(List<Integer> path, int line, String prefix) {
        List<OperationStep> steps = new ArrayList<>();
        for (int i = 0; i < path.size(); i++) {
            int value = path.get(i);
            steps.add(step(i, line, prefix + value + ".", List.of(value), List.of(), false));
        }
        if (steps.isEmpty()) {
            steps.add(step(0, line, "No node is visited.", List.of(), List.of(), false));
        }
        return steps;
    }

    private OperationStep step(int index, int pseudocodeLine, String description,
                               List<Integer> highlightedValues, List<Integer> outputValues,
                               boolean treeChanged) {
        return new OperationStep(index, pseudocodeLine, description,
                highlightedValues, outputValues, treeChanged);
    }

}
