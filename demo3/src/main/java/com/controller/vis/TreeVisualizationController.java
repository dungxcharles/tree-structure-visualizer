package com.controller.vis;

import com.model.vis.VisualTree;
import com.model.vis.VisualNode;
import com.model.vis.VisualEdge;
import com.view.vis.TreeCanvas;
import com.view.vis.animation.AnimationManager;
import com.view.vis.animation.FadeAnimation;
import com.view.vis.animation.NodeColorAnimation;
import com.view.vis.animation.NodeMoveAnimation;
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

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class TreeVisualizationController implements TreeOperationAnimator, TreeOperationListener {
    private VisualTree visualTree;
    private TreeCanvas canvas;
    private AnimationManager animationManager;
    private LayoutStrategy layoutStrategy;
    private List<AnimationStep> recordedSteps;
    private StepAnimatorFactory animatorFactory;

    private Canvas fxCanvas;
    private AnimationTimer renderLoop;
    private boolean animating = false;

    private Consumer<String> stepHighlightCallback;

    private Runnable onAnimationFinished;

    private AbstractTree<?> logicalTree;

    private double canvasWidth;
    private double canvasHeight;

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

    public void setFxCanvas(Canvas fxCanvas) {
        this.fxCanvas = fxCanvas;
    }

    public void setStepHighlightCallback(Consumer<String> stepHighlightCallback) {
        this.stepHighlightCallback = stepHighlightCallback;
    }

    public void setOnAnimationFinished(Runnable onAnimationFinished) {
        this.onAnimationFinished = onAnimationFinished;
    }

    public boolean isAnimating() {
        return animating;
    }

    public void setTreeData(Object logicalTreeData) {
        if (this.visualTree != null) {
            this.visualTree.clear();
        }

        if (logicalTreeData instanceof AbstractTree) {
            AbstractTree<?> tree = (AbstractTree<?>) logicalTreeData;

            this.logicalTree = tree;
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
        this.canvasWidth = width;
        this.canvasHeight = height;
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

    @Override
    public void onStep(StepType type, int nodeValue, String message) {
        this.recordedSteps.add(new AnimationStep(type, nodeValue, message));
    }

    private void processRecordedStepsAndAnimate() {
        if (this.recordedSteps.isEmpty()) {
            if (onAnimationFinished != null) {
                onAnimationFinished.run();
            }
            return;
        }

        List<TreeAnimation> animationsToPlay = new ArrayList<>();

        for (AnimationStep step : this.recordedSteps) {
            String message = step.getMessage();
            
            // Add a zero-duration animation that just fires the callback to insert and highlight the text
            animationsToPlay.add(new TreeAnimation() {
                private Runnable onFinished;
                @Override
                public void play() {
                    if (stepHighlightCallback != null) {
                        stepHighlightCallback.accept(message);
                    }
                    if (onFinished != null) {
                        javafx.application.Platform.runLater(onFinished);
                    }
                }
                @Override public void pause() {}
                @Override public void stop() {}
                @Override public void setOnFinished(Runnable action) { this.onFinished = action; }
            });

            StepAnimationStrategy strategy = animatorFactory.getStrategy(step.getType());
            if (strategy != null) {
                List<TreeAnimation> stepAnimations = strategy.createAnimations(step, this.visualTree);
                if (stepAnimations != null) {
                    animationsToPlay.addAll(stepAnimations);
                }
            }
        }

        this.recordedSteps.clear();

        if (animationsToPlay.isEmpty()) {
            if (onAnimationFinished != null) {
                onAnimationFinished.run();
            }
            return;
        }

        startRenderLoop();

        this.animationManager.setOnAllFinished(() -> {
            resetAllNodeColors();

            Map<String, double[]> oldPositions = capturePositions();

            rebuildVisualTree();

            if (canvasWidth > 0 && canvasHeight > 0) {
                updateLayout(canvasWidth, canvasHeight);
            }

            List<TreeAnimation> moveAnimations = createMoveAnimations(oldPositions);
            if (!moveAnimations.isEmpty()) {
                this.animationManager.setOnAllFinished(this::finishAnimationSequence);
                this.animationManager.playParallel(moveAnimations);
            } else {
                finishAnimationSequence();
            }
        });

        this.animationManager.playSequential(animationsToPlay);
    }

    private void finishAnimationSequence() {
        stopRenderLoop();
        renderCurrentFrame();
        if (onAnimationFinished != null) {
            onAnimationFinished.run();
        }
    }

    private void resetAllNodeColors() {
        for (VisualNode node : this.visualTree.getNodes()) {
            // Only reset non-RB nodes to white; RB nodes keep their color
            if (!node.getColorHex().equals("#ff0000") && !node.getColorHex().equals("#333333")) {
                node.setColorHex("#ffffff");
            }
        }
    }

    private Map<String, double[]> capturePositions() {
        Map<String, double[]> positions = new HashMap<>();
        for (VisualNode node : this.visualTree.getNodes()) {
            positions.put(node.getLabel(), new double[] { node.getX(), node.getY() });
        }
        return positions;
    }

    private void rebuildVisualTree() {
        this.visualTree.clear();
        if (this.logicalTree != null) {
            Node root = this.logicalTree.getRoot();
            if (root != null) {
                mapLogicalNodeToVisual(root, null);
            }
        }
    }

    private List<TreeAnimation> createMoveAnimations(Map<String, double[]> oldPositions) {
        List<TreeAnimation> moves = new ArrayList<>();
        for (VisualNode node : this.visualTree.getNodes()) {
            double[] oldPos = oldPositions.get(node.getLabel());
            if (oldPos != null) {
                double newX = node.getX();
                double newY = node.getY();
                // Move from old position to new position
                node.setX(oldPos[0]);
                node.setY(oldPos[1]);
                if (Math.abs(oldPos[0] - newX) > 1 || Math.abs(oldPos[1] - newY) > 1) {
                    moves.add(new NodeMoveAnimation(node, newX, newY, 400));
                }
            } else {
                // New node: fade in gradually with its parent edge
                node.setOpacity(0.0);
                moves.add(new FadeAnimation(node, this.visualTree, 0.0, 1.0, 400));
            }
        }
        return moves;
    }

    private void startRenderLoop() {
        if (renderLoop != null) {
            renderLoop.stop();
        }
        animating = true;
        renderLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                renderCurrentFrame();
            }
        };
        renderLoop.start();
    }

    private void stopRenderLoop() {
        animating = false;
        if (renderLoop != null) {
            renderLoop.stop();
            renderLoop = null;
        }
    }

    private void renderCurrentFrame() {
        if (fxCanvas != null) {
            renderFrame(fxCanvas.getGraphicsContext2D());
        }
    }

    @Override
    public void playAnimations() {
        processRecordedStepsAndAnimate();
    }

}
