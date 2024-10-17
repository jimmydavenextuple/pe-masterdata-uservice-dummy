/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.jobs.framework.common.utils;

import com.nextuple.common.constants.CommonConstants;
import com.nextuple.common.util.JsonUtil;
import com.nextuple.jobs.framework.common.domain.enums.RecordDataTypeEnum;
import com.nextuple.jobs.framework.common.domain.pojo.JobDto;
import com.nextuple.jobs.framework.common.domain.pojo.RecordDto;
import com.nextuple.jobs.framework.common.domain.pojo.RecordInputDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

@Slf4j
public class KafkaUtil {

  private KafkaUtil() {
    // Use static methods
  }

  public static RecordDto createKafkaRecord(
      int recordId, String data, JobDto jobDto, RecordDataTypeEnum fileType) {
    return RecordDto.builder()
        .orgId(jobDto.getOrgId())
        .recordId(recordId)
        .recordData(data)
        .inputs(parseRecordInputs(data, jobDto.getTotalRecords()))
        .jobId(jobDto.getJobId())
        .jobType(String.valueOf(jobDto.getJobType()))
        .totalRecords(jobDto.getTotalRecords())
        .recordType(fileType)
        .build();
  }

  private static RecordInputDto parseRecordInputs(String data, int totalRecords) {
    var recordInput = JsonUtil.convertToObject(data, RecordInputDto.class);
    recordInput.put("retryCount", totalRecords);
    return recordInput;
  }

  public static void publishDataToKafka(
      Message<RecordDto> message, KafkaTemplate<String, RecordDto> kafkaTemplate) {
    try {
      log.debug("Publishing message to kafka");
      kafkaTemplate
          .send(message)
          .whenComplete(
              (sr, ex) -> {
                if (ex != null) {
                  log.error("JobService::publishDataToKafka():failed to publish record dto", ex);
                }
              });
    } catch (Exception e) {
      log.error("JobService::publishDataToKafka():failed to publish record dto", e);
    }
  }

  public static Message<RecordDto> createKafkaMessage(
      RecordDto recordDto, String dashboardProducerName, String userId) {
    return MessageBuilder.withPayload(recordDto)
        .setHeader(KafkaHeaders.TOPIC, dashboardProducerName)
        .setHeader(CommonConstants.HEADER_USER, userId)
        .build();
  }
}
