package com.view.vis.animation.strategy;

import com.model.step.AnimationStep;
import com.model.vis.VisualNode;
import com.model.vis.VisualTree;
import com.view.vis.animation.NodeColorAnimation;
import com.view.vis.animation.TreeAnimation;

import java.util.ArrayList;
import java.util.List;

public class SearchStepStrategy implements StepAnimationStrategy {

    private static final String TRAVERSAL_COLOR = "#FF7F00";
    private static final String FOUND_COLOR = "#2ECC71";
    private static final String NOT_FOUND_COLOR = "#E74C3C";
    private static final String DEFAULT_COLOR = "#FFFFFF";
    private static final double STEP_DURATION_MS = 400;

    @Override
    public List<TreeAnimation> createAnimations(AnimationStep step, VisualTree tree) {
        List<TreeAnimation> animations = new ArrayList<>();
        VisualNode targetNode = findVisualNodeByValue(tree, step.getMainNodeValue());

        if (targetNode == null) {
            return animations;
        }

        String originalColor = targetNode.getColorHex();

        switch (step.getType()) {
            case VISIT:
            case COMPARE:
                // Highlight orange while comparing, then restore
                animations.add(new NodeColorAnimation(targetNode, originalColor, TRAVERSAL_COLOR, STEP_DURATION_MS));
                animations
                        .add(new NodeColorAnimation(targetNode, TRAVERSAL_COLOR, originalColor, STEP_DURATION_MS / 2));
                break;

            case GO_LEFT:
            case GO_RIGHT:
            case GO_CHILD:
                // Brief orange flash to indicate direction taken
                animations
                        .add(new NodeColorAnimation(targetNode, originalColor, TRAVERSAL_COLOR, STEP_DURATION_MS / 2));
                animations
                        .add(new NodeColorAnimation(targetNode, TRAVERSAL_COLOR, originalColor, STEP_DURATION_MS / 2));
                break;

            case ADD_TO_RESULT:
                // Quick green flash for traversal result
                animations.add(new NodeColorAnimation(targetNode, originalColor, FOUND_COLOR, STEP_DURATION_MS / 2));
                break;

            case FOUND:
                // Node found: turn green and stay
                animations.add(new NodeColorAnimation(targetNode, originalColor, FOUND_COLOR, STEP_DURATION_MS));
                break;

            case NOT_FOUND:
                // Search failed: flash red
                animations.add(new NodeColorAnimation(targetNode, originalColor, NOT_FOUND_COLOR, STEP_DURATION_MS));
                break;

            case DONE:
                // Restore to default color
                animations.add(new NodeColorAnimation(targetNode, targetNode.getColorHex(), DEFAULT_COLOR,
                        STEP_DURATION_MS / 2));
                break;

            default:
                break;
        }

        return animations;
    }

    private VisualNode findVisualNodeByValue(VisualTree tree, int value) {
        String targetLabel = String.valueOf(value);
        for (VisualNode vNode : tree.getNodes()) {
            if (vNode.getLabel().equals(targetLabel)) {
                return vNode;
            }
        }
        return null;
    }
}
