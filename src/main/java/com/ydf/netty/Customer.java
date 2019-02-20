package com.ydf.netty;

import lombok.Data;

/**
 * @author yuandongfei
 * @date 2019/2/19
 */
@Data
public class Customer {
    /**
     * 0：心跳消息，1: 业务消息
     */
    private int type;
    private String sign;
    private String content;
}
