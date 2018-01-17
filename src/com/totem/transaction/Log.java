package com.totem.transaction;

import com.totem.storage.Page;
import com.totem.table.Cell;
import com.totem.table.Value;
import javafx.util.Pair;

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
    private int eCnt;
    private Page detailPage;
    private int dCnt;
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
            eventPage.buffer[2] = 0;
            detailPage.buffer[2] = 0;
            eventPage.buffer[3] = (byte)0xFF;
            detailPage.buffer[3] = (byte)0xFF;
            activePageId = 2;
            eCnt = 4;
            dCnt = 4;
        } else {
            int ePage = scanPageId(eventPage, 1);
            int dPage = scanPageId(detailPage, 2);
            eventPage.replace(ePage);
            eCnt = 4 + (int)readWord(eventPage, 1);
            detailPage.replace(dPage);
            dCnt = sizeDetail(detailPage);
            activePageId = 1 + Math.max(ePage, dPage);
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
        if (orgPage.buffer[3] == (byte)0xFF)
            p = p + 0xFF;
        else {
            if (orgPage.buffer[0] == id) // <-- id correct
                p = p + orgPage.buffer[3];
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
        while (orgPage.buffer[3] != 0) { // <-- have more page
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
                orgPage.buffer[3] = (byte) 0xFF;
            } else {
                orgPage.buffer[3] = (byte) (activePageId - orgPage.page);
            }
            orgPage.replace(activePageId);
            activePageId = activePageId + 1;
            orgPage.buffer[0] = (byte) newId;
            orgPage.buffer[1] = 0;
            orgPage.buffer[2] = 0;
            orgPage.buffer[3] = 0; // <-- no more page
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
        int xSize = 4;
        if (eCnt + xSize > 4096) {
            createPage(eventPage, 1);
            eCnt = 4;
        }
        long result = relatedId;
        switch (eventType) {
            case StartTransaction:
                break;
            case EndTransaction:
                result = (1L << 30) | result;
                break;
            case StartCheckpoint:
                result = result | (1L << 31);
                break;
            case EndCheckpoint:
                result = result | (1L<<31) | (1L<<30);
                break;
        }

        writeInt(eventPage, eCnt, result);
        eCnt = eCnt + 4;
        return true;
    }

    private long getDeailFlag(LogDetail.DetailType type, int tid){
        long result = tid;
        switch (type) {
            case Insert:
                result = result | (1L << 30);
                break;
            case Write:
                result = result | (1L << 31);
                break;
            case Delete:
                result = result | (1L << 31) | (1L << 30);
        }
        return result;
    }

    public boolean addInsertLog(int tid, int rowId) {
        int xSize = 8;
        if (dCnt + xSize > 4096) {
            createPage(detailPage, 2);
            dCnt = 4;
        }
        long flag = getDeailFlag(LogDetail.DetailType.Insert, tid);
        writeInt(detailPage, dCnt, flag);
        writeInt(detailPage, dCnt + 4, rowId);
        dCnt = dCnt + 8;
        return false;
    }

    public boolean addWriteLog(int tid, int rowId, Cell newValue) {
        String tbName = newValue.getAttribute().getTableName();
        byte[] strBytes = tbName.getBytes();
        int xSize = 4 + 2 + (strBytes.length + 2) + 4 + 1 + newValue.getValue().getType().getSize();
        if (dCnt + xSize > 4096) {
            createPage(detailPage, 2);
            dCnt = 4;
        }
        int xLength = xSize - 4 - 2;

        dCnt = writeInt(detailPage, dCnt, getDeailFlag(LogDetail.DetailType.Write, tid));
        dCnt = writeWord(detailPage, dCnt, xLength);
        dCnt = writeBytes(detailPage, dCnt, strBytes);

        detailPage.buffer[dCnt] = (byte)newValue.getValue().getType().getMetaType().ordinal();
        dCnt = dCnt + 1;
        dCnt = writeBytes(detailPage, dCnt, newValue.getValue().getSerial());
        return false;
    }

    public boolean addDeleteLog(int tid, int rowId) {
        int xSize = 8;
        if (dCnt + xSize > 4096) {
            createPage(detailPage, 2);
            dCnt = 4;
        }
        long flag = getDeailFlag(LogDetail.DetailType.Delete, tid);
        writeInt(detailPage, dCnt, flag);
        writeInt(detailPage, dCnt + 4, rowId);
        dCnt = dCnt + 8;
        return false;
    }

    public boolean removeTransaction(int tid) {
        // just don't commit it
        return true;
    }

    public boolean sync() {
        eventPage.sync();
        detailPage.sync();
        return true;
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

    private long readWord(Page page, int p) {
        return page.buffer[p] + page.buffer[p + 1] * 256;
    }

    private long readInt(Page page, int p){
        return readWord(page, p) + readWord(page, p + 2) * 256L*256L;
    }

    private int writeWord(Page page, int p, long v){
        page.buffer[p] = (byte)(v & 0xff);
        page.buffer[p + 1] = (byte)((v >> 8)&0xff);
        return p + 2;
    }

    private int writeInt(Page page, int p, long v){
        writeWord(page, p, v & 0xffff);
        writeWord(page, p + 2, (v>>16) & 0xffff);
        return p + 4;
    }

    private int writeBytes(Page page, int p, byte[] bytes){
        p = writeWord(page, p, bytes.length);
        for (byte aByte : bytes) {
            page.buffer[p] = aByte;
            p = p + 1;
        }
        return p;
    }

    private int sizeDetail(Page page){
        int p = 4;
        int cnt = (int)readWord(page, 1);
        for (int i = 0; i < cnt; i++){
            int head = (int)readInt(page, p);
            int flags = (head >> 30) & 0b11;
            if (flags == 0)
                break;
            if (flags == 1 || flags == 3){
                // insert/delete
                p = p + 8;
            } else {
                // flag, len, read len
                p = p + 4 + 2 + (int)readWord(page, p + 4);
            }
        }
        return p;
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
0x00 [2bits] InsertOp 1/WriteOp 2/DeleteOp 3
0x00 [30bits] tid (all 0 for removed)
--- for insert/delete ---
0x04 [Int] row_id
--- for write ---
0x04 [Word] length
0x06 [String] Effected Table
0x?? [Int] Effected Column
0x?? [Byte] type
0x?? [Type] NewData
 */