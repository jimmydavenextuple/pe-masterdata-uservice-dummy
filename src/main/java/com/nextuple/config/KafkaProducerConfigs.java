package com.nextuple.config;

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

  @Value(value = "${spring.kafka.bootstrap-servers}")
  private String bootstrapServers;

  @Bean("jsonSerializerProperties")
  @ConfigurationProperties(prefix = "spring.kafka.producer-json")
  public Map<String, Object> jsonSerializerProperties() {
    return this.kafkaProperties.buildProducerProperties();
  }

  @Bean("itemSerializerProperties")
  @ConfigurationProperties(prefix = "spring.kafka.producer-item")
  public Map<String, Object> itemSerializerProperties() {
    return this.kafkaProperties.buildProducerProperties();
  }

  @Bean
  public ProducerFactory<String, Object> kafkaJsonProducerFactory() {
    Map<String, Object> prop = new HashMap<>(jsonSerializerProperties());
    prop.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, prop.get("key.serializer"));
    prop.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, prop.get("value.serializer"));
    prop.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    // Map<String, Object> properties = (Map<String, Object>) prop.get("properties");
    // Map<String, Object> interceptor = (Map<String, Object>) prop.get("interceptor");
    // Map<String, Object> saslProps = (Map<String, Object>) properties.get("sasl");

    prop.put(ProducerConfig.ACKS_CONFIG, prop.get("acks").toString());
    // prop.put(CommonClientConfigs.RETRIES_CONFIG, properties.get("retries"));
    // prop.put(CommonClientConfigs.RETRY_BACKOFF_MS_CONFIG, properties.get("retry-backoff-ms"));

    prop.put(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG, prop.get("interceptor.classes"));
    //    prop.put(
    //        CommonClientConfigs.SECURITY_PROTOCOL_CONFIG,
    //        ((Map<String, Object>) properties.get("security"))
    //            .get("protocol")); // properties -> security -> protocol
    //    prop.put(SaslConfigs.SASL_MECHANISM, saslProps.get("mechanism"));
    //    prop.put(
    //        SaslConfigs.SASL_JAAS_CONFIG, ((Map<String, Object>)
    // saslProps.get("jaas")).get("config"));
    return new DefaultKafkaProducerFactory<>(prop);
  }

  @Bean
  public ProducerFactory<Object, Object> kafkaItemProducerFactory() {
    HashMap<String, Object> prop = new HashMap<>(itemSerializerProperties());
    prop.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, prop.get("key.serializer"));
    prop.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, prop.get("value.serializer"));
    prop.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    //    prop.put("schema.registry.url", prop.get("schema-registry-url"));
    //    Map<String, Object> properties = (Map<String, Object>) prop.get("properties");
    //    Map<String, Object> interceptor = (Map<String, Object>) properties.get("interceptor");
    //    Map<String, Object> saslProps = (Map<String, Object>) properties.get("sasl");
    //
    //    prop.put(ProducerConfig.ACKS_CONFIG, properties.get("acks").toString());
    //    prop.put(CommonClientConfigs.RETRIES_CONFIG, properties.get("retries"));
    //    prop.put(CommonClientConfigs.RETRY_BACKOFF_MS_CONFIG, properties.get("retry-backoff-ms"));
    //    prop.put(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG, interceptor.get("classes"));
    //    prop.put(
    //        CommonClientConfigs.SECURITY_PROTOCOL_CONFIG,
    //        ((Map<String, Object>) properties.get("security"))
    //            .get("protocol")); // properties -> security -> protocol
    //    prop.put(SaslConfigs.SASL_MECHANISM, saslProps.get("mechanism"));
    //    prop.put(
    //        SaslConfigs.SASL_JAAS_CONFIG, ((Map<String, Object>)
    // saslProps.get("jaas")).get("config"));
    return new DefaultKafkaProducerFactory<>(prop);
  }

  @Bean(name = "JsonSerializerProducer")
  public KafkaTemplate<String, Object> kafkaTemplate() {
    return new KafkaTemplate<>(kafkaJsonProducerFactory());
  }

  @Bean(name = "ItemSerializerProducer")
  @Primary
  public KafkaTemplate<Object, Object> avroKafkaTemplate() {
    return new KafkaTemplate<>(kafkaItemProducerFactory());
  }
}
