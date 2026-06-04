package com.demo3.model.step;

import java.util.List;

public final class OperationStep {

    private final int index;
    private final int pseudocodeLine;
    private final String description;
    private final List<Integer> highlightedValues;
    private final List<Integer> outputValues;
    private final boolean treeChanged;

    public OperationStep(int index, int pseudocodeLine, String description,
                         List<Integer> highlightedValues, List<Integer> outputValues,
                         boolean treeChanged) {
        this.index = index;
        this.pseudocodeLine = pseudocodeLine;
        this.description = description;
        this.highlightedValues = List.copyOf(highlightedValues);
        this.outputValues = List.copyOf(outputValues);
        this.treeChanged = treeChanged;
    }

    public int getIndex() {
        return index;
    }

    public int getPseudocodeLine() {
        return pseudocodeLine;
    }

    public String getDescription() {
        return description;
    }

    public List<Integer> getHighlightedValues() {
        return highlightedValues;
    }

    public List<Integer> getOutputValues() {
        return outputValues;
    }

    public boolean isTreeChanged() {
        return treeChanged;
    }
}
