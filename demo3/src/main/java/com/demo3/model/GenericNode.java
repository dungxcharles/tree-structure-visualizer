package com.demo3.model;

import java.util.List;
import java.util.ArrayList;

public class GenericNode <T> extends Node<T>{
    private List<GenericNode<T>> children;

    //constructor
    public GenericNode(T value){
        super(value);
        this.children = new ArrayList<>();
    }

    //getter
    public List<GenericNode<T>> getChildren(){
        return List.copyOf(this.children);
    }


    // we should have exception handling
    public void addChild(GenericNode<T> child){
        this.children.add(child);
    }
    public void removeChild(GenericNode<T> child){
        this.children.remove(child);
    }

    // manage child list is the responsibility of Generic Node
}
