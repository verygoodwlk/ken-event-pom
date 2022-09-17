package com.ken.event.config;

import com.ken.event.action.apply.consumer.IKenEventHandler;
import com.ken.event.action.core.consumer.DefaultCoreConsumerHandler;
import com.ken.event.action.core.producer.DefaultCoreProducerHandler;
import com.ken.event.commons.utils.ApplicationUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 事件总线的自动装配类
 * author Ken
 * create_time 2022/9/17
 */
@Configuration
public class KenEventAutoConfiguration {

    /**
     * 核心服务层 - 生产者的默认处理器
     * @return
     */
    @Bean
    public DefaultCoreProducerHandler getDefaultCoreProducerHandler(){
        return new DefaultCoreProducerHandler();
    }

    /**
     * 核心服务层 - 消费者的默认处理器
     * @return
     */
    @Bean
    @ConditionalOnBean(IKenEventHandler.class)
    public DefaultCoreConsumerHandler getDefaultCoreConsumerHandler(){
        return new DefaultCoreConsumerHandler();
    }

    @Bean
    public ApplicationUtils getApplicationUtils(){
        return new ApplicationUtils();
    }
}
