/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.calendar.consumer.consumer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.nextuple.calendar.consumer.dto.CalendarFeedDto;
import com.nextuple.calendar.consumer.impl.CalendarBatchServiceImpl;
import com.nextuple.master.data.integration.consumer.MasterDataFeedConsumer;
import com.nextuple.master.data.integration.inbound.BatchRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.converter.KafkaMessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@KafkaListener(
    topics = "${master-data.calendar.topic-name}",
    groupId = "${master-data.calendar.group-id}",
    batch = "true")
public class CalendarConsumer extends MasterDataFeedConsumer<CalendarFeedDto> {

  private final CalendarBatchServiceImpl calendarBatchService;
  private final TypeReference<List<BatchRequest<CalendarFeedDto>>> calendarTypeReference =
      new TypeReference<>() {};

  @KafkaHandler
  public void consumeCalendarFeed(
      @Payload List<BatchRequest<CalendarFeedDto>> calendarFeed,
      @Headers KafkaMessageHeaders headers) {
    consumeMasterDataFeed(calendarFeed);
  }

  @Override
  public void invokeFeedProcessingService(List<BatchRequest<CalendarFeedDto>> calendarFeed) {
    calendarBatchService.processRecordsWithRetry(calendarFeed);
  }

  @Override
  public TypeReference<List<BatchRequest<CalendarFeedDto>>> getTypeReference() {
    return calendarTypeReference;
  }
}
