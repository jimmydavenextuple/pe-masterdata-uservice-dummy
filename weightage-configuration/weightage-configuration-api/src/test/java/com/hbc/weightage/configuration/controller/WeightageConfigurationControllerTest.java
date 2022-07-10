package com.hbc.weightage.configuration.controller;

import static com.hbc.weightage.configuration.utils.WeightageConfigurationConstants.KEYS;
import static com.hbc.weightage.configuration.utils.WeightageConfigurationConstants.ORG_ID;
import static com.hbc.weightage.configuration.utils.WeightageConfigurationConstants.STATUS_CODE;
import static com.hbc.weightage.configuration.utils.WeightageConfigurationConstants.TYPE;
import static com.hbc.weightage.configuration.utils.WeightageConfigurationConstants.WEIGHTAGE_CONFIGURATION_SUCCESSFULLY_CREATED;
import static com.hbc.weightage.configuration.utils.WeightageConfigurationConstants.WEIGHTAGE_CONFIGURATION_SUCCESSFULLY_DELETED;
import static com.hbc.weightage.configuration.utils.WeightageConfigurationConstants.WEIGHTAGE_CONFIGURATION_SUCCESSFULLY_FETCHED;
import static com.hbc.weightage.configuration.utils.WeightageConfigurationConstants.WEIGHTAGE_CONFIGURATION_SUCCESSFULLY_UPDATED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.hbc.common.exception.PromiseEngineException;
import com.hbc.common.response.BaseResponse;
import com.hbc.weightage.configuration.api.domain.dto.WeightageConfigurationDto;
import com.hbc.weightage.configuration.api.domain.inbound.CreateWeightageConfigurationRequest;
import com.hbc.weightage.configuration.api.domain.inbound.FetchWeightageRequest;
import com.hbc.weightage.configuration.api.domain.inbound.UpdateWeightageConfigurationRequest;
import com.hbc.weightage.configuration.service.WeightageConfigurationService;
import com.hbc.weightage.configuration.TestUtil;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class WeightageConfigurationControllerTest {
  @Mock private WeightageConfigurationService weightageConfigurationService;

  @InjectMocks private WeightageConfigurationController weightageConfigurationController;

  @InjectMocks private TestUtil testUtil;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void fetchWeightageTest() throws PromiseEngineException {
    FetchWeightageRequest fetchWeightageRequest = testUtil.getFetchWeightageRequest();
    Map<String, Float> fetchWeightageResponse = testUtil.getFetchWeightageResponse();
    when(weightageConfigurationService.fetchWeightage(any(FetchWeightageRequest.class)))
        .thenReturn(fetchWeightageResponse);
    ResponseEntity<BaseResponse<Map<String, Float>>> responseEntity =
        weightageConfigurationController.fetchWeightage(fetchWeightageRequest);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode(), STATUS_CODE);
    assertEquals(fetchWeightageResponse, responseEntity.getBody().getPayload());
    verify(weightageConfigurationService, times(1))
        .fetchWeightage(any(FetchWeightageRequest.class));
  }

  @Test
  void fetchWeightageNotFoundTest() throws PromiseEngineException {
    FetchWeightageRequest fetchWeightageRequest = testUtil.getFetchWeightageRequest();
    when(weightageConfigurationService.fetchWeightage(any(FetchWeightageRequest.class)))
        .thenThrow(PromiseEngineException.class);

    assertThrows(
        PromiseEngineException.class,
        () -> {
          weightageConfigurationController.fetchWeightage(fetchWeightageRequest);
        });
    verify(weightageConfigurationService, times(1))
        .fetchWeightage(any(FetchWeightageRequest.class));
  }

  @Test
  void createWeightageConfigurationTest() throws PromiseEngineException {
    CreateWeightageConfigurationRequest createWeightageConfigurationRequest =
        testUtil.getCreateWeightageConfigurationRequest();
    WeightageConfigurationDto weightageConfigurationDto = testUtil.getWeightageConfigurationDto();
    when(weightageConfigurationService.createWeightageConfiguration(
            any(CreateWeightageConfigurationRequest.class)))
        .thenReturn(weightageConfigurationDto);

    ResponseEntity<BaseResponse<WeightageConfigurationDto>> responseEntity =
        weightageConfigurationController.createWeightageConfiguration(
            createWeightageConfigurationRequest);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode(), STATUS_CODE);
    assertEquals(
        weightageConfigurationDto,
        responseEntity.getBody().getPayload(),
        WEIGHTAGE_CONFIGURATION_SUCCESSFULLY_CREATED);
    verify(weightageConfigurationService, times(1))
        .createWeightageConfiguration(any(CreateWeightageConfigurationRequest.class));
  }

  @Test
  void createWeightageConfigurationExceptionTest() throws PromiseEngineException {
    CreateWeightageConfigurationRequest createWeightageConfigurationRequest =
        testUtil.getCreateWeightageConfigurationRequest();
    when(weightageConfigurationService.createWeightageConfiguration(
            any(CreateWeightageConfigurationRequest.class)))
        .thenThrow(PromiseEngineException.class);

    assertThrows(
        PromiseEngineException.class,
        () -> {
          weightageConfigurationController.createWeightageConfiguration(
              createWeightageConfigurationRequest);
        });
    verify(weightageConfigurationService, times(1))
        .createWeightageConfiguration(any(CreateWeightageConfigurationRequest.class));
  }

  @Test
  void getWeightageConfigurationTest() throws PromiseEngineException {
    WeightageConfigurationDto weightageConfigurationDto = testUtil.getWeightageConfigurationDto();
    when(weightageConfigurationService.getWeightageConfiguration(
            anyString(), anyString(), anyString()))
        .thenReturn(weightageConfigurationDto);

    ResponseEntity<BaseResponse<WeightageConfigurationDto>> responseEntity =
        weightageConfigurationController.getWeightageConfiguration(ORG_ID, TYPE, KEYS.get(0));

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode(), STATUS_CODE);
    assertEquals(
        weightageConfigurationDto,
        responseEntity.getBody().getPayload(),
        WEIGHTAGE_CONFIGURATION_SUCCESSFULLY_FETCHED);
    verify(weightageConfigurationService, times(1))
        .getWeightageConfiguration(anyString(), anyString(), anyString());
  }

  @Test
  void getWeightageConfigurationNotFoundTest() throws PromiseEngineException {
    when(weightageConfigurationService.getWeightageConfiguration(
            anyString(), anyString(), anyString()))
        .thenThrow(PromiseEngineException.class);

    assertThrows(
        PromiseEngineException.class,
        () -> {
          weightageConfigurationController.getWeightageConfiguration(ORG_ID, TYPE, KEYS.get(0));
        });
    verify(weightageConfigurationService, times(1))
        .getWeightageConfiguration(anyString(), anyString(), anyString());
  }

  @Test
  void getWeightageConfigurationsByKeyTest() throws PromiseEngineException {
    WeightageConfigurationDto weightageConfigurationDto = testUtil.getWeightageConfigurationDto();
    when(weightageConfigurationService.getWeightageConfigurationsByKey(anyString()))
        .thenReturn(Collections.singletonList(weightageConfigurationDto));

    ResponseEntity<BaseResponse<List<WeightageConfigurationDto>>> responseEntity =
        weightageConfigurationController.getWeightageConfigurationsByKey(KEYS.get(0));

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode(), STATUS_CODE);
    assertEquals(
        weightageConfigurationDto,
        responseEntity.getBody().getPayload().get(0),
        WEIGHTAGE_CONFIGURATION_SUCCESSFULLY_FETCHED);
    verify(weightageConfigurationService, times(1)).getWeightageConfigurationsByKey(anyString());
  }

  @Test
  void getWeightageConfigurationsByKeyNotFoundTest() throws PromiseEngineException {
    when(weightageConfigurationService.getWeightageConfigurationsByKey(anyString()))
        .thenThrow(PromiseEngineException.class);

    assertThrows(
        PromiseEngineException.class,
        () -> {
          weightageConfigurationController.getWeightageConfigurationsByKey(KEYS.get(0));
        });
    verify(weightageConfigurationService, times(1)).getWeightageConfigurationsByKey(anyString());
  }

  @Test
  void updateWeightageConfigurationTest() throws PromiseEngineException {
    WeightageConfigurationDto weightageConfigurationDto = testUtil.getWeightageConfigurationDto();
    UpdateWeightageConfigurationRequest baseRequest =
        testUtil.getUpdateWeightageConfigurationRequest();
    when(weightageConfigurationService.updateWeightageConfiguration(
            anyString(), anyString(), anyString(), any(UpdateWeightageConfigurationRequest.class)))
        .thenReturn(weightageConfigurationDto);

    ResponseEntity<BaseResponse<WeightageConfigurationDto>> responseEntity =
        weightageConfigurationController.updateWeightageConfiguration(
            ORG_ID, TYPE, KEYS.get(0), baseRequest);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode(), STATUS_CODE);
    assertEquals(
        weightageConfigurationDto,
        responseEntity.getBody().getPayload(),
        WEIGHTAGE_CONFIGURATION_SUCCESSFULLY_UPDATED);
    verify(weightageConfigurationService, times(1))
        .updateWeightageConfiguration(
            anyString(), anyString(), anyString(), any(UpdateWeightageConfigurationRequest.class));
  }

  @Test
  void updateWeightageConfigurationExceptionTest() throws PromiseEngineException {
    UpdateWeightageConfigurationRequest baseRequest =
        testUtil.getUpdateWeightageConfigurationRequest();
    when(weightageConfigurationService.updateWeightageConfiguration(
            anyString(), anyString(), anyString(), any(UpdateWeightageConfigurationRequest.class)))
        .thenThrow(PromiseEngineException.class);

    assertThrows(
        PromiseEngineException.class,
        () -> {
          weightageConfigurationController.updateWeightageConfiguration(
              ORG_ID, TYPE, KEYS.get(0), baseRequest);
        });
    verify(weightageConfigurationService, times(1))
        .updateWeightageConfiguration(
            anyString(), anyString(), anyString(), any(UpdateWeightageConfigurationRequest.class));
  }

  @Test
  void deleteWeightageConfigurationTest() throws PromiseEngineException {
    WeightageConfigurationDto weightageConfigurationDto = testUtil.getWeightageConfigurationDto();
    when(weightageConfigurationService.deleteWeightageConfiguration(
            anyString(), anyString(), anyString()))
        .thenReturn(weightageConfigurationDto);

    ResponseEntity<BaseResponse<WeightageConfigurationDto>> responseEntity =
        weightageConfigurationController.deleteWeightageConfiguration(ORG_ID, TYPE, KEYS.get(0));

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode(), STATUS_CODE);
    assertEquals(
        WEIGHTAGE_CONFIGURATION_SUCCESSFULLY_DELETED, responseEntity.getBody().getMessage());
    verify(weightageConfigurationService, times(1))
        .deleteWeightageConfiguration(anyString(), anyString(), anyString());
  }

  @Test
  void deleteWeightageConfigurationExceptionTest() throws PromiseEngineException {
    when(weightageConfigurationService.deleteWeightageConfiguration(
            anyString(), anyString(), anyString()))
        .thenThrow(PromiseEngineException.class);

    assertThrows(
        PromiseEngineException.class,
        () -> {
          weightageConfigurationController.deleteWeightageConfiguration(ORG_ID, TYPE, KEYS.get(0));
        });
    verify(weightageConfigurationService, times(1))
        .deleteWeightageConfiguration(anyString(), anyString(), anyString());
  }
}
