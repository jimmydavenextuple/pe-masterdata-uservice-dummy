/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.dataupload.controller;

import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.FILE_URI;
import static com.nextuple.dataupload.helper.NodeDataUploadConstants.NODE_DATA_UPLOAD_FAILED;
import static com.nextuple.dataupload.helper.NodeDataUploadConstants.NODE_DATA_UPLOAD_PARTIAL_SUCCESS;
import static com.nextuple.dataupload.helper.NodeDataUploadConstants.NODE_DATA_UPLOAD_SUCCESS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.dataupload.service.NodeDataUploadService;
import com.nextuple.dataupload.util.TestUtil;
import java.io.IOException;
import java.util.Objects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class NodeDataUploadControllerTest {
  @Mock private NodeDataUploadService nodeDataUploadService;

  @InjectMocks private NodeDataUploadController nodeDataUploadController;

  @InjectMocks private TestUtil testUtil;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void uploadNodeDataSuccessfulResponseTest() throws CommonServiceException, IOException {
    ResponseEntity<BaseResponse<String>> successfulResponseEntity =
        testUtil.getUploadNodeDataSuccessfulResponse();
    when(nodeDataUploadService.uploadNodeData(anyString())).thenReturn(successfulResponseEntity);

    ResponseEntity<BaseResponse<String>> responseEntity =
        nodeDataUploadController.uploadNodeData(FILE_URI);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(
        NODE_DATA_UPLOAD_SUCCESS, Objects.requireNonNull(responseEntity.getBody()).getMessage());

    verify(nodeDataUploadService, times(1)).uploadNodeData(anyString());
  }

  @Test
  void uploadNodeDataPartiallySuccessfulResponseTest() throws CommonServiceException, IOException {
    ResponseEntity<BaseResponse<String>> partiallySuccessfulResponseEntity =
        testUtil.getUploadNodeDataPartiallySuccessfulResponse();
    when(nodeDataUploadService.uploadNodeData(anyString()))
        .thenReturn(partiallySuccessfulResponseEntity);

    ResponseEntity<BaseResponse<String>> responseEntity =
        nodeDataUploadController.uploadNodeData(FILE_URI);

    assertEquals(HttpStatus.MULTI_STATUS, responseEntity.getStatusCode());
    assertEquals(
        NODE_DATA_UPLOAD_PARTIAL_SUCCESS,
        Objects.requireNonNull(responseEntity.getBody()).getMessage());

    verify(nodeDataUploadService, times(1)).uploadNodeData(anyString());
  }

  @Test
  void uploadNodeDataFailureResponseTest() throws CommonServiceException, IOException {
    ResponseEntity<BaseResponse<String>> failureResponseEntity =
        testUtil.getUploadNodeDataFailureResponse();
    when(nodeDataUploadService.uploadNodeData(anyString())).thenReturn(failureResponseEntity);

    ResponseEntity<BaseResponse<String>> responseEntity =
        nodeDataUploadController.uploadNodeData(FILE_URI);

    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    assertEquals(
        NODE_DATA_UPLOAD_FAILED, Objects.requireNonNull(responseEntity.getBody()).getMessage());

    verify(nodeDataUploadService, times(1)).uploadNodeData(anyString());
  }

  @Test
  void uploadNodeDataCommonServiceExceptionTest() throws CommonServiceException, IOException {
    when(nodeDataUploadService.uploadNodeData(anyString())).thenThrow(CommonServiceException.class);

    assertThrows(
        CommonServiceException.class,
        () -> {
          nodeDataUploadController.uploadNodeData(FILE_URI);
        });
    verify(nodeDataUploadService, times(1)).uploadNodeData(anyString());
  }

  @Test
  void uploadNodeDataIOExceptionTest() throws CommonServiceException, IOException {
    when(nodeDataUploadService.uploadNodeData(anyString())).thenThrow(IOException.class);

    assertThrows(
        IOException.class,
        () -> {
          nodeDataUploadController.uploadNodeData(FILE_URI);
        });
    verify(nodeDataUploadService, times(1)).uploadNodeData(anyString());
  }
}
