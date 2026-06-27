package com.controller.vis;

import com.model.step.AnimationStep;
import com.model.step.StepType;
import com.model.step.TreeOperationListener;
import com.model.tree.AbstractTree;
import com.view.vis.viewmodel.VisualEdge;
import com.view.vis.viewmodel.VisualNode;
import com.view.vis.viewmodel.VisualTree;
import com.view.vis.TreeCanvas;
import com.view.vis.animation.AnimationManager;
import com.view.vis.animation.NodeMoveAnimation;
import com.view.vis.animation.TreeAnimation;
import com.view.vis.animation.strategy.StepAnimationStrategy;
import com.view.vis.animation.strategy.StepAnimatorFactory;
import com.view.vis.layout.LayoutStrategy;
import com.util.VisualTreeMapper;
import com.util.VisualTreeUtils;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class TreeVisualizationController implements TreeOperationListener {
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
    private Consumer<Double> progressCallback;
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

    public void setStepHighlightCallback(Consumer<String> callback) {
        this.stepHighlightCallback = callback;
    }

    public void setProgressCallback(Consumer<Double> callback) {
        this.progressCallback = callback;
    }

    public void setAnimationSpeed(double speed) {
        this.animationSpeed = speed;
    }

    public void setOnAnimationFinished(Runnable action) {
        this.onAnimationFinished = action;
    }

    public void setLayoutStrategy(LayoutStrategy layoutStrategy) {
        this.layoutStrategy = layoutStrategy;
    }

    public boolean isAnimating() {
        return animating;
    }

    public boolean isAnimationPaused() {
        return this.animationManager.isPaused();
    }

    public VisualTree getVisualTree() {
        return visualTree;
    }

    public void pauseAnimation() {
        this.animationManager.pause();
    }

    public void resumeAnimation() {
        this.animationManager.resume();
    }

    public void resetCamera() {
        if (this.canvas != null) {
            this.canvas.resetCamera();
        }
    }

    public void setTreeData(Object logicalTreeData) {
        if (this.visualTree != null) {
            this.visualTree.clear();
        }

        if (logicalTreeData instanceof AbstractTree) {
            AbstractTree<?> tree = (AbstractTree<?>) logicalTreeData;
            this.logicalTree = tree;
            tree.setListener(this);
            VisualTreeMapper.updateVisualTreeWithoutLayout(tree, this.visualTree);
        }
    }

    public void updateLayout(double width, double height) {
        this.canvasWidth = width;
        this.canvasHeight = height;
        if (this.layoutStrategy != null && this.visualTree != null) {
            this.layoutStrategy.calculateLayout(this.visualTree, width, height);
        }
    }

    public void renderFrame(GraphicsContext gc) {
        if (this.canvas != null && gc != null) {
            this.canvas.clear(gc);
            this.canvas.draw(this.visualTree, gc);
        }
    }

    @Override
    public void onStep(StepType type, int nodeValue, String message) {
        this.recordedSteps.add(new AnimationStep(type, nodeValue, message));
    }

    public void playAnimations() {
        if (this.visualTree == null || this.recordedSteps.isEmpty()) {
            finishWithoutAnimation();
            return;
        }
        if (progressCallback != null)
            progressCallback.accept(0.0);
        processRecordedStepsAndAnimate();
    }

    private void finishWithoutAnimation() {
        if (this.visualTree != null && this.logicalTree != null) {
            VisualTreeMapper.updateVisualTreeWithoutLayout(this.logicalTree, this.visualTree);
            if (canvasWidth > 0 && canvasHeight > 0) {
                updateLayout(canvasWidth, canvasHeight);
            }
            renderCurrentFrame();
        }
        if (progressCallback != null) {
            progressCallback.accept(1.0);
        }
        if (onAnimationFinished != null) {
            onAnimationFinished.run();
        }
    }

    private void processRecordedStepsAndAnimate() {
        VisualTree finalTree = VisualTreeMapper.build(this.logicalTree, this.layoutStrategy, this.canvasWidth,
                this.canvasHeight);

        List<VisualNode> newNodes = injectInvisibleNewNodes(finalTree);
        boolean hasDeletions = checkForDeletions(finalTree, newNodes);

        List<TreeAnimation> stepAnimations = createStepAnimations();

        List<TreeAnimation> moveAnimations = createLayoutMoveAnimations(finalTree, newNodes);

        playAnimationSequence(stepAnimations, moveAnimations, newNodes.isEmpty(), hasDeletions);
    }

    private List<VisualNode> injectInvisibleNewNodes(VisualTree finalTree) {
        List<VisualNode> newNodes = new ArrayList<>();
        for (VisualNode finalNode : finalTree.getNodes()) {
            VisualNode existingNode = VisualTreeUtils.findNodeById(this.visualTree, finalNode.getId());

            if (existingNode == null) {
                VisualNode newNode = new VisualNode(finalNode.getId(), finalNode.getLabel());
                newNode.setX(finalNode.getX());
                newNode.setY(finalNode.getY());
                newNode.setOpacity(0.0);
                this.visualTree.addNode(newNode);
                newNodes.add(newNode);

                VisualEdge finalEdge = VisualTreeUtils.findIncomingEdge(finalTree, finalNode);
                if (finalEdge != null) {
                    VisualNode sourceInCurrent = VisualTreeUtils.findNodeById(this.visualTree,
                            finalEdge.getSource().getId());
                    if (sourceInCurrent != null) {
                        VisualEdge newEdge = new VisualEdge(sourceInCurrent, newNode, finalEdge.getChildSide());
                        newEdge.setProgress(0.0);
                        this.visualTree.addEdge(newEdge);
                    }
                }
            }
        }
        return newNodes;
    }

    private boolean checkForDeletions(VisualTree finalTree, List<VisualNode> newlyInjectedNodes) {
        for (VisualNode currentNode : this.visualTree.getNodes()) {
            if (VisualTreeUtils.findNodeById(finalTree, currentNode.getId()) == null
                    && !newlyInjectedNodes.contains(currentNode)) {
                return true;
            }
        }
        return false;
    }

    private List<TreeAnimation> createStepAnimations() {
        List<TreeAnimation> stepAnimations = new ArrayList<>();
        int totalSteps = this.recordedSteps.size();
        int currentStepIndex = 0;

        for (AnimationStep step : this.recordedSteps) {
            final String message = step.getMessage();
            final double progress = totalSteps > 0 ? (double) currentStepIndex / totalSteps : 0.0;
            currentStepIndex++;

            stepAnimations.add(new TreeAnimation() {
                private Runnable onFinished;

                @Override
                public void play() {
                    if (stepHighlightCallback != null)
                        stepHighlightCallback.accept(message);
                    if (progressCallback != null)
                        progressCallback.accept(progress);
                    if (onFinished != null)
                        javafx.application.Platform.runLater(onFinished);
                }

                @Override
                public void pause() {
                }

                @Override
                public void stop() {
                }

                @Override
                public void setOnFinished(Runnable action) {
                    this.onFinished = action;
                }

                @Override
                public void setRate(double rate) {
                }
            });

            StepAnimationStrategy strategy = animatorFactory.getStrategy(step.getType());
            if (strategy != null) {
                List<TreeAnimation> animations = strategy.createAnimations(step, this.visualTree);
                if (animations != null)
                    stepAnimations.addAll(animations);
            }
        }
        this.recordedSteps.clear();
        return stepAnimations;
    }

    private List<TreeAnimation> createLayoutMoveAnimations(VisualTree finalTree, List<VisualNode> newNodes) {
        List<TreeAnimation> moveAnimations = new ArrayList<>();

        for (VisualNode currentNode : this.visualTree.getNodes()) {
            VisualNode finalNode = VisualTreeUtils.findNodeById(finalTree, currentNode.getId());

            if (finalNode != null && !newNodes.contains(currentNode)) {
                double diffX = Math.abs(currentNode.getX() - finalNode.getX());
                double diffY = Math.abs(currentNode.getY() - finalNode.getY());

                // Only animate if the distance moved is significant (greater than 1 pixel)
                if (diffX > 1 || diffY > 1) {
                    moveAnimations.add(new NodeMoveAnimation(currentNode, finalNode.getX(), finalNode.getY(), 400));
                }
            }
        }
        return moveAnimations;
    }

    private void playAnimationSequence(List<TreeAnimation> stepAnimations, List<TreeAnimation> moveAnimations,
            boolean noNewNodes, boolean hasDeletions) {
        startRenderLoop();

        Runnable finalizeAndCleanup = () -> {
            resetAllNodeColors();
            VisualTreeMapper.updateVisualTreeWithoutLayout(this.logicalTree, this.visualTree);
            if (canvasWidth > 0 && canvasHeight > 0)
                updateLayout(canvasWidth, canvasHeight);
            stopRenderLoop();
            renderCurrentFrame();
            if (progressCallback != null)
                progressCallback.accept(1.0);
            if (onAnimationFinished != null)
                onAnimationFinished.run();
        };

        if (stepAnimations.isEmpty() && moveAnimations.isEmpty()) {
            finalizeAndCleanup.run();
            return;
        }

        if (!noNewNodes || (!hasDeletions && !moveAnimations.isEmpty())) {
            if (!moveAnimations.isEmpty()) {
                for (TreeAnimation a : moveAnimations)
                    a.setRate(this.animationSpeed);
                this.animationManager.setOnAllFinished(() -> playSequentialSteps(stepAnimations, finalizeAndCleanup));
                this.animationManager.playParallel(moveAnimations);
            } else {
                playSequentialSteps(stepAnimations, finalizeAndCleanup);
            }
        } else if (hasDeletions) {
            playSequentialSteps(stepAnimations, () -> {
                if (!moveAnimations.isEmpty()) {
                    for (TreeAnimation a : moveAnimations)
                        a.setRate(this.animationSpeed);
                    this.animationManager.setOnAllFinished(finalizeAndCleanup);
                    this.animationManager.playParallel(moveAnimations);
                } else {
                    finalizeAndCleanup.run();
                }
            });
        } else {
            playSequentialSteps(stepAnimations, finalizeAndCleanup);
        }
    }

    private void playSequentialSteps(List<TreeAnimation> stepAnimations, Runnable onFinished) {
        if (stepAnimations.isEmpty()) {
            onFinished.run();
            return;
        }
        for (TreeAnimation a : stepAnimations)
            a.setRate(this.animationSpeed);
        this.animationManager.setOnAllFinished(onFinished);
        this.animationManager.playSequential(stepAnimations);
    }

    private void resetAllNodeColors() {
        for (VisualNode node : this.visualTree.getNodes()) {
            if (!node.getColorHex().equals(VisualNode.COLOR_RED_BLACK_RED)
                    && !node.getColorHex().equals(VisualNode.COLOR_RED_BLACK_BLACK)) {
                node.setColorHex(VisualNode.COLOR_DEFAULT);
            }
        }
    }

    private void startRenderLoop() {
        if (renderLoop != null)
            renderLoop.stop();
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
