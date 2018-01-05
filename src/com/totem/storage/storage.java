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
        Tables if you want to maintain sys tables by storage
        ! Move it to engine if you want to storage store data only.
     */
    private ITable sys_class;
    private ITable sys_deputy_relation;
    private ITable sys_attribute;

    public boolean InitSysTables(){
        return false;
    }

    public boolean LoadSysTables(){
        return false;
    }

    /**
     * If you want to maintain sys tables by storage
     * @param tbName table name
     * @return table
     */
    public ITable OpenTable(String tbName) {
        return null;
    }

    /**
     * otherwise, all table should be opened by a function like this
     * @param tbName table name
     * @param scheme table scheme
     * @return table
     */
    public ITable OpenSysTable(String tbName, TableScheme scheme){
        return null;
    }
    public boolean CreateTable(String tbName, TableScheme scheme) {
        return false;
    }
    public boolean DropTable(String tbName) {
        return false;
    }
    public RandomAccessFile getTableFile(String tbName) throws FileNotFoundException {
        return new RandomAccessFile(db_root + "/" + tbName + ".db", "rws");
    }
    public RandomAccessFile getJournalFile() throws FileNotFoundException {
        return new RandomAccessFile(db_root + "/journal.dat", "rws");
    }
    public RandomAccessFile getIndexFile(String tableName, String colunmName) throws FileNotFoundException {
        return new RandomAccessFile(db_root + "/" + tableName + "." + colunmName + ".idx", "rws");
    }
}
