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
}
