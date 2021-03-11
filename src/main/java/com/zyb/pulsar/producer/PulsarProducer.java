package com.zyb.pulsar.producer;

import com.zyb.pulsar.annotations.Producer;
import org.apache.pulsar.client.api.Schema;

/**
 * javadoc PulsarProducer
 * <p>
 *     pulsar producer config
 * <p>
 * @author zhang yebai
 * @date 2021/3/10 5:49 PM
 * @version 1.0.0
 **/
@Producer
public class PulsarProducer<T> {

    private String topic;

    private Schema<T> schema;

    private boolean blockIfQueueFull = true;

    public String getTopic() {
        return topic;
    }

    public PulsarProducer<T> setTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public Schema<T> getSchema() {
        return schema;
    }

    public PulsarProducer<T> setSchema(Schema<T> schema) {
        this.schema = schema;
        return this;
    }

    public boolean getBlockIfQueueFull() {
        return blockIfQueueFull;
    }

    public PulsarProducer<T> setBlockIfQueueFull(boolean blockIfQueueFull) {
        this.blockIfQueueFull = blockIfQueueFull;
        return this;
    }

    @Override
    public String toString() {
        return "PulsarProducer{" +
                "topic='" + topic + '\'' +
                ", schema=" + schema +
                ", blockIfQueueFull=" + blockIfQueueFull +
                '}';
    }
}
