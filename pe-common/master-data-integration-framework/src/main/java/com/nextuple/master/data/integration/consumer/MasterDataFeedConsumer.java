/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.master.data.integration.consumer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nextuple.common.context.Logger;
import com.nextuple.common.context.LoggerFactory;
import com.nextuple.master.data.integration.inbound.BatchRequest;
import java.util.Date;
import java.util.List;

public abstract class MasterDataFeedConsumer<T> {
  private final ObjectMapper objectMapper = new ObjectMapper();
  private static final Logger logger = LoggerFactory.getLogger(MasterDataFeedConsumer.class);

  public void consumeMasterDataFeed(List<BatchRequest<T>> feedRequest) {
    logger.info("Inside master data consumer feed with feed: {} ", feedRequest);
    Date receivedDate = new Date();
    feedRequest.forEach(nodeFeedRequest -> nodeFeedRequest.setReceivedTimestamp(receivedDate));
    TypeReference<List<BatchRequest<T>>> typeReference = getTypeReference();
    List<BatchRequest<T>> feedList = objectMapper.convertValue(feedRequest, typeReference);
    invokeFeedProcessingService(feedList);
  }

  public abstract void invokeFeedProcessingService(List<BatchRequest<T>> feedList);

  public abstract TypeReference<List<BatchRequest<T>>> getTypeReference();
}
