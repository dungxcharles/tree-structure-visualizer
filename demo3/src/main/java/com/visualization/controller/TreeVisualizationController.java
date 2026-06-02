package com.visualization.controller;

import com.visualization.model.VisualTree;
import com.visualization.model.VisualNode;
import com.visualization.model.VisualEdge;
import com.visualization.view.TreeCanvas;
import com.visualization.view.animation.AnimationManager;
import com.visualization.view.animation.NodeMoveAnimation;
import com.visualization.view.animation.NodeColorAnimation;
import com.visualization.view.animation.TreeAnimation;
import com.visualization.layout.LayoutStrategy;

import javafx.scene.canvas.GraphicsContext;
import java.util.ArrayList;
import java.util.List;

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
        if (this.visualTree != null) {
            this.visualTree.clear();
        }
        
        if (logicalTreeData instanceof com.model.tree.AbstractTree) {
            com.model.tree.AbstractTree<?> tree = (com.model.tree.AbstractTree<?>) logicalTreeData;
            com.model.node.Node root = tree.getRoot();
            if (root != null) {
                mapLogicalNodeToVisual(root, null);
            }
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

    public void setLayoutStrategy(LayoutStrategy layoutStrategy) {
        this.layoutStrategy = layoutStrategy;
    }

    public VisualTree getVisualTree() {
        return visualTree;
    }
}
