package com.hbc.csvdownload.controller;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hbc.common.exception.CommonServiceException;
import com.hbc.csvdownload.service.EddComputationService;
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
  void eddComputationDataTest() throws CommonServiceException, IOException {
    HttpServletResponse response = mock(HttpServletResponse.class);

    doNothing().when(response).setStatus(HttpStatus.OK.value());
    ServletOutputStream servletOutputStream = mock(ServletOutputStream.class);

    when(response.getOutputStream()).thenReturn(servletOutputStream);
    when(eddComputationService.uploadEddCompuationData("eddComputation"))
        .thenReturn(File.createTempFile("BAY", "BAY"));

    eddComputationUtilityController.eddComputationData("eddComputation", response);
    verify(response, times(1)).getOutputStream();
  }

  @Test
  void eddComputationDataExceptionTest() throws CommonServiceException, IOException {
    HttpServletResponse response = mock(HttpServletResponse.class);
    when(eddComputationService.uploadEddCompuationData("eddComputation"))
        .thenThrow(IOException.class);
    Assertions.assertThrows(
        IOException.class,
        () -> eddComputationUtilityController.eddComputationData("eddComputation", response));
  }
}
