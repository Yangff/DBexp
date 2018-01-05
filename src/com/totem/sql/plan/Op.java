package com.totem.sql.plan;

public interface Op {
    /**
     * Visit this op-tree
     * @param visitor the implement of IVisitor,
     *                carry out the action based
     *                on the plan.
     *                Now, the plan would run
     *                in a naive way that not
     *                optimal.
     */
    void Visit(IVisitor visitor);
}
