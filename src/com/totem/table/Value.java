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
        if (type == null)
            return null;
        if (type.getMetaType() == Type.MetaType.Chars)
            if (str.length <= type.getStrLen())
                return this.strValue = str;
        return null;
    }
    public char[] getCharsValue(){
        if (type == null) {
            return null;
        }
        if (type.getMetaType() == Type.MetaType.Chars)
            return strValue;
        return null;
    }
    public int setIntegerValue(Integer i){
        if (type == null) {
            return 0;
        }
        if (type.getMetaType() == Type.MetaType.Int)
            return intValue = i;
        return 0;
    }
    public int getIntegerValue(){
        if (type == null)
            return 0;
        if (type.getMetaType() == Type.MetaType.Int)
            return intValue;
        return 0;
    }
    public double setDoubleValue(double v) {
        if (type == null)
            return 0;
        if (type.getMetaType() == Type.MetaType.Double)
            return doubleValue = v;
        return 0;
    }
    public double getDoubleValue() {
        if (type == null)
            return 0;
        if (type.getMetaType() == Type.MetaType.Double)
            return doubleValue;
        return 0;
    }

    public Date getDateValue() {
        if (type == null)
            return null;
        if (type.getMetaType() == Type.MetaType.DateTime) {
            return dateValue;
        } else {
            return null;
        }
    }

    public Date setDateValue(Date d){
        if (type == null)
            return null;
        if (type.getMetaType() == Type.MetaType.DateTime){
            return this.dateValue = d;
        }
        return d;
    }

    public Object getValue() {
        if (type == null)
            return null;
        switch (type.getMetaType()) {
            case Int:
                return getIntegerValue();
            case Chars:
                return getCharsValue();
            case Double:
                return getDoubleValue();
            case DateTime:
                return getDateValue();
        }
        return null;
    }

    public Object setValue(Object o) {
        if (type == null)
            return null;
        switch (type.getMetaType()) {
            case DateTime:
                return setDateValue((Date)o);
            case Double:
                return setDoubleValue((double)o);
            case Chars:
                return setCharsValue((char[])o);
            case Int:
                return setIntegerValue((int) o);
        }
        return null;
    }

    public boolean toBoolean(){
        if (type == null)
            return false;
        switch (type.getMetaType()) {
            case Int:
                return getIntegerValue() == 1;
            case Chars:
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

    public byte[] getSerial() {
        byte[] result;
        int len = type.getSize();
        result = new byte[len];
        switch (type.getMetaType()) {
            case Int:
                result[0] = (byte)(((long)getIntegerValue()) & 0xff);
                result[1] = (byte)(((long)getIntegerValue()>>8) & 0xff);
                result[2] = (byte)(((long)getIntegerValue()>>16) & 0xff);
                result[3] = (byte)(((long)getIntegerValue()>>24) & 0xff);
                return result;
            case Chars:
                char[] bts = getCharsValue();
                result[0] = (byte)(bts.length & 0xff);
                result[1] = (byte)((bts.length>>8) & 0xff);
                for (int i = 0; i < bts.length; i++){
                    result[i + 2] = (byte)bts[i];
                }
                return result;
            case Double:
                return result;
            case DateTime:
                return result;
        }
        return null;
    }
}
