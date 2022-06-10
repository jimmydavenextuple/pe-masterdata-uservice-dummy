package com.nextuple.pe.masterdata.service.domain;

import com.nextuple.pe.masterdata.domain.CarrierServiceDomain;
import com.nextuple.pe.masterdata.domain.entity.CarrierServiceEntity;
import com.nextuple.pe.masterdata.domain.repository.CarrierServiceRepository;
import com.nextuple.pe.masterdata.exception.CarrierServiceDomainException;
import com.nextuple.pe.masterdata.service.TestUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CarrierServiceDomainTest {

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
    when(carrierServiceRepository.findCarrierServiceByCarrierIdAndServiceIdAndOrgId(
            any(), any(), any()))
        .thenReturn(Optional.of(carrierServiceEntity));

    Optional<CarrierServiceEntity> carrierServiceEntity1 =
        carrierServiceDomain.findCarrierServiceByCarrierIdAndServiceIdAndOrgId(
            TestUtil.CARRIER_ID, TestUtil.SERVICE_ID, TestUtil.ORG_ID);
    Assertions.assertEquals(carrierServiceEntity, carrierServiceEntity1.get());

    verify(carrierServiceRepository, times(1))
        .findCarrierServiceByCarrierIdAndServiceIdAndOrgId(any(), any(), any());
  }

  @Test
  void getCarrierServiceDetailsTestException() {
    when(carrierServiceRepository.findCarrierServiceByCarrierIdAndServiceIdAndOrgId(
            any(), any(), any()))
        .thenThrow(new RuntimeException("Error while fetching details"));

    Exception exception =
        assertThrows(
            CarrierServiceDomainException.class,
            () ->
                carrierServiceDomain.findCarrierServiceByCarrierIdAndServiceIdAndOrgId(
                    TestUtil.CARRIER_ID, TestUtil.SERVICE_ID, TestUtil.ORG_ID));
    Assertions.assertEquals("Error while finding carrier service", exception.getMessage());
    verify(carrierServiceRepository, times(1))
        .findCarrierServiceByCarrierIdAndServiceIdAndOrgId(any(), any(), any());
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
}
