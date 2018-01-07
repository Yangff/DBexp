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

    private HashMap<String, ITable> tableCache;
    private HashMap<String, TableModel> sysTableCache;

    public engine(database db) {
        this.db = db;
        tableCache = new HashMap<>();
        sysTableCache = new HashMap<>();
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

    /*
        FIXME: Decide to maintain system table by storage or engine [engine parts]
        If you want to maintain system table by engine, keep these code and add a initSysTable,
        call it when init.
        Otherwise, openSysTable should accept a tableName, and get corresponding table from storage for create
        table model. Maybe, table model should be moved to storage either.
     */
    public TableModel openSysTable(String def) {
        TableScheme tableScheme = TableScheme.CreateScheme(def);
        if (tableScheme == null)
            return null;
        if (sysTableCache.containsKey(tableScheme.tableName))
            return sysTableCache.get(tableScheme.tableName);

        TableModel model = new TableModel(tableScheme, db.Storage.openSysTable(tableScheme));
        sysTableCache.put(tableScheme.tableName, model);

        return model;
    }

    public ITable openTable(String tableName) {
        if (tableCache.containsKey(tableName))
            return tableCache.get(tableName);
        ITable orgTable = db.Storage.openTable(tableName);
        ITable logTable = db.Transaction.openLogTable((PhyTable)orgTable);
        tableCache.put(tableName, logTable);
        return logTable;
    }
}
