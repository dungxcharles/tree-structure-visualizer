package com.visualization.view;

import com.visualization.model.VisualEdge;
import com.visualization.model.VisualNode;
import com.visualization.model.VisualTree;
import com.visualization.view.render.EdgeRenderer;
import com.visualization.view.render.NodeRenderer;
import javafx.scene.canvas.GraphicsContext;

public class TreeCanvas {
    private NodeRenderer nodeRenderer;
    private EdgeRenderer edgeRenderer;

    public TreeCanvas(NodeRenderer nodeRenderer, EdgeRenderer edgeRenderer) {
        this.nodeRenderer = nodeRenderer;
        this.edgeRenderer = edgeRenderer;
    }

    public void draw(VisualTree tree, GraphicsContext gc) {
        if (tree == null || gc == null)
            return;

        // Draw edges first so they're behind the nodes
        for (VisualEdge edge : tree.getEdges()) {
            this.edgeRenderer.render(gc, edge);
        }

        // Draw nodes on top
        for (VisualNode node : tree.getNodes()) {
            this.nodeRenderer.render(gc, node);
        }
    }

    public void clear(GraphicsContext gc) {
        if (gc != null) {
            gc.clearRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
        }
    }
}
