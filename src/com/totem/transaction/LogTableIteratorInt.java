package com.totem.transaction;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class LogTableIteratorInt implements Iterator<Integer> {
    private Iterator<Integer> orgIterator;
    private Integer itCurrent;
    private boolean itCurrentAvailable;

    private DirtyMap dirtyMap;
    private int dirtyCurrent;
    private int dirtyEnd;
    private boolean dirtyAvailable;

    private boolean reversed;

    public LogTableIteratorInt(Iterator<Integer> orgIterator, int start, boolean reversed, DirtyMap dirtyMap){
        this.orgIterator = orgIterator;
        this.dirtyMap = dirtyMap;
        if (reversed) {
            dirtyCurrent = Math.min(start, dirtyMap.lastRow());
            dirtyEnd = dirtyMap.lastRow();
            dirtyAvailable = moveDirty();
        } else {
            dirtyCurrent = Math.max(start, dirtyMap.firstRow());
            dirtyEnd = dirtyMap.firstRow();
            dirtyAvailable = moveDirty();
        }
        if (orgIterator.hasNext()) {
            itCurrent = orgIterator.next();
            itCurrentAvailable = true;
            moveIt();
        } else {
            itCurrentAvailable = false;
        }

    }

    @Override
    public boolean hasNext() {
        return itCurrentAvailable || dirtyAvailable;
    }

    @Override
    public Integer next() {
        Integer result;
        if ((int)itCurrent == dirtyCurrent) {
            if (!(dirtyAvailable || itCurrentAvailable)) {
                throw new NoSuchElementException();
            }
            result = itCurrent;
            nextIt();
            nextDirty();
            return result;
        }
        ArrayList<Pair<Integer, String>> ary = new ArrayList<Pair<Integer, String>>();
        if (itCurrentAvailable)
            ary.add(new Pair<>(itCurrent, "it"));
        else
            ary.add(new Pair<>(dirtyCurrent, "di"));
        if (ary.isEmpty())
            throw new NoSuchElementException();
        if (reversed) {
            ary.sort((Pair<Integer,String> a, Pair<Integer,String> b) -> b.getKey() - a.getKey());
        } else {
            ary.sort(Comparator.comparingInt(Pair::getKey));
        }
        Pair<Integer,String> resultPair = ary.get(0);

        if (resultPair.getValue() == "it")
            nextIt();
        else
            nextDirty();

        return resultPair.getKey();
    }

    /**
     * move dirty to next available
     * @return
     */
    private boolean moveDirty() {
        while (!dirtyMap.hasRow(dirtyCurrent)) {
            if (dirtyCurrent == dirtyEnd)
                return false;
            if (reversed){
                if (dirtyCurrent > dirtyEnd) {
                    dirtyCurrent = dirtyCurrent - 1;
                }
            } else {
                if (dirtyCurrent < dirtyEnd) {
                    dirtyCurrent = dirtyCurrent + 1;
                }
            }
        }
        return dirtyMap.hasRow(dirtyCurrent);
    }

    /**
     * move dirty to next
     * @return
     */
    private boolean nextDirty(){
        if (!dirtyAvailable)
            return false;
        if (reversed){
            if (dirtyCurrent > dirtyEnd) {
                dirtyCurrent = dirtyCurrent - 1;
                return dirtyAvailable = moveDirty();
            } else
                return dirtyAvailable = false;
        } else {
            if (dirtyCurrent < dirtyEnd) {
                dirtyCurrent = dirtyCurrent + 1;
                return dirtyAvailable = moveDirty();
            } else
                return dirtyAvailable = false;
        }
    }

    /**
     * move iterator if it has removed according to logs
     * @return
     */
    private boolean moveIt(){
        while (dirtyMap.hasRemoved(itCurrent) && orgIterator.hasNext()) {
            itCurrent = orgIterator.next();
        }
        return !dirtyMap.hasRemoved(itCurrent);
    }

    /**
     * move iterator to next
     * @return
     */
    private boolean nextIt(){
        if (!itCurrentAvailable)
            return false;
        try {
            if (orgIterator.hasNext()) {
                itCurrent = orgIterator.next();
                itCurrentAvailable = moveIt();
            } else {
                itCurrentAvailable = false;
            }
        } catch (Exception e){
            return itCurrentAvailable = false;
        }
        return itCurrentAvailable;
    }
}
