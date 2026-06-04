package com.view.vis.animation.strategy;

import com.model.step.AnimationStep;
import com.model.vis.VisualNode;
import com.model.vis.VisualTree;
import com.view.vis.animation.NodeColorAnimation;
import com.view.vis.animation.TreeAnimation;

import java.util.ArrayList;
import java.util.List;

public class RedBlackStepStrategy implements StepAnimationStrategy {

    private static final String FIX_COLOR = "#ff00ff";       // Magenta
    private static final String TRANSPLANT_COLOR = "#00ced1"; // Dark Turquoise
    private static final double DURATION_MS = 600;

    @Override
    public List<TreeAnimation> createAnimations(AnimationStep step, VisualTree tree) {
        List<TreeAnimation> animations = new ArrayList<>();
        VisualNode targetNode = findVisualNodeByValue(tree, step.getMainNodeValue());
        
        if (targetNode != null) {
            switch (step.getType()) {
                case RECOLOR:
                    // Parse the message to determine the target color, or just rely on layout update later
                    // For now, flash magenta to show a color change is happening
                    animations.add(new NodeColorAnimation(targetNode, targetNode.getColorHex(), FIX_COLOR, DURATION_MS));
                    animations.add(new NodeColorAnimation(targetNode, FIX_COLOR, targetNode.getColorHex(), DURATION_MS));
                    break;
                case FIX_START:
                    // Indicate the start of fixup loop
                    animations.add(new NodeColorAnimation(targetNode, targetNode.getColorHex(), FIX_COLOR, DURATION_MS));
                    animations.add(new NodeColorAnimation(targetNode, FIX_COLOR, targetNode.getColorHex(), DURATION_MS));
                    break;
                case TRANSPLANT:
                    // Indicate transplant operation
                    animations.add(new NodeColorAnimation(targetNode, targetNode.getColorHex(), TRANSPLANT_COLOR, DURATION_MS));
                    animations.add(new NodeColorAnimation(targetNode, TRANSPLANT_COLOR, targetNode.getColorHex(), DURATION_MS));
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
