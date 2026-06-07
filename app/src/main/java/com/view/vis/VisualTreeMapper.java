    package com.view.vis;

import com.model.node.BinaryNode;
import com.model.node.GenericNode;
import com.model.node.Node;
import com.model.tree.AbstractTree;
import com.view.vis.viewmodel.VisualEdge;
import com.view.vis.viewmodel.VisualEdge.ChildSide;
import com.view.vis.viewmodel.VisualNode;
import com.view.vis.viewmodel.VisualTree;
import com.view.vis.layout.LayoutStrategy;

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
                mapLogicalNodeToVisual(root, null, ChildSide.UNKNOWN, tree);
                
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
                mapLogicalNodeToVisual(root, null, ChildSide.UNKNOWN, targetTree);
            }
        }
    }

    private static VisualNode mapLogicalNodeToVisual(Node logicalNode, VisualNode parentVisual, ChildSide childSide,
            VisualTree targetTree) {
        if (logicalNode == null) return null;

        String id = String.valueOf(System.identityHashCode(logicalNode));
        String label = String.valueOf(logicalNode.getValue());
        VisualNode vNode = new VisualNode(id, label);

        vNode.setColorHex(com.util.VisualTreeUtils.getLogicalNodeColor(logicalNode));
        targetTree.addNode(vNode);

        if (parentVisual != null) {
            targetTree.addEdge(new VisualEdge(parentVisual, vNode, childSide));
        }

        if (logicalNode instanceof BinaryNode) {
            BinaryNode bNode = (BinaryNode) logicalNode;
            mapLogicalNodeToVisual(bNode.getLeft(), vNode, ChildSide.LEFT, targetTree);
            mapLogicalNodeToVisual(bNode.getRight(), vNode, ChildSide.RIGHT, targetTree);
        } else if (logicalNode instanceof GenericNode) {
            GenericNode gNode = (GenericNode) logicalNode;
            for (GenericNode child : gNode.getChildren()) {
                mapLogicalNodeToVisual(child, vNode, ChildSide.UNKNOWN, targetTree);
            }
        }

        return vNode;
    }
}
