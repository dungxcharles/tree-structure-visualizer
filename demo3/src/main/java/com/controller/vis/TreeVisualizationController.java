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
    private double animationSpeed = 1.0;

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

    public void setAnimationSpeed(double animationSpeed) {
        this.animationSpeed = animationSpeed;
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
        return mapLogicalNodeToVisual(logicalNode, parentVisual, this.visualTree);
    }

    private VisualNode mapLogicalNodeToVisual(Node logicalNode, VisualNode parentVisual, VisualTree targetTree) {
        if (logicalNode == null)
            return null;

        String id = String.valueOf(System.identityHashCode(logicalNode));
        String label = String.valueOf(logicalNode.getValue());
        VisualNode vNode = new VisualNode(id, label);

        vNode.setColorHex(getLogicalNodeColor(logicalNode));
        targetTree.addNode(vNode);

        if (parentVisual != null)
            targetTree.addEdge(new VisualEdge(parentVisual, vNode));

        for (Node child : getLogicalChildren(logicalNode))
            mapLogicalNodeToVisual(child, vNode, targetTree);

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

    public void pauseAnimation() {
        this.animationManager.pause();
    }

    public void resumeAnimation() {
        this.animationManager.resume();
    }

    public boolean isAnimationPaused() {
        return this.animationManager.isPaused();
    }

    public void playAnimations() {
        if (this.visualTree == null || this.recordedSteps.isEmpty())
            return;
        processRecordedStepsAndAnimate();
    }

    private void processRecordedStepsAndAnimate() {
        if (this.recordedSteps.isEmpty()) {
            if (onAnimationFinished != null) onAnimationFinished.run();
            return;
        }

        // 1. Calculate final layout
        VisualTree finalTree = new VisualTree();
        if (this.logicalTree != null) {
            Node root = this.logicalTree.getRoot();
            if (root != null) {
                mapLogicalNodeToVisual(root, null, finalTree);
                if (canvasWidth > 0 && canvasHeight > 0) {
                    this.layoutStrategy.calculateLayout(finalTree, canvasWidth, canvasHeight);
                }
            }
        }

        // 2. Pre-process current visualTree to inject new nodes invisibly
        List<VisualNode> newNodes = new ArrayList<>();
        for (VisualNode fn : finalTree.getNodes()) {
            VisualNode existing = findNodeByLabel(this.visualTree, fn.getLabel());
            if (existing == null) {
                // New node! Inject it into visualTree invisibly at final layout pos
                VisualNode newNode = new VisualNode(fn.getId(), fn.getLabel());
                newNode.setX(fn.getX());
                newNode.setY(fn.getY());
                newNode.setOpacity(0.0);
                this.visualTree.addNode(newNode);
                newNodes.add(newNode);

                VisualEdge finalEdge = findIncomingEdge(fn, finalTree);
                if (finalEdge != null) {
                    VisualNode sourceInCurrent = findNodeByLabel(this.visualTree, finalEdge.getSource().getLabel());
                    if (sourceInCurrent != null) {
                        VisualEdge newEdge = new VisualEdge(sourceInCurrent, newNode);
                        newEdge.setProgress(0.0);
                        this.visualTree.addEdge(newEdge);
                    }
                }
            }
        }

        boolean hasDeletions = false;
        for (VisualNode vn : this.visualTree.getNodes()) {
            if (findNodeByLabel(finalTree, vn.getLabel()) == null && !newNodes.contains(vn)) {
                hasDeletions = true;
                break;
            }
        }

        // 3. Generate step animations
        List<TreeAnimation> animationsToPlay = new ArrayList<>();
        for (AnimationStep step : this.recordedSteps) {
            String message = step.getMessage();
            animationsToPlay.add(new TreeAnimation() {
                private Runnable onFinished;
                @Override public void play() {
                    if (stepHighlightCallback != null) stepHighlightCallback.accept(message);
                    if (onFinished != null) javafx.application.Platform.runLater(onFinished);
                }
                @Override public void pause() {}
                @Override public void stop() {}
                @Override public void setOnFinished(Runnable action) { this.onFinished = action; }
                @Override public void setRate(double rate) {}
            });

            StepAnimationStrategy strategy = animatorFactory.getStrategy(step.getType());
            if (strategy != null) {
                List<TreeAnimation> stepAnimations = strategy.createAnimations(step, this.visualTree);
                if (stepAnimations != null) animationsToPlay.addAll(stepAnimations);
            }
        }
        this.recordedSteps.clear();

        // 4. Create move animations
        List<TreeAnimation> moveAnimations = new ArrayList<>();
        for (VisualNode vn : this.visualTree.getNodes()) {
            VisualNode fn = findNodeByLabel(finalTree, vn.getLabel());
            if (fn != null && !newNodes.contains(vn)) {
                if (Math.abs(vn.getX() - fn.getX()) > 1 || Math.abs(vn.getY() - fn.getY()) > 1) {
                    moveAnimations.add(new NodeMoveAnimation(vn, fn.getX(), fn.getY(), 400));
                }
            }
        }

        startRenderLoop();

        Runnable onSequenceFinished = () -> {
            resetAllNodeColors();
            rebuildVisualTree();
            if (canvasWidth > 0 && canvasHeight > 0) updateLayout(canvasWidth, canvasHeight);
            stopRenderLoop();
            renderCurrentFrame();
            if (onAnimationFinished != null) onAnimationFinished.run();
        };

        if (animationsToPlay.isEmpty() && moveAnimations.isEmpty()) {
            onSequenceFinished.run();
            return;
        }

        if (!newNodes.isEmpty() || (!hasDeletions && !moveAnimations.isEmpty())) {
            // INSERT/UPDATE: Move first, then play steps
            if (!moveAnimations.isEmpty()) {
                for (TreeAnimation a : moveAnimations) a.setRate(this.animationSpeed);
                this.animationManager.setOnAllFinished(() -> playStepAnimations(animationsToPlay, onSequenceFinished));
                this.animationManager.playParallel(moveAnimations);
            } else {
                playStepAnimations(animationsToPlay, onSequenceFinished);
            }
        } else if (hasDeletions) {
            // DELETE: Play steps first, then move
            playStepAnimations(animationsToPlay, () -> {
                if (!moveAnimations.isEmpty()) {
                    for (TreeAnimation a : moveAnimations) a.setRate(this.animationSpeed);
                    this.animationManager.setOnAllFinished(onSequenceFinished);
                    this.animationManager.playParallel(moveAnimations);
                } else {
                    onSequenceFinished.run();
                }
            });
        } else {
            playStepAnimations(animationsToPlay, onSequenceFinished);
        }
    }

    private void playStepAnimations(List<TreeAnimation> steps, Runnable onFinished) {
        if (steps.isEmpty()) {
            onFinished.run();
            return;
        }
        for (TreeAnimation a : steps) a.setRate(this.animationSpeed);
        this.animationManager.setOnAllFinished(onFinished);
        this.animationManager.playSequential(steps);
    }

    private void resetAllNodeColors() {
        for (VisualNode node : this.visualTree.getNodes()) {
            if (!node.getColorHex().equals("#ff0000") && !node.getColorHex().equals("#333333")) {
                node.setColorHex("#ffffff");
            }
        }
    }

    private VisualNode findNodeByLabel(VisualTree tree, String label) {
        if (tree == null || label == null) return null;
        for (VisualNode node : tree.getNodes()) {
            if (label.equals(node.getLabel())) {
                return node;
            }
        }
        return null;
    }

    private VisualEdge findIncomingEdge(VisualNode node, VisualTree tree) {
        if (tree == null || node == null) return null;
        for (VisualEdge edge : tree.getEdges()) {
            if (edge.getTarget().getLabel().equals(node.getLabel())) {
                return edge;
            }
        }
        return null;
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

}
