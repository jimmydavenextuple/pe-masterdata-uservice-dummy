/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.dataupload.service;

import static com.nextuple.dataupload.helper.NodeDataUploadConstants.NODE_DATA_UPLOAD_FAILED;
import static com.nextuple.dataupload.helper.NodeDataUploadConstants.NODE_DATA_UPLOAD_FILE_EMPTY_RECORDS;
import static com.nextuple.dataupload.helper.NodeDataUploadConstants.NODE_DATA_UPLOAD_INVALID_FILE_HEADERS;
import static com.nextuple.dataupload.helper.NodeDataUploadConstants.NODE_DATA_UPLOAD_INVALID_FILE_TYPE;
import static com.nextuple.dataupload.helper.NodeDataUploadConstants.NODE_DATA_UPLOAD_LARGE_FILE_SIZE;
import static com.nextuple.dataupload.helper.NodeDataUploadConstants.NODE_DATA_UPLOAD_LARGE_ROW_SIZE;
import static com.nextuple.dataupload.helper.NodeDataUploadConstants.NODE_DATA_UPLOAD_PARTIAL_SUCCESS;
import static com.nextuple.dataupload.helper.NodeDataUploadConstants.NODE_DATA_UPLOAD_SUCCESS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.dataupload.util.TestUtil;
import com.nextuple.node.domain.feign.NodeFeign;
import com.nextuple.node.domain.inbound.NodeBaseRequest;
import com.nextuple.node.domain.inbound.NodeRequest;
import com.nextuple.node.domain.outbound.NodeResponse;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class NodeDataUploadServiceTest {

  @InjectMocks private NodeDataUploadService nodeDataUploadService;

  @InjectMocks private TestUtil testUtil;

  @Mock private NodeFeign nodeFeign;

  @BeforeEach
  public void setUp() {
    ReflectionTestUtils.setField(nodeDataUploadService, "basePath", "");
    ReflectionTestUtils.setField(nodeDataUploadService, "maxSizeInKiloBytes", 10240);
    ReflectionTestUtils.setField(nodeDataUploadService, "maxRows", 1000);
  }

  @Test
  void uploadNodeDataSuccessTest() throws CommonServiceException, IOException {
    Path resourceDirectory = Paths.get("src", "test", "resources", "node", "node_happyPath.csv");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    BaseResponse<NodeResponse> baseResponse = testUtil.getSuccessfulBaseResponseForNode();
    when(nodeFeign.createNode(any(NodeRequest.class))).thenReturn(baseResponse);
    when(nodeFeign.updateNodeDetails(anyString(), anyString(), any(NodeBaseRequest.class)))
        .thenReturn(baseResponse);
    when(nodeFeign.deleteNode(anyString(), anyString())).thenReturn(baseResponse);
    ResponseEntity<BaseResponse<String>> response =
        nodeDataUploadService.uploadNodeData(absolutePath);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(NODE_DATA_UPLOAD_SUCCESS, Objects.requireNonNull(response.getBody()).getMessage());
  }

  @Test
  void uploadNodeDataSuccessWithStartAndLastWorkingTimeTest()
      throws CommonServiceException, IOException {
    Path resourceDirectory =
        Paths.get("src", "test", "resources", "node", "node_happyPath_startAndLastWT.csv");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    BaseResponse<NodeResponse> baseResponse =
        testUtil.getSuccessfulBaseResponseForNodeWithStartAndLastWorkingTime();
    when(nodeFeign.createNode(any(NodeRequest.class))).thenReturn(baseResponse);
    when(nodeFeign.updateNodeDetails(anyString(), anyString(), any(NodeBaseRequest.class)))
        .thenReturn(baseResponse);
    when(nodeFeign.deleteNode(anyString(), anyString())).thenReturn(baseResponse);
    ResponseEntity<BaseResponse<String>> response =
        nodeDataUploadService.uploadNodeData(absolutePath);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(NODE_DATA_UPLOAD_SUCCESS, Objects.requireNonNull(response.getBody()).getMessage());
  }

  @Test
  void uploadNodeDataSuccessWithoutStartWorkingTimeTest()
      throws CommonServiceException, IOException {
    Path resourceDirectory =
        Paths.get("src", "test", "resources", "node", "node_withoutStartWorkingTime.csv");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();
    BaseResponse<NodeResponse> failedBaseResponse = testUtil.getFailedBaseResponseForNode();
    when(nodeFeign.createNode(any(NodeRequest.class))).thenReturn(failedBaseResponse);

    ResponseEntity<BaseResponse<String>> response =
        nodeDataUploadService.uploadNodeData(absolutePath);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals(NODE_DATA_UPLOAD_FAILED, Objects.requireNonNull(response.getBody()).getMessage());
  }

  @Test
  void uploadNodeDataPartialSuccessTest() throws CommonServiceException, IOException {
    Path resourceDirectory = Paths.get("src", "test", "resources", "node", "node_happyPath.csv");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    BaseResponse<NodeResponse> successfulBaseResponse = testUtil.getSuccessfulBaseResponseForNode();
    BaseResponse<NodeResponse> failedBaseResponse = testUtil.getFailedBaseResponseForNode();

    when(nodeFeign.createNode(any(NodeRequest.class))).thenReturn(successfulBaseResponse);
    when(nodeFeign.updateNodeDetails(anyString(), anyString(), any(NodeBaseRequest.class)))
        .thenReturn(failedBaseResponse);
    when(nodeFeign.deleteNode(anyString(), anyString())).thenReturn(failedBaseResponse);
    ResponseEntity<BaseResponse<String>> response =
        nodeDataUploadService.uploadNodeData(absolutePath);
    assertEquals(HttpStatus.MULTI_STATUS, response.getStatusCode());
    assertEquals(
        NODE_DATA_UPLOAD_PARTIAL_SUCCESS, Objects.requireNonNull(response.getBody()).getMessage());
  }

  @Test
  void uploadNodeDataFailureTest() throws CommonServiceException, IOException {
    Path resourceDirectory = Paths.get("src", "test", "resources", "node", "node_happyPath.csv");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    ResponseEntity<BaseResponse<String>> response =
        nodeDataUploadService.uploadNodeData(absolutePath);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals(NODE_DATA_UPLOAD_FAILED, Objects.requireNonNull(response.getBody()).getMessage());
  }

  @Test
  void uploadNodeDataFileNotFoundExceptionTest() {
    Path resourceDirectory = Paths.get("src", "test", "resources", "node", "node.csv");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    assertThrows(IOException.class, () -> nodeDataUploadService.uploadNodeData(absolutePath));
  }

  @Test
  void uploadNodeDataInvalidFileTypeExceptionTest() {
    Path resourceDirectory =
        Paths.get("src", "test", "resources", "node", "node_invalidFileType.html");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class, () -> nodeDataUploadService.uploadNodeData(absolutePath));

    assertEquals(NODE_DATA_UPLOAD_INVALID_FILE_TYPE, exception.getMessage());
    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
  }

  @Test
  void uploadNodeDataEmptyRecordsExceptionTest() {
    Path resourceDirectory = Paths.get("src", "test", "resources", "node", "node_noRecords.csv");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class, () -> nodeDataUploadService.uploadNodeData(absolutePath));

    assertEquals(NODE_DATA_UPLOAD_FILE_EMPTY_RECORDS, exception.getMessage());
    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
  }

  @Test
  void uploadNodeDataMaxRowsExceptionTest() {
    Path resourceDirectory = Paths.get("src", "test", "resources", "node", "node_maxRows.csv");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class, () -> nodeDataUploadService.uploadNodeData(absolutePath));

    assertEquals(NODE_DATA_UPLOAD_LARGE_ROW_SIZE, exception.getMessage());
    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
  }

  @Test
  void uploadNodeDataInvalidHeadersExceptionTest() {
    Path resourceDirectory =
        Paths.get("src", "test", "resources", "node", "node_invalidHeaders.csv");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class, () -> nodeDataUploadService.uploadNodeData(absolutePath));

    assertEquals(NODE_DATA_UPLOAD_INVALID_FILE_HEADERS, exception.getMessage());
    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
  }

  @Test
  void uploadNodeDataLargeFileSizeExceptionTest() {
    ReflectionTestUtils.setField(nodeDataUploadService, "maxSizeInKiloBytes", 100);
    Path resourceDirectory =
        Paths.get("src", "test", "resources", "node", "node_largeFileSize.csv");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class, () -> nodeDataUploadService.uploadNodeData(absolutePath));

    assertEquals(NODE_DATA_UPLOAD_LARGE_FILE_SIZE, exception.getMessage());
    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
  }

  @Test
  void uploadNodeDataInvalidActionExceptionTest() throws CommonServiceException, IOException {
    Path resourceDirectory =
        Paths.get("src", "test", "resources", "node", "node_invalidAction.csv");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    BaseResponse<NodeResponse> baseResponse = testUtil.getSuccessfulBaseResponseForNode();
    when(nodeFeign.createNode(any(NodeRequest.class))).thenReturn(baseResponse);

    ResponseEntity<BaseResponse<String>> response =
        nodeDataUploadService.uploadNodeData(absolutePath);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }
}
