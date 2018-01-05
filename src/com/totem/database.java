package com.totem;

import com.totem.engine.engine;
import com.totem.storage.storage;
import com.totem.transaction.transaction;

public class database {
    public engine Engine;
    public storage Storage;
    public transaction Transaction;

    public database(){
        Engine = new engine(this);
        Storage = new storage(this, "./");
        Transaction = new transaction(this);
    }

    void InitDB(){
        Storage.InitSysTables();
        Transaction.createJournal();
    }

    void LoadDB(){
        Storage.LoadSysTables();
        Transaction.restoreJournal();
    }
}
