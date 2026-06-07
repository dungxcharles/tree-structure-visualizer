package com.view.vis.render;

import com.theme.ThemeManager;
import com.view.vis.model.VisualEdge;
import com.view.vis.model.VisualNode;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class DefaultEdgeRenderer implements EdgeRenderer {

    @Override
    public void render(GraphicsContext gc, VisualEdge edge) {
        if (!edge.isVisible())
            return;

        VisualNode source = edge.getSource();
        VisualNode target = edge.getTarget();

        if (source == null || target == null || !source.isVisible() || !target.isVisible()) {
            return;
        }

        gc.setGlobalAlpha(edge.getOpacity());

        double startX = source.getX();
        double startY = source.getY();
        double targetX = target.getX();
        double targetY = target.getY();

        double currentX = startX + (targetX - startX) * edge.getProgress();
        double currentY = startY + (targetY - startY) * edge.getProgress();

        String colorHex = edge.getColorHex();
        gc.setStroke(ThemeManager.getInstance().getCurrentTheme().getCanvasEdgeColor(colorHex));
        gc.setLineWidth(2.0);
        gc.strokeLine(startX, startY, currentX, currentY);

        if (edge.getHighlightProgress() > 0.0) {
            gc.setStroke(Color.web(edge.getHighlightColor()));
            gc.setLineWidth(3.0);
            double hX = startX + (targetX - startX) * edge.getHighlightProgress();
            double hY = startY + (targetY - startY) * edge.getHighlightProgress();
            gc.strokeLine(startX, startY, hX, hY);
        }
    }
}
