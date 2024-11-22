package com.nextuple.promise.sourcing.rule.service.impl;

import static org.junit.jupiter.api.Assertions.*;

import com.nextuple.promise.sourcing.rule.TestUtil;
import com.nextuple.promise.sourcing.rule.api.domain.enums.SourcingAttributesDefinitionScopeEnum;
import com.nextuple.promise.sourcing.rule.api.domain.enums.SourcingAttributesDefinitionStatus;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.SourcingAttributesDefinitionResponse;
import com.nextuple.promise.sourcing.rule.persistence.domain.SourcingRulesConfigurationDomainDto;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

class SourcingRuleConfigurationImplTest {
  @InjectMocks SourcingRuleConfigurationImpl ruleRetrievalService;
  @InjectMocks TestUtil testUtil;

  @BeforeEach
  void setup() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("All attributes present")
  void filterAllMatchingRulesByScoringTestForAllAttributesPresent() {
    String requiredAttributeValues = "STANDARD:KITCHEN";
    String optionalAttributeValues = "random:SHIP";
    SourcingAttributesDefinitionResponse sourcingAttributesDefinition =
        testUtil.getSourcingRuleAttributesDefinitionResponse(
            SourcingAttributesDefinitionStatus.ACTIVE);
    sourcingAttributesDefinition.setScope(SourcingAttributesDefinitionScopeEnum.SOURCING_RULE);
    sourcingAttributesDefinition.setReqAttributes("100,101");
    sourcingAttributesDefinition.setOptAttributes("102,103");
    List<SourcingRulesConfigurationDomainDto> rulesList = testUtil.getSourcingRulesList();
    List<SourcingRulesConfigurationDomainDto> rankedRules =
        ruleRetrievalService.filterAllMatchingRulesByScoring(
            rulesList,
            requiredAttributeValues,
            optionalAttributeValues,
            2,
            sourcingAttributesDefinition);
    Assertions.assertNotNull(rulesList);
    Assertions.assertEquals(rulesList.get(3), rankedRules.getFirst());
  }

  @Test
  @DisplayName("All attributes present without exact match")
  void filterAllMatchingRulesByScoringTestWithoutExactMatch() {
    String requiredAttributeValues = "STANDARD:KITCHEN";
    String optionalAttributeValues = "random:SHIP";
    SourcingAttributesDefinitionResponse sourcingAttributesDefinition =
        testUtil.getSourcingRuleAttributesDefinitionResponse(
            SourcingAttributesDefinitionStatus.ACTIVE);
    sourcingAttributesDefinition.setScope(SourcingAttributesDefinitionScopeEnum.SOURCING_RULE);
    sourcingAttributesDefinition.setReqAttributes("100,101");
    sourcingAttributesDefinition.setOptAttributes("102,103");
    List<SourcingRulesConfigurationDomainDto> rulesList = testUtil.getSourcingRulesList();
    rulesList.remove(3);
    List<SourcingRulesConfigurationDomainDto> rankedRules =
        ruleRetrievalService.filterAllMatchingRulesByScoring(
            rulesList,
            requiredAttributeValues,
            optionalAttributeValues,
            2,
            sourcingAttributesDefinition);
    Assertions.assertNotNull(rulesList);
    Assertions.assertEquals(rulesList.get(0), rankedRules.getFirst());
  }

  @Test
  @DisplayName("Exact match all attributes")
  void filterAllMatchingRulesByScoringTestExactMatchAllAttributes() {
    String requiredAttributeValues = "STANDARD:KITCHEN";
    String optionalAttributeValues = "XYZ:SHIP";
    SourcingAttributesDefinitionResponse sourcingAttributesDefinition =
        testUtil.getSourcingRuleAttributesDefinitionResponse(
            SourcingAttributesDefinitionStatus.ACTIVE);
    sourcingAttributesDefinition.setScope(SourcingAttributesDefinitionScopeEnum.SOURCING_RULE);
    sourcingAttributesDefinition.setReqAttributes("100,101");
    sourcingAttributesDefinition.setOptAttributes("102,103");
    List<SourcingRulesConfigurationDomainDto> rulesList = testUtil.getSourcingRulesList();
    List<SourcingRulesConfigurationDomainDto> rankedRules =
        ruleRetrievalService.filterAllMatchingRulesByScoring(
            rulesList,
            requiredAttributeValues,
            optionalAttributeValues,
            2,
            sourcingAttributesDefinition);
    Assertions.assertNotNull(rulesList);
    Assertions.assertEquals(rulesList.get(4), rankedRules.getFirst());
  }

  @Test
  @DisplayName("Exact match required attributes and first optional attribute")
  void filterAllMatchingRulesByScoringTestReqAttributeAndFirstOptionalAttributeMatch() {
    String requiredAttributeValues = "STANDARD:KITCHEN";
    String optionalAttributeValues = "XYZ:SHIP";
    SourcingAttributesDefinitionResponse sourcingAttributesDefinition =
        testUtil.getSourcingRuleAttributesDefinitionResponse(
            SourcingAttributesDefinitionStatus.ACTIVE);
    sourcingAttributesDefinition.setScope(SourcingAttributesDefinitionScopeEnum.SOURCING_RULE);
    sourcingAttributesDefinition.setReqAttributes("100,101");
    sourcingAttributesDefinition.setOptAttributes("102,103");
    List<SourcingRulesConfigurationDomainDto> rulesList = testUtil.getSourcingRulesList();
    rulesList.remove(4);
    List<SourcingRulesConfigurationDomainDto> rankedRules =
        ruleRetrievalService.filterAllMatchingRulesByScoring(
            rulesList,
            requiredAttributeValues,
            optionalAttributeValues,
            2,
            sourcingAttributesDefinition);
    Assertions.assertNotNull(rulesList);
    Assertions.assertEquals(rulesList.get(1), rankedRules.getFirst());
  }

  @Test
  @DisplayName("Exact match required attributes and second optional attribute")
  void filterAllMatchingRulesByScoringTestReqAttributeAndSecondOptionalAttributeMatch() {
    String requiredAttributeValues = "STANDARD:KITCHEN";
    String optionalAttributeValues = "XYZ:SHIP";
    SourcingAttributesDefinitionResponse sourcingAttributesDefinition =
        testUtil.getSourcingRuleAttributesDefinitionResponse(
            SourcingAttributesDefinitionStatus.ACTIVE);
    sourcingAttributesDefinition.setScope(SourcingAttributesDefinitionScopeEnum.SOURCING_RULE);
    sourcingAttributesDefinition.setReqAttributes("100,101");
    sourcingAttributesDefinition.setOptAttributes("102,103");
    List<SourcingRulesConfigurationDomainDto> rulesList = testUtil.getSourcingRulesList();
    rulesList.remove(4);
    rulesList.remove(1);
    List<SourcingRulesConfigurationDomainDto> rankedRules =
        ruleRetrievalService.filterAllMatchingRulesByScoring(
            rulesList,
            requiredAttributeValues,
            optionalAttributeValues,
            2,
            sourcingAttributesDefinition);
    Assertions.assertNotNull(rulesList);
    Assertions.assertEquals(rulesList.get(2), rankedRules.getFirst());
  }

  @Test
  @DisplayName("Only required attribute exact match")
  void filterAllMatchingRulesByScoringTestRequiredAttributeMatch() {
    String requiredAttributeValues = "STANDARD:KITCHEN";
    String optionalAttributeValues = "XYZ:SHIP";
    SourcingAttributesDefinitionResponse sourcingAttributesDefinition =
        testUtil.getSourcingRuleAttributesDefinitionResponse(
            SourcingAttributesDefinitionStatus.ACTIVE);
    sourcingAttributesDefinition.setScope(SourcingAttributesDefinitionScopeEnum.SOURCING_RULE);
    sourcingAttributesDefinition.setReqAttributes("100,101");
    sourcingAttributesDefinition.setOptAttributes("102,103");
    List<SourcingRulesConfigurationDomainDto> rulesList = testUtil.getSourcingRulesList();
    rulesList.remove(4);
    rulesList.remove(1);
    rulesList.remove(2);

    List<SourcingRulesConfigurationDomainDto> rankedRules =
        ruleRetrievalService.filterAllMatchingRulesByScoring(
            rulesList,
            requiredAttributeValues,
            optionalAttributeValues,
            2,
            sourcingAttributesDefinition);
    Assertions.assertNotNull(rulesList);
    Assertions.assertEquals(rulesList.get(0), rankedRules.getFirst());
  }

  @Test
  @DisplayName("Only 1 optional attribute match")
  void filterAllMatchingRulesByScoringTestOneOptionalAttributeMatch() {
    String requiredAttributeValues = "STANDARD:KITCHEN";
    String optionalAttributeValues = "XYZ:Random";
    SourcingAttributesDefinitionResponse sourcingAttributesDefinition =
        testUtil.getSourcingRuleAttributesDefinitionResponse(
            SourcingAttributesDefinitionStatus.ACTIVE);
    sourcingAttributesDefinition.setScope(SourcingAttributesDefinitionScopeEnum.SOURCING_RULE);
    sourcingAttributesDefinition.setReqAttributes("100,101");
    sourcingAttributesDefinition.setOptAttributes("102,103");
    List<SourcingRulesConfigurationDomainDto> rulesList = testUtil.getSourcingRulesList();
    List<SourcingRulesConfigurationDomainDto> rankedRules =
        ruleRetrievalService.filterAllMatchingRulesByScoring(
            rulesList,
            requiredAttributeValues,
            optionalAttributeValues,
            2,
            sourcingAttributesDefinition);
    Assertions.assertNotNull(rulesList);
    Assertions.assertEquals(rulesList.get(1), rankedRules.getFirst());
  }

  @Test
  @DisplayName("1 optional attribute match and other random value")
  void filterAllMatchingRulesByScoringTestOneOptionalAttributeMatchAndOneRandomValue() {
    String requiredAttributeValues = "STANDARD:KITCHEN";
    String optionalAttributeValues = "XYZ:Random";
    SourcingAttributesDefinitionResponse sourcingAttributesDefinition =
        testUtil.getSourcingRuleAttributesDefinitionResponse(
            SourcingAttributesDefinitionStatus.ACTIVE);
    sourcingAttributesDefinition.setScope(SourcingAttributesDefinitionScopeEnum.SOURCING_RULE);
    sourcingAttributesDefinition.setReqAttributes("100,101");
    sourcingAttributesDefinition.setOptAttributes("102,103");
    List<SourcingRulesConfigurationDomainDto> rulesList = testUtil.getSourcingRulesList();
    rulesList.remove(1);
    List<SourcingRulesConfigurationDomainDto> rankedRules =
        ruleRetrievalService.filterAllMatchingRulesByScoring(
            rulesList,
            requiredAttributeValues,
            optionalAttributeValues,
            2,
            sourcingAttributesDefinition);
    Assertions.assertNotNull(rulesList);
    Assertions.assertEquals(rulesList.get(0), rankedRules.getFirst());
  }

  @Test
  @DisplayName("missing first optional attribute value")
  void filterAllMatchingRulesByScoringTestOneMissingOptionalAttributeValue() {
    String requiredAttributeValues = "STANDARD:KITCHEN";
    String optionalAttributeValues = ":SHIP";
    SourcingAttributesDefinitionResponse sourcingAttributesDefinition =
        testUtil.getSourcingRuleAttributesDefinitionResponse(
            SourcingAttributesDefinitionStatus.ACTIVE);
    sourcingAttributesDefinition.setScope(SourcingAttributesDefinitionScopeEnum.SOURCING_RULE);
    sourcingAttributesDefinition.setReqAttributes("100,101");
    sourcingAttributesDefinition.setOptAttributes("102,103");
    List<SourcingRulesConfigurationDomainDto> rulesList = testUtil.getSourcingRulesList();
    rulesList.remove(3);
    List<SourcingRulesConfigurationDomainDto> rankedRules =
        ruleRetrievalService.filterAllMatchingRulesByScoring(
            rulesList,
            requiredAttributeValues,
            optionalAttributeValues,
            2,
            sourcingAttributesDefinition);
    Assertions.assertNotNull(rulesList);
    Assertions.assertEquals(rulesList.get(3), rankedRules.getFirst());
  }

  @Test
  @DisplayName("missing second optional attribute value")
  void filterAllMatchingRulesByScoringTestMissingSecondOptionalValue() {
    String requiredAttributeValues = "STANDARD:KITCHEN";
    String optionalAttributeValues = "XYZ:";
    SourcingAttributesDefinitionResponse sourcingAttributesDefinition =
        testUtil.getSourcingRuleAttributesDefinitionResponse(
            SourcingAttributesDefinitionStatus.ACTIVE);
    sourcingAttributesDefinition.setScope(SourcingAttributesDefinitionScopeEnum.SOURCING_RULE);
    sourcingAttributesDefinition.setReqAttributes("100,101");
    sourcingAttributesDefinition.setOptAttributes("102,103");
    List<SourcingRulesConfigurationDomainDto> rulesList = testUtil.getSourcingRulesList();
    rulesList.remove(1);
    List<SourcingRulesConfigurationDomainDto> rankedRules =
        ruleRetrievalService.filterAllMatchingRulesByScoring(
            rulesList,
            requiredAttributeValues,
            optionalAttributeValues,
            2,
            sourcingAttributesDefinition);
    Assertions.assertNotNull(rulesList);
    Assertions.assertEquals(rulesList.get(3), rankedRules.getFirst());
  }
}
