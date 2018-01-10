package com.totem.transaction;

import com.totem.database;
import com.totem.storage.ITable;
import com.totem.storage.PhyTable;

import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.util.HashMap;

public class transaction {
    private database db;
    private RandomAccessFile oldJournalFile;
    private RandomAccessFile newJournalFile;
    private HashMap<String, ITable> tableMap;


    private boolean transaction_start;

    private Journal journal;

    public transaction(database db){
        transaction_start = false;
        this.db = db;
        tableMap = new HashMap<>();
    }

    /**
     * open journal file object
     * @param type journal type, new or old
     * @return succ?
     */
    private boolean openFS(String type){
        try {
            if (type == "old")
                oldJournalFile = db.Storage.getOldJournalFile();
            else
                newJournalFile = db.Storage.getNewJournalFile();
            return true;
        } catch (FileNotFoundException e){
        }
        return false;
    }

    /**
     * start transaction
     * @return transaction id
     */
    public int startTransaction(){
        if (!transaction_start)
            return -1;
        return journal.startTransaction();
    }

    /**
     * commit transaction
     * @param tid transaction it
     * @return succ?
     */
    public boolean commitTransaction(int tid){
        if (!transaction_start)
            return true;
        return journal.commitTransaction(tid);
    }

    /**
     * rollback transaction
     * @param id transaction id
     * @return succ?
     */
    public boolean rollbackTransaction(int id) {
        if (!transaction_start)
            return true;
        return journal.rollbackTransaction(id);
    }

    /**
     * restore journal from file
     * @return succ?
     */
    public boolean restoreJournal(){
        if (!openFS("old"))
            return false;

        if (!openFS("new"))
            return false;

        LogRestorer restorer = new LogRestorer(oldJournalFile, newJournalFile);
        if (restorer.start()) {
            if (restorer.done()) {
                db.Storage.migrateLog();
                oldJournalFile = newJournalFile = null;
                if (!openFS("old"))
                    return false;
                if (initJournal()) {
                    transaction_start = true;
                }
            }
        }
        return transaction_start; // if succ
    }

    /**
     * create new journal
     * @return succ?
     */
    public boolean createJournal(){
        db.Storage.deleteOldJournalFile();
        if (!openFS("old"))
            return false;
        if (initJournal()) {
            transaction_start = true;
        }
        return transaction_start; // if succ
    }

    /**
     * do checkpoint now
     * @return succ?
     */
    public boolean checkPoint(){
        if (!transaction_start)
            return true;
        return journal.checkPoint();
    }

    /**
     * create a log table for execute engine
     * @param orgTable
     * @return
     */
    public ITable openLogTable(PhyTable orgTable){
        if (!transaction_start)
            return orgTable;
        if (orgTable == null)
            return null;
        String tbName = orgTable.getTableName();
        if (tableMap.containsKey(tbName)) {
            return tableMap.get(orgTable.getTableName());
        } else {
            ITable newTable = new LogTable(journal, orgTable);
            tableMap.put(tbName, newTable);
            return newTable;
        }
    }

    /**
     * init journal object from file object
     * @return succ?
     */
    private boolean initJournal() {
        if (oldJournalFile != null) {
            journal = new Journal(oldJournalFile);
            return true;
        }
        return false;
    }
}
