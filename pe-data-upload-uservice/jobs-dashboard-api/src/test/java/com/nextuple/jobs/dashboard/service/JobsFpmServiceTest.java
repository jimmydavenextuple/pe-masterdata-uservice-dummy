/*
 *
 *  * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *  *
 *  * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 *  * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 *
 */

package com.nextuple.jobs.dashboard.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.common.base.PagePayload;
import com.nextuple.jobs.dashboard.common.TestUtil;
import com.nextuple.jobs.framework.common.domain.enums.JobStatusEnum;
import com.nextuple.jobs.framework.common.domain.outbound.JobResponse;
import com.nextuple.jobs.framework.common.domain.outbound.JobResponseForNotification;
import com.nextuple.jobs.framework.common.domain.pojo.JobFilters;
import com.nextuple.plt.client.FPMServiceClient;
import com.nextuple.plt.model.FeedFilters;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

class JobsFpmServiceTest {

  public static final String ITEM_BUFFER = "itembuffer";
  @InjectMocks private JobsFpmService jobsFpmService;

  @Mock private FPMServiceClient fpmServiceClientMock;

  @Mock private JobService jobServiceMock;

  @BeforeEach
  void init() {
    MockitoAnnotations.openMocks(this);
    ReflectionTestUtils.setField(jobsFpmService, "fpmJobTypes", "itembuffer,itemmaster");
  }

  @Test
  @DisplayName("Happy path : getJobResponseForNotificationPagePayload")
  void testGetJobResponseForNotificationPagePayload() throws Exception {
    JobFilters jobFilters = new JobFilters();
    int requiredPageNo = 1;
    int requiredPageSize = 10;

    PagePayload<JobResponse> jobServiceResponse = new PagePayload<>();
    JobResponse jobResponse = new JobResponse();
    jobResponse.setStatus(JobStatusEnum.COMPLETED);
    jobResponse.setProcessingStartedAt(new Date(1000));
    jobServiceResponse.setData(List.of(jobResponse));
    when(jobServiceMock.getJobsByJobInfo(
            TestUtil.ORG_ID,
            jobFilters.getJobType(),
            jobFilters.getDays(),
            jobFilters.getSortBy(),
            jobFilters.getSortOrder(),
            requiredPageNo,
            requiredPageSize))
        .thenReturn(jobServiceResponse);

    com.nextuple.plt.model.PagePayload<com.nextuple.plt.domain.outbound.JobResponse>
        fpmServiceResponse = new com.nextuple.plt.model.PagePayload<>();
    com.nextuple.plt.domain.outbound.JobResponse jobResponseFpm =
        new com.nextuple.plt.domain.outbound.JobResponse();
    jobResponseFpm.setFeedType("itembuffer");
    jobResponseFpm.setProcessingStartedAt(new Date(1500));
    fpmServiceResponse.setData(List.of(jobResponseFpm));
    FeedFilters feedFilters = new FeedFilters();
    feedFilters.setPageSize(Optional.of(requiredPageSize));
    feedFilters.setPageNo(Optional.of(requiredPageNo));
    when(fpmServiceClientMock.getJobsByFilter(TestUtil.ORG_ID, feedFilters))
        .thenReturn(ResponseEntity.ok(fpmServiceResponse));

    PagePayload<JobResponseForNotification> result =
        jobsFpmService.getJobResponseForNotificationPagePayload(
            TestUtil.ORG_ID, jobFilters, requiredPageNo, requiredPageSize);
    assertNotNull(result);
    assertEquals(JobStatusEnum.COMPLETED, result.getData().getLast().getStatus());
    assertEquals("itembuffer", result.getData().getFirst().getJobType());
    verify(fpmServiceClientMock, times(1)).getJobsByFilter(any(), any());
  }

  @Test
  @DisplayName(
      "Exception : getJobResponseForNotificationPagePayload - getJobsByFilter returns null")
  void testGetJobResponseForNotificationPagePayloadException() throws Exception {
    JobFilters jobFilters = new JobFilters();
    int requiredPageNo = 1;
    int requiredPageSize = 10;

    PagePayload<JobResponse> jobServiceResponse = new PagePayload<>();
    JobResponse jobResponse = new JobResponse();
    jobResponse.setStatus(JobStatusEnum.COMPLETED);
    jobResponse.setProcessingStartedAt(new Date(1000));
    jobServiceResponse.setData(List.of(jobResponse));
    when(jobServiceMock.getJobsByJobInfo(
            TestUtil.ORG_ID,
            jobFilters.getJobType(),
            jobFilters.getDays(),
            jobFilters.getSortBy(),
            jobFilters.getSortOrder(),
            requiredPageNo,
            requiredPageSize))
        .thenReturn(jobServiceResponse);

    com.nextuple.plt.model.PagePayload<com.nextuple.plt.domain.outbound.JobResponse>
        fpmServiceResponse = new com.nextuple.plt.model.PagePayload<>();
    com.nextuple.plt.domain.outbound.JobResponse jobResponseFpm =
        new com.nextuple.plt.domain.outbound.JobResponse();
    jobResponseFpm.setFeedType("itembuffer");
    jobResponseFpm.setProcessingStartedAt(new Date(1500));
    fpmServiceResponse.setData(List.of(jobResponseFpm));
    FeedFilters feedFilters = new FeedFilters();
    feedFilters.setPageSize(Optional.of(requiredPageSize));
    feedFilters.setPageNo(Optional.of(requiredPageNo));
    when(fpmServiceClientMock.getJobsByFilter(TestUtil.ORG_ID, feedFilters))
        .thenReturn(ResponseEntity.ok(null));

    PagePayload<JobResponseForNotification> result =
        jobsFpmService.getJobResponseForNotificationPagePayload(
            TestUtil.ORG_ID, jobFilters, requiredPageNo, requiredPageSize);
    assertNotNull(result);
    assertEquals(null, result.getData().getFirst().getJobType());
  }

  @Test
  @DisplayName(
      "Happy path : getJobResponseForNotificationPagePayload where the feedType is other than itembuffer")
  void testGetJobResponseForNotificationPagePayloadDifferentFeedType() throws Exception {
    JobFilters jobFilters = new JobFilters();
    int requiredPageNo = 1;
    int requiredPageSize = 10;

    PagePayload<JobResponse> jobServiceResponse = new PagePayload<>();
    JobResponse jobResponse = new JobResponse();
    jobResponse.setStatus(JobStatusEnum.COMPLETED);
    jobResponse.setProcessingStartedAt(new Date(1000));
    jobServiceResponse.setData(List.of(jobResponse));
    when(jobServiceMock.getJobsByJobInfo(
            TestUtil.ORG_ID,
            jobFilters.getJobType(),
            jobFilters.getDays(),
            jobFilters.getSortBy(),
            jobFilters.getSortOrder(),
            requiredPageNo,
            requiredPageSize))
        .thenReturn(jobServiceResponse);

    com.nextuple.plt.model.PagePayload<com.nextuple.plt.domain.outbound.JobResponse>
        fpmServiceResponse = new com.nextuple.plt.model.PagePayload<>();
    com.nextuple.plt.domain.outbound.JobResponse jobResponseFpm =
        new com.nextuple.plt.domain.outbound.JobResponse();
    jobResponseFpm.setFeedType("invalid");
    jobResponseFpm.setProcessingStartedAt(new Date(1500));
    fpmServiceResponse.setData(List.of(jobResponseFpm));
    FeedFilters feedFilters = new FeedFilters();
    feedFilters.setPageSize(Optional.of(requiredPageSize));
    feedFilters.setPageNo(Optional.of(requiredPageNo));
    when(fpmServiceClientMock.getJobsByFilter(TestUtil.ORG_ID, feedFilters))
        .thenReturn(ResponseEntity.ok(fpmServiceResponse));

    PagePayload<JobResponseForNotification> result =
        jobsFpmService.getJobResponseForNotificationPagePayload(
            TestUtil.ORG_ID, jobFilters, requiredPageNo, requiredPageSize);
    assertNotNull(result);
    assertEquals(JobStatusEnum.COMPLETED, result.getData().getLast().getStatus());
    assertEquals(null, result.getData().getFirst().getJobType());
    verify(fpmServiceClientMock, times(1)).getJobsByFilter(any(), any());
  }

  @Test
  @DisplayName(
      "Happy path : getJobResponseForNotificationPagePayload when some jobs are in submitted status")
  void testGetJobResponseForNotificationPagePayloadWhenJobsInSubmittedStatus() throws Exception {
    JobFilters jobFilters = new JobFilters();
    int requiredPageNo = 1;
    int requiredPageSize = 10;

    PagePayload<JobResponse> jobServiceResponse = new PagePayload<>();
    JobResponse jobResponse1 = new JobResponse();
    jobResponse1.setStatus(JobStatusEnum.COMPLETED);
    jobResponse1.setProcessingStartedAt(new Date(1000));

    JobResponse jobResponse2 = new JobResponse();
    jobResponse2.setJobType("NODE_UPLOAD");
    jobResponse2.setStatus(JobStatusEnum.SUBMITTED);
    jobResponse2.setProcessingStartedAt(null);
    jobServiceResponse.setData(List.of(jobResponse1, jobResponse2));
    when(jobServiceMock.getJobsByJobInfo(
            TestUtil.ORG_ID,
            jobFilters.getJobType(),
            jobFilters.getDays(),
            jobFilters.getSortBy(),
            jobFilters.getSortOrder(),
            requiredPageNo,
            requiredPageSize))
        .thenReturn(jobServiceResponse);

    com.nextuple.plt.model.PagePayload<com.nextuple.plt.domain.outbound.JobResponse>
        fpmServiceResponse = new com.nextuple.plt.model.PagePayload<>();
    com.nextuple.plt.domain.outbound.JobResponse jobResponseFpm1 =
        new com.nextuple.plt.domain.outbound.JobResponse();
    jobResponseFpm1.setFeedType("itembuffer");
    jobResponseFpm1.setProcessingStartedAt(new Date(1500));
    fpmServiceResponse.setData(List.of(jobResponseFpm1));

    com.nextuple.plt.domain.outbound.JobResponse jobResponseFpm2 =
        new com.nextuple.plt.domain.outbound.JobResponse();
    jobResponseFpm2.setFeedType(ITEM_BUFFER);
    jobResponseFpm2.setProcessingStartedAt(null);
    jobResponseFpm2.setStatus(com.nextuple.plt.domain.enums.JobStatusEnum.SUBMITTED);

    fpmServiceResponse.setData(List.of(jobResponseFpm1, jobResponseFpm2));

    FeedFilters feedFilters = new FeedFilters();
    feedFilters.setPageSize(Optional.of(requiredPageSize));
    feedFilters.setPageNo(Optional.of(requiredPageNo));
    when(fpmServiceClientMock.getJobsByFilter(TestUtil.ORG_ID, feedFilters))
        .thenReturn(ResponseEntity.ok(fpmServiceResponse));

    PagePayload<JobResponseForNotification> result =
        jobsFpmService.getJobResponseForNotificationPagePayload(
            TestUtil.ORG_ID, jobFilters, requiredPageNo, requiredPageSize);

    assertNotNull(result);
    assertEquals(4, result.getData().size());
    assertEquals(JobStatusEnum.SUBMITTED, result.getData().getFirst().getStatus());
    assertEquals(JobStatusEnum.COMPLETED, result.getData().getLast().getStatus());
    assertEquals("NODE_UPLOAD", result.getData().getFirst().getJobType());
    verify(fpmServiceClientMock, times(1)).getJobsByFilter(any(), any());
  }
}
