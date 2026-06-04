package com.view.vis.animation.strategy;

import com.model.step.AnimationStep;
import com.model.vis.VisualNode;
import com.model.vis.VisualTree;
import com.view.vis.animation.NodeColorAnimation;
import com.view.vis.animation.TreeAnimation;

import java.util.ArrayList;
import java.util.List;

public class SearchStepStrategy implements StepAnimationStrategy {

    private static final String HIGHLIGHT_COLOR = "#ffff00"; // Yellow
    private static final String FOUND_COLOR = "#0000ff";     // Blue
    private static final String NOT_FOUND_COLOR = "#ff0000"; // Red
    private static final String DEFAULT_COLOR = "#ffffff";
    private static final double DURATION_MS = 400;

    @Override
    public List<TreeAnimation> createAnimations(AnimationStep step, VisualTree tree) {
        List<TreeAnimation> animations = new ArrayList<>();
        VisualNode targetNode = findVisualNodeByValue(tree, step.getMainNodeValue());
        
        if (targetNode != null) {
            switch (step.getType()) {
                case VISIT:
                case COMPARE:
                case GO_LEFT:
                case GO_RIGHT:
                case GO_CHILD:
                case ADD_TO_RESULT:
                    // Highlight the node briefly
                    animations.add(new NodeColorAnimation(targetNode, targetNode.getColorHex(), HIGHLIGHT_COLOR, DURATION_MS));
                    animations.add(new NodeColorAnimation(targetNode, HIGHLIGHT_COLOR, targetNode.getColorHex(), DURATION_MS));
                    break;
                case FOUND:
                    // Color it blue to indicate it was found
                    animations.add(new NodeColorAnimation(targetNode, targetNode.getColorHex(), FOUND_COLOR, DURATION_MS * 2));
                    break;
                case NOT_FOUND:
                    // Color it red to indicate failure
                    animations.add(new NodeColorAnimation(targetNode, targetNode.getColorHex(), NOT_FOUND_COLOR, DURATION_MS * 2));
                    break;
                case DONE:
                    // Restore default or original color
                    animations.add(new NodeColorAnimation(targetNode, targetNode.getColorHex(), DEFAULT_COLOR, DURATION_MS));
                    break;
                default:
                    break;
            }
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
