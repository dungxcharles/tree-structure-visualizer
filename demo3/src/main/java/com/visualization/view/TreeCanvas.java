package com.visualization.view;

import com.visualization.model.VisualEdge;
import com.visualization.model.VisualNode;
import com.visualization.model.VisualTree;
import com.visualization.view.render.EdgeRenderer;
import com.visualization.view.render.NodeRenderer;
import javafx.scene.canvas.GraphicsContext;

public class TreeCanvas {
    private NodeRenderer<GraphicsContext> nodeRenderer;
    private EdgeRenderer<GraphicsContext> edgeRenderer;

    public TreeCanvas(NodeRenderer<GraphicsContext> nodeRenderer, EdgeRenderer<GraphicsContext> edgeRenderer) {
        this.nodeRenderer = nodeRenderer;
        this.edgeRenderer = edgeRenderer;
    }

}
