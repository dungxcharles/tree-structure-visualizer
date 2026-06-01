package com.visualization.model;

import java.util.List;

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
    
    public void clear() {
        this.nodes.clear();
        this.edges.clear();
    }
}
