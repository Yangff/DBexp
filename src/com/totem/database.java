package com.totem;

import com.totem.engine.engine;
import com.totem.index.index;
import com.totem.storage.storage;
import com.totem.transaction.transaction;

public class database {
    public engine Engine;
    public storage Storage;
    public transaction Transaction;
    public index Index;

    public database(){
        Engine = new engine(this);
        Storage = new storage(this, "./");
        Transaction = new transaction(this);
        Index = new index(this);
    }

    void InitDB(){
        Storage.initSysTables();
        Transaction.createJournal();
    }

    void LoadDB(){
        Storage.loadSysTables();
        Transaction.restoreJournal();
    }
}
