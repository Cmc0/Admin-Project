package com.cmc.websocket.configuration;

import com.cmc.websocket.model.enums.enums.WebSocketMessageEnum;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype") // 多例
public class MyNettyWebSocketHandler extends SimpleChannelInboundHandler<WebSocketMessageEnum> {

    /**
     * 收到消息时
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketMessageEnum webSocketMessageEnum) {

    }

}
