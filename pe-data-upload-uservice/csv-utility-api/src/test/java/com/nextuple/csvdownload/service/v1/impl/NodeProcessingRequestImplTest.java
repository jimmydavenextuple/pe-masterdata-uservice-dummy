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

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.common.response.PreSignedUrlResponse;
import com.nextuple.csvdownload.exception.JobSubmissionException;
import com.nextuple.csvdownload.util.TestUtil;
import com.nextuple.dataupload.common.config.TenantDatabaseConfig;
import com.nextuple.dataupload.common.utils.v1.DynamicCsvHeadersValidation;
import com.nextuple.jobs.framework.common.clients.FileMetaDataClient;
import com.nextuple.jobs.framework.common.clients.JobsDashboardClient;
import com.nextuple.jobs.framework.common.domain.enums.ApiStatusEnum;
import com.nextuple.jobs.framework.common.domain.enums.JobTypeEnum;
import com.nextuple.jobs.framework.common.domain.outbound.FileResponse;
import com.nextuple.jobs.framework.common.domain.pojo.JobDto;
import com.nextuple.jobs.framework.common.domain.pojo.RecordStatusDto;
import com.nextuple.jobs.framework.common.service.FileService;
import com.nextuple.jobs.framework.common.service.PreSignedUrlInterface;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;
import feign.FeignException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.ObjectUtils;

@ExtendWith(MockitoExtension.class)
class NodeProcessingRequestImplTest {
  @InjectMocks NodeProcessingRequestImpl nodeProcessingRequest;
  @Mock TenantDatabaseConfig tenantDatabaseConfig;

  @Spy JobsDashboardClient jobsDashboardClient;

  @InjectMocks private TestUtil testUtil;

  @Mock private FileService fileService;

  @Mock private FileMetaDataClient fileMetaDataClient;
  @Mock private PreSignedUrlInterface preSignedUrlInterface;
  @Mock private DynamicCsvHeadersValidation csvValidation;

  @BeforeEach
  public void init() {
    ReflectionTestUtils.setField(nodeProcessingRequest, "bucketName", "bucket");
    ReflectionTestUtils.setField(nodeProcessingRequest, "storageType", "s3");
    ReflectionTestUtils.setField(
        nodeProcessingRequest, "recordsPerPage", TestUtil.RECORDS_PER_PAGE);
    ReflectionTestUtils.setField(nodeProcessingRequest, "maxNoOfPages", TestUtil.MAX_NO_OF_PAGES);
    ReflectionTestUtils.setField(nodeProcessingRequest, "csvValidation", csvValidation);
    ReflectionTestUtils.setField(
        nodeProcessingRequest, "tenantDatabaseConfig", tenantDatabaseConfig);
  }

  @Test
  void submitJobTest() throws JobSubmissionException {
    when(jobsDashboardClient.processJobOfflineWithFileMetaDataId(
            TestUtil.ORG_ID, JobTypeEnum.UPLOAD_NODES, TestUtil.FILE_METADATA_ID))
        .thenReturn(BaseResponse.builder().payload(testUtil.getJobResponse()).build());
    String result = nodeProcessingRequest.submitJob(TestUtil.ORG_ID, TestUtil.FILE_METADATA_ID);
    Assertions.assertEquals(TestUtil.JOB_ID, result);
  }

  @Test
  void submitJobFeignExceptionTest() {
    when(jobsDashboardClient.processJobOfflineWithFileMetaDataId(
            TestUtil.ORG_ID, JobTypeEnum.UPLOAD_NODES, TestUtil.FILE_METADATA_ID))
        .thenThrow(FeignException.class);
    Assertions.assertThrows(
        JobSubmissionException.class,
        () -> nodeProcessingRequest.submitJob(TestUtil.ORG_ID, TestUtil.FILE_METADATA_ID));
  }

  @Test
  void submitJobExceptionTest() {
    when(jobsDashboardClient.processJobOfflineWithFileMetaDataId(
            TestUtil.ORG_ID, JobTypeEnum.UPLOAD_NODES, TestUtil.FILE_METADATA_ID))
        .thenThrow(ArithmeticException.class);
    Assertions.assertThrows(
        JobSubmissionException.class,
        () -> nodeProcessingRequest.submitJob(TestUtil.ORG_ID, TestUtil.FILE_METADATA_ID));
  }

  @Test
  void validateCsvTest() throws IOException, CommonServiceException, CsvException {
    Path path = Paths.get("src", "test", "resources", "node", "node.csv");
    InputStream inputStream = Files.newInputStream(path);
    FileResponse response = testUtil.getFileResponse();
    response.setInputStream(inputStream);
    nodeProcessingRequest.validate(testUtil.getGenericUploadRequest(), response);
  }

  @Test
  void validateInvalidHeadersExceptionTest() throws IOException, CommonServiceException {

    Path path = Paths.get("src", "test", "resources", "node", "nodeInvalidHeader.csv");
    InputStream inputStream = Files.newInputStream(path);
    FileResponse response = testUtil.getFileResponse();
    response.setInputStream(inputStream);

    doThrow(
            new CommonServiceException(
                "Node data uploaded file has invalid headers.",
                HttpStatus.BAD_REQUEST,
                0x2777,
                new HashMap<>()))
        .when(csvValidation)
        .validateCSVHeaders(any(String[].class), any(), any(), any());

    Exception ex =
        Assertions.assertThrows(
            Exception.class,
            () -> nodeProcessingRequest.validate(testUtil.getGenericUploadRequest(), response));
    Assertions.assertEquals("Node data uploaded file has invalid headers.", ex.getMessage());
  }

  @Test
  void validateEmptyCsvExceptionTest() throws IOException {
    Path path = Paths.get("src", "test", "resources", "node", "nodeEmpty.csv");
    InputStream inputStream = Files.newInputStream(path);
    FileResponse response = testUtil.getFileResponse();
    response.setInputStream(inputStream);
    Exception ex =
        Assertions.assertThrows(
            Exception.class,
            () -> nodeProcessingRequest.validate(testUtil.getGenericUploadRequest(), response));
    Assertions.assertEquals("No Records found in the csv", ex.getMessage());
  }

  @Test
  void validateInvalidFileTypeExceptionTest() {
    FileResponse response = testUtil.getFileResponse();
    response.setContentType("");
    Exception ex =
        Assertions.assertThrows(
            Exception.class,
            () -> nodeProcessingRequest.validate(testUtil.getGenericUploadRequest(), response));
    Assertions.assertEquals("Node data uploaded file has invalid file type.", ex.getMessage());
  }

  @Test
  void downloadErrorLogsTest1() throws CommonServiceException, JobSubmissionException, IOException {
    JobDto jobDto = testUtil.createJob(TestUtil.JOB_ID, JobTypeEnum.UPLOAD_NODES, 5);
    RecordStatusDto recordStatusDto = testUtil.getJobRecordsForNodes();
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
    when(tenantDatabaseConfig.getCurrentTenantServiceOptions())
        .thenReturn(new String[] {"sdndEligible", "expressEligible", "standardEligbile"});
    PreSignedUrlResponse preSignedUrlResponse =
        nodeProcessingRequest.downloadErrorLogs(jobDto, Optional.of(ApiStatusEnum.FAILURE.name()));
    Assertions.assertNotNull(preSignedUrlResponse);
    Assertions.assertFalse(ObjectUtils.isEmpty(preSignedUrlResponse));
    Assertions.assertEquals("URL", preSignedUrlResponse.getSignedURL());
  }

  @Test
  void downloadErrorLogsTest2() throws CommonServiceException, JobSubmissionException, IOException {
    JobDto jobDto = testUtil.createJob(TestUtil.JOB_ID, JobTypeEnum.UPLOAD_NODES, 5);
    RecordStatusDto recordStatusDto1 = testUtil.getJobRecordsForNodes();
    RecordStatusDto recordStatusDto2 = testUtil.getJobRecordsForNodes1();
    when(jobsDashboardClient.getJobRecordsByFilters(
            anyString(), anyString(), anyString(), any(), any()))
        .thenReturn(
            BaseResponse.builder()
                .payload(
                    testUtil.createPagePayloadRecordStatusDto(
                        List.of(recordStatusDto1, recordStatusDto2), 2, 5, 1))
                .build());
    doNothing().when(fileService).uploadFile(anyString(), anyString(), any());
    when(preSignedUrlInterface.downloadFileURLById(1L))
        .thenReturn(testUtil.getPreSignedUrlResponse());
    when(fileMetaDataClient.createFileMetadata(any()))
        .thenReturn(BaseResponse.builder().payload(testUtil.getFileMetaDataResponse()).build());
    when(tenantDatabaseConfig.getCurrentTenantServiceOptions())
        .thenReturn(new String[] {"sdndEligible", "expressEligible", "standardEligbile"});
    PreSignedUrlResponse preSignedUrlResponse =
        nodeProcessingRequest.downloadErrorLogs(jobDto, Optional.of(ApiStatusEnum.FAILURE.name()));
    Assertions.assertNotNull(preSignedUrlResponse);
    Assertions.assertFalse(ObjectUtils.isEmpty(preSignedUrlResponse));
    Assertions.assertEquals("URL", preSignedUrlResponse.getSignedURL());
  }

  @Test
  @DisplayName(
      "Testing node error construction method and checking if error data is populated correctly or not")
  void constructNodeTest() throws IOException, CommonServiceException {
    Set<PosixFilePermission> posixFilePermissions = new HashSet<>();
    posixFilePermissions.add(PosixFilePermission.OWNER_READ);
    posixFilePermissions.add(PosixFilePermission.OWNER_WRITE);
    Path tempFile =
        Files.createTempFile(
            "nodes-error-download" + new Date().getTime(),
            ".csv",
            PosixFilePermissions.asFileAttribute(posixFilePermissions));
    CSVWriter csvWriter = new CSVWriter(new FileWriter(tempFile.toFile(), true));
    when(tenantDatabaseConfig.getCurrentTenantServiceOptions())
        .thenReturn(
            new String[] {"sdndEligible", "expressEligible", "bopisEligible", "nextdayEligible"});
    RecordStatusDto recordStatusDto = testUtil.getJobRecordsForNodes();
    nodeProcessingRequest.constructNodeError(csvWriter, recordStatusDto);
    csvWriter.flush();
    String[] lines = Files.readAllLines(tempFile).toArray(new String[0]);
    Assertions.assertEquals(1, lines.length);
    Assertions.assertEquals(23, lines[0].split(",").length);
    Assertions.assertEquals("\"true\"", lines[0].split(",")[18]); // service option eligibility
  }

  @Test
  void getJobType() {
    JobTypeEnum jobTypeEnum = nodeProcessingRequest.getJobType();
    Assertions.assertEquals(JobTypeEnum.UPLOAD_NODES, jobTypeEnum);
  }
}
