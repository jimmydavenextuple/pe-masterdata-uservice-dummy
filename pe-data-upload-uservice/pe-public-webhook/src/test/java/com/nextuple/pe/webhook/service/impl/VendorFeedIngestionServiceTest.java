/*
 * Copyright (c) 2025., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.pe.webhook.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.nextuple.master.data.integration.inbound.BatchRequest;
import com.nextuple.pe.webhook.domain.dtos.MasterDataIngestionDto;
import com.nextuple.pe.webhook.domain.inbound.FeedRequest;
import com.nextuple.pe.webhook.producer.KafkaProducer;
import com.nextuple.pe.webhook.util.TestUtil;
import com.nextuple.vendor.consumer.dto.VendorFeedDto;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

class VendorFeedIngestionServiceTest {

  @Mock private KafkaProducer kafkaProducer;

  @InjectMocks private VendorFeedIngestionService vendorFeedIngestionService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    ReflectionTestUtils.setField(vendorFeedIngestionService, "vendorFeedTopic", "test-topic");
    ReflectionTestUtils.setField(vendorFeedIngestionService, "isPublishEnabled", true);
  }

  @Test
  @DisplayName("Publish Vendor Feed Test")
  void testPublish() {
    String orgId = TestUtil.ORG_ID;
    FeedRequest<MasterDataIngestionDto<VendorFeedDto>> feedRequest = new FeedRequest<>();
    MasterDataIngestionDto<VendorFeedDto> dto = new MasterDataIngestionDto<>();
    VendorFeedDto vendorFeedDto = new VendorFeedDto();
    vendorFeedDto.setVendorId("vendor1");
    dto.setPayload(vendorFeedDto);
    feedRequest.setData(List.of(dto));

    vendorFeedIngestionService.publish(orgId, feedRequest);

    ArgumentCaptor<BatchRequest> captor = ArgumentCaptor.forClass(BatchRequest.class);
    verify(kafkaProducer, times(1))
        .publishFeedToKafka(captor.capture(), eq("vendor1"), eq("test-topic"));

    BatchRequest<VendorFeedDto> capturedRequest = captor.getValue();
    assertEquals("vendor1", capturedRequest.getPayload().getVendorId());
    assertEquals(orgId, capturedRequest.getPayload().getOrgId());
  }

  @Test
  @DisplayName("Vendor Feed isEnabled flag test")
  void testIsPublishEnabled() {
    assertTrue(vendorFeedIngestionService.isPublishEnabled());

    ReflectionTestUtils.setField(vendorFeedIngestionService, "isPublishEnabled", false);
    assertFalse(vendorFeedIngestionService.isPublishEnabled());
  }

  @Test
  @DisplayName("Publish Vendor Feed to Kafka Test - isPublishEnabled flag is disabled")
  void testPublishFeedToKafkaDisabled() {
    ReflectionTestUtils.setField(vendorFeedIngestionService, "isPublishEnabled", false);
    String orgId = TestUtil.ORG_ID;
    FeedRequest<MasterDataIngestionDto<VendorFeedDto>> feedRequest = new FeedRequest<>();

    vendorFeedIngestionService.publishFeedToKafka(orgId, feedRequest);

    verify(kafkaProducer, never()).publishFeedToKafka(any(), any(), any());
  }
}
