package com.nextuple.csvdownload.controller;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.csvdownload.common.inbound.GenericUploadRequest;
import com.nextuple.csvdownload.service.EddComputationService;
import com.opencsv.exceptions.CsvException;
import java.io.File;
import java.io.IOException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class EddComputationUtilityControllerTest {

  @Mock private EddComputationService eddComputationService;

  @InjectMocks private EddComputationUtilityController eddComputationUtilityController;

  @Test
  void eddComputationDataTest() throws CommonServiceException, IOException, CsvException {
    HttpServletResponse response = mock(HttpServletResponse.class);

    doNothing().when(response).setStatus(HttpStatus.OK.value());
    ServletOutputStream servletOutputStream = mock(ServletOutputStream.class);

    when(response.getOutputStream()).thenReturn(servletOutputStream);
    when(eddComputationService.uploadEddCompuationData(GenericUploadRequest.builder().build()))
        .thenReturn(File.createTempFile("BAY", "BAY"));

    eddComputationUtilityController.eddComputationData(
        GenericUploadRequest.builder().build(), response);
    verify(response, times(1)).getOutputStream();
  }

  @Test
  void eddComputationDataExceptionTest() throws CommonServiceException, IOException, CsvException {
    HttpServletResponse response = mock(HttpServletResponse.class);
    when(eddComputationService.uploadEddCompuationData(GenericUploadRequest.builder().build()))
        .thenThrow(IOException.class);
    Assertions.assertThrows(
        IOException.class,
        () ->
            eddComputationUtilityController.eddComputationData(
                GenericUploadRequest.builder().build(), response));
  }
}
