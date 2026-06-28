package com.util;

import com.model.node.Node;
import com.model.node.RBNode;
import com.model.step.StepType;
import com.view.vis.viewmodel.VisualEdge;
import com.view.vis.viewmodel.VisualNode;
import com.view.vis.viewmodel.VisualTree;

public class VisualTreeUtils {

    public static VisualNode findVisualNodeByValue(VisualTree tree, int value) {
        return findVisualNodeByValue(tree, value, false);
    }

    public static VisualNode findVisualNodeByValue(VisualTree tree, int value, boolean preferNew) {
        if (tree == null)
            return null;
        String targetLabel = String.valueOf(value);
        if (preferNew) {
            for (VisualNode vNode : tree.getNodes()) {
                if (vNode.getLabel().equals(targetLabel) && vNode.getOpacity() == 0.0)
                    return vNode;
            }
        }
        for (VisualNode vNode : tree.getNodes()) {
            if (vNode.getLabel().equals(targetLabel))
                return vNode;
        }
        return null;
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
        if (tree == null || node == null)
            return null;
        for (VisualEdge edge : tree.getEdges()) {
            if (edge.getTarget().getId().equals(node.getId())) {
                return edge;
            }
        }
        return null;
    }

    public static VisualEdge findOutgoingEdge(VisualTree tree, VisualNode source, StepType direction) {
        if (tree == null || source == null)
            return null;
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


    public static String getLogicalNodeColor(Node logicalNode) {
        if (logicalNode instanceof RBNode) {
            RBNode rbNode = (RBNode) logicalNode;
            return rbNode.getColor() == RBNode.Color.RED ? VisualNode.COLOR_RED_BLACK_RED
                    : VisualNode.COLOR_RED_BLACK_BLACK;
        }
        return VisualNode.COLOR_DEFAULT;
    }
}
