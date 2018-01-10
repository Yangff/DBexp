package com.totem.transaction;

import com.totem.table.Value;

import java.io.RandomAccessFile;
import java.util.Iterator;

/**
 * Read and Write logs to file
 */
public class Log {
    private final RandomAccessFile logFile;

    public Log(RandomAccessFile logFile){
        this.logFile = logFile;
    }

    public enum LogType{
        Event, Details
    }

    public enum EventType {
        StartTransaction, EndTransaction, StartCheckpoint, EndCheckpoint
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

    // transaction allocator

    public int allocTransaction() {
        return -1;
    }

    public int destroyTransaction() {
        return -1;
    }

}

/*
Binary layout of log files
Each block is a 4k-page
[Head Section, Size = 4bytes]
0x00 [Byte] Block Type, 0 for event and 1 for details
0x01 [Word] Block length, numbers of journals contains in this block
0x03 [Byte] How far is the next block of this type, 0x00 for larger than 0xFF
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