package com.nextuple.service.inventory.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.service.inventory.TestUtil;
import com.nextuple.service.inventory.domain.inbound.ServiceInventoryRequest;
import com.nextuple.service.inventory.domain.outbound.ServiceInventoryDto;
import com.nextuple.service.inventory.exception.CommonServiceException;
import com.nextuple.service.inventory.exception.ServiceInventoryDomainException;
import com.nextuple.service.inventory.service.ServiceOptionInventoryTypeService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ServiceOptionInventoryTypeControllerTest {
  @InjectMocks private ServiceOptionInventoryTypeController serviceOptionInventoryTypeController;

  @InjectMocks private TestUtil testUtil;

  @Mock private ServiceOptionInventoryTypeService serviceOptionInventoryTypeService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void createServiceOptionInventoryTypeTest() throws ServiceInventoryDomainException {
    ServiceInventoryRequest serviceInventoryRequest = testUtil.getServiceInventoryRequest();
    when(serviceOptionInventoryTypeService.createServiceOptionInventoryType(
            any(ServiceInventoryRequest.class)))
        .thenReturn(testUtil.getServiceInventoryDto());

    ResponseEntity<BaseResponse<ServiceInventoryDto>> responseEntity =
        serviceOptionInventoryTypeController.createServiceOptionInventoryType(
            serviceInventoryRequest);

    Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    Assertions.assertEquals(
        testUtil.getServiceInventoryDto(), responseEntity.getBody().getPayload());

    verify(serviceOptionInventoryTypeService, times(1)).createServiceOptionInventoryType(any());
  }

  @Test
  void createServiceOptionInventoryTypeExceptionTest() throws ServiceInventoryDomainException {
    ServiceInventoryRequest serviceInventoryRequest = testUtil.getServiceInventoryRequest();
    when(serviceOptionInventoryTypeService.createServiceOptionInventoryType(
            any(ServiceInventoryRequest.class)))
        .thenThrow(new RuntimeException("Failed to create ServiceInventoryMapping"));

    Exception exception =
        Assertions.assertThrows(
            Exception.class,
            () ->
                serviceOptionInventoryTypeController.createServiceOptionInventoryType(
                    serviceInventoryRequest));
    Assertions.assertEquals("Failed to create ServiceInventoryMapping", exception.getMessage());

    verify(serviceOptionInventoryTypeService, times(1)).createServiceOptionInventoryType(any());
  }

  @Test
  void getServiceOptionToInventoryMappingTest()
      throws ServiceInventoryDomainException, CommonServiceException {
    ServiceInventoryDto serviceInventoryDto = testUtil.getServiceInventoryDto();
    when(serviceOptionInventoryTypeService.getServiceOptionInventoryTypeMapping(any(), any()))
        .thenReturn(serviceInventoryDto);

    ResponseEntity<BaseResponse<ServiceInventoryDto>> responseEntity =
        serviceOptionInventoryTypeController.getServiceOptionToInventoryMapping(
            TestUtil.ORG_ID, TestUtil.SERVICE_OPTION);

    Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    Assertions.assertEquals(serviceInventoryDto, responseEntity.getBody().getPayload());

    verify(serviceOptionInventoryTypeService, times(1))
        .getServiceOptionInventoryTypeMapping(any(), any());
  }

  @Test
  void getServiceOptionToInventoryMappingExceptionTest()
      throws ServiceInventoryDomainException, CommonServiceException {
    when(serviceOptionInventoryTypeService.getServiceOptionInventoryTypeMapping(any(), any()))
        .thenThrow(new RuntimeException("Unable to fetch ServiceInventoryMapping"));

    Exception exception =
        Assertions.assertThrows(
            Exception.class,
            () ->
                serviceOptionInventoryTypeController.getServiceOptionToInventoryMapping(
                    TestUtil.ORG_ID, TestUtil.SERVICE_OPTION));
    Assertions.assertEquals("Unable to fetch ServiceInventoryMapping", exception.getMessage());
    verify(serviceOptionInventoryTypeService, times(1))
        .getServiceOptionInventoryTypeMapping(any(), any());
  }
}
