package com.totem.storage;

import com.totem.table.TableScheme;

public class PhyScheme {
    private TableScheme scheme;
    private int[] offsets;
    private static final int constant_offset = 1; // 1 byte flags + 4bytes row_id
    public PhyScheme(TableScheme scheme){
        this.scheme = scheme;
        this.offsets = new int[scheme.attrList.length + 1];
        offsets[0] = constant_offset;
        for (int i = 0; i < scheme.attrList.length; i++){
            offsets[i + 1] = offsets[i] + scheme.attrList[i].getSize();
        }
    }
}
