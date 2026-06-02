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

    private VisualNode mapLogicalNodeToVisual(com.model.node.Node logicalNode, VisualNode parentVisual) {
        if (logicalNode == null) return null;

        String id = String.valueOf(System.identityHashCode(logicalNode));
        String label = String.valueOf(logicalNode.getValue());
        VisualNode vNode = new VisualNode(id, label);

        if (logicalNode instanceof com.model.node.RBNode) {
            com.model.node.RBNode rbNode = (com.model.node.RBNode) logicalNode;
            vNode.setColorHex(rbNode.getColor() == com.model.node.RBNode.Color.RED ? "#ff0000" : "#333333");
        } else {
            vNode.setColorHex("#ffffff");
        }

        this.visualTree.addNode(vNode);

        if (parentVisual != null) {
            this.visualTree.addEdge(new VisualEdge(parentVisual, vNode));
        }

        if (logicalNode instanceof com.model.node.BinaryNode) {
            com.model.node.BinaryNode bNode = (com.model.node.BinaryNode) logicalNode;
            if (bNode.getLeft() != null) mapLogicalNodeToVisual(bNode.getLeft(), vNode);
            if (bNode.getRight() != null) mapLogicalNodeToVisual(bNode.getRight(), vNode);
        } else if (logicalNode instanceof com.model.node.GenericNode) {
            com.model.node.GenericNode gNode = (com.model.node.GenericNode) logicalNode;
            for (com.model.node.GenericNode child : gNode.getChildren()) {
                mapLogicalNodeToVisual(child, vNode);
            }
        }

        return vNode;
    }

    public void animateNodeInsertion(Object logicalNodeInfo) {
        if (!(logicalNodeInfo instanceof com.model.node.Node)) return;
        com.model.node.Node logicalNode = (com.model.node.Node) logicalNodeInfo;

        String id = String.valueOf(System.identityHashCode(logicalNode));
        VisualNode targetNode = null;
        for (VisualNode n : this.visualTree.getNodes()) {
            if (n.getId().equals(id)) {
                targetNode = n;
                break;
            }
        }

        if (targetNode != null && this.animationManager != null) {
            double targetX = targetNode.getX();
            double targetY = targetNode.getY();
            targetNode.setY(targetY - 50); // Start from above to simulate dropping in

            List<TreeAnimation> animations = new ArrayList<>();
            animations.add(new NodeMoveAnimation(targetNode, targetX, targetY, 500));
            this.animationManager.playParallel(animations);
        }
    }

    public void animateNodeDeletion(Object logicalNodeInfo) {
        if (!(logicalNodeInfo instanceof com.model.node.Node)) return;
        com.model.node.Node logicalNode = (com.model.node.Node) logicalNodeInfo;

        String id = String.valueOf(System.identityHashCode(logicalNode));
        VisualNode targetNode = null;
        for (VisualNode n : this.visualTree.getNodes()) {
            if (n.getId().equals(id)) {
                targetNode = n;
                break;
            }
        }

        if (targetNode != null && this.animationManager != null) {
            List<TreeAnimation> animations = new ArrayList<>();
            NodeColorAnimation fadeOut = new NodeColorAnimation(targetNode, targetNode.getColorHex(), "#ff0000", 500);
            VisualNode finalTarget = targetNode;
            fadeOut.setOnFinished(() -> {
                this.visualTree.removeNode(finalTarget);
            });
            animations.add(fadeOut);
            this.animationManager.playSequential(animations);
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
