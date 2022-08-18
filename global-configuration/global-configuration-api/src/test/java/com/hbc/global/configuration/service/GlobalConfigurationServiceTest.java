package com.hbc.global.configuration.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hbc.common.exception.CommonServiceException;
import com.hbc.common.exception.PromiseEngineException;
import com.hbc.global.configuration.TestUtil;
import com.hbc.global.configuration.api.domain.dto.GlobalConfigurationDto;
import com.hbc.global.configuration.api.domain.inbound.UpdateGlobalConfigurationRequest;
import com.hbc.global.configuration.domain.GlobalConfigurationDomain;
import com.hbc.global.configuration.domain.entity.GlobalConfiguration;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GlobalConfigurationServiceTest {

  @InjectMocks private TestUtil testUtil;
  @InjectMocks private GlobalConfigurationService globalConfigurationService;

  @Mock private GlobalConfigurationDomain globalConfigurationDomain;

  @Test
  @DisplayName("No Result Found, Return Undefined Result")
  void fetchValue_Test() throws PromiseEngineException {
    when(globalConfigurationDomain.getGlobalConfiguration(anyString(), anyString(), anyString()))
        .thenReturn(Optional.empty());
    GlobalConfigurationDto configurationDto =
        Assertions.assertDoesNotThrow(
            () ->
                globalConfigurationService.fetchValue(
                    TestUtil.ORGID, TestUtil.KEY, TestUtil.ORGID));
    Assertions.assertEquals("UNDEFINED", configurationDto.getValue());
  }

  @Test
  void fetchValue_TestEntityExist() throws PromiseEngineException {
    when(globalConfigurationDomain.getGlobalConfiguration(anyString(), anyString(), anyString()))
        .thenReturn(Optional.of(testUtil.getGlobalConfiguration()));
    GlobalConfigurationDto configurationDto =
        Assertions.assertDoesNotThrow(
            () ->
                globalConfigurationService.fetchValue(
                    TestUtil.ORGID, TestUtil.KEY, TestUtil.ORGID));
    Assertions.assertEquals("PICK", configurationDto.getValue());
  }

  @Test
  @DisplayName("Configuration Already Exist")
  void createGlobalConfig_Test1() throws PromiseEngineException {
    when(globalConfigurationDomain.getGlobalConfiguration(anyString(), anyString(), anyString()))
        .thenReturn(Optional.of(testUtil.getGlobalConfiguration()));

    Assertions.assertThrows(
        CommonServiceException.class,
        () -> globalConfigurationService.createGlobalConfig(testUtil.getCreateRequest()));
  }

  @Test
  void createGlobalConfig_Test2() throws PromiseEngineException {
    when(globalConfigurationDomain.getGlobalConfiguration(anyString(), anyString(), anyString()))
        .thenReturn(Optional.empty());

    GlobalConfigurationDto configurationDto =
        Assertions.assertDoesNotThrow(
            () -> globalConfigurationService.createGlobalConfig(testUtil.getCreateRequest()));
    verify(globalConfigurationDomain, times(1))
        .saveGlobalConfiguration(any(GlobalConfiguration.class));
  }

  @Test
  @DisplayName("Configuration doesn't Exist")
  void updateGlobalConfiguration_Test1() throws PromiseEngineException {

    when(globalConfigurationDomain.getGlobalConfiguration(anyString(), anyString(), anyString()))
        .thenReturn(Optional.empty());
    Assertions.assertThrows(
        CommonServiceException.class,
        () ->
            globalConfigurationService.updateGlobalConfiguration(
                TestUtil.ORGID,
                TestUtil.TYPE,
                TestUtil.KEY,
                UpdateGlobalConfigurationRequest.builder().value("SHIP").build()));
  }

  @Test
  void updateGlobalConfiguration_Test2() throws PromiseEngineException {
    when(globalConfigurationDomain.getGlobalConfiguration(anyString(), anyString(), anyString()))
        .thenReturn(Optional.of(testUtil.getGlobalConfiguration()));

    Assertions.assertDoesNotThrow(
        () ->
            globalConfigurationService.updateGlobalConfiguration(
                TestUtil.ORGID,
                TestUtil.TYPE,
                TestUtil.KEY,
                UpdateGlobalConfigurationRequest.builder().value("SHIP").build()));

    verify(globalConfigurationDomain, times(1))
        .saveGlobalConfiguration(any(GlobalConfiguration.class));
  }
}
