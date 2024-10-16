/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.carrier.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.nextuple.carrier.TestUtil;
import com.nextuple.carrier.config.CarrierTenantBasedDBConfig;
import com.nextuple.carrier.domain.dto.CarrierCacheKeyDto;
import com.nextuple.carrier.domain.inbound.CarrierServiceRequest;
import com.nextuple.carrier.domain.inbound.CarrierServiceUpdateRequest;
import com.nextuple.carrier.domain.outbound.CarrierServiceResponse;
import com.nextuple.carrier.persistence.domain.CarrierServiceDomainDto;
import com.nextuple.carrier.persistence.exception.CarrierServiceDomainException;
import com.nextuple.carrier.persistence.service.impl.CarrierServicePersistenceServiceImpl;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.node.carrier.domain.feign.impl.NodeCarrierV2Feign;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

class CarrierServiceServiceTest {

  @InjectMocks private CarrierServiceService carrierServiceService;

  @Mock private NodeCarrierV2Feign nodeCarrierFeign;
  @InjectMocks private TestUtil testUtil;
  @Mock private CarrierServicePersistenceServiceImpl carrierServicePersistenceService;

  @Mock private CarrierTenantBasedDBConfig carrierOptionConfig;

  @BeforeEach
  void setUp() throws CommonServiceException {
    MockitoAnnotations.openMocks(this);
    Set<String> serviceOptions = Set.of("SDND", "STANDARD", "EXPRESS", "NEXTDAY");
    when(carrierOptionConfig.getServiceOptions(any())).thenReturn(serviceOptions);
    ReflectionTestUtils.setField(carrierServiceService, "nodeCarrierFeign", nodeCarrierFeign);
  }

  @Test
  @DisplayName("Create Carrier Service Test - Happy path")
  void createCarrierServiceTest() throws CarrierServiceDomainException, CommonServiceException {
    CarrierServiceDomainDto carrierServiceEntity = testUtil.getCarrierServiceDomainDto();
    CarrierServiceRequest carrierServiceRequest = testUtil.getCarrierServiceRequest();
    Set<String> serviceOptions =
        Set.of("SDND", "STANDARD", "EXPRESS", "NEXTDAY", TestUtil.SERVICE_OPTIONS);
    when(carrierOptionConfig.getServiceOptions(any())).thenReturn(serviceOptions);
    when(carrierServicePersistenceService.findCarrierServiceByServiceIdAndOrgId(any(), any()))
        .thenReturn(Optional.of(List.of()));
    when(carrierServicePersistenceService.saveCarrierService(any(CarrierServiceDomainDto.class)))
        .thenReturn(carrierServiceEntity);
    CarrierServiceResponse dto =
        carrierServiceService.createCarrierService(testUtil.getCarrierServiceRequest());
    assertEquals(carrierServiceRequest.getCarrierId(), dto.getCarrierId());

    verify(carrierServicePersistenceService, times(1))
        .saveCarrierService(any(CarrierServiceDomainDto.class));
  }

  @Test
  @DisplayName("Create Carrier Service Test - Happy path with empty optional domain call")
  void createCarrierServiceTestWithEmptyOptional()
      throws CarrierServiceDomainException, CommonServiceException {
    CarrierServiceDomainDto carrierServiceEntity = testUtil.getCarrierServiceDomainDto();
    CarrierServiceRequest carrierServiceRequest = testUtil.getCarrierServiceRequest();
    Set<String> serviceOptions =
        Set.of("SDND", "STANDARD", "EXPRESS", "NEXTDAY", TestUtil.SERVICE_OPTIONS);
    when(carrierOptionConfig.getServiceOptions(any())).thenReturn(serviceOptions);
    when(carrierServicePersistenceService.findCarrierServiceByServiceIdAndOrgId(any(), any()))
        .thenReturn(Optional.empty());
    when(carrierServicePersistenceService.saveCarrierService(any(CarrierServiceDomainDto.class)))
        .thenReturn(carrierServiceEntity);
    CarrierServiceResponse dto =
        carrierServiceService.createCarrierService(testUtil.getCarrierServiceRequest());
    assertEquals(carrierServiceRequest.getCarrierId(), dto.getCarrierId());

    verify(carrierServicePersistenceService, times(1))
        .saveCarrierService(any(CarrierServiceDomainDto.class));
  }

  @Test
  @DisplayName("Create Carrier Service Test - Exception : Carrier service id is already exists")
  void createCarrierServiceTestServiceIdExists()
      throws CarrierServiceDomainException, CommonServiceException {
    CarrierServiceDomainDto carrierServiceEntity = testUtil.getCarrierServiceDomainDto();
    CarrierServiceRequest carrierServiceRequest = testUtil.getCarrierServiceRequest();
    Set<String> serviceOptions =
        Set.of("SDND", "STANDARD", "EXPRESS", "NEXTDAY", TestUtil.SERVICE_OPTIONS);
    when(carrierOptionConfig.getServiceOptions(any())).thenReturn(serviceOptions);
    when(carrierServicePersistenceService.findCarrierServiceByServiceIdAndOrgId(any(), any()))
        .thenReturn(Optional.of(List.of(carrierServiceEntity)));

    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () -> carrierServiceService.createCarrierService(testUtil.getCarrierServiceRequest()));

    assertEquals(
        "Carrier Service Id already exists for given OrgId and CarrierServiceId",
        exception.getMessage());
    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
  }

  @Test
  @DisplayName("Create Carrier Service Test - Exception : Invalid service option")
  void createCarrierServiceTestCommonServiceException() {
    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () -> carrierServiceService.createCarrierService(testUtil.getCarrierServiceRequest()));

    assertEquals("Invalid service option", exception.getMessage());
    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
  }

  @Test
  @DisplayName("Get Carrier Service Test - Happy path")
  void getCarrierServiceDetailsTest() throws CarrierServiceDomainException, CommonServiceException {
    CarrierServiceDomainDto carrierServiceEntity = testUtil.getCarrierServiceDomainDto();
    when(carrierServicePersistenceService.findCarrierServiceByCarrierIdAndServiceIdAndOrgId(
            any(), any(), any()))
        .thenReturn(Optional.of(carrierServiceEntity));

    CarrierServiceResponse CarrierServiceResponse =
        carrierServiceService.getCarrierServiceDetails(
            TestUtil.CARRIER_ID, TestUtil.CARRIER_SERVICE_ID, TestUtil.ORG_ID);
    assertEquals(testUtil.getCarrierServiceUpdateResponse(), CarrierServiceResponse);
    verify(carrierServicePersistenceService, times(1))
        .findCarrierServiceByCarrierIdAndServiceIdAndOrgId(any(), any(), any());
  }

  @Test
  @DisplayName("Get Carrier Service Test - Exception")
  void getCarrierServiceDetailsTestException() throws CarrierServiceDomainException {
    when(carrierServicePersistenceService.findCarrierServiceByCarrierIdAndServiceIdAndOrgId(
            any(), any(), any()))
        .thenReturn(Optional.empty());
    Exception exception =
        assertThrows(
            CommonServiceException.class,
            () ->
                carrierServiceService.getCarrierServiceDetails(
                    TestUtil.CARRIER_ID, TestUtil.CARRIER_SERVICE_ID, TestUtil.ORG_ID));
    assertEquals("Carrier service not found with given details", exception.getMessage());

    verify(carrierServicePersistenceService, times(1))
        .findCarrierServiceByCarrierIdAndServiceIdAndOrgId(any(), any(), any());
  }

  @Test
  @DisplayName("Delete Carrier Service Test - Happy path")
  void deleteCarrierServiceTest() throws CarrierServiceDomainException, CommonServiceException {
    CarrierServiceDomainDto carrierServiceEntity = testUtil.getCarrierServiceDomainDto();
    when(nodeCarrierFeign.getAllNodeCarriersByOrgIdCarrierServiceId(any(), any()))
        .thenReturn(testUtil.getEmptyResponseOfNodeCarrier());
    when(carrierServicePersistenceService.findCarrierServiceByCarrierIdAndServiceIdAndOrgId(
            any(), any(), any()))
        .thenReturn(Optional.of(carrierServiceEntity));
    CarrierServiceResponse CarrierServiceResponse =
        carrierServiceService.deleteCarrierService(
            TestUtil.CARRIER_ID, TestUtil.CARRIER_SERVICE_ID, TestUtil.ORG_ID);
    assertEquals(testUtil.getCarrierServiceResponse(), CarrierServiceResponse);
    verify(carrierServicePersistenceService, times(1))
        .findCarrierServiceByCarrierIdAndServiceIdAndOrgId(any(), any(), any());
  }

  @Test
  @DisplayName("Delete Carrier Service Test - Exception")
  void deleteCarrierServiceWithNodeAssociationTestException() throws CarrierServiceDomainException {
    CarrierServiceDomainDto carrierServiceEntity = new CarrierServiceDomainDto();
    when(nodeCarrierFeign.getAllNodeCarriersByOrgIdCarrierServiceId(any(), any()))
        .thenReturn(testUtil.getBaseResponseOfNodeCarrier());
    when(carrierServicePersistenceService.findCarrierServiceByCarrierIdAndServiceIdAndOrgId(
            any(), any(), any()))
        .thenReturn(Optional.of(carrierServiceEntity));

    Exception exception =
        assertThrows(
            CommonServiceException.class,
            () ->
                carrierServiceService.deleteCarrierService(
                    TestUtil.CARRIER_ID, TestUtil.CARRIER_SERVICE_ID, TestUtil.ORG_ID));
    assertEquals(
        "Carrier cannot be deleted. Please delete the associated node-carrier and last Pickup time details before deleting this carrier",
        exception.getMessage());
    verify(carrierServicePersistenceService, times(1))
        .findCarrierServiceByCarrierIdAndServiceIdAndOrgId(any(), any(), any());
  }

  @Test
  @DisplayName("Delete Carrier Service Test - Exception : Carrier service not found")
  void deleteCarrierServiceTestException() throws CarrierServiceDomainException {
    when(carrierServicePersistenceService.findCarrierServiceByCarrierIdAndServiceIdAndOrgId(
            any(), any(), any()))
        .thenReturn(Optional.empty());
    Exception exception =
        assertThrows(
            CommonServiceException.class,
            () ->
                carrierServiceService.deleteCarrierService(
                    TestUtil.CARRIER_ID, TestUtil.CARRIER_SERVICE_ID, TestUtil.ORG_ID));
    assertEquals("Carrier service not found with given details", exception.getMessage());

    verify(carrierServicePersistenceService, times(1))
        .findCarrierServiceByCarrierIdAndServiceIdAndOrgId(any(), any(), any());
  }

  @Test
  @DisplayName("Update Carrier Service Test - Happy path")
  void updateCarrierServiceDetailsTest()
      throws CarrierServiceDomainException, CommonServiceException {
    CarrierServiceDomainDto carrierServiceEntity = testUtil.getCarrierServiceDomainDto();
    CarrierServiceUpdateRequest carrierServiceUpdateRequest =
        testUtil.getCarrierServiceUpdateRequest();
    CarrierServiceDomainDto updateCarrierServiceEntity = testUtil.getCarrierServiceDomainDto();
    Set<String> serviceOptions =
        Set.of("SDND", "STANDARD", "EXPRESS", "NEXTDAY", TestUtil.SERVICE_OPTIONS);
    when(carrierOptionConfig.getServiceOptions(any())).thenReturn(serviceOptions);
    when(carrierServicePersistenceService.findCarrierServiceByCarrierIdAndServiceIdAndOrgId(
            any(), any(), any()))
        .thenReturn(Optional.of(carrierServiceEntity));
    when(carrierServicePersistenceService.saveCarrierService(any()))
        .thenReturn(updateCarrierServiceEntity);

    CarrierServiceResponse CarrierServiceResponse =
        carrierServiceService.updateCarrierServiceDetails(
            TestUtil.CARRIER_ID,
            TestUtil.CARRIER_SERVICE_ID,
            TestUtil.ORG_ID,
            carrierServiceUpdateRequest);
    assertEquals(testUtil.getCarrierServiceUpdateResponse(), CarrierServiceResponse);

    verify(carrierServicePersistenceService, times(1))
        .saveCarrierService(any(CarrierServiceDomainDto.class));
    verify(carrierServicePersistenceService, times(1))
        .findCarrierServiceByCarrierIdAndServiceIdAndOrgId(any(), any(), any());
  }

  @Test
  @DisplayName(
      "Update Carrier Service Test - Exception : Carrier service not found with given details")
  void updateCarrierServiceDetailsTestNotFoundException()
      throws CarrierServiceDomainException, CommonServiceException {
    CarrierServiceUpdateRequest carrierServiceUpdateRequest =
        testUtil.getCarrierServiceUpdateRequest();
    Set<String> serviceOptions =
        Set.of("SDND", "STANDARD", "EXPRESS", "NEXTDAY", TestUtil.SERVICE_OPTIONS);
    when(carrierOptionConfig.getServiceOptions(any())).thenReturn(serviceOptions);
    when(carrierServicePersistenceService.findCarrierServiceByCarrierIdAndServiceIdAndOrgId(
            any(), any(), any()))
        .thenReturn(Optional.empty());

    Exception exception =
        assertThrows(
            CommonServiceException.class,
            () ->
                carrierServiceService.updateCarrierServiceDetails(
                    TestUtil.CARRIER_ID,
                    TestUtil.CARRIER_SERVICE_ID,
                    TestUtil.ORG_ID,
                    carrierServiceUpdateRequest));
    assertEquals("Carrier service not found with given details", exception.getMessage());

    verify(carrierServicePersistenceService, times(0))
        .saveCarrierService(any(CarrierServiceDomainDto.class));
    verify(carrierServicePersistenceService, times(1))
        .findCarrierServiceByCarrierIdAndServiceIdAndOrgId(any(), any(), any());
  }

  @Test
  @DisplayName("Get Carrier Service List Test - Happy path - ASC order")
  void getCarrierServiceListTest() throws CarrierServiceDomainException, CommonServiceException {
    List<CarrierServiceDomainDto> carrierServiceResponseList =
        testUtil.getCarrierServiceDomainDtoList();
    when(carrierServicePersistenceService.findCarrierServiceListByOrgId(
            any(), any(), any(), any(), any()))
        .thenReturn(
            testUtil.createPageCarrierServiceDomainDto(
                1, carrierServiceResponseList, carrierServiceResponseList.size()));

    Page<CarrierServiceResponse> carrierServiceResponsePage =
        carrierServiceService.getCarrierServiceList(
            TestUtil.ORG_ID, 1, 1, TestUtil.SORT_BY, TestUtil.SORT_ORDER_ASC);

    assertEquals(1, (int) carrierServiceResponsePage.getTotalElements());
    assertEquals(1, carrierServiceResponsePage.getTotalPages());
    assertEquals(carrierServiceResponseList.size(), carrierServiceResponsePage.getContent().size());
    verify(carrierServicePersistenceService, times(1))
        .findCarrierServiceListByOrgId(any(), any(), any(), any(), any());
  }

  @Test
  @DisplayName("Get Carrier Service List Test - Happy path - DESC order")
  void getCarrierServiceListDefaultSortOrderTest()
      throws CarrierServiceDomainException, CommonServiceException {
    List<CarrierServiceDomainDto> carrierServiceResponseList =
        testUtil.getCarrierServiceDomainDtoList();

    when(carrierServicePersistenceService.findCarrierServiceListByOrgId(
            any(), any(), any(), any(), any()))
        .thenReturn(
            testUtil.createPageCarrierServiceDomainDto(
                1, carrierServiceResponseList, carrierServiceResponseList.size()));

    Page<CarrierServiceResponse> carrierServiceResponsePage =
        carrierServiceService.getCarrierServiceList(
            TestUtil.ORG_ID, 1, 1, TestUtil.SORT_BY, TestUtil.SORT_ORDER_DESC);

    assertEquals(1, (int) carrierServiceResponsePage.getTotalElements());
    assertEquals(1, carrierServiceResponsePage.getTotalPages());
    assertEquals(carrierServiceResponseList.size(), carrierServiceResponsePage.getContent().size());
    verify(carrierServicePersistenceService, times(1))
        .findCarrierServiceListByOrgId(any(), any(), any(), any(), any());
  }

  @Test
  @DisplayName("Get Carrier Service List Test - Exception - invalid order")
  void getCarrierServiceListTestException() throws CarrierServiceDomainException {
    Exception exception =
        assertThrows(
            CommonServiceException.class,
            () ->
                carrierServiceService.getCarrierServiceList(
                    TestUtil.ORG_ID, 1, 1, TestUtil.SORT_BY, "invalid sort order"));

    assertEquals("Invalid sort order, consider giving either ASC or DESC", exception.getMessage());
    verify(carrierServicePersistenceService, times(0))
        .findCarrierServiceListByOrgId(any(), any(), any(), any(), any());
  }

  @Test
  @DisplayName("Get All Carrier Cache Keys Test - Happy path")
  void getAllCarrierCacheKeysTest() throws CarrierServiceDomainException {
    List<CarrierServiceDomainDto> carrierServiceEntities = testUtil.getCarrierServiceEntityList();

    when(carrierServicePersistenceService.getAllCarrierServiceEntities(any()))
        .thenReturn(carrierServiceEntities);

    List<CarrierCacheKeyDto> response = carrierServiceService.getAllCarrierCacheKeys(2);

    assertEquals(2, response.size());
    assertEquals(carrierServiceEntities.get(0).getCarrierId(), response.get(0).getCarrierId());
    verify(carrierServicePersistenceService, Mockito.times(1)).getAllCarrierServiceEntities(any());
  }

  @Test
  @DisplayName("Get Carrier Service Details By Service Id And OrgId Test - Happy path")
  void getCarrierServiceDetailsByServiceIdAndOrgIdTest()
      throws CarrierServiceDomainException, CommonServiceException {
    CarrierServiceDomainDto carrierServiceEntity = testUtil.getCarrierServiceDomainDto();
    when(carrierServicePersistenceService.findCarrierServiceByServiceIdAndOrgId(any(), any()))
        .thenReturn(Optional.of(List.of(carrierServiceEntity)));
    List<CarrierServiceResponse> CarrierServiceResponse =
        carrierServiceService.getCarrierServiceDetailsByCarrierIdAndOrgId(
            TestUtil.CARRIER_SERVICE_ID, TestUtil.ORG_ID);
    assertEquals(List.of(testUtil.getCarrierServiceUpdateResponse()), CarrierServiceResponse);
    verify(carrierServicePersistenceService, times(1))
        .findCarrierServiceByServiceIdAndOrgId(any(), any());
  }

  @Test
  @DisplayName(
      "Get Carrier Service Details By Service Id And OrgId Test - Exception : Carrier service not found")
  void getCarrierServiceDetailsByServiceIdAndOrgIdTestException()
      throws CarrierServiceDomainException {
    when(carrierServicePersistenceService.findCarrierServiceByServiceIdAndOrgId(any(), any()))
        .thenReturn(Optional.empty());

    Exception exception =
        assertThrows(
            CommonServiceException.class,
            () ->
                carrierServiceService.getCarrierServiceDetailsByCarrierIdAndOrgId(
                    TestUtil.CARRIER_SERVICE_ID, TestUtil.ORG_ID));
    assertEquals("Carrier service not found with given details", exception.getMessage());

    verify(carrierServicePersistenceService, times(1))
        .findCarrierServiceByServiceIdAndOrgId(any(), any());
  }

  @Test
  @DisplayName("Get Carrier Service List By OrgId Test - Happy path")
  void getCarrierServiceListByOrgIdTest() throws CarrierServiceDomainException {
    List<CarrierServiceDomainDto> domainDtoList = testUtil.getCarrierServiceDomainDtoList();
    when(carrierServicePersistenceService.findCarrierServiceListByOrgIdWithoutPagination(
            TestUtil.ORG_ID))
        .thenReturn(domainDtoList);
    List<CarrierServiceResponse> expectedResponseList =
        domainDtoList.stream()
            .map(
                dto ->
                    new CarrierServiceResponse(
                        dto.getOrgId(),
                        dto.getCarrierId(),
                        dto.getCarrierServiceId(),
                        dto.getCarrierName(),
                        dto.getServiceName(),
                        dto.getServiceOptions()))
            .collect(Collectors.toList());

    List<CarrierServiceResponse> actualResponseList =
        carrierServiceService.getCarrierServiceListByOrgId(TestUtil.ORG_ID);

    assertEquals(expectedResponseList, actualResponseList);
    verify(carrierServicePersistenceService, times(1))
        .findCarrierServiceListByOrgIdWithoutPagination(TestUtil.ORG_ID);
  }

  @Test
  @DisplayName("Get Carrier Service List By OrgId Test - Exception")
  void getCarrierServiceListByOrgIdExceptionTest() throws CarrierServiceDomainException {
    when(carrierServicePersistenceService.findCarrierServiceListByOrgIdWithoutPagination(
            TestUtil.ORG_ID))
        .thenThrow(new RuntimeException("Error occurred"));
    Exception e =
        assertThrows(
            RuntimeException.class,
            () -> {
              carrierServiceService.getCarrierServiceListByOrgId(TestUtil.ORG_ID);
            });

    assertEquals("Error occurred", e.getMessage());
    verify(carrierServicePersistenceService, times(1))
        .findCarrierServiceListByOrgIdWithoutPagination(any());
  }

  @Test
  @DisplayName("When invalid service option is passed in the update request")
  void updateCarrierServiceTestForInvalidServiceOption() {
    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () ->
                carrierServiceService.updateCarrierServiceDetails(
                    TestUtil.CARRIER_ID,
                    TestUtil.CARRIER_SERVICE_ID,
                    TestUtil.ORG_ID,
                    testUtil.getCarrierServiceUpdateRequest()));

    assertEquals("Invalid service option", exception.getMessage());
    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
  }
}
