package com.nextuple.masterdata.config;

import com.nextuple.core.event.LocalCacheUpdateEvent;
import com.nextuple.jobs.framework.common.domain.pojo.RecordDto;
import io.micrometer.core.instrument.MeterRegistry;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.MicrometerProducerListener;
import org.springframework.kafka.core.ProducerFactory;

@Configuration
@EnableKafka
@RequiredArgsConstructor
public class KafkaProducerConfigs {
  private final KafkaProperties kafkaProperties;
  private static final String KEYSERIALIZER = "key.serializer";
  private static final String INTERCEPTORCLASSES = "interceptor.classes";
  private static final String VALUESERIALIZER = "value.serializer";
  @Autowired private final MeterRegistry meterRegistry;

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
  public ProducerFactory<String, Object> kafkaItemProducerFactory() {
    HashMap<String, Object> prop = new HashMap<>(itemSerializerProperties());
    prop.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, prop.get(KEYSERIALIZER));
    prop.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, prop.get(VALUESERIALIZER));
    prop.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

    DefaultKafkaProducerFactory<String, Object> kafkaItemProducerFactory =
        new DefaultKafkaProducerFactory<>(prop);
    kafkaItemProducerFactory.addListener(new MicrometerProducerListener<>(meterRegistry));
    return kafkaItemProducerFactory;
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

  @Bean(name = "CacheUpdateProducer")
  public KafkaTemplate<String, LocalCacheUpdateEvent> cacheUpdateKafkaTemplate() {
    Map<String, Object> prop = getStringObjectMap();
    DefaultKafkaProducerFactory<String, LocalCacheUpdateEvent> cacheUpdateKafkaProducer =
        new DefaultKafkaProducerFactory<>(prop);
    cacheUpdateKafkaProducer.addListener(new MicrometerProducerListener<>(meterRegistry));
    return new KafkaTemplate<>(cacheUpdateKafkaProducer);
  }

  @Bean(name = "JsonSerializerProducer")
  public KafkaTemplate<String, RecordDto> jobServiceKafkaTemplate() {
    Map<String, Object> prop = getStringObjectMap();
    DefaultKafkaProducerFactory<String, RecordDto> jsonSerializerProducer =
        new DefaultKafkaProducerFactory<>(prop);
    jsonSerializerProducer.addListener(new MicrometerProducerListener<>(meterRegistry));
    return new KafkaTemplate<>(jsonSerializerProducer);
  }

  @Bean(name = "JsonSerializerProducerObj")
  public KafkaTemplate<Object, Object> jobEventKafkaTemplate() {
    Map<String, Object> prop = getStringObjectMap();
    DefaultKafkaProducerFactory<Object, Object> jsonSerializerProducer =
        new DefaultKafkaProducerFactory<>(prop);
    jsonSerializerProducer.addListener(new MicrometerProducerListener<>(meterRegistry));
    return new KafkaTemplate<>(jsonSerializerProducer);
  }

  @Bean(name = "ItemSerializerProducer")
  public KafkaTemplate<String, Object> itemKafkaTemplate() {
    ProducerFactory<String, Object> itemSerializerProducer = kafkaItemProducerFactory();
    itemSerializerProducer.addListener(new MicrometerProducerListener<>(meterRegistry));
    return new KafkaTemplate<>(itemSerializerProducer);
  }

  @Bean
  @Primary
  public KafkaTemplate<String, String> platformTaskKafkaTemplate() {
    Map<String, Object> prop = getStringObjectMap();
    DefaultKafkaProducerFactory<String, String> platformTaskKafkaProducer =
        new DefaultKafkaProducerFactory<>(prop);
    platformTaskKafkaProducer.addListener(new MicrometerProducerListener<>(meterRegistry));
    return new KafkaTemplate<>(platformTaskKafkaProducer);
  }
}
