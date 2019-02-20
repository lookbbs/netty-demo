package com.ydf.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.ImmediateEventExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.net.InetSocketAddress;

/**
 * @author yuandongfei
 * @date 2019/2/15
 */
@Slf4j
public class NettyServer {

    /**
     * NioEventLoopGroup是一个处理I / O操作的多线程事件循环。
     * Netty为不同类型的传输提供各种EventLoopGroup实现。
     * 我们在此示例中实现了服务器端应用程序，因此将使用两个NioEventLoopGroup。
     * 第一个，通常称为“老板”，接受传入连接。
     * 第二个，通常称为“工人”，
     * 一旦老板接受连接并将接受的连接注册到工作人员，就处理被接受连接的流量。
     * 使用了多少个线程以及它们如何映射到创建的Channels取决于EventLoopGroup实现，甚至可以通过构造函数进行配置。
     */
    private EventLoopGroup boss = new NioEventLoopGroup();
    private EventLoopGroup worker = new NioEventLoopGroup();
    private final ChannelGroup channelGroup = new DefaultChannelGroup(ImmediateEventExecutor.INSTANCE);

    @Autowired
    private NettyProperties nettyProperties;

    @PostConstruct
    public void initNetty() {
        new Thread(() -> this.start()).start();
    }

    private void start() {
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(nettyProperties.getPort()))
                    .childHandler(createInitializer(channelGroup,
                            nettyProperties.getReaderIdleTimeSeconds(),
                            nettyProperties.getWriterIdleTimeSeconds(),
                            nettyProperties.getAllIdleTimeSeconds()));
            ChannelFuture future = bootstrap.bind().sync();
            if (future.isSuccess()) {
                log.info(">>> netty 服务器端口{}启动成功。。。。", nettyProperties.getPort());
            }
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    protected ChannelInitializer<Channel> createInitializer(ChannelGroup channelGroup,
                                                            Integer readerIdleTimeSeconds,
                                                            Integer writerIdleTimeSeconds,
                                                            Integer allIdleTimeSeconds) {
        return new NettyServerInitializer(channelGroup,
                readerIdleTimeSeconds,
                writerIdleTimeSeconds,
                allIdleTimeSeconds);
    }

    @PreDestroy
    public void destroy() {
        if (null != boss && !boss.isShutdown()) {
            boss.shutdownGracefully().syncUninterruptibly();
        }
        if (null != worker && !worker.isShutdown()) {
            worker.shutdownGracefully().syncUninterruptibly();
        }
        log.info(">>> netty 关闭成功！");
    }
}
