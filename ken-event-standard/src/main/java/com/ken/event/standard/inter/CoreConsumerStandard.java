package com.ken.event.standard.inter;

import com.ken.event.standard.entity.KenMessage;

/**
 * 核心服务层 - 消费端的处理接口
 * author Ken
 * create_time 2022/9/17
 */
public interface CoreConsumerStandard {

    void msgHandler(KenMessage kenMessage);
}
