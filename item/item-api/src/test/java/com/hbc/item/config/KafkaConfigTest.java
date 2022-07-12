package com.hbc.item.config;

import com.hbc.item.ItemRecord;
import com.hbc.item.consumer.config.AvroKafkaConfig;
import com.hbc.item.consumer.config.KafkaConfig;
import com.hbc.item.exception.ItemDomainException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.test.util.ReflectionTestUtils;

class KafkaConfigTest {
  @InjectMocks private KafkaConfig kafkaConfig;

  @InjectMocks private AvroKafkaConfig avroKafkaConfig;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void onItemMasterEventConsumptionTest() throws ItemDomainException {
    ReflectionTestUtils.setField(kafkaConfig, "bootstrapAddress", "");
    ReflectionTestUtils.setField(avroKafkaConfig, "bootstrapAddress", "");
    //    ReflectionTestUtils.setField(kafkaConfig, "securityProtocol", "");
    //    ReflectionTestUtils.setField(kafkaConfig, "saslMechanism", "");
    //    ReflectionTestUtils.setField(kafkaConfig, "saslJaasConfig", "");
    ReflectionTestUtils.setField(kafkaConfig, "maxRetryCount", 0L);
    ConsumerFactory<String, Object> res = kafkaConfig.consumerFactory();

    ProducerFactory<String, Object> producerFactory = avroKafkaConfig.producerFactory();
    ConsumerFactory<String, ItemRecord> consumerFactory = avroKafkaConfig.consumerFactory();

    Assertions.assertEquals(8, res.getConfigurationProperties().size());
    Assertions.assertEquals(3, producerFactory.getConfigurationProperties().size());
    Assertions.assertEquals(3, consumerFactory.getConfigurationProperties().size());
  }
}
