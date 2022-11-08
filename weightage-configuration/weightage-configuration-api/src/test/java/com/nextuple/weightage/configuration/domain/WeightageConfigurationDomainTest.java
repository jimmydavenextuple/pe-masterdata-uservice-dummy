package com.nextuple.weightage.configuration.domain;

import static com.nextuple.weightage.configuration.utils.WeightageConfigurationConstants.KEYS;
import static com.nextuple.weightage.configuration.utils.WeightageConfigurationConstants.ORG_ID;
import static com.nextuple.weightage.configuration.utils.WeightageConfigurationConstants.TYPE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.weightage.configuration.TestUtil;
import com.nextuple.weightage.configuration.api.domain.inbound.FetchWeightageRequest;
import com.nextuple.weightage.configuration.domain.entity.WeightageConfiguration;
import com.nextuple.weightage.configuration.domain.repository.WeightageConfigurationRepository;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Assertions;
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

  @Test
  void getAllWeightageConfigurationTest() throws PromiseEngineException {
    List<WeightageConfiguration> weightageConfigurationList =
        testUtil.getWeightageConfigurationList();

    when(weightageConfigurationRepository.findAllRecords(any()))
        .thenReturn(weightageConfigurationList);

    List<WeightageConfiguration> response =
        weightageConfigurationDomain.getAllWeightageConfiguration(2);
    assertEquals(2, response.size());
    assertEquals(weightageConfigurationList.get(0).getType(), response.get(0).getType());
    verify(weightageConfigurationRepository, times(1)).findAllRecords(any());
  }

  @Test
  void getAllWeightageConfigurationExceptionTest() {
    when(weightageConfigurationRepository.findAllRecords(any()))
        .thenThrow(new RuntimeException("Unable to fetch all Weightage Configuration records"));

    Exception exception =
        assertThrows(
            PromiseEngineException.class,
            () -> weightageConfigurationDomain.getAllWeightageConfiguration(2));

    Assertions.assertEquals(
        "Unable to fetch all Weightage Configuration records", exception.getMessage());
    verify(weightageConfigurationRepository, times(1)).findAllRecords(any());
  }
}
