package com.ken.event.standard.inter;

import com.ken.event.standard.entity.KenMessage;

/**
 * MQ层的消息发送规范接口
 * author Ken
 * create_time 2022/9/17
 */
public interface MqProducerStandard {

    void sendMessage2MQ(KenMessage kenMessage);
}
