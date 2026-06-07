package com.controller.vis;

import com.model.step.AnimationStep;
import com.model.step.StepType;
import com.model.step.TreeOperationListener;
import com.model.tree.AbstractTree;
import com.view.vis.model.VisualEdge;
import com.view.vis.model.VisualNode;
import com.view.vis.model.VisualTree;
import com.view.vis.TreeCanvas;
import com.view.vis.VisualTreeMapper;
import com.view.vis.animation.AnimationManager;
import com.view.vis.animation.NodeMoveAnimation;
import com.view.vis.animation.TreeAnimation;
import com.view.vis.animation.strategy.StepAnimationStrategy;
import com.view.vis.animation.strategy.StepAnimatorFactory;
import com.view.vis.layout.LayoutStrategy;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * TreeVisualizationController orchestrates the visualization of the tree.
 * It manages the lifecycle of the visual components, listens for logical tree changes,
 * and builds smooth animation sequences.
 */
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

    private Consumer<Double> progressListener;

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

        this.animationManager.setOnProgressChanged(progress -> {
            if (progressListener != null) {
                progressListener.accept(progress);
            }
        });
    }

    public void setProgressListener(Consumer<Double> listener) { this.progressListener = listener; }
    public void setFxCanvas(Canvas fxCanvas) { this.fxCanvas = fxCanvas; }
    public void setStepHighlightCallback(Consumer<String> callback) { this.stepHighlightCallback = callback; }
    public void setAnimationSpeed(double speed) { this.animationSpeed = speed; }
    public void updateAnimationSpeed(double speed) {
        this.animationSpeed = speed;
        this.animationManager.setRate(speed);
    }
    public void setOnAnimationFinished(Runnable action) { this.onAnimationFinished = action; }
    public void setLayoutStrategy(LayoutStrategy layoutStrategy) { this.layoutStrategy = layoutStrategy; }
    public boolean isAnimating() { return animating; }
    public boolean isAnimationPaused() { return this.animationManager.isPaused(); }
    public VisualTree getVisualTree() { return visualTree; }

    public void pauseAnimation() { this.animationManager.pause(); }
    public void resumeAnimation() { this.animationManager.resume(); }

    public void resetCamera() {
        if (this.canvas != null) {
            this.canvas.resetCamera();
        }
    }

    /**
     * Attaches the logical data tree to this controller and initializes the starting visual layout.
     */
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
        if (this.visualTree == null || this.recordedSteps.isEmpty()) return;
        processRecordedStepsAndAnimate();
    }

    /**
     * Main orchestration method: Breaks down the animation generation into 5 readable steps.
     */
    private void processRecordedStepsAndAnimate() {
        // 1. Calculate the target layout (how the tree should look when finished)
        VisualTree finalTree = VisualTreeMapper.build(this.logicalTree, this.layoutStrategy, this.canvasWidth, this.canvasHeight);

        // 2. Scan recorded steps to build the unbalanced layout (before rotation)
        VisualTree unbalancedTree = buildUnbalancedTree(finalTree);

        // Prepare the canvas by injecting brand new nodes invisibly at their unbalanced positions
        List<VisualNode> newNodes = injectInvisibleNewNodes(finalTree, unbalancedTree);
        boolean hasDeletions = checkForDeletions(finalTree, newNodes);

        // 3. Create algorithmic animations (color changes, tracing paths, etc.) and append rotation move animations
        List<TreeAnimation> stepAnimations = createStepAnimations(finalTree);

        // 4. Create movement animations (nodes shifting to balance the tree)
        // If we are doing insertion/update (newNodes is not empty), we move to unbalancedTree layout.
        // Otherwise (delete), we move to finalTree layout.
        VisualTree targetMoveTree = newNodes.isEmpty() ? finalTree : unbalancedTree;
        List<TreeAnimation> moveAnimations = createLayoutMoveAnimations(targetMoveTree, newNodes, null);

        // 5. Build the final playback timeline and execute it
        playAnimationSequence(stepAnimations, moveAnimations, newNodes.isEmpty(), hasDeletions);
    }

    /**
     * Identifies nodes that exist in the final layout but not in the current visual tree.
     * Injects them invisibly into the current tree so they can be smoothly animated in later.
     */
    private List<VisualNode> injectInvisibleNewNodes(VisualTree finalTree, VisualTree unbalancedTree) {
        List<VisualNode> newNodes = new ArrayList<>();
        for (VisualNode finalNode : finalTree.getNodes()) {
            VisualNode existingNode = findNodeById(this.visualTree, finalNode.getId());
            
            if (existingNode == null) {
                // The node is completely new! Inject it invisibly at its unbalanced position.
                VisualNode newNode = new VisualNode(finalNode.getId(), finalNode.getLabel());
                VisualNode unbalancedNode = findNodeById(unbalancedTree, finalNode.getId());
                if (unbalancedNode != null) {
                    newNode.setX(unbalancedNode.getX());
                    newNode.setY(unbalancedNode.getY());
                } else {
                    newNode.setX(finalNode.getX());
                    newNode.setY(finalNode.getY());
                }
                newNode.setOpacity(0.0);
                this.visualTree.addNode(newNode);
                newNodes.add(newNode);

                // Inject the incoming edge for this new node using unbalancedTree
                VisualEdge unbalancedEdge = findIncomingEdge(finalNode, unbalancedTree);
                if (unbalancedEdge != null) {
                    VisualNode sourceInCurrent = findNodeById(this.visualTree, unbalancedEdge.getSource().getId());
                    if (sourceInCurrent != null) {
                        VisualEdge newEdge = new VisualEdge(sourceInCurrent, newNode);
                        newEdge.setProgress(0.0);
                        this.visualTree.addEdge(newEdge);
                    }
                }
            }
        }
        return newNodes;
    }

    /**
     * Builds a temporary VisualTree representing the tree after insertion/deletion
     * but before any rotation/rebalancing.
     */
    private VisualTree buildUnbalancedTree(VisualTree finalTree) {
        VisualTree tempTree = new VisualTree();
        java.util.Map<String, VisualNode> nodeMap = new java.util.HashMap<>();
        
        for (VisualNode vNode : this.visualTree.getNodes()) {
            VisualNode copy = new VisualNode(vNode.getId(), vNode.getLabel());
            copy.setColorHex(vNode.getColorHex());
            copy.setOpacity(vNode.getOpacity());
            copy.setVisible(vNode.isVisible());
            copy.setX(vNode.getX());
            copy.setY(vNode.getY());
            tempTree.addNode(copy);
            nodeMap.put(vNode.getId(), copy);
        }
        
        for (VisualEdge edge : this.visualTree.getEdges()) {
            VisualNode srcCopy = nodeMap.get(edge.getSource().getId());
            VisualNode tgtCopy = nodeMap.get(edge.getTarget().getId());
            if (srcCopy != null && tgtCopy != null) {
                VisualEdge edgeCopy = new VisualEdge(srcCopy, tgtCopy);
                edgeCopy.setColorHex(edge.getColorHex());
                edgeCopy.setOpacity(edge.getOpacity());
                edgeCopy.setProgress(edge.getProgress());
                tempTree.addEdge(edgeCopy);
            }
        }

        // 1. Handle deletion (remove deleted node from tempTree)
        int deletedValue = -1;
        for (AnimationStep step : recordedSteps) {
            if (step.getType() == StepType.DELETE_NODE || step.getType() == StepType.REMOVE_CHILD) {
                deletedValue = step.getMainNodeValue();
                break;
            }
        }
        
        if (deletedValue != -1) {
            VisualNode toRemove = null;
            for (VisualNode vNode : tempTree.getNodes()) {
                if (vNode.getLabel().equals(String.valueOf(deletedValue))) {
                    toRemove = vNode;
                    break;
                }
            }
            if (toRemove != null) {
                tempTree.removeNode(toRemove);
            }
        }

        // 2. Handle insertion (add new node and connect to parent in tempTree)
        VisualNode insertedNodeInFinal = null;
        for (VisualNode finalNode : finalTree.getNodes()) {
            if (nodeMap.get(finalNode.getId()) == null) {
                insertedNodeInFinal = finalNode;
                break;
            }
        }

        if (insertedNodeInFinal != null) {
            int parentValue = -1;
            int lastInsertIdx = -1;
            for (int i = recordedSteps.size() - 1; i >= 0; i--) {
                StepType type = recordedSteps.get(i).getType();
                if (type == StepType.INSERT_NODE || type == StepType.ADD_CHILD) {
                    lastInsertIdx = i;
                    break;
                }
            }
            if (lastInsertIdx != -1) {
                for (int j = lastInsertIdx - 1; j >= 0; j--) {
                    StepType type = recordedSteps.get(j).getType();
                    if (type == StepType.COMPARE || type == StepType.GO_LEFT || type == StepType.GO_RIGHT 
                            || type == StepType.GO_CHILD || type == StepType.ITERATE_CHILDREN || type == StepType.FOUND) {
                        parentValue = recordedSteps.get(j).getMainNodeValue();
                        break;
                    }
                }
            }

            VisualNode parentNodeInTemp = null;
            if (parentValue != -1) {
                for (VisualNode vNode : tempTree.getNodes()) {
                    if (vNode.getLabel().equals(String.valueOf(parentValue))) {
                        parentNodeInTemp = vNode;
                        break;
                    }
                }
            }

            VisualNode newNodeCopy = new VisualNode(insertedNodeInFinal.getId(), insertedNodeInFinal.getLabel());
            newNodeCopy.setColorHex(insertedNodeInFinal.getColorHex());
            newNodeCopy.setOpacity(1.0);
            newNodeCopy.setVisible(true);
            tempTree.addNode(newNodeCopy);

            if (parentNodeInTemp != null) {
                VisualEdge newEdge = new VisualEdge(parentNodeInTemp, newNodeCopy);
                newEdge.setProgress(1.0);
                tempTree.addEdge(newEdge);
            }
        }

        // 3. Calculate layout
        if (layoutStrategy != null && canvasWidth > 0 && canvasHeight > 0) {
            layoutStrategy.calculateLayout(tempTree, canvasWidth, canvasHeight);
        }

        return tempTree;
    }

    /**
     * Determines if any node currently on the screen is missing from the final layout.
     */
    private boolean checkForDeletions(VisualTree finalTree, List<VisualNode> newlyInjectedNodes) {
        for (VisualNode currentNode : this.visualTree.getNodes()) {
            if (findNodeById(finalTree, currentNode.getId()) == null && !newlyInjectedNodes.contains(currentNode)) {
                return true;
            }
        }
        return false;
    }

    private List<TreeAnimation> createStepAnimations(VisualTree finalTree) {
        List<TreeAnimation> stepAnimations = new ArrayList<>();
        
        for (AnimationStep step : this.recordedSteps) {
            final String message = step.getMessage();
            
            // Generate a lightweight background animation just to trigger the UI text update
            stepAnimations.add(new TreeAnimation() {
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

            // Delegate visual generation to the Strategy Factory based on the StepType
            StepAnimationStrategy strategy = animatorFactory.getStrategy(step.getType());
            if (strategy != null) {
                List<TreeAnimation> animations = strategy.createAnimations(step, this.visualTree);
                if (animations != null) stepAnimations.addAll(animations);
            }

            // If the step is ROTATE_LEFT or ROTATE_RIGHT, move all nodes in the tree to finalTree coordinates to prevent overlapping
            if (step.getType() == StepType.ROTATE_LEFT || step.getType() == StepType.ROTATE_RIGHT) {
                List<TreeAnimation> rotateMoves = new ArrayList<>();
                for (VisualNode currentVNode : this.visualTree.getNodes()) {
                    VisualNode finalVNode = findNodeById(finalTree, currentVNode.getId());
                    if (finalVNode != null) {
                        double diffX = Math.abs(currentVNode.getX() - finalVNode.getX());
                        double diffY = Math.abs(currentVNode.getY() - finalVNode.getY());
                        if (diffX > 1 || diffY > 1) {
                            rotateMoves.add(new NodeMoveAnimation(currentVNode, finalVNode.getX(), finalVNode.getY(), 400));
                        }
                    }
                }
                stepAnimations.addAll(rotateMoves);
            }
        }
        this.recordedSteps.clear();
        return stepAnimations;
    }

    private List<TreeAnimation> createLayoutMoveAnimations(VisualTree finalTree, List<VisualNode> newNodes, java.util.Set<String> excludeLabels) {
        List<TreeAnimation> moveAnimations = new ArrayList<>();
        
        for (VisualNode currentNode : this.visualTree.getNodes()) {
            if (excludeLabels != null && excludeLabels.contains(currentNode.getLabel())) {
                continue; // Exclude rotation-involved nodes from initial move
            }
            VisualNode finalNode = findNodeById(finalTree, currentNode.getId());
            
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

    /**
     * Orchestrates the correct ordering of algorithm animations vs physical movement animations.
     */
    private void playAnimationSequence(List<TreeAnimation> stepAnimations, List<TreeAnimation> moveAnimations, boolean noNewNodes, boolean hasDeletions) {
        startRenderLoop();

        Runnable finalizeAndCleanup = () -> {
            resetAllNodeColors();
            VisualTreeMapper.updateVisualTreeWithoutLayout(this.logicalTree, this.visualTree);
            if (canvasWidth > 0 && canvasHeight > 0) updateLayout(canvasWidth, canvasHeight);
            stopRenderLoop();
            renderCurrentFrame();
            if (onAnimationFinished != null) onAnimationFinished.run();
        };

        if (stepAnimations.isEmpty() && moveAnimations.isEmpty()) {
            finalizeAndCleanup.run();
            return;
        }

        // Logical Flow Control:
        if (!noNewNodes || (!hasDeletions && !moveAnimations.isEmpty())) {
            // INSERT/UPDATE Operation: Move tree to make physical room FIRST, then play algorithm steps
            if (!moveAnimations.isEmpty()) {
                for (TreeAnimation a : moveAnimations) a.setRate(this.animationSpeed);
                this.animationManager.setOnAllFinished(() -> playSequentialSteps(stepAnimations, finalizeAndCleanup));
                this.animationManager.playParallel(moveAnimations);
            } else {
                playSequentialSteps(stepAnimations, finalizeAndCleanup);
            }
        } else if (hasDeletions) {
            // DELETE Operation: Play algorithm steps FIRST (find node), then move layout to close the physical gap
            playSequentialSteps(stepAnimations, () -> {
                if (!moveAnimations.isEmpty()) {
                    for (TreeAnimation a : moveAnimations) a.setRate(this.animationSpeed);
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
        for (TreeAnimation a : stepAnimations) a.setRate(this.animationSpeed);
        this.animationManager.setOnAllFinished(onFinished);
        this.animationManager.playSequential(stepAnimations);
    }

    private void resetAllNodeColors() {
        for (VisualNode node : this.visualTree.getNodes()) {
            if (!node.getColorHex().equals("#ff0000") && !node.getColorHex().equals("#333333")) {
                node.setColorHex("#ffffff");
            }
        }
    }

    private void startRenderLoop() {
        if (renderLoop != null) renderLoop.stop();
        animating = true;
        renderLoop = new AnimationTimer() {
            @Override public void handle(long now) { renderCurrentFrame(); }
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

    // --- Helper Utility Methods ---
    private VisualNode findNodeById(VisualTree tree, String id) {
        if (tree == null || id == null) return null;
        for (VisualNode node : tree.getNodes()) {
            if (id.equals(node.getId())) return node;
        }
        return null;
    }

    private VisualNode findNodeByLabel(VisualTree tree, String label) {
        if (tree == null || label == null) return null;
        for (VisualNode node : tree.getNodes()) {
            if (label.equals(node.getLabel())) return node;
        }
        return null;
    }

    private VisualEdge findIncomingEdge(VisualNode node, VisualTree tree) {
        if (tree == null || node == null) return null;
        for (VisualEdge edge : tree.getEdges()) {
            if (edge.getTarget().getId().equals(node.getId())) return edge;
        }
        return null;
    }

    private void collectSubtreeValues(VisualNode node, VisualTree tree, java.util.Set<String> result) {
        if (node == null || result.contains(node.getLabel())) return;
        result.add(node.getLabel());
        for (VisualEdge edge : tree.getEdges()) {
            if (edge.getSource().getId().equals(node.getId())) {
                collectSubtreeValues(edge.getTarget(), tree, result);
            }
        }
    }
}
