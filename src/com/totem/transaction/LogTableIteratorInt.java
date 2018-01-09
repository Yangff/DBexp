package com.totem.transaction;

import java.util.Iterator;

public class LogTableIteratorInt<Integer> implements Iterator<Integer> {
    private Iterator<Integer> orgIterator;

    @Override
    public boolean hasNext() {
        return orgIterator.hasNext();
    }

    @Override
    public Integer next() {
        return orgIterator.next();
    }
}
