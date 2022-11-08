package com.nextuple.common.configuration.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.common.configuration.TestUtil;
import com.nextuple.common.configuration.api.domain.dto.CommonConfigurationDto;
import com.nextuple.common.configuration.domain.CommonConfigurationDomain;
import com.nextuple.common.configuration.domain.entity.CommonConfiguration;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.exception.PromiseEngineException;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CommonConfigurationServiceTest {

  @InjectMocks private TestUtil testUtil;
  @InjectMocks private CommonConfigurationService commonConfigurationService;

  @Mock private CommonConfigurationDomain commonConfigurationDomain;

  @Test
  @DisplayName("No Result Found, Return null Result")
  void fetchValue_Test() throws PromiseEngineException {
    when(commonConfigurationDomain.getCommonConfiguration(anyString(), anyString(), anyString()))
        .thenReturn(Optional.empty());
    CommonConfigurationDto configurationDto =
        Assertions.assertDoesNotThrow(
            () ->
                commonConfigurationService.fetchValue(
                    TestUtil.ORGID, TestUtil.KEY, TestUtil.ORGID));
    Assertions.assertNull(configurationDto);
  }

  @Test
  void fetchValue_TestEntityExist() throws PromiseEngineException {
    when(commonConfigurationDomain.getCommonConfiguration(anyString(), anyString(), anyString()))
        .thenReturn(Optional.of(testUtil.getCommonConfiguration()));
    CommonConfigurationDto configurationDto =
        Assertions.assertDoesNotThrow(
            () ->
                commonConfigurationService.fetchValue(
                    TestUtil.ORGID, TestUtil.KEY, TestUtil.ORGID));
    Assertions.assertEquals("PICK", configurationDto.getValue());
  }

  @Test
  void createCommonConfig_Test2() throws PromiseEngineException {

    Assertions.assertDoesNotThrow(
        () -> commonConfigurationService.createCommonConfig(testUtil.getCreateRequest()));
    verify(commonConfigurationDomain, times(1))
        .saveCommonConfiguration(any(CommonConfiguration.class));
  }

  @Test
  void updateCommonConfiguration_Test2() throws PromiseEngineException {

    Assertions.assertDoesNotThrow(
        () -> commonConfigurationService.updateCommonConfiguration(testUtil.getCreateRequest()));

    verify(commonConfigurationDomain, times(1))
        .saveCommonConfiguration(any(CommonConfiguration.class));
  }

  @Test
  @DisplayName("Configuration doesn't Exist")
  void deleteCommonConfiguration_Test1() throws PromiseEngineException {

    when(commonConfigurationDomain.getCommonConfiguration(anyString(), anyString(), anyString()))
        .thenReturn(Optional.empty());
    Assertions.assertThrows(
        CommonServiceException.class,
        () ->
            commonConfigurationService.deleteCommonConfiguration(
                TestUtil.ORGID, TestUtil.TYPE, TestUtil.KEY));
  }

  @Test
  void deleteCommonConfiguration_Test2() throws PromiseEngineException {
    when(commonConfigurationDomain.getCommonConfiguration(anyString(), anyString(), anyString()))
        .thenReturn(Optional.of(testUtil.getCommonConfiguration()));

    Assertions.assertDoesNotThrow(
        () ->
            commonConfigurationService.deleteCommonConfiguration(
                TestUtil.ORGID, TestUtil.TYPE, TestUtil.KEY));

    verify(commonConfigurationDomain, times(1))
        .deleteCommonConfiguration(any(CommonConfiguration.class));
  }
}
