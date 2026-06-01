package com.visualization.view;

import com.visualization.model.VisualTree;
import com.visualization.view.render.NodeRenderer;
import com.visualization.view.render.EdgeRenderer;

public class TreeCanvas<T> {
    private NodeRenderer<T> nodeRenderer;
    private EdgeRenderer<T> edgeRenderer;

    public TreeCanvas(NodeRenderer<T> nodeRenderer, EdgeRenderer<T> edgeRenderer) {}

    public void draw(VisualTree tree, T graphicsContext) {}
    public void clear(T graphicsContext) {}
}
