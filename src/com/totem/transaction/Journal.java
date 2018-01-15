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

    HashMap<Integer, TransactionInst> trans;

    public Journal(RandomAccessFile logFile){
        this.logs = new Log(logFile);
    }

    public TransactionInst startTransaction(){
        int tid = logs.allocTransaction();
        TransactionInst inst = new TransactionInst(tid, this);
        trans.put(tid, inst);
        return inst;
    }

    public boolean commitTransaction(int tid){
        return false;
    }

    public boolean rollbackTransaction(int tid){
        return false;
    }

    public boolean checkPoint(){
        return false;
    }

}
