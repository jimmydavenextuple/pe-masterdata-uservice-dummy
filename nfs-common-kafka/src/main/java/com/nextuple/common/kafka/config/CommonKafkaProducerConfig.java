package com.nextuple.common.kafka.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Configuration
public class CommonKafkaProducerConfig<K,V> {

    @Bean
    public ProducerFactory<K, V> producerFactory(KafkaProducerConsumerProperties configProps) {
        return new DefaultKafkaProducerFactory<>(configProps.getProducer());
    }

    @Bean
    public KafkaTemplate<K, V> kafkaTemplate(ProducerFactory<K, V> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }
}
