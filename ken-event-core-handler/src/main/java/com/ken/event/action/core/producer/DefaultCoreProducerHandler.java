package com.ken.event.action.core.producer;

import com.ken.event.standard.entity.KenMessage;
import com.ken.event.standard.inter.CoreProducerStandard;
import com.ken.event.standard.inter.MqProducerStandard;
import com.ken.event.standard.interceptor.KenProducerInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 默认的核心服务层实现类
 * author Ken
 * create_time 2022/9/17
 */
@Slf4j
public class DefaultCoreProducerHandler implements CoreProducerStandard {

    @Autowired(required = false)
    private List<KenProducerInterceptor> producerInterceptors;

    @Autowired
    private MqProducerStandard mqProducerStandard;

    @Override
    public void sendMessage(KenMessage kenMessage) {
        log.debug("[event msg] 核心服务端接收到发送消息的请求 - {}", kenMessage);
        //调用生产端的拦截器链
        if (producerInterceptors != null) {
            //循环拦截器链进行相关处理
            for (KenProducerInterceptor producerInterceptor : producerInterceptors) {
                //异常处理 -- 一旦某个拦截器出现问题后，会自动跳过，接着执行下一个拦截器
                try {
                    //判断当前拦截器是否需要处理当前消息
                    if (!producerInterceptor.isSupport(kenMessage)) continue;

                    //当前拦截器需要处理当前的消息
                    kenMessage = producerInterceptor.interceptor(kenMessage);
                    //意味着消息终止发送
                    if (kenMessage == null) {
                        log.debug("[event msg] 当前消息终止发送！");
                        // 终止发送后 可以给开发者一个回调
                        return;
                    }
                } catch (Throwable t) {
                    log.error("[event msg interceptor] 拦截器发生异常！", t);
                }
            }
        }

        //调用MQ层的接口 将消息发送出去
        mqProducerStandard.sendMessage2MQ(kenMessage);
    }
}
