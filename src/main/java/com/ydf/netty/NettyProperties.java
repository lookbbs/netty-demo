package com.ydf.netty;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * @author yuandongfei
 * @date 2019/2/15
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "netty", ignoreInvalidFields = true)
@EnableConfigurationProperties(NettyProperties.class)
public class NettyProperties {
    /**
     * netty 启动绑定的端口
     */
    private int port = 18080;

    /**
     * 读取空闲时长。单位：秒
     */
    private int readerIdleTimeSeconds = 60;

    /**
     * 写空闲时长。单位：秒
     */
    private int writerIdleTimeSeconds = 0;
    /**
     * 空闲时长。单位：秒
     */
    private int allIdleTimeSeconds = 0;
}
