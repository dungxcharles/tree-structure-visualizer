package com.demo3.model.node;

public class BinaryNode extends Node {

    private BinaryNode left;
    private BinaryNode right;

    //constructor
    public BinaryNode(int value){
        super(value);
    }

    // getter
    public BinaryNode getLeft(){
        return this.left;
    }
    public BinaryNode getRight(){
        return this.right;
    }

    //setter
    public void setLeft(BinaryNode left){
        this.left = left;
    }

    public void setRight(BinaryNode right){
        this.right = right;
    }

    // node only holds the data and collection -> Single responsibility principle

}
