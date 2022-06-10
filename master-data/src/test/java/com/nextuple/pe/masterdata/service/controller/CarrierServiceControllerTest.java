package com.nextuple.pe.masterdata.service.controller;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.pe.masterdata.controller.CarrierServiceController;
import com.nextuple.pe.masterdata.domain.inbound.CarrierServiceRequest;
import com.nextuple.pe.masterdata.domain.inbound.CarrierServiceUpdateRequest;
import com.nextuple.pe.masterdata.domain.outbound.CarrierServiceResponse;
import com.nextuple.pe.masterdata.exception.CarrierServiceDomainException;
import com.nextuple.pe.masterdata.exception.common.CommonServiceException;
import com.nextuple.pe.masterdata.service.CarrierServiceService;
import com.nextuple.pe.masterdata.service.TestUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

class CarrierServiceControllerTest {
  @InjectMocks private CarrierServiceController carrierServiceController;

  @InjectMocks private TestUtil testUtil;

  @Mock private CarrierServiceService carrierServiceService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void createCarrierServiceTest() throws CarrierServiceDomainException {
    CarrierServiceRequest carrierServiceRequest = testUtil.getCarrierServiceRequest();
    when(carrierServiceService.createCarrierService(any(CarrierServiceRequest.class)))
        .thenReturn(testUtil.getCarrierServiceResponse());

    ResponseEntity<BaseResponse<CarrierServiceResponse>> responseEntity =
        carrierServiceController.createCarrierService(carrierServiceRequest);

    Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    Assertions.assertEquals(
        testUtil.getCarrierServiceResponse(), responseEntity.getBody().getPayload());

    verify(carrierServiceService, times(1)).createCarrierService(any());
  }

  @Test
  void createCarrierServiceExceptionTest() throws CarrierServiceDomainException {
    CarrierServiceRequest carrierServiceRequest = testUtil.getCarrierServiceRequest();
    when(carrierServiceService.createCarrierService(any(CarrierServiceRequest.class)))
        .thenThrow(new RuntimeException("Failed to create carrier service"));

    Exception exception =
        Assertions.assertThrows(
            Exception.class,
            () -> carrierServiceController.createCarrierService(carrierServiceRequest));
    Assertions.assertEquals("Failed to create carrier service", exception.getMessage());

    verify(carrierServiceService, times(1)).createCarrierService(any());
  }

  @Test
  void getCarrierServiceDetailsTest() throws CarrierServiceDomainException, CommonServiceException {
    CarrierServiceResponse CarrierServiceResponse = testUtil.getCarrierServiceResponse();
    when(carrierServiceService.getCarrierServiceDetails(any(), any(), any()))
        .thenReturn(CarrierServiceResponse);

    ResponseEntity<BaseResponse<CarrierServiceResponse>> responseEntity =
        carrierServiceController.getCarrierServiceDetails(
            TestUtil.CARRIER_ID, TestUtil.SERVICE_ID, TestUtil.ORG_ID);

    Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    Assertions.assertEquals(CarrierServiceResponse, responseEntity.getBody().getPayload());

    verify(carrierServiceService, times(1)).getCarrierServiceDetails(any(), any(), any());
  }

  @Test
  void getCarrierServiceDetailsExceptionTest()
      throws CarrierServiceDomainException, CommonServiceException {
    when(carrierServiceService.getCarrierServiceDetails(any(), any(), any()))
        .thenThrow(new RuntimeException("Unable to fetch carrier service details"));

    Exception exception =
        Assertions.assertThrows(
            Exception.class,
            () ->
                carrierServiceController.getCarrierServiceDetails(
                    TestUtil.CARRIER_ID, TestUtil.SERVICE_ID, TestUtil.ORG_ID));
    Assertions.assertEquals("Unable to fetch carrier service details", exception.getMessage());
    verify(carrierServiceService, times(1)).getCarrierServiceDetails(any(), any(), any());
  }

  @Test
  void updateCarrierServiceTest() throws CarrierServiceDomainException, CommonServiceException {
    CarrierServiceUpdateRequest carrierServiceUpdateRequest =
        testUtil.getCarrierServiceUpdateRequest();
    when(carrierServiceService.updateCarrierServiceDetails(
            anyString(), anyString(), anyString(), any(CarrierServiceUpdateRequest.class)))
        .thenReturn(testUtil.getCarrierServiceUpdateResponse());

    ResponseEntity<BaseResponse<CarrierServiceResponse>> responseEntity =
        carrierServiceController.updateCarrierServiceDetails(
            TestUtil.CARRIER_ID, TestUtil.SERVICE_ID, TestUtil.ORG_ID, carrierServiceUpdateRequest);

    Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    Assertions.assertEquals(
        testUtil.getCarrierServiceUpdateResponse(), responseEntity.getBody().getPayload());

    verify(carrierServiceService, times(1))
        .updateCarrierServiceDetails(anyString(), anyString(), anyString(), any());
  }

  @Test
  void updateCarrierServiceExceptionTest()
      throws CarrierServiceDomainException, CommonServiceException {
    CarrierServiceUpdateRequest carrierServiceUpdateRequest =
        testUtil.getCarrierServiceUpdateRequest();
    when(carrierServiceService.updateCarrierServiceDetails(
            anyString(), anyString(), anyString(), any(CarrierServiceUpdateRequest.class)))
        .thenThrow(new RuntimeException("Unable to update the carrier service details"));

    Exception exception =
        Assertions.assertThrows(
            Exception.class,
            () ->
                carrierServiceController.updateCarrierServiceDetails(
                    TestUtil.CARRIER_ID,
                    TestUtil.SERVICE_ID,
                    TestUtil.ORG_ID,
                    carrierServiceUpdateRequest));
    Assertions.assertEquals("Unable to update the carrier service details", exception.getMessage());

    verify(carrierServiceService, times(1))
        .updateCarrierServiceDetails(anyString(), anyString(), anyString(), any());
  }

  @Test
  void deleteCarrierServiceTest() throws CarrierServiceDomainException, CommonServiceException {
    when(carrierServiceService.deleteCarrierService(any(), any(), any()))
        .thenReturn(testUtil.getCarrierServiceResponse());

    ResponseEntity<BaseResponse<CarrierServiceResponse>> responseEntity =
        carrierServiceController.deleteCarrierService(
            TestUtil.CARRIER_ID, TestUtil.SERVICE_ID, TestUtil.ORG_ID);

    Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    Assertions.assertEquals(
        testUtil.getCarrierServiceResponse(), responseEntity.getBody().getPayload());

    verify(carrierServiceService, times(1)).deleteCarrierService(any(), any(), any());
  }

  @Test
  void deleteCarrierServiceExceptionTest()
      throws CarrierServiceDomainException, CommonServiceException {
    when(carrierServiceService.deleteCarrierService(any(), any(), any()))
        .thenThrow(new RuntimeException("Error while deleting carrier service"));

    Exception exception =
        Assertions.assertThrows(
            Exception.class,
            () ->
                carrierServiceController.deleteCarrierService(
                    TestUtil.CARRIER_ID, TestUtil.SERVICE_ID, TestUtil.ORG_ID));
    Assertions.assertEquals("Error while deleting carrier service", exception.getMessage());

    verify(carrierServiceService, times(1)).deleteCarrierService(any(), any(), any());
  }
}
