package com.hbc.dataupload.service;

import static com.hbc.dataupload.helper.PostalCodeTimezoneDataUploadConstants.POSTAL_CODE_TIMEZONE_DATA_UPLOAD_FAILED;
import static com.hbc.dataupload.helper.PostalCodeTimezoneDataUploadConstants.POSTAL_CODE_TIMEZONE_DATA_UPLOAD_FILE_EMPTY_RECORDS;
import static com.hbc.dataupload.helper.PostalCodeTimezoneDataUploadConstants.POSTAL_CODE_TIMEZONE_DATA_UPLOAD_INVALID_FILE_HEADERS;
import static com.hbc.dataupload.helper.PostalCodeTimezoneDataUploadConstants.POSTAL_CODE_TIMEZONE_DATA_UPLOAD_INVALID_FILE_TYPE;
import static com.hbc.dataupload.helper.PostalCodeTimezoneDataUploadConstants.POSTAL_CODE_TIMEZONE_DATA_UPLOAD_LARGE_FILE_SIZE;
import static com.hbc.dataupload.helper.PostalCodeTimezoneDataUploadConstants.POSTAL_CODE_TIMEZONE_DATA_UPLOAD_LARGE_ROW_SIZE;
import static com.hbc.dataupload.helper.PostalCodeTimezoneDataUploadConstants.POSTAL_CODE_TIMEZONE_DATA_UPLOAD_PARTIAL_SUCCESS;
import static com.hbc.dataupload.helper.PostalCodeTimezoneDataUploadConstants.POSTAL_CODE_TIMEZONE_DATA_UPLOAD_SUCCESS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.hbc.common.exception.CommonServiceException;
import com.hbc.common.response.BaseResponse;
import com.hbc.dataupload.util.TestUtil;
import com.hbc.postal.code.timezone.api.domain.dto.PostalCodeTimezoneDto;
import com.hbc.postal.code.timezone.api.domain.feign.PostalCodeTimezoneFeign;
import com.hbc.postal.code.timezone.api.domain.inbound.CreatePostalCodeTimezoneRequest;
import com.hbc.postal.code.timezone.api.domain.inbound.UpdatePostalCodeTimezoneRequest;
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
class PostalCodeTimezoneDataUploadServiceTest {

  @InjectMocks private PostalCodeTimezoneDataUploadService postalCodeTimezoneDataUploadService;

  @InjectMocks private TestUtil testUtil;

  @Mock private PostalCodeTimezoneFeign postalCodeTimezoneFeign;

  @BeforeEach
  public void setUp() {
    ReflectionTestUtils.setField(postalCodeTimezoneDataUploadService, "basePath", "");
    ReflectionTestUtils.setField(postalCodeTimezoneDataUploadService, "maxSizeInKiloBytes", 10240);
    ReflectionTestUtils.setField(postalCodeTimezoneDataUploadService, "maxRows", 1000);
  }

  @Test
  void uploadPostalCodeTimezoneDataSuccessTest() throws CommonServiceException, IOException {
    Path resourceDirectory =
        Paths.get(
            "src", "test", "resources", "postalCodeTimezone", "postalCodeTimezone_happyPath.csv");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    BaseResponse<PostalCodeTimezoneDto> baseResponse =
        testUtil.getSuccessfulBaseResponseForPostalCodeTimezone();
    when(postalCodeTimezoneFeign.createPostalCodeTimezone(
            any(CreatePostalCodeTimezoneRequest.class)))
        .thenReturn(baseResponse);
    when(postalCodeTimezoneFeign.updatePostalCodeTimezone(
            anyString(), anyString(), any(UpdatePostalCodeTimezoneRequest.class)))
        .thenReturn(baseResponse);
    when(postalCodeTimezoneFeign.deletePostalCodeTimezone(anyString(), anyString()))
        .thenReturn(baseResponse);
    ResponseEntity<BaseResponse<String>> response =
        postalCodeTimezoneDataUploadService.uploadPostalCodeTimezoneData(absolutePath);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(
        POSTAL_CODE_TIMEZONE_DATA_UPLOAD_SUCCESS,
        Objects.requireNonNull(response.getBody()).getMessage());
  }

  @Test
  void uploadPostalCodeTimezoneDataPartialSuccessTest() throws CommonServiceException, IOException {
    Path resourceDirectory =
        Paths.get(
            "src", "test", "resources", "postalCodeTimezone", "postalCodeTimezone_happyPath.csv");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    BaseResponse<PostalCodeTimezoneDto> successfulBaseResponse =
        testUtil.getSuccessfulBaseResponseForPostalCodeTimezone();
    BaseResponse<PostalCodeTimezoneDto> failedBaseResponse =
        testUtil.getFailedBaseResponseForPostalCodeTimezone();

    when(postalCodeTimezoneFeign.createPostalCodeTimezone(
            any(CreatePostalCodeTimezoneRequest.class)))
        .thenReturn(successfulBaseResponse);
    when(postalCodeTimezoneFeign.updatePostalCodeTimezone(
            anyString(), anyString(), any(UpdatePostalCodeTimezoneRequest.class)))
        .thenReturn(failedBaseResponse);
    when(postalCodeTimezoneFeign.deletePostalCodeTimezone(anyString(), anyString()))
        .thenReturn(failedBaseResponse);
    ResponseEntity<BaseResponse<String>> response =
        postalCodeTimezoneDataUploadService.uploadPostalCodeTimezoneData(absolutePath);
    assertEquals(HttpStatus.MULTI_STATUS, response.getStatusCode());
    assertEquals(
        POSTAL_CODE_TIMEZONE_DATA_UPLOAD_PARTIAL_SUCCESS,
        Objects.requireNonNull(response.getBody()).getMessage());
  }

  @Test
  void uploadPostalCodeTimezoneDataFailureTest() throws CommonServiceException, IOException {
    Path resourceDirectory =
        Paths.get(
            "src", "test", "resources", "postalCodeTimezone", "postalCodeTimezone_happyPath.csv");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    BaseResponse<PostalCodeTimezoneDto> baseResponse =
        testUtil.getFailedBaseResponseForPostalCodeTimezone();
    when(postalCodeTimezoneFeign.createPostalCodeTimezone(
            any(CreatePostalCodeTimezoneRequest.class)))
        .thenReturn(baseResponse);
    when(postalCodeTimezoneFeign.updatePostalCodeTimezone(
            anyString(), anyString(), any(UpdatePostalCodeTimezoneRequest.class)))
        .thenThrow(NullPointerException.class);
    when(postalCodeTimezoneFeign.deletePostalCodeTimezone(anyString(), anyString()))
        .thenReturn(baseResponse);
    ResponseEntity<BaseResponse<String>> response =
        postalCodeTimezoneDataUploadService.uploadPostalCodeTimezoneData(absolutePath);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals(
        POSTAL_CODE_TIMEZONE_DATA_UPLOAD_FAILED,
        Objects.requireNonNull(response.getBody()).getMessage());
  }

  @Test
  void uploadPostalCodeTimezoneDataFileNotFoundExceptionTest() {
    Path resourceDirectory =
        Paths.get("src", "test", "resources", "postalCodeTimezone", "postalCodeTimezone.csv");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    assertThrows(
        IOException.class,
        () -> postalCodeTimezoneDataUploadService.uploadPostalCodeTimezoneData(absolutePath));
  }

  @Test
  void uploadPostalCodeTimezoneDataInvalidFileTypeExceptionTest() {
    Path resourceDirectory =
        Paths.get(
            "src",
            "test",
            "resources",
            "postalCodeTimezone",
            "postalCodeTimezone_invalidFileType.html");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () -> postalCodeTimezoneDataUploadService.uploadPostalCodeTimezoneData(absolutePath));

    assertEquals(POSTAL_CODE_TIMEZONE_DATA_UPLOAD_INVALID_FILE_TYPE, exception.getMessage());
    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
  }

  @Test
  void uploadPostalCodeTimezoneDataEmptyRecordsExceptionTest() {
    Path resourceDirectory =
        Paths.get(
            "src", "test", "resources", "postalCodeTimezone", "postalCodeTimezone_noRecords.csv");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () -> postalCodeTimezoneDataUploadService.uploadPostalCodeTimezoneData(absolutePath));

    assertEquals(POSTAL_CODE_TIMEZONE_DATA_UPLOAD_FILE_EMPTY_RECORDS, exception.getMessage());
    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
  }

  @Test
  void uploadPostalCodeTimezoneDataMaxRowsExceptionTest() {
    Path resourceDirectory =
        Paths.get(
            "src", "test", "resources", "postalCodeTimezone", "postalCodeTimezone_maxRows.csv");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () -> postalCodeTimezoneDataUploadService.uploadPostalCodeTimezoneData(absolutePath));

    assertEquals(POSTAL_CODE_TIMEZONE_DATA_UPLOAD_LARGE_ROW_SIZE, exception.getMessage());
    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
  }

  @Test
  void uploadPostalCodeTimezoneDataInvalidHeadersExceptionTest() {
    Path resourceDirectory =
        Paths.get(
            "src",
            "test",
            "resources",
            "postalCodeTimezone",
            "postalCodeTimezone_invalidHeaders.csv");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () -> postalCodeTimezoneDataUploadService.uploadPostalCodeTimezoneData(absolutePath));

    assertEquals(POSTAL_CODE_TIMEZONE_DATA_UPLOAD_INVALID_FILE_HEADERS, exception.getMessage());
    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
  }

  @Test
  void uploadPostalCodeTimezoneDataLargeFileSizeExceptionTest() {
    ReflectionTestUtils.setField(postalCodeTimezoneDataUploadService, "maxSizeInKiloBytes", 10);
    Path resourceDirectory =
        Paths.get(
            "src",
            "test",
            "resources",
            "postalCodeTimezone",
            "postalCodeTimezone_largeFileSize.csv");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () -> postalCodeTimezoneDataUploadService.uploadPostalCodeTimezoneData(absolutePath));

    assertEquals(POSTAL_CODE_TIMEZONE_DATA_UPLOAD_LARGE_FILE_SIZE, exception.getMessage());
    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
  }

  @Test
  void uploadPostalCodeTimezoneDataInvalidActionExceptionTest()
      throws CommonServiceException, IOException {
    Path resourceDirectory =
        Paths.get(
            "src",
            "test",
            "resources",
            "postalCodeTimezone",
            "postalCodeTimezone_invalidAction.csv");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    BaseResponse<PostalCodeTimezoneDto> baseResponse =
        testUtil.getSuccessfulBaseResponseForPostalCodeTimezone();
    when(postalCodeTimezoneFeign.createPostalCodeTimezone(
            any(CreatePostalCodeTimezoneRequest.class)))
        .thenReturn(baseResponse);

    ResponseEntity<BaseResponse<String>> response =
        postalCodeTimezoneDataUploadService.uploadPostalCodeTimezoneData(absolutePath);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }
}
