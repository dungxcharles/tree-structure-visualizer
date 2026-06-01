package com.visualization.view.render;

import com.visualization.model.VisualEdge;

public interface EdgeRenderer<T> {
    void render(T graphicsContext, VisualEdge edge);
}
