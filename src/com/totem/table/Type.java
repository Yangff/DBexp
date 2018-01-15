package com.totem.table;

import java.util.Date;

public class Type {
    public enum MetaType { Null, Int, Chars, Double, DateTime, PInfinity, NInfinity}
    private MetaType metaType = MetaType.Null;
    private Integer strMaxLen = 0;
    public MetaType getMetaType(){
        return metaType;
    }
    public MetaType setMetaType(MetaType mt){
        return this.metaType = mt;
    }
    public Integer getStrLen(){
        return strMaxLen;
    }
    public Integer setStrLen(Integer i){
        return strMaxLen = i;
    }
    public Integer getSize(){
        switch (metaType) {
            case Int:
                return 4; // 4bytes int
            case Double:
            case DateTime:
                return 8; // 8bytes double/datetime
            case Chars:
                return strMaxLen + 2; //strMaxLen (+2 bytes for length field)
            // that is how java's writeUTF works
        }
        return 1; // 1byte for everything
    }

    public static Type FromString(String org){
        Type t = new Type();
        t.setMetaType(MetaType.valueOf(org));
        return t;
    }

    public Value createEmptyValue(){
        Value v = new Value();
        v.setType(this);
        switch (metaType) {
            case Int:
            case Double:
                v.setValue(0);
                break;
            case DateTime:
                v.setDateValue(new Date());
                break;
            case Chars:
                v.setCharsValue(new char[1]); // it's 1
        }
        return v; // 1byte for everything
    }
}