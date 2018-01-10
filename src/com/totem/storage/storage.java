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
    public storage(database db, String db_root) {
        try {
            this.db_root = FileSystems.getFileSystem(new URI(db_root));
        } catch (URISyntaxException urie){
            this.db_root = null;
        }
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
     * @param colunmName name of attribute for index
     * @return file object
     * @throws FileNotFoundException when file is not exists and failed to create one
     */
    public RandomAccessFile getIndexFile(String tableName, String colunmName) throws FileNotFoundException {
        Path path = db_root.getPath(tableName + "." + colunmName + ".idx");
        return new RandomAccessFile(path.toString(), "rws");
    }
}
