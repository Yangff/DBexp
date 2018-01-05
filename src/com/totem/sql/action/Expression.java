package com.totem.sql.action;
import com.totem.sql.plan.Op;
import com.totem.sql.plan.Scan;
import com.totem.table.*;

import java.util.ArrayList;

public class Expression implements Action {
    public String GetOp() {
        return "Expression";
    }

    /**
     * Generate query plan based the index information on the directly
     * querying table.
     *
     * Notices that, `row_id` is available for every table, including
     * deputy one. However, use it is not recommended since it may
     * change for the same original item if it has left and enter the
     * deputy later.
     *
     * Later, it may provider more info for generating a better plan
     *
     * @param indexes the indexes of the directly querying table,
     *                including row_id.
     * @return a root node of the op-tree representing the query plan.
     */
    public Op GeneratePlan(ArrayList<Attribute> indexes) {
        return new Scan();
    }

    /**
     * Execute the expression by applying these values.
     * it can be used to filtering the scan result of plan
     * or calc the actually value of virtual attribute.
     * @param values the values used for execute the expression
     * @return the result value of applying arguments
     */
    public Value Apply(ArrayList<Cell> values) {
        return new Value();
    }

    /**
     * Get the interesting attributes by collecting the attributes from the
     * attributes list of all attributes from the deputy path.
     * @param attr the attributes may have on the deputy path
     * @return the attributes for a successfully apply.
     */
    public ArrayList<Attribute> GetInterestAttribute(ArrayList<Attribute> attr) {
        return null;
    }

    /**
     * Get the result type of the attribute by giving the type of existing
     * attributes.
     * STR+ANY=STR
     * TIME+INT=TIME
     * TIME+DOUBLE=TIME
     * INT (Op) DOUBLE = DOUBLE
     * INT (Op) INT = INT
     * DOUBLE (Op) DOUBLE = DOUBLE
     * boolean is int
     * Others cannot be calc so result in error.
     * @param attr
     * @return
     */
    public Type GetType(ArrayList<Attribute> attr) {
        return null;
    }
}
