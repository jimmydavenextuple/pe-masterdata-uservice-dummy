/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.csvdownload.service.v1.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.common.response.PreSignedUrlResponse;
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
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import feign.FeignException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
class CustomRegionProcessingRequestImplTest {

  @InjectMocks CustomRegionProcessingRequestImpl customRegionProcessingRequest;

  @Spy JobsDashboardClient jobsDashboardClient;

  @InjectMocks private TestUtil testUtil;

  @Mock FileService fileService;

  @Mock PreSignedUrlInterface preSignedUrlInterface;

  @Mock FileMetaDataClient fileMetaDataClient;

  @BeforeEach
  public void init() {
    ReflectionTestUtils.setField(customRegionProcessingRequest, "bucketName", "bucket");
    ReflectionTestUtils.setField(customRegionProcessingRequest, "storageType", "s3");
    ReflectionTestUtils.setField(
        customRegionProcessingRequest, "recordsPerPage", TestUtil.RECORDS_PER_PAGE);
    ReflectionTestUtils.setField(
        customRegionProcessingRequest, "maxNoOfPages", TestUtil.MAX_NO_OF_PAGES);
  }

  @Test
  void submitJobTest() throws JobSubmissionException {
    when(jobsDashboardClient.processJobOfflineWithFileMetaDataId(
            TestUtil.ORG_ID, JobTypeEnum.UPLOAD_CUSTOM_REGION, TestUtil.FILE_METADATA_ID))
        .thenReturn(BaseResponse.builder().payload(testUtil.getJobResponse()).build());
    String result =
        customRegionProcessingRequest.submitJob(TestUtil.ORG_ID, TestUtil.FILE_METADATA_ID);
    Assertions.assertEquals(TestUtil.JOB_ID, result);
  }

  @Test
  void submitJobFeignExceptionTest() {
    when(jobsDashboardClient.processJobOfflineWithFileMetaDataId(
            TestUtil.ORG_ID, JobTypeEnum.UPLOAD_CUSTOM_REGION, TestUtil.FILE_METADATA_ID))
        .thenThrow(FeignException.class);
    Assertions.assertThrows(
        JobSubmissionException.class,
        () -> customRegionProcessingRequest.submitJob(TestUtil.ORG_ID, TestUtil.FILE_METADATA_ID));
  }

  @Test
  void submitJobExceptionTest() {
    when(jobsDashboardClient.processJobOfflineWithFileMetaDataId(
            TestUtil.ORG_ID, JobTypeEnum.UPLOAD_CUSTOM_REGION, TestUtil.FILE_METADATA_ID))
        .thenThrow(ArithmeticException.class);
    Assertions.assertThrows(
        JobSubmissionException.class,
        () -> customRegionProcessingRequest.submitJob(TestUtil.ORG_ID, TestUtil.FILE_METADATA_ID));
  }

  @Test
  void validateCsvTest() throws IOException, CommonServiceException, CsvException {
    Path path = Paths.get("src", "test", "resources", "customRegion", "customRegion.csv");
    InputStream inputStream = Files.newInputStream(path);
    FileResponse response = testUtil.getFileResponse();
    response.setInputStream(inputStream);
    customRegionProcessingRequest.validate(testUtil.getGenericUploadRequest(), response);
  }

  @Test
  void validateInvalidHeadersExceptionTest()
      throws IOException, CommonServiceException, CsvException {

    Path path =
        Paths.get("src", "test", "resources", "customRegion", "customRegionInvalidHeader.csv");
    InputStream inputStream = Files.newInputStream(path);
    FileResponse response = testUtil.getFileResponse();
    response.setInputStream(inputStream);

    Exception ex =
        Assertions.assertThrows(
            Exception.class,
            () ->
                customRegionProcessingRequest.validate(
                    testUtil.getGenericUploadRequest(), response));
    Assertions.assertEquals(
        "Custom region data uploaded file has invalid headers.", ex.getMessage());
    Assertions.assertNotEquals(
        6, new CSVReader(new InputStreamReader(inputStream)).readAll().size());
  }

  @Test
  void validateEmptyCsvExceptionTest() throws IOException {
    Path path = Paths.get("src", "test", "resources", "customRegion", "customRegionEmpty.csv");
    FileResponse response = getFileResponseWithInputStream(path);
    Exception ex =
        Assertions.assertThrows(
            Exception.class,
            () ->
                customRegionProcessingRequest.validate(
                    testUtil.getGenericUploadRequest(), response));
    Assertions.assertEquals("No Records found in the csv", ex.getMessage());
  }

  @Test
  void validateFullEmptyCsvExceptionTest() throws IOException {
    Path path = Paths.get("src", "test", "resources", "customRegion", "customRegionFullEmpty.csv");
    FileResponse fileResponse = getFileResponseWithInputStream(path);
    Exception ex =
        Assertions.assertThrows(
            Exception.class,
            () ->
                customRegionProcessingRequest.validate(
                    testUtil.getGenericUploadRequest(), fileResponse));
    Assertions.assertEquals("No Records found in the csv", ex.getMessage());
  }

  @Test
  void validateInvalidFileTypeExceptionTest() {
    FileResponse response = testUtil.getFileResponse();
    response.setContentType("");
    Exception ex =
        Assertions.assertThrows(
            Exception.class,
            () ->
                customRegionProcessingRequest.validate(
                    testUtil.getGenericUploadRequest(), response));
    Assertions.assertEquals(
        "Custom region data uploaded file has invalid file type.", ex.getMessage());
  }

  @Test
  void downloadErrorLogs() throws CommonServiceException, JobSubmissionException, IOException {
    JobDto jobDto = testUtil.createJob(TestUtil.JOB_ID, JobTypeEnum.UPLOAD_NODE_CARRIER, 5);
    RecordStatusDto recordStatusDto = testUtil.getJobRecordsForCustomRegion();
    when(jobsDashboardClient.getJobRecordsByFilters(
            anyString(), anyString(), anyString(), any(), any()))
        .thenReturn(
            BaseResponse.builder()
                .payload(
                    testUtil.createPagePayloadRecordStatusDto(List.of(recordStatusDto), 2, 5, 1))
                .build());
    doNothing().when(fileService).uploadFile(anyString(), anyString(), any());
    when(preSignedUrlInterface.downloadFileURLById(1L))
        .thenReturn(testUtil.getPreSignedUrlResponse());
    when(fileMetaDataClient.createFileMetadata(any()))
        .thenReturn(BaseResponse.builder().payload(testUtil.getFileMetaDataResponse()).build());
    PreSignedUrlResponse preSignedUrlResponse =
        customRegionProcessingRequest.downloadErrorLogs(
            jobDto, Optional.of(ApiStatusEnum.FAILURE.name()));
    Assertions.assertNotNull(preSignedUrlResponse);
    Assertions.assertFalse(ObjectUtils.isEmpty(preSignedUrlResponse));
  }

  private FileResponse getFileResponseWithInputStream(Path path) throws IOException {
    InputStream inputStream = Files.newInputStream(path);
    FileResponse response = testUtil.getFileResponse();
    response.setInputStream(inputStream);
    return response;
  }

  @Test
  void getJobType() {
    JobTypeEnum jobTypeEnum = customRegionProcessingRequest.getJobType();
    Assertions.assertEquals(JobTypeEnum.UPLOAD_CUSTOM_REGION, jobTypeEnum);
  }
}
