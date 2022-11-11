package com.ken.event.action.mq.kafka.config;

import com.ken.event.action.apply.consumer.IKenEventHandler;
import com.ken.event.action.apply.consumer.KenEvent;
import com.ken.event.action.mq.kafka.consumer.KafkaConsumerListener;
import com.ken.event.action.mq.kafka.producer.KafkaMqProduceStandard;
import com.ken.event.standard.inter.MqProducerStandard;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * kafka的自动装配的类
 * description:
 * author: Ken
 * 公众号：Java架构栈
 */
@Configuration
@ConditionalOnClass(KafkaTemplate.class)
@Slf4j
public class KafkaAutoConfiguration {

    @Bean
    public MqProducerStandard kafkaMqProduceStandard(){
        log.debug("[Kafka Module Loader] Kafka的生产端的配置加载！");
        return new KafkaMqProduceStandard();
    }

    /**
     * 注册Kafka的相关工具对象（动态操作主题）
     * @return
     */
    @Bean
    public AdminClient getAdminClient(KafkaProperties kafkaProperties){
        return AdminClient.create(kafkaProperties.buildAdminProperties());
    }

    /**
     * 消费端的相关配置
     */
    @Configuration
    @ConditionalOnBean(IKenEventHandler.class)
    @Slf4j
    public static class ConsumerConfiguration{

        {
            log.debug("[Kafka Module Loader] Kafka的消费端的配置加载！");
        }

        @Autowired
        private List<IKenEventHandler> eventHandlers;

        @Autowired
        private AdminClient adminClient;

        /**
         * 创建动态topic
         * @return
         */
        @PostConstruct
        public void createTopic(){
            log.info("[event create topic] - 事件总线消费端创建Kafka Topic主题");
            List<NewTopic> newTopics = new ArrayList<>();
            for (IKenEventHandler eventHandler : eventHandlers) {
                KenEvent eventHandlerAnno = eventHandler.getClass().getAnnotation(KenEvent.class);
                //获取事件类型
                String eventType = eventHandlerAnno.value();
                //创建一个主题对象
                NewTopic newTopic = new NewTopic(eventType, 4, (short) 2);
                newTopics.add(newTopic);
            }

            //基于事件类型创建主题
            adminClient.createTopics(newTopics);
        }

        /**
         * 获取当前消费者订阅的所有事件类型（Topic）
         * @return
         */
        @Bean
        public String[] eventTypes(){
            List<String> eventTypes = new ArrayList<>();
            for (IKenEventHandler eventHandler : eventHandlers) {
                KenEvent eventHandlerAnno = eventHandler.getClass().getAnnotation(KenEvent.class);
                //获取事件类型
                String eventType = eventHandlerAnno.value();
                eventTypes.add(eventType);
            }
            //toArray 将集合转换成数组，参数表示指定转换的类型
            return eventTypes.toArray(new String[0]);
        }

        /**
         * 消费端的监听器
         * @return
         */
        @Bean
        public KafkaConsumerListener msgListener(){
            return new KafkaConsumerListener();
        }
    }
}
