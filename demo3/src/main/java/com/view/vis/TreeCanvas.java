package com.view.vis;

import com.model.vis.VisualEdge;
import com.model.vis.VisualNode;
import com.model.vis.VisualTree;
import com.view.vis.render.EdgeRenderer;
import com.view.vis.render.NodeRenderer;
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

        for (VisualEdge edge : tree.getEdges()) {
            this.edgeRenderer.render(gc, edge);
        }

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
