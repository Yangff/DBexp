package com.totem.storage;

import com.totem.database;
import com.totem.table.TableScheme;

import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.util.HashMap;

public class storage {
    HashMap<String, PhyTable> tableMap;
    private String db_root;
    private database db;
    public storage(database db, String db_root) {
        this.db_root = db_root;
        this.db = db;
    }

    /*
        FIXME: Decide to maintain system table by storage or engine [storage parts]

        These are tables if you want to maintain sys tables by storage.

        If use these, open system table only need table name as it arguments
        Otherwise, add init system table in engine and REMOVE init sys table here.
        Also, remove openSysTable and let open table accept TableScheme

     */
    private ITable sys_class;
    private ITable sys_deputy_relation;
    private ITable sys_attribute;

    public boolean initSysTables(){
        return false;
    }

    public boolean loadSysTables(){
        return false;
    }

    /**
     * If you want to maintain sys tables by storage
     * @param tbName table name
     * @return table
     */
    public ITable openTable(String tbName) {
        return null;
    }

    /**
     * otherwise, all table should be opened by a function like this
     * @param scheme table scheme
     * @return table
     */
    public ITable openSysTable(TableScheme scheme){
        return null;
    }
    public boolean createTable(String tbName, TableScheme scheme) {
        return false;
    }
    public boolean dropTable(String tbName) {
        return false;
    }

    /**
     * Get File object for table
     * @param tbName table name
     * @return file object
     * @throws FileNotFoundException when file is not exists and failed to create one
     */
    public RandomAccessFile getTableFile(String tbName) throws FileNotFoundException {
        return new RandomAccessFile(db_root + "/" + tbName + ".db", "rws");
    }

    /**
     * Get File object for journal
     * @return file object
     * @throws FileNotFoundException when file is not exists and failed to create one
     */
    public RandomAccessFile getJournalFile() throws FileNotFoundException {
        return new RandomAccessFile(db_root + "/journal.dat", "rws");
    }

    /**
     * Get File object for table
     * @param tableName table name
     * @param colunmName name of attribute for index
     * @return file object
     * @throws FileNotFoundException when file is not exists and failed to create one
     */
    public RandomAccessFile getIndexFile(String tableName, String colunmName) throws FileNotFoundException {
        return new RandomAccessFile(db_root + "/" + tableName + "." + colunmName + ".idx", "rws");
    }
}
