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
    CONTACT((byte)1, "联系人"), //
    GROUP((byte)2, "群组"), //
    ;

    @EnumValue
    @JsonValue
    private byte code;
    private String codeDescription; // code 说明

    public String getSId(Long toId) {
        return getCode() + "_" + toId;
    }

}
