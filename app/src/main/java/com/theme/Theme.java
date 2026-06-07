package com.theme;

import javafx.scene.paint.Color;

public interface Theme {
    boolean isDarkMode();
    String getStylesheetPath();
    Color getCanvasEdgeColor(String originalColorHex);
    Color getNodeFillColor(String originalColorHex);
    Color getNodeStrokeColor(String originalColorHex);
    Color getNodeTextFillColor(String originalColorHex);
    Color getCreditTitleColor();
    Color getCreditContentColor();
    Color getCreditDotInactiveColor();
    Color getCreditDotActiveColor();
}
