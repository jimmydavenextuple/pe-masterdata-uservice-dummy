package com.hbc.global.configuration.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.hbc.common.exception.CommonServiceException;
import com.hbc.common.exception.PromiseEngineException;
import com.hbc.common.response.BaseResponse;
import com.hbc.global.configuration.TestUtil;
import com.hbc.global.configuration.api.domain.dto.GlobalConfigurationDto;
import com.hbc.global.configuration.api.domain.inbound.CreateGlobalConfigurationRequest;
import com.hbc.global.configuration.api.domain.inbound.UpdateGlobalConfigurationRequest;
import com.hbc.global.configuration.service.GlobalConfigurationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class GlobalConfigurationControllerTest {
  @InjectMocks private GlobalConfigurationController configurationController;

  @Mock private GlobalConfigurationService configurationService;

  @InjectMocks private TestUtil testUtil;

  @Test
  void createGlobalConfiguration_Test() throws PromiseEngineException, CommonServiceException {
    when(configurationService.createGlobalConfig(any(CreateGlobalConfigurationRequest.class)))
        .thenReturn(testUtil.getGlobalConfigurationDto());
    Assertions.assertDoesNotThrow(
        () -> configurationController.createGlobalConfiguration(testUtil.getCreateRequest()));
  }

  @Test
  void createGlobalConfiguration_TestException()
      throws PromiseEngineException, CommonServiceException {
    when(configurationService.createGlobalConfig(any(CreateGlobalConfigurationRequest.class)))
        .thenThrow(new CommonServiceException(HttpStatus.BAD_GATEWAY, null, null));
    Assertions.assertThrows(
        CommonServiceException.class,
        () -> configurationController.createGlobalConfiguration(testUtil.getCreateRequest()));
  }

  @Test
  void fetchValue_Test() throws PromiseEngineException, CommonServiceException {
    when(configurationService.fetchValue(anyString(), anyString(), anyString()))
        .thenReturn(testUtil.getGlobalConfigurationDto());
    ResponseEntity<BaseResponse<GlobalConfigurationDto>> globalConfigurationDto =
        Assertions.assertDoesNotThrow(
            () -> configurationController.fetchValue(TestUtil.ORGID, TestUtil.TYPE, TestUtil.KEY));

    Assertions.assertEquals("PICK", globalConfigurationDto.getBody().getPayload().getValue());
  }

  @Test
  void fetchValue_TestException() throws PromiseEngineException {
    when(configurationService.fetchValue(anyString(), anyString(), anyString()))
        .thenThrow(new RuntimeException());
    Assertions.assertThrows(
        RuntimeException.class,
        () -> configurationController.fetchValue(TestUtil.ORGID, TestUtil.TYPE, TestUtil.KEY));
  }

  @Test
  void updateGlobalConfiguration_Test() throws PromiseEngineException, CommonServiceException {
    when(configurationService.updateGlobalConfiguration(
            anyString(), anyString(), anyString(), any(UpdateGlobalConfigurationRequest.class)))
        .thenReturn(testUtil.getGlobalConfigurationDto());
    Assertions.assertDoesNotThrow(
        () ->
            configurationController.updateGlobalConfiguration(
                TestUtil.ORGID,
                TestUtil.TYPE,
                TestUtil.KEY,
                UpdateGlobalConfigurationRequest.builder().value("PICK").build()));
  }

  @Test
  void updateGlobalConfiguration_TestException()
      throws PromiseEngineException, CommonServiceException {
    when(configurationService.updateGlobalConfiguration(
            anyString(), anyString(), anyString(), any(UpdateGlobalConfigurationRequest.class)))
        .thenThrow(new CommonServiceException(HttpStatus.BAD_GATEWAY, null, null));
    Assertions.assertThrows(
        CommonServiceException.class,
        () ->
            configurationController.updateGlobalConfiguration(
                TestUtil.ORGID,
                TestUtil.TYPE,
                TestUtil.KEY,
                UpdateGlobalConfigurationRequest.builder().value("PICK").build()));
  }

  @Test
  void deleteGlobalConfiguration_Test() throws PromiseEngineException, CommonServiceException {
    when(configurationService.deleteGlobalConfiguration(anyString(), anyString(), anyString()))
        .thenReturn(testUtil.getGlobalConfigurationDto());
    Assertions.assertDoesNotThrow(
        () ->
            configurationController.deleteGlobalConfiguration(
                TestUtil.ORGID, TestUtil.TYPE, TestUtil.KEY));
  }

  @Test
  void deleteGlobalConfiguration_TestException()
      throws PromiseEngineException, CommonServiceException {
    when(configurationService.deleteGlobalConfiguration(anyString(), anyString(), anyString()))
        .thenThrow(new CommonServiceException(HttpStatus.BAD_GATEWAY, null, null));
    Assertions.assertThrows(
        CommonServiceException.class,
        () ->
            configurationController.deleteGlobalConfiguration(
                TestUtil.ORGID, TestUtil.TYPE, TestUtil.KEY));
  }
}
