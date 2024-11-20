/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.promise.sourcing.rule.service;

import static com.nextuple.promise.sourcing.rule.utils.PromiseSourcingRuleConstants.ALLOCATION_RULE_ID;
import static com.nextuple.promise.sourcing.rule.utils.PromiseSourcingRuleConstants.DESTINATION_GEO_ZONE;
import static com.nextuple.promise.sourcing.rule.utils.PromiseSourcingRuleConstants.EXPRESS;
import static com.nextuple.promise.sourcing.rule.utils.PromiseSourcingRuleConstants.NEXTDAY;
import static com.nextuple.promise.sourcing.rule.utils.PromiseSourcingRuleConstants.ORG_ID;
import static com.nextuple.promise.sourcing.rule.utils.PromiseSourcingRuleConstants.PRIORITY;
import static com.nextuple.promise.sourcing.rule.utils.PromiseSourcingRuleConstants.SDND;
import static com.nextuple.promise.sourcing.rule.utils.PromiseSourcingRuleConstants.SERVICE_OPTION;
import static com.nextuple.promise.sourcing.rule.utils.PromiseSourcingRuleConstants.STANDARD;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.node.domain.feign.NodeFeign;
import com.nextuple.postal.code.timezone.api.domain.feign.PostalCodeFeign;
import com.nextuple.promise.sourcing.rule.TestUtil;
import com.nextuple.promise.sourcing.rule.api.domain.dto.PromiseSourcingRuleDto;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.CreatePromiseSourcingRuleRequest;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.FetchPromiseSourcingRuleRequest;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.UpdatePromiseSourcingRuleRequest;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.FetchPromiseSourcingRuleResponse;
import com.nextuple.promise.sourcing.rule.persistence.domain.PromiseSourcingRuleDomainDto;
import com.nextuple.promise.sourcing.rule.persistence.service.PromiseSourcingRulePersistenceService;
import com.nextuple.promise.sourcing.rule.utils.PromiseSourcingRuleConstants;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

class PromiseSourcingRuleServiceTest {
  @InjectMocks private PromiseSourcingRuleService promiseSourcingRuleService;
  @Mock private PromiseSourcingRulePersistenceService promiseSourcingRulePersistenceService;
  @Mock private NodeFeign nodeFeign;
  @Mock private PostalCodeFeign postalCodeFeign;
  @InjectMocks private PromiseSourcingRuleConstants promiseSourcingRuleConstants;

  @InjectMocks private TestUtil testUtil;

  @BeforeEach
  void setUp() {
    Set<String> serviceOptions = Set.of("SDND", "EXPRESS", "STANDARD");
    MockitoAnnotations.openMocks(this);
    ReflectionTestUtils.setField(promiseSourcingRuleService, "serviceOptions", serviceOptions);
  }

  @Test
  void fetchPromiseSourcingRuleTest() throws PromiseEngineException {
    List<PromiseSourcingRuleDomainDto> promiseSourcingRuleList =
        testUtil.getPromiseSourcingRuleList();
    FetchPromiseSourcingRuleRequest fetchPromiseSourcingRuleRequest =
        testUtil.getFetchPromiseSourcingRuleRequest();

    when(promiseSourcingRulePersistenceService.fetchSourcingRule(
            any(FetchPromiseSourcingRuleRequest.class)))
        .thenReturn(promiseSourcingRuleList);

    FetchPromiseSourcingRuleResponse fetchPromiseSourcingRuleResponse =
        promiseSourcingRuleService.fetchSourcingRule(fetchPromiseSourcingRuleRequest);
    assertEquals(
        Set.of("Node-1"),
        fetchPromiseSourcingRuleResponse
            .getServiceOptionSourcingRules()
            .get(SDND)
            .get(0)
            .getSourceNodes());
    assertEquals(
        Set.of("Node-2"),
        fetchPromiseSourcingRuleResponse
            .getServiceOptionSourcingRules()
            .get(STANDARD)
            .get(0)
            .getSourceNodes());
    assertEquals(
        Set.of("Node-3"),
        fetchPromiseSourcingRuleResponse
            .getServiceOptionSourcingRules()
            .get(EXPRESS)
            .get(0)
            .getSourceNodes());
    assertEquals(
        Set.of("Node-4"),
        fetchPromiseSourcingRuleResponse
            .getServiceOptionSourcingRules()
            .get(NEXTDAY)
            .get(0)
            .getSourceNodes());
    verify(promiseSourcingRulePersistenceService, times(1)).fetchSourcingRule(any());
  }

  @Test
  void fetchPromiseSourcingRuleNotFoundTest() throws PromiseEngineException {
    FetchPromiseSourcingRuleRequest fetchPromiseSourcingRuleRequest =
        testUtil.getFetchPromiseSourcingRuleRequest();

    when(promiseSourcingRulePersistenceService.fetchSourcingRule(
            any(FetchPromiseSourcingRuleRequest.class)))
        .thenReturn(new ArrayList<>());

    assertThrows(
        PromiseEngineException.class,
        () -> {
          promiseSourcingRuleService.fetchSourcingRule(fetchPromiseSourcingRuleRequest);
        });
    verify(promiseSourcingRulePersistenceService, times(1))
        .fetchSourcingRule(any(FetchPromiseSourcingRuleRequest.class));
  }

  @Test
  void createPromiseSourcingRuleTest() throws PromiseEngineException, CommonServiceException {
    PromiseSourcingRuleDomainDto promiseSourcingRule = testUtil.getPromiseSourcingRule();
    CreatePromiseSourcingRuleRequest createPromiseSourcingRuleRequest =
        testUtil.getPromiseSourcingRuleCreationRequest();
    when(nodeFeign.getNodeDetails(any(), any())).thenReturn(testUtil.getBaseResponseOfNode());
    when(postalCodeFeign.getByPostalCodePrefix(anyString(), anyString()))
        .thenReturn(testUtil.getPostalCodeResponse());
    when(promiseSourcingRulePersistenceService.savePromiseSourcingRule(
            any(PromiseSourcingRuleDomainDto.class)))
        .thenReturn(promiseSourcingRule);

    PromiseSourcingRuleDto received_dto =
        promiseSourcingRuleService.createPromiseSourcingRule(createPromiseSourcingRuleRequest);
    assertEquals(createPromiseSourcingRuleRequest.getOrgId(), received_dto.getOrgId());
    verify(promiseSourcingRulePersistenceService, times(1))
        .savePromiseSourcingRule(any(PromiseSourcingRuleDomainDto.class));
  }

  @Test
  void createPromiseSourcingRuleInvalidDestinationGeozoneExceptionTest()
      throws PromiseEngineException, CommonServiceException {
    PromiseSourcingRuleDomainDto promiseSourcingRule = testUtil.getPromiseSourcingRule();
    CreatePromiseSourcingRuleRequest createPromiseSourcingRuleRequest =
        testUtil.getPromiseSourcingRuleCreationRequest();
    when(nodeFeign.getNodeDetails(any(), any())).thenReturn(testUtil.getBaseResponseOfNode());
    when(postalCodeFeign.getByPostalCodePrefix(anyString(), anyString()))
        .thenReturn(testUtil.getEmptyPostalCodeResponse());
    Exception ex =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                promiseSourcingRuleService.createPromiseSourcingRule(
                    createPromiseSourcingRuleRequest));
    assertEquals("DestinationGeoZone is not valid", ex.getMessage());
  }

  @Test
  void createPromiseSourcingRuleInvalidNodeExceptionTest()
      throws PromiseEngineException, CommonServiceException {
    PromiseSourcingRuleDomainDto promiseSourcingRule = testUtil.getPromiseSourcingRule();
    CreatePromiseSourcingRuleRequest createPromiseSourcingRuleRequest =
        testUtil.getPromiseSourcingRuleCreationRequest();
    when(nodeFeign.getNodeDetails(any(), any()))
        .thenThrow(new RuntimeException("Node not found with given details"));
    Exception ex =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                promiseSourcingRuleService.createPromiseSourcingRule(
                    createPromiseSourcingRuleRequest));
    assertEquals("Node not found with given details", ex.getMessage());
  }

  @Test
  void createPromiseSourcingRuleInactiveNodeExceptionTest()
      throws PromiseEngineException, CommonServiceException {
    PromiseSourcingRuleDomainDto promiseSourcingRule = testUtil.getPromiseSourcingRule();
    CreatePromiseSourcingRuleRequest createPromiseSourcingRuleRequest =
        testUtil.getPromiseSourcingRuleCreationRequest();
    when(nodeFeign.getNodeDetails(any(), any())).thenReturn(testUtil.getBaseResponseOfNode2());
    Exception ex =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                promiseSourcingRuleService.createPromiseSourcingRule(
                    createPromiseSourcingRuleRequest));
    assertEquals("nodeId is not active", ex.getMessage());
  }

  @Test
  void createPromiseSourcingRuleWithoutSourcingRuleIdAndAllocationRuleIdTest()
      throws PromiseEngineException, CommonServiceException {
    PromiseSourcingRuleDomainDto promiseSourcingRule = testUtil.getPromiseSourcingRule();
    CreatePromiseSourcingRuleRequest createPromiseSourcingRuleRequest =
        CreatePromiseSourcingRuleRequest.builder()
            .serviceOption(SDND)
            .sourceNodes(Collections.singleton("Node-1"))
            .destinationGeoZone(DESTINATION_GEO_ZONE)
            .priority(PRIORITY)
            .orgId(ORG_ID)
            .build();
    when(nodeFeign.getNodeDetails(any(), any())).thenReturn(testUtil.getBaseResponseOfNode());
    when(postalCodeFeign.getByPostalCodePrefix(anyString(), anyString()))
        .thenReturn(testUtil.getPostalCodeResponse());
    when(promiseSourcingRulePersistenceService.savePromiseSourcingRule(
            any(PromiseSourcingRuleDomainDto.class)))
        .thenReturn(promiseSourcingRule);

    PromiseSourcingRuleDto received_dto =
        promiseSourcingRuleService.createPromiseSourcingRule(
            testUtil.getPromiseSourcingRuleCreationRequest());
    assertEquals(createPromiseSourcingRuleRequest.getPriority(), received_dto.getPriority());
    verify(promiseSourcingRulePersistenceService, times(1))
        .savePromiseSourcingRule(any(PromiseSourcingRuleDomainDto.class));
  }

  @Test
  void createPromiseSourcingRuleAndSourcingRuleAlreadyExist() throws PromiseEngineException {
    PromiseSourcingRuleDomainDto promiseSourcingRule = testUtil.getPromiseSourcingRule();
    CreatePromiseSourcingRuleRequest createPromiseSourcingRuleRequest =
        CreatePromiseSourcingRuleRequest.builder()
            .serviceOption(SDND)
            .sourceNodes(Collections.singleton("Node-1"))
            .destinationGeoZone(DESTINATION_GEO_ZONE)
            .priority(PRIORITY)
            .orgId(ORG_ID)
            .build();
    when(nodeFeign.getNodeDetails(any(), any())).thenReturn(testUtil.getBaseResponseOfNode());
    when(postalCodeFeign.getByPostalCodePrefix(anyString(), anyString()))
        .thenReturn(testUtil.getPostalCodeResponse());
    when(promiseSourcingRulePersistenceService.fetchSourcingRule(
            anyString(), anyString(), anyString(), anyString()))
        .thenReturn(List.of(promiseSourcingRule));

    Assertions.assertThrows(
        PromiseEngineException.class,
        () ->
            promiseSourcingRuleService.createPromiseSourcingRule(
                testUtil.getPromiseSourcingRuleCreationRequest()));
    verify(promiseSourcingRulePersistenceService, times(0))
        .savePromiseSourcingRule(any(PromiseSourcingRuleDomainDto.class));
  }

  @Test
  void getPromiseSourcingRuleTest() throws PromiseEngineException {
    PromiseSourcingRuleDomainDto promiseSourcingRule = testUtil.getPromiseSourcingRule();
    when(promiseSourcingRulePersistenceService.getPromiseSourcingRule(
            anyString(), anyString(), anyString(), anyString(), anyInt()))
        .thenReturn(promiseSourcingRule);

    PromiseSourcingRuleDto promiseSourcingRuleDto =
        promiseSourcingRuleService.getPromiseSourcingRule(
            ORG_ID, SERVICE_OPTION, DESTINATION_GEO_ZONE, ALLOCATION_RULE_ID, PRIORITY);
    assertEquals(promiseSourcingRuleDto.getOrgId(), promiseSourcingRule.getOrgId());
    verify(promiseSourcingRulePersistenceService, times(1))
        .getPromiseSourcingRule(anyString(), anyString(), anyString(), anyString(), anyInt());
  }

  @Test
  void getPromiseSourcingRuleNotFoundTest() throws PromiseEngineException {
    when(promiseSourcingRulePersistenceService.getPromiseSourcingRule(
            anyString(), anyString(), anyString(), anyString(), anyInt()))
        .thenReturn(null);

    assertThrows(
        PromiseEngineException.class,
        () -> {
          promiseSourcingRuleService.getPromiseSourcingRule(
              ORG_ID, SERVICE_OPTION, DESTINATION_GEO_ZONE, ALLOCATION_RULE_ID, PRIORITY);
        });
    verify(promiseSourcingRulePersistenceService, times(1))
        .getPromiseSourcingRule(anyString(), anyString(), anyString(), anyString(), anyInt());
  }

  @Test
  void getPromiseSourcingRuleByOrgIdTest() throws PromiseEngineException {
    PromiseSourcingRuleDomainDto promiseSourcingRule = testUtil.getPromiseSourcingRule();
    when(promiseSourcingRulePersistenceService.getPromiseSourcingRulesByOrgId(anyString()))
        .thenReturn(Collections.singletonList(promiseSourcingRule));

    List<PromiseSourcingRuleDto> promiseSourcingRuleDto =
        promiseSourcingRuleService.getPromiseSourcingRulesByOrgId(ORG_ID);
    assertEquals(promiseSourcingRuleDto.get(0).getOrgId(), promiseSourcingRule.getOrgId());
    verify(promiseSourcingRulePersistenceService, times(1)).getPromiseSourcingRulesByOrgId(any());
  }

  @Test
  void getPromiseSourcingRuleByPriorityTest() throws PromiseEngineException {
    PromiseSourcingRuleDomainDto promiseSourcingRule = testUtil.getPromiseSourcingRule();
    when(promiseSourcingRulePersistenceService.getPromiseSourcingRulesByPriority(anyInt()))
        .thenReturn(Collections.singletonList(promiseSourcingRule));

    List<PromiseSourcingRuleDto> promiseSourcingRuleDto =
        promiseSourcingRuleService.getPromiseSourcingRulesByPriority(PRIORITY);
    assertEquals(promiseSourcingRuleDto.get(0).getOrgId(), promiseSourcingRule.getOrgId());
    verify(promiseSourcingRulePersistenceService, times(1))
        .getPromiseSourcingRulesByPriority(PRIORITY);
  }

  @Test
  void updatePromiseSourcingRuleTest() throws PromiseEngineException, CommonServiceException {
    PromiseSourcingRuleDomainDto promiseSourcingRule = testUtil.getPromiseSourcingRule();
    UpdatePromiseSourcingRuleRequest updatePromiseSourcingRuleRequest =
        testUtil.getUpdatePromiseSourcingRuleRequest();
    when(nodeFeign.getNodeDetails(any(), any())).thenReturn(testUtil.getBaseResponseOfNode());
    when(postalCodeFeign.getByPostalCodePrefix(anyString(), anyString()))
        .thenReturn(testUtil.getPostalCodeResponse());
    when(promiseSourcingRulePersistenceService.getPromiseSourcingRule(
            anyString(), anyString(), anyString(), anyString(), anyInt()))
        .thenReturn(promiseSourcingRule);
    when(promiseSourcingRulePersistenceService.savePromiseSourcingRule(
            any(PromiseSourcingRuleDomainDto.class)))
        .thenReturn(promiseSourcingRule);

    PromiseSourcingRuleDto updated_promiseSourcingRuleDto =
        promiseSourcingRuleService.updatePromiseSourcingRule(
            ORG_ID,
            SERVICE_OPTION,
            DESTINATION_GEO_ZONE,
            ALLOCATION_RULE_ID,
            PRIORITY,
            updatePromiseSourcingRuleRequest);
    assertEquals(
        updatePromiseSourcingRuleRequest.getSourceNodes(),
        updated_promiseSourcingRuleDto.getSourceNodes());
    verify(promiseSourcingRulePersistenceService, times(1))
        .getPromiseSourcingRule(anyString(), anyString(), anyString(), anyString(), anyInt());
    verify(promiseSourcingRulePersistenceService, times(1))
        .savePromiseSourcingRule(any(PromiseSourcingRuleDomainDto.class));
  }

  @Test
  void updatePromiseSourcingRuleInactiveNodeExceptionTest()
      throws PromiseEngineException, CommonServiceException {
    PromiseSourcingRuleDomainDto promiseSourcingRule = testUtil.getPromiseSourcingRule();
    UpdatePromiseSourcingRuleRequest updatePromiseSourcingRuleRequest =
        testUtil.getUpdatePromiseSourcingRuleRequest();
    when(nodeFeign.getNodeDetails(any(), any())).thenReturn(testUtil.getBaseResponseOfNode2());
    Exception ex =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                promiseSourcingRuleService.updatePromiseSourcingRule(
                    promiseSourcingRuleConstants.ORG_ID,
                    promiseSourcingRuleConstants.SERVICE_OPTION,
                    promiseSourcingRuleConstants.DESTINATION_GEO_ZONE,
                    promiseSourcingRuleConstants.ALLOCATION_RULE_ID,
                    promiseSourcingRuleConstants.PRIORITY,
                    updatePromiseSourcingRuleRequest));
    assertEquals("nodeId is not active", ex.getMessage());
  }

  @Test
  void updatePromiseSourcingRuleInvalidNodeExceptionTest()
      throws PromiseEngineException, CommonServiceException {
    PromiseSourcingRuleDomainDto promiseSourcingRule = testUtil.getPromiseSourcingRule();
    UpdatePromiseSourcingRuleRequest updatePromiseSourcingRuleRequest =
        testUtil.getUpdatePromiseSourcingRuleRequest();
    when(nodeFeign.getNodeDetails(any(), any()))
        .thenThrow(new RuntimeException("Node not found with given details"));
    Exception ex =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                promiseSourcingRuleService.updatePromiseSourcingRule(
                    promiseSourcingRuleConstants.ORG_ID,
                    promiseSourcingRuleConstants.SERVICE_OPTION,
                    promiseSourcingRuleConstants.DESTINATION_GEO_ZONE,
                    promiseSourcingRuleConstants.ALLOCATION_RULE_ID,
                    promiseSourcingRuleConstants.PRIORITY,
                    updatePromiseSourcingRuleRequest));
    assertEquals("Node not found with given details", ex.getMessage());
  }

  @Test
  void updatePromiseSourcingRuleInvalidDestinationGeozoneExceptionTest()
      throws PromiseEngineException, CommonServiceException {
    PromiseSourcingRuleDomainDto promiseSourcingRule = testUtil.getPromiseSourcingRule();
    UpdatePromiseSourcingRuleRequest updatePromiseSourcingRuleRequest =
        testUtil.getUpdatePromiseSourcingRuleRequest();
    when(nodeFeign.getNodeDetails(any(), any())).thenReturn(testUtil.getBaseResponseOfNode());
    when(postalCodeFeign.getByPostalCodePrefix(anyString(), anyString()))
        .thenReturn(testUtil.getEmptyPostalCodeResponse());
    Exception ex =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                promiseSourcingRuleService.updatePromiseSourcingRule(
                    promiseSourcingRuleConstants.ORG_ID,
                    promiseSourcingRuleConstants.SERVICE_OPTION,
                    promiseSourcingRuleConstants.DESTINATION_GEO_ZONE,
                    promiseSourcingRuleConstants.ALLOCATION_RULE_ID,
                    promiseSourcingRuleConstants.PRIORITY,
                    updatePromiseSourcingRuleRequest));
    assertEquals("DestinationGeoZone is not valid", ex.getMessage());
  }

  @Test
  void deletePromiseSourcingRuleTest() throws PromiseEngineException {
    PromiseSourcingRuleDomainDto promiseSourcingRule = testUtil.getPromiseSourcingRule();

    when(promiseSourcingRulePersistenceService.getPromiseSourcingRule(
            anyString(), anyString(), anyString(), anyString(), anyInt()))
        .thenReturn(promiseSourcingRule);
    when(promiseSourcingRulePersistenceService.deletePromiseSourcingRule(
            any(PromiseSourcingRuleDomainDto.class)))
        .thenReturn(promiseSourcingRule);

    PromiseSourcingRuleDto promiseSourcingRuleDto =
        promiseSourcingRuleService.deletePromiseSourcingRule(
            ORG_ID, SERVICE_OPTION, DESTINATION_GEO_ZONE, ALLOCATION_RULE_ID, PRIORITY);
    assertEquals(promiseSourcingRule.getOrgId(), promiseSourcingRuleDto.getOrgId());
  }

  @Test
  void validateSourceNodesExceptionTest() throws PromiseEngineException {
    Set<String> sourceNodes = new HashSet<>();
    sourceNodes.add("");
    Exception exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> promiseSourcingRuleService.validateSourceNode(sourceNodes));
    Assertions.assertEquals(
        "sourceNodes cannot contain null or an empty string", exception.getMessage());
  }
}
