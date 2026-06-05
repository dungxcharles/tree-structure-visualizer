package com.model.vis;

public class VisualNode implements VisualElement {
    private String id;
    private String label;
    private double x;
    private double y;
    private String colorHex;
    private boolean visible;
    private double opacity = 1.0;

    public VisualNode(String id, String label) {
        this.id = id;
        this.label = label;
        this.visible = true;
        this.colorHex = "#ffffff";
    }

    public String getId() { return this.id; }
    public String getLabel() { return this.label; }
    public void setLabel(String label) { this.label = label; }
    public double getX() { return this.x; }
    public void setX(double x) { this.x = x; }
    public double getY() { return this.y; }
    public void setY(double y) { this.y = y; }
    public String getColorHex() { return this.colorHex; }
    public void setColorHex(String colorHex) { this.colorHex = colorHex; }
    public double getOpacity() { return this.opacity; }
    public void setOpacity(double opacity) { this.opacity = Math.max(0.0, Math.min(1.0, opacity)); }
    
    @Override
    public boolean isVisible() { return this.visible; }
    @Override
    public void setVisible(boolean visible) { this.visible = visible; }
}
