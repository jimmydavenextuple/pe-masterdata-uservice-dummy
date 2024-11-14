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
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import com.nextuple.common.base.PagePayload;
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
import feign.FeignException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
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
class ZoneProcessingRequestImplTest {

  @InjectMocks ZoneProcessingRequestImpl zoneProcessingRequest;

  @Spy JobsDashboardClient jobsDashboardClient;

  @InjectMocks private TestUtil testUtil;
  @Mock private FileService fileService;
  @Mock private FileMetaDataClient fileMetaDataClient;
  @Mock private PreSignedUrlInterface preSignedUrlInterface;

  @BeforeEach
  public void init() {
    ReflectionTestUtils.setField(zoneProcessingRequest, "bucketName", "bucket");
    ReflectionTestUtils.setField(zoneProcessingRequest, "storageType", "blob");
    ReflectionTestUtils.setField(zoneProcessingRequest, "maxNoOfFailedTransitEntries", 100);
  }

  @Test
  void submitJobTest() throws JobSubmissionException {
    when(jobsDashboardClient.processJobOfflineWithFileMetaDataId(
            TestUtil.ORG_ID, JobTypeEnum.UPLOAD_ZONES, TestUtil.FILE_METADATA_ID))
        .thenReturn(BaseResponse.builder().payload(testUtil.getJobResponse()).build());
    String result = zoneProcessingRequest.submitJob(TestUtil.ORG_ID, TestUtil.FILE_METADATA_ID);
    Assertions.assertEquals(TestUtil.JOB_ID, result);
  }

  @Test
  void submitJobFeignExceptionTest() {
    when(jobsDashboardClient.processJobOfflineWithFileMetaDataId(
            TestUtil.ORG_ID, JobTypeEnum.UPLOAD_ZONES, TestUtil.FILE_METADATA_ID))
        .thenThrow(FeignException.class);
    Assertions.assertThrows(
        JobSubmissionException.class,
        () -> zoneProcessingRequest.submitJob(TestUtil.ORG_ID, TestUtil.FILE_METADATA_ID));
  }

  @Test
  void submitJobExceptionTest() {
    when(jobsDashboardClient.processJobOfflineWithFileMetaDataId(
            TestUtil.ORG_ID, JobTypeEnum.UPLOAD_ZONES, TestUtil.FILE_METADATA_ID))
        .thenThrow(RuntimeException.class);
    Assertions.assertThrows(
        JobSubmissionException.class,
        () -> zoneProcessingRequest.submitJob(TestUtil.ORG_ID, TestUtil.FILE_METADATA_ID));
  }

  @Test
  void validateCSVTest() throws IOException {
    Path path = Paths.get("src", "test", "resources", "zone", "zone.csv");
    InputStream inputStream = Files.newInputStream(path);
    FileResponse response = testUtil.getFileResponse();
    response.setInputStream(inputStream);
    Assertions.assertDoesNotThrow(
        () -> zoneProcessingRequest.validate(testUtil.getGenericUploadRequest(), response));
  }

  @Test
  void validateInvalidAndEmptyCSVExceptionTest() throws IOException {
    String[][] filenames = {
      {"zoneInvalidHeader.csv", TestUtil.ZONE_INVALID_HEADER_ERROR_MESSAGE},
      {"zoneEmpty.csv", TestUtil.NO_RECORD_ERROR_MESSAGE},
      {"zoneFullEmpty.csv", TestUtil.ZONES_DATA_UPLOAD_EMPTY_INVALID_HEADERS},
      {"zoneRandomFile.csv", TestUtil.ZONES_DATA_UPLOAD_EMPTY_INVALID_HEADERS}
    };
    for (String[] file : filenames) {
      Path path = Paths.get("src", "test", "resources", "zone", file[0]);
      invalidAndEmptyCsvException(path, file[1]);
    }
  }

  private void invalidAndEmptyCsvException(Path path, String message) throws IOException {
    InputStream inputStream = Files.newInputStream(path);
    FileResponse response = testUtil.getFileResponse();
    response.setInputStream(inputStream);
    Exception ex =
        Assertions.assertThrows(
            Exception.class,
            () -> zoneProcessingRequest.validate(testUtil.getGenericUploadRequest(), response));
    Assertions.assertEquals(message, ex.getMessage());
  }

  @Test
  void validateInvalidFileTypeExceptionTest() {
    FileResponse response = testUtil.getFileResponse();
    response.setContentType("");
    Exception ex =
        Assertions.assertThrows(
            Exception.class,
            () -> zoneProcessingRequest.validate(testUtil.getGenericUploadRequest(), response));
    Assertions.assertEquals("Zones data uploaded file has invalid file type.", ex.getMessage());
  }

  @Test
  void getJobTypeTest() {
    JobTypeEnum jobTypeEnum = zoneProcessingRequest.getJobType();
    Assertions.assertEquals(JobTypeEnum.UPLOAD_ZONES, jobTypeEnum);
  }

  @Test
  void downloadErrorLogsTest() throws CommonServiceException, JobSubmissionException, IOException {
    JobDto jobDto = testUtil.createJob(TestUtil.JOB_ID, JobTypeEnum.UPLOAD_TRANSIT_TIMES, 20);
    RecordStatusDto recordStatusDto1 =
        testUtil.getJobRecordsForZone(
            "Zone data cannot be created with given carrierServiceId and orgId",
            testUtil.zoneRequestBody("source1", "dest1"));
    RecordStatusDto recordStatusDto2 =
        testUtil.getJobRecordsForZone(
            "Zone data not found", testUtil.zoneRequestBody("source2", "dest2"));

    PagePayload<RecordStatusDto> recordStatusDtoPagePayload =
        testUtil.createPagePayloadRecordStatusDto(
            List.of(recordStatusDto1, recordStatusDto2), 5, 20, 1);

    when(jobsDashboardClient.getJobRecordsByFilters(any(), any(), any(), any(), any()))
        .thenReturn(BaseResponse.builder().payload(recordStatusDtoPagePayload).build());
    doNothing().when(fileService).uploadFile(anyString(), anyString(), any());

    when(preSignedUrlInterface.downloadFileURLById(1L))
        .thenReturn(testUtil.getPreSignedUrlResponse());
    when(fileMetaDataClient.createFileMetadata(any()))
        .thenReturn(BaseResponse.builder().payload(testUtil.getFileMetaDataResponse()).build());

    PreSignedUrlResponse response =
        zoneProcessingRequest.downloadTransitTimeErrorLogs(
            jobDto, Optional.of(ApiStatusEnum.FAILURE.name()));

    Assertions.assertNotNull(response);
    Assertions.assertFalse(ObjectUtils.isEmpty(response));
  }

  @Test
  void downloadErrorLogsForLargeFilesTest()
      throws CommonServiceException, JobSubmissionException, IOException {
    JobDto jobDto = testUtil.createJob(TestUtil.JOB_ID, JobTypeEnum.UPLOAD_ZONES, 1000);
    List<RecordStatusDto> recordStatusDtos = new ArrayList<>();
    String[] destinationGeoZones = testUtil.generateGeozones(10);
    String[] sourceGeoZones = testUtil.generateGeozones(100);

    Arrays.stream(destinationGeoZones)
        .forEach(
            dest ->
                Arrays.stream(sourceGeoZones)
                    .forEach(
                        src ->
                            recordStatusDtos.add(
                                testUtil.getJobRecordsForZone(
                                    "Error message", testUtil.zoneRequestBody(src, dest)))));

    PagePayload<RecordStatusDto> recordStatusDtoPagePayload =
        testUtil.createPagePayloadRecordStatusDto(recordStatusDtos, 10, 1000, 1);

    when(jobsDashboardClient.getJobRecordsByFilters(any(), any(), any(), any(), any()))
        .thenReturn(BaseResponse.builder().payload(recordStatusDtoPagePayload).build());
    doNothing().when(fileService).uploadFile(anyString(), anyString(), any());

    when(preSignedUrlInterface.downloadFileURLById(1L))
        .thenReturn(testUtil.getPreSignedUrlResponse());
    when(fileMetaDataClient.createFileMetadata(any()))
        .thenReturn(BaseResponse.builder().payload(testUtil.getFileMetaDataResponse()).build());

    PreSignedUrlResponse response =
        zoneProcessingRequest.downloadTransitTimeErrorLogs(
            jobDto, Optional.of(ApiStatusEnum.FAILURE.name()));

    Assertions.assertNotNull(response);
    Assertions.assertFalse(ObjectUtils.isEmpty(response));
  }

  @Test
  void downloadErrorLogsTestExceptionTest() throws IOException {
    JobDto jobDto = testUtil.createJob(TestUtil.JOB_ID, JobTypeEnum.UPLOAD_ZONES, 20);
    RecordStatusDto recordStatusDto = testUtil.getJobRecordsForTransitTimes();

    PagePayload<RecordStatusDto> recordStatusDtoPagePayload =
        testUtil.createPagePayloadRecordStatusDto(List.of(recordStatusDto), 5, 20, 1);

    when(jobsDashboardClient.getJobRecordsByFilters(any(), any(), any(), any(), any()))
        .thenReturn(BaseResponse.builder().payload(recordStatusDtoPagePayload).build());

    doThrow(new RuntimeException("Error Creating the file meta"))
        .when(fileService)
        .uploadFile(anyString(), anyString(), any());

    Exception ex =
        Assertions.assertThrows(
            Exception.class,
            () ->
                zoneProcessingRequest.downloadTransitTimeErrorLogs(
                    jobDto, Optional.of(ApiStatusEnum.FAILURE.name())));
    Assertions.assertEquals("Error Creating the file meta", ex.getMessage());
  }
}
