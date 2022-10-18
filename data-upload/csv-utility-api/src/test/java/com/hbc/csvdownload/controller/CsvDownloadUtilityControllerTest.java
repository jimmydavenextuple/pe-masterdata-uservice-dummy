package com.hbc.csvdownload.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hbc.common.exception.CommonServiceException;
import com.hbc.csvdownload.common.TestUtil;
import com.hbc.csvdownload.common.pojo.DownloadNodeCarrierServiceAndServiceOptionPojo;
import com.hbc.csvdownload.common.pojo.TemplateTypes;
import com.hbc.csvdownload.exception.CarrierServiceException;
import com.hbc.csvdownload.exception.CsvDownloadUtilityServiceException;
import com.hbc.csvdownload.exception.InvalidTemplateTypeException;
import com.hbc.csvdownload.exception.PostalCodeTimezoneServiceException;
import com.hbc.csvdownload.exception.TransitServiceException;
import com.hbc.csvdownload.service.CsvDownloadUtilityService;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class CsvDownloadUtilityControllerTest {

  @Mock private CsvDownloadUtilityService csvDownloadUtilityService;

  @InjectMocks private CsvDownloadUtilityController csvDownloadUtilityController;

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
            + "Destination FSA / Source FSA ->,SFSA1,SFSA2,SFSA3,SFSA4,SFSA5,SFSA6,SFSA7,SFSA8,SFSA9,SFSA10\n"
            + "DFSA1,10,9.96,9.96,9.96,9.96,7,7,8.09,7,7\n"
            + "DFSA2,10,9,9,9,9,7.81,7.81,7.89,7.89,7.89\n"
            + "DFSA3,10,9,9,9,9,9.5,9.5,7,7.89,7.89\n"
            + "DFSA4,10,9.96,9.96,9.96,9.96,8.09,8.09,7.89,8.09,8.09\n"
            + "DFSA5,10,9.96,9.96,9.96,9.96,7.81,7.81,7.89,7.89,7.89\n"
            + "DFSA6,10,9.96,9.96,9.96,9.96,7,7,8.09,8.09,8.09\n"
            + "DFSA7,10,10,10,10,10,7.81,7.81,7.89,7.89,7.89\n"
            + "DFSA8,10,9.96,9.96,9.96,9.96,7.81,7.81,8.09,6,6\n"
            + "DFSA9,10,9.5,9.5,9.5,9.5,8.09,8.09,7.89,8.09,8.09\n"
            + "DFSA10,8,8,8,8,8,8.09,8.09,6,7,7";

    doNothing().when(response).setStatus(HttpStatus.OK.value());
    doNothing().when(response).setContentLength(transitTimesTemplate.length());

    when(response.getOutputStream()).thenThrow(new IOException("Unexpected error"));

    csvDownloadUtilityController.downloadCSVTemplate("transitTime", request, response);
    verify(response, times(1)).getOutputStream();
  }

  @Test
  void downloadTransitTimesDataCSV()
      throws IOException, TransitServiceException, PostalCodeTimezoneServiceException,
          CsvDownloadUtilityServiceException {

    String csvContents =
        "orgId,BAY\n"
            + "Carrier Service:,ALL-SDND\n"
            + "Destination FSA / Source FSA ->,A0A,M1R\n"
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
  void downloadMarketRegionDataCSVTest() throws IOException, PostalCodeTimezoneServiceException {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    String marketRegionTemplate = TemplateTypes.getTemplateData("marketRegion");
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
  void downloadCarrierServiceCSVTest() throws IOException, CarrierServiceException {

    HttpServletRequest request = mock(HttpServletRequest.class);

    File file = File.createTempFile("some-prefix", "some-ext");
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
  void downloadCarrierServiceCSVTestMockedFile() throws IOException, CarrierServiceException {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    ServletOutputStream servletOutputStream = Mockito.mock(ServletOutputStream.class);
    Path path =
        Paths.get("src", "test", "resources", "carrierService", "downloadCarrierService.csv");

    when(csvDownloadUtilityService.downloadCarrierServiceDataCSV(anyString()))
        .thenReturn(path.toFile());

    when(response.getOutputStream()).thenReturn(servletOutputStream);

    csvDownloadUtilityController.downloadCarrierServiceCSV(TestUtil.ORG_ID, request, response);
    verify(csvDownloadUtilityService, times(1)).downloadCarrierServiceDataCSV(anyString());
  }
}
