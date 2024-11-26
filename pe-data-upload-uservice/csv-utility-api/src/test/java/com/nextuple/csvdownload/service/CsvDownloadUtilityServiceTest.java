/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.csvdownload.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nextuple.calendar.domain.outbound.NodeCalendarResponse;
import com.nextuple.carrier.domain.outbound.CarrierServiceResponse;
import com.nextuple.common.base.PagePayload;
import com.nextuple.common.base.PagePayload.Pagination;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.common.response.PreSignedUrlResponse;
import com.nextuple.csvdownload.common.TestUtil;
import com.nextuple.csvdownload.common.pojo.DownloadNodeCarrierServiceAndServiceOptionPojo;
import com.nextuple.csvdownload.exception.CarrierServiceException;
import com.nextuple.csvdownload.exception.CsvDownloadUtilityServiceException;
import com.nextuple.csvdownload.exception.CustomRegionServiceException;
import com.nextuple.csvdownload.exception.JobSubmissionException;
import com.nextuple.csvdownload.exception.PostalCodeTimezoneServiceException;
import com.nextuple.csvdownload.exception.TransitServiceException;
import com.nextuple.csvdownload.service.v1.ProcessingRequestFactory;
import com.nextuple.csvdownload.service.v1.impl.CalendarProcessingRequestImpl;
import com.nextuple.csvdownload.service.v1.impl.NeipProcessingRequestImpl;
import com.nextuple.csvdownload.service.v1.impl.TransitTimeProcessingRequestImpl;
import com.nextuple.csvdownload.service.v1.impl.ZoneProcessingRequestImpl;
import com.nextuple.dataupload.common.feign.DataUploadFeign;
import com.nextuple.dataupload.common.outbound.NodeAndServiceOptionResponse;
import com.nextuple.dataupload.common.outbound.NodeCarrierServiceAndServiceOptionResponse;
import com.nextuple.dataupload.common.outbound.ProcessingTimeBufferResponse;
import com.nextuple.dataupload.common.utils.DataUploadUtil;
import com.nextuple.jobs.framework.common.domain.enums.ApiStatusEnum;
import com.nextuple.jobs.framework.common.domain.enums.JobTypeEnum;
import com.nextuple.jobs.framework.common.domain.pojo.JobDto;
import com.nextuple.jobs.framework.common.domain.pojo.RecordStatusDto;
import com.nextuple.node.carrier.domain.outbound.NodeCarrierResponse;
import com.nextuple.node.domain.dto.NodeDto;
import com.nextuple.plt.client.FPMServiceClient;
import com.nextuple.plt.exception.JobException;
import com.nextuple.plt.exception.JobRecordException;
import com.nextuple.postal.code.timezone.api.domain.dto.CustomRegionDto;
import com.nextuple.postal.code.timezone.api.domain.dto.CustomRegionInfo;
import com.nextuple.postal.code.timezone.api.domain.feign.PostalCodeFeign;
import com.nextuple.postal.code.timezone.api.domain.outbound.PostalCodeResponse;
import com.nextuple.promise.sourcing.rule.api.domain.feign.HolidayCutoffUIFeign;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.HolidayCutoffUIRequest;
import com.nextuple.transit.domain.feign.ITransitBufferFeign;
import com.nextuple.transit.domain.feign.TransferScheduleFeign;
import com.nextuple.transit.domain.inbound.FetchTransferScheduleRequest;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.IntStream;
import jdk.jfr.Description;
import org.apache.commons.io.FileUtils;
import org.joda.time.LocalDate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.ObjectUtils;

@ExtendWith(MockitoExtension.class)
class CsvDownloadUtilityServiceTest {

  @Mock private JobsConsumerService jobsConsumerService;
  @Mock private JobsDashboardService jobsDashboardService;
  @Mock private PostalCodeTimeZoneResponseService postalCodeTimeZoneResponseService;
  @Mock private TransitResponseService transitResponseService;
  @Mock private DataUploadFeign dataUploadFeign;

  @Mock private CarrierResponseService carrierResponseService;
  @Mock private CalenderResponseService calenderResponseService;
  @Mock private ProcessingTimeBuffersService processingTimeBuffersService;
  @Mock private NodeResponseService nodeResponseService;
  @Mock private NodeCarrierResponseService nodeCarrierResponseService;
  @Mock private ProcessingRequestFactory processingRequestFactory;
  @Mock private CalendarProcessingRequestImpl calendarProcessingRequest;
  @Mock private NeipProcessingRequestImpl neipProcessingRequest;
  @Mock private TransitTimeProcessingRequestImpl transitTimeProcessingRequest;
  @Mock private ZoneProcessingRequestImpl zoneProcessingRequest;
  @Mock private ITransitBufferFeign<?, ?> transitBufferFeign;
  @Mock private HolidayCutoffUIFeign holidayCutoffUIFeign;
  @Mock CostDefinitionService costDefinitionService;
  @Mock private TransferScheduleFeign transferScheduleFeign;
  @InjectMocks private CsvDownloadUtilityService csvDownloadUtilityService;
  @InjectMocks private TestUtil testUtil;
  @Mock private FPMServiceClient fpmServiceClient;
  @Captor ArgumentCaptor<File> csvCaptor;
  @Mock private ObjectMapper objectMapper;
  @Mock private Environment env;
  @Mock private PostalCodeFeign postalCodeFeign;

  @BeforeEach
  public void init() {
    ReflectionTestUtils.setField(csvDownloadUtilityService, "noOfRecordsPerPage", 200);
    ReflectionTestUtils.setField(csvDownloadUtilityService, "maxNoOfCarrierServicesForDownload", 5);
    ReflectionTestUtils.setField(
        csvDownloadUtilityService, "transitBufferFeign", transitBufferFeign);
    ReflectionTestUtils.setField(csvDownloadUtilityService, "fpmServiceClient", fpmServiceClient);
  }

  @Test
  void downloadLogsAsCsvForProcessingLeadTimes() throws CommonServiceException {
    when(jobsConsumerService.getJob(TestUtil.JOB_ID, TestUtil.ORG_ID))
        .thenReturn(testUtil.getJobDto());
    when(jobsDashboardService.getJobRecords(TestUtil.JOB_ID, TestUtil.ORG_ID, TestUtil.STATUS))
        .thenReturn(testUtil.getJobRecordsForProcessingLeadTimes());

    String csvContent =
        csvDownloadUtilityService.downloadLogsAsCsv(
            TestUtil.JOB_ID, TestUtil.ORG_ID, TestUtil.STATUS);
    assertFalse(ObjectUtils.isEmpty(csvContent));
  }

  @Test
  void downloadLogsAsCsvForTransitTime() throws CommonServiceException {
    JobDto jobDto = testUtil.getJobDto();
    jobDto.setJobType(JobTypeEnum.UPLOAD_TRANSIT_TIMES);
    when(jobsConsumerService.getJob(TestUtil.JOB_ID, TestUtil.ORG_ID)).thenReturn(jobDto);
    when(jobsDashboardService.getJobRecords(anyString(), anyString(), any()))
        .thenReturn(List.of(testUtil.getJobRecordsForTransitTimes()));

    String csvContent =
        csvDownloadUtilityService.downloadLogsAsCsv(
            TestUtil.JOB_ID, TestUtil.ORG_ID, Optional.of(ApiStatusEnum.FAILURE.name()));
    assertFalse(ObjectUtils.isEmpty(csvContent));
  }

  @Test
  void downloadLogsAsCsvForTransitTimeEmptyErrorMessage() throws CommonServiceException {
    JobDto jobDto = testUtil.getJobDto();
    RecordStatusDto recordStatusDto = testUtil.getJobRecordsForTransitTimes();
    recordStatusDto.setErrorMessage("");
    jobDto.setJobType(JobTypeEnum.UPLOAD_TRANSIT_TIMES);
    when(jobsConsumerService.getJob(TestUtil.JOB_ID, TestUtil.ORG_ID)).thenReturn(jobDto);
    when(jobsDashboardService.getJobRecords(anyString(), anyString(), any()))
        .thenReturn(List.of(recordStatusDto));

    String csvContent =
        csvDownloadUtilityService.downloadLogsAsCsv(
            TestUtil.JOB_ID, TestUtil.ORG_ID, Optional.of(ApiStatusEnum.FAILURE.name()));
    assertFalse(ObjectUtils.isEmpty(csvContent));
  }

  @Test
  void downloadLogsAsCsvForTransitTimeColumnOrder() throws CommonServiceException {
    JobDto jobDto = testUtil.getJobDto();
    jobDto.setJobType(JobTypeEnum.UPLOAD_TRANSIT_TIMES);
    var recordStatusDto1 = testUtil.getJobRecordsForTransitTimes();
    var recordStatusDto2 = testUtil.getJobRecordsForTransitTimes();
    recordStatusDto2.setRequestBody(testUtil.createtransitTimesRequestBodyJson("AAB", "M1R"));
    var recordStatusDto3 = testUtil.getJobRecordsForTransitTimes();
    recordStatusDto3.setRequestBody(testUtil.createtransitTimesRequestBodyJson("AAA", "M1R"));

    when(jobsConsumerService.getJob(TestUtil.JOB_ID, TestUtil.ORG_ID)).thenReturn(jobDto);
    when(jobsDashboardService.getJobRecords(anyString(), anyString(), any()))
        .thenReturn(List.of(recordStatusDto1, recordStatusDto2, recordStatusDto3));

    String csvContent =
        csvDownloadUtilityService.downloadLogsAsCsv(
            TestUtil.JOB_ID, TestUtil.ORG_ID, Optional.of(ApiStatusEnum.FAILURE.name()));
    assertFalse(ObjectUtils.isEmpty(csvContent));
    assertEquals("Destination geoZone / Source geoZone ->,AAA,A0A,AAB", csvContent.split("\n")[2]);
  }

  @Test
  void downloadLogsAsCsvForTransitTimeFeignRetryableException() throws CommonServiceException {
    JobDto jobDto = testUtil.getJobDto();
    RecordStatusDto recordStatusDto = testUtil.getJobRecordsForTransitTimes();
    recordStatusDto.setException("feign.RetryableException");
    jobDto.setJobType(JobTypeEnum.UPLOAD_TRANSIT_TIMES);
    when(jobsConsumerService.getJob(TestUtil.JOB_ID, TestUtil.ORG_ID)).thenReturn(jobDto);
    when(jobsDashboardService.getJobRecords(anyString(), anyString(), any()))
        .thenReturn(List.of(recordStatusDto));

    String csvContent =
        csvDownloadUtilityService.downloadLogsAsCsv(
            TestUtil.JOB_ID, TestUtil.ORG_ID, Optional.of(ApiStatusEnum.FAILURE.name()));
    assertFalse(ObjectUtils.isEmpty(csvContent));
  }

  @Test
  void downloadLogsAsCsvForCalendarV1()
      throws CommonServiceException, JobSubmissionException, IOException {
    JobDto jobDto = testUtil.getJobDto();
    jobDto.setJobType(JobTypeEnum.UPLOAD_CALENDER);
    when(jobsConsumerService.getJob(TestUtil.JOB_ID, TestUtil.ORG_ID)).thenReturn(jobDto);
    when(processingRequestFactory.getModuleByJobType(JobTypeEnum.UPLOAD_CALENDER))
        .thenReturn(calendarProcessingRequest);
    when(calendarProcessingRequest.downloadErrorLogs(any(), any()))
        .thenReturn(testUtil.getPreSignedUrlResponse());

    var csvContent =
        csvDownloadUtilityService.downloadLogsAsCsvV1(
            TestUtil.JOB_ID, TestUtil.ORG_ID, Optional.of(ApiStatusEnum.FAILURE.name()));
    assertFalse(ObjectUtils.isEmpty(csvContent));
  }

  @Test
  void downloadLogsAsCsvForCalendarV1UploadTransitTimes()
      throws CommonServiceException, JobSubmissionException, IOException {
    JobDto jobDto = testUtil.getJobDto();
    jobDto.setJobType(JobTypeEnum.UPLOAD_TRANSIT_TIMES);
    when(jobsConsumerService.getJob(TestUtil.JOB_ID, TestUtil.ORG_ID)).thenReturn(jobDto);
    when(processingRequestFactory.getModuleByJobType(JobTypeEnum.UPLOAD_TRANSIT_TIMES))
        .thenReturn(transitTimeProcessingRequest);
    when(transitTimeProcessingRequest.downloadTransitTimeErrorLogs(any(), any()))
        .thenReturn(testUtil.getPreSignedUrlResponse());

    var csvContent =
        csvDownloadUtilityService.downloadLogsAsCsvV1(
            TestUtil.JOB_ID, TestUtil.ORG_ID, Optional.of(ApiStatusEnum.FAILURE.name()));
    assertFalse(ObjectUtils.isEmpty(csvContent));
  }

  @Test
  void downloadLogsAsCsvForCalendarV1Exception()
      throws CommonServiceException, JobSubmissionException, IOException {
    JobDto jobDto = testUtil.getJobDto();
    jobDto.setJobType(JobTypeEnum.UPLOAD_CALENDER);
    when(jobsConsumerService.getJob(TestUtil.JOB_ID, TestUtil.ORG_ID))
        .thenThrow(new RuntimeException("Error while fetching job"));

    Exception exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                csvDownloadUtilityService.downloadLogsAsCsvV1(
                    TestUtil.JOB_ID, TestUtil.ORG_ID, Optional.of(ApiStatusEnum.FAILURE.name())));
    assertNotNull(exception);
  }

  @Test
  void downloadTransitTimeAndProcessingLeadTimeCsvExceptionTest() throws CommonServiceException {
    when(jobsConsumerService.getJob(TestUtil.JOB_ID, TestUtil.ORG_ID))
        .thenReturn(testUtil.getJobDto2());

    Exception exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                csvDownloadUtilityService.downloadLogsAsCsv(
                    TestUtil.JOB_ID, TestUtil.ORG_ID, TestUtil.STATUS));
    assertNotNull(exception);
  }

  @Test
  void downloadTransitTimesForSourceAndDestinationRegion()
      throws PostalCodeTimezoneServiceException,
          TransitServiceException,
          CsvDownloadUtilityServiceException {
    when(postalCodeTimeZoneResponseService.getFSAsByOrgIdAndState(
            TestUtil.ORG_ID, TestUtil.DESTINATION_REGION))
        .thenReturn(List.of(TestUtil.DESTINATION_FSA));

    when(postalCodeTimeZoneResponseService.getFSAsByOrgIdAndState(
            TestUtil.ORG_ID, TestUtil.SOURCE_REGION))
        .thenReturn(List.of(TestUtil.SOURCE_FSA));

    when(transitResponseService.getTransitDetails(anyString(), anyString(), any()))
        .thenReturn(List.of(testUtil.getTransitResponse(1.5F)));

    String csvContents =
        csvDownloadUtilityService.downloadTransitTimesForSourceAndDestinationRegion(
            TestUtil.ORG_ID,
            TestUtil.CARRIER_SERVICE_ID,
            TestUtil.SOURCE_REGION,
            TestUtil.DESTINATION_REGION);

    assertFalse(ObjectUtils.isEmpty(csvContents));
    verify(postalCodeTimeZoneResponseService, times(2))
        .getFSAsByOrgIdAndState(anyString(), anyString());
    verify(transitResponseService, times(1)).getTransitDetails(anyString(), anyString(), any());
  }

  @Test
  void downloadTransitTimesForSourceAndDestinationRegionException()
      throws PostalCodeTimezoneServiceException, TransitServiceException {
    when(postalCodeTimeZoneResponseService.getFSAsByOrgIdAndState(
            TestUtil.ORG_ID, TestUtil.DESTINATION_REGION))
        .thenReturn(List.of(TestUtil.DESTINATION_FSA));

    when(postalCodeTimeZoneResponseService.getFSAsByOrgIdAndState(
            TestUtil.ORG_ID, TestUtil.SOURCE_REGION))
        .thenReturn(List.of(TestUtil.SOURCE_FSA + "1"));

    when(transitResponseService.getTransitDetails(anyString(), anyString(), any()))
        .thenReturn(List.of(testUtil.getTransitResponse(1.5F)));

    Exception exception =
        Assertions.assertThrows(
            CsvDownloadUtilityServiceException.class,
            () ->
                csvDownloadUtilityService.downloadTransitTimesForSourceAndDestinationRegion(
                    TestUtil.ORG_ID,
                    TestUtil.CARRIER_SERVICE_ID,
                    TestUtil.SOURCE_REGION,
                    TestUtil.DESTINATION_REGION));
    assertNotNull(exception);
    verify(postalCodeTimeZoneResponseService, times(2))
        .getFSAsByOrgIdAndState(anyString(), anyString());
    verify(transitResponseService, times(1)).getTransitDetails(anyString(), anyString(), any());
  }

  @Test
  void downloadLogsAsCsvV1ForUploadZones()
      throws CommonServiceException, JobSubmissionException, IOException {
    JobDto jobDto = testUtil.getJobDto();
    jobDto.setJobType(JobTypeEnum.UPLOAD_ZONES);
    when(jobsConsumerService.getJob(TestUtil.JOB_ID, TestUtil.ORG_ID)).thenReturn(jobDto);
    when(processingRequestFactory.getModuleByJobType(JobTypeEnum.UPLOAD_ZONES))
        .thenReturn(zoneProcessingRequest);
    when(zoneProcessingRequest.downloadTransitTimeErrorLogs(any(), any()))
        .thenReturn(testUtil.getPreSignedUrlResponse());

    var csvContent =
        csvDownloadUtilityService.downloadLogsAsCsvV1(
            TestUtil.JOB_ID, TestUtil.ORG_ID, Optional.of(ApiStatusEnum.FAILURE.name()));
    assertFalse(ObjectUtils.isEmpty(csvContent));
  }

  @Test
  @Description("when custom region is not null")
  void downloadMarketRegionForOrgIdAndCountry_Test() throws PostalCodeTimezoneServiceException {
    when(postalCodeTimeZoneResponseService.getPostalCodeTimeZoneByOrgIdAndCountry(
            anyString(), anyString()))
        .thenReturn(List.of(testUtil.getPostalCodeResponse1()));

    String csvContents =
        csvDownloadUtilityService.downloadMarketRegionForOrgIdAndCountry(
            TestUtil.ORG_ID, TestUtil.COUNTRY);

    assertFalse(ObjectUtils.isEmpty(csvContents));
    verify(postalCodeTimeZoneResponseService, times(1))
        .getPostalCodeTimeZoneByOrgIdAndCountry(anyString(), anyString());
  }

  @Test
  @Description("When custom region is null in the response it will be saved as empty string")
  void downloadMarketRegionForOrgIdAndCountry_Test_2() throws PostalCodeTimezoneServiceException {
    when(postalCodeTimeZoneResponseService.getPostalCodeTimeZoneByOrgIdAndCountry(
            anyString(), anyString()))
        .thenReturn(List.of(testUtil.getPostalCodeResponse2()));

    String csvContents =
        csvDownloadUtilityService.downloadMarketRegionForOrgIdAndCountry(
            TestUtil.ORG_ID, TestUtil.COUNTRY);

    assertFalse(ObjectUtils.isEmpty(csvContents));
    verify(postalCodeTimeZoneResponseService, times(1))
        .getPostalCodeTimeZoneByOrgIdAndCountry(TestUtil.ORG_ID, TestUtil.COUNTRY);
    List<PostalCodeResponse> postalCodeResponse =
        postalCodeTimeZoneResponseService.getPostalCodeTimeZoneByOrgIdAndCountry(
            TestUtil.ORG_ID, TestUtil.COUNTRY);
    assertTrue(Objects.isNull(postalCodeResponse.get(0).getCustomRegion()));
  }

  @Test
  void downloadCarrierServiceData_Test()
      throws CarrierServiceException, TransitServiceException, IOException {
    when(carrierResponseService.getCarrierService(anyString()))
        .thenReturn(List.of(testUtil.getCarrierServiceResponse()));
    when(calenderResponseService.getCarrierServiceCalender(anyString(), anyString()))
        .thenReturn(List.of(testUtil.getCarrierServiceCalendarResponse2("2022-01-01")));

    when(transitResponseService.getTransitTimeEntries(anyString(), anyString()))
        .thenReturn(testUtil.getTransitTimeEntriesDto());
    File csvContents = csvDownloadUtilityService.downloadCarrierServiceDataCSV(TestUtil.ORG_ID);

    assertFalse(ObjectUtils.isEmpty(csvContents));
    verify(calenderResponseService, times(1)).getCarrierServiceCalender(anyString(), anyString());
    verify(transitResponseService, times(1)).getTransitTimeEntries(anyString(), anyString());
    verify(carrierResponseService, times(1)).getCarrierService(anyString());
  }

  @Test
  void downloadCarrierServiceData_TestNoCalenderExist()
      throws CarrierServiceException, TransitServiceException, IOException {
    List<CarrierServiceResponse> carrierResponseServiceResponses = new ArrayList<>();
    IntStream.range(0, 6)
        .forEach(
            i -> {
              carrierResponseServiceResponses.add(testUtil.getCarrierServiceResponse());
            });
    when(carrierResponseService.getCarrierService(anyString()))
        .thenReturn(carrierResponseServiceResponses);
    when(calenderResponseService.getCarrierServiceCalender(anyString(), anyString()))
        .thenThrow(CarrierServiceException.class);

    when(transitResponseService.getTransitTimeEntries(anyString(), anyString()))
        .thenReturn(testUtil.getTransitTimeEntriesDto());
    File csvContents = csvDownloadUtilityService.downloadCarrierServiceDataCSV(TestUtil.ORG_ID);
    assertFalse(ObjectUtils.isEmpty(csvContents));
    verify(calenderResponseService, times(5)).getCarrierServiceCalender(anyString(), anyString());
    verify(transitResponseService, times(5)).getTransitTimeEntries(anyString(), anyString());
    verify(carrierResponseService, times(1)).getCarrierService(anyString());
  }

  @Test
  void downloadCarrierServiceData_TestErrorTransitEntries()
      throws CarrierServiceException, TransitServiceException, IOException {
    when(carrierResponseService.getCarrierService(anyString()))
        .thenReturn(List.of(testUtil.getCarrierServiceResponse()));
    when(calenderResponseService.getCarrierServiceCalender(anyString(), anyString()))
        .thenReturn(List.of(testUtil.getCarrierServiceCalendarResponse()));

    when(transitResponseService.getTransitTimeEntries(anyString(), anyString()))
        .thenThrow(TransitServiceException.class);
    File csvContents = csvDownloadUtilityService.downloadCarrierServiceDataCSV(TestUtil.ORG_ID);

    assertFalse(ObjectUtils.isEmpty(csvContents));
    verify(calenderResponseService, times(1)).getCarrierServiceCalender(anyString(), anyString());
    verify(transitResponseService, times(1)).getTransitTimeEntries(anyString(), anyString());
    verify(carrierResponseService, times(1)).getCarrierService(anyString());
  }

  @Test
  void downloadCarrierServiceDataWithFutureEffectiveDateTest()
      throws CarrierServiceException, TransitServiceException, IOException {
    when(carrierResponseService.getCarrierService(anyString()))
        .thenReturn(List.of(testUtil.getCarrierServiceResponse()));
    when(calenderResponseService.getCarrierServiceCalender(anyString(), anyString()))
        .thenReturn(List.of(testUtil.getCarrierServiceCalendarResponse2("2024-01-01")));

    when(transitResponseService.getTransitTimeEntries(anyString(), anyString()))
        .thenReturn(testUtil.getTransitTimeEntriesDto());
    File csvContents = csvDownloadUtilityService.downloadCarrierServiceDataCSV(TestUtil.ORG_ID);

    assertFalse(ObjectUtils.isEmpty(csvContents));
    verify(calenderResponseService, times(1)).getCarrierServiceCalender(anyString(), anyString());
    verify(transitResponseService, times(1)).getTransitTimeEntries(anyString(), anyString());
    verify(carrierResponseService, times(1)).getCarrierService(anyString());
  }

  @Test
  void downloadCarrierServiceDataWithNATest()
      throws CarrierServiceException, TransitServiceException, IOException {
    when(carrierResponseService.getCarrierService(anyString()))
        .thenReturn(List.of(testUtil.getCarrierServiceResponse()));
    when(transitResponseService.getTransitTimeEntries(anyString(), anyString()))
        .thenReturn(testUtil.getTransitTimeEntriesDto());
    try (MockedStatic<DataUploadUtil> utilities = mockStatic(DataUploadUtil.class)) {
      utilities
          .when(() -> DataUploadUtil.getActiveCalendarResponse(anyList()))
          .thenReturn(Optional.empty());
    }

    File csvContents = csvDownloadUtilityService.downloadCarrierServiceDataCSV(TestUtil.ORG_ID);

    assertFalse(ObjectUtils.isEmpty(csvContents));
    verify(calenderResponseService, times(1)).getCarrierServiceCalender(anyString(), anyString());
    verify(transitResponseService, times(1)).getTransitTimeEntries(anyString(), anyString());
    verify(carrierResponseService, times(1)).getCarrierService(anyString());
  }

  @Test
  void downloadNodeCarrierServiceAndServiceOptionsDataCSV() throws IOException {
    ReflectionTestUtils.setField(csvDownloadUtilityService, "noOfRecordsPerPage", 200);
    when(dataUploadFeign.getListOfNodeCarrierServiceAndServiceOptionDetails(
            any(), any(), any(), any(), any()))
        .thenReturn(
            BaseResponse.builder()
                .payload(testUtil.getNodeCarrierServiceAndServiceOptionResponse(1))
                .build());

    DownloadNodeCarrierServiceAndServiceOptionPojo pojo =
        csvDownloadUtilityService.downloadNodeCarrierServiceAndServiceOptionsDataCSV(
            TestUtil.ORG_ID);

    verify(dataUploadFeign, times(1))
        .getListOfNodeCarrierServiceAndServiceOptionDetails(any(), any(), any(), any(), any());

    assertNotNull(pojo);
    assertNotNull(pojo.getFileContents());
    assertTrue(pojo.getContentsLength() > 0);
  }

  @Test
  @Description("Test Download Nodes carrier pickup calendar")
  void downloadNodesCarrierPickupCalendarByOrgId()
      throws IOException, CarrierServiceException, CommonServiceException {
    ReflectionTestUtils.setField(csvDownloadUtilityService, "noOfRecordsPerPage", 200);
    when(calenderResponseService.getNodeCarrierServiceCalender(any()))
        .thenReturn(testUtil.getNodeCarrierServiceCalendarResponse());
    when(nodeCarrierResponseService.getNodeCarrierResponseByOrgIdNodeIdAndCarrierServiceId(
            any(), any(), any()))
        .thenReturn(testUtil.getNodeCarrierResponseList());
    File file =
        csvDownloadUtilityService.downloadNodesCarrierPickupCalendarByOrgId(TestUtil.ORG_ID);
    verify(calenderResponseService, times(1)).getNodeCarrierServiceCalender(any());
    assertNotNull(file);
    assertNotNull(file.getName());
    assertTrue(file.getName().startsWith("download-nodes-carrier-pickup-calendar"));
  }

  @Test
  @Description("Test Download Nodes carrier pickup calendar when pickup time is null")
  void downloadNodesCarrierPickupCalendarByOrgIdWhenPickUpTimeIsNull()
      throws IOException, CarrierServiceException, CommonServiceException {
    ReflectionTestUtils.setField(csvDownloadUtilityService, "noOfRecordsPerPage", 200);
    when(calenderResponseService.getNodeCarrierServiceCalender(any()))
        .thenReturn(testUtil.getNodeCarrierServiceCalendarResponse());
    when(nodeCarrierResponseService.getNodeCarrierResponseByOrgIdNodeIdAndCarrierServiceId(
            any(), any(), any()))
        .thenReturn(List.of(testUtil.getNodeCarrierResponse2()));
    File file =
        csvDownloadUtilityService.downloadNodesCarrierPickupCalendarByOrgId(TestUtil.ORG_ID);
    verify(calenderResponseService, times(1)).getNodeCarrierServiceCalender(any());
    assertNotNull(file);
    assertNotNull(file.getName());
    assertTrue(file.getName().startsWith("download-nodes-carrier-pickup-calendar"));
  }

  @Test
  void downloadNodeCarrierServiceAndServiceOptionsDataCSVEmptyPagePayloadData() throws IOException {
    ReflectionTestUtils.setField(csvDownloadUtilityService, "noOfRecordsPerPage", 200);
    when(dataUploadFeign.getListOfNodeCarrierServiceAndServiceOptionDetails(
            any(), any(), any(), any(), any()))
        .thenReturn(BaseResponse.builder().payload(null).build());

    DownloadNodeCarrierServiceAndServiceOptionPojo pojo =
        csvDownloadUtilityService.downloadNodeCarrierServiceAndServiceOptionsDataCSV(
            TestUtil.ORG_ID);

    verify(dataUploadFeign, times(1))
        .getListOfNodeCarrierServiceAndServiceOptionDetails(any(), any(), any(), any(), any());

    assertNotNull(pojo);
    assertNotNull(pojo.getFileContents());
    assertEquals(0, pojo.getContentsLength());
  }

  @Test
  void downloadNodeCarrierServiceAndServiceOptionsDataCSVNullResponse() throws IOException {
    ReflectionTestUtils.setField(csvDownloadUtilityService, "noOfRecordsPerPage", 200);
    when(dataUploadFeign.getListOfNodeCarrierServiceAndServiceOptionDetails(
            any(), any(), any(), any(), any()))
        .thenReturn(null);

    DownloadNodeCarrierServiceAndServiceOptionPojo pojo =
        csvDownloadUtilityService.downloadNodeCarrierServiceAndServiceOptionsDataCSV(
            TestUtil.ORG_ID);

    verify(dataUploadFeign, times(1))
        .getListOfNodeCarrierServiceAndServiceOptionDetails(any(), any(), any(), any(), any());

    assertNotNull(pojo);
    assertNotNull(pojo.getFileContents());
    assertEquals(0, pojo.getContentsLength());
  }

  @Test
  void downloadNodeCarrierServiceAndServiceOptionsDataCSVMultiplePages() throws IOException {
    ReflectionTestUtils.setField(csvDownloadUtilityService, "noOfRecordsPerPage", 200);
    NodeCarrierServiceAndServiceOptionResponse response1 =
        testUtil.getNodeCarrierServiceAndServiceOptionResponse();

    when(dataUploadFeign.getListOfNodeCarrierServiceAndServiceOptionDetails(
            TestUtil.ORG_ID, null, 200, null, null))
        .thenReturn(BaseResponse.builder().payload(responsePagePayload(response1, 1)).build());

    when(dataUploadFeign.getListOfNodeCarrierServiceAndServiceOptionDetails(
            TestUtil.ORG_ID, 2, 200, null, null))
        .thenReturn(BaseResponse.builder().payload(responsePagePayload(response1, 2)).build());

    DownloadNodeCarrierServiceAndServiceOptionPojo pojo =
        csvDownloadUtilityService.downloadNodeCarrierServiceAndServiceOptionsDataCSV(
            TestUtil.ORG_ID);

    verify(dataUploadFeign, times(1))
        .getListOfNodeCarrierServiceAndServiceOptionDetails(TestUtil.ORG_ID, null, 200, null, null);

    verify(dataUploadFeign, times(1))
        .getListOfNodeCarrierServiceAndServiceOptionDetails(TestUtil.ORG_ID, 2, 200, null, null);

    assertNotNull(pojo);
    assertNotNull(pojo.getFileContents());
    assertTrue(pojo.getContentsLength() > 0);
  }

  private PagePayload<NodeCarrierServiceAndServiceOptionResponse> responsePagePayload(
      NodeCarrierServiceAndServiceOptionResponse response, int pageNo) {
    PagePayload<NodeCarrierServiceAndServiceOptionResponse> nodeCarrierResponseServicePagePayload =
        new PagePayload<>();

    response.setNodeId(TestUtil.NODE_ID + 1);
    response.setOrgId(TestUtil.ORG_ID);
    response.setStreet(TestUtil.STREET);
    response.setCity(TestUtil.CITY);
    response.setState(TestUtil.STATE);
    response.setZipCode(TestUtil.ZIP_CODE);
    response.setCarrierServices(List.of(TestUtil.CARRIER_SERVICE_ID));
    response.setServiceOptions(List.of(TestUtil.SERVICE_OPTION));
    response.setActiveCombination(List.of(testUtil.getActiveCombination()));

    Pagination pagination = new Pagination();
    pagination.setTotalPages(2);
    pagination.setCurrentPage(pageNo);
    pagination.setSortBy("nodeId");
    pagination.setSortOrder("ASC");
    pagination.setTotalRecords(2);
    nodeCarrierResponseServicePagePayload.setPagination(pagination);
    nodeCarrierResponseServicePagePayload.setData(List.of(response));

    return nodeCarrierResponseServicePagePayload;
  }

  @Test
  void downloadNodeAndServiceOptionsDataCSV() throws IOException {
    ReflectionTestUtils.setField(csvDownloadUtilityService, "noOfRecordsPerPage", 200);
    when(dataUploadFeign.getNodeServiceOption(any(), any(), any(), any(), any()))
        .thenReturn(
            BaseResponse.builder().payload(testUtil.getNodeAndServiceOptionResponse(1)).build());

    DownloadNodeCarrierServiceAndServiceOptionPojo pojo =
        csvDownloadUtilityService.downloadNodeAndServiceOptionsDataCSV(TestUtil.ORG_ID);

    verify(dataUploadFeign, times(1)).getNodeServiceOption(any(), any(), any(), any(), any());

    assertNotNull(pojo);
    assertNotNull(pojo.getFileContents());
    assertTrue(pojo.getContentsLength() > 0);
  }

  @Test
  void downloadNodeAndServiceOptionsDataCSVEmptyPagePayloadData() throws IOException {
    ReflectionTestUtils.setField(csvDownloadUtilityService, "noOfRecordsPerPage", 200);
    when(dataUploadFeign.getNodeServiceOption(any(), any(), any(), any(), any()))
        .thenReturn(BaseResponse.builder().payload(null).build());

    DownloadNodeCarrierServiceAndServiceOptionPojo pojo =
        csvDownloadUtilityService.downloadNodeAndServiceOptionsDataCSV(TestUtil.ORG_ID);

    verify(dataUploadFeign, times(1)).getNodeServiceOption(any(), any(), any(), any(), any());

    assertNotNull(pojo);
    assertNotNull(pojo.getFileContents());
    assertEquals(0, pojo.getContentsLength());
  }

  @Test
  void downloadNodeAndServiceOptionsDataCSVNullResponse() throws IOException {
    ReflectionTestUtils.setField(csvDownloadUtilityService, "noOfRecordsPerPage", 200);
    when(dataUploadFeign.getNodeServiceOption(any(), any(), any(), any(), any())).thenReturn(null);

    DownloadNodeCarrierServiceAndServiceOptionPojo pojo =
        csvDownloadUtilityService.downloadNodeAndServiceOptionsDataCSV(TestUtil.ORG_ID);

    verify(dataUploadFeign, times(1)).getNodeServiceOption(any(), any(), any(), any(), any());

    assertNotNull(pojo);
    assertNotNull(pojo.getFileContents());
    assertEquals(0, pojo.getContentsLength());
  }

  @Test
  void downloadNodeAndServiceOptionsDataCSVMultiplePages() throws IOException {
    ReflectionTestUtils.setField(csvDownloadUtilityService, "noOfRecordsPerPage", 200);
    NodeAndServiceOptionResponse response1 = testUtil.getNodeAndServiceOptionResponse();

    when(dataUploadFeign.getNodeServiceOption(TestUtil.ORG_ID, null, 200, null, null))
        .thenReturn(BaseResponse.builder().payload(responsePagePayload1(response1, 1)).build());

    when(dataUploadFeign.getNodeServiceOption(TestUtil.ORG_ID, 2, 200, null, null))
        .thenReturn(BaseResponse.builder().payload(responsePagePayload1(response1, 2)).build());

    DownloadNodeCarrierServiceAndServiceOptionPojo pojo =
        csvDownloadUtilityService.downloadNodeAndServiceOptionsDataCSV(TestUtil.ORG_ID);

    verify(dataUploadFeign, times(1)).getNodeServiceOption(TestUtil.ORG_ID, null, 200, null, null);

    verify(dataUploadFeign, times(1)).getNodeServiceOption(TestUtil.ORG_ID, 2, 200, null, null);

    assertNotNull(pojo);
    assertNotNull(pojo.getFileContents());
    assertTrue(pojo.getContentsLength() > 0);
  }

  private PagePayload<NodeAndServiceOptionResponse> responsePagePayload1(
      NodeAndServiceOptionResponse response, int pageNo) {
    PagePayload<NodeAndServiceOptionResponse> nodeServicePagePayload = new PagePayload<>();

    response.setNodeId(TestUtil.NODE_ID + 1);
    response.setOrgId(TestUtil.ORG_ID);
    response.setNodeType("STORE");
    response.setStreet(TestUtil.STREET);
    response.setCity(TestUtil.CITY);
    response.setState(TestUtil.STATE);
    response.setIsActive(TestUtil.isActive);
    response.setServiceOptions(List.of("STANDARD"));
    response.setProcessingTime(Map.of("STANDARD", 10.0));

    Pagination pagination = new Pagination();
    pagination.setTotalPages(2);
    pagination.setCurrentPage(pageNo);
    pagination.setSortBy("nodeId");
    pagination.setSortOrder("ASC");
    pagination.setTotalRecords(2);
    nodeServicePagePayload.setPagination(pagination);
    nodeServicePagePayload.setData(List.of(response));

    return nodeServicePagePayload;
  }

  @Test
  void downloadProcessingTimeBuffersByOrgIdTest() throws IOException {
    List<ProcessingTimeBufferResponse> responseList =
        List.of(testUtil.getProcessingTimeBufferResponse(TestUtil.NODE_ID));
    when(processingTimeBuffersService.getProcessingTimeBuffers(any(), any()))
        .thenReturn(responseList);
    File file =
        csvDownloadUtilityService.downloadProcessingTimeBuffersByOrgId(
            TestUtil.ORG_ID, TestUtil.NODE_IDS);

    assertNotNull(file);
    verify(processingTimeBuffersService, times(1)).getProcessingTimeBuffers(any(), any());
  }

  @Test
  void downloadProcessingTimeBuffersByOrgIdWithEmptyValuesTest() throws IOException {
    List<ProcessingTimeBufferResponse> responseList =
        List.of(testUtil.getProcessingTimeBufferResponseEmptyValues(TestUtil.NODE_ID));
    when(processingTimeBuffersService.getProcessingTimeBuffers(any(), any()))
        .thenReturn(responseList);

    File file =
        csvDownloadUtilityService.downloadProcessingTimeBuffersByOrgId(
            TestUtil.ORG_ID, TestUtil.NODE_IDS);

    assertNotNull(file);
    verify(processingTimeBuffersService, times(1)).getProcessingTimeBuffers(any(), any());
  }

  @Test
  void downloadProcessingTimeBuffersByOrgIdWithPartialEmptyValuesAndNullValuesTest()
      throws IOException {
    List<ProcessingTimeBufferResponse> responseList =
        List.of(testUtil.getProcessingTimeBufferResponsePartialEmptyValues(TestUtil.NODE_ID));
    when(processingTimeBuffersService.getProcessingTimeBuffers(any(), any()))
        .thenReturn(responseList);

    File file =
        csvDownloadUtilityService.downloadProcessingTimeBuffersByOrgId(
            TestUtil.ORG_ID, TestUtil.NODE_ID);

    assertNotNull(file);
    verify(processingTimeBuffersService, times(1)).getProcessingTimeBuffers(any(), any());
  }

  @Test
  void downloadNodesByOrgIdTest() throws IOException {
    NodeDto node1 = testUtil.getNodeDto(TestUtil.NODE_ID);
    node1.setStartWorkingTime("08:00");
    node1.setLastWorkingTime("16:00");
    List<NodeDto> nodeDtoList = List.of(node1, testUtil.getNodeDto(TestUtil.NODE_ID_2));
    List<NodeCarrierResponse> nodeCarrierResponses = testUtil.getNodeCarrierResponseList();
    List<NodeCalendarResponse> nodeCalendarResponses = testUtil.getNodeCalendarResponseList();

    when(nodeResponseService.getNodeList(any(), any(), any())).thenReturn(nodeDtoList);
    when(calenderResponseService.getNodeCalendar(any(), any())).thenReturn(nodeCalendarResponses);
    when(nodeCarrierResponseService.getNodeCarrierResponse(any(), any()))
        .thenReturn(nodeCarrierResponses);

    File file = csvDownloadUtilityService.downloadNodesByOrgId(TestUtil.ORG_ID, null, null);

    assertNotNull(file);
    List<String> lines = FileUtils.readLines(file, StandardCharsets.UTF_8);
    boolean containsExpectedStartWorkingTime =
        lines.stream().anyMatch(line -> line.contains("08:00"));
    assertTrue(containsExpectedStartWorkingTime);
    verify(nodeResponseService, times(1)).getNodeList(any(), any(), any());
    verify(nodeCarrierResponseService, times(2)).getNodeCarrierResponse(any(), any());
    verify(calenderResponseService, times(2)).getNodeCalendar(any(), any());
  }

  @Test
  @DisplayName("Download nodes by nodeIds and nodeType and orgId")
  void downloadNodesByOrgIdAndNodeIdAndNodeTypeTest() throws IOException {
    NodeDto node1 = testUtil.getNodeDto(TestUtil.NODE_ID);
    node1.setStartWorkingTime("08:00");
    node1.setLastWorkingTime("16:00");
    List<NodeDto> nodeDtoList = List.of(node1, testUtil.getNodeDto(TestUtil.NODE_ID_2));
    List<NodeCarrierResponse> nodeCarrierResponses = testUtil.getNodeCarrierResponseList();
    List<NodeCalendarResponse> nodeCalendarResponses = testUtil.getNodeCalendarResponseList();

    when(nodeResponseService.getNodeList(any(), any(), any())).thenReturn(nodeDtoList);
    when(calenderResponseService.getNodeCalendar(any(), any())).thenReturn(nodeCalendarResponses);
    when(nodeCarrierResponseService.getNodeCarrierResponse(any(), any()))
        .thenReturn(nodeCarrierResponses);

    File file =
        csvDownloadUtilityService.downloadNodesByOrgId(
            TestUtil.ORG_ID, TestUtil.NODE_IDS, TestUtil.NODE_TYPE);

    assertNotNull(file);
    List<String> lines = FileUtils.readLines(file, StandardCharsets.UTF_8);
    boolean containsExpectedStartWorkingTime =
        lines.stream().anyMatch(line -> line.contains("08:00"));
    assertTrue(containsExpectedStartWorkingTime);
    verify(nodeResponseService, times(1)).getNodeList(any(), any(), any());
    verify(nodeCarrierResponseService, times(2)).getNodeCarrierResponse(any(), any());
    verify(calenderResponseService, times(2)).getNodeCalendar(any(), any());
  }

  @Test
  void downloadTransitBufferDetailsTest() {
    PreSignedUrlResponse preSignedUrlResponse = testUtil.getPreSignedUrlResponse();
    when(transitBufferFeign.getTransitBufferDetails(anyLong(), anyString()))
        .thenReturn(BaseResponse.builder().payload(preSignedUrlResponse).build());
    PreSignedUrlResponse response =
        csvDownloadUtilityService.downloadTransitBufferDetails(1L, "user1");
    assertEquals(preSignedUrlResponse, response);
    verify(transitBufferFeign, times(1)).getTransitBufferDetails(anyLong(), anyString());
  }

  @Test
  void downloadNodesByOrgIdEmptyValuesTest() throws IOException {
    NodeDto node1 = testUtil.getNodeDto(TestUtil.NODE_ID);
    node1.setStartWorkingTime("08:00");
    node1.setLastWorkingTime("12:00");
    List<NodeDto> nodeDtoList = List.of(node1, testUtil.getNodeDto(TestUtil.NODE_ID_2));
    when(nodeResponseService.getNodeList(any(), any(), any())).thenReturn(nodeDtoList);
    when(calenderResponseService.getNodeCalendar(any(), any())).thenReturn(new ArrayList<>());
    when(nodeCarrierResponseService.getNodeCarrierResponse(any(), any()))
        .thenReturn(new ArrayList<>());

    File file = csvDownloadUtilityService.downloadNodesByOrgId(TestUtil.ORG_ID, null, null);

    assertNotNull(file);
    List<String> lines = FileUtils.readLines(file, StandardCharsets.UTF_8);
    boolean containsExpectedLastWorkingTime =
        lines.stream().anyMatch(line -> line.contains("12:00"));
    assertTrue(containsExpectedLastWorkingTime);
    verify(nodeResponseService, times(1)).getNodeList(any(), any(), any());
    verify(nodeCarrierResponseService, times(2)).getNodeCarrierResponse(any(), any());
    verify(calenderResponseService, times(2)).getNodeCalendar(any(), any());
  }

  @Test
  void downloadCustomRegionsByOrgIdTest() throws IOException {
    List<CustomRegionDto> customRegionDtoList =
        List.of(
            testUtil.getCustomRegionDto(TestUtil.REGION_ID),
            testUtil.getCustomRegionDto(TestUtil.REGION_ID_2));

    when(postalCodeTimeZoneResponseService.getCustomRegionsByOrgId(any()))
        .thenReturn(customRegionDtoList);

    File file = csvDownloadUtilityService.downloadCustomRegionsForOrgId(TestUtil.ORG_ID);

    assertNotNull(file);
    verify(postalCodeTimeZoneResponseService, times(1)).getCustomRegionsByOrgId(any());
  }

  @Test
  @DisplayName("happy Path : Download Custom regions by orgId, country, regionName and regionId")
  void downloadCustomRegionsDetailsTest() throws IOException {
    BaseResponse<PagePayload<CustomRegionInfo>> mockResponse =
        BaseResponse.builder().payload(testUtil.getCustomRegionInfoPagePayload()).build();
    when(postalCodeFeign.getCustomRegionInfo(
            any(), any(), any(), any(), any(), any(), any(), any()))
        .thenReturn(mockResponse);

    File file =
        csvDownloadUtilityService.downloadCustomRegionDetails(
            TestUtil.ORG_ID, TestUtil.COUNTRY, TestUtil.REGION_ID, TestUtil.REGION_NAME);
    assertNotNull(file);
    List<String> lines = FileUtils.readLines(file, StandardCharsets.UTF_8);
    boolean containsRegionId1 = lines.stream().anyMatch(line -> line.contains("CRID1"));
    boolean containsRegionId2 = lines.stream().anyMatch(line -> line.contains("CRID2"));
    assertTrue(containsRegionId1);
    assertTrue(containsRegionId2);
    verify(postalCodeFeign, times(1))
        .getCustomRegionInfo(any(), any(), any(), any(), any(), any(), any(), any());
  }

  @Test
  void downloadCustomRegionForOrgIdAndRegionIdTest() throws CustomRegionServiceException {
    when(postalCodeTimeZoneResponseService.getCustomRegionsByOrgIdAndRegionId(
            anyString(), anyString()))
        .thenReturn(testUtil.getCustomRegionResponse());

    String csvContents =
        csvDownloadUtilityService.downloadCustomRegionsForOrgIdAndRegionId(
            TestUtil.ORG_ID, TestUtil.REGION_ID);

    assertFalse(ObjectUtils.isEmpty(csvContents));
    verify(postalCodeTimeZoneResponseService, times(1))
        .getCustomRegionsByOrgIdAndRegionId(anyString(), anyString());
  }

  @Test
  @DisplayName("Download Cost Definition: Grid Happy Path")
  void downloadCostDefinitionForOrgIdGridHappyPathTest()
      throws CommonServiceException, IOException {
    when(costDefinitionService.getCostTypeValidationResponse(any(), any()))
        .thenReturn(testUtil.getCostTypeValidationResponse());
    when(costDefinitionService.getCostDefinitionResponse(any(), any(), any()))
        .thenReturn(testUtil.getGridResponse());

    InputStream inputStream =
        csvDownloadUtilityService.downloadCostDefinitionForOrgId(
            TestUtil.ORG_ID, testUtil.getGridRequest());

    assertNotNull(inputStream);
    String[] content = getContentFromInputStream(inputStream);
    assertEquals(11, content.length);
    assertTrue(content[0].contains("USD"));
    assertTrue(content[1].contains(TestUtil.ORG_ID));
    assertTrue(content[2].contains(TestUtil.SHIPPING_COST));
    assertTrue(
        content[3].contains(testUtil.getCostTypeValidationResponse().getSelectorCfDisplayName()));

    verify(costDefinitionService, times(1)).getCostDefinitionResponse(any(), any(), any());
  }

  @Test
  @DisplayName("Download Cost Definition: Grid without Selector Happy Path")
  void downloadCostDefinitionForOrgIdGridWithoutSelectorHappyPathTest()
      throws CommonServiceException, IOException {
    when(costDefinitionService.getCostTypeValidationResponse(any(), any()))
        .thenReturn(testUtil.getCostTypeValidationResponseWithoutSelector());
    when(costDefinitionService.getCostDefinitionResponse(any(), any(), any()))
        .thenReturn(testUtil.getGridResponse());
    InputStream inputStream =
        csvDownloadUtilityService.downloadCostDefinitionForOrgId(
            TestUtil.ORG_ID, testUtil.getGridRequestWithoutSelector());
    assertNotNull(inputStream);
    String[] con = getContentFromInputStream(inputStream);
    assertEquals(10, con.length);
    assertTrue(con[0].contains("USD"));
    assertTrue(con[1].contains(TestUtil.ORG_ID));
    assertTrue(con[2].contains(TestUtil.NODE_PROCESSING_COST));

    verify(costDefinitionService, times(1)).getCostDefinitionResponse(any(), any(), any());
  }

  @Test
  @DisplayName("Download Cost Definition: Table Happy Path")
  void downloadCostDefinitionForOrgIdTableHappyPathTest()
      throws CommonServiceException, IOException {
    when(costDefinitionService.getCostTypeValidationResponse(any(), any()))
        .thenReturn(testUtil.getCostTypeValidationResponse());
    when(costDefinitionService.getCostDefinitionResponse(any(), any(), any()))
        .thenReturn(testUtil.getTableResponse());

    InputStream inputStream =
        csvDownloadUtilityService.downloadCostDefinitionForOrgId(
            TestUtil.ORG_ID, testUtil.getTableRequest());
    assertNotNull(inputStream);
    String[] content = getContentFromInputStream(inputStream);
    assertEquals(10, content.length);
    assertTrue(content[0].contains("USD"));
    assertTrue(content[1].contains(TestUtil.ORG_ID));
    assertTrue(content[2].contains(TestUtil.SHIPPING_COST));
    assertTrue(
        content[3].contains(testUtil.getCostTypeValidationResponse().getSelectorCfDisplayName()));
    assertEquals("\"Bill Weight\",\"\"", content[5]);

    verify(costDefinitionService, times(1)).getCostDefinitionResponse(any(), any(), any());
  }

  @Test
  @DisplayName("Download Cost Definition: Static table Happy Path")
  void downloadCostDefinitionForOrgIdStaticTableHappyPathTest()
      throws CommonServiceException, IOException {
    when(costDefinitionService.getCostTypeValidationResponse(any(), any()))
        .thenReturn(testUtil.getCostTypeValidationResponse());
    when(costDefinitionService.getCostDefinitionResponse(any(), any(), any()))
        .thenReturn(testUtil.getStaticTableResponse());
    InputStream inputStream =
        csvDownloadUtilityService.downloadCostDefinitionForOrgId(
            TestUtil.ORG_ID, testUtil.getStaticTableRequest());

    assertNotNull(inputStream);
    String[] content = getContentFromInputStream(inputStream);
    assertEquals(5, content.length);
    assertTrue(content[0].contains("USD"));
    assertTrue(content[1].contains(TestUtil.ORG_ID));
    assertTrue(content[2].contains(TestUtil.SHIPPING_COST));
    assertTrue(
        content[3].contains(testUtil.getCostTypeValidationResponse().getSelectorCfDisplayName()));

    verify(costDefinitionService, times(1)).getCostDefinitionResponse(any(), any(), any());
  }

  @Test
  @DisplayName("Download Holiday cutoff: Happy Path")
  void downloadHolidayCutoffHappyPathTest() throws IOException {
    var baseResponse = ResponseEntity.ok(testUtil.getBaseResponseForHCO());
    HolidayCutoffUIRequest holidayCutoffUIRequest =
        HolidayCutoffUIRequest.builder().sourcingAttributesDefinitionId(1L).build();
    when(holidayCutoffUIFeign.getHolidayCutoffDetails(
            anyString(),
            anyBoolean(),
            any(),
            any(),
            any(),
            any(),
            any(HolidayCutoffUIRequest.class)))
        .thenReturn(baseResponse);
    InputStream inputStream =
        csvDownloadUtilityService.downloadHolidayCutoffRulesForOrgId(
            TestUtil.ORG_ID, holidayCutoffUIRequest);
    assertNotNull(inputStream);
    String[] content = getContentFromInputStream(inputStream);
    assertEquals(2, content.length);
    assertTrue(content[0].contains("serviceOption"));
    assertTrue(content[1].contains("SDND"));

    verify(holidayCutoffUIFeign, times(1))
        .getHolidayCutoffDetails(
            anyString(),
            anyBoolean(),
            any(),
            any(),
            any(),
            any(),
            any(HolidayCutoffUIRequest.class));
  }

  @Test
  @DisplayName("Download Holiday cutoff: Happy Path with value as empty string")
  void downloadHolidayCutoffHappyPathWithNullValueTest() throws IOException {
    var baseResponse = ResponseEntity.ok(testUtil.getBaseResponseForHCOWithNullAttributeValue());
    HolidayCutoffUIRequest holidayCutoffUIRequest =
        HolidayCutoffUIRequest.builder().sourcingAttributesDefinitionId(1L).build();
    when(holidayCutoffUIFeign.getHolidayCutoffDetails(
            anyString(),
            anyBoolean(),
            any(),
            any(),
            any(),
            any(),
            any(HolidayCutoffUIRequest.class)))
        .thenReturn(baseResponse);
    InputStream inputStream =
        csvDownloadUtilityService.downloadHolidayCutoffRulesForOrgId(
            TestUtil.ORG_ID, holidayCutoffUIRequest);
    assertNotNull(inputStream);
    String[] content = getContentFromInputStream(inputStream);
    assertEquals(2, content.length);
    assertTrue(content[0].contains("serviceOption"));
    assertTrue(content[1].contains(""));

    verify(holidayCutoffUIFeign, times(1))
        .getHolidayCutoffDetails(
            anyString(),
            anyBoolean(),
            any(),
            any(),
            any(),
            any(),
            any(HolidayCutoffUIRequest.class));
  }

  @Test
  @DisplayName("Download Holiday cutoff: With config but without data")
  void downloadHolidayCutoffWithoutDataTest() throws IOException {
    HolidayCutoffUIRequest holidayCutoffUIRequest =
        HolidayCutoffUIRequest.builder().sourcingAttributesDefinitionId(1L).build();
    when(holidayCutoffUIFeign.getHolidayCutoffDetails(
            anyString(),
            anyBoolean(),
            any(),
            any(),
            any(),
            any(),
            any(HolidayCutoffUIRequest.class)))
        .thenReturn(ResponseEntity.ok(testUtil.getBaseResponseForHCOWithoutData()));
    InputStream inputStream =
        csvDownloadUtilityService.downloadHolidayCutoffRulesForOrgId(
            TestUtil.ORG_ID, holidayCutoffUIRequest);
    assertNotNull(inputStream);
    String[] content = getContentFromInputStream(inputStream);
    assertEquals(1, content.length);
    assertTrue(content[0].contains("serviceOption"));

    verify(holidayCutoffUIFeign, times(1))
        .getHolidayCutoffDetails(
            anyString(),
            anyBoolean(),
            any(),
            any(),
            any(),
            any(),
            any(HolidayCutoffUIRequest.class));
  }

  @Test
  @DisplayName("Download Holiday cutoff: Without config")
  void downloadHolidayCutoffWithoutConfigTest() throws IOException {
    HolidayCutoffUIRequest holidayCutoffUIRequest =
        HolidayCutoffUIRequest.builder().sourcingAttributesDefinitionId(1L).build();
    when(holidayCutoffUIFeign.getHolidayCutoffDetails(
            anyString(),
            anyBoolean(),
            any(),
            any(),
            any(),
            any(),
            any(HolidayCutoffUIRequest.class)))
        .thenReturn(ResponseEntity.ok(testUtil.getBaseResponseForHCOWithoutConfig()));
    InputStream inputStream =
        csvDownloadUtilityService.downloadHolidayCutoffRulesForOrgId(
            TestUtil.ORG_ID, holidayCutoffUIRequest);
    assertNotNull(inputStream);
    String[] content = getContentFromInputStream(inputStream);
    assertEquals(0, content.length);

    verify(holidayCutoffUIFeign, times(1))
        .getHolidayCutoffDetails(
            anyString(),
            anyBoolean(),
            any(),
            any(),
            any(),
            any(),
            any(HolidayCutoffUIRequest.class));
  }

  @Test
  @DisplayName("Download Holiday cutoff: With pageResponse as null")
  void downloadHolidayCutoffWithNoPageResponse() throws IOException {
    HolidayCutoffUIRequest holidayCutoffUIRequest =
        HolidayCutoffUIRequest.builder().sourcingAttributesDefinitionId(1L).build();
    when(holidayCutoffUIFeign.getHolidayCutoffDetails(
            anyString(),
            anyBoolean(),
            any(),
            any(),
            any(),
            any(),
            any(HolidayCutoffUIRequest.class)))
        .thenReturn(ResponseEntity.ok(null));
    InputStream inputStream =
        csvDownloadUtilityService.downloadHolidayCutoffRulesForOrgId(
            TestUtil.ORG_ID, holidayCutoffUIRequest);
    assertNotNull(inputStream);
    String[] content = getContentFromInputStream(inputStream);
    assertEquals(0, content.length);

    verify(holidayCutoffUIFeign, times(1))
        .getHolidayCutoffDetails(
            anyString(),
            anyBoolean(),
            any(),
            any(),
            any(),
            any(),
            any(HolidayCutoffUIRequest.class));
  }

  private String[] getContentFromInputStream(InputStream inputStream) throws IOException {
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
      return reader.lines().toArray(String[]::new);
    }
  }

  @Test
  @DisplayName("Download transfer schedules test")
  void downloadTransferSchedulesDataTest() throws IOException {
    FetchTransferScheduleRequest request =
        FetchTransferScheduleRequest.builder().endDate(new LocalDate()).build();
    var transferScheduleFeignResponse = testUtil.getTransferScheduleResponse();

    when(transferScheduleFeign.fetchTransferSchedule(
            any(), any(), any(), any(), any(), any(), any()))
        .thenReturn(transferScheduleFeignResponse);

    File file = csvDownloadUtilityService.downloadTransferSchedulesData(TestUtil.ORG_ID, request);

    Assertions.assertNotNull(file);
    verify(transferScheduleFeign, times(1))
        .fetchTransferSchedule(any(), any(), any(), any(), any(), any(), any());
  }

  @Test
  @DisplayName("Happy path : When origin is PE")
  void downloadLogsAsCsvForCalendarV2()
      throws CommonServiceException, JobSubmissionException, IOException {
    JobDto jobDto = testUtil.getJobDto();
    jobDto.setJobType(JobTypeEnum.UPLOAD_CALENDER);
    when(jobsConsumerService.getJob(TestUtil.JOB_ID, TestUtil.ORG_ID)).thenReturn(jobDto);
    when(processingRequestFactory.getModuleByJobType(JobTypeEnum.UPLOAD_CALENDER))
        .thenReturn(calendarProcessingRequest);
    when(calendarProcessingRequest.downloadErrorLogs(any(), any()))
        .thenReturn(testUtil.getPreSignedUrlResponse());

    var csvContent =
        csvDownloadUtilityService.downloadLogsAsCsvV2(
            TestUtil.JOB_ID, TestUtil.ORG_ID, Optional.of(ApiStatusEnum.FAILURE.name()), "PE");
    assertFalse(ObjectUtils.isEmpty(csvContent));
    verify(jobsConsumerService, times(1)).getJob(anyString(), anyString());
  }

  @Test
  @DisplayName("Exception : When origin is PE")
  void downloadLogsAsCsvForCalendarV2Exception() throws CommonServiceException {
    JobDto jobDto = testUtil.getJobDto();
    jobDto.setJobType(JobTypeEnum.UPLOAD_CALENDER);
    when(jobsConsumerService.getJob(TestUtil.JOB_ID, TestUtil.ORG_ID))
        .thenThrow(new RuntimeException("Error while fetching job"));

    Exception exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                csvDownloadUtilityService.downloadLogsAsCsvV2(
                    TestUtil.JOB_ID,
                    TestUtil.ORG_ID,
                    Optional.of(ApiStatusEnum.FAILURE.name()),
                    "PE"));
    assertNotNull(exception);
    verify(jobsConsumerService, times(1)).getJob(anyString(), anyString());
  }

  @Test
  @DisplayName("Happy path : When origin is NEIP")
  void downloadLogsAsCsvForItemBufferV2NEIP()
      throws CommonServiceException, IOException, JobRecordException, JobException {
    MockitoAnnotations.openMocks(this);
    var baseResponse = ResponseEntity.ok(List.of(testUtil.getJobRecordDtoForFpm()));
    when(fpmServiceClient.getJobRecordByJobIdAndStatus(
            TestUtil.ORG_ID, TestUtil.JOB_ID, ApiStatusEnum.FAILURE.name()))
        .thenReturn(baseResponse);
    when(neipProcessingRequest.generateURLResponse(any(), any(), any()))
        .thenReturn(testUtil.getPreSignedUrlResponse());
    when(fpmServiceClient.getJob(any(), any())).thenReturn(testUtil.getJobDtoForNeip());
    when(env.getProperty("neip.headers.itembuffer", "No headers found"))
        .thenReturn(
            "RecordNo,action,orgId,itemId,uom,bufferHours,bufferStartDate,bufferEndDate,Error Message");
    var response =
        csvDownloadUtilityService.downloadLogsAsCsvV2(
            TestUtil.JOB_ID, TestUtil.ORG_ID, Optional.of(ApiStatusEnum.FAILURE.name()), "NEIP");
    assertFalse(ObjectUtils.isEmpty(response));
    assertEquals("URL", response.getSignedURL());
    assertEquals("path", response.getFilePath());
    verify(fpmServiceClient, times(1)).getJobRecordByJobIdAndStatus(any(), any(), any());
    verify(neipProcessingRequest, times(1)).generateURLResponse(any(), any(), any());
  }

  @Test
  @DisplayName("Exception : fpmServiceClient returns null")
  void downloadLogsAsCsvForItemBufferV2NEIPException() throws JobRecordException, JobException {
    when(fpmServiceClient.getJob(any(), any())).thenReturn(testUtil.getJobDtoForNeip());
    when(fpmServiceClient.getJobRecordByJobIdAndStatus(
            TestUtil.ORG_ID, TestUtil.JOB_ID, ApiStatusEnum.FAILURE.name()))
        .thenReturn(null);
    Exception exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                csvDownloadUtilityService.downloadLogsAsCsvV2(
                    TestUtil.JOB_ID,
                    TestUtil.ORG_ID,
                    Optional.of(ApiStatusEnum.FAILURE.name()),
                    "NEIP"));
    assertNotNull(exception);
    assertEquals("Error while downloading error logs as csv", exception.getMessage());
    verify(fpmServiceClient, times(1)).getJob(any(), any());
    verify(fpmServiceClient, times(1)).getJobRecordByJobIdAndStatus(any(), any(), any());
  }

  @Test
  @DisplayName("Happy path : Validate the error log file")
  void downloadLogsAsCsvValidateLogFileContent()
      throws CommonServiceException, IOException, JobRecordException, JobException {
    MockitoAnnotations.openMocks(this);
    var baseResponse = ResponseEntity.ok(List.of(testUtil.getJobRecordDtoForFpm()));
    when(fpmServiceClient.getJobRecordByJobIdAndStatus(
            TestUtil.ORG_ID, TestUtil.JOB_ID, ApiStatusEnum.FAILURE.name()))
        .thenReturn(baseResponse);
    when(neipProcessingRequest.generateURLResponse(any(), any(), any()))
        .thenReturn(testUtil.getPreSignedUrlResponse());
    when(fpmServiceClient.getJob(any(), any())).thenReturn(testUtil.getJobDtoForNeip());
    when(env.getProperty("neip.headers.itembuffer", "No headers found"))
        .thenReturn(
            "RecordNo,action,orgId,itemId,uom,bufferHours,bufferStartDate,bufferEndDate,ErrorMessage");
    var response =
        csvDownloadUtilityService.downloadLogsAsCsvV2(
            TestUtil.JOB_ID, TestUtil.ORG_ID, Optional.of(ApiStatusEnum.FAILURE.name()), "NEIP");

    assertFalse(ObjectUtils.isEmpty(response));
    assertEquals("URL", response.getSignedURL());
    assertEquals("path", response.getFilePath());
    verify(fpmServiceClient, times(1)).getJobRecordByJobIdAndStatus(any(), any(), any());
    verify(neipProcessingRequest, times(1)).generateURLResponse(csvCaptor.capture(), any(), any());
    File csvFile = csvCaptor.getValue();
    String fileContent = readFileAsString(csvFile);
    assertEquals(
        "RecordNo,action,orgId,itemId,uom,bufferHours,bufferStartDate,bufferEndDate,ErrorMessage\n"
            + "1,raw request,Item not found for given details",
        fileContent.toString().trim());
  }

  @Test
  @DisplayName("Happy path : Validate the error log file with error message as null")
  void downloadLogsAsCsvValidateLogFileContentWithErrorMessageAsNull()
      throws CommonServiceException, IOException, JobRecordException, JobException {
    MockitoAnnotations.openMocks(this);
    var baseResponse =
        ResponseEntity.ok(List.of(testUtil.getJobRecordDtoForFpmWithErrorMessageAsNull()));
    when(fpmServiceClient.getJobRecordByJobIdAndStatus(
            TestUtil.ORG_ID, TestUtil.JOB_ID, ApiStatusEnum.FAILURE.name()))
        .thenReturn(baseResponse);
    when(neipProcessingRequest.generateURLResponse(any(), any(), any()))
        .thenReturn(testUtil.getPreSignedUrlResponse());
    when(fpmServiceClient.getJob(any(), any())).thenReturn(testUtil.getJobDtoForNeip());
    when(env.getProperty("neip.headers.itembuffer", "No headers found"))
        .thenReturn(
            "RecordNo,action,orgId,itemId,uom,bufferHours,bufferStartDate,bufferEndDate,ErrorMessage");

    var response =
        csvDownloadUtilityService.downloadLogsAsCsvV2(
            TestUtil.JOB_ID, TestUtil.ORG_ID, Optional.of(ApiStatusEnum.FAILURE.name()), "NEIP");

    assertFalse(ObjectUtils.isEmpty(response));
    assertEquals("URL", response.getSignedURL());
    assertEquals("path", response.getFilePath());
    verify(fpmServiceClient, times(1)).getJobRecordByJobIdAndStatus(any(), any(), any());
    verify(neipProcessingRequest, times(1)).generateURLResponse(csvCaptor.capture(), any(), any());
    File csvFile = csvCaptor.getValue();
    String fileContent = readFileAsString(csvFile);
    assertEquals(
        "RecordNo,action,orgId,itemId,uom,bufferHours,bufferStartDate,bufferEndDate,ErrorMessage\n"
            + "1,raw request,error message",
        fileContent.toString().trim());
  }

  private String readFileAsString(File file) throws IOException {
    StringBuilder content = new StringBuilder();
    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
      String line;
      while ((line = reader.readLine()) != null) {
        content.append(line).append("\n");
      }
    }
    return content.toString();
  }
}
