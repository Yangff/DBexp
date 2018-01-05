package com.totem.table;

public class Cell {
    private Attribute attr;
    private Value value;

    public Attribute getAttribute(){
        return attr;
    }
    public Attribute setAttribute(Attribute attr){
        return this.attr = attr;
    }
    public Value getValue(){
        return value;
    }
    public Value setValue(Value v) {
        if (v.getType() == attr.getType())
            return value = v;
        return v;
    }
}
