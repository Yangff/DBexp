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

    public Journal(transaction root, RandomAccessFile logFile, boolean createJournal){
        this.root = root;
        this.logs = new Log(logFile, createJournal);
    }

    public TransactionInst startTransaction(){
        int tid = logs.allocTransaction();
        TransactionInst inst = new TransactionInst(tid, this);
        trans.put(tid, inst);
        return inst;
    }

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
