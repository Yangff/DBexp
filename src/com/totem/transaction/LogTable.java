package com.totem.transaction;

import com.totem.storage.ITable;
import com.totem.table.Cell;

import java.util.Iterator;

public class LogTable implements ITable {
    @Override
    public Iterator<Integer> begin(int start, int col) {
        return null;
    }

    @Override
    public Iterator<Integer> rbegin(int start, int col) {
        return null;
    }

    @Override
    public Iterator<Cell[]> beginScan(int start, int[] interest) {
        return null;
    }

    @Override
    public Iterator<Cell[]> rbeginScan(int start, int[] interest) {
        return null;
    }

    @Override
    public Cell[] get(int row, int[] interest) {
        return new Cell[0];
    }

    @Override
    public int insert(Cell[] cells) {
        return 0;
    }

    @Override
    public boolean remove(int row_id) {
        return false;
    }
}
