package com.visualization.view.render;

import com.visualization.model.VisualNode;
import javafx.scene.canvas.GraphicsContext;

public interface NodeRenderer {
    void render(GraphicsContext gc, VisualNode node);
}
