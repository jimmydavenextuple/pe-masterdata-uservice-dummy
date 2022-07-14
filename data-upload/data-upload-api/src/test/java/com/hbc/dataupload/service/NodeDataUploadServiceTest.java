package com.hbc.dataupload.service;

import static com.hbc.dataupload.helper.NodeDataUploadConstants.NODE_DATA_UPLOAD_FAILED;
import static com.hbc.dataupload.helper.NodeDataUploadConstants.NODE_DATA_UPLOAD_FILE_EMPTY_RECORDS;
import static com.hbc.dataupload.helper.NodeDataUploadConstants.NODE_DATA_UPLOAD_INVALID_FILE_HEADERS;
import static com.hbc.dataupload.helper.NodeDataUploadConstants.NODE_DATA_UPLOAD_INVALID_FILE_TYPE;
import static com.hbc.dataupload.helper.NodeDataUploadConstants.NODE_DATA_UPLOAD_LARGE_FILE_SIZE;
import static com.hbc.dataupload.helper.NodeDataUploadConstants.NODE_DATA_UPLOAD_LARGE_ROW_SIZE;
import static com.hbc.dataupload.helper.NodeDataUploadConstants.NODE_DATA_UPLOAD_PARTIAL_SUCCESS;
import static com.hbc.dataupload.helper.NodeDataUploadConstants.NODE_DATA_UPLOAD_SUCCESS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.hbc.common.exception.CommonServiceException;
import com.hbc.common.response.BaseResponse;
import com.hbc.dataupload.util.TestUtil;
import com.hbc.node.domain.feign.NodeFeign;
import com.hbc.node.domain.inbound.NodeRequest;
import com.hbc.node.domain.inbound.NodeUpdationRequest;
import com.hbc.node.domain.outbound.NodeResponse;
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
    when(nodeFeign.updateNodeDetails(anyString(), anyString(), any(NodeUpdationRequest.class)))
        .thenReturn(baseResponse);
    when(nodeFeign.deleteNode(anyString(), anyString())).thenReturn(baseResponse);
    ResponseEntity<BaseResponse<String>> response =
        nodeDataUploadService.uploadNodeData(absolutePath);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(NODE_DATA_UPLOAD_SUCCESS, Objects.requireNonNull(response.getBody()).getMessage());
  }

  @Test
  void uploadNodeDataPartialSuccessTest() throws CommonServiceException, IOException {
    Path resourceDirectory = Paths.get("src", "test", "resources", "node", "node_happyPath.csv");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    BaseResponse<NodeResponse> successfulBaseResponse = testUtil.getSuccessfulBaseResponseForNode();
    BaseResponse<NodeResponse> failedBaseResponse = testUtil.getFailedBaseResponseForNode();

    when(nodeFeign.createNode(any(NodeRequest.class))).thenReturn(successfulBaseResponse);
    when(nodeFeign.updateNodeDetails(anyString(), anyString(), any(NodeUpdationRequest.class)))
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
