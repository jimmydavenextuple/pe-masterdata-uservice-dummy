package com.hbc.jobs.dashboard.controller;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hbc.common.exception.CommonServiceException;
import com.hbc.common.response.BaseResponse;
import com.hbc.jobs.framework.common.domain.outbound.PreSignedUrlResponse;
import com.hbc.jobs.framework.common.service.PreSignedUrlInterface;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class FileDashboardControllerTest {

  @InjectMocks private FileDashboardController fileDashboardController;
  @Mock private PreSignedUrlInterface preSignedUrlInterface;

  @BeforeEach
  public void init() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  void getPreSignedUrlTest() throws CommonServiceException {
    when(preSignedUrlInterface.getPreSignedURL(any(), any()))
        .thenReturn(PreSignedUrlResponse.builder().build());
    ResponseEntity<BaseResponse<PreSignedUrlResponse>> response =
        fileDashboardController.getPreSignedUrl("test.csv", "transit");
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode(), "Status code");
    verify(preSignedUrlInterface, times(1)).getPreSignedURL(any(), any());
  }

  @Test
  void getPreSignedUrlExceptionTest() throws CommonServiceException {
    when(preSignedUrlInterface.getPreSignedURL(any(), any()))
        .thenThrow(new RuntimeException("error"));
    Exception ex =
        Assertions.assertThrows(
            Exception.class, () -> fileDashboardController.getPreSignedUrl("test.csv", "transit"));
    Assertions.assertEquals("error", ex.getMessage());
    verify(preSignedUrlInterface, times(1)).getPreSignedURL(any(), any());
  }

  @Test
  void downloadFileURLTest() throws CommonServiceException {
    when(preSignedUrlInterface.downloadFileURLById(anyLong()))
        .thenReturn(PreSignedUrlResponse.builder().signedURL("").filePath("nodes/xyz.csv").build());
    ResponseEntity<BaseResponse<PreSignedUrlResponse>> response =
        fileDashboardController.downloadFileURL(1L);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode(), "Status code");
    verify(preSignedUrlInterface, times(1)).downloadFileURLById(anyLong());
  }

  @Test
  void downloadFileURLExceptionTest() throws CommonServiceException {
    when(preSignedUrlInterface.downloadFileURLById(anyLong()))
        .thenThrow(new RuntimeException("error"));
    Exception ex =
        Assertions.assertThrows(Exception.class, () -> fileDashboardController.downloadFileURL(1L));
    Assertions.assertEquals("error", ex.getMessage());
    verify(preSignedUrlInterface, times(1)).downloadFileURLById(anyLong());
  }
}
