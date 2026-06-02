package com.visualization.view.render;

import com.visualization.model.VisualNode;
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

        // Draw node background
        gc.setFill(Color.web(node.getColorHex()));
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2.0);
        gc.fillOval(x - RADIUS, y - RADIUS, RADIUS * 2, RADIUS * 2);
        gc.strokeOval(x - RADIUS, y - RADIUS, RADIUS * 2, RADIUS * 2);

        // Draw label
        gc.setFill(Color.BLACK);
        gc.setFont(new Font("System", 14));
        gc.fillText(node.getLabel(), x - 8, y + 5);
    }
}
