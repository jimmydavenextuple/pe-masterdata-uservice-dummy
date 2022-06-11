package com.nextuple.pe.masterdata.service.domain;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.nextuple.pe.masterdata.domain.ServiceOptionInventoryTypeDomain;
import com.nextuple.pe.masterdata.domain.entity.ServiceOptionInventoryTypeEntity;
import com.nextuple.pe.masterdata.domain.repository.ServiceInventoryRepository;
import com.nextuple.pe.masterdata.exception.ServiceInventoryDomainException;
import com.nextuple.pe.masterdata.service.TestUtil;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ServiceOptionInventoryTypeDomainTest {
  @InjectMocks private ServiceOptionInventoryTypeDomain serviceOptionInventoryTypeDomain;
  @InjectMocks private TestUtil testUtil;

  @Mock private ServiceInventoryRepository serviceInventoryRepository;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void saveServiceOptionInventoryTypeEntityTest() throws ServiceInventoryDomainException {
    ServiceOptionInventoryTypeEntity serviceOptionInventoryTypeEntity =
        testUtil.getServiceOptionInventoryTypeEntity();
    when(serviceInventoryRepository.save(any())).thenReturn(serviceOptionInventoryTypeEntity);

    ServiceOptionInventoryTypeEntity serviceOptionInventoryTypeEntity1 =
        serviceOptionInventoryTypeDomain.saveServiceOptionInventoryTypeEntity(
            serviceOptionInventoryTypeEntity);
    Assertions.assertEquals(serviceOptionInventoryTypeEntity, serviceOptionInventoryTypeEntity1);

    verify(serviceInventoryRepository, times(1)).save(any());
  }

  @Test
  void saveServiceOptionInventoryTypeEntityExceptionTest() {
    ServiceOptionInventoryTypeEntity serviceOptionInventoryTypeEntity =
        testUtil.getServiceOptionInventoryTypeEntity();
    when(serviceInventoryRepository.save(any()))
        .thenThrow(new RuntimeException("Error while saving"));

    Exception exception =
        assertThrows(
            ServiceInventoryDomainException.class,
            () ->
                serviceOptionInventoryTypeDomain.saveServiceOptionInventoryTypeEntity(
                    serviceOptionInventoryTypeEntity));
    Assertions.assertEquals(
        "Error while saving the ServiceOptionInventoryTypeEntity", exception.getMessage());
    verify(serviceInventoryRepository, times(1)).save(any());
  }

  @Test
  void findServiceOptionInventoryTypeEntityByOrgIdAndServiceOptionTest()
      throws ServiceInventoryDomainException {
    ServiceOptionInventoryTypeEntity serviceOptionInventoryTypeEntity =
        testUtil.getServiceOptionInventoryTypeEntity();
    when(serviceInventoryRepository.findServiceOptionInventoryTypeEntityByOrgIdAndServiceOption(
            any(), any()))
        .thenReturn(Optional.of(serviceOptionInventoryTypeEntity));

    Optional<ServiceOptionInventoryTypeEntity> serviceOptionInventoryTypeEntity1 =
        serviceOptionInventoryTypeDomain
            .findServiceOptionInventoryTypeEntityByOrgIdAndServiceOption(
                TestUtil.ORG_ID, TestUtil.SERVICE_OPTION);
    Assertions.assertEquals(
        serviceOptionInventoryTypeEntity, serviceOptionInventoryTypeEntity1.get());

    verify(serviceInventoryRepository, times(1))
        .findServiceOptionInventoryTypeEntityByOrgIdAndServiceOption(any(), any());
  }

  @Test
  void findServiceOptionInventoryTypeEntityByOrgIdAndServiceOptionTestException() {
    when(serviceInventoryRepository.findServiceOptionInventoryTypeEntityByOrgIdAndServiceOption(
            any(), any()))
        .thenThrow(new RuntimeException("Error while fetching details"));

    Exception exception =
        assertThrows(
            ServiceInventoryDomainException.class,
            () ->
                serviceOptionInventoryTypeDomain
                    .findServiceOptionInventoryTypeEntityByOrgIdAndServiceOption(
                        TestUtil.ORG_ID, TestUtil.SERVICE_OPTION));
    Assertions.assertEquals(
        "Error while finding ServiceOptionInventoryTypeEntity", exception.getMessage());
    verify(serviceInventoryRepository, times(1))
        .findServiceOptionInventoryTypeEntityByOrgIdAndServiceOption(any(), any());
  }
}
