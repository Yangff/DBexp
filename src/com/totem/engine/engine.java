package com.totem.engine;

import com.totem.database;
import com.totem.storage.ITable;

import java.util.Iterator;

public class engine {
    /*
    Step 1. SQL Parser
         2. Execute Phy Plan
         3. Calc required attributes for where cond. and collect them
         4. Calc required attributes for deputy migration. and collect them (notices that you can
            sort items by row_id of original class to accelerate this collection one scan for all
            O(NlogN) )
         5. Filter by where cond.
         6. Filter by Deputy migration cond.
         7. Calc required attributes for final result and collect them
         8. Do final result, eliminate useless attributes

      Without a iterator combinator, we scan all data we need to memory and do calc.
      Or, every step is a iterator and combinator and we can get result one by one.
     */
    private database db;
    public engine(database db) {
        this.db = db;
    }

    public boolean Execute(String sql){
        return false;
    }

    public Iterator GetResult(){
        return null;
    }

    public boolean Clean(){
        return true;
    }
}
