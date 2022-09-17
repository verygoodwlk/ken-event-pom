package com.ken.event.action.core.consumer;

import com.ken.event.action.apply.consumer.IKenEventHandler;
import com.ken.event.action.apply.consumer.KenEvent;
import com.ken.event.standard.entity.KenMessage;
import com.ken.event.standard.inter.CoreConsumerStandard;
import com.ken.event.standard.interceptor.KenConsumerInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * author Ken
 * create_time 2022/9/17
 */
@Slf4j
public class DefaultCoreConsumerHandler implements CoreConsumerStandard {

    /**
     * 注入消费者拦截器的集合
     */
    @Autowired(required = false)
    private List<KenConsumerInterceptor> consumerInterceptors;

    /**
     * 注入消费者的实现类
     */
    @Autowired
    private List<IKenEventHandler> kenEventHandlers;

    /**
     * 核心服务层处理消息
     * @param kenMessage
     */
    @Override
    public void msgHandler(KenMessage kenMessage) {
        //进行消费者拦截器的相关处理
        if(consumerInterceptors != null){
            for (KenConsumerInterceptor consumerInterceptor : consumerInterceptors) {
                try {
                    //依次判断拦截器是否可以执行
                    if (!consumerInterceptor.isSupport(kenMessage)) continue;

                    //调用拦截器处理消息
                    kenMessage = consumerInterceptor.consumerInterceptor(kenMessage);
                    //如果返回空，则消费端不会再消费到这条消息
                    if (kenMessage == null) return;
                } catch (Throwable t) {
                   log.error("[consumer event interceptor] 消费端拦截器异常！", t);
                }
            }
        }

        //调用应用层的代码进行消息的实际处理
        for (IKenEventHandler eventHandler : kenEventHandlers) {
            //消息的事件类型
            String eventType = kenMessage.getEventType();
            //处理器需要处理的事件类型
            KenEvent kenEvent = eventHandler.getClass().getAnnotation(KenEvent.class);
            if (kenEvent == null) continue;

            //判断事件类型
            if(eventType.equals(kenEvent.value())){
                //当前eventHandler 就是需要处理这个消息的处理器
                eventHandler.eventHandler(kenMessage.getMsg(), kenMessage);
                break;
            }
        }
    }
}
