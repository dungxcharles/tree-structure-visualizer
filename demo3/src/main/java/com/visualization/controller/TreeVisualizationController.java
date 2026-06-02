package com.visualization.controller;

import com.visualization.model.VisualTree;
import com.visualization.view.TreeCanvas;
import com.visualization.view.animation.AnimationManager;
import com.visualization.layout.LayoutStrategy;

import javafx.scene.canvas.GraphicsContext;

public class TreeVisualizationController {
    private VisualTree visualTree;
    private TreeCanvas canvas;
    private AnimationManager animationManager;
    private LayoutStrategy layoutStrategy;

    public TreeVisualizationController(
            TreeCanvas canvas,
            AnimationManager animationManager,
            LayoutStrategy layoutStrategy) {
        this.visualTree = new VisualTree();
        this.canvas = canvas;
        this.animationManager = animationManager;
        this.layoutStrategy = layoutStrategy;
    }

    public void setTreeData(Object logicalTreeData) {
        // TODO: Map the abstract logicalTreeData to VisualTree nodes and edges.
        // For now, simply clear the visual tree when new data is injected.
        if (this.visualTree != null) {
            this.visualTree.clear();
        }
    }

    public void updateLayout(double width, double height) {
        if (this.layoutStrategy != null && this.visualTree != null) {
            this.layoutStrategy.calculateLayout(this.visualTree, width, height);
        }
    }

    public void renderFrame(GraphicsContext graphicsContext) {
        if (this.canvas != null && graphicsContext != null) {
            this.canvas.clear(graphicsContext);
            this.canvas.draw(this.visualTree, graphicsContext);
        }
    }

    public void animateNodeInsertion(Object logicalNodeInfo) {
        // TODO: Convert logicalNodeInfo to VisualNode, calculate layout delta, and pass
        // to animationManager
    }

    public void animateNodeDeletion(Object logicalNodeInfo) {
        // TODO: Find VisualNode from logicalNodeInfo, create disappearing animation,
        // and pass to animationManager
    }

    public void setLayoutStrategy(LayoutStrategy layoutStrategy) {
        this.layoutStrategy = layoutStrategy;
    }

    public VisualTree getVisualTree() {
        return visualTree;
    }
}
