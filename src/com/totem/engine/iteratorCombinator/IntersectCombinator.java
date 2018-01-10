package com.totem.engine.iteratorCombinator;

import com.totem.table.Cell;

import java.util.Iterator;

public class IntersectCombinator implements Iterator<Cell[]> {
    public IntersectCombinator(Iterator<Cell[]> a, Iterator<Cell[]> b) { }

    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public Cell[] next() {
        return new Cell[0];
    }
}
