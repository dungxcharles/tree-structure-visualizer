package com.visualization.view.render;

import com.visualization.model.VisualNode;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class DefaultNodeRenderer<T> implements NodeRenderer<T> {
    private static final double RADIUS = 20.0;

    public DefaultNodeRenderer() {
    }

    @Override
    public void render(T graphicsContext, VisualNode node) {

    }
}
