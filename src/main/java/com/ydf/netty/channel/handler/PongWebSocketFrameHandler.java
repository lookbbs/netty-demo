package com.ydf.netty.channel.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yuandongfei
 * @date 2019/2/20
 */
@Slf4j
public class PongWebSocketFrameHandler extends SimpleChannelInboundHandler<PongWebSocketFrame> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, PongWebSocketFrame frame) throws Exception {
        log.info(">>> netty server 接收到消息：{}", frame);
        ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
    }
}
