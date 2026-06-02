package com.model.step;

import com.model.node.Node;

import java.util.List;

public final class OperationTrace<N extends Node> {

    private final TreeOperation operation;
    private final List<String> pseudocode;
    private final List<OperationStep> steps;
    private final boolean success;
    private final String message;
    private final TreeSnapshot<N> beforeSnapshot;
    private final TreeSnapshot<N> afterSnapshot;

    public OperationTrace(TreeOperation operation, List<String> pseudocode,
                          List<OperationStep> steps, boolean success, String message,
                          TreeSnapshot<N> beforeSnapshot, TreeSnapshot<N> afterSnapshot) {
        this.operation = operation;
        this.pseudocode = List.copyOf(pseudocode);
        this.steps = List.copyOf(steps);
        this.success = success;
        this.message = message;
        this.beforeSnapshot = beforeSnapshot;
        this.afterSnapshot = afterSnapshot;
    }

    public TreeOperation getOperation() {
        return operation;
    }

    public List<String> getPseudocode() {
        return pseudocode;
    }

    public List<OperationStep> getSteps() {
        return steps;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public TreeSnapshot<N> getBeforeSnapshot() {
        return beforeSnapshot;
    }

    public TreeSnapshot<N> getAfterSnapshot() {
        return afterSnapshot;
    }

    public int getProgressPercent(int stepIndex) {
        if (steps.isEmpty()) {
            return 100;
        }
        int completedSteps = Math.max(0, Math.min(stepIndex + 1, steps.size()));
        return completedSteps * 100 / steps.size();
    }

    public StepPlayer<N> createPlayer() {
        return new StepPlayer<>(this);
    }
}
