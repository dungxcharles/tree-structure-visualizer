package com.view.vis.animation.strategy;

import com.model.step.AnimationStep;
import com.view.vis.viewmodel.VisualNode;
import com.view.vis.viewmodel.VisualTree;
import com.view.vis.viewmodel.VisualEdge;
import com.view.vis.animation.NodeColorAnimation;
import com.view.vis.animation.TreeAnimation;
import com.view.vis.animation.EdgeTraversalAnimation;
import com.util.VisualTreeUtils;

import java.util.ArrayList;
import java.util.List;

public class SearchStepStrategy implements StepAnimationStrategy {

    private static final String TRAVERSAL_COLOR = "#FF7F00";
    private static final String FOUND_COLOR = "#2ECC71";
    private static final String NOT_FOUND_COLOR = "#E74C3C";
    private static final String DEFAULT_COLOR = "#FFFFFF";
    private static final double DURATION_MS = 400;

    @Override
    public List<TreeAnimation> createAnimations(AnimationStep step, VisualTree tree) {
        List<TreeAnimation> animations = new ArrayList<>();
        VisualNode targetNode = VisualTreeUtils.findVisualNodeByValue(tree, step.getMainNodeValue());

        if (targetNode == null) {
            return animations;
        }

        String originalColor = targetNode.getColorHex();

        switch (step.getType()) {
            case VISIT:
            case COMPARE:
                animations.add(new NodeColorAnimation(targetNode, originalColor, TRAVERSAL_COLOR, DURATION_MS));
                animations
                        .add(new NodeColorAnimation(targetNode, TRAVERSAL_COLOR, originalColor, DURATION_MS / 2));
                break;

            case GO_LEFT:
            case GO_RIGHT:
            case GO_CHILD:
                animations
                        .add(new NodeColorAnimation(targetNode, originalColor, TRAVERSAL_COLOR, DURATION_MS / 2));
                animations
                        .add(new NodeColorAnimation(targetNode, TRAVERSAL_COLOR, originalColor, DURATION_MS / 2));
                
                VisualEdge outgoingEdge = VisualTreeUtils.findOutgoingEdge(tree, targetNode, step.getType());
                if (outgoingEdge != null && outgoingEdge.getTarget().getOpacity() > 0.0) {
                    animations.add(new EdgeTraversalAnimation(outgoingEdge, TRAVERSAL_COLOR, DURATION_MS));
                }
                break;

            case ADD_TO_RESULT:
                animations.add(new NodeColorAnimation(targetNode, originalColor, FOUND_COLOR, DURATION_MS / 2));
                break;

            case FOUND:
                animations.add(new NodeColorAnimation(targetNode, originalColor, FOUND_COLOR, DURATION_MS));
                break;

            case NOT_FOUND:
                animations.add(new NodeColorAnimation(targetNode, originalColor, NOT_FOUND_COLOR, DURATION_MS));
                break;

            case DONE:
                animations.add(new NodeColorAnimation(targetNode, targetNode.getColorHex(), DEFAULT_COLOR,
                        DURATION_MS / 2));
                break;

            default:
                break;
        }

        return animations;
    }

}
