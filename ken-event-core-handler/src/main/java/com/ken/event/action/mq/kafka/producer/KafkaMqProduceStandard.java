package com.ken.event.action.mq.kafka.producer;

import com.ken.event.standard.entity.KenMessage;
import com.ken.event.standard.inter.MqProducerStandard;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.util.SerializationUtils;

import java.util.Set;

/**
 * 卡夫卡底层实现的发送消息的方法
 * description:
 * author: Ken
 * 公众号：Java架构栈
 */
@Slf4j
public class KafkaMqProduceStandard implements MqProducerStandard {

    @Autowired
    private KafkaTemplate kafkaTemplate;

    @Autowired
    private AdminClient adminClient;

    @Override
    public void sendMessage2MQ(KenMessage qphoneMessage) {
        log.info("[send msg] - 事件总线发送消息，基于Kafka - {}", qphoneMessage);

        //判断当前的事件类型 在kafka中是否存在对应的Topic
        //获取所有的topic
        ListTopicsResult topics = adminClient.listTopics();
        try {
            Set<String> topicNames = topics.names().get();

            //判断topic名称的集合中是否存在当前的事件类型
            if (!topicNames.contains(qphoneMessage.getEventType())) return;

            //将消息对象序列化成byte数组
            byte[] bytes = SerializationUtils.serialize(qphoneMessage);
            kafkaTemplate.send(qphoneMessage.getEventType(), bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
