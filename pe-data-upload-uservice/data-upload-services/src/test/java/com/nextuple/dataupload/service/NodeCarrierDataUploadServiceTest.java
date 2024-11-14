/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.dataupload.service;

import static com.nextuple.dataupload.helper.NodeCarrierDataUploadConstants.NODE_CARRIER_DATA_UPLOAD_INVALID_FILE_HEADERS;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.dataupload.util.TestUtil;
import com.nextuple.node.carrier.domain.feign.impl.NodeCarrierV2Feign;
import com.nextuple.node.carrier.domain.outbound.NodeCarrierResponse;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class NodeCarrierDataUploadServiceTest {

  @InjectMocks NodeCarrierDataUploadService nodeCarrierDataUploadService;

  @InjectMocks TestUtil testUtil;

  @Mock NodeCarrierV2Feign nodeCarrierFeign;

  @BeforeEach
  public void setUp() {
    ReflectionTestUtils.setField(nodeCarrierDataUploadService, "basePath", "");
    ReflectionTestUtils.setField(nodeCarrierDataUploadService, "maxSizeInKiloBytes", 10240);
    ReflectionTestUtils.setField(nodeCarrierDataUploadService, "maxRows", 1000);
    ReflectionTestUtils.setField(
        nodeCarrierDataUploadService, "nodeCarrierFeign", nodeCarrierFeign);
  }

  @Test
  void uploadNodeCarrierDataSuccessTest() throws CommonServiceException, IOException {
    Path resourceDirectory =
        Paths.get("src", "test", "resources", "nodeCarrier", "nodeCarrier_happyPath.csv");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    BaseResponse<NodeCarrierResponse> baseResponse =
        testUtil.getBaseResponseOfNodeCarrierResponse();
    when(nodeCarrierFeign.createNodeCarrier(any())).thenReturn(baseResponse);
    when(nodeCarrierFeign.updateNodeCarrier(any(), any(), any(), any(), any()))
        .thenReturn(baseResponse);
    when(nodeCarrierFeign.deleteNodeCarrier(any(), any(), any(), any())).thenReturn(baseResponse);
    ResponseEntity<BaseResponse<String>> response =
        nodeCarrierDataUploadService.uploadNodeCarrierData(absolutePath);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertEquals(
        "Node Carrier Data successfully uploaded!", response.getBody().getMessage());
  }

  @Test
  void uploadNodeCarrierInvalidHeadersExceptionTest() {
    Path resourceDirectory =
        Paths.get("src", "test", "resources", "nodeCarrier", "nodeCarrier_invalidHeaders.csv");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () -> nodeCarrierDataUploadService.uploadNodeCarrierData(absolutePath));

    Assertions.assertEquals(NODE_CARRIER_DATA_UPLOAD_INVALID_FILE_HEADERS, exception.getMessage());
    Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
  }

  @Test
  void uploadNodeCarrierInvalidFileTypeExceptionTest() {
    Path resourceDirectory =
        Paths.get("src", "test", "resources", "nodeCarrier", "nodeCarrier_invalidFileType.html");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () -> nodeCarrierDataUploadService.uploadNodeCarrierData(absolutePath));

    Assertions.assertEquals(
        "Node Carrier data uploaded file is not a csv file.", exception.getMessage());
    Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
  }

  @Test
  void uploadNodeCarrierLargeFileSizeExceptionTest() {
    ReflectionTestUtils.setField(nodeCarrierDataUploadService, "maxSizeInKiloBytes", 1);

    Path resourceDirectory =
        Paths.get("src", "test", "resources", "nodeCarrier", "nodeCarrier_largeFileSize.csv");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () -> nodeCarrierDataUploadService.uploadNodeCarrierData(absolutePath));

    Assertions.assertEquals(
        "Node Carrier data uploaded file has size greater than 10240 kB.", exception.getMessage());
    Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
  }

  @Test
  void uploadNodeCarrierEmptyRecordsExceptionTest() {
    Path resourceDirectory =
        Paths.get("src", "test", "resources", "nodeCarrier", "nodeCarrier_emptyRecords.csv");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () -> nodeCarrierDataUploadService.uploadNodeCarrierData(absolutePath));

    Assertions.assertEquals(
        "Node Carrier data uploaded file has no records.", exception.getMessage());
    Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
  }

  @Test
  void uploadNodeCarrierLargeRowSizeExceptionTest() {
    ReflectionTestUtils.setField(nodeCarrierDataUploadService, "maxRows", 30);

    Path resourceDirectory =
        Paths.get("src", "test", "resources", "nodeCarrier", "nodeCarrier_largeRowSize.csv");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () -> nodeCarrierDataUploadService.uploadNodeCarrierData(absolutePath));

    Assertions.assertEquals(
        "Node Carrier data uploaded file has exceeded maximum file size limit.",
        exception.getMessage());
    Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
  }

  @Test
  void uploadNodeCarrierInvalidActionExceptionTest() throws CommonServiceException, IOException {
    Path resourceDirectory =
        Paths.get("src", "test", "resources", "nodeCarrier", "nodeCarrier_invalidAction.csv");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    ResponseEntity<BaseResponse<String>> response =
        nodeCarrierDataUploadService.uploadNodeCarrierData(absolutePath);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  @Test
  void uploadNodeCarrierDataFailureTest() throws CommonServiceException, IOException {
    Path resourceDirectory =
        Paths.get("src", "test", "resources", "nodeCarrier", "nodeCarrier.csv");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    ResponseEntity<BaseResponse<String>> response =
        nodeCarrierDataUploadService.uploadNodeCarrierData(absolutePath);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Assertions.assertEquals("Node Carrier Data upload failed!", response.getBody().getMessage());
  }

  @Test
  void uploadNodeCarrierDataPartialUploadTest() throws CommonServiceException, IOException {
    Path resourceDirectory =
        Paths.get("src", "test", "resources", "nodeCarrier", "nodeCarrier.csv");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    BaseResponse<NodeCarrierResponse> baseResponse =
        testUtil.getBaseResponseOfNodeCarrierResponse();
    when(nodeCarrierFeign.createNodeCarrier(any())).thenReturn(baseResponse);
    when(nodeCarrierFeign.updateNodeCarrier(any(), any(), any(), any(), any()))
        .thenReturn(baseResponse);
    ResponseEntity<BaseResponse<String>> response =
        nodeCarrierDataUploadService.uploadNodeCarrierData(absolutePath);

    assertEquals(HttpStatus.MULTI_STATUS, response.getStatusCode());
    Assertions.assertEquals(
        "Node Carrier Data partially uploaded with some rows failed!",
        response.getBody().getMessage());
  }
}
