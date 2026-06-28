package com.view.vis.animation.strategy;

import com.model.step.AnimationStep;
import com.view.vis.viewmodel.VisualNode;
import com.view.vis.viewmodel.VisualTree;
import com.view.vis.animation.NodeColorAnimation;
import com.view.vis.animation.TreeAnimation;
import com.util.VisualTreeUtils;

import java.util.ArrayList;
import java.util.List;

public class BalanceStepStrategy implements StepAnimationStrategy {

    private static final String BALANCE_CHECK_COLOR = "#ffa500";
    private static final String ROTATE_COLOR = "#800080";
    private static final double DURATION_MS = 500;

    @Override
    public List<TreeAnimation> createAnimations(AnimationStep step, VisualTree tree) {
        List<TreeAnimation> animations = new ArrayList<>();
        VisualNode targetNode = VisualTreeUtils.findVisualNodeByValue(tree, step.getMainNodeValue());

        if (targetNode == null) {
            return animations;
        }

        switch (step.getType()) {
            case UPDATE_HEIGHT:
            case CHECK_BALANCE:
                animations.add(new NodeColorAnimation(targetNode, targetNode.getColorHex(), BALANCE_CHECK_COLOR,
                        DURATION_MS));
                animations.add(new NodeColorAnimation(targetNode, BALANCE_CHECK_COLOR, targetNode.getColorHex(),
                        DURATION_MS));
                break;
            case ROTATE_LEFT:
            case ROTATE_RIGHT:
                animations.add(new NodeColorAnimation(targetNode, targetNode.getColorHex(), ROTATE_COLOR,
                        DURATION_MS));
                animations.add(new NodeColorAnimation(targetNode, ROTATE_COLOR, targetNode.getColorHex(),
                        DURATION_MS));
                break;
            default:
                break;
        }

        return animations;
    }

}
