package com.totem.sql.plan;

public class Union implements Op {
    private IVisitor leftResult, rightResult;
    private Op leftOp, rightOp;
    public Union(Op left, Op right) {
        leftOp = left;
        rightOp = right;
    }
    /**
     * Visit left op and right op separately and merge it by
     * do intersect.
     * @param visitor the implement of IVisitor,
     *                carry out the action based
     *                on the plan.
     *                Now, the plan would run
     *                in a naive way that not
     */
    @Override
    public void Visit(IVisitor visitor) {
        leftResult = visitor.runTour();
        leftOp.Visit(leftResult);
        rightResult = visitor.runTour();
        rightOp.Visit(rightResult);
        visitor.doUnion(leftResult, rightResult);
        leftResult = rightResult = null;
        visitor.end();
    }
}
