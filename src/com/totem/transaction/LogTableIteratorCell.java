package com.totem.transaction;

import com.totem.storage.ITable;
import com.totem.table.Cell;
import javafx.util.Pair;

import java.util.*;

public class LogTableIteratorCell implements Iterator<Cell[]> {
    private Iterator<Cell[]> orgIterator;
    private Cell[] itCurrent;
    private boolean itCurrentAvailable;

    private DirtyMap dirtyMap;
    private int dirtyCurrent;
    private int dirtyEnd;
    private Cell[] dirtyCurrentValue;

    private boolean dirtyAvailable;

    private boolean reversed;

    private Cell[] emprySource;

    private int[] interested;

    public LogTableIteratorCell(ITable orgTable, Iterator<Cell[]> orgIterator, int[] interested, int start, boolean reversed, DirtyMap dirtyMap){
        this.orgIterator = orgIterator;
        this.dirtyMap = dirtyMap;
        this.interested = interested;
        this.emprySource = orgTable.getTableInfo().createEmptyRow();
        if (reversed) {
            dirtyCurrent = Math.min(start, dirtyMap.lastRow());
            dirtyCurrentValue = dirtyMap.get(dirtyCurrent, interested);
            dirtyEnd = dirtyMap.lastRow();
            dirtyAvailable = moveDirty();
        } else {
            dirtyCurrent = Math.max(start, dirtyMap.firstRow());
            dirtyCurrentValue = dirtyMap.get(dirtyCurrent, interested);
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
    public Cell[] next() {
        Integer result;
        ArrayList<Pair<Integer, String>> ary = new ArrayList<>();
        if (itCurrentAvailable)
            ary.add(new Pair<>(itCurrent[itCurrent.length - 1].getValue().getIntegerValue(), "it"));
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
        if (ary.size() == 2) {
            Pair<Integer, String> resultPair2 = ary.get(0);
            if (resultPair.getKey().equals(resultPair2.getKey())) {
                // need merge
                Cell []curModify = dirtyCurrentValue;
                Cell []curSource = itCurrent;
                nextIt();
                nextDirty();
                return finalStrip(merge(curSource, curModify));
            }
        }
        Cell[] cur = null;
        if (resultPair.getValue().equals("it")) {
            // it has, dirty not
            cur = itCurrent;
            nextIt();
            return finalStrip(cur);
        } else {
            cur = merge(emprySource, dirtyCurrentValue);
            nextDirty();
            return finalStrip(cur);
        }
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

    private Cell[] finalStrip(Cell[] org){
        Cell[] rst = new Cell[org.length - 2];
        System.arraycopy(org, 0, rst, 0, org.length - 1);
        return rst;
    }

    /**
     * move dirty to next available
     * @return succ?
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
     * @return succ?
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

    private int currentRowId(){
        return itCurrent[itCurrent.length - 1].getValue().getIntegerValue();
    }

    /**
     * move iterator if it has removed according to logs
     * @return succ?
     */
    private boolean moveIt(){
        if (!itCurrentAvailable)
            return false;
        while (dirtyMap.hasRemoved(currentRowId()) && orgIterator.hasNext()) {
            itCurrent = orgIterator.next();
        }
        return !dirtyMap.hasRemoved(currentRowId());
    }

    /**
     * move iterator to next
     * @return succ?
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
