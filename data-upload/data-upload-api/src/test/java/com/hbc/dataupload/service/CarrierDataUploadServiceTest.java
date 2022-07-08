package com.hbc.dataupload.service;

import static com.hbc.dataupload.helper.CarrierDataUploadConstants.CARRIER_DATA_UPLOAD_FAILED;
import static com.hbc.dataupload.helper.CarrierDataUploadConstants.CARRIER_DATA_UPLOAD_FILE_EMPTY_RECORDS;
import static com.hbc.dataupload.helper.CarrierDataUploadConstants.CARRIER_DATA_UPLOAD_INVALID_FILE_HEADERS;
import static com.hbc.dataupload.helper.CarrierDataUploadConstants.CARRIER_DATA_UPLOAD_INVALID_FILE_TYPE;
import static com.hbc.dataupload.helper.CarrierDataUploadConstants.CARRIER_DATA_UPLOAD_LARGE_FILE_SIZE;
import static com.hbc.dataupload.helper.CarrierDataUploadConstants.CARRIER_DATA_UPLOAD_LARGE_ROW_SIZE;
import static com.hbc.dataupload.helper.CarrierDataUploadConstants.CARRIER_DATA_UPLOAD_PARTIAL_SUCCESS;
import static com.hbc.dataupload.helper.CarrierDataUploadConstants.CARRIER_DATA_UPLOAD_SUCCESS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.hbc.carrier.domain.feign.CarrierFeign;
import com.hbc.carrier.domain.inbound.CarrierServiceRequest;
import com.hbc.carrier.domain.inbound.CarrierServiceUpdateRequest;
import com.hbc.carrier.domain.outbound.CarrierServiceResponse;
import com.hbc.common.exception.CommonServiceException;
import com.hbc.common.response.BaseResponse;
import com.hbc.dataupload.util.TestUtil;
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
class CarrierDataUploadServiceTest {

  @InjectMocks private CarrierDataUploadService carrierDataUploadService;

  @InjectMocks private TestUtil testUtil;

  @Mock private CarrierFeign carrierFeign;

  @BeforeEach
  public void setUp() {
    ReflectionTestUtils.setField(carrierDataUploadService, "basePath", "");
    ReflectionTestUtils.setField(carrierDataUploadService, "maxSizeInKiloBytes", 10240);
    ReflectionTestUtils.setField(carrierDataUploadService, "maxRows", 1000);
  }

  @Test
  void uploadCarrierDataSuccessTest() throws CommonServiceException, IOException {
    Path resourceDirectory =
        Paths.get("src", "test", "resources", "carrier", "carrier_happyPath.csv");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    BaseResponse<CarrierServiceResponse> baseResponse =
        testUtil.getSuccessfulBaseResponseForCarrier();
    when(carrierFeign.createCarrierService(any(CarrierServiceRequest.class)))
        .thenReturn(baseResponse);
    when(carrierFeign.updateCarrierServiceDetails(
            anyString(), anyString(), anyString(), any(CarrierServiceUpdateRequest.class)))
        .thenReturn(baseResponse);
    when(carrierFeign.deleteCarrierService(anyString(), anyString(), anyString()))
        .thenReturn(baseResponse);
    ResponseEntity<BaseResponse<String>> response =
        carrierDataUploadService.uploadCarrierData(absolutePath);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(
        CARRIER_DATA_UPLOAD_SUCCESS, Objects.requireNonNull(response.getBody()).getMessage());
  }

  @Test
  void uploadCarrierDataPartialSuccessTest() throws CommonServiceException, IOException {
    Path resourceDirectory =
        Paths.get("src", "test", "resources", "carrier", "carrier_happyPath.csv");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    BaseResponse<CarrierServiceResponse> successfulBaseResponse =
        testUtil.getSuccessfulBaseResponseForCarrier();
    BaseResponse<CarrierServiceResponse> failedBaseResponse =
        testUtil.getFailedBaseResponseForCarrier();

    when(carrierFeign.createCarrierService(any(CarrierServiceRequest.class)))
        .thenReturn(successfulBaseResponse);
    when(carrierFeign.updateCarrierServiceDetails(
            anyString(), anyString(), anyString(), any(CarrierServiceUpdateRequest.class)))
        .thenReturn(failedBaseResponse);
    when(carrierFeign.deleteCarrierService(anyString(), anyString(), anyString()))
        .thenReturn(failedBaseResponse);
    ResponseEntity<BaseResponse<String>> response =
        carrierDataUploadService.uploadCarrierData(absolutePath);
    assertEquals(HttpStatus.MULTI_STATUS, response.getStatusCode());
    assertEquals(
        CARRIER_DATA_UPLOAD_PARTIAL_SUCCESS,
        Objects.requireNonNull(response.getBody()).getMessage());
  }

  @Test
  void uploadCarrierDataFailureTest() throws CommonServiceException, IOException {
    Path resourceDirectory =
        Paths.get("src", "test", "resources", "carrier", "carrier_happyPath.csv");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    BaseResponse<CarrierServiceResponse> baseResponse = testUtil.getFailedBaseResponseForCarrier();
    when(carrierFeign.createCarrierService(any(CarrierServiceRequest.class)))
        .thenReturn(baseResponse);
    when(carrierFeign.updateCarrierServiceDetails(
            anyString(), anyString(), anyString(), any(CarrierServiceUpdateRequest.class)))
        .thenThrow(NullPointerException.class);
    when(carrierFeign.deleteCarrierService(anyString(), anyString(), anyString()))
        .thenReturn(baseResponse);
    ResponseEntity<BaseResponse<String>> response =
        carrierDataUploadService.uploadCarrierData(absolutePath);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals(
        CARRIER_DATA_UPLOAD_FAILED, Objects.requireNonNull(response.getBody()).getMessage());
  }

  @Test
  void uploadCarrierDataFileNotFoundExceptionTest() {
    Path resourceDirectory = Paths.get("src", "test", "resources", "carrier", "carrier.csv");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    assertThrows(IOException.class, () -> carrierDataUploadService.uploadCarrierData(absolutePath));
  }

  @Test
  void uploadCarrierDataInvalidFileTypeExceptionTest() {
    Path resourceDirectory =
        Paths.get("src", "test", "resources", "carrier", "carrier_invalidFileType.html");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () -> carrierDataUploadService.uploadCarrierData(absolutePath));

    assertEquals(CARRIER_DATA_UPLOAD_INVALID_FILE_TYPE, exception.getMessage());
    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
  }

  @Test
  void uploadCarrierDataEmptyRecordsExceptionTest() {
    Path resourceDirectory =
        Paths.get("src", "test", "resources", "carrier", "carrier_noRecords.csv");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () -> carrierDataUploadService.uploadCarrierData(absolutePath));

    assertEquals(CARRIER_DATA_UPLOAD_FILE_EMPTY_RECORDS, exception.getMessage());
    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
  }

  @Test
  void uploadCarrierDataMaxRowsExceptionTest() {
    Path resourceDirectory =
        Paths.get("src", "test", "resources", "carrier", "carrier_maxRows.csv");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () -> carrierDataUploadService.uploadCarrierData(absolutePath));

    assertEquals(CARRIER_DATA_UPLOAD_LARGE_ROW_SIZE, exception.getMessage());
    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
  }

  @Test
  void uploadCarrierDataInvalidHeadersExceptionTest() {
    Path resourceDirectory =
        Paths.get("src", "test", "resources", "carrier", "carrier_invalidHeaders.csv");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () -> carrierDataUploadService.uploadCarrierData(absolutePath));

    assertEquals(CARRIER_DATA_UPLOAD_INVALID_FILE_HEADERS, exception.getMessage());
    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
  }

  @Test
  void uploadNodeDataLargeFileSizeExceptionTest() {
    ReflectionTestUtils.setField(carrierDataUploadService, "maxSizeInKiloBytes", 10);
    Path resourceDirectory =
        Paths.get("src", "test", "resources", "carrier", "carrier_largeFileSize.csv");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () -> carrierDataUploadService.uploadCarrierData(absolutePath));

    assertEquals(CARRIER_DATA_UPLOAD_LARGE_FILE_SIZE, exception.getMessage());
    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
  }

  @Test
  void uploadNodeDataInvalidActionExceptionTest() throws CommonServiceException, IOException {
    Path resourceDirectory =
        Paths.get("src", "test", "resources", "carrier", "carrier_invalidAction.csv");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    BaseResponse<CarrierServiceResponse> baseResponse =
        testUtil.getSuccessfulBaseResponseForCarrier();
    when(carrierFeign.createCarrierService(any(CarrierServiceRequest.class)))
        .thenReturn(baseResponse);

    ResponseEntity<BaseResponse<String>> response =
        carrierDataUploadService.uploadCarrierData(absolutePath);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }
}
