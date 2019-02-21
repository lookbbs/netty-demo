package com.ydf.netty;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author yuandongfei
 * @date 2019/2/20
 */
@Configuration
public class NettyConfig {

    @Bean
    @ConditionalOnMissingBean(NettyServer.class)
    public NettyServer nettyServer() {
        return new NettyServer();
    }

//    @Bean
//    public NettyServer secureNettyServer() throws Exception {
//        SelfSignedCertificate cert = new SelfSignedCertificate();
//        SslContext sslContext = SslContextBuilder.forServer(cert.privateKey(), cert.certificate()).build();
//        SecureNettyServer server = new SecureNettyServer(sslContext);
//        return server;
//    }
}
