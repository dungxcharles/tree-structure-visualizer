package com.theme;

import javafx.scene.paint.Color;

public class LightTheme implements Theme {

    @Override
    public boolean isDarkMode() {
        return false;
    }

    @Override
    public String getStylesheetPath() {
        return "/com/view/css/base-style.css";
    }

    @Override
    public Color getCanvasEdgeColor(String originalColorHex) {
        return Color.web(originalColorHex);
    }

    @Override
    public Color getNodeFillColor(String originalColorHex) {
        return Color.web(originalColorHex);
    }

    @Override
    public Color getNodeStrokeColor(String originalColorHex) {
        return Color.BLACK;
    }

    @Override
    public Color getNodeTextFillColor(String originalColorHex) {
        if ("#333333".equals(originalColorHex) || "#000000".equals(originalColorHex)) {
            return Color.WHITE;
        }
        return Color.BLACK;
    }

    @Override
    public Color getCreditTitleColor() {
        return Color.web("#2c3e50");
    }

    @Override
    public Color getCreditContentColor() {
        return Color.web("#34495e");
    }

    @Override
    public Color getCreditDotInactiveColor() {
        return Color.web("#bdc3c7");
    }

    @Override
    public Color getCreditDotActiveColor() {
        return Color.web("#3498db");
    }
}
