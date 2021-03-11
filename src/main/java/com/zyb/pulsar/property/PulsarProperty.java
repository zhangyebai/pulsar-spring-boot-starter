package com.zyb.pulsar.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.UUID;

/**
 * javadoc PulsarProperty
 * <p>
 *     pulsar client property
 * <p>
 * @author zhang yebai
 * @date 2021/3/10 4:01 PM
 * @version 1.0.0
 **/
@ConfigurationProperties(prefix = "spring.pulsar")
public class PulsarProperty {

    /**
     * pulsar地址:
     *      - pulsar://localhost:6650
     *      - pulsar+ssl://localhost:6651
     **/
    private String serviceUrl;

    /**
     * pulsar客户端连接名
     **/
    private String listenerName = UUID.randomUUID().toString();

    /**
     * 客户端io线程数
     **/
    private int ioThreads = 1;

    /**
     * 客户端消息监听线程数
     **/
    private int listenerThreads = 1;

    /**
     * tcp no delay
     **/
    private boolean enableTcpNoDelay = true;

    /**
     * 连接超时时间, 5秒
     **/
    private int connectionTimeout = 5000;

    /**
     * million second unit
     **/
    private int keepAliveInterval = 30000;

    /**
     * million second unit
     **/
    private int operationTimeout = 30000;

    public String getServiceUrl() {
        return serviceUrl;
    }

    public PulsarProperty setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
        return this;
    }

    public String getListenerName() {
        return listenerName;
    }

    public PulsarProperty setListenerName(String listenerName) {
        this.listenerName = listenerName;
        return this;
    }

    public int getIoThreads() {
        return ioThreads;
    }

    public PulsarProperty setIoThreads(int ioThreads) {
        this.ioThreads = ioThreads;
        return this;
    }

    public int getListenerThreads() {
        return listenerThreads;
    }

    public PulsarProperty setListenerThreads(int listenerThreads) {
        this.listenerThreads = listenerThreads;
        return this;
    }

    public boolean isEnableTcpNoDelay() {
        return enableTcpNoDelay;
    }

    public PulsarProperty setEnableTcpNoDelay(boolean enableTcpNoDelay) {
        this.enableTcpNoDelay = enableTcpNoDelay;
        return this;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public PulsarProperty setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
        return this;
    }

    public int getKeepAliveInterval() {
        return keepAliveInterval;
    }

    public PulsarProperty setKeepAliveInterval(int keepAliveInterval) {
        this.keepAliveInterval = keepAliveInterval;
        return this;
    }

    public int getOperationTimeout() {
        return operationTimeout;
    }

    public PulsarProperty setOperationTimeout(int operationTimeout) {
        this.operationTimeout = operationTimeout;
        return this;
    }

    @Override
    public String toString() {
        return "PulsarProperty{" +
                "serviceUrl='" + serviceUrl + '\'' +
                ", listenerName='" + listenerName + '\'' +
                ", ioThreads=" + ioThreads +
                ", listenerThreads=" + listenerThreads +
                ", enableTcpNoDelay=" + enableTcpNoDelay +
                ", connectionTimeout=" + connectionTimeout +
                ", keepAliveInterval=" + keepAliveInterval +
                ", operationTimeout=" + operationTimeout +
                '}';
    }
}
