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
import com.model.node.Node;
import com.model.node.RBNode;
import com.model.node.BinaryNode;
import com.model.node.GenericNode;
import com.model.tree.AbstractTree;

import javafx.scene.canvas.GraphicsContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TreeVisualizationController implements TreeOperationAnimator {
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

        if (logicalTreeData instanceof AbstractTree) {
            AbstractTree<?> tree = (AbstractTree<?>) logicalTreeData;
            Node root = tree.getRoot();
            if (root != null) {
                mapLogicalNodeToVisual(root, null);
            }
        }
    }

    private VisualNode mapLogicalNodeToVisual(Node logicalNode, VisualNode parentVisual) {
        if (logicalNode == null)
            return null;

        String id = String.valueOf(System.identityHashCode(logicalNode));
        String label = String.valueOf(logicalNode.getValue());
        VisualNode vNode = new VisualNode(id, label);

        vNode.setColorHex(getLogicalNodeColor(logicalNode));
        this.visualTree.addNode(vNode);

        if (parentVisual != null)
            this.visualTree.addEdge(new VisualEdge(parentVisual, vNode));

        for (Node child : getLogicalChildren(logicalNode))
            mapLogicalNodeToVisual(child, vNode);

        return vNode;
    }

    private String getLogicalNodeColor(Node logicalNode) {
        if (logicalNode instanceof RBNode) {
            RBNode rbNode = (RBNode) logicalNode;
            return rbNode.getColor() == RBNode.Color.RED ? "#ff0000" : "#333333";
        }
        return "#ffffff";
    }

    private List<Node> getLogicalChildren(Node logicalNode) {
        List<Node> children = new ArrayList<>();
        if (logicalNode instanceof BinaryNode) {
            BinaryNode bNode = (BinaryNode) logicalNode;
            if (bNode.getLeft() != null)
                children.add(bNode.getLeft());
            if (bNode.getRight() != null)
                children.add(bNode.getRight());
        } else if (logicalNode instanceof GenericNode) {
            GenericNode gNode = (GenericNode) logicalNode;
            children.addAll(gNode.getChildren());
        }
        return children;
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

    // --- TreeOperationAnimator Implementation ---

    @Override
    public void animateInsert(Node node) {
        // TODO: Implement insert animation logic
    }

    @Override
    public void animateRemove(Node node) {
        // TODO: Implement remove animation logic
    }

    @Override
    public void animateHighlight(Node node) {
        // TODO: Implement highlight animation logic
    }

    @Override
    public void animateSearch(Node node) {
        // TODO: Implement search animation logic
    }

    @Override
    public void animateRotate(Node node) {
        // TODO: Implement rotate animation logic
    }
}
