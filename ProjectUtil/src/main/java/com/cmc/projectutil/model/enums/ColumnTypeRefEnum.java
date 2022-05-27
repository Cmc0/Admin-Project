package com.cmc.projectutil.model.enums;

import cn.hutool.core.util.StrUtil;
import com.cmc.projectutil.model.dto.CodeGenerateListDTO;
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

    public static ColumnTypeRefEnum getByColumnType(CodeGenerateListDTO dto) {

        if (ColumnTypeRefEnum.TINYINT_ONE.equals(dto.getColumnType())) {
            return Boolean;
        }

        String subBefore = StrUtil.subBefore(dto.getColumnType(), "(", false);

        for (ColumnTypeRefEnum item : ColumnTypeRefEnum.values()) {
            if (item.getColumnType().equals(subBefore)) {
                return item;
            }
        }

        return null;
    }

}
