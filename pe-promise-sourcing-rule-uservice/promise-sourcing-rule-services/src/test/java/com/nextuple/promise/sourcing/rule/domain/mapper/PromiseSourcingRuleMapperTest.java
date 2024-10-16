/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.promise.sourcing.rule.domain.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nextuple.promise.sourcing.rule.TestUtil;
import com.nextuple.promise.sourcing.rule.api.domain.dto.PromiseSourcingRuleDto;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.CreatePromiseSourcingRuleRequest;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.UpdatePromiseSourcingRuleRequest;
import com.nextuple.promise.sourcing.rule.persistence.domain.PromiseSourcingRuleDomainDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

class PromiseSourcingRuleMapperTest {

  @InjectMocks private TestUtil testUtil;

  private static final PromiseSourcingRuleMapper INSTANCE =
      Mappers.getMapper(PromiseSourcingRuleMapper.class);

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void convertToPromiseSourcingRuleDtoTest() {
    PromiseSourcingRuleDomainDto promiseSourcingRule = testUtil.getPromiseSourcingRule();

    PromiseSourcingRuleDto received_dto =
        INSTANCE.convertToPromiseSourcingRuleDto(promiseSourcingRule);
    assertEquals(promiseSourcingRule.getPriority(), received_dto.getPriority());
  }

  @Test
  void convertToPromiseSourcingRuleEntityTest() {
    PromiseSourcingRuleDto promiseSourcingRuleDto = testUtil.getPromiseSourcingRuleDto();

    PromiseSourcingRuleDomainDto received_entity =
        INSTANCE.convertToPromiseSourcingRuleEntity(promiseSourcingRuleDto);
    assertEquals(promiseSourcingRuleDto.getPriority(), received_entity.getPriority());
  }

  @Test
  void insertValuesFromUpdatePromiseSourcingRuleRequestToEntityTest() {
    UpdatePromiseSourcingRuleRequest updatePromiseSourcingRuleRequest =
        testUtil.getUpdatePromiseSourcingRuleRequest();
    PromiseSourcingRuleDomainDto mockPromiseSourcingRule = testUtil.getPromiseSourcingRule();

    INSTANCE.insertValuesFromUpdatePromiseSourcingRuleRequestToEntity(
        updatePromiseSourcingRuleRequest, mockPromiseSourcingRule);

    assertEquals(
        updatePromiseSourcingRuleRequest.getSourceNodes(),
        mockPromiseSourcingRule.getSourceNodes());
  }

  @Test
  void convertFromCreatePromiseSourcingRuleRequestToEntityTest() {
    CreatePromiseSourcingRuleRequest createPromiseSourcingRuleRequest =
        testUtil.getPromiseSourcingRuleCreationRequest();

    PromiseSourcingRuleDomainDto received_entity =
        INSTANCE.convertFromCreatePromiseSourcingRuleRequestToEntity(
            createPromiseSourcingRuleRequest);
    assertEquals(createPromiseSourcingRuleRequest.getPriority(), received_entity.getPriority());
  }
}
