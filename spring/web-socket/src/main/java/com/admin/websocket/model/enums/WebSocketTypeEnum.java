package com.admin.websocket.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * WebSocket 在线状态
 */
@AllArgsConstructor
@Getter
public enum WebSocketTypeEnum {
    ONLINE((byte)1, "在线"), //
    HIDDEN((byte)2, "隐身"), //
    MATCH((byte)3, "用于匹配，没有实际意义，但是必须存在"), //
    ;

    @EnumValue
    @JsonValue
    private byte code;
    private String codeDescription; // code 说明

    public static WebSocketTypeEnum getByCode(byte code) {
        for (WebSocketTypeEnum item : WebSocketTypeEnum.values()) {
            if (item.getCode() == code) {
                return item;
            }
        }
        return null;
    }

}
