package com.hbc.dataupload.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.hbc.common.exception.CommonServiceException;
import com.hbc.common.response.BaseResponse;
import com.hbc.dataupload.service.UploadBufferService;
import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class UploadBufferControllerTest {

  @InjectMocks UploadBufferController uploadBufferController;

  @Mock UploadBufferService uploadBufferService;

  @Test
   void uploadNodeServiceOptBufferDataSuccessTest()
      throws CommonServiceException, IOException {
    String fileUri = "fileName.csv";
    ResponseEntity<BaseResponse<String>> response =
        getBaseResponse(HttpStatus.OK, "Node Service Option Buffer Data successfully uploaded!");
    when(uploadBufferService.uploadNodeServiceOptionBufferData(any())).thenReturn(response);

    ResponseEntity<BaseResponse<String>> actualResponse =
        uploadBufferController.uploadNodeServiceOptionBufferData(fileUri);
    assertEquals(HttpStatus.OK, actualResponse.getStatusCode());

    Assertions.assertEquals(
        "Node Service Option Buffer Data successfully uploaded!",
        actualResponse.getBody().getMessage());
    verify(uploadBufferService, times(1)).uploadNodeServiceOptionBufferData(any());
  }

  @Test
   void uploadNodeServiceOptBufferDataUploadFailedTest()
      throws CommonServiceException, IOException {
    String fileUri = "fileName.csv";
    ResponseEntity<BaseResponse<String>> response =
        getBaseResponse(HttpStatus.BAD_REQUEST, "Node Service Option Buffer Data upload failed!");
    when(uploadBufferService.uploadNodeServiceOptionBufferData(any())).thenReturn(response);

    ResponseEntity<BaseResponse<String>> actualResponse =
        uploadBufferController.uploadNodeServiceOptionBufferData(fileUri);
    assertEquals(HttpStatus.BAD_REQUEST, actualResponse.getStatusCode());

    Assertions.assertEquals(
        "Node Service Option Buffer Data upload failed!", actualResponse.getBody().getMessage());
    verify(uploadBufferService, times(1)).uploadNodeServiceOptionBufferData(any());
  }

  @Test
   void uploadNodeServiceOptBufferDataPartialUploadTest()
      throws CommonServiceException, IOException {
    String fileUri = "fileName.csv";
    ResponseEntity<BaseResponse<String>> response =
        getBaseResponse(
            HttpStatus.MULTI_STATUS,
            "Node Service Option Buffer Data partially uploaded with some rows failed!");
    when(uploadBufferService.uploadNodeServiceOptionBufferData(any())).thenReturn(response);

    ResponseEntity<BaseResponse<String>> actualResponse =
        uploadBufferController.uploadNodeServiceOptionBufferData(fileUri);

    assertEquals(HttpStatus.MULTI_STATUS, actualResponse.getStatusCode());
    Assertions.assertEquals(
        "Node Service Option Buffer Data partially uploaded with some rows failed!",
        actualResponse.getBody().getMessage());
    verify(uploadBufferService, times(1)).uploadNodeServiceOptionBufferData(any());
  }

  @Test
   void uploadNodeServiceOptBufferDataExceptionTest()
      throws CommonServiceException, IOException {
    String fileUri = "fileName.csv";
    when(uploadBufferService.uploadNodeServiceOptionBufferData(any()))
        .thenThrow(new IOException("File not found!"));

    Exception exception =
        assertThrows(
            IOException.class,
            () -> uploadBufferController.uploadNodeServiceOptionBufferData(fileUri));

    assertEquals("File not found!", exception.getMessage());
    verify(uploadBufferService, times(1)).uploadNodeServiceOptionBufferData(any());
  }

  @Test
   void uploadTransitBufferDataSuccessTest() throws CommonServiceException, IOException {
    String fileUri = "fileName.csv";
    ResponseEntity<BaseResponse<String>> response =
        getBaseResponse(HttpStatus.OK, "Transit Buffer Data successfully uploaded!");
    when(uploadBufferService.uploadTransitBufferData(any())).thenReturn(response);

    ResponseEntity<BaseResponse<String>> actualResponse =
        uploadBufferController.uploadTransitBufferData(fileUri);

    assertEquals(HttpStatus.OK, actualResponse.getStatusCode());
    Assertions.assertEquals(
        "Transit Buffer Data successfully uploaded!", actualResponse.getBody().getMessage());
    verify(uploadBufferService, times(1)).uploadTransitBufferData(any());
  }

  @Test
   void uploadTransitBufferDataUploadFailedTest() throws CommonServiceException, IOException {
    String fileUri = "fileName.csv";
    ResponseEntity<BaseResponse<String>> response =
        getBaseResponse(HttpStatus.BAD_REQUEST, "Transit Buffer Data upload failed!");
    when(uploadBufferService.uploadTransitBufferData(any())).thenReturn(response);

    ResponseEntity<BaseResponse<String>> actualResponse =
        uploadBufferController.uploadTransitBufferData(fileUri);

    assertEquals(HttpStatus.BAD_REQUEST, actualResponse.getStatusCode());
    Assertions.assertEquals(
        "Transit Buffer Data upload failed!", actualResponse.getBody().getMessage());
    verify(uploadBufferService, times(1)).uploadTransitBufferData(any());
  }

  @Test
   void uploadTransitBufferDataPartialUploadTest()
      throws CommonServiceException, IOException {
    String fileUri = "fileName.csv";
    ResponseEntity<BaseResponse<String>> response =
        getBaseResponse(
            HttpStatus.MULTI_STATUS,
            "Transit Buffer Data partially uploaded with some rows failed!");
    when(uploadBufferService.uploadTransitBufferData(any())).thenReturn(response);

    ResponseEntity<BaseResponse<String>> actualResponse =
        uploadBufferController.uploadTransitBufferData(fileUri);

    assertEquals(HttpStatus.MULTI_STATUS, actualResponse.getStatusCode());
    Assertions.assertEquals(
        "Transit Buffer Data partially uploaded with some rows failed!",
        actualResponse.getBody().getMessage());
    verify(uploadBufferService, times(1)).uploadTransitBufferData(any());
  }

  @Test
   void uploadTransitBufferDataExceptionTest() throws CommonServiceException, IOException {
    String fileUri = "fileName.csv";
    when(uploadBufferService.uploadTransitBufferData(any()))
        .thenThrow(new IOException("File not found!"));

    Exception exception =
        assertThrows(
            IOException.class, () -> uploadBufferController.uploadTransitBufferData(fileUri));

    assertEquals("File not found!", exception.getMessage());
    verify(uploadBufferService, times(1)).uploadTransitBufferData(any());
  }

  private ResponseEntity<BaseResponse<String>> getBaseResponse(
      HttpStatus httpStatus, String message) {
    return ResponseEntity.status(httpStatus).body(BaseResponse.builder().message(message).build());
  }
}
