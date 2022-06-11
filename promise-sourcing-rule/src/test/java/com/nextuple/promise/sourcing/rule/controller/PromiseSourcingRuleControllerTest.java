package com.nextuple.promise.sourcing.rule.controller;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.promise.sourcing.rule.domain.dto.PromiseSourcingRuleDto;
import com.nextuple.promise.sourcing.rule.domain.inbound.CreatePromiseSourcingRuleRequest;
import com.nextuple.promise.sourcing.rule.domain.inbound.FetchPromiseSourcingRuleRequest;
import com.nextuple.promise.sourcing.rule.domain.inbound.UpdatePromiseSourcingRuleRequest;
import com.nextuple.promise.sourcing.rule.domain.outbound.FetchPromiseSourcingRuleResponse;
import com.nextuple.promise.sourcing.rule.exception.common.PromiseEngineException;
import com.nextuple.promise.sourcing.rule.service.PromiseSourcingRuleService;
import com.nextuple.promise.sourcing.rule.utils.TestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static com.nextuple.promise.sourcing.rule.utils.PromiseSourcingRuleConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

class PromiseSourcingRuleControllerTest {

  @Mock private PromiseSourcingRuleService promiseSourcingRuleService;
  @InjectMocks private PromiseSourcingRuleController promiseSourcingRuleController;
  @InjectMocks private TestUtil testUtil;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void fetchPromiseSourcingRuleTest() throws PromiseEngineException {
    FetchPromiseSourcingRuleResponse fetchPromiseSourcingRuleResponse =
        testUtil.getFetchPromiseSourcingRuleResponse();
    FetchPromiseSourcingRuleRequest fetchPromiseSourcingRuleRequest =
        testUtil.getFetchPromiseSourcingRuleRequest();

    when(promiseSourcingRuleService.fetchSourcingRule(fetchPromiseSourcingRuleRequest))
        .thenReturn(fetchPromiseSourcingRuleResponse);

    ResponseEntity<BaseResponse<FetchPromiseSourcingRuleResponse>> responseEntity =
        promiseSourcingRuleController.fetchSourcingRule(fetchPromiseSourcingRuleRequest);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode(), STATUS_CODE);
    assertEquals(
        fetchPromiseSourcingRuleResponse,
        responseEntity.getBody().getPayload(),
        PROMISE_SOURCING_RULE_SUCCESSFULLY_FETCHED);
    verify(promiseSourcingRuleService, times(1))
        .fetchSourcingRule(any(FetchPromiseSourcingRuleRequest.class));
  }

  @Test
  void fetchPromiseSourcingRuleNotFoundTest() throws PromiseEngineException {
    FetchPromiseSourcingRuleRequest fetchPromiseSourcingRuleRequest =
        testUtil.getFetchPromiseSourcingRuleRequest();
    when(promiseSourcingRuleService.fetchSourcingRule(any(FetchPromiseSourcingRuleRequest.class)))
        .thenThrow(PromiseEngineException.class);

    assertThrows(
        PromiseEngineException.class,
        () -> {
          promiseSourcingRuleController.fetchSourcingRule(fetchPromiseSourcingRuleRequest);
        });
    verify(promiseSourcingRuleService, times(1))
        .fetchSourcingRule(any(FetchPromiseSourcingRuleRequest.class));
  }

  @Test
  void createPromiseSourcingRuleTest() throws PromiseEngineException {
    CreatePromiseSourcingRuleRequest createPromiseSourcingRuleRequest =
        testUtil.getPromiseSourcingRuleCreationRequest();
    PromiseSourcingRuleDto promiseSourcingRuleDto = testUtil.getPromiseSourcingRuleDto();
    when(promiseSourcingRuleService.createPromiseSourcingRule(
            any(CreatePromiseSourcingRuleRequest.class)))
        .thenReturn(promiseSourcingRuleDto);

    ResponseEntity<BaseResponse<PromiseSourcingRuleDto>> responseEntity =
        promiseSourcingRuleController.createPromiseSourcingRule(createPromiseSourcingRuleRequest);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode(), STATUS_CODE);
    assertEquals(
        promiseSourcingRuleDto,
        responseEntity.getBody().getPayload(),
        PROMISE_SOURCING_RULE_SUCCESSFULLY_CREATED);

    verify(promiseSourcingRuleService, times(1))
        .createPromiseSourcingRule(any(CreatePromiseSourcingRuleRequest.class));
  }

  @Test
  void createPromiseSourcingRuleExceptionTest() throws PromiseEngineException {
    CreatePromiseSourcingRuleRequest createPromiseSourcingRuleRequest =
        testUtil.getPromiseSourcingRuleCreationRequest();
    when(promiseSourcingRuleService.createPromiseSourcingRule(
            any(CreatePromiseSourcingRuleRequest.class)))
        .thenThrow(PromiseEngineException.class);

    assertThrows(
        PromiseEngineException.class,
        () -> {
          promiseSourcingRuleController.createPromiseSourcingRule(createPromiseSourcingRuleRequest);
        });

    verify(promiseSourcingRuleService, times(1))
        .createPromiseSourcingRule(any(CreatePromiseSourcingRuleRequest.class));
  }

  @Test
  void getPromiseSourcingRuleTest() throws PromiseEngineException {
    PromiseSourcingRuleDto dto = testUtil.getPromiseSourcingRuleDto();
    when(promiseSourcingRuleService.getPromiseSourcingRule(
            anyString(), anyString(), anyString(), anyString(), anyInt()))
        .thenReturn(dto);

    ResponseEntity<BaseResponse<PromiseSourcingRuleDto>> responseEntity =
        promiseSourcingRuleController.getPromiseSourcingRule(
            ORG_ID, SERVICE_OPTION, DESTINATION_GEO_ZONE, ALLOCATION_RULE_ID, PRIORITY);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode(), STATUS_CODE);
    assertEquals(
        dto, responseEntity.getBody().getPayload(), PROMISE_SOURCING_RULE_SUCCESSFULLY_FETCHED);
    verify(promiseSourcingRuleService, times(1))
        .getPromiseSourcingRule(anyString(), anyString(), anyString(), anyString(), anyInt());
  }

  @Test
  void getPromiseSourcingRuleNotFoundTest() throws PromiseEngineException {
    when(promiseSourcingRuleService.getPromiseSourcingRule(
            anyString(), anyString(), anyString(), anyString(), anyInt()))
        .thenThrow(PromiseEngineException.class);

    assertThrows(
        PromiseEngineException.class,
        () -> {
          promiseSourcingRuleController.getPromiseSourcingRule(
              ORG_ID, SERVICE_OPTION, DESTINATION_GEO_ZONE, ALLOCATION_RULE_ID, PRIORITY);
        });
    verify(promiseSourcingRuleService, times(1))
        .getPromiseSourcingRule(anyString(), anyString(), anyString(), anyString(), anyInt());
  }

  @Test
  void getPromiseSourcingRuleByOrgIdTest() throws PromiseEngineException {
    List<PromiseSourcingRuleDto> dto = testUtil.getPromiseSourcingRuleByOrgId();
    when(promiseSourcingRuleService.getPromiseSourcingRulesByOrgId(anyString())).thenReturn(dto);

    ResponseEntity<BaseResponse<List<PromiseSourcingRuleDto>>> responseEntity =
        promiseSourcingRuleController.getPromiseSourcingRulesByOrgId(ORG_ID);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode(), STATUS_CODE);
    assertEquals(
        dto, responseEntity.getBody().getPayload(), PROMISE_SOURCING_RULE_SUCCESSFULLY_FETCHED);
    verify(promiseSourcingRuleService, times(1)).getPromiseSourcingRulesByOrgId(anyString());
  }

  @Test
  void getPromiseSourcingRuleByOrgIdNotFoundTest() throws PromiseEngineException {
    when(promiseSourcingRuleService.getPromiseSourcingRulesByOrgId(anyString()))
        .thenThrow(PromiseEngineException.class);

    assertThrows(
        PromiseEngineException.class,
        () -> {
          promiseSourcingRuleController.getPromiseSourcingRulesByOrgId(ORG_ID);
        });
    verify(promiseSourcingRuleService, times(1)).getPromiseSourcingRulesByOrgId(anyString());
  }

  @Test
  void getPromiseSourcingRulesByPriorityTest() throws PromiseEngineException {
    List<PromiseSourcingRuleDto> dto = testUtil.getPromiseSourcingRuleByPriority();
    when(promiseSourcingRuleService.getPromiseSourcingRulesByPriority(anyInt())).thenReturn(dto);

    ResponseEntity<BaseResponse<List<PromiseSourcingRuleDto>>> responseEntity =
        promiseSourcingRuleController.getPromiseSourcingRulesByPriority(PRIORITY);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode(), STATUS_CODE);
    assertEquals(
        dto, responseEntity.getBody().getPayload(), PROMISE_SOURCING_RULE_SUCCESSFULLY_FETCHED);
    verify(promiseSourcingRuleService, times(1)).getPromiseSourcingRulesByPriority(anyInt());
  }

  @Test
  void getPromiseSourcingRuleByPriorityNotFoundTest() throws PromiseEngineException {
    when(promiseSourcingRuleService.getPromiseSourcingRulesByPriority(anyInt()))
        .thenThrow(PromiseEngineException.class);

    assertThrows(
        PromiseEngineException.class,
        () -> {
          promiseSourcingRuleController.getPromiseSourcingRulesByPriority(PRIORITY);
        });
    verify(promiseSourcingRuleService, times(1)).getPromiseSourcingRulesByPriority(anyInt());
  }

  @Test
  void updatePromiseSourcingRuleTest() throws PromiseEngineException {
    PromiseSourcingRuleDto dto = testUtil.getPromiseSourcingRuleDto();
    UpdatePromiseSourcingRuleRequest baseRequest = testUtil.getUpdatePromiseSourcingRuleRequest();
    when(promiseSourcingRuleService.updatePromiseSourcingRule(
            anyString(),
            anyString(),
            anyString(),
            anyString(),
            anyInt(),
            any(UpdatePromiseSourcingRuleRequest.class)))
        .thenReturn(dto);

    ResponseEntity<BaseResponse<PromiseSourcingRuleDto>> responseEntity =
        promiseSourcingRuleController.updatePromiseSourcingRule(
            ORG_ID,
            SERVICE_OPTION,
            DESTINATION_GEO_ZONE,
            ALLOCATION_RULE_ID,
            PRIORITY,
            baseRequest);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode(), STATUS_CODE);
    assertEquals(
        dto, responseEntity.getBody().getPayload(), PROMISE_SOURCING_RULE_SUCCESSFULLY_UPDATED);
    verify(promiseSourcingRuleService, times(1))
        .updatePromiseSourcingRule(
            anyString(),
            anyString(),
            anyString(),
            anyString(),
            anyInt(),
            any(UpdatePromiseSourcingRuleRequest.class));
  }

  @Test
  void updatePromiseSourcingRuleExceptionTest() throws PromiseEngineException {
    UpdatePromiseSourcingRuleRequest baseRequest = testUtil.getUpdatePromiseSourcingRuleRequest();
    when(promiseSourcingRuleService.updatePromiseSourcingRule(
            anyString(),
            anyString(),
            anyString(),
            anyString(),
            anyInt(),
            any(UpdatePromiseSourcingRuleRequest.class)))
        .thenThrow(PromiseEngineException.class);

    assertThrows(
        PromiseEngineException.class,
        () -> {
          promiseSourcingRuleController.updatePromiseSourcingRule(
              ORG_ID,
              SERVICE_OPTION,
              DESTINATION_GEO_ZONE,
              ALLOCATION_RULE_ID,
              PRIORITY,
              baseRequest);
        });
    verify(promiseSourcingRuleService, times(1))
        .updatePromiseSourcingRule(
            anyString(),
            anyString(),
            anyString(),
            anyString(),
            anyInt(),
            any(UpdatePromiseSourcingRuleRequest.class));
  }

  @Test
  void deletePromiseSourcingRuleTest() throws PromiseEngineException {
    PromiseSourcingRuleDto dto = testUtil.getPromiseSourcingRuleDto();
    when(promiseSourcingRuleService.deletePromiseSourcingRule(
            anyString(), anyString(), anyString(), anyString(), anyInt()))
        .thenReturn(dto);

    ResponseEntity<BaseResponse<PromiseSourcingRuleDto>> responseEntity =
        promiseSourcingRuleController.deletePromiseSourcingRule(
            ORG_ID, SERVICE_OPTION, DESTINATION_GEO_ZONE, ALLOCATION_RULE_ID, PRIORITY);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode(), STATUS_CODE);
    assertEquals(PROMISE_SOURCING_RULE_SUCCESSFULLY_DELETED, responseEntity.getBody().getMessage());
    verify(promiseSourcingRuleService, times(1))
        .deletePromiseSourcingRule(anyString(), anyString(), anyString(), anyString(), anyInt());
  }

  @Test
  void deletePromiseSourcingRuleExceptionTest() throws PromiseEngineException {
    when(promiseSourcingRuleService.deletePromiseSourcingRule(
            anyString(), anyString(), anyString(), anyString(), anyInt()))
        .thenThrow(PromiseEngineException.class);

    assertThrows(
        PromiseEngineException.class,
        () -> {
          promiseSourcingRuleController.deletePromiseSourcingRule(
              ORG_ID, SERVICE_OPTION, DESTINATION_GEO_ZONE, ALLOCATION_RULE_ID, PRIORITY);
        });
    verify(promiseSourcingRuleService, times(1))
        .deletePromiseSourcingRule(anyString(), anyString(), anyString(), anyString(), anyInt());
  }
}
