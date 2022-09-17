package com.ken.event.action.apply.consumer;

import com.ken.event.standard.entity.KenMessage;

/**
 * author Ken
 * create_time 2022/9/17
 */
public interface IKenEventHandler<T> {

    void eventHandler(T data, KenMessage kenMessage);
}
