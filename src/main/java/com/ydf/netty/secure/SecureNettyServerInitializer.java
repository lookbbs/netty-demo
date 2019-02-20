package com.ydf.netty.secure;

import com.ydf.netty.NettyServerInitializer;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;

import javax.net.ssl.SSLEngine;

/**
 * 通信加密应用
 * @author yuandongfei
 * @date 2019/2/20
 */
public class SecureNettyServerInitializer extends NettyServerInitializer {
    private final SslContext context;

    public SecureNettyServerInitializer(ChannelGroup group, Integer readerIdleTimeSeconds, Integer writerIdleTimeSeconds, Integer allIdleTimeSeconds, SslContext context) {
        super(group, readerIdleTimeSeconds, writerIdleTimeSeconds, allIdleTimeSeconds);
        this.context = context;
    }

    @Override
    protected void initChannel(Channel channel) {
        super.initChannel(channel);
        SSLEngine engine = context.newEngine(channel.alloc());
        channel.pipeline().addFirst(new SslHandler(engine));
    }
}
