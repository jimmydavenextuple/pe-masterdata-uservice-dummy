package com.hbc.common.configuration.domain;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import com.hbc.common.configuration.TestUtil;
import com.hbc.common.configuration.domain.entity.CommonConfiguration;
import com.hbc.common.configuration.repository.CommonConfigurationRepository;
import com.hbc.common.exception.PromiseEngineException;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CommonConfigurationDomainTest {

  @InjectMocks private CommonConfigurationDomain commonConfigurationDomain;

  @Mock private CommonConfigurationRepository repository;

  @InjectMocks private TestUtil testUtil;

  @Test
  void getCommonConfiguration_Test() {
    when(repository.findByOrgIdAndTypeAndKey(anyString(), anyString(), anyString()))
        .thenReturn(Optional.of(testUtil.getCommonConfiguration()));
    Optional<CommonConfiguration> result =
        Assertions.assertDoesNotThrow(
            () ->
                commonConfigurationDomain.getCommonConfiguration(
                    TestUtil.ORGID, TestUtil.TYPE, TestUtil.KEY));
    Assertions.assertEquals("PICK", result.get().getValue());
  }

  @Test
  void getCommonConfiguration_TestException() {
    when(repository.findByOrgIdAndTypeAndKey(anyString(), anyString(), anyString()))
        .thenThrow(NullPointerException.class);
    PromiseEngineException ex =
        Assertions.assertThrows(
            PromiseEngineException.class,
            () ->
                commonConfigurationDomain.getCommonConfiguration(
                    TestUtil.ORGID, TestUtil.TYPE, TestUtil.KEY));
    Assertions.assertEquals("Unable to find Common Configuration", ex.getMessage());
  }

  @Test
  void saveCommonConfiguration_Test() {
    when(repository.save(any(CommonConfiguration.class)))
        .thenReturn(testUtil.getCommonConfiguration());
    CommonConfiguration globalConfiguration =
        Assertions.assertDoesNotThrow(
            () ->
                commonConfigurationDomain.saveCommonConfiguration(
                    testUtil.getCommonConfiguration()));
    Assertions.assertEquals(TestUtil.KEY, globalConfiguration.getKey());
    Assertions.assertEquals(TestUtil.ORGID, globalConfiguration.getOrgId());
    Assertions.assertEquals(TestUtil.TYPE, globalConfiguration.getType());
  }

  @Test
  void saveCommonConfiguration_TestException() {
    when(repository.save(any(CommonConfiguration.class))).thenThrow(RuntimeException.class);
    PromiseEngineException ex =
        Assertions.assertThrows(
            PromiseEngineException.class,
            () ->
                commonConfigurationDomain.saveCommonConfiguration(
                    testUtil.getCommonConfiguration()));
    Assertions.assertEquals("Unable to save Common Configuration", ex.getMessage());
  }

  @Test
  void deleteCommonConfiguration_Test() {
    doNothing().when(repository).delete(any(CommonConfiguration.class));
    Assertions.assertDoesNotThrow(
        () ->
            commonConfigurationDomain.deleteCommonConfiguration(testUtil.getCommonConfiguration()));
  }

  @Test
  void deleteCommonConfiguration_TestException() {
    doThrow(RuntimeException.class).when(repository).delete(any(CommonConfiguration.class));
    PromiseEngineException ex =
        Assertions.assertThrows(
            PromiseEngineException.class,
            () ->
                commonConfigurationDomain.deleteCommonConfiguration(
                    testUtil.getCommonConfiguration()));
    Assertions.assertEquals("Unable to delete Common Configuration", ex.getMessage());
  }
}
