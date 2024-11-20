/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.csvdownload.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.csvdownload.common.TestUtil;
import com.nextuple.jobs.framework.common.clients.JobsDashboardClient;
import com.nextuple.jobs.framework.common.domain.pojo.RecordStatusDto;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.CollectionUtils;

@ExtendWith(MockitoExtension.class)
class JobRecordServiceTest {
  @Mock private JobsDashboardClient jobsDashboardClient;
  @InjectMocks private TestUtil testUtil;
  @InjectMocks private JobsDashboardService jobsDashboardService;

  @Test
  void getJobRecordsTest() throws CommonServiceException {
    when(jobsDashboardClient.getJobRecords(anyString(), anyString(), any()))
        .thenReturn(
            BaseResponse.builder().payload(testUtil.getJobRecordsForProcessingLeadTimes()).build());
    List<RecordStatusDto> recordStatusDtoList =
        jobsDashboardService.getJobRecords(TestUtil.JOB_ID, TestUtil.ORG_ID, TestUtil.STATUS);
    Assertions.assertFalse(CollectionUtils.isEmpty(recordStatusDtoList));
  }

  @Test
  void getJobRecordEmptyListTest() {
    when(jobsDashboardClient.getJobRecords(anyString(), anyString(), any()))
        .thenReturn(BaseResponse.builder().payload(Collections.emptyList()).build());
    Exception exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                jobsDashboardService.getJobRecords(
                    TestUtil.JOB_ID, TestUtil.ORG_ID, TestUtil.STATUS));
    Assertions.assertNotNull(exception);
  }

  @Test
  void getJobRecordsNullResponseTest() {
    when(jobsDashboardClient.getJobRecords(anyString(), anyString(), any())).thenReturn(null);
    Exception exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                jobsDashboardService.getJobRecords(
                    TestUtil.JOB_ID, TestUtil.ORG_ID, TestUtil.STATUS));
    Assertions.assertNotNull(exception);
  }
}
