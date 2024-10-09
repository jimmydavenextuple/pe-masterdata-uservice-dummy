/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.carrier.persistence.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.nextuple.carrier.persistence.TestUtil;
import com.nextuple.carrier.persistence.domain.CarrierServiceDomainDto;
import com.nextuple.carrier.persistence.entity.CarrierServiceEntity;
import com.nextuple.carrier.persistence.exception.CarrierServiceDomainException;
import com.nextuple.carrier.persistence.mapper.CarrierServiceEntityMapper;
import com.nextuple.carrier.persistence.repository.CarrierServiceRepository;
import com.nextuple.common.pojo.PageProperties;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

class CarrierServicePersistenceServiceImplTest {

  @InjectMocks private CarrierServicePersistenceServiceImpl carrierServicePersistenceService;

  @InjectMocks private TestUtil testUtil;

  @Mock private PageProperties pageProperties;

  @Mock private CarrierServiceRepository carrierServiceRepository;

  @Mock private CarrierServiceEntityMapper carrierServiceEntityMapper;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    // Added this
    ReflectionTestUtils.setField(
        carrierServicePersistenceService, "repository", carrierServiceRepository);
    ReflectionTestUtils.setField(
        carrierServicePersistenceService, "mapper", carrierServiceEntityMapper);
  }

  @Test
  @DisplayName("saveCarrierServiceTest - Happy path")
  void saveCarrierServiceTest() throws CarrierServiceDomainException {
    CarrierServiceEntity carrierServiceEntity = new CarrierServiceEntity();
    CarrierServiceDomainDto carrierServiceDomainDto = new CarrierServiceDomainDto();
    when(carrierServiceRepository.save(any())).thenReturn(carrierServiceEntity);
    when(carrierServiceEntityMapper.toEntity(any(CarrierServiceDomainDto.class)))
        .thenReturn(carrierServiceEntity);
    when(carrierServiceEntityMapper.toDomain(any(CarrierServiceEntity.class)))
        .thenReturn(carrierServiceDomainDto);
    CarrierServiceDomainDto savedCarrierService =
        carrierServicePersistenceService.saveCarrierService(carrierServiceDomainDto);
    assertEquals(carrierServiceDomainDto, savedCarrierService);
    verify(carrierServiceRepository, times(1)).save(any());
  }

  @Test
  @DisplayName("saveCarrierServiceTest - Exception")
  void saveCarrierServiceExceptionTest() {
    CarrierServiceDomainDto carrierServiceDomainDto = new CarrierServiceDomainDto();
    when(carrierServiceRepository.save(any()))
        .thenThrow(new RuntimeException("Error while saving"));
    when(carrierServiceEntityMapper.toEntity(any(CarrierServiceDomainDto.class)))
        .thenReturn(new CarrierServiceEntity());
    Exception exception =
        assertThrows(
            CarrierServiceDomainException.class,
            () -> carrierServicePersistenceService.saveCarrierService(carrierServiceDomainDto));
    assertEquals("Error while saving the carrier service", exception.getMessage());
    verify(carrierServiceRepository, times(1)).save(any());
  }

  @Test
  @DisplayName("findCarrierServiceByCarrierIdAndServiceIdAndOrgIdTest - Happy path")
  void findCarrierServiceByCarrierIdAndServiceIdAndOrgIdTest()
      throws CarrierServiceDomainException {
    CarrierServiceDomainDto carrierServiceDomainDto = testUtil.getCarrierServiceDomainDto();
    CarrierServiceEntity entity = new CarrierServiceEntity();
    when(carrierServiceRepository.findById(any())).thenReturn(Optional.of(entity));
    when(carrierServiceEntityMapper.toDomain(any(CarrierServiceEntity.class)))
        .thenReturn(carrierServiceDomainDto);

    Optional<CarrierServiceDomainDto> foundCarrierService =
        carrierServicePersistenceService.findCarrierServiceByCarrierIdAndServiceIdAndOrgId(
            TestUtil.CARRIER_ID, TestUtil.CARRIER_SERVICE_ID, TestUtil.ORG_ID);

    Assertions.assertTrue(foundCarrierService.isPresent());
    assertEquals(carrierServiceDomainDto, foundCarrierService.get());
    verify(carrierServiceRepository, times(1)).findById(any());
  }

  @Test
  @DisplayName("findCarrierServiceByCarrierIdAndServiceIdAndOrgIdTest - Exception: Id not found")
  void findCarrierServiceByCarrierIdAndServiceIdAndOrgIdNotFoundTest()
      throws CarrierServiceDomainException {
    when(carrierServiceRepository.findCarrierServiceByCarrierIdAndCarrierServiceIdAndOrgId(
            any(), any(), any()))
        .thenReturn(Optional.empty());
    Optional<CarrierServiceDomainDto> foundCarrierService =
        carrierServicePersistenceService.findCarrierServiceByCarrierIdAndServiceIdAndOrgId(
            TestUtil.CARRIER_ID, TestUtil.CARRIER_SERVICE_ID, TestUtil.ORG_ID);
    Assertions.assertFalse(foundCarrierService.isPresent());
    verify(carrierServiceRepository, times(0))
        .findCarrierServiceByCarrierIdAndCarrierServiceIdAndOrgId(any(), any(), any());
  }

  @Test
  @DisplayName("findCarrierServiceByCarrierIdAndServiceIdAndOrgIdTest - Exception")
  void findCarrierServiceByCarrierIdAndServiceIdAndOrgIdExceptionTest() {
    when(carrierServiceRepository.findById(any()))
        .thenThrow(new RuntimeException("Error while finding"));

    Exception exception =
        Assertions.assertThrows(
            CarrierServiceDomainException.class,
            () ->
                carrierServicePersistenceService.findCarrierServiceByCarrierIdAndServiceIdAndOrgId(
                    TestUtil.CARRIER_ID, TestUtil.CARRIER_SERVICE_ID, TestUtil.ORG_ID));

    Assertions.assertEquals("Error while finding carrier service", exception.getMessage());
    verify(carrierServiceRepository, times(1)).findById(any());
  }

  @Test
  @DisplayName("deleteCarrierServiceTest - Happy path")
  void deleteCarrierServiceTest() throws CarrierServiceDomainException {
    CarrierServiceDomainDto carrierServiceDomainDto = new CarrierServiceDomainDto();
    doNothing().when(carrierServiceRepository).delete(any());
    carrierServicePersistenceService.deleteCarrierService(carrierServiceDomainDto);
    verify(carrierServiceRepository, times(1)).delete(any());
  }

  @Test
  @DisplayName("deleteCarrierServiceTest - Exception")
  void deleteCarrierServiceExceptionTest() {
    CarrierServiceDomainDto carrierServiceDomainDto = new CarrierServiceDomainDto();
    when(carrierServiceEntityMapper.toEntity(any(CarrierServiceDomainDto.class)))
        .thenReturn(new CarrierServiceEntity());
    doThrow(new RuntimeException("Error while deleting"))
        .when(carrierServiceRepository)
        .delete(new CarrierServiceEntity());

    CarrierServiceDomainException exception =
        assertThrows(
            CarrierServiceDomainException.class,
            () -> carrierServicePersistenceService.deleteCarrierService(carrierServiceDomainDto));
    assertEquals("Error while deleting carrier service", exception.getMessage());
    verify(carrierServiceRepository, times(1)).delete(any());
  }

  @Test
  @DisplayName("findCarrierServiceListByOrgIdTest - Happy path - ASC order")
  void findCarrierServiceListByOrgIdTest() throws CarrierServiceDomainException {
    List<CarrierServiceEntity> carrierServiceEntities = new ArrayList<>();
    CarrierServiceEntity carrierServiceEntity1 = new CarrierServiceEntity();
    CarrierServiceEntity carrierServiceEntity2 = new CarrierServiceEntity();
    carrierServiceEntities.add(carrierServiceEntity1);
    carrierServiceEntities.add(carrierServiceEntity2);
    when(carrierServiceRepository.findCarrierServicesByOrgId(any(), any(Pageable.class)))
        .thenReturn(new PageImpl<>(carrierServiceEntities));
    List<CarrierServiceDomainDto> expectedDomainDtoList = new ArrayList<>();
    CarrierServiceDomainDto domainDto1 = new CarrierServiceDomainDto();
    CarrierServiceDomainDto domainDto2 = new CarrierServiceDomainDto();
    expectedDomainDtoList.add(domainDto1);
    expectedDomainDtoList.add(domainDto2);
    when(carrierServiceEntityMapper.toDomain(anyList())).thenReturn(expectedDomainDtoList);
    Page<CarrierServiceDomainDto> resultPage =
        carrierServicePersistenceService.findCarrierServiceListByOrgId(
            TestUtil.ORG_ID, 1, 10, "id", "ASC");
    List<CarrierServiceDomainDto> resultDomainDtoList = resultPage.getContent();
    assertEquals(expectedDomainDtoList.size(), resultDomainDtoList.size());
    verify(carrierServiceRepository, times(1))
        .findCarrierServicesByOrgId(eq(TestUtil.ORG_ID), any(Pageable.class));
  }

  @Test
  @DisplayName("findCarrierServiceListByOrgIdTest - Happy path - DESC order")
  void findCarrierServiceListByOrgIdTestDescOrder() throws CarrierServiceDomainException {
    List<CarrierServiceEntity> carrierServiceEntities = new ArrayList<>();
    CarrierServiceEntity carrierServiceEntity1 = new CarrierServiceEntity();
    CarrierServiceEntity carrierServiceEntity2 = new CarrierServiceEntity();
    carrierServiceEntities.add(carrierServiceEntity1);
    carrierServiceEntities.add(carrierServiceEntity2);
    when(carrierServiceRepository.findCarrierServicesByOrgId(any(), any(Pageable.class)))
        .thenReturn(new PageImpl<>(carrierServiceEntities));
    List<CarrierServiceDomainDto> expectedDomainDtoList = new ArrayList<>();
    CarrierServiceDomainDto domainDto1 = new CarrierServiceDomainDto();
    CarrierServiceDomainDto domainDto2 = new CarrierServiceDomainDto();
    expectedDomainDtoList.add(domainDto1);
    expectedDomainDtoList.add(domainDto2);
    when(carrierServiceEntityMapper.toDomain(anyList())).thenReturn(expectedDomainDtoList);
    Page<CarrierServiceDomainDto> resultPage =
        carrierServicePersistenceService.findCarrierServiceListByOrgId(
            TestUtil.ORG_ID, 1, 10, "id", "DESC");
    List<CarrierServiceDomainDto> resultDomainDtoList = resultPage.getContent();
    assertEquals(expectedDomainDtoList.size(), resultDomainDtoList.size());
    verify(carrierServiceRepository, times(1))
        .findCarrierServicesByOrgId(eq(TestUtil.ORG_ID), any(Pageable.class));
  }

  @Test
  @DisplayName("findCarrierServiceListByOrgIdTest - Exception")
  void findCarrierServiceListByOrgIdExceptionTest() {
    when(carrierServiceRepository.findCarrierServicesByOrgId(any(), any(Pageable.class)))
        .thenThrow(new RuntimeException("Error while finding"));
    Exception exception =
        assertThrows(
            CarrierServiceDomainException.class,
            () ->
                carrierServicePersistenceService.findCarrierServiceListByOrgId(
                    TestUtil.ORG_ID, 1, 10, "id", "ASC"));
    assertEquals("Error while finding carrier service list", exception.getMessage());
    verify(carrierServiceRepository, times(1))
        .findCarrierServicesByOrgId(eq(TestUtil.ORG_ID), any(Pageable.class));
  }

  @Test
  @DisplayName("findCarrierServiceListByOrgIdWithoutPaginationTest - Happy path")
  void findCarrierServiceListByOrgIdWithoutPaginationTest() throws CarrierServiceDomainException {
    List<CarrierServiceEntity> carrierServiceEntities = new ArrayList<>();
    CarrierServiceEntity carrierServiceEntity1 = new CarrierServiceEntity();
    CarrierServiceEntity carrierServiceEntity2 = new CarrierServiceEntity();
    carrierServiceEntities.add(carrierServiceEntity1);
    carrierServiceEntities.add(carrierServiceEntity2);

    when(carrierServiceRepository.findCarrierServicesByOrgId(any()))
        .thenReturn(carrierServiceEntities);
    List<CarrierServiceDomainDto> expectedDomainDtoList = new ArrayList<>();
    CarrierServiceDomainDto domainDto1 = new CarrierServiceDomainDto();
    CarrierServiceDomainDto domainDto2 = new CarrierServiceDomainDto();
    expectedDomainDtoList.add(domainDto1);
    expectedDomainDtoList.add(domainDto2);
    when(carrierServiceEntityMapper.toDomain(anyList())).thenReturn(expectedDomainDtoList);
    List<CarrierServiceDomainDto> resultDomainDtoList =
        carrierServicePersistenceService.findCarrierServiceListByOrgIdWithoutPagination(
            TestUtil.ORG_ID);

    assertEquals(expectedDomainDtoList.size(), resultDomainDtoList.size());

    verify(carrierServiceRepository, times(1)).findCarrierServicesByOrgId(TestUtil.ORG_ID);
  }

  @Test
  @DisplayName("findCarrierServiceListByOrgIdWithoutPaginationTest - Exception")
  void findCarrierServiceListByOrgIdWithoutPaginationExceptionTest() {
    when(carrierServiceRepository.findCarrierServicesByOrgId(any()))
        .thenThrow(new RuntimeException("Error while finding"));
    Exception exception =
        assertThrows(
            CarrierServiceDomainException.class,
            () ->
                carrierServicePersistenceService.findCarrierServiceListByOrgIdWithoutPagination(
                    TestUtil.ORG_ID));
    assertEquals("Error while finding carrier service list", exception.getMessage());
    verify(carrierServiceRepository, times(1)).findCarrierServicesByOrgId(TestUtil.ORG_ID);
  }

  @Test
  @DisplayName("findCarrierServiceByServiceIdAndOrgIdTest - Happy path")
  void findCarrierServiceByServiceIdAndOrgIdTest() throws CarrierServiceDomainException {
    CarrierServiceEntity carrierServiceEntity = new CarrierServiceEntity();
    List<CarrierServiceEntity> carrierServiceEntities = new ArrayList<>();
    carrierServiceEntities.add(carrierServiceEntity);
    when(carrierServiceRepository.findCarrierServiceByCarrierServiceIdAndOrgId(any(), any()))
        .thenReturn(Optional.of(carrierServiceEntities));
    CarrierServiceDomainDto expectedDomainDto = new CarrierServiceDomainDto();
    when(carrierServiceEntityMapper.toDomain(any(CarrierServiceEntity.class)))
        .thenReturn(expectedDomainDto);
    Optional<List<CarrierServiceDomainDto>> resultDomainDtoOptional =
        carrierServicePersistenceService.findCarrierServiceByServiceIdAndOrgId(
            TestUtil.CARRIER_SERVICE_ID, TestUtil.ORG_ID);

    Assertions.assertTrue(resultDomainDtoOptional.isPresent());
    verify(carrierServiceRepository, times(1))
        .findCarrierServiceByCarrierServiceIdAndOrgId(TestUtil.CARRIER_SERVICE_ID, TestUtil.ORG_ID);
  }

  @Test
  @DisplayName("findCarrierServiceByServiceIdAndOrgIdTest - Exception : Not found")
  void findCarrierServiceByServiceIdAndOrgIdNotFoundTest() throws CarrierServiceDomainException {
    when(carrierServiceRepository.findCarrierServiceByCarrierServiceIdAndOrgId(any(), any()))
        .thenReturn(Optional.empty());
    Optional<List<CarrierServiceDomainDto>> resultDomainDtoOptional =
        carrierServicePersistenceService.findCarrierServiceByServiceIdAndOrgId(
            TestUtil.CARRIER_SERVICE_ID, TestUtil.ORG_ID);
    Assertions.assertFalse(resultDomainDtoOptional.isPresent());
    verify(carrierServiceRepository, times(1))
        .findCarrierServiceByCarrierServiceIdAndOrgId(TestUtil.CARRIER_SERVICE_ID, TestUtil.ORG_ID);
  }

  @Test
  @DisplayName("findCarrierServiceByServiceIdAndOrgIdExceptionTest - Exception")
  void findCarrierServiceByServiceIdAndOrgIdExceptionTest() {
    when(carrierServiceRepository.findCarrierServiceByCarrierServiceIdAndOrgId(any(), any()))
        .thenThrow(new RuntimeException("Error while finding"));
    Exception exception =
        assertThrows(
            CarrierServiceDomainException.class,
            () ->
                carrierServicePersistenceService.findCarrierServiceByServiceIdAndOrgId(
                    TestUtil.CARRIER_SERVICE_ID, TestUtil.ORG_ID));
    assertEquals("Error while finding carrier service", exception.getMessage());
    verify(carrierServiceRepository, times(1))
        .findCarrierServiceByCarrierServiceIdAndOrgId(TestUtil.CARRIER_SERVICE_ID, TestUtil.ORG_ID);
  }

  @Test
  @DisplayName("getAllCarrierServiceEntitiesTest - Happy path")
  void getAllCarrierServiceEntitiesTest() throws CarrierServiceDomainException {
    List<CarrierServiceEntity> carrierServiceEntities = new ArrayList<>();
    carrierServiceEntities.add(new CarrierServiceEntity());
    carrierServiceEntities.add(new CarrierServiceEntity());
    when(carrierServiceRepository.findAllCarriersByLimit(any())).thenReturn(carrierServiceEntities);

    List<CarrierServiceDomainDto> expectedDomainDtoList = new ArrayList<>();
    expectedDomainDtoList.add(new CarrierServiceDomainDto());
    expectedDomainDtoList.add(new CarrierServiceDomainDto());
    when(carrierServiceEntityMapper.toDomain(anyList())).thenReturn(expectedDomainDtoList);

    List<CarrierServiceDomainDto> resultDomainDtoList =
        carrierServicePersistenceService.getAllCarrierServiceEntities(10);

    assertEquals(expectedDomainDtoList.size(), resultDomainDtoList.size());

    verify(carrierServiceRepository, times(1)).findAllCarriersByLimit(10);
  }

  @Test
  @DisplayName("getAllCarrierServiceEntitiesTest - Exception")
  void getAllCarrierServiceEntitiesExceptionTest() {
    when(carrierServiceRepository.findAllCarriersByLimit(any()))
        .thenThrow(new RuntimeException("Error while fetching"));
    Exception exception =
        assertThrows(
            CarrierServiceDomainException.class,
            () -> carrierServicePersistenceService.getAllCarrierServiceEntities(10));
    assertEquals("Error while fetching all carrier services", exception.getMessage());
    verify(carrierServiceRepository, times(1)).findAllCarriersByLimit(10);
  }
}
