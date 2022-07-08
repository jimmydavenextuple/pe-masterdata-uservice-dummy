package com.hbc.dataupload.controller;

import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.FILE_URI;
import static com.hbc.dataupload.helper.PromiseSourcingRuleDataUploadConstants.PROMISE_SOURCING_RULE_DATA_UPLOAD_FAILED;
import static com.hbc.dataupload.helper.PromiseSourcingRuleDataUploadConstants.PROMISE_SOURCING_RULE_DATA_UPLOAD_PARTIAL_SUCCESS;
import static com.hbc.dataupload.helper.PromiseSourcingRuleDataUploadConstants.PROMISE_SOURCING_RULE_DATA_UPLOAD_SUCCESS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hbc.common.exception.CommonServiceException;
import com.hbc.common.response.BaseResponse;
import com.hbc.dataupload.service.PromiseSourcingRuleDataUploadService;
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

class PromiseSourcingRuleDataUploadControllerTest {
  @Mock private PromiseSourcingRuleDataUploadService promiseSourcingRuleDataUploadUtilityService;

  @InjectMocks
  private PromiseSourcingRuleDataUploadController promiseSourcingRuleDataUploadController;

  @InjectMocks private TestUtil testUtil;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void uploadPromiseSourcingRuleDataSuccessfulResponseTest()
      throws CommonServiceException, IOException {
    ResponseEntity<BaseResponse<String>> successfulResponseEntity =
        testUtil.getUploadPromiseSourcingRuleDataSuccessfulResponse();
    when(promiseSourcingRuleDataUploadUtilityService.uploadPromiseSourcingRuleData(anyString()))
        .thenReturn(successfulResponseEntity);

    ResponseEntity<BaseResponse<String>> responseEntity =
        promiseSourcingRuleDataUploadController.uploadPromiseSourcingRuleData(FILE_URI);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(
        PROMISE_SOURCING_RULE_DATA_UPLOAD_SUCCESS,
        Objects.requireNonNull(responseEntity.getBody()).getMessage());

    verify(promiseSourcingRuleDataUploadUtilityService, times(1))
        .uploadPromiseSourcingRuleData(anyString());
  }

  @Test
  void uploadPromiseSourcingRuleDataPartiallySuccessfulResponseTest()
      throws CommonServiceException, IOException {
    ResponseEntity<BaseResponse<String>> partiallySuccessfulResponseEntity =
        testUtil.getUploadPromiseSourcingRuleDataPartiallySuccessfulResponse();
    when(promiseSourcingRuleDataUploadUtilityService.uploadPromiseSourcingRuleData(anyString()))
        .thenReturn(partiallySuccessfulResponseEntity);

    ResponseEntity<BaseResponse<String>> responseEntity =
        promiseSourcingRuleDataUploadController.uploadPromiseSourcingRuleData(FILE_URI);

    assertEquals(HttpStatus.MULTI_STATUS, responseEntity.getStatusCode());
    assertEquals(
        PROMISE_SOURCING_RULE_DATA_UPLOAD_PARTIAL_SUCCESS,
        Objects.requireNonNull(responseEntity.getBody()).getMessage());

    verify(promiseSourcingRuleDataUploadUtilityService, times(1))
        .uploadPromiseSourcingRuleData(anyString());
  }

  @Test
  void uploadPromiseSourcingRuleDataFailureResponseTest()
      throws CommonServiceException, IOException {
    ResponseEntity<BaseResponse<String>> failureResponseEntity =
        testUtil.getUploadPromiseSourcingRuleDataFailureResponse();
    when(promiseSourcingRuleDataUploadUtilityService.uploadPromiseSourcingRuleData(anyString()))
        .thenReturn(failureResponseEntity);

    ResponseEntity<BaseResponse<String>> responseEntity =
        promiseSourcingRuleDataUploadController.uploadPromiseSourcingRuleData(FILE_URI);

    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    assertEquals(
        PROMISE_SOURCING_RULE_DATA_UPLOAD_FAILED,
        Objects.requireNonNull(responseEntity.getBody()).getMessage());

    verify(promiseSourcingRuleDataUploadUtilityService, times(1))
        .uploadPromiseSourcingRuleData(anyString());
  }

  @Test
  void uploadPromiseSourcingRuleDataCommonServiceExceptionTest()
      throws CommonServiceException, IOException {
    when(promiseSourcingRuleDataUploadUtilityService.uploadPromiseSourcingRuleData(anyString()))
        .thenThrow(CommonServiceException.class);

    assertThrows(
        CommonServiceException.class,
        () -> {
          promiseSourcingRuleDataUploadController.uploadPromiseSourcingRuleData(FILE_URI);
        });
    verify(promiseSourcingRuleDataUploadUtilityService, VerificationModeFactory.times(1))
        .uploadPromiseSourcingRuleData(anyString());
  }

  @Test
  void uploadPromiseSourcingRuleDataIOExceptionTest() throws CommonServiceException, IOException {
    when(promiseSourcingRuleDataUploadUtilityService.uploadPromiseSourcingRuleData(anyString()))
        .thenThrow(IOException.class);

    assertThrows(
        IOException.class,
        () -> {
          promiseSourcingRuleDataUploadController.uploadPromiseSourcingRuleData(FILE_URI);
        });
    verify(promiseSourcingRuleDataUploadUtilityService, VerificationModeFactory.times(1))
        .uploadPromiseSourcingRuleData(anyString());
  }
}
