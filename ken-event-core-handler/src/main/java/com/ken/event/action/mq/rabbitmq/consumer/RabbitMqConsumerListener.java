package com.ken.event.action.mq.rabbitmq.consumer;

import com.ken.event.commons.base.Constants;
import com.ken.event.standard.entity.KenMessage;
import com.ken.event.standard.inter.CoreConsumerStandard;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.SerializationUtils;

/**
 * RabbitMQ消费者的监听器
 * author Ken
 * create_time 2022/9/17
 */
public class RabbitMqConsumerListener {

    @Autowired
    private CoreConsumerStandard coreConsumerStandard;

    /**
     * 监听队列的回调方法
     * @param message 消息对象
     */
    @RabbitListener(queues = Constants.RABBITMQ_QUEUE_PREFIX + "${spring.application.name}")
    public void handler(Message message){
        //获得RabbitMQ的Message对象中的结果
        byte[] body = message.getBody();
        //将字节数组反序列化成KenMessage
        KenMessage kenMessage = (KenMessage) SerializationUtils.deserialize(body);
        //调用核心服务层 实现消息的处理
        coreConsumerStandard.msgHandler(kenMessage);
    }
}
