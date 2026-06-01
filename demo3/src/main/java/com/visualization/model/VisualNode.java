package com.visualization.model;

public class VisualNode implements VisualElement {
    private String id;
    private String label;
    private double x;
    private double y;
    private String colorHex;
    private boolean visible;

    public VisualNode(String id, String label) {}

    public String getId() { return null; }
    public String getLabel() { return null; }
    public double getX() { return 0; }
    public void setX(double x) {}
    public double getY() { return 0; }
    public void setY(double y) {}
    public String getColorHex() { return null; }
    public void setColorHex(String colorHex) {}
    
    @Override
    public boolean isVisible() { return false; }
    @Override
    public void setVisible(boolean visible) {}
}
