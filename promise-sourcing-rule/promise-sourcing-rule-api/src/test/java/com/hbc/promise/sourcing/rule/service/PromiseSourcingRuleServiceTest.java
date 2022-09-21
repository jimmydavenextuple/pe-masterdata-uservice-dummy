package com.hbc.promise.sourcing.rule.service;

import static com.hbc.promise.sourcing.rule.utils.PromiseSourcingRuleConstants.ALLOCATION_RULE_ID;
import static com.hbc.promise.sourcing.rule.utils.PromiseSourcingRuleConstants.DESTINATION_GEO_ZONE;
import static com.hbc.promise.sourcing.rule.utils.PromiseSourcingRuleConstants.EXPRESS;
import static com.hbc.promise.sourcing.rule.utils.PromiseSourcingRuleConstants.NEXTDAY;
import static com.hbc.promise.sourcing.rule.utils.PromiseSourcingRuleConstants.ORG_ID;
import static com.hbc.promise.sourcing.rule.utils.PromiseSourcingRuleConstants.PRIORITY;
import static com.hbc.promise.sourcing.rule.utils.PromiseSourcingRuleConstants.SDND;
import static com.hbc.promise.sourcing.rule.utils.PromiseSourcingRuleConstants.SERVICE_OPTION;
import static com.hbc.promise.sourcing.rule.utils.PromiseSourcingRuleConstants.STANDARD;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.hbc.common.exception.CommonServiceException;
import com.hbc.common.exception.PromiseEngineException;
import com.hbc.node.domain.feign.NodeFeign;
import com.hbc.postal.code.timezone.api.domain.feign.PostalCodeTimezoneFeign;
import com.hbc.promise.sourcing.rule.TestUtil;
import com.hbc.promise.sourcing.rule.api.domain.dto.PromiseSourcingRuleDto;
import com.hbc.promise.sourcing.rule.api.domain.inbound.CreatePromiseSourcingRuleRequest;
import com.hbc.promise.sourcing.rule.api.domain.inbound.FetchPromiseSourcingRuleRequest;
import com.hbc.promise.sourcing.rule.api.domain.inbound.UpdatePromiseSourcingRuleRequest;
import com.hbc.promise.sourcing.rule.api.domain.outbound.FetchPromiseSourcingRuleResponse;
import com.hbc.promise.sourcing.rule.domain.PromiseSourcingRuleDomain;
import com.hbc.promise.sourcing.rule.domain.entity.PromiseSourcingRule;
import com.hbc.promise.sourcing.rule.utils.PromiseSourcingRuleConstants;
import java.util.ArrayList;
import java.util.Collections;
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
  @Mock private PromiseSourcingRuleDomain promiseSourcingRuleDomain;
  @Mock private NodeFeign nodeFeign;
  @Mock private PostalCodeTimezoneFeign postalCodeTimezoneFeign;
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
    List<PromiseSourcingRule> promiseSourcingRuleList = testUtil.getPromiseSourcingRuleList();
    FetchPromiseSourcingRuleRequest fetchPromiseSourcingRuleRequest =
        testUtil.getFetchPromiseSourcingRuleRequest();

    when(promiseSourcingRuleDomain.fetchSourcingRule(any(FetchPromiseSourcingRuleRequest.class)))
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
    verify(promiseSourcingRuleDomain, times(1)).fetchSourcingRule(any());
  }

  @Test
  void fetchPromiseSourcingRuleNotFoundTest() throws PromiseEngineException {
    FetchPromiseSourcingRuleRequest fetchPromiseSourcingRuleRequest =
        testUtil.getFetchPromiseSourcingRuleRequest();

    when(promiseSourcingRuleDomain.fetchSourcingRule(any(FetchPromiseSourcingRuleRequest.class)))
        .thenReturn(new ArrayList<>());

    assertThrows(
        PromiseEngineException.class,
        () -> {
          promiseSourcingRuleService.fetchSourcingRule(fetchPromiseSourcingRuleRequest);
        });
    verify(promiseSourcingRuleDomain, times(1))
        .fetchSourcingRule(any(FetchPromiseSourcingRuleRequest.class));
  }

  @Test
  void createPromiseSourcingRuleTest() throws PromiseEngineException, CommonServiceException {
    PromiseSourcingRule promiseSourcingRule = testUtil.getPromiseSourcingRule();
    CreatePromiseSourcingRuleRequest createPromiseSourcingRuleRequest =
        testUtil.getPromiseSourcingRuleCreationRequest();
    when(nodeFeign.getNodeDetails(any(), any())).thenReturn(testUtil.getBaseResponseOfNode());
    when(postalCodeTimezoneFeign.getPostalCodeTimezone(any(), any()))
        .thenReturn(testUtil.getBaseResponseOfPostalCodeTimezoneDto());
    when(promiseSourcingRuleDomain.savePromiseSourcingRule(any(PromiseSourcingRule.class)))
        .thenReturn(promiseSourcingRule);

    PromiseSourcingRuleDto received_dto =
        promiseSourcingRuleService.createPromiseSourcingRule(createPromiseSourcingRuleRequest);
    assertEquals(createPromiseSourcingRuleRequest.getOrgId(), received_dto.getOrgId());
    verify(promiseSourcingRuleDomain, times(1))
        .savePromiseSourcingRule(any(PromiseSourcingRule.class));
  }

  @Test
  void createPromiseSourcingRuleInvalidDestinationGeozoneExceptionTest()
      throws PromiseEngineException, CommonServiceException {
    PromiseSourcingRule promiseSourcingRule = testUtil.getPromiseSourcingRule();
    CreatePromiseSourcingRuleRequest createPromiseSourcingRuleRequest =
        testUtil.getPromiseSourcingRuleCreationRequest();
    when(nodeFeign.getNodeDetails(any(), any())).thenReturn(testUtil.getBaseResponseOfNode());
    when(postalCodeTimezoneFeign.getPostalCodeTimezone(any(), any()))
        .thenReturn(testUtil.getBaseResponseOfPostalCodeTimezoneDto2());
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
    PromiseSourcingRule promiseSourcingRule = testUtil.getPromiseSourcingRule();
    CreatePromiseSourcingRuleRequest createPromiseSourcingRuleRequest =
        testUtil.getPromiseSourcingRuleCreationRequest();
    when(nodeFeign.getNodeDetails(any(), any())).thenReturn(testUtil.getBaseResponseOfNode3());
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
    PromiseSourcingRule promiseSourcingRule = testUtil.getPromiseSourcingRule();
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
    PromiseSourcingRule promiseSourcingRule = testUtil.getPromiseSourcingRule();
    CreatePromiseSourcingRuleRequest createPromiseSourcingRuleRequest =
        CreatePromiseSourcingRuleRequest.builder()
            .serviceOption(SDND)
            .sourceNodes(Collections.singleton("Node-1"))
            .destinationGeoZone(DESTINATION_GEO_ZONE)
            .priority(PRIORITY)
            .orgId(ORG_ID)
            .build();
    when(nodeFeign.getNodeDetails(any(), any())).thenReturn(testUtil.getBaseResponseOfNode());
    when(postalCodeTimezoneFeign.getPostalCodeTimezone(any(), any()))
        .thenReturn(testUtil.getBaseResponseOfPostalCodeTimezoneDto());
    when(promiseSourcingRuleDomain.savePromiseSourcingRule(any(PromiseSourcingRule.class)))
        .thenReturn(promiseSourcingRule);

    PromiseSourcingRuleDto received_dto =
        promiseSourcingRuleService.createPromiseSourcingRule(
            testUtil.getPromiseSourcingRuleCreationRequest());
    assertEquals(createPromiseSourcingRuleRequest.getPriority(), received_dto.getPriority());
    verify(promiseSourcingRuleDomain, times(1))
        .savePromiseSourcingRule(any(PromiseSourcingRule.class));
  }

  @Test
  void createPromiseSourcingRuleAndSourcingRuleAlreadyExist() throws PromiseEngineException {
    PromiseSourcingRule promiseSourcingRule = testUtil.getPromiseSourcingRule();
    CreatePromiseSourcingRuleRequest createPromiseSourcingRuleRequest =
        CreatePromiseSourcingRuleRequest.builder()
            .serviceOption(SDND)
            .sourceNodes(Collections.singleton("Node-1"))
            .destinationGeoZone(DESTINATION_GEO_ZONE)
            .priority(PRIORITY)
            .orgId(ORG_ID)
            .build();
    when(nodeFeign.getNodeDetails(any(), any())).thenReturn(testUtil.getBaseResponseOfNode());
    when(postalCodeTimezoneFeign.getPostalCodeTimezone(any(), any()))
        .thenReturn(testUtil.getBaseResponseOfPostalCodeTimezoneDto());
    when(promiseSourcingRuleDomain.fetchSourcingRule(
            anyString(), anyString(), anyString(), anyString()))
        .thenReturn(List.of(promiseSourcingRule));

    Assertions.assertThrows(
        PromiseEngineException.class,
        () ->
            promiseSourcingRuleService.createPromiseSourcingRule(
                testUtil.getPromiseSourcingRuleCreationRequest()));
    verify(promiseSourcingRuleDomain, times(0))
        .savePromiseSourcingRule(any(PromiseSourcingRule.class));
  }

  @Test
  void getPromiseSourcingRuleTest() throws PromiseEngineException {
    PromiseSourcingRule promiseSourcingRule = testUtil.getPromiseSourcingRule();
    when(promiseSourcingRuleDomain.getPromiseSourcingRule(
            anyString(), anyString(), anyString(), anyString(), anyInt()))
        .thenReturn(promiseSourcingRule);

    PromiseSourcingRuleDto promiseSourcingRuleDto =
        promiseSourcingRuleService.getPromiseSourcingRule(
            ORG_ID, SERVICE_OPTION, DESTINATION_GEO_ZONE, ALLOCATION_RULE_ID, PRIORITY);
    assertEquals(promiseSourcingRuleDto.getOrgId(), promiseSourcingRule.getOrgId());
    verify(promiseSourcingRuleDomain, times(1))
        .getPromiseSourcingRule(anyString(), anyString(), anyString(), anyString(), anyInt());
  }

  @Test
  void getPromiseSourcingRuleNotFoundTest() throws PromiseEngineException {
    when(promiseSourcingRuleDomain.getPromiseSourcingRule(
            anyString(), anyString(), anyString(), anyString(), anyInt()))
        .thenReturn(null);

    assertThrows(
        PromiseEngineException.class,
        () -> {
          promiseSourcingRuleService.getPromiseSourcingRule(
              ORG_ID, SERVICE_OPTION, DESTINATION_GEO_ZONE, ALLOCATION_RULE_ID, PRIORITY);
        });
    verify(promiseSourcingRuleDomain, times(1))
        .getPromiseSourcingRule(anyString(), anyString(), anyString(), anyString(), anyInt());
  }

  @Test
  void getPromiseSourcingRuleByOrgIdTest() throws PromiseEngineException {
    PromiseSourcingRule promiseSourcingRule = testUtil.getPromiseSourcingRule();
    when(promiseSourcingRuleDomain.getPromiseSourcingRulesByOrgId(anyString()))
        .thenReturn(Collections.singletonList(promiseSourcingRule));

    List<PromiseSourcingRuleDto> promiseSourcingRuleDto =
        promiseSourcingRuleService.getPromiseSourcingRulesByOrgId(ORG_ID);
    assertEquals(promiseSourcingRuleDto.get(0).getOrgId(), promiseSourcingRule.getOrgId());
    verify(promiseSourcingRuleDomain, times(1)).getPromiseSourcingRulesByOrgId(any());
  }

  @Test
  void getPromiseSourcingRuleByPriorityTest() throws PromiseEngineException {
    PromiseSourcingRule promiseSourcingRule = testUtil.getPromiseSourcingRule();
    when(promiseSourcingRuleDomain.getPromiseSourcingRulesByPriority(anyInt()))
        .thenReturn(Collections.singletonList(promiseSourcingRule));

    List<PromiseSourcingRuleDto> promiseSourcingRuleDto =
        promiseSourcingRuleService.getPromiseSourcingRulesByPriority(PRIORITY);
    assertEquals(promiseSourcingRuleDto.get(0).getOrgId(), promiseSourcingRule.getOrgId());
    verify(promiseSourcingRuleDomain, times(1)).getPromiseSourcingRulesByPriority(PRIORITY);
  }

  @Test
  void updatePromiseSourcingRuleTest() throws PromiseEngineException, CommonServiceException {
    PromiseSourcingRule promiseSourcingRule = testUtil.getPromiseSourcingRule();
    UpdatePromiseSourcingRuleRequest updatePromiseSourcingRuleRequest =
        testUtil.getUpdatePromiseSourcingRuleRequest();
    when(nodeFeign.getNodeDetails(any(), any())).thenReturn(testUtil.getBaseResponseOfNode());
    when(postalCodeTimezoneFeign.getPostalCodeTimezone(any(), any()))
        .thenReturn(testUtil.getBaseResponseOfPostalCodeTimezoneDto());
    when(promiseSourcingRuleDomain.getPromiseSourcingRule(
            anyString(), anyString(), anyString(), anyString(), anyInt()))
        .thenReturn(promiseSourcingRule);
    when(promiseSourcingRuleDomain.savePromiseSourcingRule(any(PromiseSourcingRule.class)))
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
    verify(promiseSourcingRuleDomain, times(1))
        .getPromiseSourcingRule(anyString(), anyString(), anyString(), anyString(), anyInt());
    verify(promiseSourcingRuleDomain, times(1))
        .savePromiseSourcingRule(any(PromiseSourcingRule.class));
  }

  @Test
  void updatePromiseSourcingRuleInactiveNodeExceptionTest()
      throws PromiseEngineException, CommonServiceException {
    PromiseSourcingRule promiseSourcingRule = testUtil.getPromiseSourcingRule();
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
    PromiseSourcingRule promiseSourcingRule = testUtil.getPromiseSourcingRule();
    UpdatePromiseSourcingRuleRequest updatePromiseSourcingRuleRequest =
        testUtil.getUpdatePromiseSourcingRuleRequest();
    when(nodeFeign.getNodeDetails(any(), any())).thenReturn(testUtil.getBaseResponseOfNode3());
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
    PromiseSourcingRule promiseSourcingRule = testUtil.getPromiseSourcingRule();
    UpdatePromiseSourcingRuleRequest updatePromiseSourcingRuleRequest =
        testUtil.getUpdatePromiseSourcingRuleRequest();
    when(nodeFeign.getNodeDetails(any(), any())).thenReturn(testUtil.getBaseResponseOfNode());
    when(postalCodeTimezoneFeign.getPostalCodeTimezone(any(), any()))
        .thenReturn(testUtil.getBaseResponseOfPostalCodeTimezoneDto2());
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
    PromiseSourcingRule promiseSourcingRule = testUtil.getPromiseSourcingRule();

    when(promiseSourcingRuleDomain.getPromiseSourcingRule(
            anyString(), anyString(), anyString(), anyString(), anyInt()))
        .thenReturn(promiseSourcingRule);
    when(promiseSourcingRuleDomain.deletePromiseSourcingRule(any(PromiseSourcingRule.class)))
        .thenReturn(promiseSourcingRule);

    PromiseSourcingRuleDto promiseSourcingRuleDto =
        promiseSourcingRuleService.deletePromiseSourcingRule(
            ORG_ID, SERVICE_OPTION, DESTINATION_GEO_ZONE, ALLOCATION_RULE_ID, PRIORITY);
    assertEquals(promiseSourcingRule.getOrgId(), promiseSourcingRuleDto.getOrgId());
  }
}
