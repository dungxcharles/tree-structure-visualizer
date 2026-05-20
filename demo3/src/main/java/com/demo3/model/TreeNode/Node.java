package com.demo3.model.TreeNode;

public abstract class Node<T> {
    protected T value;
    // protected for subclass

    public Node(T value){
        this.value = value;
    }
    public T getValue(){
        return this.value;
    }
    public void setValue(T value){
        this.value = value;
    }
    
    
}
