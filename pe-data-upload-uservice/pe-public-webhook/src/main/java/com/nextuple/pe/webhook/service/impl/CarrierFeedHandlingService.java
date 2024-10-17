/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.pe.webhook.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.nextuple.carrier.consumer.dto.CarrierFeedDto;
import com.nextuple.carrier.consumer.impl.CarrierBatchServiceImpl;
import com.nextuple.master.data.integration.inbound.BatchRequest;
import com.nextuple.master.data.integration.outbound.BatchResponse;
import com.nextuple.pe.webhook.domain.dtos.MasterDataIngestionDto;
import com.nextuple.pe.webhook.domain.inbound.FeedRequest;
import com.nextuple.pe.webhook.service.FeedHandlingService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CarrierFeedHandlingService extends FeedHandlingService<CarrierFeedDto> {
  private final CarrierBatchServiceImpl carrierBatchService;
  public final CarrierFeedIngestionService carrierFeedIngestionService;

  @Override
  public TypeReference<List<BatchRequest<CarrierFeedDto>>> getTypeReference() {
    return new TypeReference<List<BatchRequest<CarrierFeedDto>>>() {};
  }

  @Override
  public BatchResponse invokeBatchFeedImplMethod(List<BatchRequest<CarrierFeedDto>> batchRequests) {
    return carrierBatchService.processRecordsWithoutRetry(batchRequests);
  }

  @Override
  public void populateOrgId(List<BatchRequest<CarrierFeedDto>> batchRequests, String orgId) {
    batchRequests.forEach(carrierBatchRequest -> carrierBatchRequest.getPayload().setOrgId(orgId));
  }

  @Override
  public TypeReference<FeedRequest<MasterDataIngestionDto<CarrierFeedDto>>>
      getTypeReferenceForPublishing() {
    return new TypeReference<FeedRequest<MasterDataIngestionDto<CarrierFeedDto>>>() {};
  }

  @Override
  public void publishFeedToKafka(
      String orgId, FeedRequest<MasterDataIngestionDto<CarrierFeedDto>> batchRequests) {
    carrierFeedIngestionService.publishFeedToKafka(orgId, batchRequests);
  }
}
