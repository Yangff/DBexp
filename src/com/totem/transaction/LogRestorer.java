package com.totem.transaction;

import com.totem.storage.PhyTable;
import com.totem.storage.storage;

import java.io.IOException;
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
    private transaction trans;

    /**
     * Log restore task
     * @param trans transaction interface
     * @param oldLog old log to read
     * @param newLog new log to write
     */
    public LogRestorer(transaction trans, RandomAccessFile oldLog, RandomAccessFile newLog) {
        this.oldLog = oldLog;
        this.newLog = newLog;
        this.trans = trans;
        this.oldLogO = new Log(oldLog, false);
        this.newLogO = new Log(newLog, true);
    }

    /**
     * do log restore
     * @return succ?
     */
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
        oldLogO.eachTransactions((Log.LogEvent le) -> {
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
        try {
            newLogO.sync(); // <-- sync log file
            // redo
            newLogO.eachDetails((Log.LogDetail ld) -> {
                PhyTable tb = (PhyTable)trans.openPhyTable(ld.tbName);
                if (ld.type == Log.LogDetail.DetailType.Write) {
                    tb.writeById(ld.row, ld.col, ld.newValue);
                }
                if (ld.type == Log.LogDetail.DetailType.Insert){
                    tb.insertById(ld.row);
                }
                if (ld.type == Log.LogDetail.DetailType.Delete) {
                    tb.remove(ld.row);
                }
            });
            trans.storageSync();
            oldLog.close();
            newLog.close();
        } catch (IOException ioe){
            return false;
        }
        return true;
    }
}
