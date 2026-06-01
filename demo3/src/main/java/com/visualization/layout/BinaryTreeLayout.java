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

    private Map<VisualNode, List<VisualNode>> buildChildrenMap(VisualTree tree) {
        Map<VisualNode, List<VisualNode>> map = new HashMap<>();
        for (VisualNode node : tree.getNodes()) {
            map.put(node, new ArrayList<>());
        }

        for (VisualEdge edge : tree.getEdges()) {
            List<VisualNode> children = map.get(edge.getSource());
            if (children != null) {
                children.add(edge.getTarget());
            }
        }

        // Sort children to consistently place smaller values on the left
        // assuming the label contains a comparable representation of the node.
        for (List<VisualNode> children : map.values()) {
            children.sort(Comparator.comparing(VisualNode::getLabel));
        }

        return map;
    }

    private void layoutNode(VisualNode node, Map<VisualNode, List<VisualNode>> childrenMap,
                            double minX, double maxX, int depth) {
        if (node == null) {
            return;
        }

        // Calculate current node position (Center of horizontal bounds)
        double x = minX + (maxX - minX) / 2.0;
        double y = PADDING_TOP + depth * LEVEL_HEIGHT;

        node.setX(x);
        node.setY(y);

        List<VisualNode> children = childrenMap.getOrDefault(node, new ArrayList<>());

        if (children.size() == 1) {
            // One child, place it in the center or let it take the whole bounds
            layoutNode(children.get(0), childrenMap, minX, maxX, depth + 1);
        } else if (children.size() >= 2) {
            // Two children (Binary), divide space. First goes left, second goes right.
            layoutNode(children.get(0), childrenMap, minX, x, depth + 1);
            layoutNode(children.get(1), childrenMap, x, maxX, depth + 1);
        }
    }
}
