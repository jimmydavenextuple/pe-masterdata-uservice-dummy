/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.dataupload.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.csvdownload.exception.CsvFormatValidationFailedException;
import com.nextuple.csvdownload.exception.JobSubmissionException;
import com.nextuple.dataupload.service.UploadBufferService;
import com.opencsv.exceptions.CsvException;
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
  void uploadNodeServiceOptBufferDataSuccessTest() throws CommonServiceException, IOException {
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
  void uploadNodeServiceOptBufferDataUploadFailedTest() throws CommonServiceException, IOException {
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
  void uploadNodeServiceOptBufferDataExceptionTest() throws CommonServiceException, IOException {
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
  void uploadTransitBufferDataSuccessTest()
      throws CommonServiceException, IOException, CsvException {
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
  void uploadTransitBufferDataUploadFailedTest()
      throws CommonServiceException, IOException, CsvException {
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
      throws CommonServiceException, IOException, CsvException {
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
  void uploadTransitBufferDataExceptionTest()
      throws CommonServiceException, IOException, CsvException {
    String fileUri = "fileName.csv";
    when(uploadBufferService.uploadTransitBufferData(any()))
        .thenThrow(new IOException("File not found!"));

    Exception exception =
        assertThrows(
            IOException.class, () -> uploadBufferController.uploadTransitBufferData(fileUri));

    assertEquals("File not found!", exception.getMessage());
    verify(uploadBufferService, times(1)).uploadTransitBufferData(any());
  }

  @Test
  void uploadDeleteTransitBufferData()
      throws CsvFormatValidationFailedException,
          CommonServiceException,
          IOException,
          JobSubmissionException,
          CsvException {
    String fileUri = "fileName.csv";
    when(uploadBufferService.deleteTransitBuffer(fileUri))
        .thenReturn("Delete transit buffer job submitted to job framework successfully");

    ResponseEntity<BaseResponse<String>> response =
        uploadBufferController.uploadDeleteTransitBufferData(fileUri);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    verify(uploadBufferService, times(1)).deleteTransitBuffer(fileUri);
  }

  private ResponseEntity<BaseResponse<String>> getBaseResponse(
      HttpStatus httpStatus, String message) {
    return ResponseEntity.status(httpStatus).body(BaseResponse.builder().message(message).build());
  }
}
