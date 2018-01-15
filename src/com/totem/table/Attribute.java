package com.totem.table;

/**
 * sharing attribute between different module
 */
public class Attribute {
    private String tableName;
    private String columnName;

    public Attribute(String columnName, String tbName){
        tableName = tbName;
        this.columnName = columnName;
    }

    public Attribute(String columnName){
        this.columnName = columnName;
        this.tableName = "";
    }

    public Attribute(){
        this.columnName = "";
    }

    // type of this field
    private Type type;
    // mark if this attribute is index
    private boolean index;
    // mark if this attribute is row id? (aka physical order)
    private boolean rowId;
    private int attrId;

    // switch
    private boolean virtual;
    private String switchExpr;

    public String getTableName(){
        return tableName;
    }
    public String setTableName(String name) {
        return this.tableName = name;
    }
    public String getColumnName(){
        return columnName;
    }
    public String setColumnName(String name) {
        return this.columnName = name;
    }
    public Type getType(){
        return type;
    }
    public Type setType(Type type) {
        return this.type = type;
    }
    public boolean isIndex(){
        return index;
    }
    public boolean isRowId(){
        return rowId;
    }
    public boolean isVirtual(){
        return virtual;
    }
    public boolean setVirtual(boolean v){
        return virtual = v;
    }
    public boolean setIndex(boolean idx){
        return index = idx;
    }
    public boolean setRowId(boolean rid){
        return rowId = rid;
    }

    public int setColId(int cid){
        return attrId = cid;
    }

    public int getColId(){
        return attrId;
    }

    public String getSwitchExpr(){return switchExpr;}

    public String setSwitchExpr(String switchExpr) {
        return this.switchExpr = switchExpr;
    }

    public String getFullName(){
        if (tableName == null || tableName.equals(""))
            return columnName;
        return tableName + "." + columnName;
    }

    public boolean isAttribute(String name) {
        if (columnName == null)
            return false;
        if (columnName.equals(name) || getFullName().equals(name))
            return true;
        return false;
    }

    public int getSize(){
        if (!virtual)
            return type.getSize();
        else
            return 0;
    }
}
