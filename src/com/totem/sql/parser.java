package com.totem.sql;

import com.totem.database;
import com.totem.sql.action.Expression;

public class parser {
    private database db;
    public parser(database db){
        this.db = db;
    }

    /**
     * parse {@code sql} can generate action
     * used for parse user input and switch expression of virtual attributes
     * @param sql sql input, maybe expression
     * @return Action, one of the followings: CreateIndex, CreateTable, Insert, Select, Expression
     */
    public Expression Parse(String sql){
        return null;
    }

}
