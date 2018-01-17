package com.totem.transaction;

import java.io.RandomAccessFile;
import java.util.HashSet;
import java.util.TreeSet;

/**
 * Read old log(by {@code Log}) and write only useful part to new log file
 * a.k.a. inspect checkpoints
 */
public class LogRestorer {
    private RandomAccessFile oldLog, newLog;
    private Log oldLogO, newLogO;
    public LogRestorer(RandomAccessFile oldLog, RandomAccessFile newLog) {
        this.oldLog = oldLog;
        this.newLog = newLog;
        this.oldLogO = new Log(oldLog, false);
        this.newLogO = new Log(newLog, true);
    }

    public boolean start(){
        /*
            scan all logs
            pair all transactions, remove not paired transactions.
            find start and end checkpoint
            for last pairs of checkpoint, remove all paired transaction above start checkpoint
            remove other checkpoint, leave last one
            write leaved transactions into new file.
         */
        HashSet<Integer> openTransactions = new HashSet<>();
        TreeSet<Integer> redoTransactions = new TreeSet<>();
        HashSet<Integer> okeyTransactions = new HashSet<>();
        // step1. scan all open transactions
        oldLogO.eachTids((Log.LogEvent le) -> {
            if (le.type == Log.EventType.StartTransaction) {
                openTransactions.add(le.tid);
            }
            if (le.type == Log.EventType.EndTransaction) {
                if (openTransactions.contains(le.tid)) {
                    openTransactions.remove(le.tid);
                    redoTransactions.add(le.tid);
                }
            }
            if (le.type == Log.EventType.StartCheckpoint){
                okeyTransactions.addAll(redoTransactions);
            }
            if (le.type == Log.EventType.EndCheckpoint){
                redoTransactions.removeAll(okeyTransactions);
                okeyTransactions.clear();
            }
        });
        for (Integer open : redoTransactions){
            newLogO.addEventLog(Log.EventType.StartTransaction, open);
            newLogO.addEventLog(Log.EventType.EndTransaction, open);
        }
        oldLogO.eachDetails((Log.LogDetail ld) -> {
            if (redoTransactions.contains(ld.tid)) {
                if (ld.type == Log.LogDetail.DetailType.Write) {
                    newLogO.addWriteLog(ld.tid, ld.row, ld.tbName, ld.newValue);
                }
                if (ld.type == Log.LogDetail.DetailType.Delete) {
                    newLogO.addDeleteLog(ld.tid, ld.row);
                }
                if (ld.type == Log.LogDetail.DetailType.Insert) {
                    newLogO.addInsertLog(ld.tid, ld.row);
                }
            }
        });
        return true;
    }

    /**
     * write everything in buffer to file and close all files
     * @return succ?
     */
    public boolean done() {
        /*
        write file
        close file
         */
        return true;
    }
}
