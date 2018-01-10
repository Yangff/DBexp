package com.totem.sql.action;

public class Select implements Action {
    private String tableName;
    private String[] migrationPath;

    private Expression[] interestedAttribute;

    private Expression cond;

    public String getOp() {
        return "Select";
    }
}
