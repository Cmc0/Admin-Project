package com.admin.im.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 即时通讯 在群组里的角色
 */
@AllArgsConstructor
@Getter
public enum ImGroupJoinRoleEnum {
    CREATOR((byte)1, "创建人"), //
    MANAGER((byte)2, "管理员"), //
    USER((byte)3, "普通用户"), //
    ;

    @EnumValue
    @JsonValue
    private byte code;
    private String codeDescription; // code 说明

}
