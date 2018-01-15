package com.totem.transaction;

import com.totem.storage.ITable;
import com.totem.storage.PhyTable;

import java.util.HashMap;

public class TransactionInst {
    private int tid;
    private Journal journal;

    HashMap<String, DirtyMap> dirtyMaps;

    public TransactionInst(int tid, Journal journal){
        this.tid = tid;
        this.journal = journal;
    }

    public int getTid() {
        return tid;
    }

    /**
     * commit transaction
     * @return succ?
     */
    public boolean commitTransaction(){
        return journal.commitTransaction(tid);
    }

    /**
     * rollback transaction
     * @return succ?
     */
    public boolean rollbackTransaction() {
        return journal.rollbackTransaction(tid);
    }

    public DirtyMap getDirtyMap(String tbName){
        if (dirtyMaps.containsKey(tbName)) {
            return dirtyMaps.get(tbName);
        } else {
            DirtyMap newMap = new DirtyMap();
            dirtyMaps.put(tbName, newMap);
            return newMap;
        }
    }
    /**
     * create a log table for execute engine
     * @param orgTable original table object
     * @return table interface
     */
    public ITable openLogTable(PhyTable orgTable){
        if (orgTable == null)
            return null;
        return new LogTable(getDirtyMap(orgTable.getTableName()), tid, orgTable);
    }
}
