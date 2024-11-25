/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.sourcing.rule.spring.cache.util;

import static com.nextuple.promise.sourcing.rule.api.domain.enums.HolidayCutoffStatus.ACTIVE;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.promise.sourcing.rule.api.domain.enums.HolidayCutoffDaysType;
import com.nextuple.promise.sourcing.rule.api.domain.enums.RulesConfigurationModuleNameEnum;
import com.nextuple.promise.sourcing.rule.api.domain.enums.SourcingAttributesDefinitionScopeEnum;
import com.nextuple.promise.sourcing.rule.api.domain.enums.SourcingAttributesDefinitionStatus;
import com.nextuple.promise.sourcing.rule.api.domain.enums.SourcingConstraintEnum;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.FetchGroupDefinitionRequest;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.FetchPromiseSourcingRuleRequest;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.FetchRuleConfigurationRequest;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.FetchSourcingRulesRequest;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.HolidayCutoffRulesRequest;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.DetailedOptimizationStrategyResponse;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.FetchPromiseSourcingRuleResponse;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.FetchSourcingRulesResponse;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.GroupDefinitionListResponse;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.GroupDefinitionResponse;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.HolidayCutoffRulesResponse;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.NodePriorityResponse;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.RulesConfigurationResponse;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.SourcingAttributeResponse;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.SourcingAttributesDefinitionResponse;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.SourcingConstraintsResponse;
import com.nextuple.promise.sourcing.rule.api.domain.pojo.AttributeInfo;
import com.nextuple.promise.sourcing.rule.api.domain.pojo.GroupDefinitionInfo;
import com.nextuple.promise.sourcing.rule.api.domain.pojo.HolidayCutoffInfo;
import com.nextuple.promise.sourcing.rule.api.domain.pojo.NodeGroupDetailsInfo;
import com.nextuple.promise.sourcing.rule.api.domain.pojo.NodePriorityInfo;
import com.nextuple.promise.sourcing.rule.api.domain.pojo.ServiceOptionInfo;
import com.nextuple.promise.sourcing.rule.api.domain.pojo.SourcingAttributeValuesInfo;
import com.nextuple.promise.sourcing.rule.api.domain.pojo.SourcingConstraintInfo;
import com.nextuple.promise.sourcing.rule.api.domain.pojo.SourcingRulesInfo;
import com.nextuple.sourcing.rule.cache.domain.GroupDefinitionCacheKey;
import com.nextuple.sourcing.rule.cache.domain.GroupDefinitionCacheValue;
import com.nextuple.sourcing.rule.cache.domain.GroupDefinitionRuleCacheKey;
import com.nextuple.sourcing.rule.cache.domain.GroupDefinitionRuleCacheValue;
import com.nextuple.sourcing.rule.cache.domain.HolidayCutoffKey;
import com.nextuple.sourcing.rule.cache.domain.HolidayCutoffValue;
import com.nextuple.sourcing.rule.cache.domain.NodeGroupCacheKey;
import com.nextuple.sourcing.rule.cache.domain.NodeGroupCacheValue;
import com.nextuple.sourcing.rule.cache.domain.OptimizationStrategyCacheKey;
import com.nextuple.sourcing.rule.cache.domain.OptimizationStrategyCacheValue;
import com.nextuple.sourcing.rule.cache.domain.RulesConfigurationKey;
import com.nextuple.sourcing.rule.cache.domain.RulesConfigurationValue;
import com.nextuple.sourcing.rule.cache.domain.SourcingAttributeByIdKey;
import com.nextuple.sourcing.rule.cache.domain.SourcingAttributeByIdValue;
import com.nextuple.sourcing.rule.cache.domain.SourcingAttributeDefinitionByActiveStatusKey;
import com.nextuple.sourcing.rule.cache.domain.SourcingAttributeDefinitionByActiveStatusValue;
import com.nextuple.sourcing.rule.cache.domain.SourcingConstraintCacheKey;
import com.nextuple.sourcing.rule.cache.domain.SourcingConstraintCacheValue;
import com.nextuple.sourcing.rule.cache.domain.SourcingRuleCacheKey;
import com.nextuple.sourcing.rule.cache.domain.SourcingRuleCacheValue;
import com.nextuple.sourcing.rule.cache.domain.SourcingRuleConfigurationFetchRulesKey;
import com.nextuple.sourcing.rule.cache.domain.SourcingRuleConfigurationFetchRulesValue;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TestUtil {
  public static final Long ID = 1L;
  public static final String ORG_ID = "NEXTUPLE_GR";
  public static final String NODE1 = "NODE1";
  public static final String NODE2 = "NODE2";
  public static final String HOLIDAY_CUTOFF_NAME = "holiday_cutoff_name";
  public static final String HOLIDAY_CUTOFF_RULE = "EXPRESS:R1";
  public static final String HOLIDAY_CUTOFF_DESCRIPTION = "holiday_cutoff_description";
  public static final Date DATE = new Date();
  public static final Double PRE_CUTOFF_DAYS = 5.0;
  public static final Double DELIVERY_COOL_DOWN_DAYS = 3.0;
  public static final HolidayCutoffDaysType PRE_CUTOFF_DAYS_TYPE =
      HolidayCutoffDaysType.BUSINESS_DAYS;
  public static final HolidayCutoffDaysType DELIVERY_COOL_DOWN_DAYS_TYPE =
      HolidayCutoffDaysType.BUSINESS_DAYS;
  public static final String HOLIDAY_CUTOFF_REQUIRED_ATTRIBUTES = "EXPRESS:R2";

  public FetchPromiseSourcingRuleRequest getFetchPromiseSourcingRuleRequest(
      String orgId,
      List<String> serviceOptions,
      String destinationGeoZone,
      String allocationRuleId) {
    return FetchPromiseSourcingRuleRequest.builder()
        .allocationRuleId(allocationRuleId)
        .destinationGeoZone(destinationGeoZone)
        .orgId(orgId)
        .serviceOptions(serviceOptions)
        .build();
  }

  public ServiceOptionInfo getServiceOptionInfo(int priority, Set<String> sourceNodes) {
    return ServiceOptionInfo.builder().priority(priority).sourceNodes(sourceNodes).build();
  }

  public FetchPromiseSourcingRuleResponse getFetchPromiseSourcingRuleResponse() {
    return FetchPromiseSourcingRuleResponse.builder()
        .serviceOptionSourcingRules(
            Map.of(
                "SDND",
                java.util.List.of(
                    ServiceOptionInfo.builder()
                        .priority(1)
                        .sourceNodes(Collections.singleton("N1"))
                        .build()),
                "STANDARD",
                new ArrayList<>(),
                "EXPRESS",
                new ArrayList<>(),
                "NEXTDAY",
                java.util.List.of(
                    ServiceOptionInfo.builder()
                        .priority(1)
                        .sourceNodes(Collections.singleton("N2"))
                        .build())))
        .build();
  }

  public SourcingRuleCacheValue getSourcingRuleCacheValue() {
    return SourcingRuleCacheValue.builder()
        .fetchPromiseSourcingRuleResponse(getFetchPromiseSourcingRuleResponse())
        .build();
  }

  public SourcingRuleCacheKey getSourcingRuleCacheKey() {
    return SourcingRuleCacheKey.builder()
        .fetchPromiseSourcingRuleRequest(
            getFetchPromiseSourcingRuleRequest("ABC", List.of("SDND"), "AOA", "DEFAULT"))
        .build();
  }

  public BaseResponse<FetchPromiseSourcingRuleResponse>
      getBaseResponseOfFetchPromiseSourcingRuleResponse() {
    BaseResponse<FetchPromiseSourcingRuleResponse> response = new BaseResponse<>();
    response.setMessage("Promise Sourcing Rule fetched successfully!");
    response.setPayload(getFetchPromiseSourcingRuleResponse());
    return response;
  }

  public SourcingAttributeByIdKey getSourcingAttributeByIdCacheKey() {
    SourcingAttributeByIdKey sourcingAttributeByIdKey =
        SourcingAttributeByIdKey.builder().orgId("ABC").id(1L).build();
    return sourcingAttributeByIdKey;
  }

  public SourcingAttributeByIdValue getSourcingAttributeByIdCacheValue() {
    SourcingAttributeByIdValue sourcingAttributeByIdValue =
        SourcingAttributeByIdValue.builder()
            .id(1L)
            .orgId("NEXTUPLE")
            .attributeName("abc")
            .jsonPath("@json/path")
            .isDerived(false)
            .build();
    return sourcingAttributeByIdValue;
  }

  public BaseResponse<SourcingAttributeResponse> getSourcingAttributeResponse() {
    BaseResponse<SourcingAttributeResponse> response = new BaseResponse<>();
    SourcingAttributeResponse sourcingAttributeResponse =
        SourcingAttributeResponse.builder()
            .id(1L)
            .orgId("NEXTUPLE")
            .attributeName("abc")
            .jsonPath("@json/path")
            .isDerived(false)
            .build();
    response.setPayload(sourcingAttributeResponse);
    return response;
  }

  public SourcingAttributeDefinitionByActiveStatusKey
      getSourcingAttributeDefinitionByActiveStatusCacheKey() {
    SourcingAttributeDefinitionByActiveStatusKey sourcingAttributeDefinitionByActiveStatusKey =
        SourcingAttributeDefinitionByActiveStatusKey.builder()
            .orgId("NEXTUPLE")
            .scope("SOURCING_RULE")
            .build();
    return sourcingAttributeDefinitionByActiveStatusKey;
  }

  public SourcingAttributeDefinitionByActiveStatusValue
      getSourcingAttributeDefinitionByIdActiveStatusValue() {
    SourcingAttributeDefinitionByActiveStatusValue sourcingAttributeDefinitionByActiveStatusValue =
        SourcingAttributeDefinitionByActiveStatusValue.builder()
            .id(1L)
            .orgId("NEXTUPLE")
            .name("name")
            .reqAttributes("1,2")
            .optAttributes("3")
            .status(SourcingAttributesDefinitionStatus.ACTIVE)
            .scope(SourcingAttributesDefinitionScopeEnum.OPTIMIZATION)
            .module("module")
            .build();
    return sourcingAttributeDefinitionByActiveStatusValue;
  }

  public BaseResponse<SourcingAttributesDefinitionResponse>
      getSourcingAttributeDefinitionResponse() {
    BaseResponse<SourcingAttributesDefinitionResponse> response = new BaseResponse<>();
    SourcingAttributesDefinitionResponse sourcingAttributesDefinitionResponse =
        SourcingAttributesDefinitionResponse.builder()
            .id(1L)
            .orgId("NEXTUPLE")
            .name("name")
            .reqAttributes("1,2")
            .optAttributes("3")
            .status(SourcingAttributesDefinitionStatus.ACTIVE)
            .scope(SourcingAttributesDefinitionScopeEnum.OPTIMIZATION)
            .module("module")
            .build();
    response.setPayload(sourcingAttributesDefinitionResponse);
    return response;
  }

  public SourcingRuleConfigurationFetchRulesKey getSourcingRuleConfigurationFetchRulesCacheKey() {
    SourcingAttributeValuesInfo sourcingAttributeValuesInfo = new SourcingAttributeValuesInfo();
    sourcingAttributeValuesInfo.setRequiredAttributesValue("1,2");
    FetchSourcingRulesRequest fetchSourcingRulesRequest =
        FetchSourcingRulesRequest.builder()
            .orgId("NEXTUPLE")
            .sourcingAttributesDefinitionId(1L)
            .sourcingAttributeValuesInfo(sourcingAttributeValuesInfo)
            .build();
    SourcingRuleConfigurationFetchRulesKey sourcingRuleConfigurationFetchRulesKey =
        SourcingRuleConfigurationFetchRulesKey.builder()
            .fetchSourcingRulesRequest(fetchSourcingRulesRequest)
            .build();
    return sourcingRuleConfigurationFetchRulesKey;
  }

  public SourcingRuleConfigurationFetchRulesValue
      getSourcingRuleConfigurationFetchRuleCacheValue() {
    NodePriorityInfo nodePriorityInfo = new NodePriorityInfo();
    nodePriorityInfo.setNodeId("Node-1");
    nodePriorityInfo.setPriority(1);
    NodeGroupDetailsInfo nodeGroupDetailsInfo = new NodeGroupDetailsInfo();
    nodeGroupDetailsInfo.setNodeGroupId(1L);
    nodeGroupDetailsInfo.setNodeInfo(List.of(nodePriorityInfo));
    SourcingRulesInfo sourcingRulesInfo =
        SourcingRulesInfo.builder()
            .orgId("NEXTUPLE")
            .sourcingRule("V1:V2")
            .sequence(1)
            .nodeGroupDetailsInfo(List.of(nodeGroupDetailsInfo))
            .sourcingRuleName("SOURCING_RULE_NAME")
            .sourcingAttributesDefinitionId(1L)
            .requiredAttributes(getRequiredAttributes())
            .optionalAttributes(getOptionalAttributes())
            .build();
    SourcingRuleConfigurationFetchRulesValue sourcingRuleConfigurationFetchRulesValue =
        SourcingRuleConfigurationFetchRulesValue.builder()
            .sourcingRulesInfo(List.of(sourcingRulesInfo))
            .message("message")
            .build();
    return sourcingRuleConfigurationFetchRulesValue;
  }

  public BaseResponse<FetchSourcingRulesResponse> getFetchSourcingRuleResponse() {
    BaseResponse<FetchSourcingRulesResponse> response = new BaseResponse<>();
    NodePriorityInfo nodePriorityInfo = new NodePriorityInfo();
    nodePriorityInfo.setNodeId("Node-1");
    nodePriorityInfo.setPriority(1);
    NodeGroupDetailsInfo nodeGroupDetailsInfo = new NodeGroupDetailsInfo();
    nodeGroupDetailsInfo.setNodeGroupId(1L);
    nodeGroupDetailsInfo.setNodeInfo(List.of(nodePriorityInfo));

    SourcingRulesInfo sourcingRulesInfo =
        SourcingRulesInfo.builder()
            .orgId("NEXTUPLE")
            .sourcingRule("V1:V2")
            .sequence(1)
            .nodeGroupDetailsInfo(List.of(nodeGroupDetailsInfo))
            .sourcingRuleName("SOURCING_RULE_NAME")
            .sourcingAttributesDefinitionId(1L)
            .requiredAttributes(getRequiredAttributes())
            .optionalAttributes(getOptionalAttributes())
            .build();
    FetchSourcingRulesResponse fetchSourcingRulesResponse =
        FetchSourcingRulesResponse.builder()
            .sourcingRulesInfo(List.of(sourcingRulesInfo))
            .message("message")
            .build();
    response.setPayload(fetchSourcingRulesResponse);
    return response;
  }

  public OptimizationStrategyCacheKey getOptimizationStrategyKey() {
    OptimizationStrategyCacheKey optimizationStrategyCacheKey =
        OptimizationStrategyCacheKey.builder().groupId("group1").orgId("NEXTUPLE").build();
    return optimizationStrategyCacheKey;
  }

  public OptimizationStrategyCacheValue getOptimizationStrategyValue() {
    DetailedOptimizationStrategyResponse detailedOptimizationStrategyResponse =
        DetailedOptimizationStrategyResponse.builder()
            .orgId("NEXTUPLE")
            .groupId("group1")
            .id(1L)
            .optimizationStrategyName("strategy_name")
            .optimizationStrategyDetails("strategy_details")
            .requiredAttributes(getRequiredAttributes())
            .build();
    OptimizationStrategyCacheValue optimizationStrategyCacheValue =
        OptimizationStrategyCacheValue.builder()
            .detailedOptimizationStrategyResponse(detailedOptimizationStrategyResponse)
            .build();
    return optimizationStrategyCacheValue;
  }

  public BaseResponse<DetailedOptimizationStrategyResponse> getNamedOptimizationStrategyResponse() {
    BaseResponse<DetailedOptimizationStrategyResponse> response = new BaseResponse<>();
    DetailedOptimizationStrategyResponse namedDetailedOptimizationStrategyResponse =
        DetailedOptimizationStrategyResponse.builder()
            .orgId("NEXTUPLE")
            .groupId("group1")
            .id(1L)
            .optimizationStrategyName("strategy_name")
            .optimizationStrategyDetails("strategy_details")
            .requiredAttributes(getRequiredAttributes())
            .build();
    response.setPayload(namedDetailedOptimizationStrategyResponse);
    return response;
  }

  private List<AttributeInfo> getRequiredAttributes() {
    List<AttributeInfo> attributeInfos = new ArrayList<>();
    AttributeInfo infoA = new AttributeInfo("serviceOption", "1", "SDND");
    AttributeInfo infoB = new AttributeInfo("geoZone", "2", "T2P");
    attributeInfos.add(infoA);
    attributeInfos.add(infoB);
    return attributeInfos;
  }

  private List<AttributeInfo> getOptionalAttributes() {
    List<AttributeInfo> attributeInfos = new ArrayList<>();
    AttributeInfo info = new AttributeInfo("fulfilmentType", "3", "PICK");
    attributeInfos.add(info);
    return attributeInfos;
  }

  public SourcingConstraintCacheKey getSourcingConstraintCacheKey() {
    SourcingConstraintCacheKey sourcingConstraintCacheKey =
        SourcingConstraintCacheKey.builder().groupId("group1").orgId("NEXTUPLE").build();
    return sourcingConstraintCacheKey;
  }

  public SourcingConstraintCacheValue getSourcingConstraintCacheValue() {
    SourcingConstraintInfo constraintInfo =
        SourcingConstraintInfo.builder()
            .sourcingConstraintValue("0")
            .sourcingConstraint(SourcingConstraintEnum.SHIP_COMPLETE_LINE)
            .build();
    SourcingConstraintsResponse sourcingConstraintsResponse =
        SourcingConstraintsResponse.builder()
            .orgId("NEXTUPLE")
            .groupId("group1")
            .sourcingConstraintsInfo(List.of(constraintInfo))
            .build();
    SourcingConstraintCacheValue sourcingConstraintCacheValue =
        SourcingConstraintCacheValue.builder()
            .sourcingConstraintsResponse(sourcingConstraintsResponse)
            .build();
    return sourcingConstraintCacheValue;
  }

  public BaseResponse<SourcingConstraintsResponse> getSourcingConstraintResponse() {
    BaseResponse<SourcingConstraintsResponse> response = new BaseResponse<>();
    SourcingConstraintInfo constraintInfo =
        SourcingConstraintInfo.builder()
            .sourcingConstraintValue("0")
            .sourcingConstraint(SourcingConstraintEnum.SHIP_COMPLETE_LINE)
            .build();
    SourcingConstraintsResponse sourcingConstraintsResponse =
        SourcingConstraintsResponse.builder()
            .orgId("NEXTUPLE")
            .groupId("group1")
            .sourcingConstraintsInfo(List.of(constraintInfo))
            .build();
    response.setPayload(sourcingConstraintsResponse);
    return response;
  }

  public GroupDefinitionCacheValue getGroupDefinitionCacheValue() {
    GroupDefinitionInfo groupDefinitionInfo = new GroupDefinitionInfo();
    groupDefinitionInfo.setGroupName("group-1");
    groupDefinitionInfo.setId(1L);
    groupDefinitionInfo.setReqAttributesValue("1:2");
    GroupDefinitionListResponse groupDefinitionListResponse =
        GroupDefinitionListResponse.builder()
            .orgId("NEXTUPLE")
            .sourcingAttributesDefinitionId(1l)
            .groupDefinitionInfoList(List.of(groupDefinitionInfo))
            .build();
    return GroupDefinitionCacheValue.builder()
        .groupDefinitionListResponse(groupDefinitionListResponse)
        .build();
  }

  public BaseResponse<GroupDefinitionListResponse> getGroupDefinitionResponse() {
    BaseResponse<GroupDefinitionListResponse> response = new BaseResponse<>();
    GroupDefinitionInfo groupDefinitionInfo = new GroupDefinitionInfo();
    groupDefinitionInfo.setGroupName("group-1");
    groupDefinitionInfo.setId(1L);
    groupDefinitionInfo.setReqAttributesValue("1:2");
    GroupDefinitionListResponse groupDefinitionListResponse =
        GroupDefinitionListResponse.builder()
            .orgId("NEXTUPLE")
            .sourcingAttributesDefinitionId(1l)
            .groupDefinitionInfoList(List.of(groupDefinitionInfo))
            .build();
    response.setPayload(groupDefinitionListResponse);
    return response;
  }

  public GroupDefinitionCacheKey getGroupDescriptionCacheKey() {
    return GroupDefinitionCacheKey.builder()
        .orgId("NEXTUPLE")
        .sourcingAttributesDefinitionId(1L)
        .build();
  }

  public BaseResponse<HolidayCutoffRulesResponse> getHolidayCutoffRulesResponse() {
    BaseResponse<HolidayCutoffRulesResponse> response = new BaseResponse<>();

    HolidayCutoffInfo holidayCutoffInfo =
        HolidayCutoffInfo.builder()
            .orgId(ORG_ID)
            .holidayCutoffName(HOLIDAY_CUTOFF_NAME)
            .holidayCutoffDescription(HOLIDAY_CUTOFF_DESCRIPTION)
            .holidayCutoffRule(HOLIDAY_CUTOFF_RULE)
            .sourcingAttributesDefinitionId(1L)
            .status(ACTIVE)
            .startDate(DATE)
            .holidayCutoffDate(DATE)
            .holidayDeliveryDate(DATE)
            .preCutoffDays(PRE_CUTOFF_DAYS)
            .deliveryCoolDownDays(DELIVERY_COOL_DOWN_DAYS)
            .preCutoffDaysType(PRE_CUTOFF_DAYS_TYPE)
            .deliveryCoolDownDaysType(DELIVERY_COOL_DOWN_DAYS_TYPE)
            .build();
    HolidayCutoffRulesResponse holidayCutoffRulesResponse =
        HolidayCutoffRulesResponse.builder().holidayCutoffInfo(List.of(holidayCutoffInfo)).build();
    response.setPayload(holidayCutoffRulesResponse);
    return response;
  }

  public HolidayCutoffValue getHolidayCutoffValue() {

    HolidayCutoffInfo holidayCutoffInfo =
        HolidayCutoffInfo.builder()
            .orgId(ORG_ID)
            .holidayCutoffName(HOLIDAY_CUTOFF_NAME)
            .holidayCutoffDescription(HOLIDAY_CUTOFF_DESCRIPTION)
            .holidayCutoffRule(HOLIDAY_CUTOFF_RULE)
            .sourcingAttributesDefinitionId(1L)
            .status(ACTIVE)
            .startDate(DATE)
            .holidayCutoffDate(DATE)
            .holidayDeliveryDate(DATE)
            .preCutoffDays(PRE_CUTOFF_DAYS)
            .deliveryCoolDownDays(DELIVERY_COOL_DOWN_DAYS)
            .preCutoffDaysType(PRE_CUTOFF_DAYS_TYPE)
            .deliveryCoolDownDaysType(DELIVERY_COOL_DOWN_DAYS_TYPE)
            .build();
    return HolidayCutoffValue.builder()
        .holidayCutoffRulesResponse(
            HolidayCutoffRulesResponse.builder()
                .holidayCutoffInfo(List.of(holidayCutoffInfo))
                .build())
        .build();
  }

  public HolidayCutoffKey getHolidayCutoffKey() {
    SourcingAttributeValuesInfo sourcingAttributeValuesInfo =
        SourcingAttributeValuesInfo.builder()
            .requiredAttributesValue(HOLIDAY_CUTOFF_REQUIRED_ATTRIBUTES)
            .build();
    return HolidayCutoffKey.builder()
        .holidayCutoffRulesRequest(
            HolidayCutoffRulesRequest.builder()
                .orgId(ORG_ID)
                .sourcingAttributesDefinitionId(1L)
                .sourcingAttributeValuesInfo(sourcingAttributeValuesInfo)
                .build())
        .build();
  }

  public RulesConfigurationValue getRulesConfigurationValue() {
    return RulesConfigurationValue.builder()
        .rulesConfigurationResponse(
            RulesConfigurationResponse.builder()
                .orgId(ORG_ID)
                .rule(HOLIDAY_CUTOFF_RULE)
                .ruleName(HOLIDAY_CUTOFF_NAME)
                .attributeDefinitionId(1L)
                .moduleName(RulesConfigurationModuleNameEnum.PROCESSING_TIME)
                .scope(SourcingAttributesDefinitionScopeEnum.ML_RULE)
                .build())
        .build();
  }

  public RulesConfigurationKey getRulesConfigurationKey() {
    return RulesConfigurationKey.builder()
        .fetchRuleConfigurationRequest(
            FetchRuleConfigurationRequest.builder()
                .scope(SourcingAttributesDefinitionScopeEnum.ML_RULE)
                .moduleName(RulesConfigurationModuleNameEnum.PROCESSING_TIME)
                .orgId("NEXTUPLE_GR")
                .attributeDefinitionId(229L)
                .attributeValuesInfo(
                    SourcingAttributeValuesInfo.builder()
                        .requiredAttributesValue("Node1:Item1")
                        .build())
                .build())
        .build();
  }

  public BaseResponse<RulesConfigurationResponse> getRulesConfigurationResponse() {
    BaseResponse<RulesConfigurationResponse> response = new BaseResponse<>();

    RulesConfigurationResponse rulesConfigurationResponse =
        RulesConfigurationResponse.builder()
            .orgId(ORG_ID)
            .rule(HOLIDAY_CUTOFF_RULE)
            .ruleName(HOLIDAY_CUTOFF_NAME)
            .attributeDefinitionId(1L)
            .moduleName(RulesConfigurationModuleNameEnum.PROCESSING_TIME)
            .scope(SourcingAttributesDefinitionScopeEnum.ML_RULE)
            .build();

    response.setPayload(rulesConfigurationResponse);
    return response;
  }

  public NodeGroupCacheKey getNodeGroupCacheKey() {
    return NodeGroupCacheKey.builder().orgId(TestUtil.ORG_ID).nodeId(NODE1).build();
  }

  public NodeGroupCacheValue getNodeGroupCacheValue() {
    NodePriorityResponse nodePriorityResponse1 =
        NodePriorityResponse.builder()
            .orgId(TestUtil.ORG_ID)
            .nodeId(TestUtil.NODE1)
            .nodeGroupId(1L)
            .priority(1)
            .build();
    NodePriorityResponse nodePriorityResponse2 =
        NodePriorityResponse.builder()
            .orgId(TestUtil.ORG_ID)
            .nodeId(TestUtil.NODE2)
            .nodeGroupId(2L)
            .priority(1)
            .build();
    NodeGroupCacheValue nodeGroupCacheValue =
        NodeGroupCacheValue.builder()
            .nodePriorityResponseList(List.of(nodePriorityResponse1, nodePriorityResponse2))
            .build();
    return nodeGroupCacheValue;
  }

  public BaseResponse<List<NodePriorityResponse>> getBaseResponseListOfNodePriorityResponse() {
    return BaseResponse.builder()
        .payload(getNodeGroupCacheValue().getNodePriorityResponseList())
        .build();
  }

  public GroupDefinitionRuleCacheKey getGroupDefinitionRuleCacheKey() {
    return GroupDefinitionRuleCacheKey.builder()
        .fetchGroupDefinitionRequest(
            FetchGroupDefinitionRequest.builder()
                .orgId(ORG_ID)
                .sourcingAttributeDefinitionId(1L)
                .attributeValuesInfo(
                    SourcingAttributeValuesInfo.builder()
                        .requiredAttributesValue("R1:R2")
                        .optionalAttributesValue("O1:O2")
                        .build())
                .build())
        .build();
  }

  public GroupDefinitionRuleCacheValue getGroupDefinitionRuleCacheValue() {
    return GroupDefinitionRuleCacheValue.builder()
        .groupDefinitionResponse(
            GroupDefinitionResponse.builder()
                .id(1L)
                .sourcingAttributesDefinitionId(1L)
                .groupName("Grp1")
                .orgId(ORG_ID)
                .reqAttributesValue("R1:R2")
                .optionalAttributesValue("O1:O2")
                .build())
        .build();
  }

  public BaseResponse<GroupDefinitionResponse> getBaseResponseOfGroupDefinitionResponse() {
    return BaseResponse.builder()
        .payload(getGroupDefinitionRuleCacheValue().getGroupDefinitionResponse())
        .build();
  }
}
