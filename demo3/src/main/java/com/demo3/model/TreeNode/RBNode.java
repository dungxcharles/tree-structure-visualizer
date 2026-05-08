package com.demo3.model.treenode;

public class RBNode <T extends Comparable<T>> extends BinaryNode<T>{
    private RBNode<T> parent;
    private Color color;

    public enum Color{
        RED,
        BLACK;
    }

    public RBNode(T value){
        super(value);
        this.parent = null;
        this.color = Color.RED;
    }

    // getter
    // parent is needed in order to do red black tree manipulation
    public RBNode<T> getParent(){
        return this.parent;
    }
    public Color getColor(){
        return this.color;
    }
    public void setParent(RBNode<T> parent){
        this.parent = parent;
    }
    public void setColor(Color color){
        this.color = color;
    }
    
}