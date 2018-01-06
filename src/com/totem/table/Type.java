package com.totem.table;

public class Type {
    public enum MetaType { Null, Int, Char, Double, DateTime, PInfinity, NInfinity}
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
            case Char:
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
}