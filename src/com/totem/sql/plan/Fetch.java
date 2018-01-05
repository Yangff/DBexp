package com.totem.sql.plan;

import com.totem.table.Attribute;
import com.totem.table.Value;

public class Fetch implements Op {
    private Attribute attr;
    private Value value;
    public Fetch(Attribute attr, Value val){
        this.attr = attr;
        value = val;
    }
    @Override
    public void Visit(IVisitor visitor) {
        visitor.doFetch(attr, value);
    }
}
