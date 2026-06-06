package com.view.vis.render;

import com.model.vis.VisualNode;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class DefaultNodeRenderer implements NodeRenderer {
    private static final double RADIUS = 20.0;

    public DefaultNodeRenderer() {
    }

    @Override
    public void render(GraphicsContext gc, VisualNode node) {
        if (!node.isVisible())
            return;

        double x = node.getX();
        double y = node.getY();

        gc.setGlobalAlpha(node.getOpacity());

        gc.setFill(Color.web(node.getColorHex()));
        boolean isDark = com.controller.NavigationManager.getInstance().isDarkMode();
        gc.setStroke(isDark ? Color.WHITE : Color.BLACK);
        gc.setLineWidth(2.0);
        gc.fillOval(x - RADIUS, y - RADIUS, RADIUS * 2, RADIUS * 2);
        gc.strokeOval(x - RADIUS, y - RADIUS, RADIUS * 2, RADIUS * 2);

        String fillHex = node.getColorHex();
        if ("#333333".equals(fillHex) || "#000000".equals(fillHex)) {
            gc.setFill(Color.WHITE);
        } else {
            gc.setFill(Color.BLACK);
        }
        gc.setFont(new Font("System", 14));
        gc.fillText(node.getLabel(), x - 8, y + 5);
    }
}
