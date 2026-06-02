package com.visualization.view.render;

import com.visualization.model.VisualEdge;
import javafx.scene.canvas.GraphicsContext;

public interface EdgeRenderer {
    void render(GraphicsContext gc, VisualEdge edge);
}
