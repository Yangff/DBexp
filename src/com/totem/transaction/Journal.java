package com.totem.transaction;

import com.totem.sql.plan.Intersect;

import java.io.RandomAccessFile;
import java.util.HashMap;

/**
 * deal with transaction ops
 * memory buffer all dirty data's log for fast look up
 */
public class Journal {
    public Log logs; // maybe private?
    public transaction root;

    HashMap<Integer, TransactionInst> trans;

    /**
     * Journal instance
     * @param root transaction instance
     * @param logFile log file
     * @param createJournal should I create basic structure?
     */
    public Journal(transaction root, RandomAccessFile logFile, boolean createJournal){
        this.root = root;
        this.logs = new Log(logFile, createJournal);
    }

    /**
     * start a transaction
     * @return transaction object for read/write/... logs
     */
    public TransactionInst startTransaction(){
        int tid = logs.allocTransaction();
        TransactionInst inst = new TransactionInst(tid, this);
        trans.put(tid, inst);
        return inst;
    }

    /**
     * do checkpoint, put all memory data to storage
     * @return succ?
     */
    public boolean checkPoint(){
        logs.addEventLog(Log.EventType.StartCheckpoint, 1);
        logs.sync(); // <- make sure log written
        if (root.storageSync()){
            logs.addEventLog(Log.EventType.EndCheckpoint, 1);
            return logs.sync();
        }
        return false;
    }

}
