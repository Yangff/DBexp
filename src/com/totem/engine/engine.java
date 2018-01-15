package com.totem.engine;

import com.totem.database;
import com.totem.storage.ITable;
import com.totem.storage.PhyTable;
import com.totem.table.TableScheme;

import java.util.HashMap;
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

         * Or you can have that pointer table for the deputy relations between objects as the design said,
         * I personally think it would be better to just create a index on gid (global object id for the original
         * object) and figure out where to find required data...
         * They are both O(MlogN), however the second one can reduce one table and you just have to maintain the index
         * normal index, nothing special.

      Without a iterator combinator, we scan all data we need to memory and do calc.
      Or, every step is a iterator and combinator and we can get result one by one.
     */
    private database db;

    private HashMap<String, ITable> tableCache;
    public engine(database db) {
        this.db = db;
        tableCache = new HashMap<>();
    }

    public boolean execute(String sql){
        return false;
    }

    public Iterator getResult(){
        return null;
    }

    public boolean clean(){
        return true;
    }

    public ITable openTable(String tableName) {
        if (tableCache.containsKey(tableName))
            return tableCache.get(tableName);
        ITable orgTable = db.Storage.openTable(tableName);
        tableCache.put(tableName, orgTable);
        return orgTable;
    }
}
