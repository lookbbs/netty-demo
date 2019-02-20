package com.ydf.netty.channel.handler;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yuandongfei
 * @date 2019/2/20
 */
@Slf4j
public class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private final String wsUri;

    public HttpRequestHandler(String wsUri) {
        this.wsUri = wsUri;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        log.info(">>> netty service 接收到请求：{}", request);
        if (wsUri.equalsIgnoreCase(request.uri())) {
            //如果请求了WebSocket协议升级，则增加引用计数（调用retain()方 法 ），并将它传递给下一个ChannelInboundHandler
            ctx.fireChannelRead(request.retain());
        } else {
            /**
             * 100 Continue
             * 是这样的一种情况：HTTP客户端程序有一个实体的主体部分要发送给服务器，
             * 但希望在发送之前查看下服务器是否会接受这个实体，
             * 所以在发送实体之前先发送了一个携带100
             * Continue的Expect请求首部的请求。
             * 服务器在收到这样的请求后，应该用 100 Continue或一条错误码来进行响应。
             */
            if (HttpUtil.is100ContinueExpected(request)) {
                //处理100 Continue请求以符合HTTP1.1规范
                send100Continue(ctx);
            }
            HttpResponse response = new DefaultHttpResponse(request.protocolVersion(), HttpResponseStatus.OK);
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain;charset=UTF-8");
            boolean keepAlive = HttpUtil.isKeepAlive(request);
            if (keepAlive) {
                //如果请求了keep-alive，则添加所需要的HTTP头信息
                response.headers().set(HttpHeaderNames.CONTENT_LENGTH, 1024);
                response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
            }
            ctx.write(response);

            // 写LastHttpContent并冲刷至客户端
            ChannelFuture future = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
            if (!keepAlive) {
                //如果没有请求keep-alive，则在写操作完成后关闭Channel
                future.addListener(ChannelFutureListener.CLOSE);
            }
        }
    }

    private void send100Continue(ChannelHandlerContext ctx) {
        DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE);
        ctx.writeAndFlush(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        ctx.close();
    }
}
