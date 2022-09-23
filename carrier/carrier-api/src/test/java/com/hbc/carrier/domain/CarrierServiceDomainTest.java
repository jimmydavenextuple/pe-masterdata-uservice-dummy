package com.hbc.carrier.domain;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hbc.carrier.TestUtil;
import com.hbc.carrier.domain.entity.CarrierServiceEntity;
import com.hbc.carrier.domain.outbound.CarrierServiceResponse;
import com.hbc.carrier.exception.CarrierServiceDomainException;
import com.hbc.carrier.repository.CarrierServiceRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

class CarrierServiceDomainTest {

  @InjectMocks private CarrierServiceDomain carrierServiceDomain;
  @InjectMocks private TestUtil testUtil;

  @Mock private CarrierServiceRepository carrierServiceRepository;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void saveCarrierServiceTest() throws CarrierServiceDomainException {
    CarrierServiceEntity carrierServiceEntity = testUtil.getCarrierServiceEntity();
    when(carrierServiceRepository.save(any())).thenReturn(carrierServiceEntity);

    CarrierServiceEntity carrierService =
        carrierServiceDomain.saveCarrierServiceEntity(carrierServiceEntity);
    Assertions.assertEquals(carrierServiceEntity, carrierService);

    verify(carrierServiceRepository, times(1)).save(any());
  }

  @Test
  void saveCarrierServiiceExceptionTest() {
    CarrierServiceEntity carrierServiceEntity = testUtil.getCarrierServiceEntity();
    when(carrierServiceRepository.save(any()))
        .thenThrow(new RuntimeException("Error while saving"));

    Exception exception =
        assertThrows(
            CarrierServiceDomainException.class,
            () -> carrierServiceDomain.saveCarrierServiceEntity(carrierServiceEntity));
    Assertions.assertEquals("Error while saving the carrier service", exception.getMessage());
    verify(carrierServiceRepository, times(1)).save(any());
  }

  @Test
  void getCarrierServiceDetailsTest() throws CarrierServiceDomainException {
    CarrierServiceEntity carrierServiceEntity = testUtil.getCarrierServiceEntity();
    when(carrierServiceRepository.findCarrierServiceByCarrierIdAndCarrierServiceIdAndOrgId(
            any(), any(), any()))
        .thenReturn(Optional.of(carrierServiceEntity));

    Optional<CarrierServiceEntity> carrierServiceEntity1 =
        carrierServiceDomain.findCarrierServiceByCarrierIdAndServiceIdAndOrgId(
            TestUtil.CARRIER_ID, TestUtil.CARRIER_SERVICE_ID, TestUtil.ORG_ID);
    Assertions.assertEquals(carrierServiceEntity, carrierServiceEntity1.get());

    verify(carrierServiceRepository, times(1))
        .findCarrierServiceByCarrierIdAndCarrierServiceIdAndOrgId(any(), any(), any());
  }

  @Test
  void getCarrierServiceDetailsTestException() {
    when(carrierServiceRepository.findCarrierServiceByCarrierIdAndCarrierServiceIdAndOrgId(
            any(), any(), any()))
        .thenThrow(new RuntimeException("Error while fetching details"));

    Exception exception =
        assertThrows(
            CarrierServiceDomainException.class,
            () ->
                carrierServiceDomain.findCarrierServiceByCarrierIdAndServiceIdAndOrgId(
                    TestUtil.CARRIER_ID, TestUtil.CARRIER_SERVICE_ID, TestUtil.ORG_ID));
    Assertions.assertEquals("Error while finding carrier service", exception.getMessage());
    verify(carrierServiceRepository, times(1))
        .findCarrierServiceByCarrierIdAndCarrierServiceIdAndOrgId(any(), any(), any());
  }

  @Test
  void carrierServiceDeletionTest() throws CarrierServiceDomainException {
    CarrierServiceEntity carrierServiceEntity = testUtil.getCarrierServiceEntity();
    doNothing().when(carrierServiceRepository).delete(any());
    carrierServiceDomain.deleteCarrierService(carrierServiceEntity);

    verify(carrierServiceRepository, times(1)).delete(any());
  }

  @Test
  void carrierServiceDeletionTestException() {
    doThrow(new RuntimeException("error while deleting"))
        .when(carrierServiceRepository)
        .delete(any());

    Exception exception =
        assertThrows(
            CarrierServiceDomainException.class,
            () -> carrierServiceDomain.deleteCarrierService(testUtil.getCarrierServiceEntity()));
    Assertions.assertEquals("Error while deleting carrier service", exception.getMessage());
    verify(carrierServiceRepository, times(1)).delete(any());
  }

  @Test
  void findCarrierServiceListByOrgIdDefaultTest() throws CarrierServiceDomainException {
    List<CarrierServiceEntity> carrierServiceEntityList = testUtil.getCarrierServiceEntityList();
    Pageable pageable = PageRequest.of(1, 1);
    Page<CarrierServiceEntity> carrierServiceEntityPage =
        new PageImpl<>(carrierServiceEntityList, pageable, carrierServiceEntityList.size());

    when(carrierServiceRepository.findCarrierServicesByOrgId(anyString(), any(Pageable.class)))
        .thenReturn(carrierServiceEntityPage);

    Page<CarrierServiceResponse> response =
        carrierServiceDomain.findCarrierServiceListByOrgId(
            TestUtil.ORG_ID, 1, 1, TestUtil.SORT_BY, TestUtil.SORT_ORDER_ASC);

    Assertions.assertEquals(carrierServiceEntityList.size(), response.getContent().size());
    Assertions.assertEquals(2, response.getTotalPages());
    Assertions.assertEquals(1, response.getPageable().getPageSize());
    Assertions.assertEquals(2, response.getTotalElements());

    verify(carrierServiceRepository, times(1))
        .findCarrierServicesByOrgId(anyString(), any(Pageable.class));
  }

  @Test
  void findCarrierServiceListByOrgIdDESCTest() throws CarrierServiceDomainException {
    List<CarrierServiceEntity> carrierServiceEntityList = testUtil.getCarrierServiceEntityList();
    Pageable pageable = PageRequest.of(1, 1);
    Page<CarrierServiceEntity> carrierServiceEntityPage =
        new PageImpl<>(carrierServiceEntityList, pageable, carrierServiceEntityList.size());

    when(carrierServiceRepository.findCarrierServicesByOrgId(anyString(), any(Pageable.class)))
        .thenReturn(carrierServiceEntityPage);

    Page<CarrierServiceResponse> response =
        carrierServiceDomain.findCarrierServiceListByOrgId(
            TestUtil.ORG_ID, 1, 1, TestUtil.SORT_BY, TestUtil.SORT_ORDER_DESC);

    Assertions.assertEquals(carrierServiceEntityList.size(), response.getContent().size());
    Assertions.assertEquals(2, response.getTotalPages());
    Assertions.assertEquals(1, response.getPageable().getPageSize());
    Assertions.assertEquals(
        carrierServiceEntityList.get(0).getOrgId(), response.getContent().get(0).getOrgId());
    Assertions.assertEquals(2, response.getTotalElements());

    verify(carrierServiceRepository, times(1))
        .findCarrierServicesByOrgId(anyString(), any(Pageable.class));
  }

  @Test
  void findCarrierServiceListByOrgIdTestException() {
    doThrow(new RuntimeException("Error while finding carrier service list"))
        .when(carrierServiceRepository)
        .findCarrierServicesByOrgId(anyString(), any(Pageable.class));

    Exception exception =
        assertThrows(
            CarrierServiceDomainException.class,
            () ->
                carrierServiceDomain.findCarrierServiceListByOrgId(
                    TestUtil.ORG_ID, 1, 1, TestUtil.SORT_BY, TestUtil.SORT_ORDER_ASC));
    Assertions.assertEquals("Error while finding carrier service list", exception.getMessage());
    verify(carrierServiceRepository, times(1))
        .findCarrierServicesByOrgId(anyString(), any(Pageable.class));
  }

  @Test
  void getAllCarrierServiceEntitiesTest() throws CarrierServiceDomainException {
    List<CarrierServiceEntity> carrierServiceEntities = testUtil.getCarrierServiceEntityList();

    when(carrierServiceRepository.findAllCarriersByLimit(any())).thenReturn(carrierServiceEntities);

    List<CarrierServiceEntity> response = carrierServiceDomain.getAllCarrierServiceEntities(2);

    Assertions.assertEquals(2, response.size());
    Assertions.assertEquals(
        carrierServiceEntities.get(0).getCarrierId(), response.get(0).getCarrierId());
    verify(carrierServiceRepository, times(1)).findAllCarriersByLimit(any());
  }

  @Test
  void getAllCarrierServiceEntitiesExceptionTest() {
    when(carrierServiceRepository.findAllCarriersByLimit(any()))
        .thenThrow(new RuntimeException("Error while fetching all carrier services"));

    Exception exception =
        assertThrows(
            CarrierServiceDomainException.class,
            () -> carrierServiceDomain.getAllCarrierServiceEntities(2));

    Assertions.assertEquals("Error while fetching all carrier services", exception.getMessage());
    verify(carrierServiceRepository, times(1)).findAllCarriersByLimit(any());
  }

  @Test
  void getCarrierServiceDetailsByCarrierServiceIdAndOrgIdTest() throws CarrierServiceDomainException {
    CarrierServiceEntity carrierServiceEntity = testUtil.getCarrierServiceEntity();
    when(carrierServiceRepository.findCarrierServiceByCarrierServiceIdAndOrgId(
            any(), any()))
            .thenReturn(Optional.of(List.of(carrierServiceEntity)));

    Optional<List<CarrierServiceEntity>> carrierServiceEntity1 =
            carrierServiceDomain.findCarrierServiceByServiceIdAndOrgId(
                    TestUtil.CARRIER_SERVICE_ID, TestUtil.ORG_ID);
    Assertions.assertEquals(List.of(carrierServiceEntity), carrierServiceEntity1.get());

    verify(carrierServiceRepository, times(1))
            .findCarrierServiceByCarrierServiceIdAndOrgId(any(), any());
  }

  @Test
  void getCarrierServiceDetailsByCarrierServiceIdAndOrgIdTestException() {
    when(carrierServiceRepository.findCarrierServiceByCarrierServiceIdAndOrgId(
            any(), any()))
            .thenThrow(new RuntimeException("Error while fetching details"));

    Exception exception =
            assertThrows(
                    CarrierServiceDomainException.class,
                    () ->
                            carrierServiceDomain.findCarrierServiceByServiceIdAndOrgId(
                                    TestUtil.CARRIER_SERVICE_ID, TestUtil.ORG_ID));
    Assertions.assertEquals("Error while finding carrier service", exception.getMessage());
    verify(carrierServiceRepository, times(1))
            .findCarrierServiceByCarrierServiceIdAndOrgId(any(), any());
  }

}
