package com.totem.index;

import com.totem.table.Type;
import com.totem.table.Value;
import javafx.util.Pair;

public class OrderIndex {
    private BTreeNode btree;
    public OrderIndex(String tableName, String colunmName, Type type){

    }
    public BTIterator<Pair<Value, Integer>> begin(){
        return null;
    }
    public BTIterator<Pair<Value, Integer>> rbegin() {
        return null;
    }
    public BTIterator<Pair<Value, Integer>> upper_bound(Value key) {
        return null;
    }
    public BTIterator<Pair<Value, Integer>> lower_bound(Value key) {
        return null;
    }
    public BTIterator<Pair<Value, Integer>> find(Value key){
        return null;
    }
    public boolean insert(Value key, int value) {
        return false;
    }
    public boolean remove(Value key) {
        return false;
    }
}
