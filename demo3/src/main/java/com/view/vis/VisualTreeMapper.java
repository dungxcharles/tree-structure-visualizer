    package com.view.vis;

import com.model.node.BinaryNode;
import com.model.node.GenericNode;
import com.model.node.Node;
import com.model.node.RBNode;
import com.model.tree.AbstractTree;
import com.model.vis.VisualEdge;
import com.model.vis.VisualNode;
import com.model.vis.VisualTree;
import com.view.vis.layout.LayoutStrategy;

import java.util.ArrayList;
import java.util.List;

/**
 * VisualTreeMapper is responsible for converting a Logical Tree (data model)
 * into a Visual Tree (graphics model). It strictly adheres to the Single Responsibility Principle.
 */
public class VisualTreeMapper {

    /**
     * Builds a fully mapped and positioned VisualTree from a LogicalTree.
     *
     * @param logicalTree    The data model tree.
     * @param layoutStrategy The strategy used to calculate X/Y coordinates.
     * @param width          The width of the available canvas.
     * @param height         The height of the available canvas.
     * @return A newly constructed and positioned VisualTree.
     */
    public static VisualTree build(AbstractTree<?> logicalTree, LayoutStrategy layoutStrategy, double width, double height) {
        VisualTree tree = new VisualTree();
        if (logicalTree != null) {
            Node root = logicalTree.getRoot();
            if (root != null) {
                mapLogicalNodeToVisual(root, null, tree);
                
                // Calculate physical coordinates if valid dimensions are provided
                if (layoutStrategy != null && width > 0 && height > 0) {
                    layoutStrategy.calculateLayout(tree, width, height);
                }
            }
        }
        return tree;
    }

    /**
     * Updates an existing VisualTree directly without recalculating layout.
     */
    public static void updateVisualTreeWithoutLayout(AbstractTree<?> logicalTree, VisualTree targetTree) {
        targetTree.clear();
        if (logicalTree != null) {
            Node root = logicalTree.getRoot();
            if (root != null) {
                mapLogicalNodeToVisual(root, null, targetTree);
            }
        }
    }

    private static VisualNode mapLogicalNodeToVisual(Node logicalNode, VisualNode parentVisual, VisualTree targetTree) {
        if (logicalNode == null) return null;

        String id = String.valueOf(System.identityHashCode(logicalNode));
        String label = String.valueOf(logicalNode.getValue());
        VisualNode vNode = new VisualNode(id, label);

        vNode.setColorHex(getLogicalNodeColor(logicalNode));
        targetTree.addNode(vNode);

        if (parentVisual != null) {
            targetTree.addEdge(new VisualEdge(parentVisual, vNode));
        }

        for (Node child : getLogicalChildren(logicalNode)) {
            mapLogicalNodeToVisual(child, vNode, targetTree);
        }

        return vNode;
    }

    private static String getLogicalNodeColor(Node logicalNode) {
        if (logicalNode instanceof RBNode) {
            RBNode rbNode = (RBNode) logicalNode;
            return rbNode.getColor() == RBNode.Color.RED ? "#ff0000" : "#333333";
        }
        return "#ffffff"; // Default color
    }

    private static List<Node> getLogicalChildren(Node logicalNode) {
        List<Node> children = new ArrayList<>();
        if (logicalNode instanceof BinaryNode) {
            BinaryNode bNode = (BinaryNode) logicalNode;
            if (bNode.getLeft() != null) children.add(bNode.getLeft());
            if (bNode.getRight() != null) children.add(bNode.getRight());
        } else if (logicalNode instanceof GenericNode) {
            GenericNode gNode = (GenericNode) logicalNode;
            children.addAll(gNode.getChildren());
        }
        return children;
    }
}
