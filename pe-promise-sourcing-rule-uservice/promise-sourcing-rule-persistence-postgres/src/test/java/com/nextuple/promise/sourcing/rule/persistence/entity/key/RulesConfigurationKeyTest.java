/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.promise.sourcing.rule.persistence.entity.key;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.nextuple.promise.sourcing.rule.api.domain.enums.RulesConfigurationModuleNameEnum;
import com.nextuple.promise.sourcing.rule.api.domain.enums.SourcingAttributesDefinitionScopeEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RulesConfigurationKeyTest {

  private RulesConfigurationKey key;

  @BeforeEach
  void setUp() {
    key = new RulesConfigurationKey();
  }

  @Test
  @DisplayName("Default Constructor Test")
  void defaultConstructorTest() {
    assertNull(key.getOrgId());
    assertNull(key.getRuleName());
    assertNull(key.getRule());
    assertNull(key.getModuleName());
    assertNull(key.getScope());
  }

  @Test
  @DisplayName("AllArgsConstructor Test")
  void allArgsConstructorTest() {
    RulesConfigurationKey key =
        new RulesConfigurationKey(
            "ORG1",
            "Rule1",
            "RuleBody",
            RulesConfigurationModuleNameEnum.PROCESSING_TIME,
            SourcingAttributesDefinitionScopeEnum.ML_RULE);

    assertEquals("ORG1", key.getOrgId());
    assertEquals("Rule1", key.getRuleName());
    assertEquals("RuleBody", key.getRule());
    assertEquals(RulesConfigurationModuleNameEnum.PROCESSING_TIME, key.getModuleName());
    assertEquals(SourcingAttributesDefinitionScopeEnum.ML_RULE, key.getScope());
  }

  @Test
  @DisplayName("Builder Test")
  void builderTest() {
    RulesConfigurationKey key =
        RulesConfigurationKey.builder()
            .orgId("ORG2")
            .ruleName("Rule2")
            .rule("RuleText")
            .moduleName(RulesConfigurationModuleNameEnum.PROCESSING_TIME)
            .scope(SourcingAttributesDefinitionScopeEnum.ML_RULE)
            .build();

    assertEquals("ORG2", key.getOrgId());
    assertEquals("Rule2", key.getRuleName());
    assertEquals("RuleText", key.getRule());
    assertEquals(RulesConfigurationModuleNameEnum.PROCESSING_TIME, key.getModuleName());
    assertEquals(SourcingAttributesDefinitionScopeEnum.ML_RULE, key.getScope());
  }

  @Test
  @DisplayName("Getter and Setter Test")
  void getterSetterTest() {
    key.setOrgId("ORG3");
    key.setRuleName("Rule3");
    key.setRule("RuleScript");
    key.setModuleName(RulesConfigurationModuleNameEnum.PROCESSING_TIME);
    key.setScope(SourcingAttributesDefinitionScopeEnum.ML_RULE);

    assertEquals("ORG3", key.getOrgId());
    assertEquals("Rule3", key.getRuleName());
    assertEquals("RuleScript", key.getRule());
    assertEquals(RulesConfigurationModuleNameEnum.PROCESSING_TIME, key.getModuleName());
    assertEquals(SourcingAttributesDefinitionScopeEnum.ML_RULE, key.getScope());
  }

  @Test
  @DisplayName("Equals and HashCode Test")
  void equalsHashCodeTest() {
    RulesConfigurationKey key1 =
        RulesConfigurationKey.builder()
            .orgId("ORG1")
            .ruleName("Rule1")
            .rule("RuleText")
            .moduleName(RulesConfigurationModuleNameEnum.PROCESSING_TIME)
            .scope(SourcingAttributesDefinitionScopeEnum.ML_RULE)
            .build();

    RulesConfigurationKey key2 =
        RulesConfigurationKey.builder()
            .orgId("ORG1")
            .ruleName("Rule1")
            .rule("RuleText")
            .moduleName(RulesConfigurationModuleNameEnum.PROCESSING_TIME)
            .scope(SourcingAttributesDefinitionScopeEnum.ML_RULE)
            .build();

    assertEquals(key1, key2);
    assertEquals(key1.hashCode(), key2.hashCode());

    RulesConfigurationKey differentKey =
        RulesConfigurationKey.builder()
            .orgId("ORG2")
            .ruleName("Rule2")
            .rule("DifferentRule")
            .moduleName(RulesConfigurationModuleNameEnum.PROCESSING_TIME)
            .scope(SourcingAttributesDefinitionScopeEnum.SOURCING_RULE)
            .build();

    assertNotEquals(key1, differentKey);
  }

  @Test
  @DisplayName("ToString Test")
  void toStringTest() {
    RulesConfigurationKey key =
        RulesConfigurationKey.builder()
            .orgId("ORG1")
            .ruleName("Rule1")
            .rule("RuleText")
            .moduleName(RulesConfigurationModuleNameEnum.PROCESSING_TIME)
            .scope(SourcingAttributesDefinitionScopeEnum.ML_RULE)
            .build();

    String expectedToString =
        "RulesConfigurationKey(orgId=ORG1, ruleName=Rule1, rule=RuleText, moduleName=PROCESSING_TIME, scope=ML_RULE)";
    assertEquals(expectedToString, key.toString());
  }
}
