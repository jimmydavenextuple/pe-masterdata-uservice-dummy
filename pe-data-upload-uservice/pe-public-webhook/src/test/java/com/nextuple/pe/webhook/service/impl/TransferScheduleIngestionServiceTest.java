/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.pe.webhook.service.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.nextuple.master.data.integration.enums.ActionEnum;
import com.nextuple.pe.webhook.domain.dtos.MasterDataIngestionDto;
import com.nextuple.pe.webhook.domain.inbound.FeedRequest;
import com.nextuple.pe.webhook.producer.KafkaProducer;
import com.nextuple.pe.webhook.util.TestUtil;
import com.nextuple.transit.consumer.dto.TransferScheduleDto;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

public class TransferScheduleIngestionServiceTest {
  @Mock KafkaProducer kafkaProducer;
  @InjectMocks TransferScheduleIngestionService transferScheduleIngestionService;
  @InjectMocks TestUtil testUtil;

  @BeforeEach
  public void init() {
    MockitoAnnotations.openMocks(this);
    ReflectionTestUtils.setField(
        transferScheduleIngestionService,
        "transferScheduleFeedTopic",
        TestUtil.TRANSFER_SCHEDULE_TOPIC);
  }

  @Test
  @DisplayName("Case when message not published by default")
  void publishFeedToKafkaDefaultTest() {
    FeedRequest<MasterDataIngestionDto<TransferScheduleDto>> feedRequest = new FeedRequest<>();
    MasterDataIngestionDto<TransferScheduleDto> calendarIngestionRequest =
        new MasterDataIngestionDto<>();
    calendarIngestionRequest.setAction(ActionEnum.CREATE);
    calendarIngestionRequest.setPayload(testUtil.createTransferScheduleDto());
    feedRequest.setData(List.of(calendarIngestionRequest));

    transferScheduleIngestionService.publishFeedToKafka(TestUtil.ORG_ID, feedRequest);
    verify(kafkaProducer, times(0)).publishFeedToKafka(any(), any(), any());
  }

  @Test
  @DisplayName("Case when message not published since it is disabled")
  void publishFeedToKafkaDisabledTest() {
    ReflectionTestUtils.setField(transferScheduleIngestionService, "isPublishEnabled", false);
    FeedRequest<MasterDataIngestionDto<TransferScheduleDto>> feedRequest = new FeedRequest<>();
    MasterDataIngestionDto<TransferScheduleDto> calendarIngestionRequest =
        new MasterDataIngestionDto<>();
    calendarIngestionRequest.setAction(ActionEnum.CREATE);
    calendarIngestionRequest.setPayload(testUtil.createTransferScheduleDto());
    feedRequest.setData(List.of(calendarIngestionRequest));

    transferScheduleIngestionService.publishFeedToKafka(TestUtil.ORG_ID, feedRequest);
    verify(kafkaProducer, times(0)).publishFeedToKafka(any(), any(), any());
  }

  @Test
  @DisplayName("Case when message is published since it is enabled")
  void publishFeedToKafkaTest() {
    ReflectionTestUtils.setField(transferScheduleIngestionService, "isPublishEnabled", true);
    FeedRequest<MasterDataIngestionDto<TransferScheduleDto>> feedRequest = new FeedRequest<>();
    MasterDataIngestionDto<TransferScheduleDto> calendarIngestionRequest =
        new MasterDataIngestionDto<>();
    calendarIngestionRequest.setAction(ActionEnum.CREATE);
    calendarIngestionRequest.setPayload(testUtil.createTransferScheduleDto());
    feedRequest.setData(List.of(calendarIngestionRequest));

    doNothing().when(kafkaProducer).publishFeedToKafka(any(), any(), any());
    transferScheduleIngestionService.publishFeedToKafka(TestUtil.ORG_ID, feedRequest);
    verify(kafkaProducer, times(1)).publishFeedToKafka(any(), any(), any());
  }
}
