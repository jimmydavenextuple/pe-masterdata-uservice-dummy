package com.nextuple.promise.sourcing.rule.domain.mapper;

import com.nextuple.promise.sourcing.rule.api.domain.dto.PromiseSourcingRuleDto;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.CreatePromiseSourcingRuleRequest;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.UpdatePromiseSourcingRuleRequest;
import com.nextuple.promise.sourcing.rule.domain.entity.PromiseSourcingRule;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PromiseSourcingRuleMapper {
  PromiseSourcingRule convertToPromiseSourcingRuleEntity(
      PromiseSourcingRuleDto promiseSourcingRuleDto);

  PromiseSourcingRuleDto convertToPromiseSourcingRuleDto(PromiseSourcingRule promiseSourcingRule);

  @Mapping(target = "sourceNodes", ignore = true)
  void insertValuesFromUpdatePromiseSourcingRuleRequestToEntity(
      UpdatePromiseSourcingRuleRequest updatePromiseSourcingRuleRequest,
      @MappingTarget PromiseSourcingRule promiseSourcingRule);

  @AfterMapping
  default void insertValuesFromUpdatePromiseSourcingRuleRequestToEntityMappingSourceNodes(
      UpdatePromiseSourcingRuleRequest updatePromiseSourcingRuleRequest,
      @MappingTarget PromiseSourcingRule promiseSourcingRule) {
    promiseSourcingRule.setSourceNodes(updatePromiseSourcingRuleRequest.getSourceNodes());
  }

  PromiseSourcingRule convertFromCreatePromiseSourcingRuleRequestToEntity(
      CreatePromiseSourcingRuleRequest createPromiseSourcingRuleRequest);
}
