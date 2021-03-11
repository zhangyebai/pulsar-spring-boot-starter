package com.zyb.pulsar.consumer;



import java.lang.reflect.Method;

/**
 * javadoc PulsarConsumer
 * <p>
 *     pulsar consumer
 * <p>
 * @author zhang yebai
 * @date 2021/3/10 7:08 PM
 * @version 1.0.0
 **/
public class PulsarConsumer {

    /**
     * 监听方法所在对象
     **/
    private Object object;

    /**
     * 监听方法
     **/
    private Method method;

    public Object getObject() {
        return object;
    }

    public PulsarConsumer setObject(Object object) {
        this.object = object;
        return this;
    }

    public Method getMethod() {
        return method;
    }

    public PulsarConsumer setMethod(Method method) {
        this.method = method;
        return this;
    }

    @Override
    public String toString() {
        return "PulsarConsumer{" +
                "object=" + object +
                ", method=" + method +
                '}';
    }
}
