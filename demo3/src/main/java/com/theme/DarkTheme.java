package com.theme;

import javafx.scene.paint.Color;

public class DarkTheme implements Theme {

    @Override
    public boolean isDarkMode() {
        return true;
    }

    @Override
    public String getStylesheetPath() {
        return "/com/view/dark-mode.css";
    }

    @Override
    public Color getCanvasEdgeColor(String originalColorHex) {
        if ("#000000".equals(originalColorHex)) {
            return Color.WHITE;
        }
        return Color.web(originalColorHex);
    }

    @Override
    public Color getNodeFillColor(String originalColorHex) {
        if ("#ffffff".equalsIgnoreCase(originalColorHex) || "#333333".equals(originalColorHex) || "#000000".equals(originalColorHex)) {
            return Color.web("#2b2b2b");
        }
        return Color.web(originalColorHex);
    }

    @Override
    public Color getNodeStrokeColor(String originalColorHex) {
        return Color.WHITE;
    }

    @Override
    public Color getNodeTextFillColor(String originalColorHex) {
        if ("#ffffff".equalsIgnoreCase(originalColorHex) || "#333333".equals(originalColorHex) || "#000000".equals(originalColorHex)) {
            return Color.WHITE;
        }
        if ("#ff0000".equalsIgnoreCase(originalColorHex)) {
            return Color.WHITE;
        }
        return Color.BLACK;
    }

    @Override
    public Color getCreditTitleColor() {
        return Color.web("#ecf0f1");
    }

    @Override
    public Color getCreditContentColor() {
        return Color.web("#bdc3c7");
    }

    @Override
    public Color getCreditDotInactiveColor() {
        return Color.web("#555555");
    }

    @Override
    public Color getCreditDotActiveColor() {
        return Color.web("#3498db");
    }
}
