package com.cmc.projectutil.util;

import cn.hutool.json.JSONObject;

public class CodeGenerateHelperUtil {

    public static final String TINYINT_ONE = "tinyint(1)" ;

    public static final JSONObject COLUMN_TYPE_REF_MAP = new JSONObject();

    static {
        COLUMN_TYPE_REF_MAP.set(TINYINT_ONE, "Boolean");
        COLUMN_TYPE_REF_MAP.set("tinyint", "Byte");
        COLUMN_TYPE_REF_MAP.set("int", "Integer");
        COLUMN_TYPE_REF_MAP.set("bigint", "Long");
        COLUMN_TYPE_REF_MAP.set("varchar", "String");
        COLUMN_TYPE_REF_MAP.set("text", "String");
        COLUMN_TYPE_REF_MAP.set("longtext", "String");
        COLUMN_TYPE_REF_MAP.set("datetime", "Date");
    }

}
