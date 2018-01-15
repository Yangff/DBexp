package com.totem.index;

import com.totem.database;
import com.totem.table.Attribute;

public class index {
    private database db;
    public index(database db) {
        this.db = db;
    }
    public OrderIndex openIndex(Attribute column) {
        return null;
    }

    /**
     * Get indexs of table
     * @param tableName table name
     * @return array of attributes has index
     */
    public Attribute[] getIndexs(String tableName) {return null; }
}
