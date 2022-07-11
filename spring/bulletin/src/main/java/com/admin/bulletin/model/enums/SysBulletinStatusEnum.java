package com.admin.bulletin.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 公告状态
 */
@AllArgsConstructor
@Getter
public enum SysBulletinStatusEnum {
    DRAFT((byte)1, "草稿"), //
    PUBLICITY((byte)2, "公示"), //
    ;

    @EnumValue
    @JsonValue
    private byte code;
    private String codeDescription; // code 说明

    public static SysBulletinStatusEnum getByCode(Byte code) {
        if (code == null) {
            return null;
        }
        for (SysBulletinStatusEnum item : SysBulletinStatusEnum.values()) {
            if (item.getCode() == code) {
                return item;
            }
        }
        return null;
    }

}
