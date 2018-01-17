package com.totem.transaction;

import com.totem.storage.Page;
import com.totem.table.Type;
import com.totem.table.Value;

import java.io.RandomAccessFile;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Consumer;

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

    /**
     * New Log Object
     * @param logFile log file
     * @param createScheme should I create a log scheme?
     */
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
            detailPage.markDirty();
            eventPage.markDirty();
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
            recoveryTids();
        }
    }

    /**
     * each all transactions
     * @param call callback function for every transaction info
     */
    public void eachTransactions(Consumer<LogEvent> call){
        long orgPage = eventPage.page;
        eventPage.replace(0);
        while (true){
            long len = readWord(eventPage, 1);
            int pos = 4;
            for (int i = 0; i < len; i++){
                long tid = readInt(eventPage, pos + 4 * i);
                int flags = (int)(tid >> 30);
                tid = tid & 0b00111111111111111111111111111111;
                LogEvent ev = new LogEvent();
                ev.tid = (int)tid;
                if (tid != 0) {
                    switch (flags) {
                        case 0:
                            ev.type = EventType.StartTransaction;
                            break;
                        case 1:
                            ev.type = EventType.EndTransaction;
                            break;
                        case 2:
                            ev.type = EventType.StartCheckpoint;
                            break;
                        case 3:
                            ev.type = EventType.EndCheckpoint;
                    }
                    call.accept(ev);
                }
            }
            if (eventPage.buffer[3] == 0)
                break;
            int pg = getNextPage(eventPage, 1);
            if (pg != -1)
                eventPage.replace(pg);
            else break;
        }
        eventPage.replace(orgPage);
    }

    /**
     * each all detail logs
     * @param call callback function for details
     */
    public void eachDetails(Consumer<LogDetail> call){
        long orgPage = detailPage.page;
        detailPage.replace(1);
        while (true){
            long len = readWord(detailPage, 1);
            int pos = 4;
            for (int i = 0; i < len; i++){
                long tid = readInt(detailPage, pos);
                pos = pos + 4;
                int flags = (int)(tid >> 30);
                tid = tid & 0b00111111111111111111111111111111;
                LogDetail ld = new LogDetail();
                ld.tid = (int)tid;
                if (tid != 0) {
                    switch (flags) {
                        case 0:
                            return ; // <-- bad flag
                        case 1:
                            ld.type = LogDetail.DetailType.Insert;
                            break;
                        case 2:
                            ld.type = LogDetail.DetailType.Write;
                            break;
                        case 3:
                            ld.type = LogDetail.DetailType.Delete;
                    }
                    long row_id = readInt(detailPage, pos);
                    ld.row = (int)row_id;
                    pos = pos + 4;
                    if (ld.type == LogDetail.DetailType.Insert || ld.type == LogDetail.DetailType.Delete){
                        call.accept(ld);
                    } else {
                        pos = pos + 2; // skip len
                        int sLen = (int)readWord(detailPage, pos);
                        String tbName = readString(detailPage, pos);
                        pos = pos + 2 + sLen;
                        ld.tbName = tbName;
                        int col = (int)readInt(detailPage, pos);
                        pos = pos + 4;
                        ld.col = col;
                        int type = detailPage.buffer[pos];
                        pos = pos + 1;
                        Type xType = new Type();
                        Value v = new Value();
                        v.setType(xType);
                        // Null, Int, Chars, Double, DateTime, PInfinity, NInfinity
                        byte[] data = null;
                        switch (type) {
                            case 1:
                                xType.setMetaType(Type.MetaType.Int);
                                data = new byte[4];
                                System.arraycopy(detailPage.buffer, pos, data, 0, 4);
                                pos = pos + 4;
                                break;
                            case 2:
                                xType.setMetaType(Type.MetaType.Chars);
                                int charsLen = (int)readWord(detailPage, pos);
                                xType.setStrLen(charsLen);
                                pos = pos + 2;
                                data = new byte[charsLen];
                                System.arraycopy(detailPage.buffer, pos, data, 0, charsLen);
                                pos = pos + charsLen;
                                break;
                            case 3:
                                xType.setMetaType(Type.MetaType.Double);
                                data = new byte[8];
                                System.arraycopy(detailPage.buffer, pos, data, 0, 8);
                                pos = pos + 8;
                                break;
                            case 4:
                                data = new byte[8];
                                System.arraycopy(detailPage.buffer, pos, data, 0, 8);
                                pos = pos + 8;
                                xType.setMetaType(Type.MetaType.DateTime);
                                break;
                        }
                        if (data == null)
                            return; // <-- broken data
                        v.fromSerial(data);
                        ld.newValue = v;
                        call.accept(ld);
                    }

                }
            }
            if (detailPage.buffer[3] == 0) {
                break;
            }
            int pg = getNextPage(detailPage, 1);
            if (pg != -1)
                detailPage.replace(pg);
            else
                break;
        }
        detailPage.replace(orgPage);
    }

    /**
     * recovery used tids
     */
    private void recoveryTids() {
        eachTransactions((LogEvent le) -> {
            if (le.type == EventType.StartTransaction) {
                tidPool.add(le.tid);
            }
        });
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

    /**
     * get next page id
     * @param orgPage original page
     * @param id page type
     * @return next page id
     */
    private int getNextPage(Page orgPage, int id){
        Page newPage = new Page(logFile, orgPage.page);
        if (orgPage.buffer[3] != 0){
            return nextBlock(orgPage, newPage, id);
        }
        return -1;
    }

    /**
     * create new page
     * @param orgPage last page
     * @param newId page type
     * @return succ?
     */
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
            orgPage.markDirty();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * create event page
     * @return succ?
     */
    private boolean createEventBlock() {
        return createPage(eventPage, 1);
    }

    /**
     * create detail page
     * @return succ?
     */
    private boolean createDetailBlock() {
        return createPage(detailPage,2);
    }

    /**
     * type of logs
     */
    public enum LogType{
        Event, Details
    }

    /**
     * type of events
     */
    public enum EventType {
        StartTransaction, EndTransaction, StartCheckpoint, EndCheckpoint
    }

    /**
     * log detail
     */
    public static class LogDetail {
        /**
         * Ops
         */
        public enum DetailType {
            Insert, Write, Delete
        }

        /**
         * Op type
         */
        public DetailType type;
        /**
         * related table name
         */
        public String tbName;
        /**
         * transaction id
         */
        public int tid;
        /**
         * row_id
         */
        public int row;
        /**
         * column_id may be empty
         */
        public int col; // <- possible
        /**
         * new value for redo, may be empty
         */
        public Value newValue; // <- possible
    }

    /**
     * event log
     */
    public static class LogEvent {
        EventType type;
        int tid;
    }

    /**
     * add event log
     * @param eventType type of event
     * @param relatedId tid/checkpoint id
     * @return succ?
     */
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

    /**
     * pack detail flag and tid
     * @param type event type
     * @param tid tid
     * @return flaged tid
     */
    private long getDetailFlag(LogDetail.DetailType type, int tid){
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

    /**
     * add insert log
     * @param tid transaciton id
     * @param rowId row_id
     * @return succ?
     */
    public boolean addInsertLog(int tid, int rowId) {
        int xSize = 8;
        if (dCnt + xSize > 4096) {
            createPage(detailPage, 2);
            dCnt = 4;
        }
        long flag = getDetailFlag(LogDetail.DetailType.Insert, tid);
        writeInt(detailPage, dCnt, flag);
        writeInt(detailPage, dCnt + 4, rowId);
        dCnt = dCnt + 8;
        return false;
    }

    /**
     * add write log
     * @param tid transaciton id
     * @param rowId row_id
     * @param tbName table name
     * @param newValue new value
     * @return succ?
     */
    public boolean addWriteLog(int tid, int rowId, String tbName, Value newValue) {
        Type type = newValue.getType();
        byte[] strBytes = tbName.getBytes();
        int xSize = 4 + 4 + 2 + (strBytes.length + 2) + 4 + 1 + type.getSize();
        if (dCnt + xSize > 4096) {
            createPage(detailPage, 2);
            dCnt = 4;
        }
        int xLength = xSize - 4 - 4 - 2;

        dCnt = writeInt(detailPage, dCnt, getDetailFlag(LogDetail.DetailType.Write, tid));
        dCnt = writeInt(detailPage, dCnt, rowId);

        dCnt = writeWord(detailPage, dCnt, xLength);
        dCnt = writeBytes(detailPage, dCnt, strBytes);

        detailPage.buffer[dCnt] = (byte)type.getMetaType().ordinal();
        dCnt = dCnt + 1;
        dCnt = writeBytes(detailPage, dCnt, newValue.getSerial());
        detailPage.markDirty();
        return false;
    }

    /**
     * add delete log
     * @param tid transaction id
     * @param rowId row_id
     * @return
     */
    public boolean addDeleteLog(int tid, int rowId) {
        int xSize = 8;
        if (dCnt + xSize > 4096) {
            createPage(detailPage, 2);
            dCnt = 4;
        }
        long flag = getDetailFlag(LogDetail.DetailType.Delete, tid);
        writeInt(detailPage, dCnt, flag);
        writeInt(detailPage, dCnt + 4, rowId);
        dCnt = dCnt + 8;
        return false;
    }

    /**
     * remove transaction
     * @param tid transaction id
     * @return succ?
     */
    public boolean removeTransaction(int tid) {
        // just don't commit it
        return true;
    }

    /**
     * sync to disk
     * @return succ?
     */
    public boolean sync() {
        eventPage.sync();
        detailPage.sync();
        return true;
    }

    // transaction allocator

    private int tidNow = 1;

    /**
     * get next transaction id
     * @return tid
     */
    public int nextTid(){
        while (tidPool.contains(tidNow)) {
            tidNow = tidNow + 1;
        }
        return tidNow;
    }

    /**
     * allocate a tid
     * @return new tid
     */
    public int allocTransaction() {
        int tid = nextTid();
        tidPool.add(tid);
        return tid;
    }

    /**
     * free tid
     * @param tid tid
     * @return tid
     */
    public int freeTransaction(int tid) {
        // tidPool.remove(tid); <-- cuz we spitted two log, tid should never reuse
        return tid;
    }

    /**
     * get current used transactions count
     * @return transactions countin
     */
    public int transactionCount(){
        return tidPool.size();
    }

    private long readWord(Page page, int p) {
        return page.buffer[p] + page.buffer[p + 1] * 256;
    }

    private long readInt(Page page, int p){
        return readWord(page, p) + readWord(page, p + 2) * 256L*256L;
    }

    private String readString(Page page, int p){
        int len = (int)readWord(page, p);
        p = p + 2;
        byte buf[] = new byte[len];
        for (int i = 0; i < len; i++)
            buf[i] = page.buffer[p + i];
        return new String(buf);
    }

    private int writeWord(Page page, int p, long v){
        page.buffer[p] = (byte)(v & 0xff);
        page.buffer[p + 1] = (byte)((v >> 8)&0xff);
        page.markDirty();
        return p + 2;
    }

    private int writeInt(Page page, int p, long v){
        writeWord(page, p, v & 0xffff);
        writeWord(page, p + 2, (v>>16) & 0xffff);
        page.markDirty();
        return p + 4;
    }

    private int writeBytes(Page page, int p, byte[] bytes){
        p = writeWord(page, p, bytes.length);
        for (byte aByte : bytes) {
            page.buffer[p] = aByte;
            p = p + 1;
        }
        page.markDirty();
        return p;
    }

    /**
     * get writable offset of detail
     * @param page page object
     * @return offset
     */
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