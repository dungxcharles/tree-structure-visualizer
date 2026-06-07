package com.view.vis.animation.strategy;

import com.model.step.AnimationStep;
import com.view.vis.model.VisualNode;
import com.view.vis.model.VisualTree;
import com.view.vis.animation.NodeColorAnimation;
import com.view.vis.animation.TreeAnimation;

import java.util.ArrayList;
import java.util.List;

public class BalanceStepStrategy implements StepAnimationStrategy {

    private static final String BALANCE_CHECK_COLOR = "#ffa500"; // Orange
    private static final String ROTATE_COLOR = "#800080";        // Purple
    private static final double DURATION_MS = 500;

    @Override
    public List<TreeAnimation> createAnimations(AnimationStep step, VisualTree tree) {
        List<TreeAnimation> animations = new ArrayList<>();
        VisualNode targetNode = findVisualNodeByValue(tree, step.getMainNodeValue());
        
        if (targetNode != null) {
            switch (step.getType()) {
                case UPDATE_HEIGHT:
                case CHECK_BALANCE:
                    // Flash orange for balance/height checks
                    animations.add(new NodeColorAnimation(targetNode, targetNode.getColorHex(), BALANCE_CHECK_COLOR, DURATION_MS));
                    animations.add(new NodeColorAnimation(targetNode, BALANCE_CHECK_COLOR, targetNode.getColorHex(), DURATION_MS));
                    break;
                case ROTATE_LEFT:
                case ROTATE_RIGHT:
                    // Flash purple to indicate rotation participation
                    animations.add(new NodeColorAnimation(targetNode, targetNode.getColorHex(), ROTATE_COLOR, DURATION_MS));
                    animations.add(new NodeColorAnimation(targetNode, ROTATE_COLOR, targetNode.getColorHex(), DURATION_MS));
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
