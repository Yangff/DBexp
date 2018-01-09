package com.totem.transaction;

import com.totem.database;
import com.totem.storage.ITable;
import com.totem.storage.PhyTable;

import java.io.FileNotFoundException;
import java.io.RandomAccessFile;

public class transaction {
    private database db;
    private RandomAccessFile oldJournalFile;
    private RandomAccessFile newJournalFile;

    private boolean transaction_start;

    private Journal journal;

    public transaction(database db){
        transaction_start = false;
        this.db = db;
    }

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

    public int startTransaction(){
        if (!transaction_start)
            return -1;
        return 0;
    }

    public boolean commitTransaction(int tid){
        if (!transaction_start)
            return true;
        return false;
    }

    public boolean rollbackTransaction(int id) {
        if (!transaction_start)
            return true;
        return false;
    }

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

    public boolean createJournal(){
        db.Storage.deleteOldJournalFile();
        if (!openFS("old"))
            return false;
        if (initJournal()) {
            transaction_start = true;
        }
        return transaction_start; // if succ
    }

    public boolean checkPoint(){
        if (!transaction_start)
            return true;
        return false;
    }

    public ITable openLogTable(PhyTable orgTable){
        if (!transaction_start)
            return orgTable;
        return null;
    }

    private boolean initJournal() {
        if (oldJournalFile != null) {
            journal = new Journal(oldJournalFile);
            return true;
        }
        return false;
    }
}
