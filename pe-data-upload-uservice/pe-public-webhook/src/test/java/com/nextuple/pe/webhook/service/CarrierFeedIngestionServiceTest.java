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

import com.nextuple.carrier.consumer.dto.CarrierFeedDto;
import com.nextuple.master.data.integration.enums.ActionEnum;
import com.nextuple.pe.webhook.domain.dtos.MasterDataIngestionDto;
import com.nextuple.pe.webhook.domain.inbound.FeedRequest;
import com.nextuple.pe.webhook.producer.KafkaProducer;
import com.nextuple.pe.webhook.service.impl.CarrierFeedIngestionService;
import com.nextuple.pe.webhook.util.TestUtil;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

class CarrierFeedIngestionServiceTest {
  @Mock KafkaProducer kafkaProducer;
  @InjectMocks CarrierFeedIngestionService carrierFeedIngestionService;
  @InjectMocks TestUtil testUtil;

  @BeforeEach
  public void init() {
    MockitoAnnotations.openMocks(this);
    ReflectionTestUtils.setField(
        carrierFeedIngestionService, "carrierFeedTopic", TestUtil.CARRIER_TOPIC);
  }

  @Test
  @DisplayName("Case when message not published by default")
  void publishFeedToKafkaDefaultTest() {
    FeedRequest<MasterDataIngestionDto<CarrierFeedDto>> feedRequest = new FeedRequest<>();
    MasterDataIngestionDto<CarrierFeedDto> carrierIngestionRequest = new MasterDataIngestionDto<>();
    carrierIngestionRequest.setAction(ActionEnum.CREATE);
    carrierIngestionRequest.setPayload(testUtil.createCarrierFeedDto());
    MasterDataIngestionDto<CarrierFeedDto> carrierIngestionUpdationRequest =
        new MasterDataIngestionDto<>();
    carrierIngestionUpdationRequest.setAction(ActionEnum.UPDATE);
    carrierIngestionUpdationRequest.setPayload(testUtil.createCarrierFeedDto());
    feedRequest.setData(List.of(carrierIngestionRequest, carrierIngestionUpdationRequest));

    carrierFeedIngestionService.publishFeedToKafka(TestUtil.ORG_ID, feedRequest);
    verify(kafkaProducer, times(0)).publishFeedToKafka(any(), any(), any());
  }

  @Test
  @DisplayName("Case when message not published since it is disabled")
  void publishFeedToKafkaDisabledTest() {
    ReflectionTestUtils.setField(carrierFeedIngestionService, "isPublishEnabled", false);
    FeedRequest<MasterDataIngestionDto<CarrierFeedDto>> feedRequest = new FeedRequest<>();
    MasterDataIngestionDto<CarrierFeedDto> carrierIngestionRequest = new MasterDataIngestionDto<>();
    carrierIngestionRequest.setAction(ActionEnum.CREATE);
    carrierIngestionRequest.setPayload(testUtil.createCarrierFeedDto());
    MasterDataIngestionDto<CarrierFeedDto> carrierIngestionUpdationRequest =
        new MasterDataIngestionDto<>();
    carrierIngestionUpdationRequest.setAction(ActionEnum.UPDATE);
    carrierIngestionUpdationRequest.setPayload(testUtil.createCarrierFeedDto());
    feedRequest.setData(List.of(carrierIngestionRequest, carrierIngestionUpdationRequest));

    carrierFeedIngestionService.publishFeedToKafka(TestUtil.ORG_ID, feedRequest);
    verify(kafkaProducer, times(0)).publishFeedToKafka(any(), any(), any());
  }

  @Test
  @DisplayName("Case when message is published since it is enabled")
  void publishFeedToKafkaEnabledTest() {
    ReflectionTestUtils.setField(carrierFeedIngestionService, "isPublishEnabled", true);
    FeedRequest<MasterDataIngestionDto<CarrierFeedDto>> feedRequest = new FeedRequest<>();
    MasterDataIngestionDto<CarrierFeedDto> carrierIngestionRequest = new MasterDataIngestionDto<>();
    carrierIngestionRequest.setAction(ActionEnum.CREATE);
    carrierIngestionRequest.setPayload(testUtil.createCarrierFeedDto());
    MasterDataIngestionDto<CarrierFeedDto> carrierIngestionUpdationRequest =
        new MasterDataIngestionDto<>();
    carrierIngestionUpdationRequest.setAction(ActionEnum.UPDATE);
    carrierIngestionUpdationRequest.setPayload(testUtil.createCarrierFeedDto());
    feedRequest.setData(List.of(carrierIngestionRequest, carrierIngestionUpdationRequest));

    doNothing().when(kafkaProducer).publishFeedToKafka(any(), any(), any());
    carrierFeedIngestionService.publishFeedToKafka(TestUtil.ORG_ID, feedRequest);
    verify(kafkaProducer, times(2)).publishFeedToKafka(any(), any(), any());
  }
}
