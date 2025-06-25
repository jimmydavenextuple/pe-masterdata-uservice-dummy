package com.nextuple.item.substitution.consumer.consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.fasterxml.jackson.core.type.TypeReference;
import com.nextuple.item.substitution.consumer.dto.ItemSubstitutionFeedDto;
import com.nextuple.item.substitution.consumer.impl.ItemSubstitutionBatchServiceImpl;
import com.nextuple.master.data.integration.inbound.BatchRequest;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.support.converter.KafkaMessageHeaders;

class ItemSubstitutionFeedConsumerTest {

  @InjectMocks private ItemSubstitutionFeedConsumer itemSubstitutionFeedConsumer;

  @Mock private ItemSubstitutionBatchServiceImpl itemSubstitutionBatchService;

  @Mock private KafkaMessageHeaders kafkaMessageHeaders;

  private List<BatchRequest<ItemSubstitutionFeedDto>> mockFeed;

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

    mockFeed = List.of(batchRequest);
  }

  @Test
  void testConsumeVendorFeedSuccess() {
    itemSubstitutionFeedConsumer.consumeVendorFeed(mockFeed, kafkaMessageHeaders);
    verify(itemSubstitutionBatchService, times(1)).processRecordsWithRetry(mockFeed);
  }

  @Test
  void testConsumeVendorFeedException() {
    doThrow(new RuntimeException("Processing error"))
        .when(itemSubstitutionBatchService)
        .processRecordsWithRetry(mockFeed);

    RuntimeException exception =
        assertThrows(
            RuntimeException.class,
            () -> itemSubstitutionFeedConsumer.consumeVendorFeed(mockFeed, kafkaMessageHeaders));
    assertEquals("Processing error", exception.getMessage());

    verify(itemSubstitutionBatchService, times(1)).processRecordsWithRetry(mockFeed);
  }

  @Test
  void testInvokeFeedProcessingService() {
    itemSubstitutionFeedConsumer.invokeFeedProcessingService(mockFeed);
    verify(itemSubstitutionBatchService, times(1)).processRecordsWithRetry(mockFeed);
  }

  @Test
  void testGetTypeReference() {
    TypeReference<List<BatchRequest<ItemSubstitutionFeedDto>>> typeReference =
        itemSubstitutionFeedConsumer.getTypeReference();
    assertNotNull(typeReference);
  }
}
