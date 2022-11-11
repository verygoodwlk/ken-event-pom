package com.ken.event.action.mq.kafka.consumer;

import com.ken.event.standard.entity.KenMessage;
import com.ken.event.standard.inter.CoreConsumerStandard;
import org.springframework.util.SerializationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;

/**
 * description:
 * author: Ken
 * 公众号：Java架构栈
 */
public class KafkaConsumerListener {

    @Autowired
    private CoreConsumerStandard consumerStandrad;

    @KafkaListener(groupId = "group-${spring.application.name}", topics = "#{@eventTypes}")
    public void handler(byte[] msg){
        //反序列化
        KenMessage qphoneMessage = (KenMessage) SerializationUtils.deserialize(msg);
        //调用上层的接口处理对象
        consumerStandrad.msgHandler(qphoneMessage);
    }
}
