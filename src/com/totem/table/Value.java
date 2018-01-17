package com.totem.table;

import java.nio.ByteBuffer;
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
        ByteBuffer buff = ByteBuffer.wrap(result);
        switch (type.getMetaType()) {
            case Int:
                buff.putInt(getIntegerValue());
                return result;
            case Chars:
                char[] bts = getCharsValue();
                result[0] = (byte)(type.getStrLen() & 0xff);
                result[1] = (byte)((type.getStrLen()>>8) & 0xff);
                for (int i = 0; i < bts.length; i++){
                    result[i + 2] = (byte)bts[i];
                }
                return result;
            case Double:
                buff.putDouble(getDoubleValue());
                return result;
            case DateTime:
                buff.putLong(getDateValue().getTime());
                return result;
        }
        return null;
    }

    public void fromSerial(byte[] bytes){
        ByteBuffer buff = ByteBuffer.wrap(bytes);
        switch (type.getMetaType()) {
            case Int:
                setIntegerValue(buff.getInt());
                return ;
            case Chars:
                char[] chars = new char[bytes.length];
                for (int i = 0; i < bytes.length; i++){
                    chars[i] = (char)bytes[i];
                }
                setCharsValue(chars);
                return ;
            case Double:
                setDoubleValue(buff.getDouble());
                return ;
            case DateTime:
                setDateValue(new Date(buff.getLong()));
                return ;
        }
    }
}
