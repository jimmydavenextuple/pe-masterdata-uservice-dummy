package com.hbc.dataupload.service;

import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.ACTION_INVALID_MESSAGE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.INVALID_SELECTION_CRITERIA;
import static com.hbc.dataupload.helper.NodeCarrierSelectionUploadConstants.NODE_CARRIER_SELECTION_DATA_UPLOAD_FAILED;
import static com.hbc.dataupload.helper.NodeCarrierSelectionUploadConstants.NODE_CARRIER_SELECTION_DATA_UPLOAD_FILE_EMPTY_RECORDS;
import static com.hbc.dataupload.helper.NodeCarrierSelectionUploadConstants.NODE_CARRIER_SELECTION_DATA_UPLOAD_INVALID_FILE_HEADERS;
import static com.hbc.dataupload.helper.NodeCarrierSelectionUploadConstants.NODE_CARRIER_SELECTION_DATA_UPLOAD_INVALID_FILE_TYPE;
import static com.hbc.dataupload.helper.NodeCarrierSelectionUploadConstants.NODE_CARRIER_SELECTION_DATA_UPLOAD_LARGE_FILE_SIZE;
import static com.hbc.dataupload.helper.NodeCarrierSelectionUploadConstants.NODE_CARRIER_SELECTION_DATA_UPLOAD_LARGE_ROW_SIZE;
import static com.hbc.dataupload.helper.NodeCarrierSelectionUploadConstants.NODE_CARRIER_SELECTION_DATA_UPLOAD_PARTIAL_SUCCESS;
import static com.hbc.dataupload.helper.NodeCarrierSelectionUploadConstants.NODE_CARRIER_SELECTION_DATA_UPLOAD_SUCCESS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.hbc.common.configuration.api.domain.dto.CommonConfigurationDto;
import com.hbc.common.configuration.api.domain.feign.CommonConfigFeign;
import com.hbc.common.configuration.api.domain.inbound.CreateCommonConfigurationRequest;
import com.hbc.common.exception.CommonServiceException;
import com.hbc.common.response.BaseResponse;
import com.hbc.dataupload.util.TestUtil;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import org.junit.jupiter.api.Assertions;
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
class NodeCarrierSelectionUploadServiceTest {

  @InjectMocks NodeCarrierSelectionUploadService nodeCarrierSelectionUploadService;
  @InjectMocks private TestUtil testUtil;
  @Mock private CommonConfigFeign commonConfigFeign;

  @BeforeEach
  public void setUp() {
    ReflectionTestUtils.setField(nodeCarrierSelectionUploadService, "basePath", "");
    ReflectionTestUtils.setField(nodeCarrierSelectionUploadService, "maxSizeInKiloBytes", 10240);
    ReflectionTestUtils.setField(nodeCarrierSelectionUploadService, "maxRows", 1000);
  }

  @Test
  void NodeCarrierSelectionDataSuccessTest() throws CommonServiceException, IOException {
    Path resourceDirectory =
        Paths.get(
            "src",
            "test",
            "resources",
            "nodeCarrierSelectionUpload",
            "nodeCarrierSelection_happyPath.csv");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    BaseResponse<CommonConfigurationDto> baseResponse =
        testUtil.getSuccessfulBaseResponseForNodeCarrierSelection();
    when(commonConfigFeign.createCommonConfiguration(any(CreateCommonConfigurationRequest.class)))
        .thenReturn(baseResponse);
    when(commonConfigFeign.deleteCommonConfiguration(anyString(), anyString(), anyString()))
        .thenReturn(baseResponse);
    ResponseEntity<BaseResponse<String>> response =
        nodeCarrierSelectionUploadService.nodeCarrierSelectionUpload(absolutePath);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(
        NODE_CARRIER_SELECTION_DATA_UPLOAD_SUCCESS,
        Objects.requireNonNull(response.getBody()).getMessage());
  }

  @Test
  void NodeCarrierSelectionDataPartialSuccessTest() throws CommonServiceException, IOException {
    Path resourceDirectory =
        Paths.get(
            "src",
            "test",
            "resources",
            "nodeCarrierSelectionUpload",
            "nodeCarrierSelection_happyPath.csv");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    BaseResponse<CommonConfigurationDto> successfulBaseResponse =
        testUtil.getSuccessfulBaseResponseForNodeCarrierSelection();
    BaseResponse<CommonConfigurationDto> failedBaseResponse =
        testUtil.getFailedBaseResponseForNodeCarrierSelection();

    when(commonConfigFeign.createCommonConfiguration(any(CreateCommonConfigurationRequest.class)))
        .thenReturn(successfulBaseResponse);
    when(commonConfigFeign.deleteCommonConfiguration(anyString(), anyString(), anyString()))
        .thenReturn(failedBaseResponse);
    ResponseEntity<BaseResponse<String>> response =
        nodeCarrierSelectionUploadService.nodeCarrierSelectionUpload(absolutePath);
    assertEquals(HttpStatus.MULTI_STATUS, response.getStatusCode());
    assertEquals(
        NODE_CARRIER_SELECTION_DATA_UPLOAD_PARTIAL_SUCCESS,
        Objects.requireNonNull(response.getBody()).getMessage());
  }

  @Test
  void NodeCarrierSelectionDataFailureTest() throws CommonServiceException, IOException {
    Path resourceDirectory =
        Paths.get(
            "src",
            "test",
            "resources",
            "nodeCarrierSelectionUpload",
            "nodeCarrierSelection_happyPath.csv");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();
    ResponseEntity<BaseResponse<String>> response =
        nodeCarrierSelectionUploadService.nodeCarrierSelectionUpload(absolutePath);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals(
        NODE_CARRIER_SELECTION_DATA_UPLOAD_FAILED,
        Objects.requireNonNull(response.getBody()).getMessage());
  }

  @Test
  void NodeCarrierSelectionDataFileNotFoundExceptionTest() {
    Path resourceDirectory =
        Paths.get(
            "src", "test", "resources", "nodeCarrierSelectionUpload", "nodeCarrierSelection.csv");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    assertThrows(
        IOException.class,
        () -> nodeCarrierSelectionUploadService.nodeCarrierSelectionUpload(absolutePath));
  }

  @Test
  void NodeCarrierSelectionDataInvalidFileTypeExceptionTest() {
    Path resourceDirectory =
        Paths.get(
            "src",
            "test",
            "resources",
            "nodeCarrierSelectionUpload",
            "nodeCarrierSelection_invalidFileType.html");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () -> nodeCarrierSelectionUploadService.nodeCarrierSelectionUpload(absolutePath));

    assertEquals(NODE_CARRIER_SELECTION_DATA_UPLOAD_INVALID_FILE_TYPE, exception.getMessage());
    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
  }

  @Test
  void NodeCarrierSelectionDataEmptyRecordsExceptionTest() {
    Path resourceDirectory =
        Paths.get(
            "src",
            "test",
            "resources",
            "nodeCarrierSelectionUpload",
            "nodeCarrierSelection_noRecords.csv");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () -> nodeCarrierSelectionUploadService.nodeCarrierSelectionUpload(absolutePath));

    assertEquals(NODE_CARRIER_SELECTION_DATA_UPLOAD_FILE_EMPTY_RECORDS, exception.getMessage());
    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
  }

  @Test
  void NodeCarrierSelectionDataMaxRowsExceptionTest() {
    Path resourceDirectory =
        Paths.get(
            "src",
            "test",
            "resources",
            "nodeCarrierSelectionUpload",
            "nodeCarrierSelection_maxRows.csv");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () -> nodeCarrierSelectionUploadService.nodeCarrierSelectionUpload(absolutePath));

    assertEquals(NODE_CARRIER_SELECTION_DATA_UPLOAD_LARGE_ROW_SIZE, exception.getMessage());
    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
  }

  @Test
  void NodeCarrierSelectionDataInvalidHeadersExceptionTest() {
    Path resourceDirectory =
        Paths.get(
            "src",
            "test",
            "resources",
            "nodeCarrierSelectionUpload",
            "nodeCarrierSelection_invalidHeaders.csv");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () -> nodeCarrierSelectionUploadService.nodeCarrierSelectionUpload(absolutePath));

    assertEquals(NODE_CARRIER_SELECTION_DATA_UPLOAD_INVALID_FILE_HEADERS, exception.getMessage());
    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
  }

  @Test
  void NodeCarrierSelectionDataLargeFileSizeExceptionTest() {
    ReflectionTestUtils.setField(nodeCarrierSelectionUploadService, "maxSizeInKiloBytes", 10);
    Path resourceDirectory =
        Paths.get(
            "src",
            "test",
            "resources",
            "nodeCarrierSelectionUpload",
            "nodeCarrierSelection_largeFileSize.csv");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () -> nodeCarrierSelectionUploadService.nodeCarrierSelectionUpload(absolutePath));

    assertEquals(NODE_CARRIER_SELECTION_DATA_UPLOAD_LARGE_FILE_SIZE, exception.getMessage());
    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
  }

  @Test
  void NodeCarrierSelectionDataInvalidActionExceptionTest()
      throws CommonServiceException, IOException {
    Path resourceDirectory =
        Paths.get(
            "src",
            "test",
            "resources",
            "nodeCarrierSelectionUpload",
            "nodeCarrierSelection_invalidAction.csv");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();
    Exception exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> nodeCarrierSelectionUploadService.nodeCarrierSelectionUpload(absolutePath));
    assertEquals(ACTION_INVALID_MESSAGE, exception.getMessage());
  }

  @Test
  void NodeCarrierSelectionInvalidSelectionCriteriaExceptionTest()
      throws CommonServiceException, IOException {
    Path resourceDirectory =
        Paths.get(
            "src",
            "test",
            "resources",
            "nodeCarrierSelectionUpload",
            "nodeCarrierSelection_invalidSelectionCriteria.csv");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();
    Exception exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> nodeCarrierSelectionUploadService.nodeCarrierSelectionUpload(absolutePath));
    assertEquals(INVALID_SELECTION_CRITERIA, exception.getMessage());
  }
}
