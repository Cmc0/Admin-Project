package com.admin.common.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 请求类别
 */
@AllArgsConstructor
@Getter
public enum SysRequestCategoryEnum {
    H5((byte)1, "H5（网页端）"), //
    APP((byte)2, "APP（移动端）"), //
    PC((byte)3, "PC（桌面程序）"), //
    WX_APP((byte)4, "微信小程序"), //
    MATCH((byte)5, "用于匹配，没有实际意义，但是必须存在"), //
    ;

    @EnumValue
    @JsonValue
    private byte code;
    private String codeDescription; // code 说明

    public static SysRequestCategoryEnum getByCode(Byte code) {
        if (code == null) {
            return H5;
        }
        for (SysRequestCategoryEnum item : SysRequestCategoryEnum.values()) {
            if (item.getCode() == code) {
                return item;
            }
        }
        return H5;
    }

}
