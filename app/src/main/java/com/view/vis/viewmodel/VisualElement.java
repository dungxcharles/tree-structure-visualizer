package com.view.vis.viewmodel;

public interface VisualElement {
    boolean isVisible();

    void setVisible(boolean visible);

    double getOpacity();

    void setOpacity(double opacity);

    String getColorHex();

    void setColorHex(String colorHex);
}
