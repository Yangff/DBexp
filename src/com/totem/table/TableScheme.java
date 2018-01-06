package com.totem.table;

public class TableScheme {
    public Attribute[] attrList;

    /**
     * Generate table scheme by def string
     * @param def scheme definition string
     *            Format: TableName[*ColumnName1:Type1, ColumnName2:Type2,...]
     *            `*` for index
     *            Name can be /([a-zA-Z0-9]+/
     *            Type can be one of:
     *              * Chars(MaxLength)
     *              * Int
     *              * Double
     *              * DateTime
     *            e.g. sys_class[name:Chars(32), deputyClass:Int, deputyBy:Int]
     *                 sys_attribute[*class:Int, name:Chars(32), type:Int, virtual:Int]
     *                 sys_deputyRelation[*deputyClass: Int, *parentClass: Int, deputyCond: Chars(32)]
     *                 sys_virtualAttribute[*fromAttribute: Int, *forAttribute: Int, genCond: Chars(32)]
     *
     * @return
     */
    public static TableScheme CreateScheme(String def) {
        return new TableScheme();
    }
}
