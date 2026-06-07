package com.view.vis.layout;

import com.view.vis.model.VisualNode;
import com.view.vis.model.VisualTree;
import com.view.vis.model.VisualEdge;
import com.view.vis.model.VisualEdge.ChildSide;

public class BinaryTreeLayout implements LayoutStrategy {

    private static final double LEVEL_HEIGHT = 80.0;
    private static final double PADDING_TOP = 40.0;

    @Override
    public void calculateLayout(VisualTree visualTree, double containerWidth, double containerHeight) {
        if (visualTree == null || visualTree.getNodes().isEmpty()) {
            return;
        }

        VisualNode root = visualTree.getRoot();
        if (root == null) {
            return;
        }

        layoutNode(root, visualTree, 0, containerWidth, 0);
    }

    private void layoutNode(VisualNode node, VisualTree visualTree,
            double minX, double maxX, int depth) {
        if (node == null) {
            return;
        }

        // Positioning
        double x = minX + (maxX - minX) / 2.0;
        double y = PADDING_TOP + depth * LEVEL_HEIGHT;

        node.setX(x);
        node.setY(y);

        VisualNode leftChild = null;
        VisualNode rightChild = null;

        for (VisualEdge edge : visualTree.getEdges()) {
            if (!edge.getSource().equals(node)) {
                continue;
            }
            if (edge.getChildSide() == ChildSide.LEFT) {
                leftChild = edge.getTarget();
            } else if (edge.getChildSide() == ChildSide.RIGHT) {
                rightChild = edge.getTarget();
            }
        }

        if (leftChild != null) {
            layoutNode(leftChild, visualTree, minX, x, depth + 1);
        }
        if (rightChild != null) {
            layoutNode(rightChild, visualTree, x, maxX, depth + 1);
        }
    }
}
