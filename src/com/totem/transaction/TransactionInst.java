package com.totem.transaction;

import com.totem.storage.ITable;
import com.totem.storage.PhyTable;
import com.totem.table.Cell;
import com.totem.table.Value;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;

public class TransactionInst {
    private int tid;
    private Journal journal;

    HashMap<String, DirtyMap> dirtyMaps;

    public TransactionInst(int tid, Journal journal){
        this.tid = tid;
        this.journal = journal;
        journal.logs.addEventLog(Log.EventType.StartTransaction, tid);
    }

    public int getTid() {
        return tid;
    }

    /**
     * commit transaction
     * @return succ?
     */
    public boolean commitTransaction(){
        journal.logs.addEventLog(Log.EventType.EndCheckpoint, tid);
        if (journal.logs.sync()){
            // log sync done
            for (Map.Entry<String, DirtyMap> m:dirtyMaps.entrySet()){
                PhyTable tb = journal.root.openPhyTable(m.getKey());
                DirtyMap dm = m.getValue();
                for (Integer rm : dm.removedRow) {
                    tb.remove(rm);
                }
                for (Integer is : dm.insertedRow) {
                    tb.insertById(is);
                }
                for (Map.Entry<Integer, SortedMap<Integer, Cell>> css: dm.rows.entrySet()){
                    int row = css.getKey();
                    for (Map.Entry<Integer, Cell> cs: css.getValue().entrySet()) {
                        tb.writeById(row, cs.getKey(), cs.getValue().getValue());
                    }
                }
            }
            return true;
        }
        return false;
    }

    /**
     * rollback transaction
     * @return succ?
     */
    public boolean rollbackTransaction() {
        journal.logs.removeTransaction(tid);
        journal.logs.freeTransaction(tid);
        return true;
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
        return new LogTable(journal.logs, getDirtyMap(orgTable.getTableName()), tid, orgTable);
    }
}
