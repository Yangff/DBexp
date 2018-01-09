package com.totem.transaction;

import java.io.RandomAccessFile;

/**
 * deal with transaction ops
 * memory buffer all dirty data's log for fast look up
 */
public class Journal {
    public Log logs;

    public Journal(RandomAccessFile logFile){
        this.logs = new Log(logFile);
    }
}
