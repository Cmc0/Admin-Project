package com.admin.websocket.listener;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONObject;
import com.admin.common.model.constant.BaseConstant;
import com.admin.common.model.enums.WebSocketMessageEnum;
import com.admin.websocket.configuration.MyNettyChannelGroupHelper;
import io.netty.channel.Channel;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * WebSocket 监听器
 */
@Component
@KafkaListener(topics = {BaseConstant.MQ_WEB_SOCKET_TOPIC}, containerFactory = "dynamicGroupIdContainerFactory")
public class WebSocketListener {

    @KafkaHandler
    public void receive(WebSocketMessageEnum webSocketMessageEnum) {
        handleWebSocketMessageEnum(webSocketMessageEnum);
    }

    /**
     * 处理 webSocketMessageEnum
     */
    private void handleWebSocketMessageEnum(WebSocketMessageEnum webSocketMessageEnum) {

        int code = webSocketMessageEnum.getCode();
        JSONObject json = webSocketMessageEnum.getJson();
        List<Channel> channelList;

        if (code >= 2 && code <= 4) {

            // 2 账号已在其他地方登录，您被迫下线
            // 3 登录过期，请重新登录
            // 4 账号已被注销
            Set<Number> idSet = json.get("userIdSet", Set.class);

            if (CollUtil.isEmpty(idSet)) {
                idSet = json.get("webSocketIdSet", Set.class);
                channelList =
                    MyNettyChannelGroupHelper.getChannelByIdSet(MyNettyChannelGroupHelper.WEB_SOCKET_ID_KEY, idSet);
            } else {
                channelList = MyNettyChannelGroupHelper.getChannelByIdSet(MyNettyChannelGroupHelper.USER_ID_KEY, idSet);
            }

            if (CollUtil.isNotEmpty(channelList)) {
                writeAndFlush(channelList, webSocketMessageEnum);
            }

        } else if (code == 5) {

            // 5 有新的通知
            Set<Number> userIdSet = json.get("userIdSet", Set.class);

            channelList = MyNettyChannelGroupHelper.getChannelByIdSet(MyNettyChannelGroupHelper.USER_ID_KEY, userIdSet);

            if (CollUtil.isNotEmpty(channelList)) {
                writeAndFlush(channelList, webSocketMessageEnum);
            }

        } else if (code == 6) {

            // 6 有新的公告
            Set<Number> userIdSet = json.get("userIdSet", Set.class);

            if (CollUtil.isEmpty(userIdSet)) {
                writeAndFlush(null, webSocketMessageEnum);
            } else {
                channelList =
                    MyNettyChannelGroupHelper.getChannelByIdSet(MyNettyChannelGroupHelper.USER_ID_KEY, userIdSet);
                writeAndFlush(channelList, webSocketMessageEnum);
            }

        }
    }

    /**
     * 推送消息
     */
    private void writeAndFlush(List<Channel> channelList, WebSocketMessageEnum webSocketMessageEnum) {

        webSocketMessageEnum.setJson(null);

        if (channelList == null) {
            MyNettyChannelGroupHelper.sendToAll(webSocketMessageEnum); // 发送给所有人
        } else {
            channelList.forEach(it -> it.writeAndFlush(webSocketMessageEnum));
        }

    }

}
