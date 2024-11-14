/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.csvdownload.service.v1.impl;

import static com.nextuple.csvdownload.util.TestUtil.costDefinitionRequestBodyJson1;
import static com.nextuple.csvdownload.util.TestUtil.costDefinitionRequestBodyJson2;
import static com.nextuple.csvdownload.util.TestUtil.costDefinitionRequestBodyJson3;
import static com.nextuple.csvdownload.util.TestUtil.costDefinitionRequestBodyJson4;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import com.nextuple.common.base.PagePayload;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.common.response.PreSignedUrlResponse;
import com.nextuple.csvdownload.common.inbound.GenericUploadRequest;
import com.nextuple.csvdownload.exception.JobSubmissionException;
import com.nextuple.csvdownload.service.CostDefinitionService;
import com.nextuple.csvdownload.util.TestUtil;
import com.nextuple.jobs.framework.common.clients.FileMetaDataClient;
import com.nextuple.jobs.framework.common.clients.JobsConsumerClient;
import com.nextuple.jobs.framework.common.clients.JobsDashboardClient;
import com.nextuple.jobs.framework.common.domain.enums.ApiStatusEnum;
import com.nextuple.jobs.framework.common.domain.enums.JobTypeEnum;
import com.nextuple.jobs.framework.common.domain.outbound.FileResponse;
import com.nextuple.jobs.framework.common.domain.pojo.JobDto;
import com.nextuple.jobs.framework.common.domain.pojo.RecordStatusDto;
import com.nextuple.jobs.framework.common.enums.ModuleEnum;
import com.nextuple.jobs.framework.common.service.FileService;
import com.nextuple.jobs.framework.common.service.PreSignedUrlInterface;
import feign.FeignException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
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
class CostDefinitionProcessingRequestImplTest {
  @InjectMocks CostDefinitionProcessingRequestImpl costDefinitionProcessingRequest;
  @Spy JobsDashboardClient jobsDashboardClient;
  @Mock JobsConsumerClient jobsConsumerClient;
  @Mock private FileMetaDataClient fileMetaDataClient;
  @Mock PreSignedUrlInterface preSignedUrlInterface;
  @Mock FileService fileService;
  @Mock CostDefinitionService costDefinitionService;
  @InjectMocks TestUtil testUtil;

  @BeforeEach
  public void init() {
    ReflectionTestUtils.setField(costDefinitionProcessingRequest, "bucketName", "bucket");
    ReflectionTestUtils.setField(costDefinitionProcessingRequest, "storageType", "s3");
    ReflectionTestUtils.setField(
        costDefinitionProcessingRequest, "recordsPerPage", TestUtil.RECORDS_PER_PAGE);
    ReflectionTestUtils.setField(
        costDefinitionProcessingRequest, "maxNoOfPages", TestUtil.MAX_NO_OF_PAGES);
    ReflectionTestUtils.setField(costDefinitionProcessingRequest, "maxNoOfFailedTransitEntries", 7);
  }

  @Test
  void tempFilePrefix() {
    assertEquals("download-log-cost-definition", costDefinitionProcessingRequest.tempFilePrefix());
  }

  @Test
  void getModuleType() {
    assertEquals(
        ModuleEnum.COST_DEFINITION.getModuleValue(),
        costDefinitionProcessingRequest.getModuleType());
  }

  @Test
  void submitJob() throws JobSubmissionException {

    when(jobsDashboardClient.processJobOfflineWithFileMetaDataId(
            TestUtil.ORG_ID, JobTypeEnum.UPLOAD_COST_DEFINITION, TestUtil.FILE_METADATA_ID))
        .thenReturn(BaseResponse.builder().payload(testUtil.getJobResponse()).build());
    String result =
        costDefinitionProcessingRequest.submitJob(TestUtil.ORG_ID, TestUtil.FILE_METADATA_ID);
    Assertions.assertEquals(TestUtil.JOB_ID, result);
  }

  @Test
  void submitJobFeignExceptionTest() {
    when(jobsDashboardClient.processJobOfflineWithFileMetaDataId(
            TestUtil.ORG_ID, JobTypeEnum.UPLOAD_COST_DEFINITION, TestUtil.FILE_METADATA_ID))
        .thenThrow(FeignException.class);
    Assertions.assertThrows(
        JobSubmissionException.class,
        () ->
            costDefinitionProcessingRequest.submitJob(TestUtil.ORG_ID, TestUtil.FILE_METADATA_ID));
  }

  @Test
  void submitJobExceptionTest() {
    when(jobsDashboardClient.processJobOfflineWithFileMetaDataId(
            TestUtil.ORG_ID, JobTypeEnum.UPLOAD_COST_DEFINITION, TestUtil.FILE_METADATA_ID))
        .thenThrow(ArithmeticException.class);
    Assertions.assertThrows(
        JobSubmissionException.class,
        () ->
            costDefinitionProcessingRequest.submitJob(TestUtil.ORG_ID, TestUtil.FILE_METADATA_ID));
  }

  @Test
  void getJobType() {
    assertEquals(JobTypeEnum.UPLOAD_COST_DEFINITION, costDefinitionProcessingRequest.getJobType());
  }

  @Test
  void downloadErrorLogsTest() throws CommonServiceException, JobSubmissionException, IOException {
    InputStream inputStream =
        new ByteArrayInputStream(TestUtil.CSV_CONTENTS_WITHOUT_SELECTOR_CF_FOR_GRID.getBytes());
    JobDto jobDto = testUtil.createJob(TestUtil.JOB_ID, JobTypeEnum.UPLOAD_COST_DEFINITION, 2);
    RecordStatusDto recordStatusDto1 =
        testUtil.getJobRecordsForCostDefinition(
            "Invalid cost value", costDefinitionRequestBodyJson1);
    RecordStatusDto recordStatusDto2 =
        testUtil.getJobRecordsForCostDefinition(
            "Cost value details not found", costDefinitionRequestBodyJson2);

    PagePayload<RecordStatusDto> recordStatusDtoPagePayload =
        testUtil.createPagePayloadRecordStatusDto(
            List.of(recordStatusDto1, recordStatusDto2), 1, 2, 1);

    when(jobsDashboardClient.getJobRecordsByFilters(any(), any(), any(), any(), any()))
        .thenReturn(BaseResponse.builder().payload(recordStatusDtoPagePayload).build());
    doNothing().when(fileService).uploadFile(anyString(), anyString(), any());
    when(jobsConsumerClient.getJob(anyString(), anyString()))
        .thenReturn(BaseResponse.builder().payload(testUtil.getJobDto()).build());
    when(fileMetaDataClient.findFileMetadataById(any()))
        .thenReturn(testUtil.getFileMetaDataBaseResponse());
    when(fileService.getFile(anyString(), anyString()))
        .thenReturn(testUtil.getCostDefinitionFileResponse(inputStream));

    when(preSignedUrlInterface.downloadFileURLById(1L))
        .thenReturn(testUtil.getPreSignedUrlResponse());
    when(fileMetaDataClient.createFileMetadata(any()))
        .thenReturn(BaseResponse.builder().payload(testUtil.getFileMetaDataResponse()).build());

    PreSignedUrlResponse response =
        costDefinitionProcessingRequest.downloadTransitTimeErrorLogs(
            jobDto, Optional.of(ApiStatusEnum.FAILURE.name()));

    Assertions.assertNotNull(response);
    Assertions.assertFalse(ObjectUtils.isEmpty(response));
  }

  @Test
  void downloadErrorLogsTableTest()
      throws CommonServiceException, JobSubmissionException, IOException {
    InputStream inputStream =
        new ByteArrayInputStream(TestUtil.CSV_CONTENTS_WITH_OUT_SELECTOR_CF_FOR_TABLE.getBytes());
    JobDto jobDto = testUtil.createJob(TestUtil.JOB_ID, JobTypeEnum.UPLOAD_COST_DEFINITION, 2);
    RecordStatusDto recordStatusDto1 =
        testUtil.getJobRecordsForCostDefinition("empty String", costDefinitionRequestBodyJson3);
    RecordStatusDto recordStatusDto2 =
        testUtil.getJobRecordsForCostDefinition(
            "Cost value details not found", costDefinitionRequestBodyJson4);

    PagePayload<RecordStatusDto> recordStatusDtoPagePayload =
        testUtil.createPagePayloadRecordStatusDto(
            List.of(recordStatusDto1, recordStatusDto2), 1, 2, 1);

    when(jobsDashboardClient.getJobRecordsByFilters(any(), any(), any(), any(), any()))
        .thenReturn(BaseResponse.builder().payload(recordStatusDtoPagePayload).build());
    doNothing().when(fileService).uploadFile(anyString(), anyString(), any());
    when(jobsConsumerClient.getJob(anyString(), anyString()))
        .thenReturn(BaseResponse.builder().payload(testUtil.getJobDto()).build());
    when(fileMetaDataClient.findFileMetadataById(any()))
        .thenReturn(testUtil.getFileMetaDataBaseResponse());
    when(fileService.getFile(anyString(), anyString()))
        .thenReturn(testUtil.getCostDefinitionFileResponse(inputStream));

    when(preSignedUrlInterface.downloadFileURLById(1L))
        .thenReturn(testUtil.getPreSignedUrlResponse());
    when(fileMetaDataClient.createFileMetadata(any()))
        .thenReturn(BaseResponse.builder().payload(testUtil.getFileMetaDataResponse()).build());

    PreSignedUrlResponse response =
        costDefinitionProcessingRequest.downloadTransitTimeErrorLogs(
            jobDto, Optional.of(ApiStatusEnum.FAILURE.name()));

    Assertions.assertNotNull(response);
    Assertions.assertFalse(ObjectUtils.isEmpty(response));
  }

  @Test
  void downloadErrorLogsStaticTableTest()
      throws CommonServiceException, JobSubmissionException, IOException {
    InputStream inputStream =
        new ByteArrayInputStream(
            TestUtil.CSV_CONTENTS_WITH_SELECTOR_CF_FOR_STATIC_TABLE.getBytes());
    JobDto jobDto = testUtil.createJob(TestUtil.JOB_ID, JobTypeEnum.UPLOAD_COST_DEFINITION, 2);
    RecordStatusDto recordStatusDto1 =
        testUtil.getJobRecordsForCostDefinition("empty String", costDefinitionRequestBodyJson4);

    PagePayload<RecordStatusDto> recordStatusDtoPagePayload =
        testUtil.createPagePayloadRecordStatusDto(List.of(recordStatusDto1), 1, 1, 1);

    when(jobsDashboardClient.getJobRecordsByFilters(any(), any(), any(), any(), any()))
        .thenReturn(BaseResponse.builder().payload(recordStatusDtoPagePayload).build());
    doNothing().when(fileService).uploadFile(anyString(), anyString(), any());
    when(jobsConsumerClient.getJob(anyString(), anyString()))
        .thenReturn(BaseResponse.builder().payload(testUtil.getJobDto()).build());
    when(fileMetaDataClient.findFileMetadataById(any()))
        .thenReturn(testUtil.getFileMetaDataBaseResponse());
    when(fileService.getFile(anyString(), anyString()))
        .thenReturn(testUtil.getCostDefinitionFileResponse(inputStream));

    when(preSignedUrlInterface.downloadFileURLById(1L))
        .thenReturn(testUtil.getPreSignedUrlResponse());
    when(fileMetaDataClient.createFileMetadata(any()))
        .thenReturn(BaseResponse.builder().payload(testUtil.getFileMetaDataResponse()).build());

    PreSignedUrlResponse response =
        costDefinitionProcessingRequest.downloadTransitTimeErrorLogs(
            jobDto, Optional.of(ApiStatusEnum.FAILURE.name()));

    Assertions.assertNotNull(response);
    Assertions.assertFalse(ObjectUtils.isEmpty(response));
  }

  @Test
  void downloadErrorLogsStaticTableWithOutSelectorCfTest()
      throws CommonServiceException, JobSubmissionException, IOException {
    InputStream inputStream =
        new ByteArrayInputStream(
            TestUtil.CSV_CONTENTS_WITH_OUT_SELECTOR_CF_FOR_STATIC_TABLE.getBytes());
    JobDto jobDto = testUtil.createJob(TestUtil.JOB_ID, JobTypeEnum.UPLOAD_COST_DEFINITION, 2);
    RecordStatusDto recordStatusDto1 =
        testUtil.getJobRecordsForCostDefinition("empty String", costDefinitionRequestBodyJson4);

    PagePayload<RecordStatusDto> recordStatusDtoPagePayload =
        testUtil.createPagePayloadRecordStatusDto(List.of(recordStatusDto1), 1, 1, 1);

    when(jobsDashboardClient.getJobRecordsByFilters(any(), any(), any(), any(), any()))
        .thenReturn(BaseResponse.builder().payload(recordStatusDtoPagePayload).build());
    doNothing().when(fileService).uploadFile(anyString(), anyString(), any());
    when(jobsConsumerClient.getJob(anyString(), anyString()))
        .thenReturn(BaseResponse.builder().payload(testUtil.getJobDto()).build());
    when(fileMetaDataClient.findFileMetadataById(any()))
        .thenReturn(testUtil.getFileMetaDataBaseResponse());
    when(fileService.getFile(anyString(), anyString()))
        .thenReturn(testUtil.getCostDefinitionFileResponse(inputStream));

    when(preSignedUrlInterface.downloadFileURLById(1L))
        .thenReturn(testUtil.getPreSignedUrlResponse());
    when(fileMetaDataClient.createFileMetadata(any()))
        .thenReturn(BaseResponse.builder().payload(testUtil.getFileMetaDataResponse()).build());

    PreSignedUrlResponse response =
        costDefinitionProcessingRequest.downloadTransitTimeErrorLogs(
            jobDto, Optional.of(ApiStatusEnum.FAILURE.name()));

    Assertions.assertNotNull(response);
    Assertions.assertFalse(ObjectUtils.isEmpty(response));
  }

  @Test
  void downloadErrorLogsTestExceptionTest() throws IOException, CommonServiceException {
    InputStream inputStream =
        new ByteArrayInputStream(TestUtil.CSV_CONTENTS_WITHOUT_SELECTOR_CF_FOR_GRID.getBytes());
    JobDto jobDto = testUtil.createJob(TestUtil.JOB_ID, JobTypeEnum.UPLOAD_COST_DEFINITION, 20);
    RecordStatusDto recordStatusDto = testUtil.getJobRecordsForTransitTimes();

    PagePayload<RecordStatusDto> recordStatusDtoPagePayload =
        testUtil.createPagePayloadRecordStatusDto(List.of(recordStatusDto), 5, 20, 1);

    when(jobsDashboardClient.getJobRecordsByFilters(any(), any(), any(), any(), any()))
        .thenReturn(BaseResponse.builder().payload(recordStatusDtoPagePayload).build());
    when(jobsConsumerClient.getJob(anyString(), anyString()))
        .thenReturn(BaseResponse.builder().payload(testUtil.getJobDto()).build());
    when(fileMetaDataClient.findFileMetadataById(any()))
        .thenReturn(testUtil.getFileMetaDataBaseResponse());
    when(fileService.getFile(anyString(), anyString()))
        .thenReturn(testUtil.getCostDefinitionFileResponse(inputStream));

    doThrow(new RuntimeException("Error Creating the file meta"))
        .when(fileService)
        .uploadFile(anyString(), anyString(), any());

    Exception ex =
        Assertions.assertThrows(
            Exception.class,
            () ->
                costDefinitionProcessingRequest.downloadTransitTimeErrorLogs(
                    jobDto, Optional.of(ApiStatusEnum.FAILURE.name())));
    Assertions.assertEquals("Error Creating the file meta", ex.getMessage());
  }

  @Test
  void validateFullEmptyCSVTest() throws IOException {
    Path path = Paths.get("src", "test", "resources", "common", "fullEmpty.csv");
    InputStream inputStream = Files.newInputStream(path);
    FileResponse response = testUtil.getFileResponse();
    response.setInputStream(inputStream);

    Exception ex =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                costDefinitionProcessingRequest.validate(
                    testUtil.getUploadRequestWithAdditionalReference(), response));
    Assertions.assertEquals(
        "Cost definition data upload has no records or headers are invalid.", ex.getMessage());
  }

  @Test
  void validateEmptyCSVTest() throws IOException {
    Path path = Paths.get("src", "test", "resources", "costDefinition", "costDefinitionEmpty.csv");
    InputStream inputStream = Files.newInputStream(path);
    FileResponse response = testUtil.getFileResponse();
    response.setInputStream(inputStream);

    Exception ex =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                costDefinitionProcessingRequest.validate(
                    testUtil.getUploadRequestWithAdditionalReference(), response));
    Assertions.assertEquals(
        "Cost definition data upload has no records or headers are invalid.", ex.getMessage());
  }

  @Test
  void validateRequestCostTypeTest() throws IOException {
    Path path = Paths.get("src", "test", "resources", "costDefinition", "costDefinitionTable.csv");
    InputStream inputStream = Files.newInputStream(path);
    FileResponse response = testUtil.getFileResponse();
    response.setInputStream(inputStream);
    GenericUploadRequest request = testUtil.getGenericUploadRequest();
    request.setOrgId("NEXTUPLE");

    Exception ex =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> costDefinitionProcessingRequest.validate(request, response));
    Assertions.assertEquals(
        "Cost type value provided in request's additionalReference is empty or null.",
        ex.getMessage());
  }

  @Test
  void validateCostTypeValuesTest() throws IOException {
    Path path =
        Paths.get(
            "src", "test", "resources", "costDefinition", "costDefinitionIncorrectCostType.csv");
    InputStream inputStream = Files.newInputStream(path);
    FileResponse response = testUtil.getFileResponse();
    response.setInputStream(inputStream);

    Exception ex =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                costDefinitionProcessingRequest.validate(
                    testUtil.getUploadRequestWithAdditionalReference(), response));
    Assertions.assertEquals(
        "Cost type value provided in the CSV does not match with request cost type SHIPPING_COST.",
        ex.getMessage());
  }

  @Test
  void validateRequestOrgIdTest() throws IOException {
    Path path = Paths.get("src", "test", "resources", "costDefinition", "costDefinitionTable.csv");
    InputStream inputStream = Files.newInputStream(path);
    FileResponse response = testUtil.getFileResponse();
    response.setInputStream(inputStream);
    GenericUploadRequest request = testUtil.getGenericUploadRequest();
    request.setOrgId("");

    Exception ex =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> costDefinitionProcessingRequest.validate(request, response));
    Assertions.assertEquals("OrgId value provided in request is empty or null.", ex.getMessage());
  }

  @Test
  void validateOrgIdTest() throws IOException {
    Path path =
        Paths.get("src", "test", "resources", "costDefinition", "costDefinitionIncorrectOrgId.csv");
    InputStream inputStream = Files.newInputStream(path);
    FileResponse response = testUtil.getFileResponse();
    response.setInputStream(inputStream);

    Exception ex =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                costDefinitionProcessingRequest.validate(
                    testUtil.getUploadRequestWithAdditionalReference(), response));
    Assertions.assertEquals(
        "OrgId value provided in the CSV does not match with request org NEXTUPLE.",
        ex.getMessage());
  }

  @Test
  void validateNotesHeaderTest() throws IOException {
    Path path =
        Paths.get(
            "src", "test", "resources", "costDefinition", "costDefinitionIncorrectHeader.csv");
    InputStream inputStream = Files.newInputStream(path);
    FileResponse response = testUtil.getFileResponse();
    response.setInputStream(inputStream);

    Exception ex =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                costDefinitionProcessingRequest.validate(
                    testUtil.getUploadRequestWithAdditionalReference(), response));
    Assertions.assertEquals("Invalid Notes headers", ex.getMessage());
  }

  @Test
  void validateStaticHeadersTest() throws IOException {
    Path path =
        Paths.get(
            "src", "test", "resources", "costDefinition", "costDefinitionInvalidStaticHeaders.csv");
    InputStream inputStream = Files.newInputStream(path);
    FileResponse response = testUtil.getFileResponse();
    response.setInputStream(inputStream);

    Exception ex =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                costDefinitionProcessingRequest.validate(
                    testUtil.getUploadRequestWithAdditionalReference(), response));
    Assertions.assertEquals(
        "Cost definition data upload has invalid static headers.", ex.getMessage());
  }

  @Test
  void validateInvalidSelectorCfHeaderTest() throws IOException, CommonServiceException {
    Path path =
        Paths.get(
            "src",
            "test",
            "resources",
            "costDefinition",
            "costDefinitionInvalidSelectorCfHeaders.csv");
    InputStream inputStream = Files.newInputStream(path);
    FileResponse response = testUtil.getFileResponse();
    response.setInputStream(inputStream);
    when(costDefinitionService.getCostTypeValidationResponse(any(), any()))
        .thenReturn(testUtil.getCostTypeValidationResponseForTable());

    Exception ex =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                costDefinitionProcessingRequest.validate(
                    testUtil.getUploadRequestWithAdditionalReference(), response));
    Assertions.assertEquals("Invalid CSV Headers for Cost definition upload.", ex.getMessage());
  }

  @Test
  void validateInvalidFilterCfHeaderTest() throws IOException, CommonServiceException {
    Path path =
        Paths.get(
            "src",
            "test",
            "resources",
            "costDefinition",
            "costDefinitionInvalidFilterCfHeaders.csv");
    InputStream inputStream = Files.newInputStream(path);
    FileResponse response = testUtil.getFileResponse();
    response.setInputStream(inputStream);
    when(costDefinitionService.getCostTypeValidationResponse(any(), any()))
        .thenReturn(testUtil.getCostTypeValidationResponseForTable());

    Exception ex =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                costDefinitionProcessingRequest.validate(
                    testUtil.getUploadRequestWithAdditionalReference(), response));
    Assertions.assertEquals("Invalid CSV Headers for Cost definition upload.", ex.getMessage());
  }

  @Test
  void validateInvalidDynamicHeaderTest() throws IOException, CommonServiceException {
    Path path =
        Paths.get(
            "src",
            "test",
            "resources",
            "costDefinition",
            "costDefinitionInvalidDynamicHeaders.csv");
    InputStream inputStream = Files.newInputStream(path);
    FileResponse response = testUtil.getFileResponse();
    response.setInputStream(inputStream);
    when(costDefinitionService.getCostTypeValidationResponse(any(), any()))
        .thenReturn(testUtil.getCostTypeValidationResponseForTable());

    Exception ex =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                costDefinitionProcessingRequest.validate(
                    testUtil.getUploadRequestWithAdditionalReference(), response));
    Assertions.assertEquals("Invalid CSV Headers for Cost definition upload.", ex.getMessage());
  }

  @Test
  void validateInvalidRowColCfHeaderTest() throws IOException, CommonServiceException {
    Path path =
        Paths.get(
            "src",
            "test",
            "resources",
            "costDefinition",
            "costDefinitionInvalidRowColCfHeader.csv");
    InputStream inputStream = Files.newInputStream(path);
    FileResponse response = testUtil.getFileResponse();
    response.setInputStream(inputStream);
    when(costDefinitionService.getCostTypeValidationResponse(any(), any()))
        .thenReturn(testUtil.getCostTypeValidationResponseForTable());

    Exception ex =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                costDefinitionProcessingRequest.validate(
                    testUtil.getUploadRequestWithAdditionalReference(), response));
    Assertions.assertEquals("Invalid CSV Headers for Cost definition upload.", ex.getMessage());
  }

  @Test
  void validateInvalidRowCfValuesTest() throws IOException, CommonServiceException {
    Path path =
        Paths.get(
            "src", "test", "resources", "costDefinition", "costDefinitionInvalidRowCfValues.csv");
    InputStream inputStream = Files.newInputStream(path);
    FileResponse response = testUtil.getFileResponse();
    response.setInputStream(inputStream);
    when(costDefinitionService.getCostTypeValidationResponse(any(), any()))
        .thenReturn(testUtil.getCostTypeValidationResponseForTable());

    Exception ex =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                costDefinitionProcessingRequest.validate(
                    testUtil.getUploadRequestWithAdditionalReference(), response));
    Assertions.assertEquals("Invalid CSV Headers for Cost definition upload.", ex.getMessage());
  }

  @Test
  void validateInvalidColCfValuesTest() throws IOException, CommonServiceException {
    Path path =
        Paths.get(
            "src", "test", "resources", "costDefinition", "costDefinitionInvalidColCfValues.csv");
    InputStream inputStream = Files.newInputStream(path);
    FileResponse response = testUtil.getFileResponse();
    response.setInputStream(inputStream);
    when(costDefinitionService.getCostTypeValidationResponse(any(), any()))
        .thenReturn(testUtil.getCostTypeValidationResponseForGrid());

    Exception ex =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                costDefinitionProcessingRequest.validate(
                    testUtil.getUploadRequestWithAdditionalReference(), response));
    Assertions.assertEquals("Invalid column cost factor headers.", ex.getMessage());
  }

  @Test
  void validateInvalidSelectorCfValueTest() throws IOException, CommonServiceException {
    Path path =
        Paths.get(
            "src",
            "test",
            "resources",
            "costDefinition",
            "costDefinitionInvalidSelectorCfValue.csv");
    InputStream inputStream = Files.newInputStream(path);
    FileResponse response = testUtil.getFileResponse();
    response.setInputStream(inputStream);
    when(costDefinitionService.getCostTypeValidationResponse(any(), any()))
        .thenReturn(testUtil.getCostTypeValidationResponseForGrid());

    Exception ex =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                costDefinitionProcessingRequest.validate(
                    testUtil.getUploadRequestWithAdditionalReference(), response));
    Assertions.assertEquals("Invalid selector cost factor value: INVALID", ex.getMessage());
  }

  @Test
  void validateInvalidFilterCfValueTest() throws IOException, CommonServiceException {
    Path path =
        Paths.get(
            "src", "test", "resources", "costDefinition", "costDefinitionInvalidFilterCfValue.csv");
    InputStream inputStream = Files.newInputStream(path);
    FileResponse response = testUtil.getFileResponse();
    response.setInputStream(inputStream);
    when(costDefinitionService.getCostTypeValidationResponse(any(), any()))
        .thenReturn(testUtil.getCostTypeValidationResponseForGrid());

    Exception ex =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                costDefinitionProcessingRequest.validate(
                    testUtil.getUploadRequestWithAdditionalReference(), response));
    Assertions.assertEquals("Invalid filter cost factor value: INVALID", ex.getMessage());
  }

  @Test
  void validateInvalidDynamicValueTest() throws IOException, CommonServiceException {
    Path path =
        Paths.get(
            "src",
            "test",
            "resources",
            "costDefinition",
            "costDefinitionInvalidDynamicCfValue.csv");
    InputStream inputStream = Files.newInputStream(path);
    FileResponse response = testUtil.getFileResponse();
    response.setInputStream(inputStream);
    when(costDefinitionService.getCostTypeValidationResponse(any(), any()))
        .thenReturn(testUtil.getCostTypeValidationResponseForGrid());

    Exception ex =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                costDefinitionProcessingRequest.validate(
                    testUtil.getUploadRequestWithAdditionalReference(), response));
    Assertions.assertEquals("Dynamic cost factor: Invalid boolean value", ex.getMessage());
  }

  @Test
  void validateRowCountWhenDynamicCfIsTrueTest() throws IOException, CommonServiceException {
    Path path =
        Paths.get(
            "src", "test", "resources", "costDefinition", "costDefinitionInvalidRowCount.csv");
    InputStream inputStream = Files.newInputStream(path);
    FileResponse response = testUtil.getFileResponse();
    response.setInputStream(inputStream);
    when(costDefinitionService.getCostTypeValidationResponse(any(), any()))
        .thenReturn(testUtil.getCostTypeValidationResponseForSingleCf());

    Exception ex =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                costDefinitionProcessingRequest.validate(
                    testUtil.getUploadRequestWithAdditionalReference(), response));
    Assertions.assertEquals(
        "Cost value upload row count should be greater than one when dynamic cost value is true.",
        ex.getMessage());
  }

  @Test
  void validateHappyPathForTableTest() throws IOException, CommonServiceException {
    Path path = Paths.get("src", "test", "resources", "costDefinition", "costDefinitionTable.csv");
    InputStream inputStream = Files.newInputStream(path);
    FileResponse response = testUtil.getFileResponse();
    response.setInputStream(inputStream);
    when(costDefinitionService.getCostTypeValidationResponse(any(), any()))
        .thenReturn(testUtil.getCostTypeValidationResponseForTable());

    assertDoesNotThrow(
        () ->
            costDefinitionProcessingRequest.validate(
                testUtil.getUploadRequestWithAdditionalReference(), response));
  }

  @Test
  void validateHappyPathForGridTest() throws IOException, CommonServiceException {
    Path path = Paths.get("src", "test", "resources", "costDefinition", "costDefinitionGrid.csv");
    InputStream inputStream = Files.newInputStream(path);
    FileResponse response = testUtil.getFileResponse();
    response.setInputStream(inputStream);
    when(costDefinitionService.getCostTypeValidationResponse(any(), any()))
        .thenReturn(testUtil.getCostTypeValidationResponseForGrid());

    assertDoesNotThrow(
        () ->
            costDefinitionProcessingRequest.validate(
                testUtil.getUploadRequestWithAdditionalReference(), response));
  }

  @Test
  void validateHappyPathForStaticTableTest() throws IOException, CommonServiceException {
    Path path =
        Paths.get("src", "test", "resources", "costDefinition", "costDefinitionStaticTable.csv");
    InputStream inputStream = Files.newInputStream(path);
    FileResponse response = testUtil.getFileResponse();
    response.setInputStream(inputStream);
    when(costDefinitionService.getCostTypeValidationResponse(any(), any()))
        .thenReturn(testUtil.getCostTypeValidationResponseForStaticTable());

    assertDoesNotThrow(
        () ->
            costDefinitionProcessingRequest.validate(
                testUtil.getUploadRequestWithAdditionalReference(), response));
  }

  @Test
  void validateHappyPathForStaticTableWithNoSelectorCfTest()
      throws IOException, CommonServiceException {
    Path path =
        Paths.get(
            "src", "test", "resources", "costDefinition", "costDefinitionStaticTableWithNoSCF.csv");
    InputStream inputStream = Files.newInputStream(path);
    FileResponse response = testUtil.getFileResponse();
    response.setInputStream(inputStream);
    when(costDefinitionService.getCostTypeValidationResponse(any(), any()))
        .thenReturn(testUtil.getCostTypeValidationResponseForStaticWithOutSCF());

    assertDoesNotThrow(
        () ->
            costDefinitionProcessingRequest.validate(
                testUtil.getUploadRequestWithAdditionalReference(), response));
  }

  @Test
  void validateHappyPathForGridWithNoSelectorCfTest() throws IOException, CommonServiceException {
    Path path =
        Paths.get("src", "test", "resources", "costDefinition", "costDefinitionGridWithNoSCF.csv");
    InputStream inputStream = Files.newInputStream(path);
    FileResponse response = testUtil.getFileResponse();
    response.setInputStream(inputStream);
    when(costDefinitionService.getCostTypeValidationResponse(any(), any()))
        .thenReturn(testUtil.getCostTypeValidationResponseForGridWithOutSCF());

    assertDoesNotThrow(
        () ->
            costDefinitionProcessingRequest.validate(
                testUtil.getUploadRequestWithAdditionalReference(), response));
  }

  @Test
  void validateHappyPathForGridDefaultTest() throws IOException, CommonServiceException {
    Path path =
        Paths.get("src", "test", "resources", "costDefinition", "costDefinitionGridDefault.csv");
    InputStream inputStream = Files.newInputStream(path);
    FileResponse response = testUtil.getFileResponse();
    response.setInputStream(inputStream);
    when(costDefinitionService.getCostTypeValidationResponse(any(), any()))
        .thenReturn(testUtil.getCostTypeValidationResponseForGrid());

    assertDoesNotThrow(
        () ->
            costDefinitionProcessingRequest.validate(
                testUtil.getUploadRequestWithAdditionalReference(), response));
  }

  @Test
  void validateHeadersForGridWithNoSelectorCfTest() throws IOException, CommonServiceException {
    Path path =
        Paths.get("src", "test", "resources", "costDefinition", "gridWithNoSCFInvalidHeaders.csv");
    InputStream inputStream = Files.newInputStream(path);
    FileResponse response = testUtil.getFileResponse();
    response.setInputStream(inputStream);
    when(costDefinitionService.getCostTypeValidationResponse(any(), any()))
        .thenReturn(testUtil.getCostTypeValidationResponseForGridWithOutSCF());

    Exception ex =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                costDefinitionProcessingRequest.validate(
                    testUtil.getUploadRequestWithAdditionalReference(), response));
    Assertions.assertEquals("Invalid CSV Headers for Cost definition upload.", ex.getMessage());
  }

  @Test
  void validateHeadersForStaticTableWithNoSelectorCfTest()
      throws IOException, CommonServiceException {
    Path path =
        Paths.get(
            "src", "test", "resources", "costDefinition", "staticTableWithNoSCFInvalidHeaders.csv");
    InputStream inputStream = Files.newInputStream(path);
    FileResponse response = testUtil.getFileResponse();
    response.setInputStream(inputStream);
    when(costDefinitionService.getCostTypeValidationResponse(any(), any()))
        .thenReturn(testUtil.getCostTypeValidationResponseForStaticWithOutSCF());

    Exception ex =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                costDefinitionProcessingRequest.validate(
                    testUtil.getUploadRequestWithAdditionalReference(), response));
    Assertions.assertEquals("Invalid CSV Headers for Cost definition upload.", ex.getMessage());
  }

  @Test
  void validateHeadersForStaticTableWithSelectorCfTest()
      throws IOException, CommonServiceException {
    Path path =
        Paths.get(
            "src", "test", "resources", "costDefinition", "staticTableWithSCFInvalidHeaders.csv");
    InputStream inputStream = Files.newInputStream(path);
    FileResponse response = testUtil.getFileResponse();
    response.setInputStream(inputStream);
    when(costDefinitionService.getCostTypeValidationResponse(any(), any()))
        .thenReturn(testUtil.getCostTypeValidationResponseForStaticWithOutSCF());

    Exception ex =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                costDefinitionProcessingRequest.validate(
                    testUtil.getUploadRequestWithAdditionalReference(), response));
    Assertions.assertEquals("Invalid CSV Headers for Cost definition upload.", ex.getMessage());
  }

  @Test
  void validateHeadersForStaticTableWithInvalidCostTypeTest()
      throws IOException, CommonServiceException {
    Path path =
        Paths.get(
            "src",
            "test",
            "resources",
            "costDefinition",
            "staticTableWithCostTypeInvalidHeaders.csv");
    InputStream inputStream = Files.newInputStream(path);
    FileResponse response = testUtil.getFileResponse();
    response.setInputStream(inputStream);
    when(costDefinitionService.getCostTypeValidationResponse(any(), any()))
        .thenReturn(testUtil.getCostTypeValidationResponseForStaticWithOutSCF());

    Exception ex =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                costDefinitionProcessingRequest.validate(
                    testUtil.getUploadRequestWithAdditionalReference(), response));
    Assertions.assertEquals("Invalid CSV Headers for Cost definition upload.", ex.getMessage());
  }

  @Test
  void validateHeadersForGridWithDefaultDataTest() throws IOException, CommonServiceException {
    Path path =
        Paths.get(
            "src",
            "test",
            "resources",
            "costDefinition",
            "costDefinitionDefaultInvalidHeaders.csv");
    InputStream inputStream = Files.newInputStream(path);
    FileResponse response = testUtil.getFileResponse();
    response.setInputStream(inputStream);
    when(costDefinitionService.getCostTypeValidationResponse(any(), any()))
        .thenReturn(testUtil.getCostTypeValidationResponseForGrid());

    Exception ex =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                costDefinitionProcessingRequest.validate(
                    testUtil.getUploadRequestWithAdditionalReference(), response));
    Assertions.assertEquals("Invalid CSV Headers for Cost definition upload.", ex.getMessage());
  }

  @Test
  void validateSelectorCfValueForGridWithDefaultDataTest()
      throws IOException, CommonServiceException {
    Path path =
        Paths.get(
            "src",
            "test",
            "resources",
            "costDefinition",
            "costDefinitionDefaultInvalidSelectorCfValue.csv");
    InputStream inputStream = Files.newInputStream(path);
    FileResponse response = testUtil.getFileResponse();
    response.setInputStream(inputStream);
    when(costDefinitionService.getCostTypeValidationResponse(any(), any()))
        .thenReturn(testUtil.getCostTypeValidationResponseForGrid());

    Exception ex =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                costDefinitionProcessingRequest.validate(
                    testUtil.getUploadRequestWithAdditionalReference(), response));
    Assertions.assertEquals("Invalid selector cost factor value: INVALID", ex.getMessage());
  }

  @Test
  void validateInvalidMultipleFilterCfHeaderTest() throws IOException, CommonServiceException {
    Path path =
        Paths.get("src", "test", "resources", "costDefinition", "multipleFilterCfInvalidValue.csv");
    InputStream inputStream = Files.newInputStream(path);
    FileResponse response = testUtil.getFileResponse();
    response.setInputStream(inputStream);
    when(costDefinitionService.getCostTypeValidationResponse(any(), any()))
        .thenReturn(testUtil.getCostTypeDetailsWithMultipleFilterCf());

    Exception ex =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                costDefinitionProcessingRequest.validate(
                    testUtil.getUploadRequestWithAdditionalReference(), response));
    Assertions.assertEquals("Invalid filter cost factor value: INVALID", ex.getMessage());
  }

  @Test
  void validateHappyPathForGridWithMultipleFilterCfTest()
      throws IOException, CommonServiceException {
    Path path =
        Paths.get(
            "src", "test", "resources", "costDefinition", "costDefinitionMultipleFilterCf.csv");
    InputStream inputStream = Files.newInputStream(path);
    FileResponse response = testUtil.getFileResponse();
    response.setInputStream(inputStream);
    when(costDefinitionService.getCostTypeValidationResponse(any(), any()))
        .thenReturn(testUtil.getCostTypeDetailsWithMultipleFilterCf());

    assertDoesNotThrow(
        () ->
            costDefinitionProcessingRequest.validate(
                testUtil.getUploadRequestWithAdditionalReference(), response));
  }
}
