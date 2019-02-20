package com.ydf.netty;

import com.ydf.netty.channel.handler.HttpRequestHandler;
import com.ydf.netty.channel.handler.PongWebSocketFrameHandler;
import com.ydf.netty.channel.handler.TextWebSocketFrameHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * 初始化ChannelPipeline
 *
 * @author yuandongfei
 * @date 2019/2/15
 */
public class NettyServerInitializer extends ChannelInitializer<Channel> {

    private final ChannelGroup group;
    private final Integer readerIdleTimeSeconds;
    private final Integer writerIdleTimeSeconds;
    private final Integer allIdleTimeSeconds;

    public NettyServerInitializer(ChannelGroup group, Integer readerIdleTimeSeconds, Integer writerIdleTimeSeconds, Integer allIdleTimeSeconds) {
        this.group = group;
        this.readerIdleTimeSeconds = readerIdleTimeSeconds;
        this.writerIdleTimeSeconds = writerIdleTimeSeconds;
        this.allIdleTimeSeconds = allIdleTimeSeconds;
    }

    @Override
    protected void initChannel(Channel channel) {
        // HttpServerCodec：将请求和应答消息解码为HTTP消息
        channel.pipeline().addLast(new HttpServerCodec());
        // HttpObjectAggregator：将HTTP消息的多个部分合成一条完整的HTTP消息
        // Http对象聚合器，，参数：消息的最大长度
        // 几乎在Netty中的编程都会使用到这个handler
        channel.pipeline().addLast(new HttpObjectAggregator(1024 * 64));
        // ChunkedWriteHandler：向客户端发送HTML5文件
        channel.pipeline().addLast(new ChunkedWriteHandler());
        // 处理那些不发送到 /ws URI的请求
        channel.pipeline().addLast(new HttpRequestHandler("/ws"));
        // 如果被请求的端点是 "/ws",则处理该升级握手
        channel.pipeline().addLast(new WebSocketServerProtocolHandler("/ws"));
        // 在管道中添加我们自己的接收数据实现方法
        channel.pipeline().addLast(new TextWebSocketFrameHandler(group));
        channel.pipeline().addLast(new PongWebSocketFrameHandler());
        channel.pipeline().addLast(new IdleStateHandler(readerIdleTimeSeconds, writerIdleTimeSeconds, allIdleTimeSeconds));
    }
}
