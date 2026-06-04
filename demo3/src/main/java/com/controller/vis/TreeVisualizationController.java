package com.controller.vis;

import com.model.vis.VisualTree;
import com.model.vis.VisualNode;
import com.model.vis.VisualEdge;
import com.view.vis.TreeCanvas;
import com.view.vis.animation.AnimationManager;
import com.view.vis.animation.TreeAnimation;
import com.view.vis.layout.LayoutStrategy;
import com.model.node.Node;
import com.model.node.RBNode;
import com.model.node.BinaryNode;
import com.model.node.GenericNode;
import com.model.tree.AbstractTree;
import com.model.step.TreeOperationListener;
import com.model.step.AnimationStep;
import com.model.step.StepType;
import com.view.vis.animation.strategy.StepAnimationStrategy;
import com.view.vis.animation.strategy.StepAnimatorFactory;

import javafx.scene.canvas.GraphicsContext;
import java.util.ArrayList;
import java.util.List;

public class TreeVisualizationController implements TreeOperationAnimator, TreeOperationListener {
    private VisualTree visualTree;
    private TreeCanvas canvas;
    private AnimationManager animationManager;
    private LayoutStrategy layoutStrategy;
    private List<AnimationStep> recordedSteps;
    private StepAnimatorFactory animatorFactory;

    public TreeVisualizationController(
            TreeCanvas canvas,
            AnimationManager animationManager,
            LayoutStrategy layoutStrategy) {
        this.visualTree = new VisualTree();
        this.canvas = canvas;
        this.animationManager = animationManager;
        this.layoutStrategy = layoutStrategy;
        this.recordedSteps = new ArrayList<>();
        this.animatorFactory = new StepAnimatorFactory();
    }

    public void setTreeData(Object logicalTreeData) {
        if (this.visualTree != null) {
            this.visualTree.clear();
        }

        if (logicalTreeData instanceof AbstractTree) {
            AbstractTree<?> tree = (AbstractTree<?>) logicalTreeData;
            // Listen to tree's operation steps
            tree.setListener(this);
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

    private VisualNode findVisualNodeByValue(int value) {
        String targetLabel = String.valueOf(value);
        for (VisualNode vNode : this.visualTree.getNodes()) {
            if (vNode.getLabel().equals(targetLabel)) {
                return vNode;
            }
        }
        return null;
    }

    // --- TreeOperationListener Implementation ---

    @Override
    public void onStep(StepType type, int nodeValue, String message) {
        this.recordedSteps.add(new AnimationStep(type, nodeValue, message));
    }

    private void processRecordedStepsAndAnimate() {
        if (this.recordedSteps.isEmpty())
            return;

        List<TreeAnimation> animationsToPlay = new ArrayList<>();

        for (AnimationStep step : this.recordedSteps) {
            StepAnimationStrategy strategy = animatorFactory.getStrategy(step.getType());
            if (strategy != null) {
                List<TreeAnimation> stepAnimations = strategy.createAnimations(step, this.visualTree);
                if (stepAnimations != null) {
                    animationsToPlay.addAll(stepAnimations);
                }
            }
        }

        // Play accumulated animations sequentially
        this.animationManager.playSequential(animationsToPlay);

        // Clear steps for the next operation
        this.recordedSteps.clear();
    }

    @Override
    public void playAnimations() {
        processRecordedStepsAndAnimate();
    }
}
