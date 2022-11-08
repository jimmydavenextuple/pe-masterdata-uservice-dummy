package com.nextuple.dataupload.service;

import static com.nextuple.dataupload.helper.TransitDataUploadConstants.TRANSIT_DATA_UPLOAD_FAILED;
import static com.nextuple.dataupload.helper.TransitDataUploadConstants.TRANSIT_DATA_UPLOAD_FILE_EMPTY_RECORDS;
import static com.nextuple.dataupload.helper.TransitDataUploadConstants.TRANSIT_DATA_UPLOAD_INVALID_FILE_HEADERS;
import static com.nextuple.dataupload.helper.TransitDataUploadConstants.TRANSIT_DATA_UPLOAD_INVALID_FILE_TYPE;
import static com.nextuple.dataupload.helper.TransitDataUploadConstants.TRANSIT_DATA_UPLOAD_LARGE_FILE_SIZE;
import static com.nextuple.dataupload.helper.TransitDataUploadConstants.TRANSIT_DATA_UPLOAD_LARGE_ROW_SIZE;
import static com.nextuple.dataupload.helper.TransitDataUploadConstants.TRANSIT_DATA_UPLOAD_PARTIAL_SUCCESS;
import static com.nextuple.dataupload.helper.TransitDataUploadConstants.TRANSIT_DATA_UPLOAD_SUCCESS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.dataupload.util.TestUtil;
import com.nextuple.transit.domain.feign.TransitFeign;
import com.nextuple.transit.domain.inbound.TransitDataCreationRequest;
import com.nextuple.transit.domain.inbound.TransitDataUpdationRequest;
import com.nextuple.transit.domain.outbound.TransitResponse;
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
class TransitDataUploadServiceTest {

  @InjectMocks private TransitDataUploadService transitDataUploadService;

  @InjectMocks private TestUtil testUtil;

  @Mock private TransitFeign transitFeign;

  @BeforeEach
  public void setUp() {
    ReflectionTestUtils.setField(transitDataUploadService, "basePath", "");
    ReflectionTestUtils.setField(transitDataUploadService, "maxSizeInKiloBytes", 10240);
    ReflectionTestUtils.setField(transitDataUploadService, "maxRows", 1000);
  }

  @Test
  void uploadTransitDataSuccessTest() throws CommonServiceException, IOException {
    Path resourceDirectory =
        Paths.get("src", "test", "resources", "transit", "transit_happyPath.csv");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    BaseResponse<TransitResponse> baseResponse = testUtil.getSuccessfulBaseResponseForTransit();
    when(transitFeign.addTransitData(any(TransitDataCreationRequest.class)))
        .thenReturn(baseResponse);
    when(transitFeign.updateTransitData(
            anyString(),
            anyString(),
            anyString(),
            anyString(),
            any(TransitDataUpdationRequest.class)))
        .thenReturn(baseResponse);
    when(transitFeign.deleteTransitDetails(anyString(), anyString(), anyString(), anyString()))
        .thenReturn(baseResponse);
    ResponseEntity<BaseResponse<String>> response =
        transitDataUploadService.uploadTransitData(absolutePath);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(
        TRANSIT_DATA_UPLOAD_SUCCESS, Objects.requireNonNull(response.getBody()).getMessage());
  }

  @Test
  void uploadTransitDataPartialSuccessTest() throws CommonServiceException, IOException {
    Path resourceDirectory =
        Paths.get("src", "test", "resources", "transit", "transit_happyPath.csv");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    BaseResponse<TransitResponse> successfulBaseResponse =
        testUtil.getSuccessfulBaseResponseForTransit();
    BaseResponse<TransitResponse> failedBaseResponse = testUtil.getFailedBaseResponseForTransit();

    when(transitFeign.addTransitData(any(TransitDataCreationRequest.class)))
        .thenReturn(successfulBaseResponse);
    when(transitFeign.updateTransitData(
            anyString(),
            anyString(),
            anyString(),
            anyString(),
            any(TransitDataUpdationRequest.class)))
        .thenReturn(failedBaseResponse);
    when(transitFeign.deleteTransitDetails(anyString(), anyString(), anyString(), anyString()))
        .thenReturn(failedBaseResponse);
    ResponseEntity<BaseResponse<String>> response =
        transitDataUploadService.uploadTransitData(absolutePath);
    assertEquals(HttpStatus.MULTI_STATUS, response.getStatusCode());
    assertEquals(
        TRANSIT_DATA_UPLOAD_PARTIAL_SUCCESS,
        Objects.requireNonNull(response.getBody()).getMessage());
  }

  @Test
  void uploadTransitDataFailureTest() throws CommonServiceException, IOException {
    Path resourceDirectory =
        Paths.get("src", "test", "resources", "transit", "transit_happyPath.csv");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    ResponseEntity<BaseResponse<String>> response =
        transitDataUploadService.uploadTransitData(absolutePath);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals(
        TRANSIT_DATA_UPLOAD_FAILED, Objects.requireNonNull(response.getBody()).getMessage());
  }

  @Test
  void uploadTransitDataFileNotFoundExceptionTest() {
    Path resourceDirectory = Paths.get("src", "test", "resources", "transit", "transit.csv");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    assertThrows(IOException.class, () -> transitDataUploadService.uploadTransitData(absolutePath));
  }

  @Test
  void uploadTransitDataInvalidFileTypeExceptionTest() {
    Path resourceDirectory =
        Paths.get("src", "test", "resources", "transit", "transit_invalidFileType.html");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () -> transitDataUploadService.uploadTransitData(absolutePath));

    assertEquals(TRANSIT_DATA_UPLOAD_INVALID_FILE_TYPE, exception.getMessage());
    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
  }

  @Test
  void uploadTransitDataEmptyRecordsExceptionTest() {
    Path resourceDirectory =
        Paths.get("src", "test", "resources", "transit", "transit_noRecords.csv");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () -> transitDataUploadService.uploadTransitData(absolutePath));

    assertEquals(TRANSIT_DATA_UPLOAD_FILE_EMPTY_RECORDS, exception.getMessage());
    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
  }

  @Test
  void uploadTransitDataMaxRowsExceptionTest() {
    Path resourceDirectory =
        Paths.get("src", "test", "resources", "transit", "transit_maxRows.csv");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () -> transitDataUploadService.uploadTransitData(absolutePath));

    assertEquals(TRANSIT_DATA_UPLOAD_LARGE_ROW_SIZE, exception.getMessage());
    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
  }

  @Test
  void uploadTransitDataInvalidHeadersExceptionTest() {
    Path resourceDirectory =
        Paths.get("src", "test", "resources", "transit", "transit_invalidHeaders.csv");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () -> transitDataUploadService.uploadTransitData(absolutePath));

    assertEquals(TRANSIT_DATA_UPLOAD_INVALID_FILE_HEADERS, exception.getMessage());
    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
  }

  @Test
  void uploadTransitDataLargeFileSizeExceptionTest() {
    ReflectionTestUtils.setField(transitDataUploadService, "maxSizeInKiloBytes", 10);
    Path resourceDirectory =
        Paths.get("src", "test", "resources", "transit", "transit_largeFileSize.csv");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () -> transitDataUploadService.uploadTransitData(absolutePath));

    assertEquals(TRANSIT_DATA_UPLOAD_LARGE_FILE_SIZE, exception.getMessage());
    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
  }

  @Test
  void uploadTransitDataInvalidActionExceptionTest() throws CommonServiceException, IOException {
    Path resourceDirectory =
        Paths.get("src", "test", "resources", "transit", "transit_invalidAction.csv");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    BaseResponse<TransitResponse> baseResponse = testUtil.getSuccessfulBaseResponseForTransit();
    when(transitFeign.addTransitData(any(TransitDataCreationRequest.class)))
        .thenReturn(baseResponse);

    ResponseEntity<BaseResponse<String>> response =
        transitDataUploadService.uploadTransitData(absolutePath);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }
}
