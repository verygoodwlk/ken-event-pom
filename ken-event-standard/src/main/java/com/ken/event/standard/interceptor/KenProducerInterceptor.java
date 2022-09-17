package com.ken.event.standard.interceptor;

import com.ken.event.standard.entity.KenMessage;

/**
 * 拦截器的接口
 * author Ken
 * create_time 2022/9/17
 */
public interface KenProducerInterceptor {

    /**
     * 判断当前的拦截器 是否需要对当前消息进行拦截
     * @param kenMessage
     * @return 如果返回true，则拦截器的interceptor 就会触发，如果false则跳过当前拦截器
     */
    boolean isSupport(KenMessage kenMessage);

    /**
     * 发送端拦截方法
     * @param kenMessage 需要处理的消息对象
     * @return 处理过的消息对象 如果返回null, 则表示中断当前消息的发送
     */
    KenMessage interceptor(KenMessage kenMessage);
}
