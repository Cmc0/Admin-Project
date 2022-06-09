package com.cmc.websocket.listener;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.cmc.common.model.constant.BaseConstant;
import com.cmc.websocket.configuration.MyChannelGroupHelper;
import com.cmc.websocket.model.enums.WebSocketMessageEnum;
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
    public void receive(String jsonStr) {

        JSONObject jsonObject = JSONUtil.parseObj(jsonStr);

        Integer code = jsonObject.getInt("code");
        if (code == null) {
            return;
        }

        WebSocketMessageEnum webSocketMessageEnum = WebSocketMessageEnum.getByCode(code);
        if (webSocketMessageEnum == null) {
            return;
        }

        BeanUtil.copyProperties(jsonObject, webSocketMessageEnum);

        handleWebSocketMessageEnum(webSocketMessageEnum);
    }

    /**
     * 处理 webSocketMessageEnum
     */
    private void handleWebSocketMessageEnum(WebSocketMessageEnum webSocketMessageEnum) {

        int code = webSocketMessageEnum.getCode();
        JSONObject json = webSocketMessageEnum.getJson();
        List<Channel> channelList;

        if (code == 1) {
            // 1 socket连接记录主键id
        } else if (code >= 2 && code <= 4) {

            // 2 账号已在其他地方登录，您被迫下线
            // 3 登录过期，请重新登录
            // 4 账号已被注销
            Set<Number> idSet = json.get("userIdSet", Set.class);

            if (CollUtil.isEmpty(idSet)) {
                idSet = json.get("socketIdSet", Set.class);
                channelList = MyChannelGroupHelper.getChannelByIdSet(MyChannelGroupHelper.WEB_SOCKET_ID_KEY, idSet);
            } else {
                channelList = MyChannelGroupHelper.getChannelByIdSet(MyChannelGroupHelper.USER_ID_KEY, idSet);
            }

            if (CollUtil.isNotEmpty(channelList)) {
                writeAndFlush(channelList, webSocketMessageEnum);
            }

        } else if (code == 5) {

            // 5 有新的通知
            Set<Number> userIdSet = json.get("userIdSet", Set.class);

            channelList = MyChannelGroupHelper.getChannelByIdSet(MyChannelGroupHelper.USER_ID_KEY, userIdSet);

            if (CollUtil.isNotEmpty(channelList)) {
                writeAndFlush(channelList, webSocketMessageEnum);
            }

        } else if (code == 6) {

            // 6 有新的公告
            Set<Number> userIdSet = json.get("userIdSet", Set.class);

            if (CollUtil.isEmpty(userIdSet)) {
                writeAndFlush(null, webSocketMessageEnum);
            } else {
                channelList = MyChannelGroupHelper.getChannelByIdSet(MyChannelGroupHelper.USER_ID_KEY, userIdSet);
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
            MyChannelGroupHelper.send2All(webSocketMessageEnum); // 发送给所有人
        } else {
            channelList.forEach(it -> it.writeAndFlush(webSocketMessageEnum));
        }

    }

}
