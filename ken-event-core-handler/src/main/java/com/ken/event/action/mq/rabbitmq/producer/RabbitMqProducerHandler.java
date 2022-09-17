package com.ken.event.action.mq.rabbitmq.producer;

import com.ken.event.commons.base.Constants;
import com.ken.event.standard.entity.KenMessage;
import com.ken.event.standard.inter.MqProducerStandard;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.SerializationUtils;

/**
 * author Ken
 * create_time 2022/9/17
 */
public class RabbitMqProducerHandler implements MqProducerStandard {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 发送消息到RabbitMQ服务
     * @param kenMessage
     */
    @Override
    public void sendMessage2MQ(KenMessage kenMessage) {

        MessageProperties messageProperties = new MessageProperties();
        //设置消息的持久化
        //判断消息类型
        //如果是迅捷消息 消息就不持久化
        //如果是非迅捷消息 消息就持久化
        messageProperties.setDeliveryMode(kenMessage.getMsgType() == 0 ? MessageDeliveryMode.NON_PERSISTENT : MessageDeliveryMode.PERSISTENT);

        //将发送的内容序列化
        byte[] body = SerializationUtils.serialize(kenMessage);
        //封装成RabbitMQ的消息对象
        Message message = new Message(body, messageProperties);

        if (kenMessage.getMsgType() == 2){
            //延迟消息
        } else {
            //发送到指定交换机
            rabbitTemplate.send(Constants.RABBITMQ_NORMAL_EXCHANGE_NAME,
                    kenMessage.getEventType(),
                    message);
        }
    }
}
