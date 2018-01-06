package com.totem.index;

import com.totem.table.Attribute;
import com.totem.table.Value;
import javafx.util.Pair;

import java.io.RandomAccessFile;

public class OrderIndex {
    private BTreeNode btree_root;
    private RandomAccessFile raf;
    private Attribute attr;

    public OrderIndex(Attribute attr, RandomAccessFile raf){
        this.raf = raf;
        this.attr = attr;
        this.btree_root = new BTreeNode(this);
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
    public boolean sync(){
        return true;
    }

}
