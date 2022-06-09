package com.cmc.websocket.configuration;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.net.url.UrlQuery;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import com.cmc.common.exception.BaseBizCodeEnum;
import com.cmc.common.model.constant.BaseConstant;
import com.cmc.common.model.enums.RequestCategoryEnum;
import com.cmc.common.model.vo.ApiResultVO;
import com.cmc.websocket.model.entity.WebSocketDO;
import com.cmc.websocket.model.enums.WebSocketMessageEnum;
import com.cmc.websocket.model.enums.WebSocketTypeEnum;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Scope("prototype") // 多例
public class MyNettyWebSocketHandler extends SimpleChannelInboundHandler<WebSocketMessageEnum> {

    @Resource
    RedisTemplate<String, WebSocketDO> redisTemplate;

    @SneakyThrows
    @Override
    public void channelInactive(ChannelHandlerContext ctx) {

        Long socketId = ctx.channel().attr(MyNettyChannelGroupHelper.WEB_SOCKET_ID_KEY).get();

        super.channelInactive(ctx);
    }

    @SneakyThrows
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {

        // 首次连接是 FullHttpRequest，处理参数
        if (msg instanceof FullHttpRequest) {
            FullHttpRequest request = (FullHttpRequest)msg;
            UrlQuery urlQuery = UrlQuery.of(request.uri(), CharsetUtil.CHARSET_UTF_8);

            String code = Convert.toStr(urlQuery.get("code")); // 随机码
            Byte category = Convert.toByte(urlQuery.get(BaseConstant.REQUEST_HEADER_CATEGORY));

            if (category == null || StrUtil.isBlank(code)) {
                ApiResultVO.error(BaseBizCodeEnum.ILLEGAL_REQUEST);
            }

            ValueOperations<String, WebSocketDO> ops = redisTemplate.opsForValue();

            String redisKey = NettyServer.webSocketRegCodePreLockKey + code;

            WebSocketDO webSocketDO = ops.get(redisKey);
            if (webSocketDO == null) {
                ApiResultVO.error(BaseBizCodeEnum.ILLEGAL_REQUEST);
            }

            // 删除 redis中该 key，目的：只能用一次
            redisTemplate.delete(redisKey);

            // 由于 存在 redis中的是 数字，在给对象赋值的时候，是从 下标为 0开始进行匹配的，所以这里要 减 1
            webSocketDO.setType(WebSocketTypeEnum.getByCode((byte)(webSocketDO.getType().getCode() - 1)));
            webSocketDO.setCategory(RequestCategoryEnum.getByCode(category)); // 类别
            // 保存到数据库

            // 上线操作
            online(webSocketDO, ctx.channel());
        }

        super.channelRead(ctx, msg);
    }

    private void online(WebSocketDO webSocketDO, Channel channel) {

        // 绑定 userId
        channel.attr(MyNettyChannelGroupHelper.USER_ID_KEY).set(webSocketDO.getUserId());
        // 绑定 WebSocket 连接记录主表，主键id
        channel.attr(MyNettyChannelGroupHelper.WEB_SOCKET_ID_KEY).set(webSocketDO.getId());

        MyNettyChannelGroupHelper.CHANNEL_GROUP.add(channel); // 备注：断开连接之后，ChannelGroup 会自动移除该通道

    }

    /**
     * 收到消息时
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketMessageEnum webSocketMessageEnum) {

    }

}
