package com.view.vis.layout;

import com.model.vis.VisualNode;
import com.model.vis.VisualTree;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BinaryTreeLayout implements LayoutStrategy {

    private static final double LEVEL_HEIGHT = 80.0;
    private static final double PADDING_TOP = 40.0;

    public BinaryTreeLayout() {
    }

    @Override
    public void calculateLayout(VisualTree visualTree, double containerWidth, double containerHeight) {
        if (visualTree == null || visualTree.getNodes().isEmpty()) {
            return;
        }

        VisualNode root = visualTree.getRoot();
        if (root == null) {
            return;
        }

        Map<VisualNode, List<VisualNode>> childrenMap = visualTree.getChildrenMap();

        layoutNode(root, childrenMap, 0, containerWidth, 0);
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
