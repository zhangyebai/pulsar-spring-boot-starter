package com.zyb.pulsar.collector;

import com.zyb.pulsar.producer.PulsarProducer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pulsar.client.api.*;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * javadoc PulsarCollector
 * <p>
 * pulsar consumer producer depends collector
 * <p>
 *
 * @author zhang yebai
 * @version 1.0.0
 * @date 2021/3/10 7:10 PM
 **/
@Component
public class PulsarCollector implements BeanPostProcessor {
    protected final Log log = LogFactory.getLog(this.getClass());

    private PulsarClient pulsarClient;

    @Autowired
    public void setPulsarClient(PulsarClient pulsarClient) {
        this.pulsarClient = pulsarClient;
    }

    private final Map<String, Producer<?>> producers = new ConcurrentHashMap<>();

    private final List<Consumer<?>> consumers = new ArrayList<>();

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        final Class<?> clazz = bean.getClass();
        if (clazz.isAnnotationPresent(com.zyb.pulsar.annotations.Producer.class) && bean instanceof PulsarProducer) {
            final PulsarProducer<?> p = (PulsarProducer<?>) bean;
            final Producer<?> producer = producer(p);
            if (Objects.nonNull(producers.putIfAbsent(p.getTopic(), producer))) {
                log.error("cant build multi topic[ " + p.getTopic() + " ] producer");
                throw new RuntimeException("cant build multi topic[ " + p.getTopic() + " ] producer");
            }
        }
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(com.zyb.pulsar.annotations.Consumer.class)){
                consumers.add(consumer(bean, method, method.getAnnotation(com.zyb.pulsar.annotations.Consumer.class)));
            }
        }
        return bean;
    }

    private Consumer<?> consumer(Object bean, Method method, com.zyb.pulsar.annotations.Consumer consumer) {
        try{
            final String name = "".equals(consumer.name()) ? bean.getClass().getName()+"#"+method.getName() : consumer.name();
            final String description = "".equals(consumer.description()) ? "description_" + name : consumer.description();

            return pulsarClient.newConsumer(schema(consumer.type()))
                    .consumerName(name)
                    .subscriptionName(description)
                    .topic(consumer.topics())
                    .subscriptionType(subscriptionType(consumer.sharedType()))
                    .messageListener((c, m) -> {
                        try{
                            method.setAccessible(true);
                            if(consumer.autoAck()){
                                method.invoke(bean, m.getValue());
                                c.acknowledge(m);
                            }else{
                                method.invoke(bean, c, m);
                            }
                        }catch (Exception ex){
                            log.error("Pulsar Message Listener-" + bean.getClass().getName() + "#" + method.getName() + "-handle message[" + m + "] ex: " + ex);
                            if(consumer.autoAck()){
                                c.negativeAcknowledge(m);
                            }
                            if(consumer.onException()){
                                throw new RuntimeException(ex);
                            }
                        }
                    })
                    .subscribe();
        }catch (Exception ex){
            log.error("Producer<?> consumer( " + bean.toString() + ", " + method.toString() + ", " + consumer.toString() + ") exception: " + ex);
            throw new RuntimeException(ex);
        }
    }

    private static SubscriptionType subscriptionType(int type){
        switch (type){
            case 0:
                return SubscriptionType.Exclusive;
            case 1:
                return SubscriptionType.Shared;
            case 2:
                return SubscriptionType.Failover;
            case 3:
                return SubscriptionType.Key_Shared;
            default:
                throw new RuntimeException("illegal pulsar consumer SubscriptionType[" + type + "]");
        }
    }

    private static Schema<?> schema(Class<?> clazz){
        if(byte[].class.equals(clazz)){
            return Schema.BYTES;
        }else if(ByteBuffer.class.equals(clazz)){
            return Schema.BYTEBUFFER;
        }else if(String.class.equals(clazz)){
            return Schema.STRING;
        }else if(byte.class.equals(clazz)){
            return Schema.INT8;
        }else if(Short.class.equals(clazz) || short.class.equals(clazz)){
            return Schema.INT16;
        }else if(Integer.class.equals(clazz) || int.class.equals(clazz)){
            return Schema.INT32;
        }else if(Long.class.equals(clazz) || long.class.equals(clazz)){
            return Schema.INT64;
        }else if(Boolean.class.equals(clazz) || boolean.class.equals(clazz)){
            return Schema.BOOL;
        }else if(Float.class.equals(clazz) || float.class.equals(clazz)){
            return Schema.FLOAT;
        }else if(Double.class.equals(clazz) || double.class.equals(clazz)){
            return Schema.DOUBLE;
        }else if(Date.class.equals(clazz)){
            return Schema.DATE;
        }else if(Time.class.equals(clazz)){
            return Schema.TIME;
        }else if(Timestamp.class.equals(clazz)){
            return Schema.TIMESTAMP;
        }else if(Instant.class.equals(clazz)){
            return Schema.INSTANT;
        }else if(LocalDate.class.equals(clazz)){
            return Schema.LOCAL_DATE;
        }else if(LocalTime.class.equals(clazz)){
            return Schema.LOCAL_TIME;
        }else if(LocalDateTime.class.equals(clazz)){
            return Schema.LOCAL_DATE_TIME;
        }
        return Schema.JSON(clazz);
    }

    /**
     * javadoc newProducer
     * @apiNote 新生成生产者
     *
     * @param topic 队列主题
     * @param obj 发送对象
     * @author zhang yebai
     * @date 2021/3/12 2:52 PM
     **/
    public <T> void newProducer(String topic, T obj)  {
        synchronized (this){
            final Producer<?> p =  producers.get(topic);
            if(Objects.nonNull(p)){
                return;
            }
            try{
                final Producer<?> newP = pulsarClient.newProducer(schema(obj.getClass()))
                        .topic(topic)
                        .blockIfQueueFull(true)
                        .create();
                producers.putIfAbsent(topic, newP);
            }catch (Exception ex){
                log.error("create pulsar producer[" + topic + ", " + obj + "] error: " + ex);
            }
        }
    }

    private Producer<?> producer(PulsarProducer<?> pulsarProducer) {
        try {
            return pulsarClient.newProducer(pulsarProducer.getSchema())
                    .topic(pulsarProducer.getTopic())
                    .blockIfQueueFull(pulsarProducer.getBlockIfQueueFull())
                    .create();
        } catch (Exception ex) {
            log.error("Producer<?> producer( " + pulsarProducer.toString() + " ) exception: " + ex);
            throw new RuntimeException(ex);
        }
    }

    @SuppressWarnings(value = "unchecked")
    public <T> Optional<Producer<T>> producer(String topic) {
        return Optional.ofNullable((Producer<T>) producers.get(topic));
    }

    public List<Consumer<?>> consumers(){
        return consumers;
    }

    public Map<String, Producer<?>> producers(){
        return producers;
    }
}
