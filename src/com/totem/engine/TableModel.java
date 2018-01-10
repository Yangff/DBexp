package com.totem.engine;

import com.totem.index.OrderIndex;
import com.totem.storage.ITable;
import com.totem.table.Attribute;
import com.totem.table.Cell;
import com.totem.table.TableScheme;
import com.totem.table.Value;

import java.util.HashMap;

/**
 * Accessing table as a 2d array
 * normally used for system table
 *
 * Can be eliminated if you don't need such op in engine
 *
 * THIS CLASS IS NOT USED FOR EXECUTE QUERY
 */
public class TableModel {
    private TableScheme scheme;
    private ITable table;
    private HashMap<String, OrderIndex> indexes;

    public TableModel(TableScheme scheme, ITable table) {
        this.scheme = scheme;
        this.table = table;
    }

    public HashMap<String, Value> get(int row_id) {
        return null;
    }

    public Value get(int row_id, int col_id){
        return null;
    }

    public Value get(int row_id, String col_name){
        return null;
    }

    public HashMap<String, Value> get(Attribute key, Value val){
        return null;
    }

    public Value get(Attribute key, Value val, int col_id){
        return null;
    }

    public Value get(Attribute key, Value val, String col_name){
        return null;
    }

    public Iterable<Cell[]> begin() {
        return null;
    }

    public Iterable<Cell[]> find(int rowId){
        return null;
    }

    public Iterable<Cell[]> find(Attribute key, Value val){
        return null;
    }

}
