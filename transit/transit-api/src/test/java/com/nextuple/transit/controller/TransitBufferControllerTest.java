package com.nextuple.transit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.transit.TestUtil;
import com.nextuple.transit.domain.outbound.TransitBufferResponse;
import com.nextuple.transit.service.TransitBufferService;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class TransitBufferControllerTest {

  @InjectMocks private TransitBufferController transitBufferController;

  @Mock private TransitBufferService transitBufferService;

  @InjectMocks private TestUtil testUtil;

  @Test
  void createTransitBufferTest() throws CommonServiceException {
    TransitBufferResponse transitBufferResponse = testUtil.getTransitBufferResponse();

    when(transitBufferService.saveTransitBuffer(any())).thenReturn(transitBufferResponse);

    ResponseEntity<BaseResponse<TransitBufferResponse>> response =
        transitBufferController.createTransitBuffer(testUtil.getTransitBufferRequest());

    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertEquals(transitBufferResponse, response.getBody().getPayload());
    verify(transitBufferService, times(1)).saveTransitBuffer(any());
  }

  @Test
  void getByOrgIdAndDestinationGeozoneTest() throws CommonServiceException {
    List<TransitBufferResponse> transitBufferResponses =
        List.of(testUtil.getTransitBufferResponse());

    when(transitBufferService.getTransitBuffersByOrgIdAndDestinationGeozone(any(), any()))
        .thenReturn(transitBufferResponses);

    ResponseEntity<BaseResponse<List<TransitBufferResponse>>> response =
        transitBufferController.getByOrgIdAndDestinationGeozone(
            TestUtil.ORG_ID, TestUtil.DESTINATION_GEOZONE);

    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertEquals(transitBufferResponses.size(), response.getBody().getPayload().size());
    verify(transitBufferService, times(1))
        .getTransitBuffersByOrgIdAndDestinationGeozone(any(), any());
  }

  @Test
  void updateTransitBufferTest() throws CommonServiceException {
    TransitBufferResponse transitBufferResponse = testUtil.getTransitBufferResponse();

    when(transitBufferService.updateTransitBuffer(any())).thenReturn(transitBufferResponse);

    ResponseEntity<BaseResponse<TransitBufferResponse>> response =
        transitBufferController.updateTransitBuffer(testUtil.getTransitBufferRequest());

    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertEquals(transitBufferResponse, response.getBody().getPayload());
    verify(transitBufferService, times(1)).updateTransitBuffer(any());
  }

  @Test
  void deleteTransitBufferDetailsTest() throws CommonServiceException {
    TransitBufferResponse transitBufferResponse = testUtil.getTransitBufferResponse();

    when(transitBufferService.deleteTransitBufferDetails(any(), any(), any(), any()))
        .thenReturn(transitBufferResponse);

    ResponseEntity<BaseResponse<TransitBufferResponse>> response =
        transitBufferController.deleteTransitBufferDetails(testUtil.getTransitBufferRequest());

    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertEquals(transitBufferResponse, response.getBody().getPayload());
    verify(transitBufferService, times(1)).deleteTransitBufferDetails(any(), any(), any(), any());
  }
}
