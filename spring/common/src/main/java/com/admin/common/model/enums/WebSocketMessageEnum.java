package com.admin.common.model.enums;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.admin.common.exception.BaseBizCodeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 给 WebSocket以及 mq，推送消息的 枚举类
 */
@AllArgsConstructor
@Getter
public enum WebSocketMessageEnum {
    SOCKET_ID(1, null, "webSocket连接记录主键 id"), // json:{ webSocketId }
    FORCED_OFFLINE(2, null,
        BaseBizCodeEnum.FORCED_OFFLINE.getMsg()), // json:{ userIdSet/webSocketIdSet }，账号已在其他地方登录，您被迫下线
    LOGIN_EXPIRED(3, null, BaseBizCodeEnum.LOGIN_EXPIRED.getMsg()), // json:{ webSocketIdSet }，登录过期，请重新登录
    DEL_ACCOUNT(4, null, "账号已被注销"), // json:{ userIdSet }
    NEW_NOTIFY(5, null, "有新的通知"), // json:{ userIdSet }
    NEW_BULLETIN(6, null, "有新的公告"), // json:{ userIdSet }
    FRIEND_REQUEST(7, null, "有新的好友申请"), // json:{ userIdSet }
    FRIEND_REQUEST_AGREED(8, null, "好友申请已通过"), // json:{ userIdSet }
    ;

    private int code;
    @Setter
    private JSONObject json; // 额外信息
    private String codeDescription; // code 说明

    public String toJsonString() {
        return JSONUtil.toJsonStr(BeanUtil.beanToMap(this));
    }

    public static WebSocketMessageEnum getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (WebSocketMessageEnum item : WebSocketMessageEnum.values()) {
            if (item.getCode() == code) {
                return item;
            }
        }
        return null;
    }

}
