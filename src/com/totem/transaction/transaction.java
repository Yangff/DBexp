package com.totem.transaction;

import com.totem.database;
import com.totem.storage.ITable;
import com.totem.storage.PhyTable;

import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.util.HashMap;

/**
 * transaction interface
 */
public class transaction {
    private database db;
    private RandomAccessFile oldJournalFile;
    private RandomAccessFile newJournalFile;


    private boolean transaction_start;

    private Journal journal;

    /**
     * create transaction instance
     * @param db database object
     */
    public transaction(database db){
        transaction_start = false;
        this.db = db;
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
    public TransactionInst startTransaction(){
        if (!transaction_start)
            return null;
        return journal.startTransaction();
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

        LogRestorer restorer = new LogRestorer(this, oldJournalFile, newJournalFile);
        if (restorer.start()) {
            if (restorer.done()) {
                db.Storage.migrateLog();
                oldJournalFile = newJournalFile = null;
                if (!openFS("old"))
                    return false;
                if (initJournal(false)) {
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
        if (initJournal(true)) {
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
     * init journal object from file object
     * @return succ?
     */
    private boolean initJournal(boolean create) {
        if (oldJournalFile != null) {
            journal = new Journal(this, oldJournalFile, create);
            return true;
        }
        return false;
    }

    /**
     * open physical table (for redo)
     * @param tbName table name
     * @return table
     */
    public PhyTable openPhyTable(String tbName){
        return (PhyTable)db.Storage.openTable(tbName);
    }

    /**
     * sync with storage
     * @return succ?
     */
    public boolean storageSync() {
        return db.Storage.sync();
    }
}
