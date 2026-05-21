package com.model.TreeNode;

public class BinaryNode<T> extends Node<T>{

    private BinaryNode<T> left;
    private BinaryNode<T> right;

    
    //constructor
    public BinaryNode(T value){
        super(value);
    }

    // getter 
    public BinaryNode<T> getLeft(){
        return this.left;
    }
    public BinaryNode<T> getRight(){
        return this.right;
    }

    //setter
    public void setLeft(BinaryNode<T> left){
        this.left = left;
    }

    public void setRight(BinaryNode<T> right){
        this.right = right;
    }

    // node only holds the data and collection -> Single responsibility principle 

}
