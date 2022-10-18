package com.hbc.transit.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.hbc.common.exception.CommonServiceException;
import com.hbc.transit.TestUtil;
import com.hbc.transit.domain.entity.TransitBufferConfigRequestEntity;
import com.hbc.transit.domain.enums.TransitBufferConfigRequestStatusEnum;
import com.hbc.transit.domain.inbound.TransitBufferConfigRequest;
import com.hbc.transit.domain.outbound.TransitBufferConfigResponse;
import com.hbc.transit.repository.TransitBufferConfigRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class TransitBufferConfigRequestServiceTest {

  @InjectMocks private TransitBufferConfigRequestService transitBufferConfigRequestService;

  @InjectMocks private TestUtil testUtil;

  @Mock private TransitBufferConfigRepository transitBufferConfigRepository;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void createTransitBufferRequestTest() throws CommonServiceException {
    TransitBufferConfigRequest transitBufferConfigRequest =
        testUtil.getTransitBufferConfigRequest();
    when(transitBufferConfigRepository.save(any(TransitBufferConfigRequestEntity.class)))
        .thenReturn(
            testUtil.getTransitBufferConfigRequestEntity(
                TransitBufferConfigRequestStatusEnum.CREATED));
    TransitBufferConfigResponse transitBufferConfigResponse =
        transitBufferConfigRequestService.createTransitBufferRequest(transitBufferConfigRequest);
    Assertions.assertEquals(
        testUtil.getTransitBufferConfigResponse(TransitBufferConfigRequestStatusEnum.CREATED),
        transitBufferConfigResponse);
    verify(transitBufferConfigRepository, times(1))
        .save(any(TransitBufferConfigRequestEntity.class));
  }

  @Test
  void createTransitBufferRequestTestException() {
    TransitBufferConfigRequest transitBufferConfigRequest =
        testUtil.getTransitBufferConfigRequest();
    when(transitBufferConfigRepository.save(any(TransitBufferConfigRequestEntity.class)))
        .thenThrow(new RuntimeException("Unable to create transit buffer config request"));
    Exception exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                transitBufferConfigRequestService.createTransitBufferRequest(
                    transitBufferConfigRequest));
    Assertions.assertEquals(
        "Unable to create transit buffer config request", exception.getMessage());
    verify(transitBufferConfigRepository, times(1))
        .save(any(TransitBufferConfigRequestEntity.class));
  }

  @Test
  void updateTransitBufferRequestStatusTest1() throws CommonServiceException {
    when(transitBufferConfigRepository.findById(anyLong()))
        .thenReturn(
            Optional.of(
                testUtil.getTransitBufferConfigRequestEntity(
                    TransitBufferConfigRequestStatusEnum.CREATED)));
    when(transitBufferConfigRepository.save(any(TransitBufferConfigRequestEntity.class)))
        .thenReturn(
            testUtil.getTransitBufferConfigRequestEntity(
                TransitBufferConfigRequestStatusEnum.INPROGRESS));
    TransitBufferConfigResponse transitBufferConfigResponse =
        transitBufferConfigRequestService.updateTransitBufferRequestStatus(
            TestUtil.ID, TransitBufferConfigRequestStatusEnum.INPROGRESS);
    Assertions.assertEquals(
        testUtil.getTransitBufferConfigResponse(TransitBufferConfigRequestStatusEnum.INPROGRESS),
        transitBufferConfigResponse);
    verify(transitBufferConfigRepository, times(1))
        .save(any(TransitBufferConfigRequestEntity.class));
    verify(transitBufferConfigRepository, times(1)).findById(anyLong());
  }

  @Test
  void updateTransitBufferRequestStatusTest2() {
    when(transitBufferConfigRepository.findById(anyLong())).thenReturn(Optional.empty());
    Exception exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                transitBufferConfigRequestService.updateTransitBufferRequestStatus(
                    TestUtil.ID, TransitBufferConfigRequestStatusEnum.INPROGRESS));
    Assertions.assertEquals(
        "Transit buffer config request not found with given id", exception.getMessage());
    verify(transitBufferConfigRepository, times(0))
        .save(any(TransitBufferConfigRequestEntity.class));
    verify(transitBufferConfigRepository, times(1)).findById(anyLong());
  }

  @Test
  void fetchTransitBufferRequestsTest() throws CommonServiceException {
    TransitBufferConfigRequestEntity transitBufferConfigRequestEntity1 =
        testUtil.getTransitBufferConfigRequestEntity(TransitBufferConfigRequestStatusEnum.CREATED);
    TransitBufferConfigRequestEntity transitBufferConfigRequestEntity2 =
        testUtil.getTransitBufferConfigRequestEntity(
            TransitBufferConfigRequestStatusEnum.COMPLETED);
    when(transitBufferConfigRepository.findByOrgIdAndCarrierServiceId(
            anyString(), anyString(), any()))
        .thenReturn(List.of(transitBufferConfigRequestEntity1, transitBufferConfigRequestEntity2));
    List<TransitBufferConfigResponse> transitBufferConfigResponse =
        transitBufferConfigRequestService.fetchTransitBufferRequests(
            TestUtil.ORG_ID, TestUtil.CARRIER_SERVICE_ID);
    Assertions.assertEquals(2, transitBufferConfigResponse.size());
    verify(transitBufferConfigRepository, times(1))
        .findByOrgIdAndCarrierServiceId(anyString(), anyString(), any());
  }

  @Test
  void fetchTransitBufferRequestsTestException() throws CommonServiceException {
    when(transitBufferConfigRepository.findByOrgIdAndCarrierServiceId(
            anyString(), anyString(), any()))
        .thenThrow(new RuntimeException("Unable to fetch transit buffer requests"));
    Exception exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                transitBufferConfigRequestService.fetchTransitBufferRequests(
                    TestUtil.ORG_ID, TestUtil.CARRIER_SERVICE_ID));

    Assertions.assertEquals("Unable to fetch transit buffer requests", exception.getMessage());
    verify(transitBufferConfigRepository, times(1))
        .findByOrgIdAndCarrierServiceId(anyString(), anyString(), any());
  }

  @Test
  void getTransitBufferRequestTest() throws CommonServiceException {
    when(transitBufferConfigRepository.findById(anyLong()))
        .thenReturn(
            Optional.of(
                testUtil.getTransitBufferConfigRequestEntity(
                    TransitBufferConfigRequestStatusEnum.CREATED)));
    TransitBufferConfigRequestEntity transitBufferConfigRequestEntity =
        transitBufferConfigRequestService.getTransitBufferRequest(TestUtil.ID);
    Assertions.assertEquals(
        testUtil.getTransitBufferConfigRequestEntity(TransitBufferConfigRequestStatusEnum.CREATED),
        transitBufferConfigRequestEntity);
    verify(transitBufferConfigRepository, times(1)).findById(anyLong());
  }

  @Test
  void getTransitBufferRequestExceptionTest() {
    when(transitBufferConfigRepository.findById(anyLong())).thenReturn(Optional.empty());
    Exception exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> transitBufferConfigRequestService.getTransitBufferRequest(TestUtil.ID));
    Assertions.assertEquals(
        "Transit buffer config request not found with given id", exception.getMessage());
    verify(transitBufferConfigRepository, times(1)).findById(anyLong());
  }
}
