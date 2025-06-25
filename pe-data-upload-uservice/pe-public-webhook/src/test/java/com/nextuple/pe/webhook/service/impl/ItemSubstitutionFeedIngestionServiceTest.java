package com.nextuple.pe.webhook.service.impl;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.nextuple.common.enums.ActionEnum;
import com.nextuple.item.substitution.consumer.dto.ItemSubstitutionFeedDto;
import com.nextuple.pe.webhook.domain.dtos.MasterDataIngestionDto;
import com.nextuple.pe.webhook.domain.inbound.FeedRequest;
import com.nextuple.pe.webhook.producer.KafkaProducer;
import java.util.Collections;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

class ItemSubstitutionFeedIngestionServiceTest {

  @InjectMocks private ItemSubstitutionFeedIngestionService itemSubstitutionFeedIngestionService;

  @Mock private KafkaProducer kafkaProducer;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testPublish() {
    FeedRequest<MasterDataIngestionDto<ItemSubstitutionFeedDto>> feedRequest = new FeedRequest<>();
    MasterDataIngestionDto<ItemSubstitutionFeedDto> ingestionDto = new MasterDataIngestionDto<>();
    ItemSubstitutionFeedDto feedDto = new ItemSubstitutionFeedDto();
    feedDto.setPrimaryItemId("item1");
    feedDto.setOrgId("org1");
    ingestionDto.setPayload(feedDto);
    ingestionDto.setAction(ActionEnum.CREATE);
    feedRequest.setData(Collections.singletonList(ingestionDto));

    doNothing().when(kafkaProducer).publishFeedToKafka(any(), any(), any());

    itemSubstitutionFeedIngestionService.publish("org1", feedRequest);

    verify(kafkaProducer, times(1)).publishFeedToKafka(any(), eq("item1"), any());
  }

  @Test
  void testIsPublishEnabledTrue() {
    ReflectionTestUtils.setField(itemSubstitutionFeedIngestionService, "isPublishEnabled", true);
    Assertions.assertTrue(itemSubstitutionFeedIngestionService.isPublishEnabled());
    ReflectionTestUtils.setField(itemSubstitutionFeedIngestionService, "isPublishEnabled", false);
    Assertions.assertFalse(itemSubstitutionFeedIngestionService.isPublishEnabled());
  }
}
