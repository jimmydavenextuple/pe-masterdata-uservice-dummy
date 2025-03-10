package com.nextuple.pe.webhook.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.nextuple.master.data.integration.inbound.BatchRequest;
import com.nextuple.master.data.integration.outbound.BatchResponse;
import com.nextuple.pe.webhook.domain.dtos.MasterDataIngestionDto;
import com.nextuple.pe.webhook.domain.inbound.FeedRequest;
import com.nextuple.pe.webhook.service.FeedHandlingService;
import com.nextuple.transit.consumer.dto.TransferScheduleDto;
import com.nextuple.transit.consumer.impl.TransferScheduleBatchServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class TransferScheduleFeedHandlingService  extends FeedHandlingService<TransferScheduleDto> {
    private final TransferScheduleBatchServiceImpl transferScheduleBatchService;
    private final TransferScheduleIngestionService transferScheduleIngesionService;

    @Override
    public TypeReference<List<BatchRequest<TransferScheduleDto>>> getTypeReference() {
        return new TypeReference<List<BatchRequest<TransferScheduleDto>>>() {};
    }

    @Override
    public BatchResponse invokeBatchFeedImplMethod(List<BatchRequest<TransferScheduleDto>> transferScheduleBatchRequests) {
        return transferScheduleBatchService.processRecordsWithoutRetry(transferScheduleBatchRequests);
    }

    @Override
    public void populateOrgId(List<BatchRequest<TransferScheduleDto>> batchRequests, String orgId) {
        batchRequests.forEach(transferScheduleBatchRequest -> transferScheduleBatchRequest.getPayload().setOrgId(orgId));
    }

    @Override
    public TypeReference<FeedRequest<MasterDataIngestionDto<TransferScheduleDto>>> getTypeReferenceForPublishing() {
        return new TypeReference<FeedRequest<MasterDataIngestionDto<TransferScheduleDto>>>() {};
    }

    @Override
    public void publishFeedToKafka(String orgId, FeedRequest<MasterDataIngestionDto<TransferScheduleDto>> transferScheduleBatchRequest) {
        transferScheduleIngesionService.publishFeedToKafka(orgId,transferScheduleBatchRequest);
    }
}
