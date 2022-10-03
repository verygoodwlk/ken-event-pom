package com.ken.event.commons.base;

/**
 * 常量接口
 * author Ken
 * create_time 2022/9/17
 */
public interface Constants {

    /**
     * rabbitmq的事件交换机的名称
     */
    String RABBITMQ_NORMAL_EXCHANGE_NAME = "event-exchange";

    String RABBITMQ_DELAY_EXCHANGE_NAME = "delay-exchange";

    /**
     * 队列的前缀名称
     */
    String RABBITMQ_QUEUE_PREFIX = "event-queue-";
}
