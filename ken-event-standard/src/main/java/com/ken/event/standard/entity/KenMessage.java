package com.ken.event.standard.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 封装的消息对象 - 构造器模式
 * author Ken
 * create_time 2022/9/17
 */
//@Getter
@Data
public class KenMessage<T> implements Serializable {

    //私有化构造方法，不允许外界直接new对象
    private KenMessage(){}

    //消息的唯一标识
    private String id;

    //消息类型 0-迅捷消息 1-可靠性消息 2-延迟消息
    private Integer msgType;

    //延迟的时间 单位ms
    private Long delayTime;

    //发送时间
    private Date sendTime;

    //接收时间
    private Date recvTime;

    //事件类型 - 开发者定义
    private String eventType;

    //事件内容
    private T msg;

    //额外的属性
    private Map<String, Object> attr;

    public KenMessage addAttr(String key, Object value) {
        this.attr.put(key, value);
        return this;
    }

    /**
     * 创建一个构建器类
     */
    public static class Builder<T>{
        //消息的唯一标识
        private String id;

        //消息类型 0-迅捷消息 1-可靠性消息 2-延迟消息
        private Integer msgType;

        //延迟的时间 单位ms
        private Long delayTime;

        //发送时间
        private Date sendTime;

        //接收时间
        private Date recvTime;

        //事件类型 - 开发者定义
        private String eventType;

        //事件内容
        private T msg;

        //额外的属性
        private Map<String, Object> attr;


        /**
         * 构建器设置一个构造方法
         */
        public Builder(String eventType, T msg){
            this.eventType = eventType;
            this.msg = msg;
            this.attr = new HashMap<>();
        }

        public Builder setId(String id) {
            this.id = id;
            return this;
        }

        public Builder setMsgType(Integer msgType) {
            this.msgType = msgType;
            return this;
        }

        public Builder setDelayTime(Long delayTime) {
            this.delayTime = delayTime;
            return this;
        }

        public Builder setSendTime(Date sendTime) {
            this.sendTime = sendTime;
            return this;
        }

        public Builder setRecvTime(Date recvTime) {
            this.recvTime = recvTime;
            return this;
        }

        public Builder addAttr(String key, Object value) {
            this.attr.put(key, value);
            return this;
        }

        public KenMessage build(){
            KenMessage kenMessage = new KenMessage();
            kenMessage.setId(id);
            kenMessage.setMsgType(msgType);
            kenMessage.setSendTime(sendTime);
            kenMessage.setRecvTime(recvTime);
            kenMessage.setAttr(attr);
            kenMessage.setEventType(eventType);
            kenMessage.setMsg(msg);
            kenMessage.setDelayTime(delayTime);
            return kenMessage;
        }
    }
}
