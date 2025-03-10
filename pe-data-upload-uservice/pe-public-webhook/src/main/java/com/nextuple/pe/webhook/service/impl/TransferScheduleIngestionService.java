package com.nextuple.pe.webhook.service.impl;

import com.nextuple.master.data.integration.inbound.BatchRequest;
import com.nextuple.pe.webhook.domain.dtos.MasterDataIngestionDto;
import com.nextuple.pe.webhook.domain.inbound.FeedRequest;
import com.nextuple.pe.webhook.producer.KafkaProducer;
import com.nextuple.pe.webhook.service.FeedIngestionService;
import com.nextuple.transit.consumer.dto.TransferScheduleDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransferScheduleIngestionService
    implements FeedIngestionService<MasterDataIngestionDto<TransferScheduleDto>> {

  @Value("${master-data.transfer-schedule.topic-names:null}")
  private String transferScheduleFeedTopic;

  @Value("${kafka-topic-flags.master-data.transfer-schedule.enabled:false}")
  private boolean isPublishEnabled;

  private final KafkaProducer kafkaProducer;

  @Override
  public boolean isPublishEnabled() {
    if (Boolean.TRUE.equals(isPublishEnabled)) return true;
    log.warn("Not publishing transfer schedule feed messages since it is disabled.");
    return false;
  }

  @Override
  public void publish(
      String orgId, FeedRequest<MasterDataIngestionDto<TransferScheduleDto>> transferScheduleFeed) {
    log.debug("Inside transfer schedule publishing service with feed : {}", transferScheduleFeed);

    transferScheduleFeed
        .getData()
        .forEach(
            transferScheduleDto -> {
              transferScheduleDto.getPayload().setOrgId(orgId);
              BatchRequest<TransferScheduleDto> batchRequest = new BatchRequest<>();
              batchRequest.setAction(transferScheduleDto.getAction());
              batchRequest.setProducedTimestamp(transferScheduleDto.getProducedTimestamp());
              batchRequest.setPayload(transferScheduleDto.getPayload());
              kafkaProducer.publishFeedToKafka(
                  // find way to uniquely identify a transfer schedule
                  batchRequest,
                  transferScheduleDto.getPayload().getRule(),
                  transferScheduleFeedTopic);
            });
  }
}
