package com.hbc.dataupload.controller;

import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.FILE_URI;
import static com.hbc.dataupload.helper.CarrierDataUploadConstants.CARRIER_DATA_UPLOAD_FAILED;
import static com.hbc.dataupload.helper.CarrierDataUploadConstants.CARRIER_DATA_UPLOAD_PARTIAL_SUCCESS;
import static com.hbc.dataupload.helper.CarrierDataUploadConstants.CARRIER_DATA_UPLOAD_SUCCESS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hbc.common.exception.CommonServiceException;
import com.hbc.common.response.BaseResponse;
import com.hbc.dataupload.service.CarrierDataUploadService;
import com.hbc.dataupload.util.TestUtil;
import java.io.IOException;
import java.util.Objects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.verification.VerificationModeFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class CarrierDataUploadControllerTest {
  @Mock private CarrierDataUploadService carrierDataUploadService;

  @InjectMocks private CarrierDataUploadController carrierDataUploadController;

  @InjectMocks private TestUtil testUtil;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void uploadCarrierDataSuccessfulResponseTest() throws CommonServiceException, IOException {
    ResponseEntity<BaseResponse<String>> successfulResponseEntity =
        testUtil.getUploadCarrierDataSuccessfulResponse();
    when(carrierDataUploadService.uploadCarrierData(anyString()))
        .thenReturn(successfulResponseEntity);

    ResponseEntity<BaseResponse<String>> responseEntity =
        carrierDataUploadController.uploadCarrierData(FILE_URI);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(
        CARRIER_DATA_UPLOAD_SUCCESS, Objects.requireNonNull(responseEntity.getBody()).getMessage());

    verify(carrierDataUploadService, times(1)).uploadCarrierData(anyString());
  }

  @Test
  void uploadCarrierDataPartiallySuccessfulResponseTest()
      throws CommonServiceException, IOException {
    ResponseEntity<BaseResponse<String>> partiallySuccessfulResponseEntity =
        testUtil.getUploadCarrierDataPartiallySuccessfulResponse();
    when(carrierDataUploadService.uploadCarrierData(anyString()))
        .thenReturn(partiallySuccessfulResponseEntity);

    ResponseEntity<BaseResponse<String>> responseEntity =
        carrierDataUploadController.uploadCarrierData(FILE_URI);

    assertEquals(HttpStatus.MULTI_STATUS, responseEntity.getStatusCode());
    assertEquals(
        CARRIER_DATA_UPLOAD_PARTIAL_SUCCESS,
        Objects.requireNonNull(responseEntity.getBody()).getMessage());

    verify(carrierDataUploadService, times(1)).uploadCarrierData(anyString());
  }

  @Test
  void uploadCarrierDataFailureResponseTest() throws CommonServiceException, IOException {
    ResponseEntity<BaseResponse<String>> failureResponseEntity =
        testUtil.getUploadCarrierDataFailureResponse();
    when(carrierDataUploadService.uploadCarrierData(anyString())).thenReturn(failureResponseEntity);

    ResponseEntity<BaseResponse<String>> responseEntity =
        carrierDataUploadController.uploadCarrierData(FILE_URI);

    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    assertEquals(
        CARRIER_DATA_UPLOAD_FAILED, Objects.requireNonNull(responseEntity.getBody()).getMessage());

    verify(carrierDataUploadService, times(1)).uploadCarrierData(anyString());
  }

  @Test
  void uploadCarrierDataCommonServiceExceptionTest() throws CommonServiceException, IOException {
    when(carrierDataUploadService.uploadCarrierData(anyString()))
        .thenThrow(CommonServiceException.class);

    assertThrows(
        CommonServiceException.class,
        () -> {
          carrierDataUploadController.uploadCarrierData(FILE_URI);
        });
    verify(carrierDataUploadService, VerificationModeFactory.times(1))
        .uploadCarrierData(anyString());
  }

  @Test
  void uploadCarrierDataIOExceptionTest() throws CommonServiceException, IOException {
    when(carrierDataUploadService.uploadCarrierData(anyString())).thenThrow(IOException.class);

    assertThrows(
        IOException.class,
        () -> {
          carrierDataUploadController.uploadCarrierData(FILE_URI);
        });
    verify(carrierDataUploadService, VerificationModeFactory.times(1))
        .uploadCarrierData(anyString());
  }
}
