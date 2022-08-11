package com.hbc.carrier.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.hbc.carrier.TestUtil;
import com.hbc.carrier.domain.CarrierServiceDomain;
import com.hbc.carrier.domain.entity.CarrierServiceEntity;
import com.hbc.carrier.domain.inbound.CarrierServiceRequest;
import com.hbc.carrier.domain.inbound.CarrierServiceUpdateRequest;
import com.hbc.carrier.domain.mapper.CarrierServiceMapper;
import com.hbc.carrier.domain.outbound.CarrierServiceResponse;
import com.hbc.carrier.exception.CarrierServiceDomainException;
import com.hbc.common.exception.CommonServiceException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;

class CarrierServiceServiceTest {

  @InjectMocks private CarrierServiceService carrierServiceService;

  @InjectMocks private TestUtil testUtil;

  @Mock private CarrierServiceDomain carrierServiceDomain;
  @Mock private CarrierServiceMapper carrierServiceMapper;

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
    assertEquals(carrierServiceRequest.getCarrierId(), dto.getCarrierId());
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
            TestUtil.CARRIER_ID,
            TestUtil.CARRIER_SERVICE_ID,
            TestUtil.ORG_ID,
            carrierServiceUpdateRequest);
    assertEquals(testUtil.getCarrierServiceUpdateResponse(), CarrierServiceResponse);

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
                    TestUtil.CARRIER_SERVICE_ID,
                    TestUtil.ORG_ID,
                    carrierServiceUpdateRequest));
    assertEquals("Carrier service not found with given details", exception.getMessage());

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
            TestUtil.CARRIER_ID, TestUtil.CARRIER_SERVICE_ID, TestUtil.ORG_ID);
    assertEquals(testUtil.getCarrierServiceUpdateResponse(), CarrierServiceResponse);
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
                    TestUtil.CARRIER_ID, TestUtil.CARRIER_SERVICE_ID, TestUtil.ORG_ID));
    assertEquals("Carrier service not found with given details", exception.getMessage());

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
            TestUtil.CARRIER_ID, TestUtil.CARRIER_SERVICE_ID, TestUtil.ORG_ID);
    assertEquals(testUtil.getCarrierServiceResponse(), CarrierServiceResponse);
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
                    TestUtil.CARRIER_ID, TestUtil.CARRIER_SERVICE_ID, TestUtil.ORG_ID));
    assertEquals("Carrier service not found with given details", exception.getMessage());

    verify(carrierServiceDomain, times(1))
        .findCarrierServiceByCarrierIdAndServiceIdAndOrgId(any(), any(), any());
  }

  @Test
  void getCarrierServiceListTest() throws CarrierServiceDomainException {
    List<CarrierServiceResponse> carrierServiceResponseList =
        testUtil.getCarrierServiceResponseList();

    when(carrierServiceDomain.findCarrierServiceListByOrgId(any(), any(), any(), any(), any()))
        .thenReturn(
            testUtil.createPageCarrierServiceResponse(
                2, carrierServiceResponseList, carrierServiceResponseList.size()));

    Page<CarrierServiceResponse> carrierServiceResponsePage =
        carrierServiceService.getCarrierServiceList(
            TestUtil.ORG_ID, 1, 1, TestUtil.SORT_BY, Optional.of(TestUtil.SORT_ORDER_DESC));

    assertEquals(2, (int) carrierServiceResponsePage.getTotalElements());
    assertEquals(2, carrierServiceResponsePage.getTotalPages());
    assertEquals(carrierServiceResponseList.size(), carrierServiceResponsePage.getContent().size());
    verify(carrierServiceDomain, times(1))
        .findCarrierServiceListByOrgId(any(), any(), any(), any(), any());
  }
}
