package com.totem.transaction;

import com.totem.table.Cell;

import java.util.Iterator;

public class LogTableIteratorCell implements Iterator<Cell[]> {
    public LogTableIteratorCell() {

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
