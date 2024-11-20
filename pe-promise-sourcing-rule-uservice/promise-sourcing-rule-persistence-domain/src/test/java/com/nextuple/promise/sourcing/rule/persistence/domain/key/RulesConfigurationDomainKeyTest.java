/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.promise.sourcing.rule.persistence.domain.key;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.nextuple.promise.sourcing.rule.api.domain.enums.RulesConfigurationModuleNameEnum;
import com.nextuple.promise.sourcing.rule.api.domain.enums.SourcingAttributesDefinitionScopeEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RulesConfigurationDomainKeyTest {

  private RulesConfigurationDomainKey domainKey;

  @BeforeEach
  void setUp() {
    domainKey = new RulesConfigurationDomainKey();
  }

  @Test
  @DisplayName("Default constructor test")
  void testDefaultConstructor() {
    assertNull(domainKey.getOrgId());
    assertNull(domainKey.getRuleName());
    assertNull(domainKey.getRule());
    assertNull(domainKey.getModuleName());
    assertNull(domainKey.getScope());
  }

  @Test
  @DisplayName("All arguments constructor test")
  void testAllArgsConstructor() {
    RulesConfigurationDomainKey key =
        new RulesConfigurationDomainKey(
            "ORG1",
            "Rule1",
            "SomeRule",
            RulesConfigurationModuleNameEnum.PROCESSING_TIME,
            SourcingAttributesDefinitionScopeEnum.ML_RULE);

    assertEquals("ORG1", key.getOrgId());
    assertEquals("Rule1", key.getRuleName());
    assertEquals("SomeRule", key.getRule());
    assertEquals(RulesConfigurationModuleNameEnum.PROCESSING_TIME, key.getModuleName());
    assertEquals(SourcingAttributesDefinitionScopeEnum.ML_RULE, key.getScope());
  }

  @Test
  @DisplayName("Builder constructor test")
  void testBuilderConstructor() {
    RulesConfigurationDomainKey key =
        RulesConfigurationDomainKey.builder()
            .orgId("ORG2")
            .ruleName("Rule2")
            .rule("AnotherRule")
            .moduleName(RulesConfigurationModuleNameEnum.PROCESSING_TIME)
            .scope(SourcingAttributesDefinitionScopeEnum.ML_RULE)
            .build();

    assertEquals("ORG2", key.getOrgId());
    assertEquals("Rule2", key.getRuleName());
    assertEquals("AnotherRule", key.getRule());
    assertEquals(RulesConfigurationModuleNameEnum.PROCESSING_TIME, key.getModuleName());
    assertEquals(SourcingAttributesDefinitionScopeEnum.ML_RULE, key.getScope());
  }

  @Test
  @DisplayName("Getter Setter test")
  void testGetterSetter() {
    domainKey.setOrgId("ORG3");
    domainKey.setRuleName("Rule3");
    domainKey.setRule("RuleBody");
    domainKey.setModuleName(RulesConfigurationModuleNameEnum.PROCESSING_TIME);
    domainKey.setScope(SourcingAttributesDefinitionScopeEnum.ML_RULE);

    assertEquals("ORG3", domainKey.getOrgId());
    assertEquals("Rule3", domainKey.getRuleName());
    assertEquals("RuleBody", domainKey.getRule());
    assertEquals(RulesConfigurationModuleNameEnum.PROCESSING_TIME, domainKey.getModuleName());
    assertEquals(SourcingAttributesDefinitionScopeEnum.ML_RULE, domainKey.getScope());
  }

  @Test
  @DisplayName("Equals and Hash Code test")
  void testEqualsAndHashCode() {
    RulesConfigurationDomainKey key1 =
        RulesConfigurationDomainKey.builder()
            .orgId("ORG1")
            .ruleName("Rule1")
            .rule("SomeRule")
            .moduleName(RulesConfigurationModuleNameEnum.PROCESSING_TIME)
            .scope(SourcingAttributesDefinitionScopeEnum.SOURCING_RULE)
            .build();

    RulesConfigurationDomainKey key2 =
        RulesConfigurationDomainKey.builder()
            .orgId("ORG1")
            .ruleName("Rule1")
            .rule("SomeRule")
            .moduleName(RulesConfigurationModuleNameEnum.PROCESSING_TIME)
            .scope(SourcingAttributesDefinitionScopeEnum.SOURCING_RULE)
            .build();

    assertEquals(key1, key2);
    assertEquals(key1.hashCode(), key2.hashCode());

    RulesConfigurationDomainKey differentKey =
        RulesConfigurationDomainKey.builder()
            .orgId("ORG2")
            .ruleName("Rule2")
            .rule("DifferentRule")
            .moduleName(RulesConfigurationModuleNameEnum.PROCESSING_TIME)
            .scope(SourcingAttributesDefinitionScopeEnum.ML_RULE)
            .build();

    assertNotEquals(key1, differentKey);
  }

  @Test
  @DisplayName("toString test")
  void testToString() {
    RulesConfigurationDomainKey key =
        RulesConfigurationDomainKey.builder()
            .orgId("ORG1")
            .ruleName("Rule1")
            .rule("SomeRule")
            .moduleName(RulesConfigurationModuleNameEnum.PROCESSING_TIME)
            .scope(SourcingAttributesDefinitionScopeEnum.ML_RULE)
            .build();

    String expectedString =
        "RulesConfigurationDomainKey(orgId=ORG1, ruleName=Rule1, rule=SomeRule, moduleName=PROCESSING_TIME, scope=ML_RULE)";
    assertEquals(expectedString, key.toString());
  }
}
