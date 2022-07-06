package com.hbc.item.consumer.config;

import com.hbc.item.ItemRecord;
import com.hbc.item.consumer.serializer.ItemDeserializer;
import com.hbc.item.consumer.serializer.ItemSerializer;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@EnableKafka
@Configuration
public class KafkaConfig {

  @Value(value = "${spring.kafka.bootstrap-servers}")
  private String bootstrapAddress;

  @Value(value = "${app.consumer-retry-count}")
  private long maxRetryCount;

  @Profile("default")
  @Bean
  public ProducerFactory<String, Object> producerFactory() {
    Map<String, Object> configProps = new HashMap<>();
    configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
    configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ItemSerializer.class);
    return new DefaultKafkaProducerFactory<>(configProps);
  }

  @Profile("default")
  @Bean
  public KafkaTemplate<String, Object> kafkaTemplate() {
    return new KafkaTemplate<>(producerFactory());
  }

  @Profile("default")
  @Bean
  public ConsumerFactory<String, ItemRecord> consumerFactory() {
    Map<String, Object> props = new HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ItemDeserializer.class);
    return new DefaultKafkaConsumerFactory<>(
        props, new StringDeserializer(), new ItemDeserializer<>(ItemRecord.class));
  }

  @Profile("default")
  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, ItemRecord>
      kafkaListenerContainerFactory() {

    ConcurrentKafkaListenerContainerFactory<String, ItemRecord> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(consumerFactory());
    return factory;
  }

  @Bean
  public CommonErrorHandler kafkaErrorHandler(KafkaOperations<String, Object> kafkaOperations) {
    return new DefaultErrorHandler(
        new DeadLetterPublishingRecoverer(
            kafkaOperations, (cr, e) -> new TopicPartition(cr.topic() + "_ERR", cr.partition())),
        new FixedBackOff(0L, maxRetryCount));
  }
}
