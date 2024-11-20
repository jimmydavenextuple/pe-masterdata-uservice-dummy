/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.promise.sourcing.rule.persistence.service.impl;

import com.nextuple.promise.sourcing.rule.api.domain.enums.RulesConfigurationModuleNameEnum;
import com.nextuple.promise.sourcing.rule.api.domain.enums.SourcingAttributesDefinitionScopeEnum;
import com.nextuple.promise.sourcing.rule.api.domain.enums.SourcingAttributesDefinitionStatus;
import com.nextuple.promise.sourcing.rule.api.domain.enums.SourcingConstraintEnum;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.FetchPromiseSourcingRuleRequest;
import com.nextuple.promise.sourcing.rule.persistence.domain.AttributeValuesDomainDto;
import com.nextuple.promise.sourcing.rule.persistence.domain.GroupDefinitionDomainDto;
import com.nextuple.promise.sourcing.rule.persistence.domain.NamedOptimizationStrategyDomainDto;
import com.nextuple.promise.sourcing.rule.persistence.domain.NodeGroupDomainDto;
import com.nextuple.promise.sourcing.rule.persistence.domain.NodePriorityDomainDto;
import com.nextuple.promise.sourcing.rule.persistence.domain.PromiseSourcingRuleDomainDto;
import com.nextuple.promise.sourcing.rule.persistence.domain.RulesConfigurationDomainDto;
import com.nextuple.promise.sourcing.rule.persistence.domain.SourcingAttributeDomainDto;
import com.nextuple.promise.sourcing.rule.persistence.domain.SourcingAttributesDefinitionDomainDto;
import com.nextuple.promise.sourcing.rule.persistence.domain.SourcingConstraintDomainDto;
import com.nextuple.promise.sourcing.rule.persistence.domain.SourcingRuleDetailsDomainDto;
import com.nextuple.promise.sourcing.rule.persistence.domain.SourcingRulesConfigurationDomainDto;
import com.nextuple.promise.sourcing.rule.persistence.entity.AttributeValuesEntity;
import com.nextuple.promise.sourcing.rule.persistence.entity.GroupDefinitionEntity;
import com.nextuple.promise.sourcing.rule.persistence.entity.NamedOptimizationStrategyEntity;
import com.nextuple.promise.sourcing.rule.persistence.entity.NodeGroupEntity;
import com.nextuple.promise.sourcing.rule.persistence.entity.NodePriorityEntity;
import com.nextuple.promise.sourcing.rule.persistence.entity.PromiseSourcingRuleEntity;
import com.nextuple.promise.sourcing.rule.persistence.entity.RulesConfigurationEntity;
import com.nextuple.promise.sourcing.rule.persistence.entity.SourcingAttributeEntity;
import com.nextuple.promise.sourcing.rule.persistence.entity.SourcingAttributesDefinitionEntity;
import com.nextuple.promise.sourcing.rule.persistence.entity.SourcingConstraintEntity;
import com.nextuple.promise.sourcing.rule.persistence.entity.SourcingRuleDetailsEntity;
import com.nextuple.promise.sourcing.rule.persistence.entity.SourcingRulesConfigurationEntity;
import com.nextuple.promise.sourcing.rule.persistence.mapper.AttributeValuesEntityMapper;
import com.nextuple.promise.sourcing.rule.persistence.mapper.GroupDefinitionEntityMapper;
import com.nextuple.promise.sourcing.rule.persistence.mapper.NamedOptimizationStrategyEntityMapper;
import com.nextuple.promise.sourcing.rule.persistence.mapper.NodeGroupEntityMapper;
import com.nextuple.promise.sourcing.rule.persistence.mapper.NodePriorityEntityMapper;
import com.nextuple.promise.sourcing.rule.persistence.mapper.PromiseSourcingRuleEntityMapper;
import com.nextuple.promise.sourcing.rule.persistence.mapper.SourcingAttributeEntityMapper;
import com.nextuple.promise.sourcing.rule.persistence.mapper.SourcingAttributesDefinitionEntityMapper;
import com.nextuple.promise.sourcing.rule.persistence.mapper.SourcingConstraintEntityMapper;
import com.nextuple.promise.sourcing.rule.persistence.mapper.SourcingRuleDetailsEntityMapper;
import com.nextuple.promise.sourcing.rule.persistence.mapper.SourcingRulesConfigurationEntityMapper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.mapstruct.factory.Mappers;

public class TestUtil {
  public static final String SDND = "SDND";
  public static final String STANDARD = "STANDARD";
  public static final String EXPRESS = "EXPRESS";
  public static final String NEXTDAY = "NEXTDAY";

  public static final String ORG_ID = "ABC";
  public static final int PRIORITY = 1;
  public static final String DESTINATION_GEO_ZONE = "IST";
  public static final String ALLOCATION_RULE_ID = "NXT";
  public static final String SERVICE_OPTION = SDND;
  public static final String NODE_ID = "Node-1";
  public static final String SORT_BY = "id";
  public static final String Node1 = "Node-1";
  public static final Long SOURCING_ATTRIBUTE_ID = 1L;
  public static final String ATTRIBUTE_NAME = "attribute-1";
  public static Boolean IS_DERIVED_TRUE = Boolean.TRUE;
  public static String CUSTOM_ATTRIBUTE_KEY = "custom-attribute-key1";
  public static String JSON_PATH = "/path-1";
  public static final Long NODE_GROUP_ID = 1L;
  public static final String NODE_GROUP_NAME = "NG1";
  public static final Long NODE_GROUP_DETAIL_ID = 1L;

  public static final Integer NODE_PRIORITY = 1;
  public static final Long SOURCING_ATTRIBUTES_DEFINITION_ID = 1L;
  public static final String SOURCING_RULE_ATTRIBUTES_DEFINITION_NAME = "definition1";
  public static final String SOURCING_RULE_NAME = "sourcingRule1";
  public static final String REQUIRED_ATTRIBUTES = "1,2";
  public static final Long SOURCING_RULE_ID = 1L;
  public static final String SOURCING_RULE = "V1:V2";
  public static final Integer SEQUENCE = 1;

  public static final Long ID = 1L;
  public static final String DEFAULT_GROUP_ID = "DEFAULT";
  public static final SourcingConstraintEnum SOURCING_CONSTRAINT =
      SourcingConstraintEnum.SHIP_COMPLETE_LINE;
  public static final String SOURCING_CONSTRAINT_VALUE_1 = "1";
  public static final String REQUIRED_ATTRIBUTES_VALUE = "V1:V2";
  public static final String OPTIONAL_ATTRIBUTES_VALUE = "V3:V4";
  public static final String GROUP_NAME = "group1";
  public static final String GROUP_ID = "1";
  public static final Long OPTIMIZATION_STRATEGY_ID = 1L;
  public static final String OPTIMIZATION_STRATEGY_NAME = "EXP_V1";
  public static final String OPTIMIZATION_STRATEGY_DETAILS = "SPEED,PRIORITY";

  public static final String UPDATED_OPTIMIZATION_STRATEGY_NAME = "EXP_V2";
  public static final Long ATTRIBUTE_ID = 1L;

  public static final String ATTRIBUTE_VALUE = "SERVICE_OPTION";
  public static final Long ATTRIBUTE_NAME_ID = 1L;

  private static final AttributeValuesEntityMapper ATTRIBUTE_VALUES_ENTITY_MAPPER =
      Mappers.getMapper(AttributeValuesEntityMapper.class);

  private static final GroupDefinitionEntityMapper GROUP_DEFINITION_ENTITY_MAPPER =
      Mappers.getMapper(GroupDefinitionEntityMapper.class);

  private static final NodeGroupEntityMapper NODE_GROUP_ENTITY_MAPPER =
      Mappers.getMapper(NodeGroupEntityMapper.class);

  private static final NodePriorityEntityMapper NODE_PRIORITY_ENTITY_MAPPER =
      Mappers.getMapper(NodePriorityEntityMapper.class);

  private static final PromiseSourcingRuleEntityMapper PROMISE_SOURCING_RULE_ENTITY_MAPPER =
      Mappers.getMapper(PromiseSourcingRuleEntityMapper.class);

  private static final NamedOptimizationStrategyEntityMapper
      NAMED_OPTIMIZATION_STRATEGY_ENTITY_MAPPER =
          Mappers.getMapper(NamedOptimizationStrategyEntityMapper.class);

  private static final SourcingAttributesDefinitionEntityMapper
      SOURCING_ATTRIBUTES_DEFINITION_ENTITY_MAPPER =
          Mappers.getMapper(SourcingAttributesDefinitionEntityMapper.class);

  private static final SourcingRuleDetailsEntityMapper SOURCING_RULE_DETAILS_ENTITY_MAPPER =
      Mappers.getMapper(SourcingRuleDetailsEntityMapper.class);

  private static final SourcingAttributeEntityMapper SOURCING_ATTRIBUTE_ENTITY_MAPPER =
      Mappers.getMapper(SourcingAttributeEntityMapper.class);

  private static final SourcingConstraintEntityMapper SOURCING_CONSTRAINT_ENTITY_MAPPER =
      Mappers.getMapper(SourcingConstraintEntityMapper.class);

  private static final SourcingRulesConfigurationEntityMapper
      SOURCING_RULES_CONFIGURATION_ENTITY_MAPPER =
          Mappers.getMapper(SourcingRulesConfigurationEntityMapper.class);

  public PromiseSourcingRuleEntity getPromiseSourcingRule() {
    PromiseSourcingRuleEntity promiseSourcingRule = new PromiseSourcingRuleEntity();
    promiseSourcingRule.setPriority(PRIORITY);
    promiseSourcingRule.setOrgId(ORG_ID);
    promiseSourcingRule.setSourceNodes(Collections.singleton(Node1));
    promiseSourcingRule.setDestinationGeoZone(DESTINATION_GEO_ZONE);
    promiseSourcingRule.setServiceOption(SERVICE_OPTION);
    promiseSourcingRule.setAllocationRuleId(ALLOCATION_RULE_ID);
    return promiseSourcingRule;
  }

  public PromiseSourcingRuleDomainDto getPromiseSourcingRuleDomainDto() {
    return PROMISE_SOURCING_RULE_ENTITY_MAPPER.toDomain(getPromiseSourcingRule());
  }

  public FetchPromiseSourcingRuleRequest getFetchPromiseSourcingRuleRequest() {
    return FetchPromiseSourcingRuleRequest.builder()
        .orgId(ORG_ID)
        .allocationRuleId(ALLOCATION_RULE_ID)
        .destinationGeoZone(DESTINATION_GEO_ZONE)
        .serviceOptions(List.of(SDND, STANDARD, EXPRESS, NEXTDAY, "UNKNOWN"))
        .build();
  }

  public SourcingAttributeEntity getSourcingAttributeEntity() {
    SourcingAttributeEntity sourcingAttributeEntity = new SourcingAttributeEntity();
    sourcingAttributeEntity.setId(SOURCING_ATTRIBUTE_ID);
    sourcingAttributeEntity.setOrgId(ORG_ID);
    sourcingAttributeEntity.setAttributeName(ATTRIBUTE_NAME);
    sourcingAttributeEntity.setIsDerived(IS_DERIVED_TRUE);
    sourcingAttributeEntity.setCustomAttributeKey(CUSTOM_ATTRIBUTE_KEY);
    sourcingAttributeEntity.setJsonPath(JSON_PATH);
    return sourcingAttributeEntity;
  }

  public SourcingAttributeDomainDto getSourcingAttributeDomainDto() {
    return SOURCING_ATTRIBUTE_ENTITY_MAPPER.toDomain(getSourcingAttributeEntity());
  }

  public NodeGroupEntity getNodeGroupEntity() {
    NodeGroupEntity nodeGroupEntity = new NodeGroupEntity();
    nodeGroupEntity.setId(NODE_GROUP_ID);
    nodeGroupEntity.setOrgId(ORG_ID);
    nodeGroupEntity.setNodeGroupName(NODE_GROUP_NAME);
    nodeGroupEntity.setNodeGroupDescription(NODE_GROUP_NAME);

    return nodeGroupEntity;
  }

  public List<NodeGroupDomainDto> getNodeGroupEntityDomainDtoList() {
    return NODE_GROUP_ENTITY_MAPPER.toDomain(getNodeGroupEntityList());
  }

  public NodeGroupDomainDto getNodeGroupDomainDto() {

    return NODE_GROUP_ENTITY_MAPPER.toDomain(getNodeGroupEntity());
  }

  public List<NodeGroupEntity> getNodeGroupEntityList() {
    NodeGroupEntity nodeGroupEntity1 = new NodeGroupEntity();
    nodeGroupEntity1.setId(NODE_GROUP_ID);
    nodeGroupEntity1.setOrgId(ORG_ID);
    nodeGroupEntity1.setNodeGroupName(NODE_GROUP_NAME);
    nodeGroupEntity1.setNodeGroupDescription(NODE_GROUP_NAME);

    NodeGroupEntity nodeGroupEntity2 = new NodeGroupEntity();
    nodeGroupEntity2.setId(2L);
    nodeGroupEntity2.setOrgId(ORG_ID);
    nodeGroupEntity2.setNodeGroupName("NG2");
    nodeGroupEntity2.setNodeGroupDescription("NG2");

    return List.of(nodeGroupEntity1, nodeGroupEntity2);
  }

  public NodePriorityEntity getNodePriorityEntity() {
    NodePriorityEntity nodeGroupDetailEntity = new NodePriorityEntity();
    nodeGroupDetailEntity.setId(NODE_GROUP_DETAIL_ID);
    nodeGroupDetailEntity.setOrgId(ORG_ID);
    nodeGroupDetailEntity.setNodeId(NODE_ID);
    nodeGroupDetailEntity.setPriority(NODE_PRIORITY);
    nodeGroupDetailEntity.setNodeGroupId(NODE_GROUP_ID);

    return nodeGroupDetailEntity;
  }

  public NodePriorityDomainDto getNodePriorityDomainDto() {
    return NODE_PRIORITY_ENTITY_MAPPER.toDomain(getNodePriorityEntity());
  }

  public SourcingAttributesDefinitionEntity getSourcingRuleAttributesDefinitionEntity(
      SourcingAttributesDefinitionStatus status) {
    SourcingAttributesDefinitionEntity sourcingRuleAttributesDefinitionEntity =
        new SourcingAttributesDefinitionEntity();
    sourcingRuleAttributesDefinitionEntity.setId(SOURCING_ATTRIBUTES_DEFINITION_ID);
    sourcingRuleAttributesDefinitionEntity.setOrgId(ORG_ID);
    sourcingRuleAttributesDefinitionEntity.setName(SOURCING_RULE_ATTRIBUTES_DEFINITION_NAME);
    sourcingRuleAttributesDefinitionEntity.setReqAttributes(REQUIRED_ATTRIBUTES);
    sourcingRuleAttributesDefinitionEntity.setStatus(status);
    sourcingRuleAttributesDefinitionEntity.setScope(
        SourcingAttributesDefinitionScopeEnum.SOURCING_RULE);
    return sourcingRuleAttributesDefinitionEntity;
  }

  public SourcingAttributesDefinitionDomainDto getSourcingAttributesDefinitionDomainDto(
      SourcingAttributesDefinitionStatus status) {
    return SOURCING_ATTRIBUTES_DEFINITION_ENTITY_MAPPER.toDomain(
        getSourcingRuleAttributesDefinitionEntity(status));
  }

  public SourcingRulesConfigurationEntity getSourcingRulesEntity() {
    SourcingRulesConfigurationEntity sourcingRulesConfigurationEntity =
        new SourcingRulesConfigurationEntity();
    sourcingRulesConfigurationEntity.setId(SOURCING_RULE_ID);
    sourcingRulesConfigurationEntity.setOrgId(ORG_ID);
    sourcingRulesConfigurationEntity.setSourcingRule(SOURCING_RULE);
    sourcingRulesConfigurationEntity.setSourcingRuleName(SOURCING_RULE_NAME);
    sourcingRulesConfigurationEntity.setSourcingAttributesDefinitionId(
        SOURCING_ATTRIBUTES_DEFINITION_ID);

    return sourcingRulesConfigurationEntity;
  }

  public SourcingRulesConfigurationDomainDto getSourcingRulesConfigurationDomainDto() {
    return SOURCING_RULES_CONFIGURATION_ENTITY_MAPPER.toDomain(getSourcingRulesEntity());
  }

  public SourcingRuleDetailsEntity getSourcingRuleDetailsEntity() {
    SourcingRuleDetailsEntity sourcingRuleDetailsEntity = new SourcingRuleDetailsEntity();
    sourcingRuleDetailsEntity.setId(ID);
    sourcingRuleDetailsEntity.setOrgId(ORG_ID);
    sourcingRuleDetailsEntity.setSourcingRuleId(SOURCING_RULE_ID);
    sourcingRuleDetailsEntity.setSequence(SEQUENCE);
    sourcingRuleDetailsEntity.setNodeGroups(NODE_GROUP_ID.toString());

    return sourcingRuleDetailsEntity;
  }

  public SourcingRuleDetailsDomainDto getSourcingRuleDetailsDomainDto() {
    return SOURCING_RULE_DETAILS_ENTITY_MAPPER.toDomain(getSourcingRuleDetailsEntity());
  }

  public SourcingConstraintEntity getSourcingConstraintEntity() {
    SourcingConstraintEntity sourcingConstraintEntity = new SourcingConstraintEntity();
    sourcingConstraintEntity.setId(ID);
    sourcingConstraintEntity.setOrgId(ORG_ID);
    sourcingConstraintEntity.setGroupId(DEFAULT_GROUP_ID);
    sourcingConstraintEntity.setSourcingConstraint(SOURCING_CONSTRAINT);
    sourcingConstraintEntity.setSourcingConstraintValue(SOURCING_CONSTRAINT_VALUE_1);
    return sourcingConstraintEntity;
  }

  public SourcingConstraintDomainDto getSourcingConstraintDomainDto() {
    return SOURCING_CONSTRAINT_ENTITY_MAPPER.toDomain(getSourcingConstraintEntity());
  }

  public GroupDefinitionEntity getGroupDefinitionEntity() {
    GroupDefinitionEntity groupDefinitionEntity = new GroupDefinitionEntity();
    groupDefinitionEntity.setId(ID);
    groupDefinitionEntity.setSourcingAttributesDefinitionId(SOURCING_ATTRIBUTES_DEFINITION_ID);
    groupDefinitionEntity.setReqAttributesValue(REQUIRED_ATTRIBUTES_VALUE);
    groupDefinitionEntity.setGroupName(GROUP_NAME);
    groupDefinitionEntity.setOrgId(ORG_ID);
    return groupDefinitionEntity;
  }

  public List<GroupDefinitionEntity> getGroupDefinitionEntityList() {
    return List.of(getGroupDefinitionEntity());
  }

  public GroupDefinitionDomainDto getGroupDefinitionDomainDto() {
    return GROUP_DEFINITION_ENTITY_MAPPER.toDomain(getGroupDefinitionEntity());
  }

  public List<GroupDefinitionDomainDto> getGroupDefinitionDomainDtoList() {
    return GROUP_DEFINITION_ENTITY_MAPPER.toDomain(getGroupDefinitionEntityList());
  }

  public NamedOptimizationStrategyEntity getNamedOptimizationStrategyEntity() {
    NamedOptimizationStrategyEntity namedOptimizationStrategyEntity =
        new NamedOptimizationStrategyEntity();
    namedOptimizationStrategyEntity.setId(OPTIMIZATION_STRATEGY_ID);
    namedOptimizationStrategyEntity.setOrgId(ORG_ID);
    namedOptimizationStrategyEntity.setGroupId(GROUP_ID);
    namedOptimizationStrategyEntity.setOptimizationStrategyName(OPTIMIZATION_STRATEGY_NAME);
    namedOptimizationStrategyEntity.setOptimizationStrategyDetails(OPTIMIZATION_STRATEGY_DETAILS);
    return namedOptimizationStrategyEntity;
  }

  public List<NamedOptimizationStrategyEntity> getNamedOptimizationStrategyEntityList() {
    return List.of(getNamedOptimizationStrategyEntity());
  }

  public NamedOptimizationStrategyDomainDto getNamedOptimizationStrategyDomainDto() {
    return NAMED_OPTIMIZATION_STRATEGY_ENTITY_MAPPER.toDomain(getNamedOptimizationStrategyEntity());
  }

  public List<NamedOptimizationStrategyDomainDto> getNamedOptimizationStrategyDomainDtoList() {
    return NAMED_OPTIMIZATION_STRATEGY_ENTITY_MAPPER.toDomain(
        getNamedOptimizationStrategyEntityList());
  }

  public NamedOptimizationStrategyEntity getUpdatedNamedOptimizationStrategyEntity() {
    NamedOptimizationStrategyEntity namedOptimizationStrategyEntity =
        new NamedOptimizationStrategyEntity();
    namedOptimizationStrategyEntity.setId(OPTIMIZATION_STRATEGY_ID);
    namedOptimizationStrategyEntity.setOrgId(ORG_ID);
    namedOptimizationStrategyEntity.setGroupId(GROUP_ID);
    namedOptimizationStrategyEntity.setOptimizationStrategyName(UPDATED_OPTIMIZATION_STRATEGY_NAME);
    namedOptimizationStrategyEntity.setOptimizationStrategyDetails(OPTIMIZATION_STRATEGY_DETAILS);
    return namedOptimizationStrategyEntity;
  }

  public List<NodePriorityEntity> getNodePriorityEntityList() {
    NodePriorityEntity entity = new NodePriorityEntity();
    entity.setId(NODE_GROUP_ID);
    entity.setOrgId(ORG_ID);
    entity.setNodeId(NODE_ID);
    entity.setPriority(PRIORITY);
    return List.of(entity);
  }

  public List<SourcingRuleDetailsEntity> getSourcingRuleConfigurations() {

    SourcingRuleDetailsEntity entity = new SourcingRuleDetailsEntity();
    entity.setSourcingRuleId(SOURCING_RULE_ID);
    entity.setId(23L);
    entity.setSequence(23);
    entity.setNodeGroups("42, 43");

    SourcingRuleDetailsEntity entity2 = new SourcingRuleDetailsEntity();
    entity2.setSourcingRuleId(SOURCING_RULE_ID);
    entity2.setId(23L);
    entity2.setSequence(23);
    entity2.setNodeGroups("42");
    return List.of(entity, entity2);
  }

  public SourcingRulesConfigurationEntity getSourcingRulesConfigurationEntity() {
    SourcingRulesConfigurationEntity entity = new SourcingRulesConfigurationEntity();
    entity.setOrgId(ORG_ID);
    entity.setId(SOURCING_RULE_ID);
    entity.setSourcingRule(SERVICE_OPTION);
    entity.setSourcingRuleName(SOURCING_RULE_NAME);
    entity.setSourcingAttributesDefinitionId(SOURCING_ATTRIBUTES_DEFINITION_ID);
    return entity;
  }

  public List<Long> getNodeGroupEntityIds(int count) {
    List<Long> nodeGroupEntityIds = new ArrayList<>();
    for (int i = 1; i < count; i++) {
      nodeGroupEntityIds.add(Long.valueOf(i));
    }
    return nodeGroupEntityIds;
  }

  public AttributeValuesEntity getAttributeValuesEntity() {
    AttributeValuesEntity attributeValuesEntity = new AttributeValuesEntity();
    attributeValuesEntity.setId(ATTRIBUTE_ID);
    attributeValuesEntity.setNameId(ATTRIBUTE_NAME_ID);
    attributeValuesEntity.setValue(ATTRIBUTE_VALUE);

    return attributeValuesEntity;
  }

  public List<AttributeValuesEntity> getAttributeValuesEntityList() {
    AttributeValuesEntity attributeValuesEntity = new AttributeValuesEntity();
    attributeValuesEntity.setId(ATTRIBUTE_ID);
    attributeValuesEntity.setNameId(ATTRIBUTE_NAME_ID);
    attributeValuesEntity.setValue(ATTRIBUTE_VALUE);

    return List.of(attributeValuesEntity);
  }

  public AttributeValuesDomainDto getAttributeValuesDomainDto() {
    return ATTRIBUTE_VALUES_ENTITY_MAPPER.toDomain(getAttributeValuesEntity());
  }

  public List<AttributeValuesDomainDto> getAttributeValuesDomainDtoList() {
    return ATTRIBUTE_VALUES_ENTITY_MAPPER.toDomain(getAttributeValuesEntityList());
  }

  public <T> List<T> getNodeGroupEntityIds(Class<T> type, int count) {
    List<T> nodeGroupEntityIds = new ArrayList<>();
    if (type.equals(String.class)) {
      for (int i = 1; i < count; i++) {
        nodeGroupEntityIds.add((T) String.valueOf(i));
      }
    } else if (type.equals(Long.class))
      for (int i = 1; i < count; i++) {
        nodeGroupEntityIds.add((T) Long.valueOf(i));
      }
    return nodeGroupEntityIds;
  }

  public RulesConfigurationEntity getRulesConfigurationEntity() {
    return RulesConfigurationEntity.builder()
        .ruleName(SOURCING_RULE_NAME)
        .orgId(ORG_ID)
        .rule(SOURCING_RULE)
        .attributeDefinitionId(SOURCING_ATTRIBUTE_ID)
        .moduleName(RulesConfigurationModuleNameEnum.PROCESSING_TIME)
        .scope(SourcingAttributesDefinitionScopeEnum.ML_RULE)
        .build();
  }

  public RulesConfigurationDomainDto getRulesConfigurationDomainDto() {
    return RulesConfigurationDomainDto.builder()
        .ruleName(SOURCING_RULE_NAME)
        .orgId(ORG_ID)
        .rule(SOURCING_RULE)
        .attributeDefinitionId(SOURCING_ATTRIBUTE_ID)
        .moduleName(RulesConfigurationModuleNameEnum.PROCESSING_TIME)
        .scope(SourcingAttributesDefinitionScopeEnum.ML_RULE)
        .build();
  }
}
