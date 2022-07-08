package com.hbc.dataupload.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hbc.common.exception.CommonServiceException;
import com.hbc.common.response.BaseResponse;
import com.hbc.dataupload.service.NodeCalendarDataUploadService;
import com.hbc.dataupload.util.TestUtil;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class NodeCalendarDataUploadControllerTest {

  @InjectMocks NodeCalendarDataUploadController nodeCalendarDataUploadController;

  @InjectMocks private TestUtil testUtil;

  @Mock NodeCalendarDataUploadService nodeCalendarDataUploadService;

  @Test
  void uploadNodeCalendarDataSuccessTest() throws CommonServiceException, IOException {
    String fileUri = "fileName.csv";
    ResponseEntity<BaseResponse<String>> response =
        testUtil.getBaseResponse(HttpStatus.OK, "Node Calendar Data successfully uploaded!");
    when(nodeCalendarDataUploadService.uploadNodeCalendarData(any())).thenReturn(response);

    ResponseEntity<BaseResponse<String>> actualResponse =
        nodeCalendarDataUploadController.uploadNodeCalendarData(fileUri);

    assertEquals(HttpStatus.OK, actualResponse.getStatusCode());
    assertEquals(
        "Node Calendar Data successfully uploaded!", actualResponse.getBody().getMessage());
    verify(nodeCalendarDataUploadService, times(1)).uploadNodeCalendarData(any());
  }

  @Test
  void uploadNodeCalendarDataUploadFailedTest() throws CommonServiceException, IOException {
    String fileUri = "fileName.csv";
    ResponseEntity<BaseResponse<String>> response =
        testUtil.getBaseResponse(HttpStatus.BAD_REQUEST, "Node Calendar Data upload failed!");
    when(nodeCalendarDataUploadService.uploadNodeCalendarData(any())).thenReturn(response);

    ResponseEntity<BaseResponse<String>> actualResponse =
        nodeCalendarDataUploadController.uploadNodeCalendarData(fileUri);

    assertEquals(HttpStatus.BAD_REQUEST, actualResponse.getStatusCode());
    assertEquals("Node Calendar Data upload failed!", actualResponse.getBody().getMessage());
    verify(nodeCalendarDataUploadService, times(1)).uploadNodeCalendarData(any());
  }

  @Test
  void uploadNodeCalendarDataPartialUploadTest() throws CommonServiceException, IOException {
    String fileUri = "fileName.csv";
    ResponseEntity<BaseResponse<String>> response =
        testUtil.getBaseResponse(
            HttpStatus.MULTI_STATUS,
            "Node Calendar Data partially uploaded with some rows failed!");
    when(nodeCalendarDataUploadService.uploadNodeCalendarData(any())).thenReturn(response);

    ResponseEntity<BaseResponse<String>> actualResponse =
        nodeCalendarDataUploadController.uploadNodeCalendarData(fileUri);

    assertEquals(HttpStatus.MULTI_STATUS, actualResponse.getStatusCode());
    assertEquals(
        "Node Calendar Data partially uploaded with some rows failed!",
        actualResponse.getBody().getMessage());
    verify(nodeCalendarDataUploadService, times(1)).uploadNodeCalendarData(any());
  }

  @Test
  void uploadNodeCalendarDataExceptionTest() throws CommonServiceException, IOException {
    String fileUri = "fileName.csv";
    when(nodeCalendarDataUploadService.uploadNodeCalendarData(any()))
        .thenThrow(new IOException("File not found!"));

    Exception exception =
        assertThrows(
            IOException.class,
            () -> nodeCalendarDataUploadController.uploadNodeCalendarData(fileUri));

    assertEquals("File not found!", exception.getMessage());
    verify(nodeCalendarDataUploadService, times(1)).uploadNodeCalendarData(any());
  }
}
