/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.item.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.nextuple.item.TestUtil;
import java.util.concurrent.CompletableFuture;
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

class ItemKafkaMockControllerTest {

  @InjectMocks private ItemKafkaMockController kafkaMockController;

  @InjectMocks private TestUtil testUtil;

  @Mock private KafkaTemplate<String, Object> kafkaTemplate;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void produceKafkaMessageTest() {
    CompletableFuture<SendResult<String, Object>> future = new CompletableFuture<>();
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
