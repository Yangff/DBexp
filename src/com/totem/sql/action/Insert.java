package com.totem.sql.action;

import com.totem.table.Value;

import java.util.HashMap;

public class Insert implements Action {
    private String tableName;
    private HashMap<String, Value>[] values;

    public String GetOp() {
        return "Insert";
    }
}
