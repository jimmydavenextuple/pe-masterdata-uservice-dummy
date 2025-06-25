package com.nextuple.pe.webhook.service.impl;

import com.nextuple.item.substitution.consumer.dto.ItemSubstitutionFeedDto;
import com.nextuple.master.data.integration.inbound.BatchRequest;
import com.nextuple.pe.webhook.domain.dtos.MasterDataIngestionDto;
import com.nextuple.pe.webhook.domain.inbound.FeedRequest;
import com.nextuple.pe.webhook.producer.KafkaProducer;
import com.nextuple.pe.webhook.service.FeedIngestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemSubstitutionFeedIngestionService implements FeedIngestionService<MasterDataIngestionDto<ItemSubstitutionFeedDto>> {
    @Value("${master-data.vendor.topic-names:null}")
    private String itemSubstitutionFeedTopic;

    @Value("${kafka-topic-flags.master-data.item-substitution.enabled:false}")
    private boolean isPublishEnabled;

    private final KafkaProducer kafkaProducer;

    @Override
    public void publish(String orgId, FeedRequest<MasterDataIngestionDto<ItemSubstitutionFeedDto>> itemSubstitutionFeed) {
        log.debug("Inside item substitution publishing service with feed : {}", itemSubstitutionFeed);
        itemSubstitutionFeed
                .getData()
                .forEach(
                        vendorDto -> {
                            vendorDto.getPayload().setOrgId(orgId);
                            publishItemSubstitutionFeedToKafka(vendorDto);
                        });
    }

    private void publishItemSubstitutionFeedToKafka(MasterDataIngestionDto<ItemSubstitutionFeedDto> itemSubstitutionDto) {
        BatchRequest<ItemSubstitutionFeedDto> batchRequest =
                BatchRequest.<ItemSubstitutionFeedDto>builder()
                        .action(itemSubstitutionDto.getAction())
                        .producedTimestamp(itemSubstitutionDto.getProducedTimestamp())
                        .payload(itemSubstitutionDto.getPayload())
                        .build();
        kafkaProducer.publishFeedToKafka(
                batchRequest, itemSubstitutionDto.getPayload().getPrimaryItemId(), itemSubstitutionFeedTopic);
    }

    @Override
    public boolean isPublishEnabled() {
        if (Boolean.TRUE.equals(isPublishEnabled)) return true;
        log.debug("Not publishing item substitution feed messages since it is disabled.");
        return false;
    }
}
