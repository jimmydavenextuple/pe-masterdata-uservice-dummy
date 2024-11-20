/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.jobs.dashboard.service;

import static org.mockito.Mockito.*;

import com.nextuple.common.context.CurrentThreadContext;
import com.nextuple.common.util.DateUtil;
import com.nextuple.jobs.consumers.authentication.AuthService;
import com.nextuple.jobs.consumers.domain.JobDomain;
import com.nextuple.jobs.consumers.domain.entity.JobEntity;
import com.nextuple.jobs.consumers.exception.JobDomainException;
import com.nextuple.jobs.consumers.exception.PublishJobEventException;
import com.nextuple.jobs.consumers.service.PublishJobEventService;
import com.nextuple.jobs.dashboard.common.TestUtil;
import com.nextuple.jobs.dashboard.exception.JobException;
import com.nextuple.jobs.framework.common.domain.enums.JobStatusEnum;
import com.nextuple.jobs.framework.common.domain.enums.JobTypeEnum;
import com.nextuple.jobs.framework.common.domain.pojo.JobDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class ScheduledProcessorTest {

  @Mock private JobDomain jobDomain;

  @Mock private PublishJobEventService publishJobEventService;

  @InjectMocks private ScheduledProcessor scheduledProcessor;

  @InjectMocks private TestUtil testUtil;

  @Mock private AuthService authService;

  @Mock private JobService jobService;

  @BeforeEach
  void setup() {
    CurrentThreadContext.getLogContext().setTenantId("NEXTUPLE_GR");
  }

  @Test
  void processJobOffline() throws JobDomainException, JobException {
    ReflectionTestUtils.setField(scheduledProcessor, "timeRangeInHours", 24);

    JobEntity jobEntity = testUtil.createJobEntity(JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES, 5);
    JobDto jobDto = testUtil.createJob(JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES, 5);
    jobDto.setStatus(JobStatusEnum.PROCESSED);
    jobEntity.setStatus(JobStatusEnum.PROCESSING);
    String csvContents = TestUtil.CSV_CONTENTS_PROCESSING_LEAD_TIMES;
    jobEntity.setFile(csvContents.getBytes());
    jobDto.setFile(csvContents.getBytes());
    when(jobDomain.getAndUpdateJobStatusByStatus(any(), any())).thenReturn(jobEntity);

    when(jobService.processJobJsonOffline(any(), any(), any()))
        .thenReturn(testUtil.createJobResponse(JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES, 5));

    Assertions.assertDoesNotThrow(() -> scheduledProcessor.processJobOffline());
  }

  @Test
  void processJobOfflineAws() throws JobDomainException, JobException {

    JobEntity jobEntity = testUtil.createJobEntity(JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES, 5);
    JobDto jobDto = testUtil.createJob(JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES, 5);
    jobDto.setStatus(JobStatusEnum.PROCESSED);
    jobEntity.setStatus(JobStatusEnum.PROCESSING);
    String csvContents = TestUtil.CSV_CONTENTS_PROCESSING_LEAD_TIMES;
    jobEntity.setFile(csvContents.getBytes());
    jobDto.setFile(csvContents.getBytes());
    when(jobDomain.getAndUpdateJobStatusByStatus(any(), any())).thenReturn(jobEntity);

    when(jobService.processJobJsonOffline(any(), any(), any()))
        .thenReturn(testUtil.createJobResponse(JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES, 5));

    Assertions.assertDoesNotThrow(() -> scheduledProcessor.processJobOffline());
  }

  @Test
  void processJobOfflineQueuedJob() throws JobDomainException, JobException {
    ReflectionTestUtils.setField(scheduledProcessor, "timeRangeInHours", 24);

    String csvContents = TestUtil.CSV_CONTENTS_PROCESSING_LEAD_TIMES;
    JobEntity jobEntity = testUtil.createJobEntity(JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES, 5);
    jobEntity.setStatus(JobStatusEnum.PROCESSING);
    jobEntity.setFile(csvContents.getBytes());
    jobEntity.setProcessingStartedAt(DateUtil.minusHoursFromCurrentDate(25));

    JobDto jobDto = testUtil.createJob(JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES, 5);
    jobDto.setStatus(JobStatusEnum.PROCESSED);
    jobDto.setFile(csvContents.getBytes());

    when(jobDomain.getAndUpdateJobStatusByStatus(JobStatusEnum.SUBMITTED, JobStatusEnum.PROCESSING))
        .thenReturn(null);
    when(jobDomain.fetchJobRecordInTimeRange(any(), any())).thenReturn(jobEntity);
    when(jobService.processJobJsonOffline(any(), any(), any()))
        .thenReturn(testUtil.createJobResponse(JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES, 2));
    jobEntity.setStatus(JobStatusEnum.PROCESSED);

    Assertions.assertDoesNotThrow(() -> scheduledProcessor.processJobOffline());
  }

  @Test
  void processJobOfflineNoJobAvailableForProcessing() throws JobDomainException {
    ReflectionTestUtils.setField(scheduledProcessor, "timeRangeInHours", 24);
    when(jobDomain.getAndUpdateJobStatusByStatus(JobStatusEnum.SUBMITTED, JobStatusEnum.PROCESSING))
        .thenReturn(null);
    when(jobDomain.fetchJobRecordInTimeRange(any(), any())).thenReturn(null);

    Assertions.assertDoesNotThrow(() -> scheduledProcessor.processJobOffline());
  }

  @Test
  void processJobOfflineException()
      throws JobDomainException, PublishJobEventException, JobException {
    ReflectionTestUtils.setField(scheduledProcessor, "timeRangeInHours", 24);
    String csvContents = TestUtil.CSV_CONTENTS_PROCESSING_LEAD_TIMES;

    JobEntity jobEntity = testUtil.createJobEntity(JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES, 5);
    jobEntity.setStatus(JobStatusEnum.PROCESSING);
    jobEntity.setFile(csvContents.getBytes());

    JobDto jobDto = testUtil.createJob(JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES, 5);
    jobDto.setStatus(JobStatusEnum.PROCESSED);
    jobDto.setFile(csvContents.getBytes());

    doNothing().when(publishJobEventService).publishJobDetailsEvent(any());

    when(jobDomain.getAndUpdateJobStatusByStatus(any(), any())).thenReturn(jobEntity);
    when(jobService.processJobJsonOffline(any(), any(), any()))
        .thenThrow(new RuntimeException("Error while fetching and updating job"));
    jobEntity.setStatus(JobStatusEnum.FAILED);
    jobEntity.setErrorMessage("Error while fetching and updating job");
    when(jobDomain.save(jobEntity)).thenReturn(jobEntity);

    Assertions.assertDoesNotThrow(() -> scheduledProcessor.processJobOffline());
  }
}
