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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.dataupload.service.NodeCarrierCalendarDataUploadService;
import com.nextuple.dataupload.util.TestUtil;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class NodeCarrierCalendarDataUploadControllerTest {

  @InjectMocks NodeCarrierCalendarDataUploadController calendarDataUploadUtilityController;

  @InjectMocks private TestUtil testUtil;

  @Mock NodeCarrierCalendarDataUploadService calendarDataUploadUtilityService;

  @Test
  void uploadNodeCarrierCalendarDataSuccessTest() throws CommonServiceException, IOException {
    String fileUri = "fileName.csv";
    ResponseEntity<BaseResponse<String>> response =
        testUtil.getBaseResponse(
            HttpStatus.OK, "Node Carrier Calendar Data successfully uploaded!");
    when(calendarDataUploadUtilityService.uploadNodeCarrierCalendarData(any()))
        .thenReturn(response);

    ResponseEntity<BaseResponse<String>> actualResponse =
        calendarDataUploadUtilityController.uploadNodeCarrierCalendarData(fileUri);

    assertEquals(HttpStatus.OK, actualResponse.getStatusCode());
    assertEquals(
        "Node Carrier Calendar Data successfully uploaded!", actualResponse.getBody().getMessage());
    verify(calendarDataUploadUtilityService, times(1)).uploadNodeCarrierCalendarData(any());
  }

  @Test
  void uploadNodeCarrierCalendarDataUploadFailedTest() throws CommonServiceException, IOException {
    String fileUri = "fileName.csv";
    ResponseEntity<BaseResponse<String>> response =
        testUtil.getBaseResponse(
            HttpStatus.BAD_REQUEST, "Node Carrier Calendar Data upload failed!");
    when(calendarDataUploadUtilityService.uploadNodeCarrierCalendarData(any()))
        .thenReturn(response);

    ResponseEntity<BaseResponse<String>> actualResponse =
        calendarDataUploadUtilityController.uploadNodeCarrierCalendarData(fileUri);

    assertEquals(HttpStatus.BAD_REQUEST, actualResponse.getStatusCode());
    assertEquals(
        "Node Carrier Calendar Data upload failed!", actualResponse.getBody().getMessage());
    verify(calendarDataUploadUtilityService, times(1)).uploadNodeCarrierCalendarData(any());
  }

  @Test
  void uploadNodeCarrierCalendarDataPartialUploadTest() throws CommonServiceException, IOException {
    String fileUri = "fileName.csv";
    ResponseEntity<BaseResponse<String>> response =
        testUtil.getBaseResponse(
            HttpStatus.MULTI_STATUS,
            "Node Carrier Calendar Data partially uploaded with some rows failed!");
    when(calendarDataUploadUtilityService.uploadNodeCarrierCalendarData(any()))
        .thenReturn(response);

    ResponseEntity<BaseResponse<String>> actualResponse =
        calendarDataUploadUtilityController.uploadNodeCarrierCalendarData(fileUri);

    assertEquals(HttpStatus.MULTI_STATUS, actualResponse.getStatusCode());
    assertEquals(
        "Node Carrier Calendar Data partially uploaded with some rows failed!",
        actualResponse.getBody().getMessage());
    verify(calendarDataUploadUtilityService, times(1)).uploadNodeCarrierCalendarData(any());
  }

  @Test
  void uploadNodeCarrierCalendarDataExceptionTest() throws CommonServiceException, IOException {
    String fileUri = "fileName.csv";
    when(calendarDataUploadUtilityService.uploadNodeCarrierCalendarData(any()))
        .thenThrow(new IOException("File not found!"));

    Exception exception =
        assertThrows(
            IOException.class,
            () -> calendarDataUploadUtilityController.uploadNodeCarrierCalendarData(fileUri));

    assertEquals("File not found!", exception.getMessage());
    verify(calendarDataUploadUtilityService, times(1)).uploadNodeCarrierCalendarData(any());
  }
}
