package com.nextuple.weightage.configuration.service;

import static com.nextuple.weightage.configuration.utils.WeightageConfigurationConstants.KEYS;
import static com.nextuple.weightage.configuration.utils.WeightageConfigurationConstants.ORG_ID;
import static com.nextuple.weightage.configuration.utils.WeightageConfigurationConstants.TYPE;
import static com.nextuple.weightage.configuration.utils.WeightageConfigurationConstants.WEIGHTAGE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.nextuple.weightage.configuration.api.domain.dto.WeightageConfigurationDto;
import com.nextuple.weightage.configuration.api.domain.inbound.CreateWeightageConfigurationRequest;
import com.nextuple.weightage.configuration.api.domain.inbound.FetchWeightageRequest;
import com.nextuple.weightage.configuration.api.domain.inbound.UpdateWeightageConfigurationRequest;
import com.nextuple.weightage.configuration.domain.WeightageConfigurationDomain;
import com.nextuple.weightage.configuration.domain.entity.WeightageConfiguration;
import com.nextuple.weightage.configuration.domain.mapper.WeightageConfigurationMapper;
import com.nextuple.weightage.configuration.exception.common.PromiseEngineException;
import com.nextuple.weightage.configuration.utils.TestUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class WeightageConfigurationServiceTest {
  @InjectMocks private WeightageConfigurationService weightageConfigurationService;
  @Mock private WeightageConfigurationDomain weightageConfigurationDomain;
  @InjectMocks private TestUtil testUtil;

  private static final WeightageConfigurationMapper INSTANCE =
      Mappers.getMapper(WeightageConfigurationMapper.class);

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void fetchWeightageTest() throws PromiseEngineException {
    List<WeightageConfiguration> weightageConfigurationList =
        Collections.singletonList(testUtil.getWeightageConfiguration());
    FetchWeightageRequest fetchWeightageRequest = testUtil.getFetchWeightageRequest();

    when(weightageConfigurationDomain.fetchWeightage(any(FetchWeightageRequest.class)))
        .thenReturn(weightageConfigurationList);

    Map<String, Float> fetchWeightageResponse =
        weightageConfigurationService.fetchWeightage(fetchWeightageRequest);
    assertTrue(fetchWeightageResponse.containsKey(KEYS.get(0)));
    assertTrue(fetchWeightageResponse.containsValue(WEIGHTAGE));
    verify(weightageConfigurationDomain, times(1)).fetchWeightage(any(FetchWeightageRequest.class));
  }

  @Test
  void fetchWeightageNotFoundTest() throws PromiseEngineException {
    FetchWeightageRequest fetchWeightageRequest = testUtil.getFetchWeightageRequest();

    when(weightageConfigurationDomain.fetchWeightage(any(FetchWeightageRequest.class)))
        .thenReturn(new ArrayList<>());

    assertThrows(
        PromiseEngineException.class,
        () -> {
          weightageConfigurationService.fetchWeightage(fetchWeightageRequest);
        });
    verify(weightageConfigurationDomain, times(1)).fetchWeightage(any(FetchWeightageRequest.class));
  }

  @Test
  void createWeightageConfigurationTest() throws PromiseEngineException {
    WeightageConfiguration weightageConfiguration = testUtil.getWeightageConfiguration();
    CreateWeightageConfigurationRequest createWeightageConfigurationRequest =
        testUtil.getCreateWeightageConfigurationRequest();

    when(weightageConfigurationDomain.saveWeightageConfiguration(any(WeightageConfiguration.class)))
        .thenReturn(weightageConfiguration);

    WeightageConfigurationDto received_dto =
        weightageConfigurationService.createWeightageConfiguration(
            createWeightageConfigurationRequest);
    assertEquals(createWeightageConfigurationRequest.getWeightage(), received_dto.getWeightage());
    verify(weightageConfigurationDomain, times(1))
        .saveWeightageConfiguration(any(WeightageConfiguration.class));
  }

  @Test
  void getWeightageConfigurationTest() throws PromiseEngineException {
    WeightageConfiguration weightageConfiguration = testUtil.getWeightageConfiguration();

    when(weightageConfigurationDomain.getWeightageConfiguration(
            anyString(), anyString(), anyString()))
        .thenReturn(weightageConfiguration);

    WeightageConfigurationDto received_dto =
        weightageConfigurationService.getWeightageConfiguration(ORG_ID, TYPE, KEYS.get(0));
    assertEquals(received_dto.getOrgId(), weightageConfiguration.getOrgId());
    verify(weightageConfigurationDomain, times(1))
        .getWeightageConfiguration(anyString(), anyString(), anyString());
  }

  @Test
  void getWeightageConfigurationNotFoundTest() throws PromiseEngineException {
    when(weightageConfigurationDomain.getWeightageConfiguration(
            anyString(), anyString(), anyString()))
        .thenReturn(null);

    assertThrows(
        PromiseEngineException.class,
        () -> {
          weightageConfigurationService.getWeightageConfiguration(ORG_ID, TYPE, KEYS.get(0));
        });
    verify(weightageConfigurationDomain, times(1))
        .getWeightageConfiguration(anyString(), anyString(), anyString());
  }

  @Test
  void getWeightageConfigurationsByKey() throws PromiseEngineException {
    WeightageConfiguration weightageConfiguration = testUtil.getWeightageConfiguration();

    when(weightageConfigurationDomain.getWeightageConfigurationsByKey(anyString()))
        .thenReturn(Collections.singletonList(weightageConfiguration));

    List<WeightageConfigurationDto> received_dto =
        weightageConfigurationService.getWeightageConfigurationsByKey(KEYS.get(0));
    assertEquals(received_dto.get(0).getOrgId(), weightageConfiguration.getOrgId());
    verify(weightageConfigurationDomain, times(1)).getWeightageConfigurationsByKey(anyString());
  }

  @Test
  void updateWeightageConfigurationTest() throws PromiseEngineException {
    WeightageConfiguration weightageConfiguration = testUtil.getWeightageConfiguration();
    UpdateWeightageConfigurationRequest baseRequest =
        testUtil.getUpdateWeightageConfigurationRequest();

    when(weightageConfigurationDomain.getWeightageConfiguration(
            anyString(), anyString(), anyString()))
        .thenReturn(weightageConfiguration);
    when(weightageConfigurationDomain.saveWeightageConfiguration(any(WeightageConfiguration.class)))
        .thenReturn(weightageConfiguration);

    WeightageConfigurationDto updated_weightageConfigurationDto =
        weightageConfigurationService.updateWeightageConfiguration(
            ORG_ID, TYPE, KEYS.get(0), baseRequest);
    assertEquals(baseRequest.getWeightage(), updated_weightageConfigurationDto.getWeightage());
    assertEquals(baseRequest.getType(), updated_weightageConfigurationDto.getType());
    verify(weightageConfigurationDomain, times(1))
        .getWeightageConfiguration(anyString(), anyString(), anyString());
    verify(weightageConfigurationDomain, times(1))
        .saveWeightageConfiguration(any(WeightageConfiguration.class));
  }

  @Test
  void deleteWeightageConfigurationTest() throws PromiseEngineException {
    WeightageConfiguration weightageConfiguration = testUtil.getWeightageConfiguration();

    when(weightageConfigurationDomain.getWeightageConfiguration(
            anyString(), anyString(), anyString()))
        .thenReturn(weightageConfiguration);
    when(weightageConfigurationDomain.deleteWeightageConfiguration(
            any(WeightageConfiguration.class)))
        .thenReturn(weightageConfiguration);

    WeightageConfigurationDto deleted_weightageConfigurationDto =
        weightageConfigurationService.deleteWeightageConfiguration(ORG_ID, TYPE, KEYS.get(0));
    assertEquals(weightageConfiguration.getOrgId(), deleted_weightageConfigurationDto.getOrgId());
  }
}
