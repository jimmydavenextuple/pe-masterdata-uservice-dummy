package com.nextuple.pe.webhook.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.nextuple.master.data.integration.inbound.BatchRequest;
import com.nextuple.master.data.integration.outbound.BatchResponse;
import com.nextuple.pe.webhook.domain.dtos.MasterDataIngestionDto;
import com.nextuple.pe.webhook.domain.inbound.FeedRequest;
import com.nextuple.pe.webhook.service.FeedHandlingService;
import com.nextuple.vendor.consumer.dto.VendorFeedDto;
import com.nextuple.vendor.consumer.impl.VendorBatchServiceImpl;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class VendorFeedHandlingService extends FeedHandlingService<VendorFeedDto> {
  private final VendorBatchServiceImpl vendorBatchService;
  private final VendorFeedIngestionService vendorFeedIngestionService;

  @Override
  public TypeReference<List<BatchRequest<VendorFeedDto>>> getTypeReference() {
    return new TypeReference<List<BatchRequest<VendorFeedDto>>>() {};
  }

  @Override
  public BatchResponse invokeBatchFeedImplMethod(List<BatchRequest<VendorFeedDto>> batchRequests) {
    return vendorBatchService.processRecordsWithoutRetry(batchRequests);
  }

  @Override
  public void populateOrgId(List<BatchRequest<VendorFeedDto>> batchRequests, String orgId) {
    batchRequests.forEach(vendorBatchRequest -> vendorBatchRequest.getPayload().setOrgId(orgId));
  }

  @Override
  public TypeReference<FeedRequest<MasterDataIngestionDto<VendorFeedDto>>>
      getTypeReferenceForPublishing() {
    return new TypeReference<FeedRequest<MasterDataIngestionDto<VendorFeedDto>>>() {};
  }

  @Override
  public void publishFeedToKafka(
      String orgId, FeedRequest<MasterDataIngestionDto<VendorFeedDto>> batchRequests) {
    vendorFeedIngestionService.publishFeedToKafka(orgId, batchRequests);
  }
}
