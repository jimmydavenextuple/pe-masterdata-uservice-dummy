/*
 * Copyright (c) 2025., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.pe.webhook.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.type.TypeReference;
import com.nextuple.master.data.integration.inbound.BatchRequest;
import com.nextuple.master.data.integration.outbound.BatchResponse;
import com.nextuple.pe.webhook.domain.dtos.MasterDataIngestionDto;
import com.nextuple.pe.webhook.domain.inbound.FeedRequest;
import com.nextuple.vendor.consumer.dto.VendorFeedDto;
import com.nextuple.vendor.consumer.impl.VendorBatchServiceImpl;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class VendorFeedHandlingServiceTest {

  @Mock private VendorBatchServiceImpl vendorBatchService;

  @Mock private VendorFeedIngestionService vendorFeedIngestionService;

  @InjectMocks private VendorFeedHandlingService vendorFeedHandlingService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("Get Type Reference for Vendor test")
  void testGetTypeReference() {
    TypeReference<List<BatchRequest<VendorFeedDto>>> typeReference =
        vendorFeedHandlingService.getTypeReference();
    assertEquals(
        new TypeReference<List<BatchRequest<VendorFeedDto>>>() {}.getType(),
        typeReference.getType());
  }

  @Test
  @DisplayName("Invoke Batch Feed for Vendor test")
  void testInvokeBatchFeedImplMethod() {
    List<BatchRequest<VendorFeedDto>> batchRequests = List.of(new BatchRequest<>());
    BatchResponse expectedResponse = new BatchResponse();
    when(vendorBatchService.processRecordsWithoutRetry(batchRequests)).thenReturn(expectedResponse);

    BatchResponse actualResponse =
        vendorFeedHandlingService.invokeBatchFeedImplMethod(batchRequests);

    assertEquals(expectedResponse, actualResponse);
    verify(vendorBatchService, times(1)).processRecordsWithoutRetry(batchRequests);
  }

  @Test
  @DisplayName("Populate OrgId for Vendor test")
  void testPopulateOrgId() {
    String orgId = "testOrg";
    BatchRequest<VendorFeedDto> batchRequest = new BatchRequest<>();
    VendorFeedDto vendorFeedDto = new VendorFeedDto();
    batchRequest.setPayload(vendorFeedDto);
    List<BatchRequest<VendorFeedDto>> batchRequests = List.of(batchRequest);

    vendorFeedHandlingService.populateOrgId(batchRequests, orgId);

    assertEquals(orgId, vendorFeedDto.getOrgId());
  }

  @Test
  @DisplayName("Get Type Reference for Publishing for Vendor test")
  void testGetTypeReferenceForPublishing() {
    TypeReference<FeedRequest<MasterDataIngestionDto<VendorFeedDto>>> typeReference =
        vendorFeedHandlingService.getTypeReferenceForPublishing();
    assertEquals(
        new TypeReference<FeedRequest<MasterDataIngestionDto<VendorFeedDto>>>() {}.getType(),
        typeReference.getType());
  }

  @Test
  @DisplayName("Publish Feed to Kafka for Vendor test")
  void testPublishFeedToKafka() {
    String orgId = "testOrg";
    FeedRequest<MasterDataIngestionDto<VendorFeedDto>> feedRequest = new FeedRequest<>();

    vendorFeedHandlingService.publishFeedToKafka(orgId, feedRequest);

    verify(vendorFeedIngestionService, times(1)).publishFeedToKafka(orgId, feedRequest);
  }
}
