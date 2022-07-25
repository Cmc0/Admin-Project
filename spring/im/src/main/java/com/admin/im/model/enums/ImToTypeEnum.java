package com.admin.im.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 即时通讯 发送对象类型
 */
@AllArgsConstructor
@Getter
public enum ImToTypeEnum {
    FRIEND((byte)1, "好友"), //
    GROUP((byte)2, "群组"), //
    ;

    @EnumValue
    @JsonValue
    private byte code;
    private String codeDescription; // code 说明

    public static String getQId(ImToTypeEnum imToTypeEnum, Long toId, Long createId) {

        if (FRIEND.equals(imToTypeEnum)) {
            return imToTypeEnum.getCode() + "_" + (toId + createId);
        }
        return imToTypeEnum.getCode() + "_" + toId;

    }

}
