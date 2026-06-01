package com.visualization.view.render;

import com.visualization.model.VisualNode;

public interface NodeRenderer<T> {
    void render(T graphicsContext, VisualNode node);
}
