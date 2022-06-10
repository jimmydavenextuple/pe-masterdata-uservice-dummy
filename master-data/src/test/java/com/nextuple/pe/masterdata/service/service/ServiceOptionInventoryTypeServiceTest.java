package com.nextuple.pe.masterdata.service.service;

import com.nextuple.pe.masterdata.domain.ServiceOptionInventoryTypeDomain;
import com.nextuple.pe.masterdata.domain.entity.ServiceOptionInventoryTypeEntity;
import com.nextuple.pe.masterdata.domain.inbound.ServiceInventoryRequest;
import com.nextuple.pe.masterdata.domain.outbound.ServiceInventoryDto;
import com.nextuple.pe.masterdata.exception.ServiceInventoryDomainException;
import com.nextuple.pe.masterdata.exception.common.CommonServiceException;
import com.nextuple.pe.masterdata.service.ServiceOptionInventoryTypeService;
import com.nextuple.pe.masterdata.service.TestUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

public class ServiceOptionInventoryTypeServiceTest {
  @InjectMocks private ServiceOptionInventoryTypeService serviceOptionInventoryTypeService;

  @InjectMocks private TestUtil testUtil;

  @Mock private ServiceOptionInventoryTypeDomain serviceOptionInventoryTypeDomain;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void createServiceOptionInventoryTypeTest() throws ServiceInventoryDomainException {
    ServiceOptionInventoryTypeEntity serviceOptionInventoryTypeEntity =
        testUtil.getServiceOptionInventoryTypeEntity();
    ServiceInventoryRequest serviceInventoryRequest = testUtil.getServiceInventoryRequest();
    when(serviceOptionInventoryTypeDomain.saveServiceOptionInventoryTypeEntity(
            any(ServiceOptionInventoryTypeEntity.class)))
        .thenReturn(serviceOptionInventoryTypeEntity);

    ServiceInventoryDto dto =
        serviceOptionInventoryTypeService.createServiceOptionInventoryType(
            testUtil.getServiceInventoryRequest());
    Assertions.assertEquals(serviceInventoryRequest.getServiceOption(), dto.getServiceOption());
    verify(serviceOptionInventoryTypeDomain, times(1))
        .saveServiceOptionInventoryTypeEntity(any(ServiceOptionInventoryTypeEntity.class));
  }

  @Test
  void getServiceOptionInventoryTypeMappingTest()
      throws ServiceInventoryDomainException, CommonServiceException {
    ServiceOptionInventoryTypeEntity serviceOptionInventoryTypeEntity =
        testUtil.getServiceOptionInventoryTypeEntity();
    when(serviceOptionInventoryTypeDomain
            .findServiceOptionInventoryTypeEntityByOrgIdAndServiceOption(any(), any()))
        .thenReturn(Optional.of(serviceOptionInventoryTypeEntity));

    ServiceInventoryDto serviceInventoryDto =
        serviceOptionInventoryTypeService.getServiceOptionInventoryTypeMapping(
            TestUtil.ORG_ID, TestUtil.SERVICE_OPTION);
    Assertions.assertEquals(testUtil.getServiceInventoryDto(), serviceInventoryDto);
    verify(serviceOptionInventoryTypeDomain, times(1))
        .findServiceOptionInventoryTypeEntityByOrgIdAndServiceOption(any(), any());
  }

  @Test
  void getServiceOptionInventoryTypeMappingExceptionTest() throws ServiceInventoryDomainException {
    when(serviceOptionInventoryTypeDomain
            .findServiceOptionInventoryTypeEntityByOrgIdAndServiceOption(any(), any()))
        .thenReturn(Optional.empty());

    Exception exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                serviceOptionInventoryTypeService.getServiceOptionInventoryTypeMapping(
                    TestUtil.ORG_ID, TestUtil.SERVICE_OPTION));
    Assertions.assertEquals(
        "ServiceOptionInventoryType not found with given details", exception.getMessage());

    verify(serviceOptionInventoryTypeDomain, times(1))
        .findServiceOptionInventoryTypeEntityByOrgIdAndServiceOption(any(), any());
  }
}
