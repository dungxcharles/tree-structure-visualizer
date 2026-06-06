package com.view.vis.render;

import com.model.vis.VisualEdge;
import javafx.scene.canvas.GraphicsContext;

public interface EdgeRenderer {
    void render(GraphicsContext gc, VisualEdge edge);
}
