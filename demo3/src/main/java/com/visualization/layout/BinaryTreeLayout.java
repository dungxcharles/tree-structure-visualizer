package com.visualization.layout;

import com.visualization.model.VisualEdge;
import com.visualization.model.VisualNode;
import com.visualization.model.VisualTree;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BinaryTreeLayout implements LayoutStrategy {

    private static final double LEVEL_HEIGHT = 80.0;
    private static final double PADDING_TOP = 40.0;

    public BinaryTreeLayout() {}

    @Override
    public void calculateLayout(VisualTree visualTree, double containerWidth, double containerHeight) {
        if (visualTree == null || visualTree.getNodes().isEmpty()) {
            return;
        }

        VisualNode root = findRoot(visualTree);
        if (root == null) {
            return;
        }

        Map<VisualNode, List<VisualNode>> childrenMap = buildChildrenMap(visualTree);
        
        layoutNode(root, childrenMap, 0, containerWidth, 0);
    }

    private VisualNode findRoot(VisualTree tree) {
        List<VisualNode> nodes = tree.getNodes();
        for (VisualNode node : nodes) {
            boolean hasIncomingEdge = false;
            for (VisualEdge edge : tree.getEdges()) {
                if (edge.getTarget().equals(node)) {
                    hasIncomingEdge = true;
                    break;
                }
            }
            if (!hasIncomingEdge) {
                return node; // Root node found
            }
        }
        return nodes.isEmpty() ? null : nodes.get(0);
    }
}
