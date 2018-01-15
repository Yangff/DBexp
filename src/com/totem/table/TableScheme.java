package com.totem.table;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Scheme of a table
 * define the type of each column
 */
public class TableScheme {
    public Attribute[] attrList;
    public String tableName;

    // deputy related
    public boolean deputyTable;
    public String selectCond;

    // system table schemes
    public static final String sys_class_scheme;
    public static final String sys_attribute_scheme;
    public static final String sys_deputyRelation_scheme;
    public static final String sys_virtualAttribute_scheme;

    static {
        sys_class_scheme = "sys_class[name:Chars(32), isDeputyClass:Int]";
        sys_attribute_scheme = "sys_attribute[*class:Int, name:Chars(32), type:Int, virtual:Int]";
        sys_deputyRelation_scheme = "sys_deputyRelation[*deputyClass: Int, *parentClass: Int, deputyCond: Chars(32)]";
        sys_virtualAttribute_scheme = "sys_virtualAttribute[*attributeId: Int, switch: Chars(32)]";
    }

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
     *                 sys_virtualAttribute[*fromAttribute: Int, *forAttribute: Int, switch: Chars(32)]
     *                 Regex:
     *            (^\s*(?P<tableName>[a-zA-Z0-9_]+))\s*\[\s*|((?P<isIndex>\**)(?P<attributeName>[a-zA-Z0-9_]+)\s*:\s*(?P<type>Chars\((?P<size>\d+)\)|Int|DateTime|Double)\s*((,\s*(?!$))|(\]\s*(?=$))))
     *
     * @return scheme created by that string
     */
    public static TableScheme CreateScheme(String def) {
        Pattern patternScheme = Pattern.compile("(^\\s*(?<tableName>[a-zA-Z0-9_]+))\\s*\\[\\s*|((?<isIndex>\\**)(?<attributeName>[a-zA-Z0-9_]+)\\s*:\\s*(?<type>Chars\\((?<size>\\d+)\\)|Int|DateTime|Double)\\s*((,\\s*(?!$))|(\\]\\s*(?=$))))");
        final Matcher matcher = patternScheme.matcher(def);
        TableScheme tableScheme = new TableScheme();
        tableScheme.deputyTable = false;
        tableScheme.selectCond = "";
        String tableName = null;
        ArrayList<Attribute> attrs = new ArrayList<>();
        int attrId = 1;
        while (matcher.find()) {
            if (matcher.group("tableName") != null && !matcher.group("tableName").isEmpty()) {
                tableName = tableScheme.tableName = matcher.group("tableName");
                continue;
            }
            if (tableName == null || tableName.isEmpty()){
                return null;
            }
            Attribute attr = new Attribute();
            attr.setColId(attrId);
            attr.setVirtual(false);
            attr.setTableName(tableName);
            if (matcher.group("attributeName") != null && !matcher.group("attributeName").isEmpty()) {
                attr.setColumnName(matcher.group("attributeName"));
            } else {
                return null; // all attributes must have name
            }
            if (matcher.group("type") != null && !matcher.group("isIndex").isEmpty()) {
                attr.setIndex(true);
            } else {
                attr.setIndex(false);
            }
            if (matcher.group("type") != null && !matcher.group("type").isEmpty()) {
                Type t;
                if (matcher.group("size") != null && !matcher.group("size").isEmpty()) {
                    t = Type.FromString(matcher.group("type").split("\\(")[0]);
                    t.setStrLen(Integer.valueOf(matcher.group("size")));
                } else {
                    t = Type.FromString(matcher.group("type"));
                }
                attr.setType(t);
            }
            attrs.add(attr);
        }
        tableScheme.attrList = attrs.toArray(new Attribute[attrs.size()]);
        return tableScheme;
    }

    public Cell[] createEmptyRow(){
        int cnt = 0;
        for (int i = 0; i < attrList.length; i++){
            if (!attrList[i].isVirtual())
                cnt ++;
        }
        Cell[] rst = new Cell[cnt];
        for (int i = 0, j = 0; i < attrList.length; i++){
            if (!attrList[i].isVirtual()) {
                rst[j] = new Cell();
                rst[j].setAttribute(attrList[i]);
                rst[j].setValue(attrList[i].getType().createEmptyValue());
                j++;
            }
        }
        return rst;
    }
}
