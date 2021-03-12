package com.zyb.pulsar.client;

import com.zyb.pulsar.collector.PulsarCollector;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.MessageId;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClientException;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * javadoc PulsarProducerTemplate
 * <p>
 *
 * <p>
 *
 * @author zhang yebai
 * @version 1.0.0
 * @date 2021/3/10 6:43 PM
 **/
public class PulsarProducerTemplate {

    protected final Log log = LogFactory.getLog(this.getClass());

    private final PulsarCollector collector;

    public PulsarProducerTemplate(PulsarCollector collector){
        this.collector = collector;
    }
    /**
     * javadoc send
     * @apiNote 同步发送pulsar消息
     *
     * @param topic 发送消息的主题
     * @param message 消息体
     * @return java.util.Optional<org.apache.pulsar.client.api.MessageId>
     * @author zhang yebai
     * @date 2021/3/10 6:59 PM
     **/
    public <T> Optional<MessageId> send(String topic, T message) {
        Optional<Producer<T>> opt = collector.producer(topic);
        if (!opt.isPresent()) {
            collector.newProducer(topic, message);
            opt = collector.producer(topic);
        }
        final MessageId id = opt
                .map(p -> {
                    try {
                        return p.send(message);
                    } catch (PulsarClientException ex) {
                        log.error("[" + p + "].send(" + message + ") exception: " + ex);
                        throw new RuntimeException(ex);
                    }
                })
                .orElse(null);
        return Optional.ofNullable(id);
    }

    /**
     * javadoc asyncSend
     * @apiNote 异步发送pulsar消息
     *
     * @param topic 发送消息主题
     * @param message 消息内容
     * @return java.util.Optional<java.util.concurrent.CompletableFuture<org.apache.pulsar.client.api.MessageId>>
     * @author zhang yebai
     * @date 2021/3/10 6:59 PM
     **/
    public <T> Optional<CompletableFuture<MessageId>> asyncSend(String topic, T message) {
        Optional<Producer<T>> opt = collector.producer(topic);
        if (!opt.isPresent()) {
            collector.newProducer(topic, message);
            opt = collector.producer(topic);
        }
        final CompletableFuture<MessageId> fid = opt
                .map(p -> p.sendAsync(message))
                .orElse(null);
        return Optional.ofNullable(fid);
    }

    public List<Consumer<?>> consumers(){
        return collector.consumers();
    }


    public Map<String, Producer<?>> producers(){
        return collector.producers();
    }
}
