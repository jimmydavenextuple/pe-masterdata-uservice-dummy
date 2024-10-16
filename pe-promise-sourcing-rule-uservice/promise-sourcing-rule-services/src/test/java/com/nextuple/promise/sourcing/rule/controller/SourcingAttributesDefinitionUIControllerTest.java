package com.nextuple.promise.sourcing.rule.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.promise.sourcing.rule.TestUtil;
import com.nextuple.promise.sourcing.rule.api.domain.enums.SourcingAttributesDefinitionScopeEnum;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.SourcingAttributeDefinitionUIResponse;
import com.nextuple.promise.sourcing.rule.service.SourcingAttributesDefinitionService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

class SourcingAttributesDefinitionUIControllerTest {
  @InjectMocks SourcingAttributesDefinitionUIController controller;
  @InjectMocks TestUtil testUtil;
  @Mock SourcingAttributesDefinitionService sourcingAttributesDefinitionService;

  @BeforeEach
  void init() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void getReqAndOptAttributesByOrgIdTest() throws CommonServiceException, PromiseEngineException {
    when(sourcingAttributesDefinitionService.processGetActiveSourcingAttributeDefinitionForUI(
            any(), any()))
        .thenReturn(testUtil.getSourcingAttributeDefinitionUIResponse());
    ResponseEntity<BaseResponse<SourcingAttributeDefinitionUIResponse>> response =
        controller.getReqAndOptAttributesByOrgId(
            TestUtil.ORG_ID, SourcingAttributesDefinitionScopeEnum.SOURCING_RULE);
    Assertions.assertNotNull(response);
  }

  @Test
  void getReqAndOptAttributesByOrgIdTestWhenScopeIsOPTIMIZATION()
      throws CommonServiceException, PromiseEngineException {
    when(sourcingAttributesDefinitionService.processGetActiveSourcingAttributeDefinitionForUI(
            any(), any()))
        .thenReturn(testUtil.getSourcingAttributeDefinitionUIResponse());
    ResponseEntity<BaseResponse<SourcingAttributeDefinitionUIResponse>> response =
        controller.getReqAndOptAttributesByOrgId(
            TestUtil.ORG_ID, SourcingAttributesDefinitionScopeEnum.OPTIMIZATION);
    Assertions.assertNotNull(response);
  }

  @Test
  void getReqAndOptAttributesByOrgIdExceptionTest()
      throws CommonServiceException, PromiseEngineException {
    when(sourcingAttributesDefinitionService.processGetActiveSourcingAttributeDefinitionForUI(
            any(), any()))
        .thenThrow(new RuntimeException("Error"));
    RuntimeException r =
        Assertions.assertThrows(
            RuntimeException.class,
            () -> {
              controller.getReqAndOptAttributesByOrgId(
                  TestUtil.ORG_ID, SourcingAttributesDefinitionScopeEnum.SOURCING_RULE);
            });
  }
}
