/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.csvdownload.service;

import static org.mockito.Mockito.*;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.csvdownload.common.TestUtil;
import com.nextuple.csvdownload.exception.CsvFormatValidationFailedException;
import com.nextuple.csvdownload.exception.CsvParsingException;
import com.nextuple.csvdownload.exception.JobSubmissionException;
import com.nextuple.jobs.framework.common.clients.JobsDashboardClient;
import com.nextuple.jobs.framework.common.domain.enums.JobTypeEnum;
import com.nextuple.jobs.framework.common.domain.outbound.JobResponse;
import com.nextuple.transit.domain.feign.TransitBufferConfigRequestFeign;
import com.nextuple.transit.domain.outbound.TransitBufferConfigResponse;
import com.opencsv.exceptions.CsvException;
import feign.FeignException;
import feign.Request;
import feign.Request.HttpMethod;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class CsvUploadUtilityServiceTest {

  @Mock private JobsDashboardClient jobsDashboardClient;

  @InjectMocks private CsvUploadUtilityService csvUploadUtilityService;
  @InjectMocks private TestUtil testUtil;

  @Mock TransitBufferConfigRequestFeign transitBufferConfigRequestFeign;

  @Test
  void uploadProcessingLeadTimesCsv()
      throws IOException,
          CsvParsingException,
          CsvFormatValidationFailedException,
          JobSubmissionException,
          CsvException {
    MultipartFile csvFile = mock(MultipartFile.class);
    String csvFileContent =
        "#CommentedLine1\n"
            + "action,nodeId,orgId,serviceOptions,processingTime (in hrs)\n"
            + "UPDATE,1554,BAY,SDND,2\n"
            + "UPDATE,1560,BAY,SDND,2\n"
            + "UPDATE,1101,BAY,SDND,2\n"
            + "UPDATE,1634,BAY,EXPRESS,30.92\n"
            + "UPDATE,1601,BAY,EXPRESS,22.55\n"
            + "UPDATE,1114,BAY,SDND,24.97\n"
            + "DELETE,1518,BAY,NEXTDAY,6\n"
            + "DELETE,1125,BAY,EXPRESS,19.90";
    when(csvFile.getInputStream())
        .thenReturn(
            new ByteArrayInputStream(csvFileContent.getBytes()),
            new ByteArrayInputStream(csvFileContent.getBytes()),
            new ByteArrayInputStream(csvFileContent.getBytes()));

    when(jobsDashboardClient.processJobOffline(any(), any(), any(), any()))
        .thenReturn(BaseResponse.builder().payload(new JobResponse()).build());

    String res = csvUploadUtilityService.uploadProcessingLeadTimesCsv(TestUtil.ORG_ID, csvFile);
    Assertions.assertFalse(ObjectUtils.isEmpty(res));
  }

  @Test
  void uploadProcessingLeadTimesCsvInvalidHeaders() throws IOException {
    MultipartFile csvFile = mock(MultipartFile.class);
    String csvFileContent =
        "nodeId,orgId,serviceOptions\n"
            + "1554,BAY,SDND\n"
            + "1560,BAY,SDND\n"
            + "1101,BAY,SDND\n"
            + "1518,BAY,NEXTDAY\n"
            + "1634,BAY,EXPRESS\n"
            + "1601,BAY,EXPRESS\n"
            + "1125,BAY,EXPRESS\n"
            + "1114,BAY,SDND";

    when(csvFile.getInputStream())
        .thenReturn(
            new ByteArrayInputStream(csvFileContent.getBytes()),
            new ByteArrayInputStream(csvFileContent.getBytes()),
            new ByteArrayInputStream(csvFileContent.getBytes()));

    Exception exception =
        Assertions.assertThrows(
            CsvFormatValidationFailedException.class,
            () -> csvUploadUtilityService.uploadProcessingLeadTimesCsv(TestUtil.ORG_ID, csvFile));
    Assertions.assertNotNull(exception);
  }

  @Test
  void uploadProcessingLeadTimesCsvEmptyCsvFile() throws IOException {
    MultipartFile csvFile = mock(MultipartFile.class);
    String csvFileContent = "action,nodeId,orgId,serviceOptions,processingTime (in hrs)";

    when(csvFile.getInputStream())
        .thenReturn(
            new ByteArrayInputStream(csvFileContent.getBytes()),
            new ByteArrayInputStream(csvFileContent.getBytes()),
            new ByteArrayInputStream(csvFileContent.getBytes()));

    Exception exception =
        Assertions.assertThrows(
            CsvFormatValidationFailedException.class,
            () -> csvUploadUtilityService.uploadProcessingLeadTimesCsv(TestUtil.ORG_ID, csvFile));
    Assertions.assertNotNull(exception);
  }

  @Test
  void uploadProcessingLeadTimesCsvEmptyRowException()
      throws IOException,
          CsvParsingException,
          CsvFormatValidationFailedException,
          JobSubmissionException,
          CsvException {
    MultipartFile csvFile = mock(MultipartFile.class);
    String csvFileContent =
        "action,nodeId,orgId,serviceOptions,processingTime (in hrs)\n"
            + "UPDATE,1554,BAY,SDND,2\n"
            + "UPDATE,1560,BAY,SDND,2\n"
            + "UPDATE,1101,BAY,SDND,2\n"
            + "\n"
            + "UPDATE,1601,BAY,EXPRESS,22.55\n"
            + "UPDATE,1114,BAY,SDND,24.97\n"
            + "DELETE,1518,BAY,NEXTDAY,6\n"
            + "DELETE,1125,BAY,EXPRESS,19.90";
    when(csvFile.getInputStream())
        .thenReturn(
            new ByteArrayInputStream(csvFileContent.getBytes()),
            new ByteArrayInputStream(csvFileContent.getBytes()),
            new ByteArrayInputStream(csvFileContent.getBytes()));

    String res = csvUploadUtilityService.uploadProcessingLeadTimesCsv(TestUtil.ORG_ID, csvFile);
    Assertions.assertFalse(ObjectUtils.isEmpty(res));
  }

  @Test
  void uploadProcessingLeadTimesCsvFeignException() throws IOException {
    MultipartFile csvFile = mock(MultipartFile.class);
    String csvFileContent = TestUtil.processingLeadTimesCsvData;
    Map<String, Collection<String>> headers = new HashMap<>();

    when(csvFile.getInputStream())
        .thenReturn(
            new ByteArrayInputStream(csvFileContent.getBytes()),
            new ByteArrayInputStream(csvFileContent.getBytes()),
            new ByteArrayInputStream(csvFileContent.getBytes()));

    when(jobsDashboardClient.processJobOffline(any(), any(), any(), any()))
        .thenThrow(
            new FeignException.BadRequest(
                "Failed to create job",
                Request.create(HttpMethod.GET, "", new HashMap<>(), null, null, null),
                "Failed to create job".getBytes(),
                headers));

    Exception exception =
        Assertions.assertThrows(
            JobSubmissionException.class,
            () -> csvUploadUtilityService.uploadProcessingLeadTimesCsv(TestUtil.ORG_ID, csvFile));
    Assertions.assertNotNull(exception);
  }

  @Test
  void uploadProcessingLeadTimesCsvException() throws IOException {
    MultipartFile csvFile = mock(MultipartFile.class);
    String csvFileContent = TestUtil.processingLeadTimesCsvData;
    when(csvFile.getInputStream())
        .thenReturn(
            new ByteArrayInputStream(csvFileContent.getBytes()),
            new ByteArrayInputStream(csvFileContent.getBytes()),
            new ByteArrayInputStream(csvFileContent.getBytes()));

    when(jobsDashboardClient.processJobOffline(any(), any(), any(), any()))
        .thenThrow(new RuntimeException("Error while submitting job to job framework"));

    Exception exception =
        Assertions.assertThrows(
            JobSubmissionException.class,
            () -> csvUploadUtilityService.uploadProcessingLeadTimesCsv(TestUtil.ORG_ID, csvFile));
    Assertions.assertNotNull(exception);
  }

  @Test
  void uploadTransitTimesCsv()
      throws IOException, CsvFormatValidationFailedException, CsvException, JobSubmissionException {
    MultipartFile csvFile = mock(MultipartFile.class);

    String csvFileContent =
        "orgId,BAY,,,,,,,,,\n"
            + "Carrier Service:,ALL-Standard,,,,,,,,,\n"
            + "Destination geoZone / Source geoZone ->,SGZ1,SGZ2,SGZ3\n"
            + "DGZ1,10,9.96,9.96\n"
            + "DGZ2,10,9,9.9\n"
            + "DGZ3,10,9,9\n";

    when(csvFile.getInputStream())
        .thenReturn(
            new ByteArrayInputStream(csvFileContent.getBytes()),
            new ByteArrayInputStream(csvFileContent.getBytes()),
            new ByteArrayInputStream(csvFileContent.getBytes()));

    JobResponse jobResponse = testUtil.createJobResponse(JobTypeEnum.UPLOAD_TRANSIT_TIMES, 9);

    when(jobsDashboardClient.processJobOffline(any(), any(), any(), any()))
        .thenReturn(BaseResponse.builder().payload(jobResponse).build());

    String res = csvUploadUtilityService.uploadTransitTimesCsv(TestUtil.ORG_ID, csvFile);

    Assertions.assertFalse(ObjectUtils.isEmpty(res));
    verify(jobsDashboardClient, times(1)).processJobOffline(any(), any(), any(), any());
  }

  @Test
  void uploadTransitTimesCsvEmptyCsvFile() throws IOException {
    MultipartFile csvFile = mock(MultipartFile.class);

    String csvFileContent = "";

    when(csvFile.getInputStream())
        .thenReturn(
            new ByteArrayInputStream(csvFileContent.getBytes()),
            new ByteArrayInputStream(csvFileContent.getBytes()),
            new ByteArrayInputStream(csvFileContent.getBytes()));

    Exception exception =
        Assertions.assertThrows(
            CsvFormatValidationFailedException.class,
            () -> csvUploadUtilityService.uploadTransitTimesCsv(TestUtil.ORG_ID, csvFile));

    Assertions.assertNotNull(exception);
  }

  @Test
  void uploadTransitTimesCsvNullTransitTime()
      throws IOException, CsvFormatValidationFailedException, JobSubmissionException, CsvException {
    MultipartFile csvFile = mock(MultipartFile.class);

    JobResponse jobResponse = testUtil.createJobResponse(JobTypeEnum.UPLOAD_TRANSIT_TIMES, 9);

    String csvFileContent =
        "orgId,BAY,,,,,,,,,\n"
            + "Carrier Service:,ALL-Standard,,,,,,,,,\n"
            + "Destination geoZone / Source geoZone ->,SGZ1,SGZ2,SGZ3\n"
            + "DGZ1,10,9.96,9.96\n"
            + "DGZ2,10,9,9.9\n"
            + "DGZ3,10,,9\n";

    when(csvFile.getInputStream())
        .thenReturn(
            new ByteArrayInputStream(csvFileContent.getBytes()),
            new ByteArrayInputStream(csvFileContent.getBytes()),
            new ByteArrayInputStream(csvFileContent.getBytes()));

    when(jobsDashboardClient.processJobOffline(any(), any(), any(), any()))
        .thenReturn(BaseResponse.builder().payload(jobResponse).build());

    String res = csvUploadUtilityService.uploadTransitTimesCsv(TestUtil.ORG_ID, csvFile);

    Assertions.assertFalse(ObjectUtils.isEmpty(res));
    verify(jobsDashboardClient, times(1)).processJobOffline(any(), any(), any(), any());
  }

  @Test
  void uploadTransitTimesCsvInvalidOrgIdHeader() throws IOException {
    MultipartFile csvFile = mock(MultipartFile.class);

    String csvFileContent =
        "orgIds,BAY,,,,,,,,,\n"
            + "Carrier Service:,ALL-Standard,,,,,,,,,\n"
            + "Destination FSA / Source FSA ->,SFSA1,SFSA2\n"
            + "DFSA1,10,9.96\n"
            + "DFSA2,10,9\n"
            + "DFSA3,10,9\n";

    when(csvFile.getInputStream())
        .thenReturn(
            new ByteArrayInputStream(csvFileContent.getBytes()),
            new ByteArrayInputStream(csvFileContent.getBytes()),
            new ByteArrayInputStream(csvFileContent.getBytes()));

    Exception exception =
        Assertions.assertThrows(
            CsvFormatValidationFailedException.class,
            () -> csvUploadUtilityService.uploadTransitTimesCsv(TestUtil.ORG_ID, csvFile));

    Assertions.assertNotNull(exception);
  }

  @Test
  void uploadTransitTimesCsvInvalidCarrierServiceIdHeader() throws IOException {
    MultipartFile csvFile = mock(MultipartFile.class);

    String csvFileContent =
        "orgId,BAY,,,,,,,,,\n"
            + "Carrier Service,ALL-Standard,,,,,,,,,\n"
            + "Destination FSA / Source FSA ->,SFSA1,SFSA2,SFSA3\n"
            + "DFSA1,10,9.96,9.96\n"
            + "DFSA2,10,9,9.9\n"
            + "DFSA3,10,9,9\n";

    when(csvFile.getInputStream())
        .thenReturn(
            new ByteArrayInputStream(csvFileContent.getBytes()),
            new ByteArrayInputStream(csvFileContent.getBytes()),
            new ByteArrayInputStream(csvFileContent.getBytes()));

    Exception exception =
        Assertions.assertThrows(
            CsvFormatValidationFailedException.class,
            () -> csvUploadUtilityService.uploadTransitTimesCsv(TestUtil.ORG_ID, csvFile));

    Assertions.assertNotNull(exception);
  }

  @Test
  void uploadTransitTimesCsvInvalidFsaHeader() throws IOException {
    MultipartFile csvFile = mock(MultipartFile.class);

    String csvFileContent =
        "orgId,BAY,,,,,,,,,\n"
            + "Carrier Service:,ALL-Standard,,,,,,,,,\n"
            + "Destination FSA / Source FSA >,SFSA1,SFSA2,SFSA3\n"
            + "DFSA1,10,9.96,9.96\n"
            + "DFSA2,10,9,9.9\n"
            + "DFSA3,10,9,9\n";

    when(csvFile.getInputStream())
        .thenReturn(
            new ByteArrayInputStream(csvFileContent.getBytes()),
            new ByteArrayInputStream(csvFileContent.getBytes()),
            new ByteArrayInputStream(csvFileContent.getBytes()));

    Exception exception =
        Assertions.assertThrows(
            CsvFormatValidationFailedException.class,
            () -> csvUploadUtilityService.uploadTransitTimesCsv(TestUtil.ORG_ID, csvFile));

    Assertions.assertNotNull(exception);
  }

  @Test
  void uploadTransitTimesCsvException() throws IOException {
    MultipartFile csvFile = mock(MultipartFile.class);

    String csvFileContent =
        "orgId,BAY,,,,,,,,,\n"
            + "Carrier Service:,ALL-Standard,,,,,,,,,\n"
            + "Destination geoZone / Source geoZone ->,SGZ1,SGZ2,SGZ3\n"
            + "DGZ1,10,9.96,9.96\n"
            + "DGZ2,10,9,9.9\n"
            + "DGZ3,10,9,9\n";

    when(csvFile.getInputStream())
        .thenReturn(
            new ByteArrayInputStream(csvFileContent.getBytes()),
            new ByteArrayInputStream(csvFileContent.getBytes()),
            new ByteArrayInputStream(csvFileContent.getBytes()));

    when(jobsDashboardClient.processJobOffline(any(), any(), any(), any()))
        .thenThrow(new RuntimeException("Error while updating the job"));

    Exception exception =
        Assertions.assertThrows(
            JobSubmissionException.class,
            () -> csvUploadUtilityService.uploadTransitTimesCsv(TestUtil.ORG_ID, csvFile));

    Assertions.assertNotNull(exception);
    verify(jobsDashboardClient, times(1)).processJobOffline(any(), any(), any(), any());
  }

  @Test
  void uploadTransitTimesCsvFeignException() throws IOException {
    MultipartFile csvFile = mock(MultipartFile.class);
    Map<String, Collection<String>> headers = new HashMap<>();
    String csvFileContent =
        "orgId,BAY,,,,,,,,,\n"
            + "Carrier Service:,ALL-Standard,,,,,,,,,\n"
            + "Destination geoZone / Source geoZone ->,SGZ1,SGZ2,SGZ3\n"
            + "DGZ1,10,9.96,9.96\n"
            + "DGZ2,10,9,9.9\n"
            + "DGZ3,10,9,9\n";

    when(csvFile.getInputStream())
        .thenReturn(
            new ByteArrayInputStream(csvFileContent.getBytes()),
            new ByteArrayInputStream(csvFileContent.getBytes()),
            new ByteArrayInputStream(csvFileContent.getBytes()));

    when(jobsDashboardClient.processJobOffline(any(), any(), any(), any()))
        .thenThrow(
            new FeignException.BadRequest(
                "Failed to create job",
                Request.create(HttpMethod.GET, "", new HashMap<>(), null, null, null),
                "Failed to create job".getBytes(),
                headers));

    Exception exception =
        Assertions.assertThrows(
            JobSubmissionException.class,
            () -> csvUploadUtilityService.uploadTransitTimesCsv(TestUtil.ORG_ID, csvFile));
    Assertions.assertNotNull(exception);
  }

  @Test
  void uploadTransitTimesDeleteCsv()
      throws IOException, CsvFormatValidationFailedException, CsvException, JobSubmissionException {
    MultipartFile csvFile = mock(MultipartFile.class);

    String csvFileContent =
        "orgId,BAY,,,,,,,,,\n"
            + "Carrier Service:,ALL-Standard,,,,,,,,,\n"
            + "Destination geoZone / Source geoZone ->,SGZ1,SGZ2,SGZ3\n"
            + "DGZ1,D,d,9.96\n"
            + "DGZ2,10,9,9.9\n"
            + "DGZ3,10,9,9\n";

    when(csvFile.getInputStream())
        .thenReturn(
            new ByteArrayInputStream(csvFileContent.getBytes()),
            new ByteArrayInputStream(csvFileContent.getBytes()),
            new ByteArrayInputStream(csvFileContent.getBytes()));

    when(csvFile.getBytes()).thenReturn(csvFileContent.getBytes());
    when(csvFile.getOriginalFilename()).thenReturn("file_name.csv");

    JobResponse jobResponse = testUtil.createJobResponse(JobTypeEnum.UPLOAD_TRANSIT_TIMES, 9);

    when(jobsDashboardClient.processJobOffline(any(), any(), any(), any()))
        .thenReturn(BaseResponse.builder().payload(jobResponse).build());

    String res = csvUploadUtilityService.uploadTransitTimesCsv(TestUtil.ORG_ID, csvFile);

    Assertions.assertFalse(ObjectUtils.isEmpty(res));
    verify(jobsDashboardClient, times(1)).processJobOffline(any(), any(), any(), any());
  }

  @Test
  void uploadTransitBufferDataTest() throws CommonServiceException {
    when(transitBufferConfigRequestFeign.processTransitBufferConfigRequest(any()))
        .thenReturn(testUtil.getTransitBufferConfigResponseBaseResponse());

    TransitBufferConfigResponse transitBufferConfigResponse =
        csvUploadUtilityService.uploadTransitBufferData(testUtil.getTransitBufferConfigRequest());
    Assertions.assertNotNull(transitBufferConfigResponse);
  }

  @Test
  void uploadTransitBufferDataTestFeignException() {
    Map<String, Collection<String>> headers = new HashMap<>();
    when(transitBufferConfigRequestFeign.processTransitBufferConfigRequest(any()))
        .thenThrow(
            new FeignException.BadRequest(
                "Feign exception while processing transit buffer request",
                Request.create(HttpMethod.GET, "", new HashMap<>(), null, null, null),
                "Feign exception while processing transit buffer request".getBytes(),
                headers));

    Exception exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                csvUploadUtilityService.uploadTransitBufferData(
                    testUtil.getTransitBufferConfigRequest()));
    Assertions.assertNotNull(exception);
  }

  @Test
  void uploadTransitBufferDataTestException() {
    when(transitBufferConfigRequestFeign.processTransitBufferConfigRequest(any()))
        .thenThrow(new RuntimeException("error"));

    Exception exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                csvUploadUtilityService.uploadTransitBufferData(
                    testUtil.getTransitBufferConfigRequest()));
    Assertions.assertNotNull(exception);
  }

  @Test
  void updateTransitBufferDataTest() throws CommonServiceException {
    when(transitBufferConfigRequestFeign.processTransitBufferConfigRequest(any()))
        .thenReturn(testUtil.getTransitBufferConfigResponseBaseResponse());

    TransitBufferConfigResponse transitBufferConfigResponse =
        csvUploadUtilityService.updatingTransitBufferData(testUtil.getTransitBufferConfigRequest());
    Assertions.assertNotNull(transitBufferConfigResponse);
  }

  @Test
  void updateTransitBufferDataTestFeignException() {
    Map<String, Collection<String>> headers = new HashMap<>();
    when(transitBufferConfigRequestFeign.processTransitBufferConfigRequest(any()))
        .thenThrow(
            new FeignException.BadRequest(
                "Feign exception while updating transit buffer request",
                Request.create(HttpMethod.GET, "", new HashMap<>(), null, null, null),
                "Feign exception while updating transit buffer request".getBytes(),
                headers));

    Exception exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                csvUploadUtilityService.updatingTransitBufferData(
                    testUtil.getTransitBufferConfigRequest()));
    Assertions.assertNotNull(exception);
  }

  @Test
  void updateTransitBufferDataTestException() {
    when(transitBufferConfigRequestFeign.processTransitBufferConfigRequest(any()))
        .thenThrow(new RuntimeException("error"));

    Exception exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                csvUploadUtilityService.updatingTransitBufferData(
                    testUtil.getTransitBufferConfigRequest()));
    Assertions.assertNotNull(exception);
  }

  @Test
  void deleteTransitBufferDataTest() throws CommonServiceException {
    when(transitBufferConfigRequestFeign.deleteTransitBufferConfigRequest(any(), any()))
        .thenReturn(testUtil.getTransitBufferConfigResponseBaseResponse());
    csvUploadUtilityService.deletingTransitBufferData(
        testUtil.getTransitBufferConfigResponse().getId(), TestUtil.CREATED_BY);
    verify(transitBufferConfigRequestFeign, times(1))
        .deleteTransitBufferConfigRequest(any(), any());
  }

  @Test
  void deleteTransitBufferDataTestFeignException() {
    Map<String, Collection<String>> headers = new HashMap<>();
    when(transitBufferConfigRequestFeign.deleteTransitBufferConfigRequest(any(), any()))
        .thenThrow(
            new FeignException.BadRequest(
                "Feign exception while deleting transit buffer request",
                Request.create(HttpMethod.GET, "", new HashMap<>(), null, null, null),
                "Feign exception while deleting transit buffer request".getBytes(),
                headers));

    Exception exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                csvUploadUtilityService.deletingTransitBufferData(
                    testUtil.getTransitBufferConfigResponse().getId(), TestUtil.CREATED_BY));
    Assertions.assertNotNull(exception);
  }

  @Test
  void deleteTransitBufferDataTestException() {
    when(transitBufferConfigRequestFeign.deleteTransitBufferConfigRequest(any(), any()))
        .thenThrow(new RuntimeException("error"));

    Exception exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                csvUploadUtilityService.deletingTransitBufferData(
                    testUtil.getTransitBufferConfigResponse().getId(), TestUtil.CREATED_BY));
    Assertions.assertNotNull(exception);
  }
}
