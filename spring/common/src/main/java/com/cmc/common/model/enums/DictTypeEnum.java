package com.cmc.common.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 字典 类型
 */
@AllArgsConstructor
@Getter
public enum DictTypeEnum {
    DICT((byte)1, "字典"), //
    DICT_ITEM((byte)2, "字典项"), //
    ;

    @EnumValue
    @JsonValue
    private byte code;
    private String codeDescription; // code 说明

    public static DictTypeEnum getByCode(byte code) {
        for (DictTypeEnum item : DictTypeEnum.values()) {
            if (item.getCode() == code) {
                return item;
            }
        }
        return null;
    }

}
