package com.admin.im.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 即时通讯 消息创建来源
 */
@AllArgsConstructor
@Getter
public enum ImMessageCreateTypeEnum {
    USER((byte)1, "用户"), //
    REQUEST_RESULT((byte)2, "通过验证"), //
    CREATE_COMPLETE((byte)3, "创建完成"), //
    ;

    @EnumValue
    @JsonValue
    private byte code;
    private String codeDescription; // code 说明

}
