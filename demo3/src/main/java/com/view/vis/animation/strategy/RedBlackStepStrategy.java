package com.view.vis.animation.strategy;

import com.model.step.AnimationStep;
import com.view.vis.model.VisualNode;
import com.view.vis.model.VisualTree;
import com.view.vis.animation.NodeColorAnimation;
import com.view.vis.animation.TreeAnimation;
import com.util.VisualTreeUtils;

import java.util.ArrayList;
import java.util.List;

public class RedBlackStepStrategy implements StepAnimationStrategy {

    private static final String FIX_COLOR = "#ff00ff";
    private static final String TRANSPLANT_COLOR = "#00ced1";
    private static final double DURATION_MS = 600;

    @Override
    public List<TreeAnimation> createAnimations(AnimationStep step, VisualTree tree) {
        List<TreeAnimation> animations = new ArrayList<>();
        VisualNode targetNode = VisualTreeUtils.findVisualNodeByValue(tree, step.getMainNodeValue());
        
        if (targetNode != null) {
            switch (step.getType()) {
                case RECOLOR:
                    animations.add(new NodeColorAnimation(targetNode, targetNode.getColorHex(), FIX_COLOR, DURATION_MS));
                    animations.add(new NodeColorAnimation(targetNode, FIX_COLOR, targetNode.getColorHex(), DURATION_MS));
                    break;
                case FIX_START:
                    animations.add(new NodeColorAnimation(targetNode, targetNode.getColorHex(), FIX_COLOR, DURATION_MS));
                    animations.add(new NodeColorAnimation(targetNode, FIX_COLOR, targetNode.getColorHex(), DURATION_MS));
                    break;
                case TRANSPLANT:
                    animations.add(new NodeColorAnimation(targetNode, targetNode.getColorHex(), TRANSPLANT_COLOR, DURATION_MS));
                    animations.add(new NodeColorAnimation(targetNode, TRANSPLANT_COLOR, targetNode.getColorHex(), DURATION_MS));
                    break;
                default:
                    break;
            }
        }
        return animations;
    }

}
