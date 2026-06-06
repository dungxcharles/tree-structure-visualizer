package com.view.vis.animation.strategy;

import com.model.step.AnimationStep;
import com.model.step.StepType;
import com.model.vis.VisualNode;
import com.model.vis.VisualTree;
import com.view.vis.animation.FadeAnimation;
import com.view.vis.animation.NodeColorAnimation;
import com.view.vis.animation.NodeLabelAnimation;
import com.view.vis.animation.TreeAnimation;
import com.view.vis.animation.EdgeGrowthAnimation;
import com.model.vis.VisualEdge;

import java.util.ArrayList;
import java.util.List;

/**
 * VisualGo-style structural change animation strategy.
 *
 * INSERT_NODE: node + edge fade in gradually with green flash.
 * DELETE_NODE: red flash then node + edge fade out gradually.
 * REPLACE_VALUE: cyan flash + label update (successor swap in BST delete).
 */
public class StructureStepStrategy implements StepAnimationStrategy {

    // VisualGo-style colors
    private static final String INSERT_COLOR = "#2ECC71";    // Green  - new node
    private static final String DELETE_COLOR = "#E74C3C";    // Red    - node being removed
    private static final String REPLACE_COLOR = "#00CED1";   // Cyan   - value replacement
    private static final double DURATION_MS = 500;

    @Override
    public List<TreeAnimation> createAnimations(AnimationStep step, VisualTree tree) {
        List<TreeAnimation> animations = new ArrayList<>();
        VisualNode targetNode = findVisualNodeByValue(tree, step.getMainNodeValue());

        if (targetNode == null) {
            return animations;
        }

        switch (step.getType()) {
            case INSERT_NODE:
            case ADD_CHILD:
                targetNode.setOpacity(0.0);
                VisualEdge incomingEdge = findIncomingEdge(tree, targetNode);
                if (incomingEdge != null) {
                    incomingEdge.setProgress(0.0);
                    animations.add(new EdgeGrowthAnimation(incomingEdge, 0.0, 1.0, DURATION_MS));
                }
                animations.add(new FadeAnimation(targetNode, tree, 0.0, 1.0, DURATION_MS));
                animations.add(new NodeColorAnimation(targetNode, "#FFFFFF", INSERT_COLOR, DURATION_MS / 2));
                animations.add(new NodeColorAnimation(targetNode, INSERT_COLOR, targetNode.getColorHex(), DURATION_MS / 2));
                break;

            case DELETE_NODE:
            case REMOVE_CHILD:
                // Red flash, then fade out: node + parent edge disappear gradually (1 → 0)
                animations.add(new NodeColorAnimation(targetNode, targetNode.getColorHex(), DELETE_COLOR, DURATION_MS));
                animations.add(new FadeAnimation(targetNode, tree, 1.0, 0.0, DURATION_MS));
                VisualEdge incomingEdgeDelete = findIncomingEdge(tree, targetNode);
                if (incomingEdgeDelete != null) {
                    animations.add(new EdgeGrowthAnimation(incomingEdgeDelete, 1.0, 0.0, DURATION_MS));
                }
                break;

            case REPLACE_VALUE:
                // Parse the successor value from the message (format: "Thay thế X bằng successor Y")
                String newLabel = parseReplacementValue(step.getMessage());
                if (newLabel != null) {
                    // Cyan flash + label change
                    animations.add(new NodeColorAnimation(targetNode, targetNode.getColorHex(), REPLACE_COLOR, DURATION_MS / 2));
                    animations.add(new NodeLabelAnimation(targetNode, newLabel, DURATION_MS / 2));
                    animations.add(new NodeColorAnimation(targetNode, REPLACE_COLOR, targetNode.getColorHex(), DURATION_MS / 2));
                } else {
                    // Fallback: just cyan flash
                    animations.add(new NodeColorAnimation(targetNode, targetNode.getColorHex(), REPLACE_COLOR, DURATION_MS));
                    animations.add(new NodeColorAnimation(targetNode, REPLACE_COLOR, targetNode.getColorHex(), DURATION_MS));
                }
                break;

            case ITERATE_CHILDREN:
                // Light highlight during iteration
                animations.add(new NodeColorAnimation(targetNode, targetNode.getColorHex(), "#CCCCCC", DURATION_MS / 2));
                animations.add(new NodeColorAnimation(targetNode, "#CCCCCC", targetNode.getColorHex(), DURATION_MS / 2));
                break;

            default:
                break;
        }

        return animations;
    }

    /**
     * Tries to extract the replacement value from messages like:
     * "Thay thế X bằng successor Y"
     */
    private String parseReplacementValue(String message) {
        if (message == null) return null;
        // Look for "successor " followed by a number
        String marker = "successor ";
        int idx = message.indexOf(marker);
        if (idx >= 0) {
            String rest = message.substring(idx + marker.length()).trim();
            // Extract the number
            StringBuilder num = new StringBuilder();
            for (char c : rest.toCharArray()) {
                if (Character.isDigit(c) || c == '-') {
                    num.append(c);
                } else {
                    break;
                }
            }
            if (num.length() > 0) {
                return num.toString();
            }
        }
        return null;
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

    private VisualEdge findIncomingEdge(VisualTree tree, VisualNode node) {
        if (tree == null || node == null) return null;
        for (VisualEdge edge : tree.getEdges()) {
            if (edge.getTarget().equals(node)) {
                return edge;
            }
        }
        return null;
    }
}
