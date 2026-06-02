package com.demo3.model.node;

public class Node {
    // tqt
    protected int value;
    // protected for subclass

    public Node(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public void setValue(int value) {
        this.value = value;
    }
    // data holder
}