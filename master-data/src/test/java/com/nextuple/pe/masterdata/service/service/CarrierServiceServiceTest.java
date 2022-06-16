package com.nextuple.pe.masterdata.service.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.nextuple.carrier.domain.outbound.CarrierServiceResponse;
import com.nextuple.pe.masterdata.domain.CarrierServiceDomain;
import com.nextuple.pe.masterdata.domain.entity.CarrierServiceEntity;
import com.nextuple.pe.masterdata.domain.inbound.CarrierServiceRequest;
import com.nextuple.pe.masterdata.domain.inbound.CarrierServiceUpdateRequest;
import com.nextuple.pe.masterdata.exception.CarrierServiceDomainException;
import com.nextuple.pe.masterdata.exception.common.CommonServiceException;
import com.nextuple.pe.masterdata.service.CarrierServiceService;
import com.nextuple.pe.masterdata.service.TestUtil;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class CarrierServiceServiceTest {

  @InjectMocks private CarrierServiceService carrierServiceService;

  @InjectMocks private TestUtil testUtil;

  @Mock private CarrierServiceDomain carrierServiceDomain;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void createCarrierServiceTest() throws CarrierServiceDomainException {
    CarrierServiceEntity carrierServiceEntity = testUtil.getCarrierServiceEntity();
    CarrierServiceRequest carrierServiceRequest = testUtil.getCarrierServiceRequest();
    when(carrierServiceDomain.saveCarrierServiceEntity(any(CarrierServiceEntity.class)))
        .thenReturn(carrierServiceEntity);

    CarrierServiceResponse dto =
        carrierServiceService.createCarrierService(testUtil.getCarrierServiceRequest());
    Assertions.assertEquals(carrierServiceRequest.getCarrierId(), dto.getCarrierId());
    verify(carrierServiceDomain, times(1))
        .saveCarrierServiceEntity(any(CarrierServiceEntity.class));
  }

  @Test
  void updateCarrierServiceDetailsTest()
      throws CarrierServiceDomainException, CommonServiceException {
    CarrierServiceEntity carrierServiceEntity = testUtil.getCarrierServiceEntity();
    CarrierServiceUpdateRequest carrierServiceUpdateRequest =
        testUtil.getCarrierServiceUpdateRequest();
    CarrierServiceEntity updatedCarrierServiceEntity = testUtil.getUpdatedCarrierServiceEntity();
    when(carrierServiceDomain.findCarrierServiceByCarrierIdAndServiceIdAndOrgId(
            any(), any(), any()))
        .thenReturn(Optional.of(carrierServiceEntity));
    when(carrierServiceDomain.saveCarrierServiceEntity(any()))
        .thenReturn(updatedCarrierServiceEntity);

    CarrierServiceResponse CarrierServiceResponse =
        carrierServiceService.updateCarrierServiceDetails(
            TestUtil.CARRIER_ID, TestUtil.SERVICE_ID, TestUtil.ORG_ID, carrierServiceUpdateRequest);
    Assertions.assertEquals(testUtil.getCarrierServiceUpdateResponse(), CarrierServiceResponse);

    verify(carrierServiceDomain, times(1))
        .saveCarrierServiceEntity(any(CarrierServiceEntity.class));
    verify(carrierServiceDomain, times(1))
        .findCarrierServiceByCarrierIdAndServiceIdAndOrgId(any(), any(), any());
  }

  @Test
  void updateCarrierServiceDetailsTestNotFoundException() throws CarrierServiceDomainException {
    CarrierServiceUpdateRequest carrierServiceUpdateRequest =
        testUtil.getCarrierServiceUpdateRequest();
    when(carrierServiceDomain.findCarrierServiceByCarrierIdAndServiceIdAndOrgId(
            any(), any(), any()))
        .thenReturn(Optional.empty());

    Exception exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                carrierServiceService.updateCarrierServiceDetails(
                    TestUtil.CARRIER_ID,
                    TestUtil.SERVICE_ID,
                    TestUtil.ORG_ID,
                    carrierServiceUpdateRequest));
    Assertions.assertEquals("Carrier service not found with given details", exception.getMessage());

    verify(carrierServiceDomain, times(0))
        .saveCarrierServiceEntity(any(CarrierServiceEntity.class));
    verify(carrierServiceDomain, times(1))
        .findCarrierServiceByCarrierIdAndServiceIdAndOrgId(any(), any(), any());
  }

  @Test
  void getCarrierServiceDetailsTest() throws CarrierServiceDomainException, CommonServiceException {
    CarrierServiceEntity carrierServiceEntity = testUtil.getCarrierServiceEntity();
    when(carrierServiceDomain.findCarrierServiceByCarrierIdAndServiceIdAndOrgId(
            any(), any(), any()))
        .thenReturn(Optional.of(carrierServiceEntity));

    CarrierServiceResponse CarrierServiceResponse =
        carrierServiceService.getCarrierServiceDetails(
            TestUtil.CARRIER_ID, TestUtil.SERVICE_ID, TestUtil.ORG_ID);
    Assertions.assertEquals(testUtil.getCarrierServiceUpdateResponse(), CarrierServiceResponse);
    verify(carrierServiceDomain, times(1))
        .findCarrierServiceByCarrierIdAndServiceIdAndOrgId(any(), any(), any());
  }

  @Test
  void getCarrierServiceDetailsTestException() throws CarrierServiceDomainException {
    when(carrierServiceDomain.findCarrierServiceByCarrierIdAndServiceIdAndOrgId(
            any(), any(), any()))
        .thenReturn(Optional.empty());

    Exception exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                carrierServiceService.getCarrierServiceDetails(
                    TestUtil.CARRIER_ID, TestUtil.SERVICE_ID, TestUtil.ORG_ID));
    Assertions.assertEquals("Carrier service not found with given details", exception.getMessage());

    verify(carrierServiceDomain, times(1))
        .findCarrierServiceByCarrierIdAndServiceIdAndOrgId(any(), any(), any());
  }

  @Test
  void deleteCarrierServiceTest() throws CarrierServiceDomainException, CommonServiceException {
    CarrierServiceEntity carrierServiceEntity = testUtil.getCarrierServiceEntity();
    when(carrierServiceDomain.findCarrierServiceByCarrierIdAndServiceIdAndOrgId(
            any(), any(), any()))
        .thenReturn(Optional.of(carrierServiceEntity));

    CarrierServiceResponse CarrierServiceResponse =
        carrierServiceService.deleteCarrierService(
            TestUtil.CARRIER_ID, TestUtil.SERVICE_ID, TestUtil.ORG_ID);
    Assertions.assertEquals(testUtil.getCarrierServiceResponse(), CarrierServiceResponse);
    verify(carrierServiceDomain, times(1))
        .findCarrierServiceByCarrierIdAndServiceIdAndOrgId(any(), any(), any());
  }

  @Test
  void deleteCarrierServiceTestException() throws CarrierServiceDomainException {
    when(carrierServiceDomain.findCarrierServiceByCarrierIdAndServiceIdAndOrgId(
            any(), any(), any()))
        .thenReturn(Optional.empty());

    Exception exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                carrierServiceService.deleteCarrierService(
                    TestUtil.CARRIER_ID, TestUtil.SERVICE_ID, TestUtil.ORG_ID));
    Assertions.assertEquals("Carrier service not found with given details", exception.getMessage());

    verify(carrierServiceDomain, times(1))
        .findCarrierServiceByCarrierIdAndServiceIdAndOrgId(any(), any(), any());
  }
}
