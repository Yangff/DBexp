package com.totem.transaction;

import com.totem.table.Cell;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.SortedMap;
import java.util.TreeMap;

public class DirtyMap {
    // rowid => row
    public SortedMap<Integer, SortedMap<Integer, Pair<Cell, Integer>>> rows;
    // tid
    public SortedMap<Integer, Integer> removedRow;
    // tid => effected row
    public SortedMap<Integer, ArrayList<Integer>> effectedRow;

    public DirtyMap(){
        rows = new TreeMap<>();
        removedRow = new TreeMap<>();
        effectedRow = new TreeMap<>();
    }

    /**
     * Get last available row_id in this table
     * @return last available row
     */
    public int lastRow(){
        return Math.min(rows.lastKey(), removedRow.lastKey());
    }

    /**
     * Get first available row_id in this table
     * @return first available row
     */
    public int firstRow(){
        return Math.max(rows.firstKey(), removedRow.firstKey());
    }

    /**
     * put a modify into dirtymap
     * @param tid transaction id
     * @param row effected row
     * @param cid effected column
     * @param val new value
     * @return succ?
     */
    public boolean put(int tid, int row, int cid, Cell val) {
        try {
            if (removedRow.containsKey(row)) {
                removedRow.remove(row);
            }
            if (!effectedRow.containsKey(tid)) {
                effectedRow.put(tid, new ArrayList<>());
            }
            effectedRow.get(tid).add(row);
            if (!rows.containsKey(row)) {
                rows.put(row, new TreeMap<>());
            }
            SortedMap<Integer, Pair<Cell, Integer>> _row = rows.get(row);
            Pair<Cell, Integer> newval = new Pair<>(val, tid);
            if (_row.containsKey(cid)) {
                _row.remove(cid);
            }
            _row.put(cid, newval);
        } catch (Exception e){
            return false;
        }
        return true;
    }

    /**
     * add remove one row into dirty map
     * @param tid transaction id
     * @param row removed row id
     * @return succ?
     */
    public boolean remove(int tid, int row){
        try {
            if (!effectedRow.containsKey(tid)) {
                effectedRow.put(tid, new ArrayList<>());
            }
            effectedRow.get(tid).add(row);
            if (removedRow.containsKey(row)) {
                removedRow.remove(row);
            }
            removedRow.put(row, tid);
        } catch (Exception e){
            return false;
        }
        return true;
    }

    /**
     * commit a transaction and remove all related info in dirty map
     * @param tid transaction id
     * @return succ?
     */
    public boolean commit(int tid) {
        try {
            if (!effectedRow.containsKey(tid))
                return false;
            ArrayList<Integer> ary = effectedRow.get(tid);
            effectedRow.remove(tid);
            for (Integer i : ary) {
                if (removedRow.containsKey(i)){
                    if (removedRow.get(i) == tid) {
                        removedRow.remove(i);
                    }
                }
                if (rows.containsKey(i)) {
                    SortedMap<Integer, Pair<Cell, Integer>> row;
                    row = rows.get(i);
                    ArrayList<Integer> removeList = new ArrayList<>();
                    for (Integer j : row.keySet()) {
                        if (row.get(j).getValue() == tid) {
                            removeList.add(j);
                        }
                    }
                    if (removeList.size() == row.size()) {
                        rows.remove(i);
                    } else {
                        for (Integer j : removeList){
                            row.remove(j);
                        }
                    }
                }

            }
        } catch (Exception e){
            return false;
        }
        return true;
    }

    /**
     * get currently opening transactions' id
     * @return count of transactions
     */
    public Integer getOpenTransactions(){
        return effectedRow.size();
    }
}
