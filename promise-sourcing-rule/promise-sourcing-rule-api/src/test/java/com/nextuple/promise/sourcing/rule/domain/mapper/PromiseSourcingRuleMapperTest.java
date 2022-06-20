package com.nextuple.promise.sourcing.rule.domain.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nextuple.promise.sourcing.rule.api.domain.dto.PromiseSourcingRuleDto;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.CreatePromiseSourcingRuleRequest;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.UpdatePromiseSourcingRuleRequest;
import com.nextuple.promise.sourcing.rule.domain.entity.PromiseSourcingRule;
import com.nextuple.promise.sourcing.rule.utils.TestUtil;
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
    PromiseSourcingRule promiseSourcingRule = testUtil.getPromiseSourcingRule();

    PromiseSourcingRuleDto received_dto =
        INSTANCE.convertToPromiseSourcingRuleDto(promiseSourcingRule);
    assertEquals(promiseSourcingRule.getPriority(), received_dto.getPriority());
  }

  @Test
  void convertToPromiseSourcingRuleEntityTest() {
    PromiseSourcingRuleDto promiseSourcingRuleDto = testUtil.getPromiseSourcingRuleDto();

    PromiseSourcingRule received_entity =
        INSTANCE.convertToPromiseSourcingRuleEntity(promiseSourcingRuleDto);
    assertEquals(promiseSourcingRuleDto.getPriority(), received_entity.getPriority());
  }

  @Test
  void insertValuesFromUpdatePromiseSourcingRuleRequestToEntityTest() {
    UpdatePromiseSourcingRuleRequest updatePromiseSourcingRuleRequest =
        testUtil.getUpdatePromiseSourcingRuleRequest();
    PromiseSourcingRule mockPromiseSourcingRule = testUtil.getPromiseSourcingRule();

    INSTANCE.insertValuesFromUpdatePromiseSourcingRuleRequestToEntity(
        updatePromiseSourcingRuleRequest, mockPromiseSourcingRule);

    assertEquals(
        updatePromiseSourcingRuleRequest.getAllocationRuleId(),
        mockPromiseSourcingRule.getAllocationRuleId());
    assertEquals(
        updatePromiseSourcingRuleRequest.getDestinationGeoZone(),
        mockPromiseSourcingRule.getDestinationGeoZone());
    assertEquals(
        updatePromiseSourcingRuleRequest.getSourceNodes(),
        mockPromiseSourcingRule.getSourceNodes());
  }

  @Test
  void convertFromCreatePromiseSourcingRuleRequestToEntityTest() {
    CreatePromiseSourcingRuleRequest createPromiseSourcingRuleRequest =
        testUtil.getPromiseSourcingRuleCreationRequest();

    PromiseSourcingRule received_entity =
        INSTANCE.convertFromCreatePromiseSourcingRuleRequestToEntity(
            createPromiseSourcingRuleRequest);
    assertEquals(createPromiseSourcingRuleRequest.getPriority(), received_entity.getPriority());
  }
}
