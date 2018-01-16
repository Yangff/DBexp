package com.totem.transaction;

import com.totem.storage.Page;
import com.totem.table.Value;

import java.io.RandomAccessFile;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Read and Write logs to file
 */
public class Log {
    private final RandomAccessFile logFile;
    private SortedSet<Integer> tidPool;

    private Page eventPage;
    private Page detailPage;
    private int activePageId;

    public Log(RandomAccessFile logFile, boolean createScheme){
        this.logFile = logFile;
        this.tidPool = new TreeSet<>();
        this.eventPage = new Page(logFile, 0);
        this.detailPage = new Page(logFile, 1);
        if (createScheme) {
            eventPage.buffer[0] = 1;
            detailPage.buffer[0] = 2;
            eventPage.buffer[1] = 0;
            detailPage.buffer[1] = 0;
            eventPage.buffer[2] = (byte)0xFF;
            detailPage.buffer[2] = (byte)0xFF;
            activePageId = 2;
        } else {
            activePageId = 1 + Math.max(scanPageId(eventPage, 1), scanPageId(detailPage, 2));
        }
    }

    /**
     * find next legal page for given type
     * @param orgPage original page holder
     * @param newPage page holder
     * @param id block type
     * @return nextPage id
     */
    private int nextBlock(Page orgPage, Page newPage, int id) {
        // find next
        // calc new page id by reading org page data
        int p = (int)orgPage.page;
        if (orgPage.buffer[2] == (byte)0xFF)
            p = p + 0xFF;
        else {
            if (orgPage.buffer[0] == id) // <-- id correct
                p = p + orgPage.buffer[2];
            else p = p + 1;
        }
        newPage.replace(p); // <-- move new page
        while (newPage.buffer[0] != id) { // <-- type incorrect, scan
            if (newPage.buffer[0] == 0) // <-- illegal page
                return p;
            p = p + 1;
            newPage.replace(p); // <-- move next
        }
        return p;
    }

    /**
     * get last page of given page and type
     * @param orgPage original page
     * @param id id
     * @return last id
     */
    private int scanPageId(Page orgPage, int id) {
        int nextPageId = (int)orgPage.page;
        Page newPage = new Page(logFile, orgPage.page);
        while (orgPage.buffer[2] != 0) { // <-- have more page
            nextPageId = nextBlock(orgPage, newPage, id); // <-- get next block_id == id
            if (newPage.buffer[0] == 0)
                return nextPageId - 1;
            orgPage = newPage;
        }
        return nextPageId;
    }

    private boolean createPage(Page orgPage, int newId) {
        try {
            if (activePageId - orgPage.page > 0xFE) {
                orgPage.buffer[2] = (byte) 0xFF;
            } else {
                orgPage.buffer[2] = (byte) (activePageId - orgPage.page);
            }
            orgPage.replace(activePageId);
            activePageId = activePageId + 1;
            orgPage.buffer[0] = (byte) newId;
            orgPage.buffer[1] = 0;
            orgPage.buffer[2] = 0; // <-- no more page
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean createEventBlock() {
        return createPage(eventPage, 1);
    }

    private boolean createDetailBlock() {
        return createPage(detailPage,2);
    }

    public enum LogType{
        Event, Details
    }

    public enum EventType {
        StartTransaction, EndTransaction, StartCheckpoint, EndCheckpoint
    }

    // maybe useful ?
    public static class LogDetail {
        public enum DetailType {
            Insert, Write, Delete
        }
        public DetailType type;
        public int row;
        public int col; // <- possible
        public Value newValue; // <- possible
    }

    public boolean addEventLog(EventType eventType, int relatedId) {
        return true;
    }

    public boolean addInsertLog(int tid, int rowId) {
        return false;
    }

    public boolean addWriteLog(int tid, int rowId, int colId, Value newValue) {
        return false;
    }

    public boolean addDeleteLog(int tid, int rowId) {
        return false;
    }

    public boolean removeTransaction(int tid) {
        return false;
    }

    public boolean sync() {
        return false;
    }

    // transaction allocator

    private int tidNow = 1;
    public int nextTid(){
        while (tidPool.contains(tidNow)) {
            tidNow = tidNow + 1;
        }
        return tidNow;
    }

    public int allocTransaction() {
        int tid = nextTid();
        tidPool.add(tid);
        return tid;
    }

    public int freeTransaction(int tid) {
        tidPool.remove(tid);
        return tid;
    }

    public int transactionCount(){
        return tidPool.size();
    }

}

/*
Binary layout of log files
Each block is a 4k-page
[Head Section, Size = 4bytes]
0x00 [Byte] Block Type, 0 for nothing, 1 for event and 2 for details
0x01 [Word] Block length, numbers of journals contains in this block
0x03 [Byte] How far is the next block of this type, 0x00 for larger than not exists, 0xFF for larger than 0xFE
------ eventlog ------
[Content Section, events log, for each log, Size=5bytes]
0x00 [2 bits] EventType, enum {StartTransaction, EndTransaction, StartCheckpoint, EndCheckpoint}
0x00 [30bits] related transaction id, use zero for removed event
Read as a UINT and use 2-msb as event type
for endCheckpoint, marks the pos of paired start checkpoint
------ details ------
[Content Section, details]
0x00 [2bits] RemovedOp/InsertOp/WriteOp/DeleteOp
0x00 [30bits] tid, use when its too long (row_id for insert op)
0x01 [String] Effected Table
0x?? [Int] Effected Column
--- for write ---
0x?? [Byte] type
0x?? [Type] NewData
 */