package com.totem.transaction;

import com.totem.table.Cell;
import javafx.util.Pair;

import java.util.*;

/**
 * Dirty map is used for store currently modify to database on memory.
 * In one transaction, the modify should take place instantly
 * however, others should not see (also cannot see these modifies)
 * because, these modifies have not written to storage level,
 * we must handle them here.
 */
public class DirtyMap {
    // row_id => column_id => cell
    public SortedMap<Integer, SortedMap<Integer, Cell>> rows;

    // row_id
    public SortedSet<Integer> removedRow;

    // effected row_id
    public SortedSet<Integer> effectedRow;

    public DirtyMap(){
        rows = new TreeMap<>();
        removedRow = new TreeSet<>();
        effectedRow = new TreeSet<>();
    }

    /**
     * Get last available row_id in this table
     * @return last available row
     */
    public int lastRow(){
        return Math.min(rows.lastKey(), removedRow.last());
    }

    /**
     * Get first available row_id in this table
     * @return first available row
     */
    public int firstRow(){
        return Math.max(rows.firstKey(), removedRow.first());
    }

    /**
     * put a modify into dirtymap
     * @param row effected row
     * @param cid effected column
     * @param val new value
     * @return succ?
     */
    public boolean put(int row, int cid, Cell val) {
        try {
            if (removedRow.contains(row)) {
                removedRow.remove(row);
            }
            if (!effectedRow.contains(row)) {
                effectedRow.add(row);
            }
            if (!rows.containsKey(row)) {
                rows.put(row, new TreeMap<>());
            }
            SortedMap<Integer, Cell> _row = rows.get(row);
            if (_row.containsKey(cid)) {
                _row.remove(cid);
            }
            _row.put(cid, val);
        } catch (Exception e){
            return false;
        }
        return true;
    }

    /**
     * get interested column, no need for row_id (so don't strip it)
     * @param row row id
     * @param interested interested column
     * @return values
     */
    public Cell[] get(int row, int[] interested) {
        if (rows.containsKey(row)) {
            SortedMap hadValues = rows.get(row);
            Cell[] results = new Cell[interested.length]; // <-- warn: may contains null
            for (int i = 0; i < interested.length; i++) {
                if (hadValues.containsKey(interested[i])) {
                    results[i] = (Cell)hadValues.get(interested[i]);
                } else {
                    results[i] = null;
                }
            }
            return results;
        }
        return null;
    }

    /**
     * add remove one row into dirty map
     * @param row removed row id
     * @return succ?
     */
    public boolean remove(int row){
        try {
            if (!effectedRow.contains(row)) {
                effectedRow.add(row);
            }
            if (removedRow.contains(row)) {
                removedRow.remove(row);
            }
            removedRow.add(row);
            if (rows.containsKey(row)) {
                rows.remove(row);
            }
        } catch (Exception e){
            return false;
        }
        return true;
    }

    public boolean hasRow(int id) {
        return rows.containsKey(id) && !removedRow.contains(id);
    }

    public boolean hasRemoved(int id){
        return removedRow.contains(id);
    }
}
