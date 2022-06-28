package com.hbc.common.kafka.config;

import static com.hbc.common.kafka.config.util.CommonKafkaUtil.flattenAndReplace;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

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
