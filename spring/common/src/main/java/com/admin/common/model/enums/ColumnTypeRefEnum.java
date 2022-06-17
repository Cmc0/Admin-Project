package com.admin.common.model.enums;

import cn.hutool.core.util.StrUtil;
import com.admin.common.model.vo.SyncEntityFromDbVO;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ColumnTypeRefEnum {

    Boolean(ColumnTypeRefEnum.TINYINT_ONE, "Boolean", "boolean"), //
    tinyint("tinyint", "Byte", "number"), //
    Integer("int", "Integer", "number"), //
    bigint("bigint", "Long", "number"), //
    varchar("varchar", "String", "string"), //
    text("text", "String", "string"), //
    longtext("longtext", "String", "string"), //
    datetime("datetime", "Date", "string"), //
    ;

    public static final String TINYINT_ONE = "tinyint(1)";

    private String columnType; // 数据库字段类型
    private String javaType; // java数据类型
    private String tsType; // ts数据类型

    public static ColumnTypeRefEnum getByColumnType(SyncEntityFromDbVO vo) {

        if (ColumnTypeRefEnum.TINYINT_ONE.equals(vo.getColumnType())) {
            return Boolean;
        }

        String subBefore = StrUtil.subBefore(vo.getColumnType(), "(", false);

        for (ColumnTypeRefEnum item : ColumnTypeRefEnum.values()) {
            if (item.getColumnType().equals(subBefore)) {
                return item;
            }
        }

        return null;
    }

    public static ColumnTypeRefEnum getByJavaType(String javaType) {

        for (ColumnTypeRefEnum item : ColumnTypeRefEnum.values()) {
            if (item.getJavaType().equals(javaType)) {
                return item;
            }
        }

        return null;
    }

}
