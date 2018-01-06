package com.totem.engine;

import com.totem.sql.plan.IVisitor;
import com.totem.table.Attribute;
import com.totem.table.Value;

public class PlanExecutor implements IVisitor {

    @Override
    public IVisitor runTour() {
        return new PlanExecutor();
    }

    @Override
    public void doFetch(Attribute attr, Value value) {

    }

    @Override
    public void doRangeScan(Attribute attr, Value L, Value R, int type) {

    }

    @Override
    public void doIntersect(IVisitor L, IVisitor R) {

    }

    @Override
    public void doUnion(IVisitor L, IVisitor R) {

    }

    @Override
    public void doScan() {

    }

    @Override
    public void end() {

    }
}
