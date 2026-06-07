package com.view.vis.render;

import com.view.vis.model.VisualNode;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class DefaultNodeRenderer implements NodeRenderer {
    private static final double RADIUS = 20.0;

    @Override
    public void render(GraphicsContext gc, VisualNode node) {
        if (!node.isVisible())
            return;

        double x = node.getX();
        double y = node.getY();

        gc.setGlobalAlpha(node.getOpacity());

        boolean isDark = com.controller.NavigationManager.getInstance().isDarkMode();
        String fillHex = node.getColorHex();

        Color fill;
        Color textFill;

        if (isDark) {
            if ("#ffffff".equalsIgnoreCase(fillHex) || "#333333".equals(fillHex) || "#000000".equals(fillHex)) {
                // Standard/Black nodes match the grey canvas background
                fill = Color.web("#2b2b2b");
                textFill = Color.WHITE;
            } else {
                // Red nodes or active highlight/animation colors
                fill = Color.web(fillHex);
                if ("#ff0000".equalsIgnoreCase(fillHex)) {
                    textFill = Color.WHITE; // Red node -> white text
                } else {
                    textFill = Color.BLACK; // Highlight colors -> black text
                }
            }
        } else {
            fill = Color.web(fillHex);
            if ("#333333".equals(fillHex) || "#000000".equals(fillHex)) {
                textFill = Color.WHITE;
            } else {
                textFill = Color.BLACK;
            }
        }

        gc.setFill(fill);
        gc.setStroke(isDark ? Color.WHITE : Color.BLACK);
        gc.setLineWidth(2.0);
        gc.fillOval(x - RADIUS, y - RADIUS, RADIUS * 2, RADIUS * 2);
        gc.strokeOval(x - RADIUS, y - RADIUS, RADIUS * 2, RADIUS * 2);

        gc.setFill(textFill);
        gc.setFont(new Font("System", 14));
        gc.fillText(node.getLabel(), x - 8, y + 5);
    }
}
