package com.admin.im.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 即时通讯 消息内容类型
 */
@AllArgsConstructor
@Getter
public enum ImContentTypeEnum {
    TEXT((byte)1, "文字"), //
    IMAGE((byte)2, "图片"), //
    FILE((byte)3, "文件"), //
    EXPRESSION((byte)4, "表情"), //
    LINK((byte)5, "链接"), //
    ;

    @EnumValue
    @JsonValue
    private byte code;
    private String codeDescription; // code 说明

    public static ImContentTypeEnum getByCode(Byte code) {
        if (code == null) {
            return null;
        }
        for (ImContentTypeEnum item : ImContentTypeEnum.values()) {
            if (item.getCode() == code) {
                return item;
            }
        }
        return null;
    }

}
