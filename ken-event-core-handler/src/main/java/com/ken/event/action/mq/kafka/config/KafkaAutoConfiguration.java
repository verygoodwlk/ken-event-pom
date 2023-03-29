package com.ken.event.action.mq.kafka.config;

import com.ken.event.action.apply.consumer.IKenEventHandler;
import com.ken.event.action.apply.consumer.KenEvent;
import com.ken.event.action.mq.kafka.consumer.KafkaConsumerListener;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.kafka.DefaultKafkaConsumerFactoryCustomizer;
import org.springframework.boot.autoconfigure.kafka.DefaultKafkaProducerFactoryCustomizer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.LoggingProducerListener;
import org.springframework.kafka.support.ProducerListener;
import org.springframework.kafka.support.converter.RecordMessageConverter;

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
@ComponentScan("com.ken.event.action.mq.kafka")
public class KafkaAutoConfiguration {

    @Autowired
    private KafkaProperties kafkaProperties;

    @PostConstruct
    public void init(){
        log.info("[init serializer] 初始化序列化器....");
        kafkaProperties.getProducer().setKeySerializer(ByteArraySerializer.class);
        kafkaProperties.getProducer().setValueSerializer(ByteArraySerializer.class);
        kafkaProperties.getConsumer().setKeyDeserializer(ByteArrayDeserializer.class);
        kafkaProperties.getConsumer().setValueDeserializer(ByteArrayDeserializer.class);
    }

    @Bean
    public KafkaTemplate<?, ?> kafkaTemplate(ProducerFactory<Object, Object> kafkaProducerFactory,
                                             ProducerListener<Object, Object> kafkaProducerListener,
                                             ObjectProvider<RecordMessageConverter> messageConverter) {
        KafkaTemplate<Object, Object> kafkaTemplate = new KafkaTemplate<>(kafkaProducerFactory);
        messageConverter.ifUnique(kafkaTemplate::setMessageConverter);
        kafkaTemplate.setProducerListener(kafkaProducerListener);
        kafkaTemplate.setDefaultTopic(this.kafkaProperties.getTemplate().getDefaultTopic());
        return kafkaTemplate;
    }

    @Bean
    public ProducerListener<Object, Object> kafkaProducerListener() {
        return new LoggingProducerListener<>();
    }

    @Bean
    public ConsumerFactory<?, ?> kafkaConsumerFactory(
            ObjectProvider<DefaultKafkaConsumerFactoryCustomizer> customizers) {
        DefaultKafkaConsumerFactory<Object, Object> factory = new DefaultKafkaConsumerFactory<>(
                this.kafkaProperties.buildConsumerProperties());
        customizers.orderedStream().forEach((customizer) -> customizer.customize(factory));
        return factory;
    }

    @Bean
    public ProducerFactory<?, ?> kafkaProducerFactory(
            ObjectProvider<DefaultKafkaProducerFactoryCustomizer> customizers) {
        DefaultKafkaProducerFactory<?, ?> factory = new DefaultKafkaProducerFactory<>(
                this.kafkaProperties.buildProducerProperties());
        String transactionIdPrefix = this.kafkaProperties.getProducer().getTransactionIdPrefix();
        if (transactionIdPrefix != null) {
            factory.setTransactionIdPrefix(transactionIdPrefix);
        }
        customizers.orderedStream().forEach((customizer) -> customizer.customize(factory));
        return factory;
    }

    /**
     * 注册Kafka的相关工具对象（动态操作主题）
     * @param kafkaProperties kafka的基本属性对象
     * @return kafka客户端对象
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
         * 
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
         * @return 消费端关注的事件类型列表
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
         * @return kafka消费者监听器对象
         */
        @Bean
        public KafkaConsumerListener msgListener(){
            return new KafkaConsumerListener();
        }
    }
}
