package com.zyb.pulsar.config;

import com.zyb.pulsar.client.PulsarProducerTemplate;
import com.zyb.pulsar.collector.PulsarCollector;
import com.zyb.pulsar.property.PulsarProperty;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * javadoc PulsarAutoConfiguration
 * <p>
 *     pulsar auto configuration
 * <p>
 * @author zhang yebai
 * @date 2021/3/10 4:05 PM
 * @version 1.0.0
 **/
@Configuration
@ComponentScan(basePackages = "com.zyb.pulsar")
@EnableConfigurationProperties(PulsarProperty.class)
public class PulsarAutoConfiguration {

    private PulsarProperty pulsarProperty;
    @Autowired
    public void setPulsarProperty(PulsarProperty pulsarProperty) {
        this.pulsarProperty = pulsarProperty;
    }


    @Bean
    @ConditionalOnMissingBean
    public PulsarProducerTemplate<?> pulsarProducerTemplate(PulsarCollector pulsarCollector){
        return new PulsarProducerTemplate<>(pulsarCollector);
    }

    @Bean
    @ConditionalOnMissingBean
    public PulsarClient pulsarClient() throws PulsarClientException {
        return PulsarClient.builder()
                .serviceUrl(pulsarProperty.getServiceUrl())
                .listenerName(pulsarProperty.getListenerName())
                .ioThreads(pulsarProperty.getIoThreads())
                .listenerThreads(pulsarProperty.getListenerThreads())
                .enableTcpNoDelay(pulsarProperty.isEnableTcpNoDelay())
                .connectionTimeout(pulsarProperty.getConnectionTimeout(), TimeUnit.MILLISECONDS)
                .keepAliveInterval(pulsarProperty.getKeepAliveInterval(), TimeUnit.MILLISECONDS)
                .operationTimeout(pulsarProperty.getOperationTimeout(), TimeUnit.MILLISECONDS)
                .build();
    }
}
