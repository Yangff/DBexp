package com.totem.storage;

import com.totem.table.Cell;
import com.totem.table.TableScheme;

import java.util.Iterator;

public interface ITable {
    /**
     * scan INT value from table
     * it scan from row_id = {@code start} and can stop anytime you
     * It's mostly used for scanning by row_id.
     * {@code col} is used for fast scanning Deputy tables whose actually
     * column for `row_id` of original (also known as object id) is {@code col}=1
     * instead of {@code col}=0
     * @param start start row_id
     * @return
     */
    Iterator<Integer> begin(int start);

    /**
     * scan INT value reversed from table
     * it scan from row_id = {@code start} and can stop anytime you
     * It's mostly used for scanning by row_id.
     * {@code col} is used for fast scanning Deputy tables whose actually
     * column for `row_id` of original (also known as object id) is {@code col}=1
     * instead of {@code col}=0
     * @param start start row_id
     * @return
     */
    Iterator<Integer> rbegin(int start) ;
    /**
     * Scan Row from Table
     * it scan from row_id = {@code start} and can stop anytime you
     * it's used for reading data from table.
     * @param start start row_id
     * @param interest interested columns
     *                 (For table scheme, columns counting from 1 for
     *                  first column because 0 is used fro row_id.)
     *                 use null for getting all data.
     * @return
     */
    Iterator<Cell[]> beginScan(int start, int[] interest);

    /**
     * Scan Row reversed from Table
     * it scan from row_id = {@code start} and can stop anytime you
     * it's used for reading data from table.
     * @param start start row_id
     * @param interest interested columns
     *                 (For table scheme, columns counting from 1 for
     *                  first column because 0 is used fro row_id.)
     *                 use null for getting all data.
     * @return
     */
    Iterator<Cell[]> rbeginScan(int start, int[] interest);

    /**
     * Get given row from table
     * @param row row_id
     * @param interest interested columns
     *                 (For table scheme, columns counting from 1 for
     *                  first column because 0 is used fro row_id.)
     *                 use null for getting all data.
     * @return row
     */
    Cell[] get(int row, int[] interest);

    /**
     * Insert given row and return inserted row_id
     * @param cells row
     * @return inserted row_id
     */
    int insert(Cell[] cells);

    /**
     * Remove given row
     * @param row_id row to remove
     * @return successful?
     */
    boolean remove(int row_id);

    /**
     * Get table info from storage level
     * @return table scheme
     */
    public TableScheme getTableInfo();
}
