/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.jobs.consumers.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.jobs.consumers.common.TestUtil;
import com.nextuple.jobs.consumers.exception.PublishJobEventException;
import com.nextuple.jobs.framework.common.domain.enums.ApiStatusEnum;
import com.nextuple.jobs.framework.common.domain.enums.JobTypeEnum;
import com.nextuple.jobs.framework.common.domain.pojo.JobDetailsDto;
import com.nextuple.jobs.framework.common.domain.pojo.RecordStatusDto;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;
import org.springframework.test.util.ReflectionTestUtils;

class PublishJobEventServiceTest {

  @InjectMocks private PublishJobEventService publishJobEventService;

  @InjectMocks private TestUtil testUtil;

  @Mock private KafkaTemplate<String, Object> kafkaTemplate;

  @BeforeEach
  public void init() {
    MockitoAnnotations.openMocks(this);
    //    MockMvcBuilders.standaloneSetup(publishJobEventService).build();
    ReflectionTestUtils.setField(publishJobEventService, "resultPublishTopicName", "topicName");
  }

  @Test
  void publishJobRecordException() {
    RecordStatusDto recordStatusDto =
        testUtil.createRecordStatus(
            TestUtil.JOB_ID,
            TestUtil.ORG_ID,
            ApiStatusEnum.SUCCESS,
            HttpStatus.OK,
            "user123",
            JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES,
            3);

    when(kafkaTemplate.send("topicName", recordStatusDto)).thenThrow(new RuntimeException());

    PublishJobEventException exception =
        assertThrows(
            PublishJobEventException.class,
            () -> publishJobEventService.publishJobRecord(recordStatusDto));

    Assertions.assertEquals(
        "Exception while publishing the job record", exception.getMessage(), "Expected Error");

    Assertions.assertEquals(recordStatusDto.getJobId(), exception.getJobId(), "Expected Error");
  }

  @Test
  void publishJobCompletionEvent() {
    JobDetailsDto jobDetailsDto = testUtil.getJobDetailsDto();

    CompletableFuture<SendResult<String, Object>> future = Mockito.mock(CompletableFuture.class);
    when(future.whenComplete(any())).thenReturn(null);
    when(kafkaTemplate.send(any(Message.class))).thenReturn(future);

    Assertions.assertDoesNotThrow(
        () -> publishJobEventService.publishJobDetailsEvent(jobDetailsDto));

    verify(kafkaTemplate, times(1)).send(any(Message.class));
  }

  @Test
  void publishJobCompletionEventException() {
    JobDetailsDto jobDetailsDto = testUtil.getJobDetailsDto();

    when(kafkaTemplate.send(any(Message.class)))
        .thenThrow(new RuntimeException("Error while publishing job completion event"));

    Exception exception =
        Assertions.assertThrows(
            PublishJobEventException.class,
            () -> publishJobEventService.publishJobDetailsEvent(jobDetailsDto));
    Assertions.assertNotNull(exception);

    verify(kafkaTemplate, times(1)).send(any(Message.class));
  }
}
