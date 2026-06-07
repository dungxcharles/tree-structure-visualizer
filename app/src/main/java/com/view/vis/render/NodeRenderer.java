package com.view.vis.render;

import com.view.vis.viewmodel.VisualNode;
import javafx.scene.canvas.GraphicsContext;

public interface NodeRenderer {
    void render(GraphicsContext gc, VisualNode node);
}
