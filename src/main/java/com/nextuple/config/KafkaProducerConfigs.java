package com.nextuple.config;

import com.nextuple.core.event.LocalCacheUpdateEvent;
import com.nextuple.jobs.framework.common.domain.pojo.RecordDto;
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
import org.springframework.kafka.core.ProducerFactory;

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
  public Map<String, Object> jsonSerializerProperties() {
    return this.kafkaProperties.buildProducerProperties();
  }

  @Bean("itemSerializerProperties")
  @ConfigurationProperties(prefix = "spring.kafka.producer-item")
  public Map<String, Object> itemSerializerProperties() {
    return this.kafkaProperties.buildProducerProperties();
  }

  @Bean
  public ProducerFactory<String, RecordDto> kafkaJsonProducerFactory() {
    Map<String, Object> prop = getStringObjectMap();

    return new DefaultKafkaProducerFactory<>(prop);
  }

  @Bean
  public ProducerFactory<Object, Object> kafkaJsonProducerFactoryObj() {
    Map<String, Object> prop = getStringObjectMap();

    return new DefaultKafkaProducerFactory<>(prop);
  }

  @Bean
  public ProducerFactory<String, Object> kafkaJsonProducerFactoryStr() {
    Map<String, Object> prop = getStringObjectMap();

    return new DefaultKafkaProducerFactory<>(prop);
  }

  @Bean
  public ProducerFactory<String, Object> kafkaItemProducerFactory() {
    HashMap<String, Object> prop = new HashMap<>(itemSerializerProperties());
    prop.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, prop.get(KEYSERIALIZER));
    prop.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, prop.get(VALUESERIALIZER));
    prop.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

    return new DefaultKafkaProducerFactory<>(prop);
  }

  @Bean
  public ProducerFactory<String, LocalCacheUpdateEvent> kafkaJsonProducerFactoryEventListener() {
    Map<String, Object> prop = getStringObjectMap();

    return new DefaultKafkaProducerFactory<>(prop);
  }

  private Map<String, Object> getStringObjectMap() {
    Map<String, Object> prop = new HashMap<>(jsonSerializerProperties());
    prop.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, prop.get(KEYSERIALIZER));
    prop.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, prop.get(VALUESERIALIZER));
    prop.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

    prop.put(ProducerConfig.ACKS_CONFIG, prop.get("acks").toString());

    prop.put(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG, prop.get(INTERCEPTORCLASSES));
    return prop;
  }

  @Primary
  @Bean
  public KafkaTemplate<String, LocalCacheUpdateEvent> kafkaTemplateEventListerner() {
    return new KafkaTemplate<>(kafkaJsonProducerFactoryEventListener());
  }

  @Bean(name = "JsonSerializerProducer")
  public KafkaTemplate<String, RecordDto> kafkaTemplate() {
    return new KafkaTemplate<>(kafkaJsonProducerFactory());
  }

  @Bean(name = "JsonSerializerProducerObj")
  public KafkaTemplate<Object, Object> kafkaTemplateObj() {
    return new KafkaTemplate<>(kafkaJsonProducerFactoryObj());
  }

  @Bean(name = "ItemSerializerProducer")
  public KafkaTemplate<String, Object> itemKafkaTemplate() {
    return new KafkaTemplate<>(kafkaItemProducerFactory());
  }
}
