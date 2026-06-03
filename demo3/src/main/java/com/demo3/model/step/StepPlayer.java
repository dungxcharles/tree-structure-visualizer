package com.demo3.model.step;

import com.demo3.model.node.Node;

public final class StepPlayer<N extends Node> {

    private final OperationTrace<N> trace;
    private int currentIndex;
    private boolean paused;

    public StepPlayer(OperationTrace<N> trace) {
        if (trace == null) {
            throw new IllegalArgumentException("Trace cannot be null.");
        }
        this.trace = trace;
        this.currentIndex = trace.getSteps().isEmpty() ? -1 : 0;
        this.paused = true;
    }

    public OperationTrace<N> getTrace() {
        return trace;
    }

    public OperationStep getCurrentStep() {
        if (currentIndex < 0 || currentIndex >= trace.getSteps().size()) {
            return null;
        }
        return trace.getSteps().get(currentIndex);
    }

    public OperationStep stepForward() {
        if (currentIndex < trace.getSteps().size() - 1) {
            currentIndex++;
        }
        return getCurrentStep();
    }

    public OperationStep stepBackward() {
        if (currentIndex > 0) {
            currentIndex--;
        }
        return getCurrentStep();
    }

    public void play() {
        paused = false;
    }

    public void pause() {
        paused = true;
    }

    public boolean isPaused() {
        return paused;
    }

    public boolean isAtBeginning() {
        return currentIndex <= 0;
    }

    public boolean isAtEnd() {
        return currentIndex >= trace.getSteps().size() - 1;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public int getProgressPercent() {
        return trace.getProgressPercent(currentIndex);
    }
}
