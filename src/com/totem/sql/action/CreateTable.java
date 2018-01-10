package com.totem.sql.action;

import com.totem.table.Attribute;
import com.totem.table.TableScheme;

import java.util.HashMap;

public class CreateTable implements Action {
    private TableScheme tableScheme;

    // deputy class related
    private boolean isDeptyTable;
    private String parentTable;
    private String switchExpression;
    private HashMap<String, String> virtualRelation;

    public String getOp() {
        return "CreateTable";
    }
}
