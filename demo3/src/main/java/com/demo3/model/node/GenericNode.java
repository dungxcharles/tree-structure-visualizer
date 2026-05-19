package com.demo3.model.node;


import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class GenericNode extends Node {

    private final List<GenericNode> children;
    private GenericNode parent;

    public GenericNode(int value) {
        super(value);
        this.parent = null;
        this.children = new ArrayList<>();
    }

    public List<GenericNode> getChildren() {
        return Collections.unmodifiableList(this.children);
    }

    public GenericNode getParent() {
        return this.parent;
    }

    public int getNumberOfChildren() {
        return this.children.size();
    }

}
