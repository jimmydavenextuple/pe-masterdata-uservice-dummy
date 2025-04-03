package com.nextuple.pe.webhook.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.core.type.TypeReference;
import com.nextuple.master.data.integration.inbound.BatchRequest;
import com.nextuple.master.data.integration.outbound.BatchResponse;
import com.nextuple.pe.webhook.domain.dtos.MasterDataIngestionDto;
import com.nextuple.pe.webhook.domain.inbound.FeedRequest;
import com.nextuple.vendor.consumer.dto.VendorFeedDto;
import com.nextuple.vendor.consumer.impl.VendorBatchServiceImpl;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
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
  void testGetTypeReference() {
    TypeReference<List<BatchRequest<VendorFeedDto>>> typeReference =
        vendorFeedHandlingService.getTypeReference();
    assertEquals(
        new TypeReference<List<BatchRequest<VendorFeedDto>>>() {}.getType(),
        typeReference.getType());
  }

  @Test
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
  void testGetTypeReferenceForPublishing() {
    TypeReference<FeedRequest<MasterDataIngestionDto<VendorFeedDto>>> typeReference =
        vendorFeedHandlingService.getTypeReferenceForPublishing();
    assertEquals(
        new TypeReference<FeedRequest<MasterDataIngestionDto<VendorFeedDto>>>() {}.getType(),
        typeReference.getType());
  }

  @Test
  void testPublishFeedToKafka() {
    String orgId = "testOrg";
    FeedRequest<MasterDataIngestionDto<VendorFeedDto>> feedRequest = new FeedRequest<>();

    vendorFeedHandlingService.publishFeedToKafka(orgId, feedRequest);

    verify(vendorFeedIngestionService, times(1)).publishFeedToKafka(orgId, feedRequest);
  }
}
