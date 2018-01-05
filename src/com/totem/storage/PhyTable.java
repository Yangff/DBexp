package com.totem.storage;
import com.totem.table.Cell;
import com.totem.table.TableScheme;

import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.util.Iterator;

public class PhyTable implements ITable {
    public static final int page_count = 8;
    private PhyScheme phyScheme;
    private String tableName;
    private RandomAccessFile file;
    private storage _storage;
    private boolean completed;

    private Page[] pages;
    private Page metaPage;

    public PhyTable(storage _storage, String tbName) {
        tableName = tbName;
        this._storage = _storage;
        completed = false;
        this.pages = new Page[page_count];
    }

    public boolean open(TableScheme scheme) {
        phyScheme = new PhyScheme(scheme);
        try {
            // rws guarantee that every write op on file would make it to the device
            file = _storage.getTableFile(tableName);
        } catch (FileNotFoundException f) {
            return false;
        }
        return completed = true;
    }

    /**
     * scan INT value from table
     * it scan from row_id = {@code start} and can stop anytime you
     * It's mostly used for scanning by row_id.
     * {@code col} is used for fast scanning Deputy tables whose actually
     * column for `row_id` of original (also known as object id) is {@code col}=1
     * instead of {@code col}=0
     * @param start start row_id
     * @param col scanning col, 0 for normal table and 1 for deputy table.
     *            (For table scheme, {@code col} counting from 1 for
     *            first column because 0 are used fro row_id.)
     * @return
     */
    public Iterator<Integer> begin(int start, int col) {
        return null;
    }

    /**
     * scan INT value reversed from table
     * it scan from row_id = {@code start} and can stop anytime you
     * It's mostly used for scanning by row_id.
     * {@code col} is used for fast scanning Deputy tables whose actually
     * column for `row_id` of original (also known as object id) is {@code col}=1
     * instead of {@code col}=0
     * @param start start row_id
     * @param col scanning col, 0 for normal table and 1 for deputy table.
     *            (For table scheme, {@code col} counting from 1 for
     *            first column because 0 are is fro row_id.)
     * @return
     */
    public Iterator<Integer> rbegin(int start, int col) {
        return null;
    }

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
    public Iterator<Cell[]> beginScan(int start, int[] interest) {
        return null;
    }

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
    public Iterator<Cell[]> rbeginScan(int start, int[] interest) {
        return null;
    }

    /**
     * Get given row from table
     * @param row row_id
     * @param interest interested columns
     *                 (For table scheme, columns counting from 1 for
     *                  first column because 0 is used fro row_id.)
     *                 use null for getting all data.
          * @return row
     */
    public Cell[] get(int row, int[] interest) {
        return null;
    }

    /**
     * Insert given row and return inserted row_id
     * @param cells row
     * @return inserted row_id
     */
    public int insert(Cell[] cells) {
        return insertById(getInsertRowId(), cells);
    }

    /**
     * Remove given row
     * @param row_id row to remove
     * @return successful?
     */
    public boolean remove(int row_id) {
        return false;
    }


    // functions for transaction
    public int insertById(int row_id, Cell[] cells){
        return 0;
    }

    public int getInsertRowId(){
        return 0;
    }

    public String getTableName() {
        return tableName;
    }
}
