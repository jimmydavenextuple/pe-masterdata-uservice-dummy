package com.hbc.csvdownload.controller;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hbc.csvdownload.common.pojo.TemplateTypes;
import com.hbc.csvdownload.exception.InvalidTemplateTypeException;
import java.io.IOException;
import java.util.Optional;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class CsvDownloadUtilityControllerTest {

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

    String leadProcessingTimesTemplate =
        "nodeId,orgId,serviceOptions,processingTime (in hrs)\n"
            + "1554,BAY,SDND,2\n"
            + "1560,BAY,SDND,2\n"
            + "1101,BAY,SDND,2\n"
            + "1518,BAY,NEXTDAY,6\n"
            + "1634,BAY,EXPRESS,30.92\n"
            + "1601,BAY,EXPRESS,22.55\n"
            + "1125,BAY,EXPRESS,19.90\n"
            + "1114,BAY,SDND,24.97";

    doNothing().when(response).setStatus(HttpStatus.OK.value());
    doNothing().when(response).setContentLength(transitTimesTemplate.length());

    when(response.getOutputStream()).thenThrow(new IOException("Unexpected error"));

    csvDownloadUtilityController.downloadCSVTemplate("transitTime", request, response);
    verify(response, times(1)).getOutputStream();
  }

  @Test
  void downloadTransitTimesDataCSV() throws IOException, InvalidTemplateTypeException {
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
    ServletOutputStream servletOutputStream = mock(ServletOutputStream.class);

    when(response.getOutputStream()).thenReturn(servletOutputStream);

    csvDownloadUtilityController.downloadTransitTimesDataCSV(
        "BAY", "C-Id", "SR01", "DR01", request, response);
    verify(response, times(1)).getOutputStream();
  }

  @Test
  void downloadLogsByFilters() throws IOException, InvalidTemplateTypeException {
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

    ServletOutputStream servletOutputStream = mock(ServletOutputStream.class);

    when(response.getOutputStream()).thenReturn(servletOutputStream);

    csvDownloadUtilityController.downloadLogsByFilters(
        "BAY", "C-Id", Optional.empty(), request, response);
    verify(response, times(1)).getOutputStream();
  }
}
