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

    public boolean addChild(GenericNode child) {
        if (child == null) {
            return false;
        }

        // Không cho add chính nó hoặc tổ tiên của this làm con
        GenericNode current = this;
        while (current != null) {
            if (current == child) {
                return false;
            }
            current = current.parent;
        }

        // Không cho add cùng một object child hai lần
        if (this.children.contains(child)) {
            return false;
        }

        // Không cho một node có 2 parent
        if (child.parent != null) {
            return false;
        }

        child.parent = this;
        this.children.add(child);
        return true;
    }

    public boolean removeChild(GenericNode child) {
        if (child == null) {
            return false; // or throw an exception, depending on how you want to handle this case
            // throw new IllegalArgumentException("Child node cannot be null");
        }
        boolean removed = this.children.remove(child);
        if (removed) {
            child.parent = null;
        }
        return removed;
    }

    public int getNumberOfChildren() {
        return this.children.size();
    }

}
