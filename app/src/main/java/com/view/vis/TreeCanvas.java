package com.view.vis;

import com.view.vis.camera.Camera;
import com.view.vis.camera.PanZoomHandler;
import com.view.vis.viewmodel.VisualEdge;
import com.view.vis.viewmodel.VisualNode;
import com.view.vis.viewmodel.VisualTree;
import com.view.vis.render.EdgeRenderer;
import com.view.vis.render.NodeRenderer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

public class TreeCanvas {
    private Canvas fxCanvas;
    private NodeRenderer nodeRenderer;
    private EdgeRenderer edgeRenderer;
    private Camera camera;
    private PanZoomHandler panZoomHandler;
    private Runnable redrawCallback;

    public TreeCanvas(Canvas canvas, NodeRenderer nodeRenderer, EdgeRenderer edgeRenderer) {
        this.fxCanvas = canvas;
        this.nodeRenderer = nodeRenderer;
        this.edgeRenderer = edgeRenderer;
        this.camera = new Camera();

        // Initialize the handler which translates mouse events to camera operations
        this.panZoomHandler = new PanZoomHandler(canvas, this.camera, this::triggerRedraw);
    }

    public void setRedrawCallback(Runnable redrawCallback) {
        this.redrawCallback = redrawCallback;
    }

    private void triggerRedraw() {
        if (redrawCallback != null) {
            redrawCallback.run();
        }
    }

    public void resetCamera() {
        if (this.camera != null) {
            this.camera.reset();
            triggerRedraw();
        }
    }

    public void draw(VisualTree tree) {
        if (tree == null || fxCanvas == null)
            return;

        GraphicsContext gc = fxCanvas.getGraphicsContext2D();
        camera.apply(gc);

        for (VisualEdge edge : tree.getEdges()) {
            this.edgeRenderer.render(gc, edge);
        }

        for (VisualNode node : tree.getNodes()) {
            this.nodeRenderer.render(gc, node);
        }

        camera.restore(gc);
    }

    public void clear() {
        if (fxCanvas != null) {
            GraphicsContext gc = fxCanvas.getGraphicsContext2D();
            // Camera context is restored at the end of draw(), so this safely clears the
            // un-transformed physical canvas.
            gc.clearRect(0, 0, fxCanvas.getWidth(), fxCanvas.getHeight());
        }
    }
}
