package com.ken.event.action.mq.rabbitmq.config;

import com.ken.event.action.apply.consumer.IKenEventHandler;
import com.ken.event.action.apply.consumer.KenEvent;
import com.ken.event.action.mq.rabbitmq.consumer.RabbitMqConsumerListener;
import com.ken.event.action.mq.rabbitmq.producer.RabbitMqProducerHandler;
import com.ken.event.commons.base.Constants;
import com.ken.event.commons.utils.ApplicationUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 事件总线中 在rabbitmq的配置类
 * author Ken
 * create_time 2022/9/17
 */
@Configuration
@Slf4j
public class KenRabbitMQConfiguration {

    /**
     * 生产者的配置
     */
    @Configuration
    public static class RabbitMQProducerConfiguration{

        {
            log.debug("[RabbitMQ Module Loader] RabbitMQ的生产端的配置加载！");
        }

        /**
         * 创建一个普通的事件交换机
         * @return
         */
        @Bean
        public DirectExchange getEventExchange(){
            return new DirectExchange(Constants.RABBITMQ_NORMAL_EXCHANGE_NAME, true, false);
        }

        /**
         * 生产端 - RabbitMQ的处理实现类
         * @return
         */
        @Bean
        public RabbitMqProducerHandler getRabbitMqProducerHandler(){
            return new RabbitMqProducerHandler();
        }
    }

    /**
     * 消费者的配置
     * @ConditionalOnBean(IKenEventHandler.class)
     *   - 如果当前IOC容器中 有一个类型的Bean是IKenEventHandler类型的话，当前这个配置类就会被Spring加载
     */
    @Configuration
    @ConditionalOnBean(IKenEventHandler.class)
    public static class RabbitMQConsumerConfiguration{

        {
            log.debug("[RabbitMQ Module Loader] RabbitMQ的消费端的配置加载！");
        }

        /**
         * 获取微服务的名称
         */
        @Value("${spring.application.name}")
        public String appName;

        /**
         * 获取消费端的 自定义的监听器集合
         */
        @Autowired
        public List<IKenEventHandler> eventHandlers;

        /**
         * RabbitMQ的默认消费者对象
         * @return
         */
        @Bean
        public RabbitMqConsumerListener getRabbitMqConsumerListener(){
            return new RabbitMqConsumerListener();
        }

        /**
         * 创建一个消费端的队列
         * @return
         */
        @Bean
        public Queue getEventQueue(){
            return new Queue(Constants.RABBITMQ_QUEUE_PREFIX + appName, true, false, false);
        }

        /**
         * 将队列和交换机进行绑定
         * @return
         */
        @Bean
        public Binding getEventBinding(DirectExchange getEventExchange, Queue getEventQueue){
            //循环所有的消费者监听器
            for(IKenEventHandler eventHandler : eventHandlers){
                //通过反射获取监听器对象上的注解
                KenEvent kenEvent = eventHandler.getClass().getAnnotation(KenEvent.class);
                if (kenEvent == null)
                    throw new RuntimeException("Bean of type IKenEventHandler, you must add KenEvent annotation!");

                //循环获取所有的消费者关心的事件类型
                Binding binding = BindingBuilder
                        .bind(getEventQueue)
                        .to(getEventExchange)
                        .with(kenEvent.value());

                //手动将Bean注册到IOC容器中
                ApplicationUtils.registerBean(binding.getClass().getName() + binding.hashCode(), binding);
            }

            return null;
        }
    }
}
