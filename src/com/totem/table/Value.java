package com.totem.table;

import java.util.Date;

public class Value {
    private Type type;
    private char[] strValue;
    private int intValue;
    private Date dateValue;
    private double doubleValue;

    public Type setType(Type t){
        return type = t;
    }

    public Type getType(){
        return type;
    }

    public char[] setCharsValue(char[] str){
        if (type.getMetaType() == Type.MetaType.Char)
            if (str.length <= type.getStrLen())
                return this.strValue = str;
        return null;
    }
    public char[] getCharsValue(){
        if (type.getMetaType() == Type.MetaType.Char)
            return strValue;
        return null;
    }
    public int setIntegerValue(Integer i){
        if (type.getMetaType() == Type.MetaType.Int)
            return intValue = i;
        return 0;
    }
    public int getIntegerValue(){
        if (type.getMetaType() == Type.MetaType.Int)
            return intValue;
        return 0;
    }
    public double setDoubleValue(double v) {
        if (type.getMetaType() == Type.MetaType.Double)
            return doubleValue = v;
        return 0;
    }
    public double getDoubleValue() {
        if (type.getMetaType() == Type.MetaType.Double)
            return doubleValue;
        return 0;
    }

    public Date getDateValue() {
        return dateValue;
    }

    public Date setDateValue(Date d){
        if (type.getMetaType() == Type.MetaType.DateTime){
            return this.dateValue = d;
        }
        return d;
    }

    public Object getValue() {
        switch (type.getMetaType()) {
            case Int:
                return getIntegerValue();
            case Char:
                return getCharsValue();
            case Double:
                return getDoubleValue();
            case DateTime:
                return getDateValue();
        }
        return null;
    }

    public Object setValue(Object o) {
        switch (type.getMetaType()) {
            case DateTime:
                return setDateValue((Date)o);
            case Double:
                return setDoubleValue((double)o);
            case Char:
                return setCharsValue((char[])o);
            case Int:
                return setIntegerValue((int) o);
        }
        return null;
    }

    public boolean toBoolean(){
        switch (type.getMetaType()) {
            case Int:
                return getIntegerValue() == 1;
            case Char:
                return getCharsValue() != null;
            case Double:
                return true;
            case DateTime:
                return getDateValue() != null;
        }
        return false;
    }

    public String toString(){
        return getValue().toString();
    }
}
