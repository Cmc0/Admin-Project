package com.admin.im.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 即时通讯 申请结果
 */
@AllArgsConstructor
@Getter
public enum ImRequestResultEnum {
    PENDING((byte)1, "未决定"), //
    AGREED((byte)2, "已同意"), //
    REJECTED((byte)3, "已拒绝"), //
    ;

    @EnumValue
    @JsonValue
    private byte code;
    private String codeDescription; // code 说明

}
