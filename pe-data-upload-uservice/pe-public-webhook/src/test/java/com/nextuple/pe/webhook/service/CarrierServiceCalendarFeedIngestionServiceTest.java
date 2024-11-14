/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.pe.webhook.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.nextuple.calendar.consumer.dto.CarrierServiceCalendarFeedDto;
import com.nextuple.master.data.integration.enums.ActionEnum;
import com.nextuple.pe.webhook.domain.dtos.MasterDataIngestionDto;
import com.nextuple.pe.webhook.domain.inbound.FeedRequest;
import com.nextuple.pe.webhook.producer.KafkaProducer;
import com.nextuple.pe.webhook.service.impl.CarrierServiceCalendarFeedIngestionService;
import com.nextuple.pe.webhook.util.TestUtil;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

class CarrierServiceCalendarFeedIngestionServiceTest {
  @Mock KafkaProducer kafkaProducer;

  @InjectMocks
  CarrierServiceCalendarFeedIngestionService carrierServiceCalendarFeedIngestionService;

  @InjectMocks TestUtil testUtil;

  @BeforeEach
  public void init() {
    MockitoAnnotations.openMocks(this);
    ReflectionTestUtils.setField(
        carrierServiceCalendarFeedIngestionService,
        "carrierServiceCalendarFeedTopic",
        TestUtil.CARRIER_SERVICE_CALENDAR_TOPIC);
  }

  @Test
  void publishFeedToKafkaDefaultTest() {
    FeedRequest<MasterDataIngestionDto<CarrierServiceCalendarFeedDto>> feedRequest =
        new FeedRequest<>();
    MasterDataIngestionDto<CarrierServiceCalendarFeedDto> carrierServiceCalendarIngestionRequest =
        new MasterDataIngestionDto<>();
    carrierServiceCalendarIngestionRequest.setAction(ActionEnum.CREATE);
    carrierServiceCalendarIngestionRequest.setPayload(
        testUtil.createCarrierServiceCalendarFeedDto());
    feedRequest.setData(List.of(carrierServiceCalendarIngestionRequest));

    carrierServiceCalendarFeedIngestionService.publishFeedToKafka(TestUtil.ORG_ID, feedRequest);
    verify(kafkaProducer, times(0)).publishFeedToKafka(any(), any(), any());
  }

  @Test
  void publishFeedToKafkaDSisabledTest() {
    ReflectionTestUtils.setField(
        carrierServiceCalendarFeedIngestionService, "isPublishEnabled", false);
    FeedRequest<MasterDataIngestionDto<CarrierServiceCalendarFeedDto>> feedRequest =
        new FeedRequest<>();
    MasterDataIngestionDto<CarrierServiceCalendarFeedDto> carrierServiceCalendarIngestionRequest =
        new MasterDataIngestionDto<>();
    carrierServiceCalendarIngestionRequest.setAction(ActionEnum.CREATE);
    carrierServiceCalendarIngestionRequest.setPayload(
        testUtil.createCarrierServiceCalendarFeedDto());
    feedRequest.setData(List.of(carrierServiceCalendarIngestionRequest));

    carrierServiceCalendarFeedIngestionService.publishFeedToKafka(TestUtil.ORG_ID, feedRequest);
    verify(kafkaProducer, times(0)).publishFeedToKafka(any(), any(), any());
  }

  @Test
  void publishFeedToKafkaEnabledTest() {
    ReflectionTestUtils.setField(
        carrierServiceCalendarFeedIngestionService, "isPublishEnabled", true);
    FeedRequest<MasterDataIngestionDto<CarrierServiceCalendarFeedDto>> feedRequest =
        new FeedRequest<>();
    MasterDataIngestionDto<CarrierServiceCalendarFeedDto> carrierServiceCalendarIngestionRequest =
        new MasterDataIngestionDto<>();
    carrierServiceCalendarIngestionRequest.setAction(ActionEnum.CREATE);
    carrierServiceCalendarIngestionRequest.setPayload(
        testUtil.createCarrierServiceCalendarFeedDto());
    feedRequest.setData(List.of(carrierServiceCalendarIngestionRequest));

    doNothing().when(kafkaProducer).publishFeedToKafka(any(), any(), any());
    carrierServiceCalendarFeedIngestionService.publishFeedToKafka(TestUtil.ORG_ID, feedRequest);
    verify(kafkaProducer, times(1)).publishFeedToKafka(any(), any(), any());
  }
}
