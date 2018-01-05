package com.totem.sql.plan;

public class Scan implements Op {
    @Override
    public void Visit(IVisitor visitor) {
        visitor.doScan();
        visitor.end();
    }
}
