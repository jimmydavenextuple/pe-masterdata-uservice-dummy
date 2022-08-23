package com.hbc.csvdownload.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hbc.common.response.BaseResponse;
import com.hbc.csvdownload.common.TestUtil;
import com.hbc.csvdownload.exception.CsvFormatValidationFailedException;
import com.hbc.csvdownload.exception.CsvParsingException;
import com.hbc.csvdownload.exception.InvalidTemplateTypeException;
import com.hbc.csvdownload.exception.JobSubmissionException;
import com.hbc.csvdownload.exception.JsonParsingException;
import com.hbc.csvdownload.service.CsvUploadUtilityService;
import java.io.IOException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class CsvUtilityControllerTest {

  @Mock private CsvUploadUtilityService csvUploadUtilityService;

  @InjectMocks private CsvUtilityController csvUtilityController;

  @Test
  void downloadCSVTemplate() throws IOException, InvalidTemplateTypeException {
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
    doNothing().when(response).setContentLength(leadProcessingTimesTemplate.length());
    ServletOutputStream servletOutputStream = mock(ServletOutputStream.class);

    when(response.getOutputStream()).thenReturn(servletOutputStream);

    csvUtilityController.downloadCSVTemplate("transitTime", request, response);
    csvUtilityController.downloadCSVTemplate("processingLeadTime", request, response);
    verify(response, times(2)).getOutputStream();
  }

  @Test
  void downloadCSVTemplateInvalidTemplateTypeException() {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    Exception exception =
        Assertions.assertThrows(
            InvalidTemplateTypeException.class,
            () -> csvUtilityController.downloadCSVTemplate("transitTime1", request, response));

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

    csvUtilityController.downloadCSVTemplate("transitTime", request, response);
    verify(response, times(1)).getOutputStream();
  }

  @Test
  void uploadProcessingLeadTimes()
      throws CsvParsingException, CsvFormatValidationFailedException, JobSubmissionException,
          JsonParsingException {
    MultipartFile csvFile = mock(MultipartFile.class);
    when(csvUploadUtilityService.uploadProcessingLeadTimesCsv(any(), any()))
        .thenReturn("Job to bulk upload processing lead times is submitted successfully");

    ResponseEntity<BaseResponse<String>> res =
        csvUtilityController.uploadProcessingLeadTimes(TestUtil.ORG_ID, csvFile);
    Assertions.assertEquals(HttpStatus.OK, res.getStatusCode());
    Assertions.assertNotNull(res.getBody());
    Assertions.assertNotNull(res.getBody().getPayload());
    Assertions.assertFalse(ObjectUtils.isEmpty(res.getBody().getPayload()));
  }

  @Test
  void uploadProcessingLeadTimesException()
      throws CsvParsingException, CsvFormatValidationFailedException, JobSubmissionException,
          JsonParsingException {
    MultipartFile csvFile = mock(MultipartFile.class);
    when(csvUploadUtilityService.uploadProcessingLeadTimesCsv(any(), any()))
        .thenThrow(new CsvParsingException("Invalid CSV template"));

    Exception exception =
        Assertions.assertThrows(
            CsvParsingException.class,
            () -> csvUtilityController.uploadProcessingLeadTimes(TestUtil.ORG_ID, csvFile));
    Assertions.assertNotNull(exception);
  }
}
