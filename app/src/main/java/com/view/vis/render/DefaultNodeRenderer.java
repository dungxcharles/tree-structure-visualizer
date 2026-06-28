package com.view.vis.render;

import com.theme.Theme;
import com.theme.ThemeManager;
import com.view.vis.viewmodel.VisualNode;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class DefaultNodeRenderer implements NodeRenderer {
    private static final double RADIUS = 20.0;

    @Override
    public void render(GraphicsContext gc, VisualNode node) {
        double x = node.getX();
        double y = node.getY();

        gc.setGlobalAlpha(node.getOpacity());

        Theme theme = ThemeManager.getInstance().getCurrentTheme();
        String fillHex = node.getColorHex();

        Color fill = theme.getNodeFillColor(fillHex);
        Color stroke = theme.getNodeStrokeColor(fillHex);
        Color textFill = theme.getNodeTextFillColor(fillHex);

        gc.setFill(fill);
        gc.setStroke(stroke);
        gc.setLineWidth(2.0);
        gc.fillOval(x - RADIUS, y - RADIUS, RADIUS * 2, RADIUS * 2);
        gc.strokeOval(x - RADIUS, y - RADIUS, RADIUS * 2, RADIUS * 2);

        gc.setFill(textFill);
        gc.setFont(new Font("System", 14));
        gc.fillText(node.getLabel(), x - 8, y + 5);
    }
}
