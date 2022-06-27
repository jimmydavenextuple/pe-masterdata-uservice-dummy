package com.hbc.promise.sourcing.rule.domain;

import static com.hbc.promise.sourcing.rule.utils.PromiseSourcingRuleConstants.ALLOCATION_RULE_ID;
import static com.hbc.promise.sourcing.rule.utils.PromiseSourcingRuleConstants.DESTINATION_GEO_ZONE;
import static com.hbc.promise.sourcing.rule.utils.PromiseSourcingRuleConstants.ORG_ID;
import static com.hbc.promise.sourcing.rule.utils.PromiseSourcingRuleConstants.PRIORITY;
import static com.hbc.promise.sourcing.rule.utils.PromiseSourcingRuleConstants.SERVICE_OPTION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hbc.promise.sourcing.rule.api.domain.inbound.FetchPromiseSourcingRuleRequest;
import com.hbc.promise.sourcing.rule.domain.entity.PromiseSourcingRule;
import com.hbc.promise.sourcing.rule.domain.repository.PromiseSourcingRuleRepository;
import com.hbc.promise.sourcing.rule.exception.common.PromiseEngineException;
import com.hbc.promise.sourcing.rule.utils.TestUtil;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class PromiseSourcingRuleDomainTest {
  @InjectMocks private PromiseSourcingRuleDomain promiseSourcingRuleDomain;

  @Mock private PromiseSourcingRuleRepository promiseSourcingRuleRepository;

  @InjectMocks private TestUtil testUtil;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void fetchPromiseSourcingRuleTest() throws PromiseEngineException {
    PromiseSourcingRule promiseSourcingRule = testUtil.getPromiseSourcingRule();
    FetchPromiseSourcingRuleRequest fetchPromiseSourcingRuleRequest =
        testUtil.getFetchPromiseSourcingRuleRequest();

    when(promiseSourcingRuleRepository
            .findByServiceOptionInAndOrgIdAndAllocationRuleIdAndDestinationGeoZone(
                anyList(), anyString(), anyString(), anyString()))
        .thenReturn(Collections.singletonList(promiseSourcingRule));

    List<PromiseSourcingRule> received_entity =
        promiseSourcingRuleDomain.fetchSourcingRule(fetchPromiseSourcingRuleRequest);
    assertEquals(fetchPromiseSourcingRuleRequest.getOrgId(), received_entity.get(0).getOrgId());
  }

  @Test
  void fetchPromiseSourcingRuleExceptionTest() {
    FetchPromiseSourcingRuleRequest fetchPromiseSourcingRuleRequest =
        testUtil.getFetchPromiseSourcingRuleRequest();

    when(promiseSourcingRuleRepository
            .findByServiceOptionInAndOrgIdAndAllocationRuleIdAndDestinationGeoZone(
                anyList(), anyString(), anyString(), anyString()))
        .thenThrow(NullPointerException.class);

    assertThrows(
        PromiseEngineException.class,
        () -> {
          promiseSourcingRuleDomain.fetchSourcingRule(fetchPromiseSourcingRuleRequest);
        });
  }

  @Test
  void savePromiseSourcingRuleTest() throws PromiseEngineException {
    PromiseSourcingRule promiseSourcingRule = testUtil.getPromiseSourcingRule();
    when(promiseSourcingRuleRepository.save(any(PromiseSourcingRule.class)))
        .thenReturn(promiseSourcingRule);

    PromiseSourcingRule received_promiseSourcingRule =
        promiseSourcingRuleDomain.savePromiseSourcingRule(promiseSourcingRule);
    assertEquals(promiseSourcingRule, received_promiseSourcingRule);
  }

  @Test
  void savePromiseSourcingRuleExceptionTest() {
    PromiseSourcingRule promiseSourcingRule = testUtil.getPromiseSourcingRule();
    when(promiseSourcingRuleRepository.save(any(PromiseSourcingRule.class)))
        .thenThrow(NullPointerException.class);

    assertThrows(
        PromiseEngineException.class,
        () -> {
          promiseSourcingRuleDomain.savePromiseSourcingRule(promiseSourcingRule);
        });
  }

  @Test
  void getPromiseSourcingRuleTest() throws PromiseEngineException {
    PromiseSourcingRule promiseSourcingRule = testUtil.getPromiseSourcingRule();
    when(promiseSourcingRuleRepository
            .findByOrgIdAndServiceOptionAndDestinationGeoZoneAndAllocationRuleIdAndPriority(
                anyString(), anyString(), anyString(), anyString(), anyInt()))
        .thenReturn(promiseSourcingRule);

    PromiseSourcingRule received_entity =
        promiseSourcingRuleDomain.getPromiseSourcingRule(
            ORG_ID, SERVICE_OPTION, DESTINATION_GEO_ZONE, ALLOCATION_RULE_ID, PRIORITY);
    assertEquals(ORG_ID, received_entity.getOrgId());
  }

  @Test
  void getPromiseSourcingRuleExceptionTest() {
    when(promiseSourcingRuleRepository
            .findByOrgIdAndServiceOptionAndDestinationGeoZoneAndAllocationRuleIdAndPriority(
                anyString(), anyString(), anyString(), anyString(), anyInt()))
        .thenThrow(NullPointerException.class);

    assertThrows(
        PromiseEngineException.class,
        () -> {
          promiseSourcingRuleDomain.getPromiseSourcingRule(
              ORG_ID, SERVICE_OPTION, DESTINATION_GEO_ZONE, ALLOCATION_RULE_ID, PRIORITY);
        });
  }

  @Test
  void getPromiseSourcingRuleByOrgIdTest() throws PromiseEngineException {
    PromiseSourcingRule promiseSourcingRule = testUtil.getPromiseSourcingRule();
    when(promiseSourcingRuleRepository.findByOrgId(anyString()))
        .thenReturn(Collections.singletonList(promiseSourcingRule));

    List<PromiseSourcingRule> received_entities =
        promiseSourcingRuleDomain.getPromiseSourcingRulesByOrgId(ORG_ID);
    assertEquals(ORG_ID, received_entities.get(0).getOrgId());
  }

  @Test
  void getPromiseSourcingRuleByOrgIdExceptionTest() {
    when(promiseSourcingRuleRepository.findByOrgId(anyString()))
        .thenThrow(NullPointerException.class);

    assertThrows(
        PromiseEngineException.class,
        () -> {
          promiseSourcingRuleDomain.getPromiseSourcingRulesByOrgId(ORG_ID);
        });
  }

  @Test
  void getPromiseSourcingRulesByPriorityTest() throws PromiseEngineException {
    PromiseSourcingRule promiseSourcingRule = testUtil.getPromiseSourcingRule();
    when(promiseSourcingRuleRepository.findByPriority(anyInt()))
        .thenReturn(Collections.singletonList(promiseSourcingRule));

    List<PromiseSourcingRule> received_entities =
        promiseSourcingRuleDomain.getPromiseSourcingRulesByPriority(PRIORITY);
    assertEquals(PRIORITY, received_entities.get(0).getPriority());
  }

  @Test
  void getPromiseSourcingRuleByPriorityExceptionTest() {
    when(promiseSourcingRuleRepository.findByPriority(anyInt()))
        .thenThrow(NullPointerException.class);

    assertThrows(
        PromiseEngineException.class,
        () -> {
          promiseSourcingRuleDomain.getPromiseSourcingRulesByPriority(PRIORITY);
        });
  }

  @Test
  void deletePromiseSourcingRuleTest() throws PromiseEngineException {
    PromiseSourcingRule promiseSourcingRule = testUtil.getPromiseSourcingRule();
    when(promiseSourcingRuleRepository
            .findByOrgIdAndServiceOptionAndDestinationGeoZoneAndAllocationRuleIdAndPriority(
                anyString(), anyString(), anyString(), anyString(), anyInt()))
        .thenReturn(promiseSourcingRule);

    PromiseSourcingRule received_entity =
        promiseSourcingRuleDomain.deletePromiseSourcingRule(promiseSourcingRule);
    assertEquals(ORG_ID, received_entity.getOrgId());
    verify(promiseSourcingRuleRepository, times(1)).delete(promiseSourcingRule);
  }
}
