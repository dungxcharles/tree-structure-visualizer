package com.view.vis.render;

import com.view.vis.model.VisualNode;
import javafx.scene.canvas.GraphicsContext;

public interface NodeRenderer {
    void render(GraphicsContext gc, VisualNode node);
}
