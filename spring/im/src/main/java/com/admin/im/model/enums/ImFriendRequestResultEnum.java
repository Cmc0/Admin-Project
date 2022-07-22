package com.admin.im.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 即时通讯 好友申请结果
 */
@AllArgsConstructor
@Getter
public enum ImFriendRequestResultEnum {
    PENDING((byte)1, "未决定"), //
    APPROVED((byte)2, "已同意"), //
    REJECTED((byte)3, "已拒绝"), //
    ;

    @EnumValue
    @JsonValue
    private byte code;
    private String codeDescription; // code 说明

    public static ImFriendRequestResultEnum getByCode(Byte code) {
        if (code == null) {
            return null;
        }
        for (ImFriendRequestResultEnum item : ImFriendRequestResultEnum.values()) {
            if (item.getCode() == code) {
                return item;
            }
        }
        return null;
    }

}
