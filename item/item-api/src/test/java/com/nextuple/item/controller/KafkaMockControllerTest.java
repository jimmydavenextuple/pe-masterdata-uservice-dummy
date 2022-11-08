package com.nextuple.item.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.nextuple.item.TestUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.SettableListenableFuture;

class KafkaMockControllerTest {

  @InjectMocks private KafkaMockController kafkaMockController;

  @InjectMocks private TestUtil testUtil;

  @Mock private KafkaTemplate<String, Object> kafkaTemplate;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void produceAvroKafkaMessageTest() {
    SettableListenableFuture<SendResult<String, Object>> future = new SettableListenableFuture<>();
    when(kafkaTemplate.send(any(), any())).thenReturn(future);

    ResponseEntity<String> response =
        kafkaMockController.produceAvroKafkaMessage("topic", testUtil.getItemRecord());

    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertEquals("Success", response.getBody());

    verify(kafkaTemplate, times(1)).send(any(), any());
  }

  @Test
  void produceAvroKafkaMessageExceptionTest() {
    when(kafkaTemplate.send(any(), any())).thenThrow(new RuntimeException("error"));

    Assertions.assertThrows(
        Exception.class,
        () -> kafkaMockController.produceAvroKafkaMessage("topic", testUtil.getItemRecord()));

    verify(kafkaTemplate, times(1)).send(any(), any());
  }

  @Test
  void produceKafkaMessageTest() {
    SettableListenableFuture<SendResult<String, Object>> future = new SettableListenableFuture<>();
    when(kafkaTemplate.send(any(), any())).thenReturn(future);

    ResponseEntity<String> response =
        kafkaMockController.produceKafkaMessage("topic", testUtil.getItemMasterEvent());

    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertEquals("Success", response.getBody());

    verify(kafkaTemplate, times(1)).send(any(), any());
  }

  @Test
  void produceKafkaMessageExceptionTest() {
    when(kafkaTemplate.send(any(), any())).thenThrow(new RuntimeException("error"));

    Assertions.assertThrows(
        Exception.class,
        () -> kafkaMockController.produceKafkaMessage("topic", testUtil.getItemMasterEvent()));

    verify(kafkaTemplate, times(1)).send(any(), any());
  }
}
