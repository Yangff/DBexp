package com.totem.storage;

import com.totem.database;
import com.totem.table.TableScheme;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class storage {
    HashMap<String, PhyTable> tableMap;
    private FileSystem db_root;
    private database db;

    private ITable sys_class;
    private ITable sys_deputy_relation;
    private ITable sys_attribute;

    public storage(database db, String db_root) {
        try {
            this.db_root = FileSystems.getFileSystem(new URI(db_root));
        } catch (URISyntaxException urie){
            this.db_root = null;
        }
        this.db = db;
    }

    /**
     * init system table when db start
     * @return succ?
     */
    public boolean initSysTables(){
        return false;
    }

    /**
     * load exists system tables
     * @return succ?
     */
    public boolean loadSysTables(){
        return false;
    }

    /**
     * Open a table
     * @param tbName table name
     * @return table
     */
    public ITable openTable(String tbName) {
        return null;
    }

    /**
     * Create a table
     * @param tbName table name
     * @param scheme table scheme
     * @return succ?
     */
    public boolean createTable(String tbName, TableScheme scheme) {
        return false;
    }

    /**
     * Drop a table
     * @param tbName table name
     * @return succ?
     */
    public boolean dropTable(String tbName) {
        return false;
    }

    /**
     * Get scheme of table
     * @param tbName table name
     * @return scheme
     */
    public TableScheme getTableScheme(String tbName) {
        return null;
    }

    /**
     * Get File object for table
     * @param tbName table name
     * @return file object
     * @throws FileNotFoundException when file is not exists and failed to create one
     */
    public RandomAccessFile getTableFile(String tbName) throws FileNotFoundException {
        String path = db_root.getPath(tbName + ".db").toString();
        return new RandomAccessFile(path, "rws");
    }

    /**
     * Get File object for new journal
     * @return file object
     * @throws FileNotFoundException when file is not exists and failed to create one
     */
    public RandomAccessFile getNewJournalFile() throws FileNotFoundException {
        Path path = db_root.getPath("journal-" + "new" + ".dat");
        try {
            Files.deleteIfExists(path);
        } catch (IOException ioe) {
            return null;
        }
        return new RandomAccessFile(path.toString(), "rws");
    }

    /**
     * Get File object for old journal
     * @return file object
     * @throws FileNotFoundException when file is not exists and failed to create one
     */
    public RandomAccessFile getOldJournalFile() throws FileNotFoundException {
        Path path = db_root.getPath("journal-" + "old" + ".dat");
        if (!Files.exists(path)) {
            // if old log not exists, maybe its crashed after the old one deleted and new one not
            // if so, we first try move the new log to old log and give it a shoot..
            if (!migrateLog()){
                return null;
            }
        }
        return new RandomAccessFile(path.toString(), "rws");
    }

    public boolean deleteOldJournalFile() {
        Path path = db_root.getPath("journal-" + "old" + ".dat");
        try {
            Files.deleteIfExists(path);
        } catch (IOException ioe){
            return false;
        }
        return true;
    }

    /***
     * When we successfully created new logFile, we can kill the old one and use it.
     * @return succ?
     */
    public boolean migrateLog(){
        Path new_path = db_root.getPath("journal-" + "new" + ".dat");
        Path old_path = db_root.getPath("journal-" + "old" + ".dat");
        try {
            Files.move(new_path, old_path, REPLACE_EXISTING);
        } catch (Exception e){
            return false;
        }
        return true;
    }

    /**
     * Get File object for table
     * @param tableName table name
     * @param columnName name of attribute for index
     * @return file object
     * @throws FileNotFoundException when file is not exists and failed to create one
     */
    public RandomAccessFile getIndexFile(String tableName, String columnName) throws FileNotFoundException {
        Path path = db_root.getPath(tableName + "." + columnName + ".idx");
        return new RandomAccessFile(path.toString(), "rws");
    }

    /**
     * sync all table file
     * @return succ?
     */
    public boolean sync(){
        return false;
    }
}
