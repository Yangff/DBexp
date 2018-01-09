package com.totem.transaction;

import java.io.RandomAccessFile;

/**
 * Read old log(by {@code Log}) and write only useful part to new log file
 * a.k.a. inspect checkpoints
 */
public class LogRestorer {
    private RandomAccessFile oldLog, newLog;
    public LogRestorer(RandomAccessFile oldLog, RandomAccessFile newLog) {
        this.oldLog = oldLog;
        this.newLog = newLog;
    }

    public boolean start(){
        return true;
    }

    /**
     * write everything in buffer to file and close all files
     * @return succ?
     */
    public boolean done() {
        return true;
    }
}
