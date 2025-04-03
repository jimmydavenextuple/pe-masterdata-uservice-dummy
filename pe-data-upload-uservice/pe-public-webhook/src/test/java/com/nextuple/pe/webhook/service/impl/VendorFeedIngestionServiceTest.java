package com.nextuple.pe.webhook.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.nextuple.master.data.integration.inbound.BatchRequest;
import com.nextuple.pe.webhook.domain.dtos.MasterDataIngestionDto;
import com.nextuple.pe.webhook.domain.inbound.FeedRequest;
import com.nextuple.pe.webhook.producer.KafkaProducer;
import com.nextuple.vendor.consumer.dto.VendorFeedDto;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
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
  void testPublish() {
    String orgId = "testOrg";
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
  void testIsPublishEnabled() {
    assertTrue(vendorFeedIngestionService.isPublishEnabled());

    ReflectionTestUtils.setField(vendorFeedIngestionService, "isPublishEnabled", false);
    assertFalse(vendorFeedIngestionService.isPublishEnabled());
  }

  @Test
  void testPublishFeedToKafkaDisabled() {
    ReflectionTestUtils.setField(vendorFeedIngestionService, "isPublishEnabled", false);
    String orgId = "testOrg";
    FeedRequest<MasterDataIngestionDto<VendorFeedDto>> feedRequest = new FeedRequest<>();

    vendorFeedIngestionService.publishFeedToKafka(orgId, feedRequest);

    verify(kafkaProducer, never()).publishFeedToKafka(any(), any(), any());
  }
}
