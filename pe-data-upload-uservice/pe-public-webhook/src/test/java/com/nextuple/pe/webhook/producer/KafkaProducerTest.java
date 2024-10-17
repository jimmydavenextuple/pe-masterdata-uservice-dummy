package com.nextuple.pe.webhook.producer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.pe.webhook.domain.dtos.InventoryATP;
import com.nextuple.pe.webhook.producer.impl.KafkaProducerImpl;
import com.nextuple.pe.webhook.util.TestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.core.KafkaTemplate;

class KafkaProducerTest {
  @Mock KafkaTemplate<Object, Object> kafkaTemplate;
  @InjectMocks KafkaProducerImpl kafkaProducer;

  @BeforeEach
  public void init() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void publishFeedToKafkaTest() {
    InventoryATP inventoryATP = TestUtil.createInventoryATPs(1).get(0);
    when(kafkaTemplate.send(any(), any(), any())).thenReturn(null);
    kafkaProducer.publishFeedToKafka(inventoryATP, inventoryATP.getItemId(), TestUtil.IV_TOPIC);
    verify(kafkaTemplate, times(1)).send(any(), any(), any());
  }
}
