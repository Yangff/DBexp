package com.totem.transaction;

import com.totem.table.Cell;

import java.util.Iterator;

public class LogTableIteratorCell implements Iterator<Cell[]> {
    private Iterator<Cell[]> orgIterator;
    private DirtyMap dirtyMap;
    private boolean reversed;

    public LogTableIteratorCell(Iterator<Cell[]> orgIterator, int start, boolean reversed, DirtyMap dirtyMap) {

    }

    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public Cell[] next() {
        return new Cell[0];
    }
}
