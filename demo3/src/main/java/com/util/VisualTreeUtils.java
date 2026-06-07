package com.util;

import com.model.step.StepType;
import com.view.vis.model.VisualEdge;
import com.view.vis.model.VisualNode;
import com.view.vis.model.VisualTree;

public class VisualTreeUtils {

    public static VisualNode findVisualNodeByValue(VisualTree tree, int value) {
        return findVisualNodeByValue(tree, value, false);
    }

    public static VisualNode findVisualNodeByValue(VisualTree tree, int value, boolean preferNew) {
        if (tree == null) return null;
        String targetLabel = String.valueOf(value);
        VisualNode bestMatch = null;
        for (VisualNode vNode : tree.getNodes()) {
            if (vNode.getLabel().equals(targetLabel)) {
                if (preferNew && vNode.getOpacity() == 0.0) {
                    return vNode;
                }
                if (bestMatch == null) {
                    bestMatch = vNode;
                }
            }
        }
        return bestMatch;
    }

    public static VisualNode findNodeById(VisualTree tree, String id) {
        if (tree == null || id == null)
            return null;
        for (VisualNode node : tree.getNodes()) {
            if (id.equals(node.getId()))
                return node;
        }
        return null;
    }

    public static VisualEdge findIncomingEdge(VisualTree tree, VisualNode node) {
        if (tree == null || node == null) return null;
        for (VisualEdge edge : tree.getEdges()) {
            if (edge.getTarget().getId().equals(node.getId())) {
                return edge;
            }
        }
        return null;
    }

    public static VisualEdge findOutgoingEdge(VisualTree tree, VisualNode source, StepType direction) {
        if (tree == null || source == null) return null;
        for (VisualEdge edge : tree.getEdges()) {
            if (edge.getSource().equals(source)) {
                if (direction == StepType.GO_LEFT && edge.getTarget().getX() <= source.getX()) {
                    return edge;
                } else if (direction == StepType.GO_RIGHT && edge.getTarget().getX() >= source.getX()) {
                    return edge;
                } else if (direction == StepType.GO_CHILD) {
                    return edge;
                }
            }
        }
        return null;
    }

    public static String parseReplacementValue(String message) {
        if (message == null) return null;
        String marker = "successor ";
        int idx = message.indexOf(marker);
        if (idx >= 0) {
            String rest = message.substring(idx + marker.length()).trim();
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
}
