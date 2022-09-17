package com.ken.event.standard.inter;

import com.ken.event.standard.entity.KenMessage;

/**
 * 生产端的核心服务层规范接口
 * author Ken
 * create_time 2022/9/17
 */
public interface CoreProducerStandard {

    void sendMessage(KenMessage kenMessage);
}
