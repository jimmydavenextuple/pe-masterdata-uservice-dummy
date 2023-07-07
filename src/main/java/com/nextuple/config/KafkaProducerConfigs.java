package com.nextuple.config;

import com.nextuple.core.event.LocalCacheUpdateEvent;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
@EnableKafka
@RequiredArgsConstructor
public class KafkaProducerConfigs {
  private final KafkaProperties kafkaProperties;
  private static final String KEYSERIALIZER = "key.serializer";
  private static final String INTERCEPTORCLASSES = "interceptor.classes";
  private static final String VALUESERIALIZER = "value.serializer";

  @Value(value = "${spring.kafka.bootstrap-servers}")
  private String bootstrapServers;

  @Bean("jsonSerializerProperties")
  @ConfigurationProperties(prefix = "spring.kafka.producer")
  public Map<String, Object> localCacheUpdateEventProperties() {
    return this.kafkaProperties.buildProducerProperties();
  }

  private Map<String, Object> getStringObjectMap() {
    Map<String, Object> prop = new HashMap<>(localCacheUpdateEventProperties());
    prop.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, prop.get(KEYSERIALIZER));
    prop.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, prop.get(VALUESERIALIZER));
    prop.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

    prop.put(ProducerConfig.ACKS_CONFIG, prop.get("acks").toString());

    prop.put(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG, prop.get(INTERCEPTORCLASSES));
    return prop;
  }

  @Primary
  @Bean
  public KafkaTemplate<String, LocalCacheUpdateEvent> cacheUpdateKafkaTemplate() {
    Map<String, Object> prop = getStringObjectMap();
    return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(prop));
  }
}
