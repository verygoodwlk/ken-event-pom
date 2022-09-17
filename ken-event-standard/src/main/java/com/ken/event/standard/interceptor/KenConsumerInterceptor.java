package com.ken.event.standard.interceptor;

import com.ken.event.standard.entity.KenMessage;

/**
 * 消费者拦截器
 * author Ken
 * create_time 2022/9/17
 */
public interface KenConsumerInterceptor {

    boolean isSupport(KenMessage kenMessage);

    KenMessage consumerInterceptor(KenMessage kenMessage);
}
