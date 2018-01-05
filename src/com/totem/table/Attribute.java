package com.totem.table;

/**
 * sharing attribute between different module
 */
public class Attribute {
    private String tableName;
    private String columnName;

    private Type type;
    private boolean index;
    private boolean rowId;
    private boolean virtual;

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
    public String getFullName(){
        if (tableName.equals(""))
            return columnName;
        return tableName + "." + columnName;
    }
    public boolean isAttribute(String name) {
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
