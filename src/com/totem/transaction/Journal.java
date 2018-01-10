package com.totem.transaction;

import java.io.RandomAccessFile;
import java.util.HashMap;

/**
 * deal with transaction ops
 * memory buffer all dirty data's log for fast look up
 */
public class Journal {
    public Log logs;

    private HashMap<String, DirtyMap> dirtyMaps;

    public Journal(RandomAccessFile logFile){
        this.logs = new Log(logFile);
    }

    public int startTransaction(){
        return 0;
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
