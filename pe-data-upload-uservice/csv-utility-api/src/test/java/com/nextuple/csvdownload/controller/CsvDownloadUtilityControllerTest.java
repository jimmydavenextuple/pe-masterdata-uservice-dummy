/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.csvdownload.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.common.response.PreSignedUrlResponse;
import com.nextuple.csvdownload.common.TestUtil;
import com.nextuple.csvdownload.common.pojo.DownloadNodeCarrierServiceAndServiceOptionPojo;
import com.nextuple.csvdownload.common.pojo.TemplateTypes;
import com.nextuple.csvdownload.exception.CarrierServiceException;
import com.nextuple.csvdownload.exception.CsvDownloadUtilityServiceException;
import com.nextuple.csvdownload.exception.CustomRegionServiceException;
import com.nextuple.csvdownload.exception.InvalidTemplateTypeException;
import com.nextuple.csvdownload.exception.PostalCodeTimezoneServiceException;
import com.nextuple.csvdownload.exception.TransitServiceException;
import com.nextuple.csvdownload.service.CsvDownloadUtilityService;
import com.nextuple.csvdownload.service.DownloadTemplateService;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.HolidayCutoffUIRequest;
import com.nextuple.transit.domain.inbound.FetchTransferScheduleRequest;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Description;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class CsvDownloadUtilityControllerTest {

  @Mock private CsvDownloadUtilityService csvDownloadUtilityService;
  @Mock private DownloadTemplateService downloadTemplateService;

  @InjectMocks private CsvDownloadUtilityController csvDownloadUtilityController;

  @InjectMocks private com.nextuple.csvdownload.util.TestUtil testUtil;

  @Test
  void downloadCSVTemplateTransitTimes() throws IOException, InvalidTemplateTypeException {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    String transitTimesTemplate = TemplateTypes.getTemplateData("transitTime");

    doNothing().when(response).setStatus(HttpStatus.OK.value());
    doNothing().when(response).setContentLength(transitTimesTemplate.length());
    ServletOutputStream servletOutputStream = mock(ServletOutputStream.class);

    when(response.getOutputStream()).thenReturn(servletOutputStream);

    csvDownloadUtilityController.downloadCSVTemplate("transitTime", request, response);
    verify(response, times(1)).getOutputStream();
  }

  @Test
  void downloadCSVTemplateProcessingLeadTimes() throws IOException, InvalidTemplateTypeException {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    String leadProcessingTimesTemplate = TemplateTypes.getTemplateData("processingLeadTime");

    doNothing().when(response).setStatus(HttpStatus.OK.value());
    doNothing().when(response).setContentLength(leadProcessingTimesTemplate.length());
    ServletOutputStream servletOutputStream = mock(ServletOutputStream.class);

    when(response.getOutputStream()).thenReturn(servletOutputStream);

    csvDownloadUtilityController.downloadCSVTemplate("processingLeadTime", request, response);
    verify(response, times(1)).getOutputStream();
  }

  @Test
  void downloadCSVTemplateInvalidTemplateTypeException() {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    Exception exception =
        Assertions.assertThrows(
            InvalidTemplateTypeException.class,
            () ->
                csvDownloadUtilityController.downloadCSVTemplate(
                    "transitTime1", request, response));

    Assertions.assertNotNull(exception);
  }

  @Test
  void downloadCSVTemplateException() throws IOException, InvalidTemplateTypeException {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    String transitTimesTemplate =
        "orgId,BAY,,,,,,,,,\n"
            + "Carrier Service:,ALL-Standard,,,,,,,,,\n"
            + "Destination geoZone / Source geoZone ->,SGZ1,SGZ2,SGZ3,SGZ4,SGZ5,SGZ6,SGZ7,SGZ8,SGZ9,SGZ10\n"
            + "DGZ1,10,9.96,9.96,9.96,9.96,7,7,8.09,7,7\n"
            + "DGZ2,10,9,9,9,9,7.81,7.81,7.89,7.89,7.89\n"
            + "DGZ3,10,9,9,9,9,9.5,9.5,7,7.89,7.89\n"
            + "DGZ4,10,9.96,9.96,9.96,9.96,8.09,8.09,7.89,8.09,8.09\n"
            + "DGZ5,10,9.96,9.96,9.96,9.96,7.81,7.81,7.89,7.89,7.89\n"
            + "DGZ6,10,9.96,9.96,9.96,9.96,7,7,8.09,8.09,8.09\n"
            + "DGZ7,10,10,10,10,10,7.81,7.81,7.89,7.89,7.89\n"
            + "DGZ8,10,9.96,9.96,9.96,9.96,7.81,7.81,8.09,6,6\n"
            + "DGZ9,10,9.5,9.5,9.5,9.5,8.09,8.09,7.89,8.09,8.09\n"
            + "DGZ10,8,8,8,8,8,8.09,8.09,6,7,7";

    doNothing().when(response).setStatus(HttpStatus.OK.value());
    doNothing().when(response).setContentLength(transitTimesTemplate.length());

    when(response.getOutputStream()).thenThrow(new IOException("Unexpected error"));

    csvDownloadUtilityController.downloadCSVTemplate("transitTime", request, response);
    verify(response, times(1)).getOutputStream();
  }

  @Test
  void downloadTransitTimesDataCSV()
      throws IOException,
          TransitServiceException,
          PostalCodeTimezoneServiceException,
          CsvDownloadUtilityServiceException {

    String csvContents =
        "orgId,BAY\n"
            + "Carrier Service:,ALL-SDND\n"
            + "Destination geoZone / Source geoZone ->,A0A,M1R\n"
            + "K1A,0.5,0.5\n"
            + "K2A,null,1.0";

    when(csvDownloadUtilityService.downloadTransitTimesForSourceAndDestinationRegion(
            anyString(), anyString(), anyString(), anyString()))
        .thenReturn(csvContents);

    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    doNothing().when(response).setStatus(HttpStatus.OK.value());
    doNothing().when(response).setContentLength(csvContents.length());
    ServletOutputStream servletOutputStream = mock(ServletOutputStream.class);

    when(response.getOutputStream()).thenReturn(servletOutputStream);

    csvDownloadUtilityController.downloadTransitTimesDataCSV(
        "BAY", "ALL-SDND", "ON", "DEL", request, response);
    verify(response, times(1)).getOutputStream();
  }

  @Test
  void downloadLogsByFiltersForProcessingLeadTime() throws IOException, CommonServiceException {

    String ProcessingLeadTimeErrorLogTemplate =
        "nodeId,orgId,serviceOptions,processingTime (in hrs),errorMessage\n"
            + "1554,BAY,SDND,2,Invalid nodeId\n"
            + "1560,BAY,SDND,2,Invalid nodeId\n"
            + "1101,BAY,SDND,2,Invalid nodeId\n"
            + "1518,BAY,NEXTDAY,6,Invalid nodeId";

    when(csvDownloadUtilityService.downloadLogsAsCsv(anyString(), anyString(), any()))
        .thenReturn(ProcessingLeadTimeErrorLogTemplate);

    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    doNothing().when(response).setStatus(HttpStatus.OK.value());
    doNothing().when(response).setContentLength(ProcessingLeadTimeErrorLogTemplate.length());

    ServletOutputStream servletOutputStream = mock(ServletOutputStream.class);

    when(response.getOutputStream()).thenReturn(servletOutputStream);

    csvDownloadUtilityController.downloadLogsByFilters(
        "BAY", "C-Id", Optional.empty(), request, response);
    verify(response, times(1)).getOutputStream();
  }

  @Test
  void downloadLogsByFiltersV1() throws IOException, CommonServiceException {

    PreSignedUrlResponse ErrorLogTemplate = new PreSignedUrlResponse("", "", "");

    when(csvDownloadUtilityService.downloadLogsAsCsvV1(anyString(), anyString(), any()))
        .thenReturn(ErrorLogTemplate);

    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    csvDownloadUtilityController.downloadLogsByFiltersV1(
        "BAY", "C-Id", Optional.empty(), request, response);
    verify(csvDownloadUtilityService, times(1))
        .downloadLogsAsCsvV1(anyString(), anyString(), any());
  }

  @Test
  void downloadMarketRegionDataCSVTest() throws IOException, PostalCodeTimezoneServiceException {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    String marketRegionTemplate = TemplateTypes.getTemplateData("postalCodeTimezone");
    when(csvDownloadUtilityService.downloadMarketRegionForOrgIdAndCountry(anyString(), anyString()))
        .thenReturn(marketRegionTemplate);

    doNothing().when(response).setStatus(HttpStatus.OK.value());
    doNothing().when(response).setContentLength(marketRegionTemplate.length());
    ServletOutputStream servletOutputStream = mock(ServletOutputStream.class);

    when(response.getOutputStream()).thenReturn(servletOutputStream);

    csvDownloadUtilityController.downloadMarketRegionDataCSV(
        TestUtil.ORG_ID, TestUtil.COUNTRY, request, response);
    verify(response, times(1)).getOutputStream();
  }

  @Test
  void downloadNodeCarrierServiceAndServiceOptionsDataCSV() throws IOException {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    ServletOutputStream servletOutputStream = Mockito.mock(ServletOutputStream.class);
    Path path =
        Paths.get(
            "src",
            "test",
            "resources",
            "nodeCarrierServiceOption",
            "downloadNodeCarrierServiceAndServiceOptionDetails.csv");
    DownloadNodeCarrierServiceAndServiceOptionPojo pojo =
        DownloadNodeCarrierServiceAndServiceOptionPojo.builder()
            .fileContents(Files.readAllBytes(path))
            .contentsLength(path.toFile().length())
            .build();

    when(csvDownloadUtilityService.downloadNodeCarrierServiceAndServiceOptionsDataCSV(anyString()))
        .thenReturn(pojo);

    when(response.getOutputStream()).thenReturn(servletOutputStream);

    doNothing().when(servletOutputStream).write(any());

    csvDownloadUtilityController.downloadNodeCarrierServiceAndServiceOptionsDataCSV(
        TestUtil.ORG_ID, request, response);
    verify(csvDownloadUtilityService, times(1))
        .downloadNodeCarrierServiceAndServiceOptionsDataCSV(anyString());
  }

  @Test
  void downloadNodeAndServiceOptionsDataCSV() throws IOException {
    HttpServletResponse response = mock(HttpServletResponse.class);
    ServletOutputStream servletOutputStream = Mockito.mock(ServletOutputStream.class);
    Path path =
        Paths.get(
            "src",
            "test",
            "resources",
            "nodeServiceOption",
            "downloadNodeAndServiceOptionDetails.csv");
    DownloadNodeCarrierServiceAndServiceOptionPojo pojo =
        DownloadNodeCarrierServiceAndServiceOptionPojo.builder()
            .fileContents(Files.readAllBytes(path))
            .contentsLength(path.toFile().length())
            .build();

    when(csvDownloadUtilityService.downloadNodeAndServiceOptionsDataCSV(anyString()))
        .thenReturn(pojo);

    when(response.getOutputStream()).thenReturn(servletOutputStream);

    doNothing().when(servletOutputStream).write(any());

    csvDownloadUtilityController.downloadNodeAndServiceOptionsDataCSV(TestUtil.ORG_ID, response);
    verify(csvDownloadUtilityService, times(1)).downloadNodeAndServiceOptionsDataCSV(anyString());
  }

  @Test
  void downloadCarrierServiceCSVTest() throws IOException, CarrierServiceException {

    HttpServletRequest request = mock(HttpServletRequest.class);

    String CARRIER_SERVICE =
        "   carrierServiceId,orgId,carrierName,carrierId,serviceName,status,carrierServiceWorkingCalendar\n"
            + " ALL-EXPRESS,BAY,ALL,01,service-1-name,INACTIVE,C002\n"
            + " ALL-EXPRESS,BAY,ALL,01,service-1-name,INACTIVE,C001\n";
    File file = File.createTempFile(CARRIER_SERVICE, "");
    file.deleteOnExit();
    when(csvDownloadUtilityService.downloadCarrierServiceDataCSV(anyString())).thenReturn(file);
    HttpServletResponse response = mock(HttpServletResponse.class);
    ServletOutputStream servletOutputStream = mock(ServletOutputStream.class);
    when(response.getOutputStream()).thenReturn(servletOutputStream);
    doNothing().when(response).setStatus(HttpStatus.OK.value());
    when(response.getOutputStream()).thenReturn(servletOutputStream);
    Assertions.assertDoesNotThrow(
        () ->
            csvDownloadUtilityController.downloadCarrierServiceCSV(
                TestUtil.ORG_ID, request, response));
  }

  @Test
  void downloadProcessingTimeBufferDataCSVTest() throws IOException {
    File file = File.createTempFile("some-prefix", "some-ext");
    file.deleteOnExit();
    when(csvDownloadUtilityService.downloadProcessingTimeBuffersByOrgId(any(), any()))
        .thenReturn(file);
    HttpServletResponse response = mock(HttpServletResponse.class);
    doNothing().when(response).setStatus(HttpStatus.OK.value());
    ServletOutputStream servletOutputStream = mock(ServletOutputStream.class);

    when(response.getOutputStream()).thenReturn(servletOutputStream);
    Assertions.assertDoesNotThrow(
        () ->
            csvDownloadUtilityController.downloadProcessingTimeBufferDataCSV(
                TestUtil.ORG_ID, TestUtil.NODE_ID, response));
  }

  @Test
  void downloadTemplateByFile() throws Exception {

    var is = new ByteArrayOutputStream();
    is.writeBytes(TestUtil.nodeCarrierCsvData.getBytes(StandardCharsets.UTF_8));

    when(downloadTemplateService.getTemplateData(any(), any())).thenReturn(is);

    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    doNothing().when(response).setStatus(HttpStatus.OK.value());
    ServletOutputStream servletOutputStream = mock(ServletOutputStream.class);

    when(response.getOutputStream()).thenReturn(servletOutputStream);

    csvDownloadUtilityController.downloadCSVTemplateFromFile(
        TestUtil.templateType, TestUtil.ORG_ID, request, response);

    verify(downloadTemplateService, times(1)).getTemplateData(anyString(), anyString());

    Assertions.assertDoesNotThrow(
        () ->
            csvDownloadUtilityController.downloadCSVTemplateFromFile(
                TestUtil.templateType, TestUtil.ORG_ID, request, response));
  }

  @Test
  void downloadTemplateByFileError() throws Exception {
    when(downloadTemplateService.getTemplateData(any(), any()))
        .thenThrow(
            new InvalidTemplateTypeException(
                TestUtil.invalidTemplateTypeErrMsg, TestUtil.templateTypeInvalid));

    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    Exception exception =
        Assertions.assertThrows(
            InvalidTemplateTypeException.class,
            () ->
                csvDownloadUtilityController.downloadCSVTemplateFromFile(
                    TestUtil.templateTypeInvalid, TestUtil.ORG_ID, request, response));

    Assertions.assertNotNull(exception);

    verify(downloadTemplateService, times(1)).getTemplateData(anyString(), anyString());
  }

  @Test
  void downloadNodesDataCSVTest() throws IOException, CommonServiceException {
    File file = File.createTempFile("some-prefix", "some-ext");
    file.deleteOnExit();
    when(csvDownloadUtilityService.downloadNodesByOrgId(any(), any(), any())).thenReturn(file);
    HttpServletResponse response = mock(HttpServletResponse.class);
    doNothing().when(response).setStatus(HttpStatus.OK.value());
    ServletOutputStream servletOutputStream = mock(ServletOutputStream.class);

    when(response.getOutputStream()).thenReturn(servletOutputStream);
    Assertions.assertDoesNotThrow(
        () ->
            csvDownloadUtilityController.downloadNodesDataCSV(
                TestUtil.ORG_ID, null, null, response));
  }

  @Test
  void downloadTransitBufferTest() {
    PreSignedUrlResponse preSignedUrlResponse = testUtil.getPreSignedUrlResponse();
    when(csvDownloadUtilityService.downloadTransitBufferDetails(anyLong(), anyString()))
        .thenReturn(preSignedUrlResponse);

    ResponseEntity<BaseResponse<PreSignedUrlResponse>> response =
        csvDownloadUtilityController.downloadTransitBuffer(1L, "user1");

    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertEquals(preSignedUrlResponse, response.getBody().getPayload());

    verify(csvDownloadUtilityService, times(1))
        .downloadTransitBufferDetails(anyLong(), anyString());
  }

  @Test
  void downloadCustomRegionsByOrgIdDataCSVTest() throws IOException {
    File file = File.createTempFile("some-prefix", "some-ext");
    file.deleteOnExit();
    when(csvDownloadUtilityService.downloadCustomRegionsForOrgId(any())).thenReturn(file);
    HttpServletResponse response = mock(HttpServletResponse.class);
    doNothing().when(response).setStatus(HttpStatus.OK.value());
    ServletOutputStream servletOutputStream = mock(ServletOutputStream.class);

    when(response.getOutputStream()).thenReturn(servletOutputStream);
    Assertions.assertDoesNotThrow(
        () ->
            csvDownloadUtilityController.downloadCustomRegionsByOrgIdDataCSV(
                TestUtil.ORG_ID, response));
  }

  @Test
  @DisplayName("Happy path : Download csv for custom region details provided orgId, country")
  void downloadCustomRegionsDetailsDataCSVTest() throws IOException {
    File file = File.createTempFile("some-prefix", "some-ext");
    file.deleteOnExit();
    when(csvDownloadUtilityService.downloadCustomRegionDetails(any(), any(), any(), any()))
        .thenReturn(file);
    HttpServletResponse response = mock(HttpServletResponse.class);
    doNothing().when(response).setStatus(HttpStatus.OK.value());
    ServletOutputStream servletOutputStream = mock(ServletOutputStream.class);

    when(response.getOutputStream()).thenReturn(servletOutputStream);
    Assertions.assertDoesNotThrow(
        () ->
            csvDownloadUtilityController.downloadCustomRegionsByOrgIdAndCountryDataCSV(
                TestUtil.ORG_ID,
                TestUtil.COUNTRY,
                TestUtil.REGION_ID,
                TestUtil.REGION_NAME,
                response));
  }

  @Test
  void downloadCustomRegionsByOrgIdAndRegionIdDataCSVTest()
      throws IOException, CustomRegionServiceException {
    HttpServletResponse response = mock(HttpServletResponse.class);
    String customRegionTemplate =
        "orgId,regionId,customRegionName,customRegionDescription,geozones\n"
            + "NEXTUPLE,ID1011,Bay Area,Bay Area Region,901:902";
    when(csvDownloadUtilityService.downloadCustomRegionsForOrgIdAndRegionId(
            anyString(), anyString()))
        .thenReturn(customRegionTemplate);

    doNothing().when(response).setStatus(HttpStatus.OK.value());
    doNothing().when(response).setContentLength(customRegionTemplate.length());
    ServletOutputStream servletOutputStream = mock(ServletOutputStream.class);

    when(response.getOutputStream()).thenReturn(servletOutputStream);

    csvDownloadUtilityController.downloadCustomRegionsByOrgIdAndRegionIdDataCSV(
        TestUtil.ORG_ID, TestUtil.REGION_ID, response);
    verify(response, times(1)).getOutputStream();
  }

  @Test
  @DisplayName(("Download cost definition test"))
  void downloadCostDefinitionTest() throws IOException, CommonServiceException {
    byte[] data = "orgId: NEXTUPLE_GR".getBytes();
    ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
    HttpServletResponse response = mock(HttpServletResponse.class);
    when(csvDownloadUtilityService.downloadCostDefinitionForOrgId(any(), any()))
        .thenReturn(inputStream);
    doNothing().when(response).setStatus(HttpStatus.OK.value());
    ServletOutputStream servletOutputStream = mock(ServletOutputStream.class);

    when(response.getOutputStream()).thenReturn(servletOutputStream);

    csvDownloadUtilityController.downloadCostDefinition(
        TestUtil.ORG_ID, testUtil.getGridRequest(), response);

    Assertions.assertEquals(servletOutputStream, response.getOutputStream());
    verify(csvDownloadUtilityService, times(1)).downloadCostDefinitionForOrgId(any(), any());
  }

  @Test
  @Description("Download Nodes Carrier Service Pickup Calendar Data")
  void downloadNodesCarrierServicePickupCalendarDataCSV()
      throws IOException, CommonServiceException, CarrierServiceException {
    File file = File.createTempFile("some-prefix", "some-ext");
    file.deleteOnExit();
    when(csvDownloadUtilityService.downloadNodesCarrierPickupCalendarByOrgId(any()))
        .thenReturn(file);
    HttpServletResponse response = mock(HttpServletResponse.class);
    doNothing().when(response).setStatus(HttpStatus.OK.value());
    ServletOutputStream servletOutputStream = mock(ServletOutputStream.class);
    when(response.getOutputStream()).thenReturn(servletOutputStream);
    Assertions.assertDoesNotThrow(
        () ->
            csvDownloadUtilityController.downloadNodesCarrierServicePickupCalendarDataCSV(
                TestUtil.ORG_ID, response));
  }

  @Test
  @DisplayName("Download holiday cutoff rules test")
  void downloadHCORulesTest() throws IOException {
    byte[] data = "orgId: NEXTUPLE_GR".getBytes();
    ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
    HttpServletResponse response = mock(HttpServletResponse.class);
    HolidayCutoffUIRequest holidayCutoffUIRequest =
        HolidayCutoffUIRequest.builder().sourcingAttributesDefinitionId(1L).build();
    when(csvDownloadUtilityService.downloadHolidayCutoffRulesForOrgId(
            anyString(), any(HolidayCutoffUIRequest.class)))
        .thenReturn(inputStream);
    doNothing().when(response).setStatus(HttpStatus.OK.value());
    ServletOutputStream servletOutputStream = mock(ServletOutputStream.class);

    when(response.getOutputStream()).thenReturn(servletOutputStream);

    csvDownloadUtilityController.downloadHCORules(
        TestUtil.ORG_ID, holidayCutoffUIRequest, response);

    Assertions.assertEquals(servletOutputStream, response.getOutputStream());
    verify(csvDownloadUtilityService, times(1))
        .downloadHolidayCutoffRulesForOrgId(anyString(), any(HolidayCutoffUIRequest.class));
  }

  @Test
  @DisplayName("Happy path : Download error logs from origin NEIP or PE")
  void downloadLogsByFiltersV2() throws CommonServiceException {
    PreSignedUrlResponse ErrorLogTemplate = new PreSignedUrlResponse("", "", "");
    when(csvDownloadUtilityService.downloadLogsAsCsvV2(
            anyString(), anyString(), any(), anyString()))
        .thenReturn(ErrorLogTemplate);
    ResponseEntity<BaseResponse<PreSignedUrlResponse>> response =
        csvDownloadUtilityController.downloadLogsByFiltersV2(
            "BAY", "C-Id", Optional.of("FAILURE"), "NEIP");
    Assertions.assertNotNull(response.getBody().getPayload());
    Assertions.assertEquals(
        "Log history URL fetched successfully", response.getBody().getMessage());
    verify(csvDownloadUtilityService, times(1))
        .downloadLogsAsCsvV2(anyString(), anyString(), any(), anyString());
  }

  @Test
  @DisplayName("Download transfer schedules test")
  void downloadTransferSchedulesTest() throws IOException {
    var request = new FetchTransferScheduleRequest();
    File file = File.createTempFile("some-prefix", "some-ext");
    file.deleteOnExit();
    when(csvDownloadUtilityService.downloadTransferSchedulesData(
            anyString(), any(FetchTransferScheduleRequest.class)))
        .thenReturn(file);
    HttpServletResponse response = mock(HttpServletResponse.class);
    doNothing().when(response).setStatus(HttpStatus.OK.value());
    ServletOutputStream servletOutputStream = mock(ServletOutputStream.class);

    when(response.getOutputStream()).thenReturn(servletOutputStream);
    Assertions.assertDoesNotThrow(
        () ->
            csvDownloadUtilityController.downloadTransferSchedules(
                TestUtil.ORG_ID, request, response));
    verify(csvDownloadUtilityService, times(1))
        .downloadTransferSchedulesData(anyString(), any(FetchTransferScheduleRequest.class));
  }
}
