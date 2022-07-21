package com.hbc.dataupload.service;

import static com.hbc.dataupload.helper.NodeCarrierDataUploadConstants.NODE_CARRIER_DATA_UPLOAD_INVALID_FILE_HEADERS;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.hbc.common.exception.CommonServiceException;
import com.hbc.common.response.BaseResponse;
import com.hbc.dataupload.util.TestUtil;
import com.hbc.node.carrier.domain.feign.NodeCarrierFeign;
import com.hbc.node.carrier.domain.outbound.NodeCarrierResponse;
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

  @Mock NodeCarrierFeign nodeCarrierFeign;

  @BeforeEach
  public void setUp() {
    ReflectionTestUtils.setField(nodeCarrierDataUploadService, "basePath", "");
    ReflectionTestUtils.setField(nodeCarrierDataUploadService, "maxSizeInKiloBytes", 10240);
    ReflectionTestUtils.setField(nodeCarrierDataUploadService, "maxRows", 1000);
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
