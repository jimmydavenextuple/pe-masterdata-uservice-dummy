package com.hbc.global.configuration.domain;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import com.hbc.common.exception.PromiseEngineException;
import com.hbc.global.configuration.TestUtil;
import com.hbc.global.configuration.domain.entity.GlobalConfiguration;
import com.hbc.global.configuration.repository.GlobalConfigurationRepository;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GlobalConfigurationDomainTest {

  @InjectMocks private GlobalConfigurationDomain globalConfigurationDomain;

  @Mock private GlobalConfigurationRepository repository;

  @InjectMocks private TestUtil testUtil;

  @Test
  void getGlobalConfiguration_Test() {
    when(repository.findByOrgIdAndTypeAndKey(anyString(), anyString(), anyString()))
        .thenReturn(Optional.of(testUtil.getGlobalConfiguration()));
    Optional<GlobalConfiguration> result =
        Assertions.assertDoesNotThrow(
            () ->
                globalConfigurationDomain.getGlobalConfiguration(
                    TestUtil.ORGID, TestUtil.TYPE, TestUtil.KEY));
    Assertions.assertEquals("PICK", result.get().getValue());
  }

  @Test
  void getGlobalConfiguration_TestException() {
    when(repository.findByOrgIdAndTypeAndKey(anyString(), anyString(), anyString()))
        .thenThrow(NullPointerException.class);
    PromiseEngineException ex =
        Assertions.assertThrows(
            PromiseEngineException.class,
            () ->
                globalConfigurationDomain.getGlobalConfiguration(
                    TestUtil.ORGID, TestUtil.TYPE, TestUtil.KEY));
    Assertions.assertEquals("Unable to find Global Configuration", ex.getMessage());
  }

  @Test
  void saveGlobalConfiguration_Test() {
    when(repository.save(any(GlobalConfiguration.class)))
        .thenReturn(testUtil.getGlobalConfiguration());
    GlobalConfiguration globalConfiguration =
        Assertions.assertDoesNotThrow(
            () ->
                globalConfigurationDomain.saveGlobalConfiguration(
                    testUtil.getGlobalConfiguration()));
    Assertions.assertEquals(TestUtil.KEY, globalConfiguration.getKey());
    Assertions.assertEquals(TestUtil.ORGID, globalConfiguration.getOrgId());
    Assertions.assertEquals(TestUtil.TYPE, globalConfiguration.getType());
  }

  @Test
  void saveGlobalConfiguration_TestException() {
    when(repository.save(any(GlobalConfiguration.class))).thenThrow(RuntimeException.class);
    PromiseEngineException ex =
        Assertions.assertThrows(
            PromiseEngineException.class,
            () ->
                globalConfigurationDomain.saveGlobalConfiguration(
                    testUtil.getGlobalConfiguration()));
    Assertions.assertEquals("Unable to save Global Configuration", ex.getMessage());
  }

  @Test
  void deleteGlobalConfiguration_Test() {
    doNothing().when(repository).delete(any(GlobalConfiguration.class));
    Assertions.assertDoesNotThrow(
        () ->
            globalConfigurationDomain.deleteGlobalConfiguration(testUtil.getGlobalConfiguration()));
  }

  @Test
  void deleteGlobalConfiguration_TestException() {
    doThrow(RuntimeException.class).when(repository).delete(any(GlobalConfiguration.class));
    PromiseEngineException ex =
        Assertions.assertThrows(
            PromiseEngineException.class,
            () ->
                globalConfigurationDomain.deleteGlobalConfiguration(
                    testUtil.getGlobalConfiguration()));
    Assertions.assertEquals("Unable to delete Global Configuration", ex.getMessage());
  }
}
