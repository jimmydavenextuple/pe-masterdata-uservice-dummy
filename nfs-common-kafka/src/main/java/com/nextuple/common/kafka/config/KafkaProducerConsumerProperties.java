package com.nextuple.common.kafka.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

import static com.nextuple.common.kafka.config.util.CommonKafkaUtil.flattenAndReplace;

@Configuration
@ConfigurationProperties(prefix = "kafka")
@Getter
@Setter
public class KafkaProducerConsumerProperties {
    Map<String, Object> producer;
    Map<String, Object> consumer;

    @PostConstruct
    public void setupConfig() {
        if (producer == null) {
            producer = new HashMap<>();
        }
        if (consumer == null) {
            consumer = new HashMap<>();
        }
        producer = flattenAndReplace(producer);
        consumer = flattenAndReplace(consumer);
    }
}
