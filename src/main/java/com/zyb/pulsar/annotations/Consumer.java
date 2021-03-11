package com.zyb.pulsar.annotations;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.UUID;

/**
 * javadoc Consumer
 * <p>
 *      pulsar message listener
 * <p>
 * @author zhang yebai
 * @date 2021/3/10 4:18 PM
 * @version 1.0.0
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Consumer {

    /**
     * 监听主题
     **/
    String[] topics();

    /**
     * 消费者名称
     **/
    String name() default "";

    /**
     * 消费者描述
     **/
    String description() default "";

    /**
     * 是否自动确认
     **/
    boolean autoAck() default true;

    /**
     * 抛出异常
     **/
    boolean onException() default true;

    /**
     * 队列监听类型
     * 0: Exclusive
     * 1: Shared
     * 2: Failover
     * 3: Key_Shared
     **/
    int sharedType() default 1;

    /**
     * 消息类型
     **/
    Class<?> type() default byte[].class;
}
