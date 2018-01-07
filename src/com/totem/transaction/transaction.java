package com.totem.transaction;

import com.totem.database;
import com.totem.storage.ITable;
import com.totem.storage.PhyTable;

import java.io.FileNotFoundException;
import java.io.RandomAccessFile;

public class transaction {
    private database db;
    private RandomAccessFile journalFile;
    private boolean transaction_start;

    public transaction(database db){
        transaction_start = false;
        this.db = db;
    }
    public boolean openFS(){
        try {
            journalFile = db.Storage.getJournalFile();
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
        if (!openFS())
            return false;
        return transaction_start = true; // if succ
    }
    public boolean createJournal(){
        if (!openFS())
            return false;
        return transaction_start = true; // if succ
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

}
