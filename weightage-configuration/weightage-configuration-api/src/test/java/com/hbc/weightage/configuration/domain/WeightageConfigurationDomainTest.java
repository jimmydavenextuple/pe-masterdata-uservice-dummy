package com.hbc.weightage.configuration.domain;

import static com.hbc.weightage.configuration.utils.WeightageConfigurationConstants.KEYS;
import static com.hbc.weightage.configuration.utils.WeightageConfigurationConstants.ORG_ID;
import static com.hbc.weightage.configuration.utils.WeightageConfigurationConstants.TYPE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.hbc.common.exception.PromiseEngineException;
import com.hbc.weightage.configuration.api.domain.inbound.FetchWeightageRequest;
import com.hbc.weightage.configuration.domain.entity.WeightageConfiguration;
import com.hbc.weightage.configuration.domain.repository.WeightageConfigurationRepository;
import com.hbc.weightage.configuration.TestUtil;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class WeightageConfigurationDomainTest {
  @InjectMocks private WeightageConfigurationDomain weightageConfigurationDomain;
  @Mock private WeightageConfigurationRepository weightageConfigurationRepository;
  @InjectMocks private TestUtil testUtil;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void fetchWeightageTest() throws PromiseEngineException {
    WeightageConfiguration weightageConfiguration = testUtil.getWeightageConfiguration();
    FetchWeightageRequest fetchWeightageRequest = testUtil.getFetchWeightageRequest();

    when(weightageConfigurationRepository.findByKeyInAndOrgIdAndType(
            anyList(), anyString(), anyString()))
        .thenReturn(Collections.singletonList(weightageConfiguration));

    List<WeightageConfiguration> received_entity =
        weightageConfigurationDomain.fetchWeightage(fetchWeightageRequest);
    assertEquals(KEYS.get(0), received_entity.get(0).getKey());
  }

  @Test
  void fetchWeightageExceptionTest() {
    FetchWeightageRequest fetchWeightageRequest = testUtil.getFetchWeightageRequest();

    when(weightageConfigurationRepository.findByKeyInAndOrgIdAndType(
            anyList(), anyString(), anyString()))
        .thenThrow(NullPointerException.class);

    assertThrows(
        PromiseEngineException.class,
        () -> {
          weightageConfigurationDomain.fetchWeightage(fetchWeightageRequest);
        });
  }

  @Test
  void createWeightageConfigurationTest() throws PromiseEngineException {
    WeightageConfiguration weightageConfiguration = testUtil.getWeightageConfiguration();

    when(weightageConfigurationRepository.save(any(WeightageConfiguration.class)))
        .thenReturn(weightageConfiguration);

    WeightageConfiguration received_entity =
        weightageConfigurationDomain.saveWeightageConfiguration(weightageConfiguration);
    assertEquals(weightageConfiguration, received_entity);
  }

  @Test
  void createWeightageConfigurationExceptionTest() {
    WeightageConfiguration weightageConfiguration = testUtil.getWeightageConfiguration();

    when(weightageConfigurationRepository.save(any(WeightageConfiguration.class)))
        .thenThrow(NullPointerException.class);

    assertThrows(
        PromiseEngineException.class,
        () -> {
          weightageConfigurationDomain.saveWeightageConfiguration(weightageConfiguration);
        });
  }

  @Test
  void getWeightageConfigurationTest() throws PromiseEngineException {
    WeightageConfiguration weightageConfiguration = testUtil.getWeightageConfiguration();

    when(weightageConfigurationRepository.findByOrgIdAndTypeAndKey(
            anyString(), anyString(), anyString()))
        .thenReturn(weightageConfiguration);

    WeightageConfiguration received_entity =
        weightageConfigurationDomain.getWeightageConfiguration(ORG_ID, TYPE, KEYS.get(0));
    assertEquals(weightageConfiguration, received_entity);
  }

  @Test
  void getWeightageConfigurationExceptionTest() {
    when(weightageConfigurationRepository.findByOrgIdAndTypeAndKey(
            anyString(), anyString(), anyString()))
        .thenThrow(NullPointerException.class);

    assertThrows(
        PromiseEngineException.class,
        () -> {
          weightageConfigurationDomain.getWeightageConfiguration(ORG_ID, TYPE, KEYS.get(0));
        });
  }

  @Test
  void getWeightageConfigurationsByKeyTest() throws PromiseEngineException {
    WeightageConfiguration weightageConfiguration = testUtil.getWeightageConfiguration();

    when(weightageConfigurationRepository.findByKey(anyString()))
        .thenReturn(Collections.singletonList(weightageConfiguration));

    List<WeightageConfiguration> received_entity =
        weightageConfigurationDomain.getWeightageConfigurationsByKey(KEYS.get(0));
    assertEquals(weightageConfiguration, received_entity.get(0));
  }

  @Test
  void getWeightageConfigurationsByKeyExceptionTest() {
    when(weightageConfigurationRepository.findByKey(anyString()))
        .thenThrow(NullPointerException.class);

    assertThrows(
        PromiseEngineException.class,
        () -> {
          weightageConfigurationDomain.getWeightageConfigurationsByKey(KEYS.get(0));
        });
  }

  @Test
  void updateWeightageConfigurationTest() throws PromiseEngineException {
    WeightageConfiguration weightageConfiguration = testUtil.getWeightageConfiguration();

    when(weightageConfigurationRepository.save(any(WeightageConfiguration.class)))
        .thenReturn(weightageConfiguration);

    WeightageConfiguration received_entity =
        weightageConfigurationDomain.saveWeightageConfiguration(weightageConfiguration);
    assertEquals(weightageConfiguration, received_entity);
  }

  @Test
  void updateWeightageConfigurationExceptionTest() {
    WeightageConfiguration weightageConfiguration = testUtil.getWeightageConfiguration();

    when(weightageConfigurationRepository.save(any(WeightageConfiguration.class)))
        .thenThrow(NullPointerException.class);

    assertThrows(
        PromiseEngineException.class,
        () -> {
          weightageConfigurationDomain.saveWeightageConfiguration(weightageConfiguration);
        });
  }

  @Test
  void deleteWeightageConfigurationTest() throws PromiseEngineException {
    WeightageConfiguration weightageConfiguration = testUtil.getWeightageConfiguration();

    when(weightageConfigurationRepository.findByOrgIdAndTypeAndKey(
            anyString(), anyString(), anyString()))
        .thenReturn(weightageConfiguration);

    WeightageConfiguration received_entity =
        weightageConfigurationDomain.deleteWeightageConfiguration(weightageConfiguration);
    assertEquals(weightageConfiguration, received_entity);
  }
}
