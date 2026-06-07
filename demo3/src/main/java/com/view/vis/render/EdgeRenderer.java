package com.view.vis.render;

import com.view.vis.model.VisualEdge;
import javafx.scene.canvas.GraphicsContext;

public interface EdgeRenderer {
    void render(GraphicsContext gc, VisualEdge edge);
}
