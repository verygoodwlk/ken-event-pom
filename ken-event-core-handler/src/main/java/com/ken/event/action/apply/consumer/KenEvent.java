package com.ken.event.action.apply.consumer;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * 消费者 应用注解 - 标识当前需要监听的事件类型
 * author Ken
 * create_time 2022/9/17
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface KenEvent {

    /**
     * 表示当前需要注册的事件名称
     */
    String value();
}
