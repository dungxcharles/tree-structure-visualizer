package com.view.vis.viewmodel;

public class VisualEdge implements VisualElement {
    public enum ChildSide {
        LEFT,
        RIGHT,
        UNKNOWN
    }

    private VisualNode source;
    private VisualNode target;
    private String colorHex;
    private double opacity = 1.0;
    private double progress = 1.0;
    private double highlightProgress = 0.0;
    private String highlightColor = "#FF7F00";
    private ChildSide childSide = ChildSide.UNKNOWN;

    public VisualEdge(VisualNode source, VisualNode target) {
        this(source, target, ChildSide.UNKNOWN);
    }

    public VisualEdge(VisualNode source, VisualNode target, ChildSide childSide) {
        if (source == null || target == null) {
            throw new IllegalArgumentException("Source and target nodes cannot be null.");
        }
        this.source = source;
        this.target = target;
        this.childSide = childSide == null ? ChildSide.UNKNOWN : childSide;
        this.colorHex = "#000000";
    }

    public VisualNode getSource() {
        return this.source;
    }

    public VisualNode getTarget() {
        return this.target;
    }

    public ChildSide getChildSide() {
        return this.childSide;
    }

    public String getColorHex() {
        return this.colorHex;
    }

    public void setColorHex(String colorHex) {
        this.colorHex = colorHex;
    }

    public double getOpacity() {
        return this.opacity;
    }

    public void setOpacity(double opacity) {
        this.opacity = Math.max(0.0, Math.min(1.0, opacity));
    }

    public double getProgress() {
        return progress;
    }

    public void setProgress(double progress) {
        this.progress = Math.max(0.0, Math.min(1.0, progress));
    }

    public double getHighlightProgress() {
        return highlightProgress;
    }

    public void setHighlightProgress(double highlightProgress) {
        this.highlightProgress = Math.max(0.0, Math.min(1.0, highlightProgress));
    }

    public String getHighlightColor() {
        return highlightColor;
    }

    public void setHighlightColor(String highlightColor) {
        this.highlightColor = highlightColor;
    }
}
