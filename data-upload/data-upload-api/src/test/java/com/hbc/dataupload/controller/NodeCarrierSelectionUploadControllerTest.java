package com.hbc.dataupload.controller;

import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.FILE_URI;
import static com.hbc.dataupload.helper.NodeCarrierSelectionUploadConstants.NODE_CARRIER_SELECTION_DATA_UPLOAD_FAILED;
import static com.hbc.dataupload.helper.NodeCarrierSelectionUploadConstants.NODE_CARRIER_SELECTION_DATA_UPLOAD_PARTIAL_SUCCESS;
import static com.hbc.dataupload.helper.NodeCarrierSelectionUploadConstants.NODE_CARRIER_SELECTION_DATA_UPLOAD_SUCCESS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hbc.common.exception.CommonServiceException;
import com.hbc.common.response.BaseResponse;
import com.hbc.dataupload.service.NodeCarrierSelectionUploadService;
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

class NodeCarrierSelectionUploadControllerTest {

  @Mock NodeCarrierSelectionUploadService nodeCarrierSelectionUploadService;

  @InjectMocks private NodeCarrierSelectionUploadController nodeCarrierSelectionUploadController;

  @InjectMocks TestUtil testUtil;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void NodeServiceSelectionDataSuccessfulResponseTest() throws CommonServiceException, IOException {
    ResponseEntity<BaseResponse<String>> successfulResponseEntity =
        testUtil.getNodeCarrierSelectionDataSuccessfulResponse();
    when(nodeCarrierSelectionUploadService.nodeCarrierSelectionUpload(anyString()))
        .thenReturn(successfulResponseEntity);

    ResponseEntity<BaseResponse<String>> responseEntity =
        nodeCarrierSelectionUploadController.nodeCarrierSelectionUpload(FILE_URI);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(
        NODE_CARRIER_SELECTION_DATA_UPLOAD_SUCCESS,
        Objects.requireNonNull(responseEntity.getBody()).getMessage());

    verify(nodeCarrierSelectionUploadService, times(1)).nodeCarrierSelectionUpload(anyString());
  }

  @Test
  void nodeCarrierSelectionPartiallySuccessfulResponseTest()
      throws CommonServiceException, IOException {
    ResponseEntity<BaseResponse<String>> partiallySuccessfulResponseEntity =
        testUtil.getNodeCarrierSelectionPartiallySuccessfulResponse();
    when(nodeCarrierSelectionUploadService.nodeCarrierSelectionUpload(anyString()))
        .thenReturn(partiallySuccessfulResponseEntity);

    ResponseEntity<BaseResponse<String>> responseEntity =
        nodeCarrierSelectionUploadController.nodeCarrierSelectionUpload(FILE_URI);

    assertEquals(HttpStatus.MULTI_STATUS, responseEntity.getStatusCode());
    assertEquals(
        NODE_CARRIER_SELECTION_DATA_UPLOAD_PARTIAL_SUCCESS,
        Objects.requireNonNull(responseEntity.getBody()).getMessage());

    verify(nodeCarrierSelectionUploadService, times(1)).nodeCarrierSelectionUpload(anyString());
  }

  @Test
  void nodeCarrierSelectionFailureResponseTest() throws CommonServiceException, IOException {
    ResponseEntity<BaseResponse<String>> failureResponseEntity =
        testUtil.getNodeCarrierSelectionFailureResponse();
    when(nodeCarrierSelectionUploadService.nodeCarrierSelectionUpload(anyString()))
        .thenReturn(failureResponseEntity);

    ResponseEntity<BaseResponse<String>> responseEntity =
        nodeCarrierSelectionUploadController.nodeCarrierSelectionUpload(FILE_URI);

    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    assertEquals(
        NODE_CARRIER_SELECTION_DATA_UPLOAD_FAILED,
        Objects.requireNonNull(responseEntity.getBody()).getMessage());

    verify(nodeCarrierSelectionUploadService, times(1)).nodeCarrierSelectionUpload(anyString());
  }

  @Test
  void nodeCarrierSelectionCommonServiceExceptionTest() throws CommonServiceException, IOException {
    when(nodeCarrierSelectionUploadService.nodeCarrierSelectionUpload(anyString()))
        .thenThrow(CommonServiceException.class);

    assertThrows(
        CommonServiceException.class,
        () -> {
          nodeCarrierSelectionUploadController.nodeCarrierSelectionUpload(FILE_URI);
        });
    verify(nodeCarrierSelectionUploadService, VerificationModeFactory.times(1))
        .nodeCarrierSelectionUpload(anyString());
  }

  @Test
  void nodeCarrierSelectionIOExceptionTest() throws CommonServiceException, IOException {
    when(nodeCarrierSelectionUploadService.nodeCarrierSelectionUpload(anyString()))
        .thenThrow(IOException.class);

    assertThrows(
        IOException.class,
        () -> {
          nodeCarrierSelectionUploadController.nodeCarrierSelectionUpload(FILE_URI);
        });
    verify(nodeCarrierSelectionUploadService, VerificationModeFactory.times(1))
        .nodeCarrierSelectionUpload(anyString());
  }
}
