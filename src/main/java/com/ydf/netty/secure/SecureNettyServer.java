package com.ydf.netty.secure;

import com.ydf.netty.NettyServer;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.ssl.SslContext;

/**
 * 加密的通信服务
 * @author yuandongfei
 * @date 2019/2/20
 */
public class SecureNettyServer extends NettyServer {
    private final SslContext context;

    public SecureNettyServer(SslContext context) {
        this.context = context;
    }

    @Override
    protected ChannelInitializer<Channel> createInitializer(ChannelGroup channelGroup, Integer readerIdleTimeSeconds, Integer writerIdleTimeSeconds, Integer allIdleTimeSeconds) {
        return new SecureNettyServerInitializer(channelGroup, readerIdleTimeSeconds, writerIdleTimeSeconds, allIdleTimeSeconds, context);
    }
}
