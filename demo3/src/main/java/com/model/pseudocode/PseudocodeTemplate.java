package com.model.pseudocode;

import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public final class PseudocodeTemplate {

    private final List<String> lines;
    private final Map<AlgorithmStep, Integer> lineByStep;

    public PseudocodeTemplate(String[] lines) {
        this.lines = List.of(lines);
        this.lineByStep = new EnumMap<>(AlgorithmStep.class);
    }

    public void addMap(AlgorithmStep step, int zeroBasedLine) {
        if (step == null) {
            throw new IllegalArgumentException("Algorithm step cannot be null.");
        }
        if (zeroBasedLine < 0 || zeroBasedLine >= lines.size()) {
            throw new IllegalArgumentException("Pseudocode line is out of range: " + zeroBasedLine);
        }
        lineByStep.put(step, zeroBasedLine);
    }

    public List<String> getLines() {
        return lines;
    }

    public Map<AlgorithmStep, Integer> getLineByStep() {
        return Collections.unmodifiableMap(lineByStep);
    }

    public int getZeroBasedLine(AlgorithmStep step, int fallbackLine) {
        return lineByStep.getOrDefault(step, fallbackLine);
    }

    public int getOneBasedLine(AlgorithmStep step, int fallbackLine) {
        return getZeroBasedLine(step, Math.max(0, fallbackLine - 1)) + 1;
    }
}
