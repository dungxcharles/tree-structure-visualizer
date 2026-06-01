package com.visualization.model;

public class VisualEdge implements VisualElement {
    private VisualNode source;
    private VisualNode target;
    private String colorHex;
    private boolean visible;

    public VisualEdge(VisualNode source, VisualNode target) {}

    public VisualNode getSource() { return null; }
    public VisualNode getTarget() { return null; }
    public String getColorHex() { return null; }
    public void setColorHex(String colorHex) {}

    @Override
    public boolean isVisible() { return false; }
    @Override
    public void setVisible(boolean visible) {}
}
