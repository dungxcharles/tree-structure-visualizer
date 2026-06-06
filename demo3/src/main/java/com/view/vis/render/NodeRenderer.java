package com.view.vis.render;

import com.model.vis.VisualNode;
import javafx.scene.canvas.GraphicsContext;

public interface NodeRenderer {
    void render(GraphicsContext gc, VisualNode node);
}
