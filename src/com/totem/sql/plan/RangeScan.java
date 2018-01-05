package com.totem.sql.plan;

import com.totem.table.Attribute;
import com.totem.table.Value;

public class RangeScan implements Op {
    private Attribute index;
    private Value left, right;
    private int scanType;
    public RangeScan(Attribute index, Value left, Value right, int scanType) {
        this.index = index;
        this.left = left; this.right = right;
        this.scanType = scanType;
    }
    @Override
    public void Visit(IVisitor visitor) {
        visitor.doRangeScan(index,left,right,scanType);
        visitor.end();
    }
}
