package com.visualization.view.render;

import com.visualization.model.VisualEdge;
import com.visualization.model.VisualNode;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class DefaultEdgeRenderer implements EdgeRenderer {
    public DefaultEdgeRenderer() {}

    @Override
    public void render(GraphicsContext gc, VisualEdge edge) {
        if (!edge.isVisible()) return;

        VisualNode source = edge.getSource();
        VisualNode target = edge.getTarget();

        if (source == null || target == null || !source.isVisible() || !target.isVisible()) {
            return;
        }
        
        gc.setStroke(Color.web(edge.getColorHex()));
        gc.setLineWidth(2.0);
        gc.strokeLine(source.getX(), source.getY(), target.getX(), target.getY());
    }
}
