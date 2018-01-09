package com.totem;

import com.totem.engine.engine;
import com.totem.index.index;
import com.totem.sql.parser;
import com.totem.storage.storage;
import com.totem.transaction.transaction;

public class database {
    public engine Engine;
    public storage Storage;
    public transaction Transaction;
    public index Index;
    public parser Parser;

    public database(String db_root){
        Engine = new engine(this);
        Storage = new storage(this, db_root);
        Transaction = new transaction(this);
        Index = new index(this);
        Parser = new parser(this);
    }

    public void InitDB(){
        Storage.initSysTables();
        Transaction.createJournal();
    }

    public void LoadDB(){
        Storage.loadSysTables();
        Transaction.restoreJournal();
    }

    public boolean Execute(String sql) {
        return Engine.execute(sql);
    }
}
