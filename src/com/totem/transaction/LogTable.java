package com.totem.transaction;

import com.totem.storage.ITable;
import com.totem.storage.PhyTable;
import com.totem.table.Cell;
import com.totem.table.TableScheme;

import java.util.Iterator;

public class LogTable implements ITable {
    private PhyTable orgTable;
    private DirtyMap dirtyMap;
    private Log logs;

    private int tid;

    public LogTable(Log logs, DirtyMap dirtyMap, int tid, PhyTable table){
        orgTable = table;
        this.dirtyMap = dirtyMap;
        this.tid = tid;
        this.logs = logs;
    }

    @Override
    public Iterator<Integer> begin(int start) {
        Iterator<Integer> oIt = orgTable.begin(start);
        LogTableIteratorInt logIt = new LogTableIteratorInt(oIt, start, false, dirtyMap);
        return logIt;
    }

    @Override
    public Iterator<Integer> rbegin(int start) {
        Iterator<Integer> oIt = orgTable.begin(start);
        LogTableIteratorInt logIt = new LogTableIteratorInt(oIt, start, true, dirtyMap);
        return logIt;
    }

    @Override
    public Iterator<Cell[]> beginScan(int start, int[] interest) {
        int[] newInst = new int[interest.length + 1];
        System.arraycopy(interest, 0, newInst, 0, interest.length);
        newInst[interest.length] = 0; // for row_id index
        Iterator<Cell[]> oIt = orgTable.beginScan(start, interest);
        //ITable orgTable, Iterator<Cell[]> orgIterator, int[] interested, int start, boolean reversed, DirtyMap dirtyMap
        LogTableIteratorCell logIt = new LogTableIteratorCell(orgTable, oIt, interest, start, false, dirtyMap);
        return logIt;
    }

    @Override
    public Iterator<Cell[]> rbeginScan(int start, int[] interest) {
        int[] newInst = new int[interest.length + 1];
        System.arraycopy(interest, 0, newInst, 0, interest.length);
        newInst[interest.length] = 0; // for row_id index
        Iterator<Cell[]> oIt = orgTable.beginScan(start, interest);
        //ITable orgTable, Iterator<Cell[]> orgIterator, int[] interested, int start, boolean reversed, DirtyMap dirtyMap
        LogTableIteratorCell logIt = new LogTableIteratorCell(orgTable, oIt, interest, start, true, dirtyMap);
        return logIt;
    }

    @Override
    public Cell[] get(int row, int[] interest) {
        if (dirtyMap.hasRemoved(row)){
            return null;
        }
        Cell[] org = orgTable.get(row, interest);
        Cell[] dirty = null;
        if (dirtyMap.hasRow(row)) {
            dirty = dirtyMap.get(row,interest);
            if (org == null) {
                Cell[] empt = getTableInfo().createEmptyRow();
                dirty = merge(empt, dirty);
                return dirty;
            } else {
                return merge(org, dirty);
            }
        }
        return org;
    }

    @Override
    public int insert(Cell[] cells) {
        int rowId = orgTable.getInsertRowId();
        logs.addInsertLog(tid, rowId);
        dirtyMap.insertRow(rowId);
        String tbName = getTableInfo().tableName;
        for (int i = 0; i < cells.length; i++) {
            logs.addWriteLog(tid, rowId, tbName, cells[i].getValue());
        }
        return 0;
    }

    @Override
    public boolean remove(int row_id) {
        logs.addDeleteLog(tid, row_id);
        dirtyMap.remove(row_id);
        return false;
    }

    @Override
    public TableScheme getTableInfo() {
        return orgTable.getTableInfo();
    }

    private Cell[] merge(Cell[] source, Cell[] modify) {
        Cell[] newCell = source.clone();
        for (int i = 0; i < source.length; i++){
            String sName = source[i].getAttribute().getFullName();
            if (modify[i] != null) {
                if (modify[i].getAttribute().getFullName().equals(sName)){
                    newCell[i] = modify[i];
                }
            }
        }
        return newCell;
    }
}
