package com.controller.vis;

import com.model.step.*;
import com.model.tree.AbstractTree;
import com.view.vis.viewmodel.*;
import com.view.vis.TreeCanvas;
import com.view.vis.animation.*;
import com.view.vis.animation.strategy.*;
import com.view.vis.layout.LayoutStrategy;
import com.util.*;
import javafx.animation.AnimationTimer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class TreeVisualizationController implements TreeOperationListener {
    private VisualTree visualTree = new VisualTree();
    private TreeCanvas canvas;
    private AnimationManager animationManager;
    private LayoutStrategy layoutStrategy;
    private List<AnimationStep> recordedSteps = new ArrayList<>();
    private StepAnimatorFactory animatorFactory = new StepAnimatorFactory();

    private AnimationTimer renderLoop;
    private double animationSpeed = 1.0;

    private Consumer<String> stepHighlightCallback;
    private Consumer<Double> progressCallback;
    private Runnable onAnimationFinished;

    private AbstractTree<?> logicalTree;
    private double canvasWidth;
    private double canvasHeight;

    public TreeVisualizationController(TreeCanvas canvas, AnimationManager animationManager,
            LayoutStrategy layoutStrategy) {
        this.canvas = canvas;
        this.animationManager = animationManager;
        this.layoutStrategy = layoutStrategy;
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

    public boolean isAnimating() {
        return renderLoop != null;
    }

    public boolean isAnimationPaused() {
        return this.animationManager.isPaused();
    }

    public void pauseAnimation() {
        this.animationManager.pause();
    }

    public void resumeAnimation() {
        this.animationManager.resume();
    }

    public void resetCamera() {
        if (this.canvas != null)
            this.canvas.resetCamera();
    }

    public void setTreeData(AbstractTree<?> tree) {
        this.visualTree.clear();

        if (tree != null) {
            this.logicalTree = tree;
            tree.setListener(this);
            VisualTreeMapper.updateVisualTreeWithoutLayout(tree, this.visualTree);
        }
    }

    public void updateLayout(double width, double height) {
        this.canvasWidth = width;
        this.canvasHeight = height;
        if (this.layoutStrategy != null && !isAnimating()) {
            this.layoutStrategy.calculateLayout(this.visualTree, width, height);
        }
    }

    public void renderFrame() {
        if (this.canvas != null) {
            this.canvas.clear();
            this.canvas.draw(this.visualTree);
        }
    }

    @Override
    public void onStep(StepType type, int nodeValue, String message) {
        this.recordedSteps.add(new AnimationStep(type, nodeValue, message));
    }

    public void playAnimations() {
        if (this.recordedSteps.isEmpty()) {
            finishWithoutAnimation();
            return;
        }
        if (progressCallback != null)
            progressCallback.accept(0.0);
        processRecordedStepsAndAnimate();
    }

    private void finishWithoutAnimation() {
        if (this.logicalTree != null) {
            VisualTreeMapper.updateVisualTreeWithoutLayout(this.logicalTree, this.visualTree);
            if (canvasWidth > 0 && canvasHeight > 0)
                updateLayout(canvasWidth, canvasHeight);
            renderFrame();
        }
        if (progressCallback != null)
            progressCallback.accept(1.0);
        if (onAnimationFinished != null)
            onAnimationFinished.run();
    }

    private void processRecordedStepsAndAnimate() {
        VisualTree finalTree = VisualTreeMapper.build(this.logicalTree, this.layoutStrategy, this.canvasWidth,
                this.canvasHeight);

        List<VisualNode> newNodes = injectInvisibleNewNodes(finalTree);
        boolean hasDeletions = checkForDeletions(finalTree);

        List<TreeAnimation> stepAnimations = createStepAnimations();

        List<TreeAnimation> moveAnimations = createLayoutMoveAnimations(finalTree);

        playAnimationSequence(stepAnimations, moveAnimations, newNodes.isEmpty(), hasDeletions);
    }

    private List<VisualNode> injectInvisibleNewNodes(VisualTree finalTree) {
        List<VisualNode> newNodes = new ArrayList<>();
        for (VisualNode finalNode : finalTree.getNodes()) {
            if (VisualTreeUtils.findNodeById(this.visualTree, finalNode.getId()) != null)
                continue;

            VisualNode newNode = new VisualNode(finalNode.getId(), finalNode.getLabel());
            newNode.setX(finalNode.getX());
            newNode.setY(finalNode.getY());
            newNode.setOpacity(0.0);
            this.visualTree.addNode(newNode);
            newNodes.add(newNode);

            VisualEdge finalEdge = VisualTreeUtils.findIncomingEdge(finalTree, finalNode);
            if (finalEdge == null)
                continue;

            VisualNode sourceInCurrent = VisualTreeUtils.findNodeById(this.visualTree,
                    finalEdge.getSource().getId());
            if (sourceInCurrent != null) {
                VisualEdge newEdge = new VisualEdge(sourceInCurrent, newNode, finalEdge.getChildSide());
                newEdge.setProgress(0.0);
                this.visualTree.addEdge(newEdge);
            }
        }
        return newNodes;
    }

    private boolean checkForDeletions(VisualTree finalTree) {
        for (VisualNode currentNode : this.visualTree.getNodes()) {
            if (VisualTreeUtils.findNodeById(finalTree, currentNode.getId()) == null) {
                return true;
            }
        }
        return false;
    }

    private List<TreeAnimation> createStepAnimations() {
        List<TreeAnimation> stepAnimations = new ArrayList<>();
        int totalSteps = this.recordedSteps.size();

        for (int i = 0; i < totalSteps; i++) {
            AnimationStep step = this.recordedSteps.get(i);
            final String message = step.getMessage();
            final double progress = totalSteps > 0 ? (double) i / totalSteps : 0.0;

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

    private List<TreeAnimation> createLayoutMoveAnimations(VisualTree finalTree) {
        List<TreeAnimation> moveAnimations = new ArrayList<>();
        for (VisualNode currentNode : this.visualTree.getNodes()) {
            VisualNode finalNode = VisualTreeUtils.findNodeById(finalTree, currentNode.getId());
            if (finalNode != null && (Math.abs(currentNode.getX() - finalNode.getX()) > 1
                    || Math.abs(currentNode.getY() - finalNode.getY()) > 1)) {
                moveAnimations.add(new NodeMoveAnimation(currentNode, finalNode.getX(), finalNode.getY(), 400));
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
            stopRenderLoop();
            if (canvasWidth > 0 && canvasHeight > 0)
                updateLayout(canvasWidth, canvasHeight);
            renderFrame();
            if (progressCallback != null)
                progressCallback.accept(1.0);
            if (onAnimationFinished != null)
                onAnimationFinished.run();
        };

        for (TreeAnimation a : moveAnimations)
            a.setRate(this.animationSpeed);

        if (moveAnimations.isEmpty()) {
            playSequentialSteps(stepAnimations, finalizeAndCleanup);
        } else if (!noNewNodes || !hasDeletions) {
            this.animationManager.setOnAllFinished(() -> playSequentialSteps(stepAnimations, finalizeAndCleanup));
            this.animationManager.playParallel(moveAnimations);
        } else {
            playSequentialSteps(stepAnimations, () -> {
                this.animationManager.setOnAllFinished(finalizeAndCleanup);
                this.animationManager.playParallel(moveAnimations);
            });
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
            String color = node.getColorHex();
            if (!color.equals(VisualNode.COLOR_RED_BLACK_RED) && !color.equals(VisualNode.COLOR_RED_BLACK_BLACK)) {
                node.setColorHex(VisualNode.COLOR_DEFAULT);
            }
        }
    }

    private void startRenderLoop() {
        if (renderLoop != null)
            renderLoop.stop();
        renderLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                renderFrame();
            }
        };
        renderLoop.start();
    }

    private void stopRenderLoop() {
        if (renderLoop != null) {
            renderLoop.stop();
            renderLoop = null;
        }
    }
}
