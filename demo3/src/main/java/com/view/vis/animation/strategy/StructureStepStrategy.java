package com.view.vis.animation.strategy;

import com.model.step.AnimationStep;
import com.model.vis.VisualNode;
import com.model.vis.VisualTree;
import com.view.vis.animation.NodeColorAnimation;
import com.view.vis.animation.TreeAnimation;

import java.util.ArrayList;
import java.util.List;

public class StructureStepStrategy implements StepAnimationStrategy {

    private static final String INSERT_COLOR = "#00ff00";   // Green
    private static final String DELETE_COLOR = "#ff0000";   // Red
    private static final String REPLACE_COLOR = "#00ffff";  // Cyan
    private static final double DURATION_MS = 600;

    @Override
    public List<TreeAnimation> createAnimations(AnimationStep step, VisualTree tree) {
        List<TreeAnimation> animations = new ArrayList<>();
        VisualNode targetNode = findVisualNodeByValue(tree, step.getMainNodeValue());
        
        if (targetNode != null) {
            switch (step.getType()) {
                case INSERT_NODE:
                case ADD_CHILD:
                    // Flash green to indicate insertion
                    animations.add(new NodeColorAnimation(targetNode, "#ffffff", INSERT_COLOR, DURATION_MS));
                    animations.add(new NodeColorAnimation(targetNode, INSERT_COLOR, targetNode.getColorHex(), DURATION_MS));
                    break;
                case DELETE_NODE:
                case REMOVE_CHILD:
                    // Fade to red before removal
                    animations.add(new NodeColorAnimation(targetNode, targetNode.getColorHex(), DELETE_COLOR, DURATION_MS));
                    break;
                case REPLACE_VALUE:
                    // Flash cyan for replacement
                    animations.add(new NodeColorAnimation(targetNode, targetNode.getColorHex(), REPLACE_COLOR, DURATION_MS));
                    animations.add(new NodeColorAnimation(targetNode, REPLACE_COLOR, targetNode.getColorHex(), DURATION_MS));
                    break;
                case ITERATE_CHILDREN:
                    // Highlight slightly during iteration
                    animations.add(new NodeColorAnimation(targetNode, targetNode.getColorHex(), "#cccccc", DURATION_MS / 2));
                    animations.add(new NodeColorAnimation(targetNode, "#cccccc", targetNode.getColorHex(), DURATION_MS / 2));
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
