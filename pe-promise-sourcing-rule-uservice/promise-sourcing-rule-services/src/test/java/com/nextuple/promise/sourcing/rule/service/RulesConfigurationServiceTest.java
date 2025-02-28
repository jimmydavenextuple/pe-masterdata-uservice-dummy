/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.promise.sourcing.rule.service;

import static com.nextuple.promise.sourcing.rule.utils.FetchRulesUtil.RULE_NOT_FOUND_ERROR_MESSAGE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.promise.sourcing.rule.TestUtil;
import com.nextuple.promise.sourcing.rule.api.domain.enums.SourcingAttributesDefinitionScopeEnum;
import com.nextuple.promise.sourcing.rule.api.domain.enums.SourcingAttributesDefinitionStatus;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.RulesConfigurationsRequest;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.RulesConfigurationResponse;
import com.nextuple.promise.sourcing.rule.api.domain.pojo.RuleConfigurationParam;
import com.nextuple.promise.sourcing.rule.api.domain.pojo.SourcingAttributeValuesInfo;
import com.nextuple.promise.sourcing.rule.api.domain.services.RulesRetrievalService;
import com.nextuple.promise.sourcing.rule.persistence.domain.RulesConfigurationDomainDto;
import com.nextuple.promise.sourcing.rule.persistence.service.RulesConfigurationPersistenceService;
import com.nextuple.promise.sourcing.rule.persistence.service.SourcingAttributesDefinitionPersistenceService;
import com.nextuple.promise.sourcing.rule.service.impl.RuleConfigImpl;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

class RulesConfigurationServiceTest {

  @Mock private RulesConfigurationPersistenceService rulesConfigurationPersistenceService;

  @Mock
  private SourcingAttributesDefinitionPersistenceService
      sourcingAttributesDefinitionPersistenceService;

  @Mock RulesRetrievalService<RulesConfigurationDomainDto> rulesRetrievalService;
  @Mock RuleConfigImpl ruleConfig;

  @InjectMocks private RulesConfigurationService rulesConfigurationService;
  @InjectMocks private TestUtil testUtil;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    ReflectionTestUtils.setField(rulesConfigurationService, "rulesRetrievalService", ruleConfig);
  }

  @Test
  @DisplayName("Create Rules Configuration - Success test")
  void createRulesConfigurationSuccessTest() throws PromiseEngineException, CommonServiceException {

    when(sourcingAttributesDefinitionPersistenceService
            .getSourcingRuleAttributesDefinitionEntityByIdAndOrgIdAndScope(any(), any(), any()))
        .thenReturn(
            Optional.ofNullable(
                testUtil.getSourcingRuleAttributesDefinitionEntityForMLRULE(
                    SourcingAttributesDefinitionStatus.ACTIVE)));

    when(rulesConfigurationPersistenceService.saveRulesConfiguration(
            any(RulesConfigurationDomainDto.class)))
        .thenReturn(testUtil.getExpectedRulesConfigurationDomainDto1());

    RulesConfigurationResponse actualResponse =
        rulesConfigurationService.createRulesConfiguration(
            testUtil.getRulesConfigurationForMLRequest());

    assertNotNull(actualResponse);
    assertEquals(
        testUtil.getExpectedRulesConfigurationResponse1().getOrgId(), actualResponse.getOrgId());
    assertEquals(
        testUtil.getExpectedRulesConfigurationResponse1().getRuleName(),
        actualResponse.getRuleName());
    assertEquals(
        testUtil.getExpectedRulesConfigurationResponse1().getRule(), actualResponse.getRule());
    assertEquals(
        testUtil.getExpectedRulesConfigurationResponse1().getAttributeDefinitionId(),
        actualResponse.getAttributeDefinitionId());
    assertEquals(
        testUtil.getExpectedRulesConfigurationResponse1().getModuleName(),
        actualResponse.getModuleName());
    assertEquals(
        testUtil.getExpectedRulesConfigurationResponse1().getScope(), actualResponse.getScope());
    assertEquals(
        testUtil.getExpectedRulesConfigurationResponse1().getCustomAttributes(),
        actualResponse.getCustomAttributes());

    verify(sourcingAttributesDefinitionPersistenceService, times(2))
        .getSourcingRuleAttributesDefinitionEntityByIdAndOrgIdAndScope(any(), any(), any());

    verify(rulesConfigurationPersistenceService, times(1))
        .saveRulesConfiguration(any(RulesConfigurationDomainDto.class));
  }

  @Test
  @DisplayName("Create Rules Configuration - Exception test")
  void createRulesConfigurationRuntimeExceptionTest() throws PromiseEngineException {

    when(sourcingAttributesDefinitionPersistenceService
            .getSourcingRuleAttributesDefinitionEntityByIdAndOrgIdAndScope(any(), any(), any()))
        .thenReturn(
            Optional.ofNullable(
                testUtil.getSourcingRuleAttributesDefinitionEntityForMLRULE(
                    SourcingAttributesDefinitionStatus.ACTIVE)));

    when(rulesConfigurationPersistenceService.saveRulesConfiguration(
            any(RulesConfigurationDomainDto.class)))
        .thenThrow(new RuntimeException("Unexpected error occurred"));

    Exception ex =
        assertThrows(
            RuntimeException.class,
            () ->
                rulesConfigurationService.createRulesConfiguration(
                    testUtil.getRulesConfigurationForMLRequest()));

    assertEquals("Unexpected error occurred", ex.getMessage());

    verify(sourcingAttributesDefinitionPersistenceService, times(2))
        .getSourcingRuleAttributesDefinitionEntityByIdAndOrgIdAndScope(any(), any(), any());

    verify(rulesConfigurationPersistenceService, times(1))
        .saveRulesConfiguration(any(RulesConfigurationDomainDto.class));
  }

  @Test
  @DisplayName(
      "Create Rules Configuration - When the attributeDefinitonid does not exist for the scope in request")
  void createRulesConfigurationWithoutSourcingAttributeDefinitionIdTest()
      throws PromiseEngineException {

    when(sourcingAttributesDefinitionPersistenceService
            .getSourcingRuleAttributesDefinitionEntityByIdAndOrgIdAndScope(any(), any(), any()))
        .thenReturn(Optional.empty());

    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                rulesConfigurationService.createRulesConfiguration(
                    testUtil.getRulesConfigurationForMLRequest()));

    assertEquals(
        "Invalid sourcing attributes definition for given scope / Sourcing attributes definition exists but not in ACTIVE status",
        ex.getMessage());
    assertEquals(HttpStatus.BAD_REQUEST, ((CommonServiceException) ex).getHttpStatus());
    verify(rulesConfigurationPersistenceService, times(0))
        .saveRulesConfiguration(any(RulesConfigurationDomainDto.class));

    verify(sourcingAttributesDefinitionPersistenceService, times(1))
        .getSourcingRuleAttributesDefinitionEntityByIdAndOrgIdAndScope(any(), any(), any());
  }

  @Test
  @DisplayName(
      "Create Rules Configuration - When an rule configuration is being created with inactive definition Id ")
  void createRulesConfigurationWithInactiveIdTest()
      throws PromiseEngineException, CommonServiceException {
    RulesConfigurationsRequest rulesConfigurationsRequest =
        testUtil.getRulesConfigurationForMLRequest();
    rulesConfigurationsRequest.setScope(SourcingAttributesDefinitionScopeEnum.SOURCING_RULE);
    when(sourcingAttributesDefinitionPersistenceService
            .getSourcingRuleAttributesDefinitionEntityByIdAndOrgIdAndScope(any(), any(), any()))
        .thenReturn(
            Optional.ofNullable(
                testUtil.getSourcingRuleAttributesDefinitionEntityForMLRULE(
                    SourcingAttributesDefinitionStatus.INACTIVE)));
    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () -> rulesConfigurationService.createRulesConfiguration(rulesConfigurationsRequest));
    assertEquals(
        "Invalid sourcing attributes definition for given scope / Sourcing attributes definition exists but not in ACTIVE status",
        ex.getMessage());
    assertEquals(HttpStatus.BAD_REQUEST, ((CommonServiceException) ex).getHttpStatus());
    verify(rulesConfigurationPersistenceService, times(0))
        .saveRulesConfiguration(any(RulesConfigurationDomainDto.class));
    verify(sourcingAttributesDefinitionPersistenceService, times(1))
        .getSourcingRuleAttributesDefinitionEntityByIdAndOrgIdAndScope(any(), any(), any());
  }

  @Test
  @DisplayName("Create Rules Configuration - When all the required attributes are not present ")
  void createRulesConfigurationWithUnequalRequiredAttributeTest()
      throws PromiseEngineException, CommonServiceException {

    RulesConfigurationsRequest rulesConfigurationsRequest =
        testUtil.getRulesConfigurationForMLRequest();
    rulesConfigurationsRequest.setRule("EXPRESS");

    when(sourcingAttributesDefinitionPersistenceService
            .getSourcingRuleAttributesDefinitionEntityByIdAndOrgIdAndScope(any(), any(), any()))
        .thenReturn(
            Optional.ofNullable(
                testUtil.getSourcingRuleAttributesDefinitionEntityForMLRULE(
                    SourcingAttributesDefinitionStatus.ACTIVE)));

    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () -> rulesConfigurationService.createRulesConfiguration(rulesConfigurationsRequest));

    assertEquals(
        "Can’t add or fetch the rules as all the required attributes are not present",
        ex.getMessage());
    assertEquals(HttpStatus.BAD_REQUEST, ((CommonServiceException) ex).getHttpStatus());
    verify(rulesConfigurationPersistenceService, times(0))
        .saveRulesConfiguration(any(RulesConfigurationDomainDto.class));

    verify(sourcingAttributesDefinitionPersistenceService, times(1))
        .getSourcingRuleAttributesDefinitionEntityByIdAndOrgIdAndScope(any(), any(), any());
  }

  @Test
  @DisplayName(
      "Create Rules Configuration - length of attributes is more than optional and required attributes combined ")
  void
      createRulesConfigurationWithUnequalLengthOfRequiredAttributeAndOptionalAttributeCombinedTest()
          throws PromiseEngineException, CommonServiceException {

    RulesConfigurationsRequest rulesConfigurationsRequest =
        testUtil.getRulesConfigurationForMLRequest();
    rulesConfigurationsRequest.setRule("EXPRESS:store:STANDARD:store2");

    when(sourcingAttributesDefinitionPersistenceService
            .getSourcingRuleAttributesDefinitionEntityByIdAndOrgIdAndScope(any(), any(), any()))
        .thenReturn(
            Optional.ofNullable(
                testUtil.getSourcingRuleAttributesDefinitionEntityForMLRULE(
                    SourcingAttributesDefinitionStatus.ACTIVE)));

    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () -> rulesConfigurationService.createRulesConfiguration(rulesConfigurationsRequest));

    assertEquals(
        "Can't add or fetch the rules as length of attributes is more than optional and required attributes combined",
        ex.getMessage());
    assertEquals(HttpStatus.BAD_REQUEST, ((CommonServiceException) ex).getHttpStatus());
    verify(rulesConfigurationPersistenceService, times(0))
        .saveRulesConfiguration(any(RulesConfigurationDomainDto.class));

    verify(sourcingAttributesDefinitionPersistenceService, times(1))
        .getSourcingRuleAttributesDefinitionEntityByIdAndOrgIdAndScope(any(), any(), any());
  }

  @Test
  @DisplayName("Create Rules Configuration - When rule contains blank values")
  void createRulesConfigurationWithBlankRuleValuesTest()
      throws PromiseEngineException, CommonServiceException {

    RulesConfigurationsRequest rulesConfigurationsRequest =
        testUtil.getRulesConfigurationForMLRequest();
    rulesConfigurationsRequest.setRule(":");

    when(sourcingAttributesDefinitionPersistenceService
            .getSourcingRuleAttributesDefinitionEntityByIdAndOrgIdAndScope(any(), any(), any()))
        .thenReturn(
            Optional.ofNullable(
                testUtil.getSourcingRuleAttributesDefinitionEntityForMLRULE(
                    SourcingAttributesDefinitionStatus.ACTIVE)));

    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () -> rulesConfigurationService.createRulesConfiguration(rulesConfigurationsRequest));

    assertEquals("Can't add the rule as rule contains blank values", ex.getMessage());
    assertEquals(HttpStatus.BAD_REQUEST, ((CommonServiceException) ex).getHttpStatus());

    verify(rulesConfigurationPersistenceService, times(0))
        .saveRulesConfiguration(any(RulesConfigurationDomainDto.class));

    verify(sourcingAttributesDefinitionPersistenceService, times(2))
        .getSourcingRuleAttributesDefinitionEntityByIdAndOrgIdAndScope(any(), any(), any());
  }

  @Test
  @DisplayName("Get Rules Configuration: Happy Path - Definition with optional attributes")
  void processFetchRulesConfigurationSuccessTest()
      throws PromiseEngineException, CommonServiceException {
    var request = testUtil.getFetchRuleConfigurationRequest();
    var ruleDefinition =
        testUtil.getSourcingRuleAttributesDefinitionForGenericRule(
            SourcingAttributesDefinitionStatus.ACTIVE);
    var ruleFromDB = testUtil.getRulesConfigurationDomainDto();

    when(sourcingAttributesDefinitionPersistenceService
            .getSourcingRuleAttributesDefinitionEntityByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.of(ruleDefinition));
    when(rulesConfigurationPersistenceService.findByOrgIdAndAttributeDefinitionIdAndRuleStartsWith(
            anyString(), anyLong(), anyString()))
        .thenReturn(List.of(ruleFromDB));

    RulesConfigurationResponse response =
        rulesConfigurationService.processFetchRulesConfiguration(request);

    assertNotNull(response);
    assertEquals(request.getOrgId(), response.getOrgId());
    assertEquals(request.getModuleName(), response.getModuleName());

    verify(sourcingAttributesDefinitionPersistenceService, times(1))
        .getSourcingRuleAttributesDefinitionEntityByIdAndOrgId(anyLong(), anyString());
    verify(rulesConfigurationPersistenceService, times(1))
        .findByOrgIdAndAttributeDefinitionIdAndRuleStartsWith(anyString(), anyLong(), anyString());
  }

  @Test
  @DisplayName("Get Rules Configuration: Happy Path - Definition with no optional attributes")
  void processFetchRulesConfigurationSuccessWithNoOptAttrTest()
      throws PromiseEngineException, CommonServiceException {
    var request = testUtil.getFetchRuleConfigurationRequest();
    request.setAttributeValuesInfo(
        SourcingAttributeValuesInfo.builder()
            .requiredAttributesValue("EXPRESS")
            .optionalAttributesValue(null)
            .build());
    var ruleDefinition =
        testUtil.getSourcingRuleAttributesDefinitionForGenericRule(
            SourcingAttributesDefinitionStatus.ACTIVE);
    ruleDefinition.setOptAttributes(null);
    var ruleFromDB = testUtil.getRulesConfigurationDomainDto();
    ruleFromDB.setRule("EXPRESS");

    when(sourcingAttributesDefinitionPersistenceService
            .getSourcingRuleAttributesDefinitionEntityByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.of(ruleDefinition));
    when(rulesConfigurationPersistenceService.findByOrgIdAndAttributeDefinitionIdAndRule(
            anyString(), anyLong(), anyString()))
        .thenReturn(Optional.of(ruleFromDB));

    RulesConfigurationResponse response =
        rulesConfigurationService.processFetchRulesConfiguration(request);

    assertNotNull(response);
    assertEquals(request.getOrgId(), response.getOrgId());
    assertEquals(request.getModuleName(), response.getModuleName());

    verify(sourcingAttributesDefinitionPersistenceService, times(1))
        .getSourcingRuleAttributesDefinitionEntityByIdAndOrgId(anyLong(), anyString());
    verify(rulesConfigurationPersistenceService, times(1))
        .findByOrgIdAndAttributeDefinitionIdAndRule(anyString(), anyLong(), anyString());
  }

  @Test
  @DisplayName(
      "Get Rules Configuration: Happy Path Scoring - Rule with matching required attributes")
  void processFetchRulesConfigurationRuleWithMatchingReqAttrTest()
      throws PromiseEngineException, CommonServiceException {
    var request = testUtil.getFetchRuleConfigurationRequest();
    var ruleDefinition =
        testUtil.getSourcingRuleAttributesDefinitionForGenericRule(
            SourcingAttributesDefinitionStatus.ACTIVE);
    var ruleFromDB = testUtil.getRulesConfigurationDomainDto();
    ruleFromDB.setRule("EXPRESS:");

    when(sourcingAttributesDefinitionPersistenceService
            .getSourcingRuleAttributesDefinitionEntityByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.of(ruleDefinition));
    when(rulesConfigurationPersistenceService.findByOrgIdAndAttributeDefinitionIdAndRuleStartsWith(
            anyString(), anyLong(), anyString()))
        .thenReturn(List.of(ruleFromDB));
    when(ruleConfig.filterAllMatchingRulesByScoring(
            anyList(), anyString(), anyString(), anyInt(), any()))
        .thenReturn(List.of(ruleFromDB));

    RulesConfigurationResponse response =
        rulesConfigurationService.processFetchRulesConfiguration(request);

    assertNotNull(response);
    assertEquals(request.getOrgId(), response.getOrgId());
    assertEquals(request.getModuleName(), response.getModuleName());

    verify(sourcingAttributesDefinitionPersistenceService, times(1))
        .getSourcingRuleAttributesDefinitionEntityByIdAndOrgId(anyLong(), anyString());
    verify(rulesConfigurationPersistenceService, times(1))
        .findByOrgIdAndAttributeDefinitionIdAndRuleStartsWith(anyString(), anyLong(), anyString());
    verify(ruleConfig, times(1))
        .filterAllMatchingRulesByScoring(anyList(), anyString(), anyString(), anyInt(), any());
  }

  @Test
  @DisplayName("Get Rules Configuration: Happy Path Scoring - Rule with max score")
  void processFetchRulesConfigurationRuleWithMaxScoreTest()
      throws PromiseEngineException, CommonServiceException {
    var request = testUtil.getFetchRuleConfigurationRequest();
    request.setAttributeValuesInfo(
        SourcingAttributeValuesInfo.builder()
            .requiredAttributesValue("EXPRESS")
            .optionalAttributesValue("MFC:Zone1")
            .build());
    var ruleDefinition =
        testUtil.getSourcingRuleAttributesDefinitionForGenericRule(
            SourcingAttributesDefinitionStatus.ACTIVE);
    ruleDefinition.setOptAttributes("2,3");
    var ruleFromDB1 = testUtil.getRulesConfigurationDomainDto();
    ruleFromDB1.setRule("EXPRESS:MFC:");
    var ruleFromDB2 = testUtil.getRulesConfigurationDomainDto();
    ruleFromDB2.setRule("EXPRESS::Zone1");

    when(sourcingAttributesDefinitionPersistenceService
            .getSourcingRuleAttributesDefinitionEntityByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.of(ruleDefinition));
    when(rulesConfigurationPersistenceService.findByOrgIdAndAttributeDefinitionIdAndRuleStartsWith(
            anyString(), anyLong(), anyString()))
        .thenReturn(List.of(ruleFromDB1, ruleFromDB2));
    when(ruleConfig.filterAllMatchingRulesByScoring(
            anyList(), anyString(), anyString(), anyInt(), any()))
        .thenReturn(List.of(ruleFromDB1));

    RulesConfigurationResponse response =
        rulesConfigurationService.processFetchRulesConfiguration(request);

    assertNotNull(response);
    assertEquals(request.getOrgId(), response.getOrgId());
    assertEquals(request.getModuleName(), response.getModuleName());
    assertEquals(ruleFromDB1.getRule(), response.getRule());

    verify(sourcingAttributesDefinitionPersistenceService, times(1))
        .getSourcingRuleAttributesDefinitionEntityByIdAndOrgId(anyLong(), anyString());
    verify(rulesConfigurationPersistenceService, times(1))
        .findByOrgIdAndAttributeDefinitionIdAndRuleStartsWith(anyString(), anyLong(), anyString());
    verify(ruleConfig, times(1))
        .filterAllMatchingRulesByScoring(anyList(), anyString(), anyString(), anyInt(), any());
  }

  @Test
  @DisplayName("Get Rules Configuration: Error - Issue in persistence service")
  void processFetchRulesConfigurationRuleDefNotFoundTest() throws PromiseEngineException {
    var request = testUtil.getFetchRuleConfigurationRequest();

    when(sourcingAttributesDefinitionPersistenceService
            .getSourcingRuleAttributesDefinitionEntityByIdAndOrgId(anyLong(), anyString()))
        .thenThrow(new RuntimeException("Not found!"));

    Exception ex =
        assertThrows(
            RuntimeException.class,
            () -> rulesConfigurationService.processFetchRulesConfiguration(request));

    assertEquals("Not found!", ex.getMessage());

    verify(sourcingAttributesDefinitionPersistenceService, times(1))
        .getSourcingRuleAttributesDefinitionEntityByIdAndOrgId(anyLong(), anyString());
    verify(rulesConfigurationPersistenceService, times(0))
        .findByOrgIdAndAttributeDefinitionIdAndRuleStartsWith(anyString(), anyLong(), anyString());
  }

  @Test
  @DisplayName("Get Rules Configuration: Error - Different Modules")
  void processFetchRulesConfigurationRuleDiffModulesTest() throws PromiseEngineException {
    var request = testUtil.getFetchRuleConfigurationRequest();
    var ruleDefinition =
        testUtil.getSourcingRuleAttributesDefinitionForGenericRule(
            SourcingAttributesDefinitionStatus.ACTIVE);
    ruleDefinition.setScope(SourcingAttributesDefinitionScopeEnum.HOLIDAY_CUTOFF);

    when(sourcingAttributesDefinitionPersistenceService
            .getSourcingRuleAttributesDefinitionEntityByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.of(ruleDefinition));

    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () -> rulesConfigurationService.processFetchRulesConfiguration(request));

    assertEquals(
        "Invalid attributes definition for given scope / attributes definition exists but not in ACTIVE status",
        ex.getMessage());

    verify(sourcingAttributesDefinitionPersistenceService, times(1))
        .getSourcingRuleAttributesDefinitionEntityByIdAndOrgId(anyLong(), anyString());
  }

  @Test
  @DisplayName("Get Rules Configuration: Error - Inactive Definition")
  void processFetchRulesConfigurationRuleInactiveDefTest() throws PromiseEngineException {
    var request = testUtil.getFetchRuleConfigurationRequest();
    var ruleDefinition =
        testUtil.getSourcingRuleAttributesDefinitionForGenericRule(
            SourcingAttributesDefinitionStatus.INACTIVE);

    when(sourcingAttributesDefinitionPersistenceService
            .getSourcingRuleAttributesDefinitionEntityByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.of(ruleDefinition));

    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () -> rulesConfigurationService.processFetchRulesConfiguration(request));

    assertEquals(
        "Invalid attributes definition for given scope / attributes definition exists but not in ACTIVE status",
        ex.getMessage());

    verify(sourcingAttributesDefinitionPersistenceService, times(1))
        .getSourcingRuleAttributesDefinitionEntityByIdAndOrgId(anyLong(), anyString());
  }

  @Test
  @DisplayName("Get Rules Configuration: Error - No matching rule found")
  void processFetchRulesConfigurationRuleNoRuleFoundTest() throws PromiseEngineException {
    var request = testUtil.getFetchRuleConfigurationRequest();
    request.setAttributeValuesInfo(
        SourcingAttributeValuesInfo.builder()
            .requiredAttributesValue("EXPRESS")
            .optionalAttributesValue("Store:Zone3")
            .build());
    var ruleDefinition =
        testUtil.getSourcingRuleAttributesDefinitionForGenericRule(
            SourcingAttributesDefinitionStatus.ACTIVE);
    ruleDefinition.setOptAttributes("2,3");
    var ruleFromDB1 = testUtil.getRulesConfigurationDomainDto();
    ruleFromDB1.setRule("EXPRESS:MFC:");
    var ruleFromDB2 = testUtil.getRulesConfigurationDomainDto();
    ruleFromDB2.setRule("EXPRESS::Zone1");

    when(sourcingAttributesDefinitionPersistenceService
            .getSourcingRuleAttributesDefinitionEntityByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.of(ruleDefinition));
    when(rulesConfigurationPersistenceService.findByOrgIdAndAttributeDefinitionIdAndRuleStartsWith(
            anyString(), anyLong(), anyString()))
        .thenReturn(List.of(ruleFromDB1, ruleFromDB2));
    when(ruleConfig.filterAllMatchingRulesByScoring(
            anyList(), anyString(), anyString(), anyInt(), any()))
        .thenReturn(List.of());

    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () -> rulesConfigurationService.processFetchRulesConfiguration(request));

    assertEquals(
        "Rule not found for a given ML_RULE scope and EXPRESS:Store:Zone3 rule", ex.getMessage());

    verify(sourcingAttributesDefinitionPersistenceService, times(1))
        .getSourcingRuleAttributesDefinitionEntityByIdAndOrgId(anyLong(), anyString());
    verify(rulesConfigurationPersistenceService, times(1))
        .findByOrgIdAndAttributeDefinitionIdAndRuleStartsWith(anyString(), anyLong(), anyString());
    verify(ruleConfig, times(1))
        .filterAllMatchingRulesByScoring(anyList(), anyString(), anyString(), anyInt(), any());
  }

  @Test
  @DisplayName("Delete Rules Configuration - Success test")
  void deleteRuleConfigurationSuccessTest() throws Exception {
    var ruleDto = testUtil.getRulesConfigurationDomainDto();
    when(rulesConfigurationPersistenceService.findByOrgIdAndRuleNameAndRuleAndModuleNameAndScope(
            any(RuleConfigurationParam.class)))
        .thenReturn(Optional.of(ruleDto));
    doNothing()
        .when(rulesConfigurationPersistenceService)
        .deleteRuleConfiguration(any(RulesConfigurationDomainDto.class));

    RulesConfigurationResponse actualResponse =
        rulesConfigurationService.deleteRuleConfiguration(testUtil.getRuleConfigurationParam());

    assertNotNull(actualResponse);
    assertEquals(ruleDto.getOrgId(), actualResponse.getOrgId());
    assertEquals(ruleDto.getRuleName(), actualResponse.getRuleName());
    assertEquals(ruleDto.getRule(), actualResponse.getRule());
    assertEquals(ruleDto.getAttributeDefinitionId(), actualResponse.getAttributeDefinitionId());
    assertEquals(ruleDto.getModuleName(), actualResponse.getModuleName());
    assertEquals(ruleDto.getScope(), actualResponse.getScope());

    verify(rulesConfigurationPersistenceService, times(1))
        .deleteRuleConfiguration(any(RulesConfigurationDomainDto.class));
  }

  @Test
  @DisplayName("Delete Rules Configuration - Exception test")
  void deleteRuleConfigurationRuntimeExceptionTest() throws PromiseEngineException {
    when(rulesConfigurationPersistenceService.findByOrgIdAndRuleNameAndRuleAndModuleNameAndScope(
            any(RuleConfigurationParam.class)))
        .thenReturn(Optional.empty());

    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                rulesConfigurationService.deleteRuleConfiguration(
                    testUtil.getRuleConfigurationParam()));

    assertEquals(RULE_NOT_FOUND_ERROR_MESSAGE, ex.getMessage());

    verify(rulesConfigurationPersistenceService, times(0))
        .deleteRuleConfiguration(any(RulesConfigurationDomainDto.class));
  }
}
