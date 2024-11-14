/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.csvdownload.service.v1.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.common.response.PreSignedUrlResponse;
import com.nextuple.csvdownload.common.inbound.GenericUploadRequest;
import com.nextuple.csvdownload.exception.JobSubmissionException;
import com.nextuple.csvdownload.util.TestUtil;
import com.nextuple.jobs.framework.common.clients.FileMetaDataClient;
import com.nextuple.jobs.framework.common.clients.JobsDashboardClient;
import com.nextuple.jobs.framework.common.domain.enums.ApiStatusEnum;
import com.nextuple.jobs.framework.common.domain.enums.JobTypeEnum;
import com.nextuple.jobs.framework.common.domain.outbound.FileResponse;
import com.nextuple.jobs.framework.common.domain.pojo.JobDto;
import com.nextuple.jobs.framework.common.domain.pojo.RecordStatusDto;
import com.nextuple.jobs.framework.common.service.FileService;
import com.nextuple.jobs.framework.common.service.PreSignedUrlInterface;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.ObjectUtils;

@ExtendWith(MockitoExtension.class)
class TransitBufferProcessingRequestImplTest {

  @InjectMocks TransitBufferProcessingRequestImpl transitBufferProcessingRequest;
  @Spy JobsDashboardClient jobsDashboardClient;
  @InjectMocks private TestUtil testUtil;
  @Mock FileService fileService;
  @Mock PreSignedUrlInterface preSignedUrlInterface;
  @Mock FileMetaDataClient fileMetaDataClient;

  @BeforeEach
  public void init() {
    ReflectionTestUtils.setField(transitBufferProcessingRequest, "bucketName", "bucket");
    ReflectionTestUtils.setField(transitBufferProcessingRequest, "storageType", "s3");
    ReflectionTestUtils.setField(
        transitBufferProcessingRequest, "recordsPerPage", TestUtil.RECORDS_PER_PAGE);
    ReflectionTestUtils.setField(
        transitBufferProcessingRequest, "maxNoOfPages", TestUtil.MAX_NO_OF_PAGES);
  }

  @Test
  void getModuleType() {
    String type = transitBufferProcessingRequest.getModuleType();
    Assertions.assertFalse(ObjectUtils.isEmpty(type));
  }

  @Test
  void submitJob() throws JobSubmissionException {
    String response = transitBufferProcessingRequest.submitJob(TestUtil.JOB_ID, 1L);
    Assertions.assertNull(response);
  }

  @Test
  void validate() {
    Assertions.assertDoesNotThrow(
        () ->
            transitBufferProcessingRequest.validate(
                new GenericUploadRequest(), new FileResponse()));
  }

  @Test
  void downloadErrorLogs() throws CommonServiceException, JobSubmissionException, IOException {
    JobDto jobDto =
        testUtil.createJob(TestUtil.JOB_ID, JobTypeEnum.UPLOAD_POSTAL_CODE_TIMEZONE, 20);
    RecordStatusDto recordStatusDto = testUtil.getRecordStatusDtoForTransitBufferUpload();
    when(jobsDashboardClient.getJobRecordsByFilters(
            anyString(), anyString(), anyString(), anyInt(), anyInt()))
        .thenReturn(
            BaseResponse.builder()
                .payload(
                    testUtil.createPagePayloadRecordStatusDto(List.of(recordStatusDto), 5, 20, 1))
                .build());
    doNothing().when(fileService).uploadFile(anyString(), anyString(), any());
    when(preSignedUrlInterface.downloadFileURLById(1L))
        .thenReturn(testUtil.getPreSignedUrlResponse());
    when(fileMetaDataClient.createFileMetadata(any()))
        .thenReturn(BaseResponse.builder().payload(testUtil.getFileMetaDataResponse()).build());

    PreSignedUrlResponse preSignedUrlResponse =
        transitBufferProcessingRequest.downloadErrorLogs(
            jobDto, Optional.of(ApiStatusEnum.FAILURE.name()));

    Assertions.assertNotNull(preSignedUrlResponse);
    Assertions.assertFalse(ObjectUtils.isEmpty(preSignedUrlResponse));
  }

  @Test
  void getJobType() {
    JobTypeEnum jobTypeEnum = transitBufferProcessingRequest.getJobType();
    Assertions.assertEquals(JobTypeEnum.TRANSIT_BUFFER_REQUEST, jobTypeEnum);
  }
}
