package com.totem.sql.action;

public class CreateIndex implements Action {
    private String tableName;
    private String indexName;

    public String getOp() {
        return "CreateIndex";
    }
}
