package com.theme;

import javafx.scene.paint.Color;
import com.view.vis.model.VisualNode;

public class DarkTheme implements Theme {

    @Override
    public boolean isDarkMode() {
        return true;
    }

    @Override
    public String getStylesheetPath() {
        return "/com/view/css/dark-mode.css";
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
        if (VisualNode.COLOR_DEFAULT.equalsIgnoreCase(originalColorHex) || VisualNode.COLOR_RED_BLACK_BLACK.equals(originalColorHex) || "#000000".equals(originalColorHex)) {
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
        if (VisualNode.COLOR_DEFAULT.equalsIgnoreCase(originalColorHex) || VisualNode.COLOR_RED_BLACK_BLACK.equals(originalColorHex) || "#000000".equals(originalColorHex)) {
            return Color.WHITE;
        }
        if (VisualNode.COLOR_RED_BLACK_RED.equalsIgnoreCase(originalColorHex)) {
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
