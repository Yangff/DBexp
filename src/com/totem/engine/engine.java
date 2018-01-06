package com.totem.engine;

import com.totem.database;
import com.totem.storage.ITable;
import com.totem.table.TableScheme;

import java.util.Iterator;

public class engine {
    /*
    Step 1. SQL Parser
         2. Execute Phy Plan
         3. Calc required attributes for where cond. and collect them
         4. Calc required attributes for deputy migration. and collect them (notices that you can
            sort items by row_id of original class to accelerate this collection one scan for all
            O(MlogM + NlogM) ) N is size of table and M is size of results.
         5. Filter by where cond.
         6. Filter by Deputy migration cond.
         7. Calc required attributes for final result and collect them
         8. Do final result, eliminate useless attributes

         * Or you can have that pointer table for the deputy relations between objects as the design said,
         * I personally think it would be better to just create a index on gid (global object id for the original
         * object) and figure out where to find required data...
         * They are both O(MlogN), however the second one can reduce one table and you just have to maintain the index
         * normal index, nothing special.

      Without a iterator combinator, we scan all data we need to memory and do calc.
      Or, every step is a iterator and combinator and we can get result one by one.
     */
    private database db;
    public engine(database db) {
        this.db = db;
    }

    public boolean Execute(String sql){
        return false;
    }

    public Iterator GetResult(){
        return null;
    }

    public boolean Clean(){
        return true;
    }

    public TableModel OpenSysTable(String def) {
        TableScheme tableScheme = TableScheme.CreateScheme(def);
        TableModel model = new TableModel(tableScheme, db.Storage.OpenSysTable(tableScheme.tableName, tableScheme));
        return model;
    }
}
