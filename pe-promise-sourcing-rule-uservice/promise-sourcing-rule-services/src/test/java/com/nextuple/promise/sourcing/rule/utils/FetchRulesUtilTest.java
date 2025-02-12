/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.promise.sourcing.rule.utils;

import static com.nextuple.promise.sourcing.rule.utils.FetchRulesUtil.REQUIRED_ATTRIBUTES_LENGTH_ERROR_MESSAGE;
import static com.nextuple.promise.sourcing.rule.utils.FetchRulesUtil.TOTAL_ATTRIBUTES_LENGTH_ERROR_MESSAGE;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.promise.sourcing.rule.TestUtil;
import com.nextuple.promise.sourcing.rule.api.domain.enums.SourcingAttributesDefinitionStatus;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.SourcingAttributesDefinitionResponse;
import com.nextuple.promise.sourcing.rule.api.domain.pojo.AttributeInfo;
import com.nextuple.promise.sourcing.rule.persistence.domain.AttributeValuesDomainDto;
import com.nextuple.promise.sourcing.rule.persistence.domain.SourcingAttributeDomainDto;
import com.nextuple.promise.sourcing.rule.persistence.domain.SourcingAttributesDefinitionDomainDto;
import com.nextuple.promise.sourcing.rule.persistence.domain.SourcingRulesConfigurationDomainDto;
import com.nextuple.promise.sourcing.rule.persistence.service.AttributeValuesPersistenceService;
import com.nextuple.promise.sourcing.rule.persistence.service.SourcingAttributePersistenceService;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class FetchRulesUtilTest {

  @Mock AttributeValuesPersistenceService attributeValuesPersistenceService;
  @Mock SourcingAttributePersistenceService sourcingAttributePersistenceService;
  @InjectMocks TestUtil testUtil;
  @InjectMocks FetchRulesUtil fetchRulesUtil;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("Validation for attribute definition: Happy Path")
  void validateAttributesDefinitionIdForScopeTest() throws CommonServiceException {
    String reqAttrFromDefinition = "23,24";
    String optionalAttrFromDefinition = "25";
    String generatedRule = "ABC:DEF:GHI";

    FetchRulesUtil.validateAttributesDefinitionIdForScope(
        reqAttrFromDefinition, optionalAttrFromDefinition, generatedRule);

    assertDoesNotThrow(
        () ->
            FetchRulesUtil.validateAttributesDefinitionIdForScope(
                reqAttrFromDefinition, optionalAttrFromDefinition, generatedRule));
  }

  @Test
  @DisplayName("Validation for attribute definition: Exception Path - Extra required attributes")
  void validateAttributesDefinitionIdForScopeExceptionTest() {
    String reqAttrFromDefinition = "23,34";
    String optionalAttrFromDefinition = "25";
    String generatedRule = "ABC::";

    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                FetchRulesUtil.validateAttributesDefinitionIdForScope(
                    reqAttrFromDefinition, optionalAttrFromDefinition, generatedRule));

    assertEquals(REQUIRED_ATTRIBUTES_LENGTH_ERROR_MESSAGE, ex.getMessage());
  }

  @Test
  @DisplayName("Validation for attribute definition: Exception Path - Extra total attributes")
  void validateAttributesDefinitionIdForScopeExceptionExtraAttributesTest() {
    String reqAttrFromDefinition = "23:24";
    String optionalAttrFromDefinition = "25:26";
    String generatedRule = "ABC:DEF:GHI";

    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                FetchRulesUtil.validateAttributesDefinitionIdForScope(
                    reqAttrFromDefinition, optionalAttrFromDefinition, generatedRule));

    assertEquals(TOTAL_ATTRIBUTES_LENGTH_ERROR_MESSAGE, ex.getMessage());
  }

  @Test
  @DisplayName("No mapping for the required attribute found in sourcing attribute")
  void processGetAllSourcingRuleDetailsByOrgIdTest4()
      throws PromiseEngineException, CommonServiceException {
    String rule1 = "Test rule 1";
    String rule2 = "Test rule 2";
    List<SourcingRulesConfigurationDomainDto> sourcingRulesConfigurationEntityList =
        new ArrayList<>();
    SourcingRulesConfigurationDomainDto sourcingRulesConfigurationEntity =
        testUtil.getSourcingRulesEntity();
    SourcingRulesConfigurationDomainDto sourcingRulesConfigurationEntity1 =
        testUtil.getSourcingRulesEntity();
    sourcingRulesConfigurationEntity1.setSourcingRule("V1:V2:V3");
    sourcingRulesConfigurationEntity.setSourcingRuleName(rule1);
    sourcingRulesConfigurationEntity1.setSourcingRuleName(rule2);
    sourcingRulesConfigurationEntityList.add(sourcingRulesConfigurationEntity);
    sourcingRulesConfigurationEntityList.add(sourcingRulesConfigurationEntity1);

    SourcingAttributesDefinitionDomainDto sourcingAttributesDefinitionEntity =
        testUtil.getSourcingRuleAttributesDefinitionEntity(
            SourcingAttributesDefinitionStatus.ACTIVE);
    sourcingAttributesDefinitionEntity.setOptAttributes("3,4");
    when(sourcingAttributePersistenceService.getSourcingAttributeById(anyLong()))
        .thenReturn(Optional.empty());
    SourcingAttributesDefinitionResponse sourcingAttributesDefinitionResponse =
        testUtil.getSourcingRuleAttributesDefinitionResponse(
            SourcingAttributesDefinitionStatus.ACTIVE);
    List<AttributeInfo> requiredAttrList = new ArrayList<>();
    Set<String> uniqueReqAttributes = new HashSet<>();
    String[] sourcingRuleValues = new String[] {"R1:R2:O1"};
    String[] reqAttributes = new String[] {"1", "2"};
    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                fetchRulesUtil.getRequiredAttributeDetails(
                    TestUtil.ORG_ID,
                    requiredAttrList,
                    uniqueReqAttributes,
                    sourcingRuleValues,
                    reqAttributes));
    assertEquals(
        "No mapping for the required attribute found in sourcing attribute", ex.getMessage());
  }

  @Test
  @DisplayName("No mapping for the optional attribute found in sourcing attribute")
  void processGetAllSourcingRuleDetailsByOrgIdTest5()
      throws PromiseEngineException, CommonServiceException {
    String rule1 = "Test rule 1";
    String rule2 = "Test rule 2";
    List<SourcingRulesConfigurationDomainDto> sourcingRulesConfigurationEntityList =
        new ArrayList<>();
    SourcingRulesConfigurationDomainDto sourcingRulesConfigurationEntity =
        testUtil.getSourcingRulesEntity();
    SourcingRulesConfigurationDomainDto sourcingRulesConfigurationEntity1 =
        testUtil.getSourcingRulesEntity();
    sourcingRulesConfigurationEntity1.setSourcingRule("V1:V2:V3");
    sourcingRulesConfigurationEntity.setSourcingRuleName(rule1);
    sourcingRulesConfigurationEntity1.setSourcingRuleName(rule2);
    sourcingRulesConfigurationEntityList.add(sourcingRulesConfigurationEntity);
    sourcingRulesConfigurationEntityList.add(sourcingRulesConfigurationEntity1);

    SourcingAttributesDefinitionDomainDto sourcingAttributesDefinitionEntity =
        testUtil.getSourcingRuleAttributesDefinitionEntity(
            SourcingAttributesDefinitionStatus.ACTIVE);
    sourcingAttributesDefinitionEntity.setOptAttributes("3,4");

    when(sourcingAttributePersistenceService.getSourcingAttributeById(anyLong()))
        .thenReturn(Optional.of(testUtil.getSourcingAttributeEntity()));

    when(sourcingAttributePersistenceService.getSourcingAttributeById(Long.parseLong("3")))
        .thenReturn(Optional.empty());
    SourcingAttributesDefinitionResponse sourcingAttributesDefinitionResponse =
        testUtil.getSourcingRuleAttributesDefinitionResponse(
            SourcingAttributesDefinitionStatus.ACTIVE);

    AttributeValuesDomainDto attributeValuesEntity1 =
        testUtil.getAttributeValuesEntity1(1L, 3L, "O1");
    AttributeValuesDomainDto attributeValuesEntity2 =
        testUtil.getAttributeValuesEntity1(2L, 4L, "O2");
    when(attributeValuesPersistenceService.getAllAttributeValues(any()))
        .thenReturn(List.of(attributeValuesEntity1, attributeValuesEntity2));

    List<AttributeInfo> optAttrList = new ArrayList<>();
    Set<String> uniqueOptAttributes = new HashSet<>();
    String[] sourcingRuleValues = new String[] {"R1:R2:O1:O2"};
    String[] reqAttributes = new String[] {"1", "2"};
    String[] optAttributes = new String[] {"3", "4"};
    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () -> {
              fetchRulesUtil.getOptionalAttributeDetails(
                  TestUtil.ORG_ID,
                  optAttrList,
                  uniqueOptAttributes,
                  sourcingRuleValues,
                  reqAttributes,
                  optAttributes);
            });
    assertEquals(
        "No mapping for the optional attribute found in sourcing attribute", ex.getMessage());
  }

  @Test
  @DisplayName("No optional attribute is provided in group definition")
  void processGetAllSourcingRuleDetailsByOrgIdTest6()
      throws PromiseEngineException, CommonServiceException {
    List<AttributeInfo> reqAttributesList = new ArrayList<>();
    fetchRulesUtil.getRequiredAttributeDetails(
        TestUtil.ORG_ID,
        reqAttributesList,
        new HashSet<>(),
        new String[0],
        new String[] {"70261", "70262"});
    Assertions.assertEquals(0, reqAttributesList.size());
  }

  @Test
  @DisplayName("Happy Path for getOptionalAttributeDetails")
  void getOptionalAttributeDetailsTest1() throws PromiseEngineException, CommonServiceException {
    Set<String> uniqueOptAttributes = new HashSet<>();
    uniqueOptAttributes.add("1");
    uniqueOptAttributes.add("2");
    uniqueOptAttributes.add("3");
    String[] sourcingRuleValues = new String[] {"V1.1", "V2.1", "V3.1", "V4.1", "", "V6.2"};
    String[] reqAttributes = new String[] {"1", "2", "3"};
    String[] optAttributes = new String[] {"4", "5", "6"};

    when(attributeValuesPersistenceService.getAllAttributeValues(any()))
        .thenReturn(
            List.of(
                testUtil.getAttributeValuesEntity1(1L, 4L, "V4.1"),
                testUtil.getAttributeValuesEntity1(2L, 4L, "V4.2"),
                testUtil.getAttributeValuesEntity1(3L, 5L, "V5.1"),
                testUtil.getAttributeValuesEntity1(4L, 5L, "V5.2"),
                testUtil.getAttributeValuesEntity1(5L, 6L, "V6.1")));

    SourcingAttributeDomainDto sourcingAttributeEntity4 =
        new SourcingAttributeDomainDto(4L, TestUtil.ORG_ID, "Attribute 4", "jsonPath", false, null);
    SourcingAttributeDomainDto sourcingAttributeEntity5 =
        new SourcingAttributeDomainDto(5L, TestUtil.ORG_ID, "Attribute 5", "jsonPath", false, null);
    SourcingAttributeDomainDto sourcingAttributeEntity6 =
        new SourcingAttributeDomainDto(6L, TestUtil.ORG_ID, "Attribute 6", "jsonPath", false, null);

    when(sourcingAttributePersistenceService.getSourcingAttributeById(Long.parseLong("4")))
        .thenReturn(Optional.of(sourcingAttributeEntity4));
    when(sourcingAttributePersistenceService.getSourcingAttributeById(Long.parseLong("5")))
        .thenReturn(Optional.of(sourcingAttributeEntity5));
    when(sourcingAttributePersistenceService.getSourcingAttributeById(Long.parseLong("6")))
        .thenReturn(Optional.of(sourcingAttributeEntity6));

    List<AttributeInfo> optAttrList = new ArrayList<>();
    fetchRulesUtil.getOptionalAttributeDetails(
        TestUtil.ORG_ID,
        optAttrList,
        uniqueOptAttributes,
        sourcingRuleValues,
        reqAttributes,
        optAttributes);
    Assertions.assertEquals(3, optAttrList.size());
    Assertions.assertEquals("4", optAttrList.get(0).getAttributeId());
    Assertions.assertEquals("V4.1", optAttrList.get(0).getAttributeValue());
    Assertions.assertEquals("Attribute 4", optAttrList.get(0).getAttributeName());
    Assertions.assertEquals("5", optAttrList.get(1).getAttributeId());
    Assertions.assertEquals("", optAttrList.get(1).getAttributeValue());
    Assertions.assertEquals("Attribute 5", optAttrList.get(1).getAttributeName());
    Assertions.assertEquals("6", optAttrList.get(2).getAttributeId());
    Assertions.assertEquals("V6.2", optAttrList.get(2).getAttributeValue());
    Assertions.assertEquals("Attribute 6", optAttrList.get(2).getAttributeName());
  }
}
