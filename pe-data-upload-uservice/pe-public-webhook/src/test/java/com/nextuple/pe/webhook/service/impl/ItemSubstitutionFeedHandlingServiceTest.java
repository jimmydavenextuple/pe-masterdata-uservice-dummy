package com.nextuple.pe.webhook.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.type.TypeReference;
import com.nextuple.item.substitution.consumer.dto.ItemSubstitutionFeedDto;
import com.nextuple.item.substitution.consumer.impl.ItemSubstitutionBatchServiceImpl;
import com.nextuple.master.data.integration.inbound.BatchRequest;
import com.nextuple.master.data.integration.outbound.BatchResponse;
import com.nextuple.pe.webhook.domain.dtos.MasterDataIngestionDto;
import com.nextuple.pe.webhook.domain.inbound.FeedRequest;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class ItemSubstitutionFeedHandlingServiceTest {

  @InjectMocks private ItemSubstitutionFeedHandlingService itemSubstitutionFeedHandlingService;

  @Mock private ItemSubstitutionBatchServiceImpl itemSubstitutionBatchService;

  @Mock private ItemSubstitutionFeedIngestionService itemSubstitutionFeedIngestionService;

  private List<BatchRequest<ItemSubstitutionFeedDto>> mockBatchRequests;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    BatchRequest<ItemSubstitutionFeedDto> batchRequest = new BatchRequest<>();
    ItemSubstitutionFeedDto feedDto = new ItemSubstitutionFeedDto();
    feedDto.setPrimaryItemId("item1");
    feedDto.setOrgId("org1");
    feedDto.setPrimaryUom("EA");
    feedDto.setAlternateItemId("item2");
    feedDto.setAlternateUom("KG");
    batchRequest.setPayload(feedDto);

    mockBatchRequests = Collections.singletonList(batchRequest);
  }

  @Test
  void testGetTypeReference() {
    TypeReference<List<BatchRequest<ItemSubstitutionFeedDto>>> typeReference =
        itemSubstitutionFeedHandlingService.getTypeReference();
    assertNotNull(typeReference);
  }

  @Test
  void testInvokeBatchFeedImplMethod() {
    BatchResponse mockResponse = new BatchResponse();
    when(itemSubstitutionBatchService.processRecordsWithoutRetry(mockBatchRequests))
        .thenReturn(mockResponse);

    BatchResponse result =
        itemSubstitutionFeedHandlingService.invokeBatchFeedImplMethod(mockBatchRequests);
    assertEquals(mockResponse, result);

    verify(itemSubstitutionBatchService, times(1)).processRecordsWithoutRetry(mockBatchRequests);
  }

  @Test
  void testPopulateOrgId() {
    itemSubstitutionFeedHandlingService.populateOrgId(mockBatchRequests, "org1");
    assertEquals("org1", mockBatchRequests.get(0).getPayload().getOrgId());
  }

  @Test
  void testGetTypeReferenceForPublishing() {
    TypeReference<FeedRequest<MasterDataIngestionDto<ItemSubstitutionFeedDto>>> typeReference =
        itemSubstitutionFeedHandlingService.getTypeReferenceForPublishing();
    assertNotNull(typeReference);
  }

  @Test
  void testPublishFeedToKafka() {
    FeedRequest<MasterDataIngestionDto<ItemSubstitutionFeedDto>> mockFeedRequest =
        new FeedRequest<>();
    doNothing()
        .when(itemSubstitutionFeedIngestionService)
        .publishFeedToKafka("org1", mockFeedRequest);

    itemSubstitutionFeedHandlingService.publishFeedToKafka("org1", mockFeedRequest);

    verify(itemSubstitutionFeedIngestionService, times(1))
        .publishFeedToKafka("org1", mockFeedRequest);
  }
}
