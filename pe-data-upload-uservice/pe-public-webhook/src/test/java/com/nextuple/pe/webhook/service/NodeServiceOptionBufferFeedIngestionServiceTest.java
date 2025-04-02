/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.pe.webhook.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.nextuple.common.enums.ActionEnum;
import com.nextuple.node.carrier.consumer.dto.NodeServiceOptionBufferFeedDto;
import com.nextuple.pe.webhook.domain.dtos.MasterDataIngestionDto;
import com.nextuple.pe.webhook.domain.inbound.FeedRequest;
import com.nextuple.pe.webhook.producer.KafkaProducer;
import com.nextuple.pe.webhook.service.impl.NodeServiceOptionBufferFeedIngestionService;
import com.nextuple.pe.webhook.util.TestUtil;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

class NodeServiceOptionBufferFeedIngestionServiceTest {
  @Mock KafkaProducer kafkaProducer;

  @InjectMocks
  NodeServiceOptionBufferFeedIngestionService nodeServiceOptionBufferFeedIngestionService;

  @InjectMocks TestUtil testUtil;

  @BeforeEach
  public void init() {
    MockitoAnnotations.openMocks(this);
    ReflectionTestUtils.setField(
        nodeServiceOptionBufferFeedIngestionService,
        "nodeServiceOptionBufferFeedTopic",
        TestUtil.NODE_SERVICE_OPTION_BUFFER_TOPIC);
  }

  @Test
  @DisplayName("Case when message not published by default")
  void publishFeedToKafkaDefaultTest() {
    FeedRequest<MasterDataIngestionDto<NodeServiceOptionBufferFeedDto>> feedRequest =
        new FeedRequest<>();
    MasterDataIngestionDto<NodeServiceOptionBufferFeedDto> nodeServiceOptionBufferIngestionRequest =
        new MasterDataIngestionDto<>();
    nodeServiceOptionBufferIngestionRequest.setAction(ActionEnum.CREATE);
    nodeServiceOptionBufferIngestionRequest.setPayload(
        testUtil.createNodeServiceOptionBufferFeedDto());
    feedRequest.setData(List.of(nodeServiceOptionBufferIngestionRequest));

    nodeServiceOptionBufferFeedIngestionService.publishFeedToKafka(TestUtil.ORG_ID, feedRequest);
    verify(kafkaProducer, times(0)).publishFeedToKafka(any(), any(), any());
  }

  @Test
  @DisplayName("Case when message not published since it is disabled")
  void publishFeedToKafkaSisabledTest() {
    ReflectionTestUtils.setField(
        nodeServiceOptionBufferFeedIngestionService, "isPublishEnabled", false);
    FeedRequest<MasterDataIngestionDto<NodeServiceOptionBufferFeedDto>> feedRequest =
        new FeedRequest<>();
    MasterDataIngestionDto<NodeServiceOptionBufferFeedDto> nodeServiceOptionBufferIngestionRequest =
        new MasterDataIngestionDto<>();
    nodeServiceOptionBufferIngestionRequest.setAction(ActionEnum.CREATE);
    nodeServiceOptionBufferIngestionRequest.setPayload(
        testUtil.createNodeServiceOptionBufferFeedDto());
    feedRequest.setData(List.of(nodeServiceOptionBufferIngestionRequest));

    nodeServiceOptionBufferFeedIngestionService.publishFeedToKafka(TestUtil.ORG_ID, feedRequest);
    verify(kafkaProducer, times(0)).publishFeedToKafka(any(), any(), any());
  }

  @Test
  @DisplayName("When the node service option buffer feed is published successfully ")
  void publishFeedToKafkaEnabledTest() {
    ReflectionTestUtils.setField(
        nodeServiceOptionBufferFeedIngestionService, "isPublishEnabled", true);
    FeedRequest<MasterDataIngestionDto<NodeServiceOptionBufferFeedDto>> feedRequest =
        new FeedRequest<>();
    MasterDataIngestionDto<NodeServiceOptionBufferFeedDto> nodeServiceOptionBufferIngestionRequest =
        new MasterDataIngestionDto<>();
    nodeServiceOptionBufferIngestionRequest.setAction(ActionEnum.CREATE);
    nodeServiceOptionBufferIngestionRequest.setPayload(
        testUtil.createNodeServiceOptionBufferFeedDto());
    feedRequest.setData(List.of(nodeServiceOptionBufferIngestionRequest));

    doNothing().when(kafkaProducer).publishFeedToKafka(any(), any(), any());
    nodeServiceOptionBufferFeedIngestionService.publishFeedToKafka(TestUtil.ORG_ID, feedRequest);
    verify(kafkaProducer, times(1)).publishFeedToKafka(any(), any(), any());
  }
}
