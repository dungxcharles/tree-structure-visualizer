package com.view.vis.model;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collections;

public class VisualTree {
    private List<VisualNode> nodes;
    private List<VisualEdge> edges;

    public VisualTree() {
        this.nodes = new ArrayList<>();
        this.edges = new ArrayList<>();
    }

    public void addNode(VisualNode node) {
        if (node != null && !this.nodes.contains(node)) {
            this.nodes.add(node);
        }
    }

    public void removeNode(VisualNode node) {
        this.nodes.remove(node);
        this.edges.removeIf(edge -> edge.getSource().equals(node) || edge.getTarget().equals(node));
    }

    public void addEdge(VisualEdge edge) {
        if (edge != null && !this.edges.contains(edge)) {
            this.edges.add(edge);
        }
    }

    public void removeEdge(VisualEdge edge) {
        this.edges.remove(edge);
    }

    public List<VisualNode> getNodes() {
        return Collections.unmodifiableList(this.nodes);
    }

    public List<VisualEdge> getEdges() {
        return Collections.unmodifiableList(this.edges);
    }

    // Root node has no incoming edges
    public VisualNode getRoot() {
        for (VisualNode node : this.nodes) {
            boolean hasIncomingEdge = false;
            for (VisualEdge edge : this.edges) {
                if (edge.getTarget().equals(node)) {
                    hasIncomingEdge = true;
                    break;
                }
            }
            if (!hasIncomingEdge) {
                return node;
            }
        }
        return this.nodes.isEmpty() ? null : this.nodes.get(0);
    }

    // Get children for each node in the tree (visual)
    public Map<VisualNode, List<VisualNode>> getChildrenMap() {
        Map<VisualNode, List<VisualNode>> map = new HashMap<>();
        for (VisualNode node : this.nodes) {
            map.put(node, new ArrayList<>());
        }

        for (VisualEdge edge : this.edges) {
            List<VisualNode> children = map.get(edge.getSource());
            if (children != null) {
                children.add(edge.getTarget());
            }
        }

        return map;
    }

    public void clear() {
        this.nodes.clear();
        this.edges.clear();
    }
}
