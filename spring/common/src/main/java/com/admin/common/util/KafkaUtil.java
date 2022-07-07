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
     * 消息推送：公告推送
     */
    public static void bulletinPush(Set<Long> userIdSet) {

        WebSocketMessageEnum newBulletin = WebSocketMessageEnum.NEW_BULLETIN;
        newBulletin.setJson(JSONUtil.createObj().set("userIdSet", userIdSet));
        sendWebSocketMessageByKafka(newBulletin);

    }

    /**
     * 消息推送：有新的通知
     */
    public static void refreshNoReadNotifyCount(Set<Long> userIdSet) {

        WebSocketMessageEnum newNotify = WebSocketMessageEnum.NEW_NOTIFY;
        newNotify.setJson(JSONUtil.createObj().set("userIdSet", userIdSet));
        sendWebSocketMessageByKafka(newNotify);

    }

    /**
     * 消息推送：账号在其他地方登录，您被迫下线
     */
    public static void forcedOffline(Set<Long> webSocketIdSet, Set<Long> userIdSet) {

        WebSocketMessageEnum forcedOffline = WebSocketMessageEnum.FORCED_OFFLINE;
        forcedOffline.setJson(JSONUtil.createObj().set("webSocketIdSet", webSocketIdSet).set("userIdSet", userIdSet));
        sendWebSocketMessageByKafka(forcedOffline);

    }

    /**
     * 消息推送：登录过期，请重新登录
     */
    public static void loginExpired(Set<Long> webSocketIdSet) {

        WebSocketMessageEnum loginExpired = WebSocketMessageEnum.LOGIN_EXPIRED;
        loginExpired.setJson(JSONUtil.createObj().set("webSocketIdSet", webSocketIdSet));
        sendWebSocketMessageByKafka(loginExpired);

    }

    /**
     * 消息推送：账号已被注销
     */
    public static void delAccount(Set<Long> userIdSet) {

        WebSocketMessageEnum delAccount = WebSocketMessageEnum.DEL_ACCOUNT;
        delAccount.setJson(JSONUtil.createObj().set("userIdSet", userIdSet));
        sendWebSocketMessageByKafka(delAccount);

    }

}
