package com.hbc.dataupload.controller;

import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.FILE_URI;
import static com.hbc.dataupload.helper.PostalCodeTimezoneDataUploadConstants.POSTAL_CODE_TIMEZONE_DATA_UPLOAD_FAILED;
import static com.hbc.dataupload.helper.PostalCodeTimezoneDataUploadConstants.POSTAL_CODE_TIMEZONE_DATA_UPLOAD_PARTIAL_SUCCESS;
import static com.hbc.dataupload.helper.PostalCodeTimezoneDataUploadConstants.POSTAL_CODE_TIMEZONE_DATA_UPLOAD_SUCCESS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hbc.common.exception.CommonServiceException;
import com.hbc.common.response.BaseResponse;
import com.hbc.dataupload.service.PostalCodeTimezoneDataUploadService;
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

class PostalCodeTimezoneDataUploadControllerTest {
  @Mock private PostalCodeTimezoneDataUploadService postalCodeTimezoneDataUploadService;

  @InjectMocks
  private PostalCodeTimezoneDataUploadController postalCodeTimezoneDataUploadController;

  @InjectMocks private TestUtil testUtil;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void uploadPostalCodeTimezoneDataSuccessfulResponseTest()
      throws CommonServiceException, IOException {
    ResponseEntity<BaseResponse<String>> successfulResponseEntity =
        testUtil.getUploadPostalCodeTimezoneDataSuccessfulResponse();
    when(postalCodeTimezoneDataUploadService.uploadPostalCodeTimezoneData(anyString()))
        .thenReturn(successfulResponseEntity);

    ResponseEntity<BaseResponse<String>> responseEntity =
        postalCodeTimezoneDataUploadController.uploadPostalCodeTimezoneData(FILE_URI);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(
        POSTAL_CODE_TIMEZONE_DATA_UPLOAD_SUCCESS,
        Objects.requireNonNull(responseEntity.getBody()).getMessage());

    verify(postalCodeTimezoneDataUploadService, times(1)).uploadPostalCodeTimezoneData(anyString());
  }

  @Test
  void uploadPostalCodeTimezoneDataPartiallySuccessfulResponseTest()
      throws CommonServiceException, IOException {
    ResponseEntity<BaseResponse<String>> partiallySuccessfulResponseEntity =
        testUtil.getUploadPostalCodeTimezoneDataPartiallySuccessfulResponse();
    when(postalCodeTimezoneDataUploadService.uploadPostalCodeTimezoneData(anyString()))
        .thenReturn(partiallySuccessfulResponseEntity);

    ResponseEntity<BaseResponse<String>> responseEntity =
        postalCodeTimezoneDataUploadController.uploadPostalCodeTimezoneData(FILE_URI);

    assertEquals(HttpStatus.MULTI_STATUS, responseEntity.getStatusCode());
    assertEquals(
        POSTAL_CODE_TIMEZONE_DATA_UPLOAD_PARTIAL_SUCCESS,
        Objects.requireNonNull(responseEntity.getBody()).getMessage());

    verify(postalCodeTimezoneDataUploadService, times(1)).uploadPostalCodeTimezoneData(anyString());
  }

  @Test
  void uploadPostalCodeTimezoneDataFailureResponseTest()
      throws CommonServiceException, IOException {
    ResponseEntity<BaseResponse<String>> failureResponseEntity =
        testUtil.getUploadPostalCodeTimezoneDataFailureResponse();
    when(postalCodeTimezoneDataUploadService.uploadPostalCodeTimezoneData(anyString()))
        .thenReturn(failureResponseEntity);

    ResponseEntity<BaseResponse<String>> responseEntity =
        postalCodeTimezoneDataUploadController.uploadPostalCodeTimezoneData(FILE_URI);

    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    assertEquals(
        POSTAL_CODE_TIMEZONE_DATA_UPLOAD_FAILED,
        Objects.requireNonNull(responseEntity.getBody()).getMessage());

    verify(postalCodeTimezoneDataUploadService, times(1)).uploadPostalCodeTimezoneData(anyString());
  }

  @Test
  void uploadPostalCodeTimezoneDataCommonServiceExceptionTest()
      throws CommonServiceException, IOException {
    when(postalCodeTimezoneDataUploadService.uploadPostalCodeTimezoneData(anyString()))
        .thenThrow(CommonServiceException.class);

    assertThrows(
        CommonServiceException.class,
        () -> {
          postalCodeTimezoneDataUploadController.uploadPostalCodeTimezoneData(FILE_URI);
        });
    verify(postalCodeTimezoneDataUploadService, VerificationModeFactory.times(1))
        .uploadPostalCodeTimezoneData(anyString());
  }

  @Test
  void uploadPostalCodeTimezoneDataIOExceptionTest() throws CommonServiceException, IOException {
    when(postalCodeTimezoneDataUploadService.uploadPostalCodeTimezoneData(anyString()))
        .thenThrow(IOException.class);

    assertThrows(
        IOException.class,
        () -> {
          postalCodeTimezoneDataUploadController.uploadPostalCodeTimezoneData(FILE_URI);
        });
    verify(postalCodeTimezoneDataUploadService, VerificationModeFactory.times(1))
        .uploadPostalCodeTimezoneData(anyString());
  }
}
