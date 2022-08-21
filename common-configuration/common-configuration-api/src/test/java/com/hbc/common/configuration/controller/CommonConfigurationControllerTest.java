package com.hbc.common.configuration.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.hbc.common.configuration.TestUtil;
import com.hbc.common.configuration.api.domain.dto.CommonConfigurationDto;
import com.hbc.common.configuration.api.domain.inbound.CreateCommonConfigurationRequest;
import com.hbc.common.configuration.api.domain.inbound.UpdateCommonConfigurationRequest;
import com.hbc.common.configuration.service.CommonConfigurationService;
import com.hbc.common.exception.CommonServiceException;
import com.hbc.common.exception.PromiseEngineException;
import com.hbc.common.response.BaseResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class CommonConfigurationControllerTest {
  @InjectMocks private CommonConfigurationController configurationController;

  @Mock private CommonConfigurationService configurationService;

  @InjectMocks private TestUtil testUtil;

  @Test
  void createCommonConfiguration_Test() throws PromiseEngineException, CommonServiceException {
    when(configurationService.createCommonConfig(any(CreateCommonConfigurationRequest.class)))
        .thenReturn(testUtil.getCommonConfigurationDto());
    Assertions.assertDoesNotThrow(
        () -> configurationController.createCommonConfiguration(testUtil.getCreateRequest()));
  }

  @Test
  void createCommonConfiguration_TestException()
      throws PromiseEngineException, CommonServiceException {
    when(configurationService.createCommonConfig(any(CreateCommonConfigurationRequest.class)))
        .thenThrow(new CommonServiceException(HttpStatus.BAD_GATEWAY, null, null));
    Assertions.assertThrows(
        CommonServiceException.class,
        () -> configurationController.createCommonConfiguration(testUtil.getCreateRequest()));
  }

  @Test
  void fetchValue_Test() throws PromiseEngineException {
    when(configurationService.fetchValue(anyString(), anyString(), anyString()))
        .thenReturn(testUtil.getCommonConfigurationDto());
    ResponseEntity<BaseResponse<CommonConfigurationDto>> commonConfigurationDto =
        Assertions.assertDoesNotThrow(
            () -> configurationController.fetchValue(TestUtil.ORGID, TestUtil.TYPE, TestUtil.KEY));

    Assertions.assertEquals("PICK", commonConfigurationDto.getBody().getPayload().getValue());
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
  void updateCommonConfiguration_Test() throws PromiseEngineException, CommonServiceException {
    when(configurationService.updateCommonConfiguration(
            anyString(), anyString(), anyString(), any(UpdateCommonConfigurationRequest.class)))
        .thenReturn(testUtil.getCommonConfigurationDto());
    Assertions.assertDoesNotThrow(
        () ->
            configurationController.updateCommonConfiguration(
                TestUtil.ORGID,
                TestUtil.TYPE,
                TestUtil.KEY,
                UpdateCommonConfigurationRequest.builder().value("PICK").build()));
  }

  @Test
  void updateCommonConfiguration_TestException()
      throws PromiseEngineException, CommonServiceException {
    when(configurationService.updateCommonConfiguration(
            anyString(), anyString(), anyString(), any(UpdateCommonConfigurationRequest.class)))
        .thenThrow(new CommonServiceException(HttpStatus.BAD_GATEWAY, null, null));
    Assertions.assertThrows(
        CommonServiceException.class,
        () ->
            configurationController.updateCommonConfiguration(
                TestUtil.ORGID,
                TestUtil.TYPE,
                TestUtil.KEY,
                UpdateCommonConfigurationRequest.builder().value("PICK").build()));
  }

  @Test
  void deleteCommonConfiguration_Test() throws PromiseEngineException, CommonServiceException {
    when(configurationService.deleteCommonConfiguration(anyString(), anyString(), anyString()))
        .thenReturn(testUtil.getCommonConfigurationDto());
    Assertions.assertDoesNotThrow(
        () ->
            configurationController.deleteCommonConfiguration(
                TestUtil.ORGID, TestUtil.TYPE, TestUtil.KEY));
  }

  @Test
  void deleteCommonConfiguration_TestException()
      throws PromiseEngineException, CommonServiceException {
    when(configurationService.deleteCommonConfiguration(anyString(), anyString(), anyString()))
        .thenThrow(new CommonServiceException(HttpStatus.BAD_GATEWAY, null, null));
    Assertions.assertThrows(
        CommonServiceException.class,
        () ->
            configurationController.deleteCommonConfiguration(
                TestUtil.ORGID, TestUtil.TYPE, TestUtil.KEY));
  }
}
