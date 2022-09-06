package com.hbc.dataupload.service;

import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.ACTION_INVALID_MESSAGE;
import static com.hbc.dataupload.helper.WeightageConfigurationDataUploadConstants.WEIGHTAGE_CONFIGURATION_DATA_UPLOAD_FAILED;
import static com.hbc.dataupload.helper.WeightageConfigurationDataUploadConstants.WEIGHTAGE_CONFIGURATION_DATA_UPLOAD_FILE_EMPTY_RECORDS;
import static com.hbc.dataupload.helper.WeightageConfigurationDataUploadConstants.WEIGHTAGE_CONFIGURATION_DATA_UPLOAD_INVALID_FILE_HEADERS;
import static com.hbc.dataupload.helper.WeightageConfigurationDataUploadConstants.WEIGHTAGE_CONFIGURATION_DATA_UPLOAD_INVALID_FILE_TYPE;
import static com.hbc.dataupload.helper.WeightageConfigurationDataUploadConstants.WEIGHTAGE_CONFIGURATION_DATA_UPLOAD_LARGE_FILE_SIZE;
import static com.hbc.dataupload.helper.WeightageConfigurationDataUploadConstants.WEIGHTAGE_CONFIGURATION_DATA_UPLOAD_LARGE_ROW_SIZE;
import static com.hbc.dataupload.helper.WeightageConfigurationDataUploadConstants.WEIGHTAGE_CONFIGURATION_DATA_UPLOAD_PARTIAL_SUCCESS;
import static com.hbc.dataupload.helper.WeightageConfigurationDataUploadConstants.WEIGHTAGE_CONFIGURATION_DATA_UPLOAD_SUCCESS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.hbc.common.exception.CommonServiceException;
import com.hbc.common.response.BaseResponse;
import com.hbc.dataupload.util.TestUtil;
import com.hbc.weightage.configuration.api.domain.dto.WeightageConfigurationDto;
import com.hbc.weightage.configuration.api.domain.feign.WeightageConfigurationFeign;
import com.hbc.weightage.configuration.api.domain.inbound.CreateWeightageConfigurationRequest;
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
class WeightageConfigurationDataUploadServiceTest {

  @InjectMocks
  private WeightageConfigurationDataUploadService weightageConfigurationDataUploadService;

  @InjectMocks private TestUtil testUtil;

  @Mock private WeightageConfigurationFeign weightageConfigurationFeign;

  @BeforeEach
  public void setUp() {
    ReflectionTestUtils.setField(weightageConfigurationDataUploadService, "basePath", "");
    ReflectionTestUtils.setField(
        weightageConfigurationDataUploadService, "maxSizeInKiloBytes", 10240);
    ReflectionTestUtils.setField(weightageConfigurationDataUploadService, "maxRows", 1000);
  }

  @Test
  void uploadWeightageConfigurationDataSuccessTest() throws CommonServiceException, IOException {
    Path resourceDirectory =
        Paths.get(
            "src",
            "test",
            "resources",
            "weightageConfiguration",
            "weightageConfiguration_happyPath.csv");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    BaseResponse<WeightageConfigurationDto> baseResponse =
        testUtil.getSuccessfulBaseResponseForWeightageConfiguration();
    when(weightageConfigurationFeign.createWeightageConfiguration(
            any(CreateWeightageConfigurationRequest.class)))
        .thenReturn(baseResponse);
    when(weightageConfigurationFeign.deleteWeightageConfiguration(
            anyString(), anyString(), anyString()))
        .thenReturn(baseResponse);
    ResponseEntity<BaseResponse<String>> response =
        weightageConfigurationDataUploadService.uploadWeightageConfigurationData(absolutePath);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(
        WEIGHTAGE_CONFIGURATION_DATA_UPLOAD_SUCCESS,
        Objects.requireNonNull(response.getBody()).getMessage());
  }

  @Test
  void uploadWeightageConfigurationDataDeleteSuccessTest()
      throws CommonServiceException, IOException {
    Path resourceDirectory =
        Paths.get(
            "src",
            "test",
            "resources",
            "weightageConfiguration",
            "weightageConfiguration_deleteHappyPath.csv");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    BaseResponse<WeightageConfigurationDto> baseResponse =
        testUtil.getSuccessfulBaseResponseForWeightageConfiguration();
    when(weightageConfigurationFeign.createWeightageConfiguration(
            any(CreateWeightageConfigurationRequest.class)))
        .thenReturn(baseResponse);
    when(weightageConfigurationFeign.deleteWeightageConfiguration(
            anyString(), anyString(), anyString()))
        .thenReturn(baseResponse);
    ResponseEntity<BaseResponse<String>> response =
        weightageConfigurationDataUploadService.uploadWeightageConfigurationData(absolutePath);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(
        WEIGHTAGE_CONFIGURATION_DATA_UPLOAD_SUCCESS,
        Objects.requireNonNull(response.getBody()).getMessage());
  }

  @Test
  void uploadWeightageConfigurationDataPartialSuccessTest()
      throws CommonServiceException, IOException {
    Path resourceDirectory =
        Paths.get(
            "src",
            "test",
            "resources",
            "weightageConfiguration",
            "weightageConfiguration_happyPath.csv");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    BaseResponse<WeightageConfigurationDto> successfulBaseResponse =
        testUtil.getSuccessfulBaseResponseForWeightageConfiguration();
    BaseResponse<WeightageConfigurationDto> failedBaseResponse =
        testUtil.getFailedBaseResponseForWeightageConfiguration();

    when(weightageConfigurationFeign.createWeightageConfiguration(
            any(CreateWeightageConfigurationRequest.class)))
        .thenReturn(successfulBaseResponse);
    when(weightageConfigurationFeign.deleteWeightageConfiguration(
            anyString(), anyString(), anyString()))
        .thenReturn(failedBaseResponse);
    ResponseEntity<BaseResponse<String>> response =
        weightageConfigurationDataUploadService.uploadWeightageConfigurationData(absolutePath);
    assertEquals(HttpStatus.MULTI_STATUS, response.getStatusCode());
    assertEquals(
        WEIGHTAGE_CONFIGURATION_DATA_UPLOAD_PARTIAL_SUCCESS,
        Objects.requireNonNull(response.getBody()).getMessage());
  }

  @Test
  void uploadWeightageConfigurationDataFailureTest() throws CommonServiceException, IOException {
    Path resourceDirectory =
        Paths.get(
            "src",
            "test",
            "resources",
            "weightageConfiguration",
            "weightageConfiguration_happyPath.csv");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    ResponseEntity<BaseResponse<String>> response =
        weightageConfigurationDataUploadService.uploadWeightageConfigurationData(absolutePath);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals(
        WEIGHTAGE_CONFIGURATION_DATA_UPLOAD_FAILED,
        Objects.requireNonNull(response.getBody()).getMessage());
  }

  @Test
  void uploadWeightageConfigurationDataFileNotFoundExceptionTest() {
    Path resourceDirectory =
        Paths.get(
            "src", "test", "resources", "weightageConfiguration", "weightageConfiguration.csv");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    assertThrows(
        IOException.class,
        () ->
            weightageConfigurationDataUploadService.uploadWeightageConfigurationData(absolutePath));
  }

  @Test
  void uploadWeightageConfigurationInvalidFileTypeExceptionTest() {
    Path resourceDirectory =
        Paths.get(
            "src",
            "test",
            "resources",
            "weightageConfiguration",
            "weightageConfiguration_invalidFileType.html");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () ->
                weightageConfigurationDataUploadService.uploadWeightageConfigurationData(
                    absolutePath));

    assertEquals(WEIGHTAGE_CONFIGURATION_DATA_UPLOAD_INVALID_FILE_TYPE, exception.getMessage());
    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
  }

  @Test
  void uploadWeightageConfigurationDataEmptyRecordsExceptionTest() {
    Path resourceDirectory =
        Paths.get(
            "src",
            "test",
            "resources",
            "weightageConfiguration",
            "weightageConfiguration_noRecords.csv");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () ->
                weightageConfigurationDataUploadService.uploadWeightageConfigurationData(
                    absolutePath));

    assertEquals(WEIGHTAGE_CONFIGURATION_DATA_UPLOAD_FILE_EMPTY_RECORDS, exception.getMessage());
    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
  }

  @Test
  void uploadWeightageConfigurationDataMaxRowsExceptionTest() {
    Path resourceDirectory =
        Paths.get(
            "src",
            "test",
            "resources",
            "weightageConfiguration",
            "weightageConfiguration_maxRows.csv");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () ->
                weightageConfigurationDataUploadService.uploadWeightageConfigurationData(
                    absolutePath));

    assertEquals(WEIGHTAGE_CONFIGURATION_DATA_UPLOAD_LARGE_ROW_SIZE, exception.getMessage());
    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
  }

  @Test
  void uploadWeightageConfigurationInvalidHeadersExceptionTest() {
    Path resourceDirectory =
        Paths.get(
            "src",
            "test",
            "resources",
            "weightageConfiguration",
            "weightageConfiguration_invalidHeaders.csv");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () ->
                weightageConfigurationDataUploadService.uploadWeightageConfigurationData(
                    absolutePath));

    assertEquals(WEIGHTAGE_CONFIGURATION_DATA_UPLOAD_INVALID_FILE_HEADERS, exception.getMessage());
    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
  }

  @Test
  void uploadWeightageConfigurationDataLargeFileSizeExceptionTest() {
    ReflectionTestUtils.setField(weightageConfigurationDataUploadService, "maxSizeInKiloBytes", 10);
    Path resourceDirectory =
        Paths.get(
            "src",
            "test",
            "resources",
            "weightageConfiguration",
            "weightageConfiguration_largeFileSize.csv");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () ->
                weightageConfigurationDataUploadService.uploadWeightageConfigurationData(
                    absolutePath));

    assertEquals(WEIGHTAGE_CONFIGURATION_DATA_UPLOAD_LARGE_FILE_SIZE, exception.getMessage());
    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
  }

  @Test
  void uploadWeightageConfigurationDataInvalidActionExceptionTest()
      throws CommonServiceException, IOException {
    Path resourceDirectory =
        Paths.get(
            "src",
            "test",
            "resources",
            "weightageConfiguration",
            "weightageConfiguration_invalidAction.csv");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    BaseResponse<WeightageConfigurationDto> baseResponse =
        testUtil.getSuccessfulBaseResponseForWeightageConfiguration();
    when(weightageConfigurationFeign.createWeightageConfiguration(
            any(CreateWeightageConfigurationRequest.class)))
        .thenReturn(baseResponse);
    when(weightageConfigurationFeign.deleteWeightageConfiguration(
            anyString(), anyString(), anyString()))
        .thenReturn(baseResponse);

    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () ->
                weightageConfigurationDataUploadService.uploadWeightageConfigurationData(
                    absolutePath));

    assertEquals(ACTION_INVALID_MESSAGE, exception.getMessage());
    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
  }

  @Test
  void uploadWeightageConfigurationDataNullActionExceptionTest()
      throws CommonServiceException, IOException {
    Path resourceDirectory =
        Paths.get(
            "src",
            "test",
            "resources",
            "weightageConfiguration",
            "weightageConfiguration_nullAction.csv");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () ->
                weightageConfigurationDataUploadService.uploadWeightageConfigurationData(
                    absolutePath));

    assertEquals(ACTION_INVALID_MESSAGE, exception.getMessage());
    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
  }
}
