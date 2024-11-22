/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.promise.sourcing.rule.service.impl;

import com.nextuple.promise.sourcing.rule.api.domain.services.RulesRetrievalService;
import com.nextuple.promise.sourcing.rule.persistence.domain.GroupDefinitionDomainDto;
import com.nextuple.promise.sourcing.rule.persistence.domain.RulesConfigurationDomainDto;
import com.nextuple.promise.sourcing.rule.persistence.domain.SourcingRulesConfigurationDomainDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

class RuleRetrievalFactoryTest {
  @InjectMocks RuleRetrievalFactory ruleRetrievalFactory;
  @Mock SourcingRuleConfigurationImpl sourcingRuleRetrieval;
  @Mock RuleConfigImpl ruleRetrieval;
  @Mock GroupDefinitionRuleImpl groupDefinitionRuleRetrieval;

  @BeforeEach
  void init() {
    MockitoAnnotations.openMocks(this);
    ReflectionTestUtils.setField(
        ruleRetrievalFactory, "sourcingRuleRetrieval", sourcingRuleRetrieval);
    ReflectionTestUtils.setField(ruleRetrievalFactory, "ruleRetrieval", ruleRetrieval);
    ReflectionTestUtils.setField(
        ruleRetrievalFactory, "groupDefinitionRuleRetrieval", groupDefinitionRuleRetrieval);
  }

  @Test
  @DisplayName("Test sourcing group and rule config factory response")
  void getRuleRetrievalServiceSourcingTest() {
    RulesRetrievalService sourcingRetrievalService =
        ruleRetrievalFactory.getRuleRetrievalService(new SourcingRulesConfigurationDomainDto());
    RulesRetrievalService groupRetrievalService =
        ruleRetrievalFactory.getRuleRetrievalService(new GroupDefinitionDomainDto());
    RulesRetrievalService ruleConfigRetrievalService =
        ruleRetrievalFactory.getRuleRetrievalService(new RulesConfigurationDomainDto());
    Assertions.assertTrue(sourcingRetrievalService instanceof SourcingRuleConfigurationImpl);
    Assertions.assertTrue(groupRetrievalService instanceof GroupDefinitionRuleImpl);
    Assertions.assertTrue(ruleConfigRetrievalService instanceof RuleConfigImpl);
  }
}
