package com.totem.table;

import junit.framework.TestCase;
import org.junit.Test;

import static org.junit.Assert.*;

public class TableSchemeTest {

    @Test
    public void createScheme() {
        // "sys_class[name:Chars(32), isDeputyClass:Int]";
        TableScheme scheme_class = TableScheme.CreateScheme(TableScheme.sys_class_scheme);
        assertEquals("system class is not deputy class", false, scheme_class.deputyTable);
        assertEquals("system class has no select cond", scheme_class.selectCond, "");

        assertEquals("system class has 2 attrs",2, scheme_class.attrList.length);

        assertEquals("sys_class[0].name == name", scheme_class.attrList[0].getColumnName(), "name");
        assertEquals("sys_class[0].size == 32+2", scheme_class.attrList[0].getSize(), 34);
        assertEquals("sys_class[0].virtual == false", scheme_class.attrList[0].isVirtual(), false);
        assertEquals("sys_class[0].index == false", scheme_class.attrList[0].isIndex(), false);
        assertEquals("sys_class[0].tableName == sys_class", "sys_class", scheme_class.attrList[0].getTableName());
        assertEquals("sys_class[0].type == Chars", scheme_class.attrList[0].getType().getMetaType(), Type.MetaType.Chars);

        assertEquals("sys_class[1].name == isDeputyClass", scheme_class.attrList[1].getColumnName(), "isDeputyClass");
        assertEquals("sys_class[1].size == 4", scheme_class.attrList[1].getSize(), 4);
        assertEquals("sys_class[1].virtual == false", scheme_class.attrList[1].isVirtual(), false);
        assertEquals("sys_class[1].index == false", scheme_class.attrList[1].isIndex(), false);
        assertEquals("sys_class[1].tableName == sys_class", scheme_class.attrList[1].getTableName(), "sys_class");
        assertEquals("sys_class[1].type == Int", scheme_class.attrList[1].getType().getMetaType(), Type.MetaType.Int);
    }
}