package com.model.vis;

public class VisualEdge implements VisualElement {
    private VisualNode source;
    private VisualNode target;
    private String colorHex;
    private boolean visible;

    public VisualEdge(VisualNode source, VisualNode target) {
        if (source == null || target == null) {
            throw new IllegalArgumentException("Source and target nodes cannot be null.");
        }
        this.source = source;
        this.target = target;
        this.visible = true;
        this.colorHex = "#000000";
    }

    public VisualNode getSource() { return this.source; }
    public VisualNode getTarget() { return this.target; }
    public String getColorHex() { return this.colorHex; }
    public void setColorHex(String colorHex) { this.colorHex = colorHex; }

    @Override
    public boolean isVisible() { return this.visible; }
    @Override
    public void setVisible(boolean visible) { this.visible = visible; }
}
