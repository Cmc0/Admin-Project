package com.admin.common.util;

import cn.hutool.json.JSONUtil;
import com.admin.common.model.constant.BaseConstant;
import com.admin.common.model.enums.WebSocketMessageEnum;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Set;

/**
 * Kafka 工具类
 */
@Component
public class KafkaUtil {

    private static KafkaTemplate<String, Object> kafkaTemplate;

    @Resource
    private void setKafkaTemplate(KafkaTemplate<String, Object> val) {
        KafkaUtil.kafkaTemplate = val;
    }

    /**
     * 给【webSocket】发送消息：通过 消息中间件
     * {@link com.admin.websocket.listener.WebSocketListener}
     */
    private static void sendWebSocketMessageByKafka(WebSocketMessageEnum webSocketMessageEnum) {
        try {
            kafkaTemplate.send(BaseConstant.MQ_WEB_SOCKET_TOPIC, webSocketMessageEnum.toJsonString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 消息推送：即时通讯，发送消息
     */
    public static void imSend(Set<Long> userIdSet) {

        WebSocketMessageEnum webSocketMessageEnum = WebSocketMessageEnum.IM_SEND;
        webSocketMessageEnum.setJson(JSONUtil.createObj().set("userIdSet", userIdSet));
        sendWebSocketMessageByKafka(webSocketMessageEnum);

    }

    /**
     * 消息推送：好友申请已通过
     */
    public static void friendRequestAgreed(Set<Long> userIdSet) {

        WebSocketMessageEnum webSocketMessageEnum = WebSocketMessageEnum.FRIEND_REQUEST_AGREED;
        webSocketMessageEnum.setJson(JSONUtil.createObj().set("userIdSet", userIdSet));
        sendWebSocketMessageByKafka(webSocketMessageEnum);

    }

    /**
     * 消息推送：公告推送
     */
    public static void newBulletin(Set<Long> userIdSet) {

        WebSocketMessageEnum webSocketMessageEnum = WebSocketMessageEnum.NEW_BULLETIN;
        webSocketMessageEnum.setJson(JSONUtil.createObj().set("userIdSet", userIdSet));
        sendWebSocketMessageByKafka(webSocketMessageEnum);

    }

    /**
     * 消息推送：有新的通知
     */
    public static void newNotify(Set<Long> userIdSet) {

        WebSocketMessageEnum webSocketMessageEnum = WebSocketMessageEnum.NEW_NOTIFY;
        webSocketMessageEnum.setJson(JSONUtil.createObj().set("userIdSet", userIdSet));
        sendWebSocketMessageByKafka(webSocketMessageEnum);

    }

    /**
     * 消息推送：账号在其他地方登录，您被迫下线
     */
    public static void forcedOffline(Set<Long> webSocketIdSet, Set<Long> userIdSet) {

        WebSocketMessageEnum webSocketMessageEnum = WebSocketMessageEnum.FORCED_OFFLINE;
        webSocketMessageEnum
            .setJson(JSONUtil.createObj().set("webSocketIdSet", webSocketIdSet).set("userIdSet", userIdSet));
        sendWebSocketMessageByKafka(webSocketMessageEnum);

    }

    /**
     * 消息推送：登录过期，请重新登录
     */
    public static void loginExpired(Set<Long> webSocketIdSet) {

        WebSocketMessageEnum webSocketMessageEnum = WebSocketMessageEnum.LOGIN_EXPIRED;
        webSocketMessageEnum.setJson(JSONUtil.createObj().set("webSocketIdSet", webSocketIdSet));
        sendWebSocketMessageByKafka(webSocketMessageEnum);

    }

    /**
     * 消息推送：账号已被注销
     */
    public static void delAccount(Set<Long> userIdSet) {

        WebSocketMessageEnum webSocketMessageEnum = WebSocketMessageEnum.DEL_ACCOUNT;
        webSocketMessageEnum.setJson(JSONUtil.createObj().set("userIdSet", userIdSet));
        sendWebSocketMessageByKafka(webSocketMessageEnum);

    }

    /**
     * 消息推送：有新的好友申请
     */
    public static void friendRequest(Set<Long> userIdSet) {

        WebSocketMessageEnum webSocketMessageEnum = WebSocketMessageEnum.FRIEND_REQUEST;
        webSocketMessageEnum.setJson(JSONUtil.createObj().set("userIdSet", userIdSet));
        sendWebSocketMessageByKafka(webSocketMessageEnum);

    }

}
