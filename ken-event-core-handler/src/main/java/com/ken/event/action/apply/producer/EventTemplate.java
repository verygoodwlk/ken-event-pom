package com.ken.event.action.apply.producer;

import com.ken.event.commons.utils.ApplicationUtils;
import com.ken.event.standard.entity.KenMessage;
import com.ken.event.standard.inter.CoreProducerStandard;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 事件发布工具类 - 生产者调用
 * author Ken
 * create_time 2022/9/17
 */
public class EventTemplate {

    /**
     * 获得 核心服务器层生产端的操作对象 ????
     */
    private static CoreProducerStandard coreProducerStandard;

    static {
        coreProducerStandard = ApplicationUtils.getBean(CoreProducerStandard.class);
    }

    /**
     * 发送迅捷消息 - 不保证可靠性 性能最高 实时性最好
     * @param eventType 事件类型
     * @param data 消息内容
     */
    public static <T> void sendQuickly(String eventType, T data){
        send(eventType, data, 0, null);
    }

    /**
     * 发送可靠消息 - 保证消息可达 性能和实时性会略差
     * @param eventType 事件类型
     * @param data 消息内容
     */
    public static <T> void sendReliable(String eventType, T data){
        send(eventType, data, 1, null);
    }

    /**
     * 发送延迟消息
     * @param eventType 事件类型
     * @param data 消息内容
     * @param time 延迟事件
     * @param unit 时间单位
     */
    public static <T> void sendDelay(String eventType, T data, long time, TimeUnit unit){
        send(eventType, data, 2, unit.toMillis(time));
    }

    /**
     * 统一的发送方法
     */
    private static <T> void send(String eventType, T data, Integer msgType, Long delayTimeMs){
        //构建一个KenMessage对象
        KenMessage<T> kenMessage = new KenMessage.Builder<>(eventType, data)
                .setId(UUID.randomUUID().toString())//设置消息的ID号
                .setSendTime(new Date())//发送时间
                .setMsgType(msgType)//消息类型
                .setDelayTime(delayTimeMs)
                .build();

        //调用下层将当前对象发送出去
        coreProducerStandard.sendMessage(kenMessage);
    }
}
