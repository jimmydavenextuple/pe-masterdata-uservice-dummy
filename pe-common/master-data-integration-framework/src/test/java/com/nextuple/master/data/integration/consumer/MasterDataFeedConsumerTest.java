/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.master.data.integration.consumer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.nextuple.common.enums.ActionEnum;
import com.nextuple.common.util.DateUtil;
import com.nextuple.master.data.integration.inbound.BatchRequest;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MasterDataFeedConsumerTest {
  @Test
  @DisplayName("When master data feed is consumed successfully")
  void consumeMasterDataFeedTest() {
    MasterDataFeedConsumer<Object> masterDataFeedConsumer =
        new MasterDataFeedConsumer<>() {
          @Override
          public TypeReference<List<BatchRequest<Object>>> getTypeReference() {
            return new TypeReference<List<BatchRequest<Object>>>() {};
          }

          @Override
          public void invokeFeedProcessingService(List<BatchRequest<Object>> batchRequest) {}
        };

    BatchRequest<Object> batchRequest1 = new BatchRequest<>();
    batchRequest1.setAction(ActionEnum.CREATE);
    batchRequest1.setRecordNo(1);
    batchRequest1.setProducedTimestamp(DateUtil.addDaysToDate(new Date(), 1));

    BatchRequest<Object> batchRequest2 = new BatchRequest<>();
    batchRequest2.setAction(ActionEnum.CREATE);
    batchRequest2.setRecordNo(2);
    Assertions.assertDoesNotThrow(
        () -> masterDataFeedConsumer.consumeMasterDataFeed(List.of(batchRequest1, batchRequest2)));
  }
}
