/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.core.event.listeners;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.nextuple.core.exception.LocalCacheUpdateEventException;
import com.nextuple.core.producer.EntityEventProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

class CommonEntityListenerTest {
  @Mock EntityEventProducer entityEventProducer;
  @InjectMocks CommonEntityListener commonEntityListener;

  @BeforeEach
  void init() {
    MockitoAnnotations.openMocks(this);
    ReflectionTestUtils.setField(commonEntityListener, "kafkaEnabledFlag", true);
  }

  @Test
  void afterUpdateTest() throws LocalCacheUpdateEventException, IllegalAccessException {
    NodePriorityEntity nodePriorityEntity = new NodePriorityEntity("Node1", "NXT");
    doNothing().when(entityEventProducer).publishEntityEvent(any());
    commonEntityListener.afterUpdating(nodePriorityEntity);
    verify(entityEventProducer, times(2)).publishEntityEvent(any());
  }
}

class NodePriorityEntity {
  private String nodeId;
  private String orgId;

  NodePriorityEntity(String nodeId, String orgId) {
    this.nodeId = nodeId;
    this.nodeId = orgId;
  }
}
