package com.ken.event.action.mq.kafka.interceptor;

import com.ken.event.standard.entity.KenMessage;
import com.ken.event.standard.inter.CoreProducerStandard;
import com.ken.event.standard.interceptor.KenProducerInterceptor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * 延迟消息的发送端拦截器
 * @author Ken
 * @create_time 2023/3/29
 */
@Component
@Slf4j
public class DelayMessageProducerInterceptor implements KenProducerInterceptor {

    /**
     * 创建一个延迟队列
     */
    private DelayQueue<MyDelayTask> delayQueue = new DelayQueue();

    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    //再次获取核心层对象
    @Autowired
    private CoreProducerStandard coreProducerStandard;

    @PostConstruct
    public void init(){
        log.info("[delay queue]延迟消息拦截器加载....");
        log.info("[delay queue]获取Spring默认线程池，处理延迟队列... {}", threadPoolTaskExecutor);
        threadPoolTaskExecutor.submit(() -> {
            while(true) {
                try {
                    MyDelayTask take = delayQueue.take();
                    log.debug("[delay queue] 获取到延迟任务并处理... {}", take);

                    KenMessage kenMessage = take.getKenMessage();
                    kenMessage.addAttr("delay", true);//设置延迟标记
                    //将延迟消息再次发送
                    coreProducerStandard.sendMessage(kenMessage);
                } catch (InterruptedException e) {
                    log.error("[delay queue] 处理延迟任务异常！", e);
                }
            }
        });
    }

    @Override
    public boolean isSupport(KenMessage kenMessage) {
        //适用于延迟消息
        return kenMessage.getMsgType() == 2 && !kenMessage.getAttr().containsKey("delay");
    }

    @Override
    public KenMessage interceptor(KenMessage kenMessage) {
        log.info("[delay queue]经过了延迟拦截器处理....");
        //获取延迟时间
        Long delayTime = kenMessage.getDelayTime();
        //如果延迟时间大于0 则立刻发送该消息
        if (delayTime <= 0) return kenMessage;
        //放入本地延迟队列
        delayQueue.add(new MyDelayTask(kenMessage));
        return null;
    }

    @Data
    private static class MyDelayTask implements Delayed {

        private KenMessage kenMessage;

        //到期的时间
        private long ttl;

        public MyDelayTask(KenMessage kenMessage) {
            this.kenMessage = kenMessage;
            //计算到期的时间
            this.ttl = System.currentTimeMillis() + kenMessage.getDelayTime();
        }

        /**
         * 到期时间，该方法返回 大于0表示未到期 小于0表示已经到期
         * @param unit the time unit
         * @return
         */
        @Override
        public long getDelay(TimeUnit unit) {
            //设置过期时间
            return ttl - System.currentTimeMillis();
        }

        @Override
        public int compareTo(Delayed o) {
            return (int) (ttl - ((MyDelayTask)o).getTtl());
        }
    }
}
