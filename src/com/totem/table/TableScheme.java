package com.totem.table;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TableScheme {
    public Attribute[] attrList;
    public String tableName;

    /**
     * Generate table scheme by def string
     * Especially system table scheme
     * deputy class and virtual attribute is not under consideration.
     * @param def scheme definition string
     *            Format: TableName[*ColumnName1:Type1, ColumnName2:Type2,...]
     *            `*` for index
     *            Name can be /([a-zA-Z0-9_]+/
     *            Type can be one of:
     *              * Chars(MaxLength)
     *              * Int
     *              * Double
     *              * DateTime
     *            e.g. sys_class[name:Chars(32), isDeputyClass:Int]
     *                 sys_attribute[*class:Int, name:Chars(32), type:Int, virtual:Int]
     *                 sys_deputyRelation[*deputyClass: Int, *parentClass: Int, deputyCond: Chars(32)]
     *                 sys_virtualAttribute[*fromAttribute: Int, *forAttribute: Int, genCond: Chars(32)]
     *                 Regex:
     *            (^\s*(?P<tableName>[a-zA-Z0-9_]+))\s*\[\s*|((?P<isIndex>\**)(?P<attributeName>[a-zA-Z0-9_]+)\s*:\s*(?P<type>Chars\((?P<size>\d+)\)|Int|DateTime|Double)\s*((,\s*(?!$))|(\]\s*(?=$))))
     *
     * @return scheme created by that string
     */
    public static TableScheme CreateScheme(String def) {
        Pattern patternScheme = Pattern.compile("(^\\s*(?<tableName>[a-zA-Z0-9_]+))\\s*\\[\\s*|((?<isIndex>\\**)(?<attributeName>[a-zA-Z0-9_]+)\\s*:\\s*(?<type>Chars\\((?<size>\\d+)\\)|Int|DateTime|Double)\\s*((,\\s*(?!$))|(\\]\\s*(?=$))))");
        final Matcher matcher = patternScheme.matcher(def);
        TableScheme tableScheme = new TableScheme();
        String tableName = null;
        ArrayList<Attribute> attrs = new ArrayList<>();
        while (matcher.find()) {
            if (!matcher.group("tableName").isEmpty()) {
                tableName = tableScheme.tableName = matcher.group("tableName");
                continue;
            }
            if (tableName == null || tableName.isEmpty()){
                return null;
            }
            Attribute attr = new Attribute();
            attr.setTableName(tableName);
            if (!matcher.group("attributeName").isEmpty()) {
                attr.setColumnName(matcher.group("attributeName"));
            }
            if (!matcher.group("isIndex").isEmpty()) {
                attr.setIndex(true);
            } else {
                attr.setIndex(false);
            }
            if (!matcher.group("type").isEmpty()) {
                Type t = Type.FromString(matcher.group("type"));
                if (!matcher.group("size").isEmpty()) {
                    t.setStrLen(Integer.valueOf(matcher.group("size")));
                }
                attr.setType(t);
            }
            attrs.add(attr);
        }
        tableScheme.attrList = (Attribute[])attrs.toArray();
        return tableScheme;
    }
}
