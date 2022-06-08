package com.cmc.websocket.configuration;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.cmc.common.configuration.BaseConfiguration;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * netty服务器
 */
@Component
@Slf4j
public class NettyServer implements CommandLineRunner {

    @Override
    public void run(String... args) {

        int port = BaseConfiguration.port + 1; // WebSocket端口

        ThreadUtil.execute(() -> start(port));

        log.info("WebSocket 启动完成：" + BaseConfiguration.adminProperties.getSocketAddress() + ":" + port);
    }

    @SneakyThrows
    public void start(int port) {
        EventLoopGroup childGroup = new NioEventLoopGroup();
        EventLoopGroup parentGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.option(ChannelOption.SO_BACKLOG, 1024);
            serverBootstrap.group(parentGroup, childGroup) // 绑定线程池
                .channel(NioServerSocketChannel.class) // 指定使用的channel
                .localAddress(port) // 绑定监听端口
                .childHandler(new ChannelInitializer<SocketChannel>() { // 绑定客户端连接时候触发操作
                    @Override
                    protected void initChannel(SocketChannel ch) { // 绑定客户端连接时候触发操作
                        // websocket协议本身是基于http协议的，所以这边也要使用http解编码器
                        ch.pipeline().addLast(new HttpServerCodec());
                        // 以块的方式来写的处理器
                        ch.pipeline().addLast(new ChunkedWriteHandler());
                        ch.pipeline().addLast(new HttpObjectAggregator(8192));
                        ch.pipeline().addLast(SpringUtil.getBean(MyNettyWebSocketHandler.class));
                        ch.pipeline().addLast(new WebSocketServerProtocolHandler("/ws", null, true, 65536 * 10));
                    }
                });
            ChannelFuture channelFuture = serverBootstrap.bind().sync(); // 服务器同步创建绑定
            channelFuture.channel().closeFuture().sync(); // 关闭服务器通道
        } finally {
            parentGroup.shutdownGracefully().sync(); // 释放线程池资源
            childGroup.shutdownGracefully().sync();
        }
    }

}
