/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.pe.webhook.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.nextuple.master.data.integration.inbound.BatchRequest;
import com.nextuple.master.data.integration.outbound.BatchResponse;
import com.nextuple.pe.webhook.domain.dtos.MasterDataIngestionDto;
import com.nextuple.pe.webhook.domain.inbound.FeedRequest;
import jakarta.annotation.PostConstruct;
import java.util.List;

public abstract class FeedHandlingService<T> { // NOSONAR
  private final ObjectMapper objectMapper = new ObjectMapper();

  @PostConstruct
  public void init() {
    objectMapper.registerModule(new JodaModule());
  }

  public BatchResponse processRecords(List<BatchRequest<?>> batchFeed, String orgId) {
    TypeReference<List<BatchRequest<T>>> typeReference = getTypeReference();
    List<BatchRequest<T>> batchRequests = objectMapper.convertValue(batchFeed, typeReference);
    populateOrgId(batchRequests, orgId);
    return invokeBatchFeedImplMethod(batchRequests);
  }

  public abstract TypeReference<List<BatchRequest<T>>> getTypeReference();

  public abstract BatchResponse invokeBatchFeedImplMethod(List<BatchRequest<T>> batchRequests);

  public abstract void populateOrgId(List<BatchRequest<T>> batchRequests, String orgId);

  public void publishRecords(
      FeedRequest<MasterDataIngestionDto<?>> masterDataIngestionFeedRequest, String orgId) {
    TypeReference<FeedRequest<MasterDataIngestionDto<T>>> typeReference =
        getTypeReferenceForPublishing();
    FeedRequest<MasterDataIngestionDto<T>> batchRequests =
        objectMapper.convertValue(masterDataIngestionFeedRequest, typeReference);
    publishFeedToKafka(orgId, batchRequests);
  }

  public abstract TypeReference<FeedRequest<MasterDataIngestionDto<T>>>
      getTypeReferenceForPublishing();

  public abstract void publishFeedToKafka(
      String orgId, FeedRequest<MasterDataIngestionDto<T>> batchRequests);
}
