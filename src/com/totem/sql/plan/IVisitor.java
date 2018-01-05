package com.totem.sql.plan;

import com.totem.table.Attribute;
import com.totem.table.Value;

public interface IVisitor {
    /**
     * Start tour a sub Op-tree
     * @return a visitor that tour the subtree
     */
    IVisitor runTour();

    /**
     * Fetch rows from table
     * filter(db[attr] == value)
     * attr might be not a index
     * @param attr interesting attribute
     * @param value the target value
     */
    void doFetch(Attribute attr, Value value);

    /**
     * run a scan on range L,R
     * Notice that L and R might be infinity
     * attr may be not a index
     * @param attr
     * @param L left bound of scan
     * @param R right bound of scan
     * @param type scan type
     *             0 - scan in [L, R] range
     *             1 - scan in [L, R) range
     *             2 - scan in (L, R] range
     *             3 - scan in (L, R) range
     */
    void doRangeScan(Attribute attr, Value L, Value R, int type);

    /**
     * do intersect (based on row id of the original class) on result of L and R
     * aka. (L and R)
     * @param L l-result
     * @param R r-result
     */
    void doIntersect(IVisitor L, IVisitor R);

    /**
     * do union (based on row id of the original class) on result of L and R
     * aka. (L and R)
     * @param L l-result
     * @param R r-result
     */
    void doUnion(IVisitor L, IVisitor R);

    /**
     * Scan whole table,
     * also means nothing we can do for you. (at least on this subtree)
     * it maybe a always true expression
     * or the condition related to a attribute that is not indexed.
     * or the condition is too complex for this planner
     * (e.g. select a, b from T where a + b > 10, a and b are index
     * or select T1.a, T2.a from T as T1, T as T2 where T1.a + T2.a > 10
     * and a are index)
     * to achieve these types of querying we need to modify index structure
     * or change a executor (also visitor) and make things not so obvious.
     */
    void doScan();

    /**
     * Visit on the sub-tree has done.
     */
    void end();
}
