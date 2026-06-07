package com.view.vis.animation.strategy;

import com.model.step.AnimationStep;
import com.model.step.StepType;
import com.view.vis.model.VisualNode;
import com.view.vis.model.VisualTree;
import com.view.vis.animation.FadeAnimation;
import com.view.vis.animation.NodeColorAnimation;
import com.view.vis.animation.NodeLabelAnimation;
import com.view.vis.animation.TreeAnimation;
import com.view.vis.animation.EdgeGrowthAnimation;
import com.view.vis.model.VisualEdge;
import com.util.VisualTreeUtils;

import java.util.ArrayList;
import java.util.List;

public class StructureStepStrategy implements StepAnimationStrategy {

    private static final String INSERT_COLOR = "#2ECC71";
    private static final String DELETE_COLOR = "#E74C3C";
    private static final String REPLACE_COLOR = "#00CED1";
    private static final double DURATION_MS = 500;

    @Override
    public List<TreeAnimation> createAnimations(AnimationStep step, VisualTree tree) {
        List<TreeAnimation> animations = new ArrayList<>();
        boolean preferNew = (step.getType() == StepType.INSERT_NODE || step.getType() == StepType.ADD_CHILD);
        VisualNode targetNode = VisualTreeUtils.findVisualNodeByValue(tree, step.getMainNodeValue(), preferNew);

        if (targetNode == null) {
            return animations;
        }

        switch (step.getType()) {
            case INSERT_NODE:
            case ADD_CHILD:
                if (targetNode.getOpacity() == 0.0) {
                    VisualEdge incomingEdge = VisualTreeUtils.findIncomingEdge(tree, targetNode);
                    if (incomingEdge != null) {
                        incomingEdge.setProgress(0.0);
                        animations.add(new EdgeGrowthAnimation(incomingEdge, 0.0, 1.0, DURATION_MS));
                    }
                    animations.add(new FadeAnimation(targetNode, tree, 0.0, 1.0, DURATION_MS));
                    animations.add(new NodeColorAnimation(targetNode, "#FFFFFF", INSERT_COLOR, DURATION_MS / 2));
                    animations.add(new NodeColorAnimation(targetNode, INSERT_COLOR, targetNode.getColorHex(), DURATION_MS / 2));
                }
                break;

            case DELETE_NODE:
            case REMOVE_CHILD:
                animations.add(new NodeColorAnimation(targetNode, targetNode.getColorHex(), DELETE_COLOR, DURATION_MS));
                animations.add(new FadeAnimation(targetNode, tree, 1.0, 0.0, DURATION_MS));
                VisualEdge incomingEdgeDelete = VisualTreeUtils.findIncomingEdge(tree, targetNode);
                if (incomingEdgeDelete != null) {
                    animations.add(new EdgeGrowthAnimation(incomingEdgeDelete, 1.0, 0.0, DURATION_MS));
                }
                break;

            case REPLACE_VALUE:
                String newLabel = VisualTreeUtils.parseReplacementValue(step.getMessage());
                if (newLabel != null) {
                    animations.add(new NodeColorAnimation(targetNode, targetNode.getColorHex(), REPLACE_COLOR, DURATION_MS / 2));
                    animations.add(new NodeLabelAnimation(targetNode, newLabel, DURATION_MS / 2));
                    animations.add(new NodeColorAnimation(targetNode, REPLACE_COLOR, targetNode.getColorHex(), DURATION_MS / 2));
                } else {
                    animations.add(new NodeColorAnimation(targetNode, targetNode.getColorHex(), REPLACE_COLOR, DURATION_MS));
                    animations.add(new NodeColorAnimation(targetNode, REPLACE_COLOR, targetNode.getColorHex(), DURATION_MS));
                }
                break;

            case ITERATE_CHILDREN:
                animations.add(new NodeColorAnimation(targetNode, targetNode.getColorHex(), "#CCCCCC", DURATION_MS / 2));
                animations.add(new NodeColorAnimation(targetNode, "#CCCCCC", targetNode.getColorHex(), DURATION_MS / 2));
                break;

            default:
                break;
        }

        return animations;
    }

}
