/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.promise.sourcing.rule;

import static com.nextuple.promise.sourcing.rule.api.domain.enums.HolidayCutoffStatus.ACTIVE;
import static com.nextuple.promise.sourcing.rule.utils.PromiseSourcingRuleConstants.ALLOCATION_RULE_ID;
import static com.nextuple.promise.sourcing.rule.utils.PromiseSourcingRuleConstants.DESTINATION_GEO_ZONE;
import static com.nextuple.promise.sourcing.rule.utils.PromiseSourcingRuleConstants.EXPRESS;
import static com.nextuple.promise.sourcing.rule.utils.PromiseSourcingRuleConstants.NEXTDAY;
import static com.nextuple.promise.sourcing.rule.utils.PromiseSourcingRuleConstants.PRIORITY;
import static com.nextuple.promise.sourcing.rule.utils.PromiseSourcingRuleConstants.SDND;
import static com.nextuple.promise.sourcing.rule.utils.PromiseSourcingRuleConstants.SERVICE_OPTION;
import static com.nextuple.promise.sourcing.rule.utils.PromiseSourcingRuleConstants.STANDARD;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.pojo.PageParams;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.common.response.error.FieldError;
import com.nextuple.node.domain.outbound.NodeResponse;
import com.nextuple.postal.code.timezone.api.domain.dto.PostalCodeTimezoneDto;
import com.nextuple.postal.code.timezone.api.domain.outbound.PostalCodeResponse;
import com.nextuple.promise.sourcing.rule.api.domain.dto.AllConstraintUIDto;
import com.nextuple.promise.sourcing.rule.api.domain.dto.PromiseSourcingRuleDto;
import com.nextuple.promise.sourcing.rule.api.domain.enums.HolidayCutoffDaysType;
import com.nextuple.promise.sourcing.rule.api.domain.enums.HolidayCutoffStatus;
import com.nextuple.promise.sourcing.rule.api.domain.enums.RulesConfigurationModuleNameEnum;
import com.nextuple.promise.sourcing.rule.api.domain.enums.SourcingAttributesDefinitionScopeEnum;
import com.nextuple.promise.sourcing.rule.api.domain.enums.SourcingAttributesDefinitionStatus;
import com.nextuple.promise.sourcing.rule.api.domain.enums.SourcingConstraintEnum;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.AttributeDetailsUIRequest;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.CreateNodeGroupRequest;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.CreatePromiseSourcingRuleRequest;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.CreateSourcingAttributeRequest;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.DeleteNodeGroupsRequest;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.FetchGroupDefinitionRequest;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.FetchPromiseSourcingRuleRequest;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.FetchRuleConfigurationRequest;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.FetchSourcingRulesRequest;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.GroupDefinitionRequest;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.HolidayCutoffRequest;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.HolidayCutoffRulesRequest;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.HolidayCutoffUpdateRequest;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.NamedOptimizationStrategyRequest;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.NamedOptimizationStrategyUpdationRequest;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.NodePriorityRequest;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.NodePriorityUpdationRequest;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.OptimizationRuleUpdationUIRequest;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.OptimizationStrategyUIRequest;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.RulesConfigurationRequest;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.RulesConfigurationsRequest;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.SourcingAttributesDefinitionRequest;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.SourcingAttributesDefinitionUpdationRequest;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.SourcingConstraintRequest;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.SourcingConstraintUpdationRequest;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.SourcingRuleUpdationRequest;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.UpdateNodeGroupRequest;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.UpdatePromiseSourcingRuleRequest;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.UpdateSourcingRuleNodeGroupRequest;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.UpdateSourcingRuleRequest;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.AllSourcingRulesResponse;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.AttributeDetailsUIResponse;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.DetailedOptimizationStrategyResponse;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.FetchPromiseSourcingRuleResponse;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.FetchSourcingRulesResponse;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.GroupDefinitionListResponse;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.GroupDefinitionResponse;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.HolidayCutoffRulesResponse;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.NamedOptimizationStrategyResponse;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.NodeDetail;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.NodeGroupDetailWithPriorityResponse;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.NodeGroupDetailsResponse;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.NodeGroupResponse;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.NodeGroupWithNodesResponse;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.NodePriorityResponse;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.OptimizationRuleUIResponse;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.PageResponse;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.PageResponseForHolidayCutoff;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.PaginationAttribute;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.RulesConfigurationResponse;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.SourcingAttributeDefinitionUIResponse;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.SourcingAttributeResponse;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.SourcingAttributesDefinitionResponse;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.SourcingConstraintDetailsResponse;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.SourcingConstraintsResponse;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.edit.sourcing.rules.UpdateNodeGroupDetailsResponse;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.edit.sourcing.rules.UpdateSourcingAttributeResponse;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.edit.sourcing.rules.UpdateSourcingRuleConfigurationResponse;
import com.nextuple.promise.sourcing.rule.api.domain.pojo.AttributeInfo;
import com.nextuple.promise.sourcing.rule.api.domain.pojo.GroupDefinitionInfo;
import com.nextuple.promise.sourcing.rule.api.domain.pojo.HolidayCutoffInfo;
import com.nextuple.promise.sourcing.rule.api.domain.pojo.NodeGroupDetailsInfo;
import com.nextuple.promise.sourcing.rule.api.domain.pojo.NodeGroupInfo;
import com.nextuple.promise.sourcing.rule.api.domain.pojo.NodePriorityInfo;
import com.nextuple.promise.sourcing.rule.api.domain.pojo.NodePriorityInfoV1;
import com.nextuple.promise.sourcing.rule.api.domain.pojo.RuleConfigurationParam;
import com.nextuple.promise.sourcing.rule.api.domain.pojo.ServiceOptionInfo;
import com.nextuple.promise.sourcing.rule.api.domain.pojo.SourcingAttributeValuesInfo;
import com.nextuple.promise.sourcing.rule.api.domain.pojo.SourcingConstraintInfo;
import com.nextuple.promise.sourcing.rule.api.domain.pojo.SourcingRuleDetails;
import com.nextuple.promise.sourcing.rule.api.domain.pojo.SourcingRulesInfo;
import com.nextuple.promise.sourcing.rule.domain.entity.HolidayCutoffEntity;
import com.nextuple.promise.sourcing.rule.domain.mapper.PromiseSourcingRuleMapper;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.mapstruct.factory.Mappers;
import org.springframework.http.HttpStatus;

public class TestUtil {
  public static final String ORG_ID = "ABC";
  public static final String NODE_ID = "Node-1";
  public static final String STREET = "street-1";
  public static final String CITY = "city-1";
  public static final String PROVINCE = "province-1";
  public static final String POSTAL_CODE = "33666";
  public static final String COUNTRY = "country-1";
  public static final String LATITUDE = "43.769912";
  public static final String LONGITUDE = "-79.296678";
  public static final String TIME_ZONE = "America/Toronto";
  public static final String SORT_BY = "id";
  public static final String SORT_ORDER_DESC = "DESC";
  public static final String DEFAULT_SORT_ORDER = "ASC";
  public static Boolean SHIP_TO_TIME = Boolean.TRUE;
  public static Boolean BOPIS_ELIGIBLE = Boolean.TRUE;
  public static Boolean SDND_ELIGIBLE = Boolean.TRUE;
  public static Boolean EXPRESS_ELIGIBLE = Boolean.TRUE;
  public static String NODE_TYPE = "MFC";
  public static Boolean IS_ACTIVE = Boolean.TRUE;
  public static final String Node1 = "Node-1";
  public static final Long SOURCING_ATTRIBUTE_ID = 1L;
  public static final Long SOURCING_ATTRIBUTE_ID2 = 2L;
  public static final String ATTRIBUTE_NAME = "attribute-1";
  public static final String ATTRIBUTE_NAME2 = "attribute-2";
  public static final String ATTRIBUTE_NAME_2 = "serviceOption";
  public static Boolean IS_DERIVED_TRUE = Boolean.TRUE;
  public static Boolean IS_DERIVED_FALSE = Boolean.FALSE;
  public static String CUSTOM_ATTRIBUTE_KEY = "custom-attribute-key1";
  public static String EMPTY_CUSTOM_ATTRIBUTE_KEY = "";
  public static String JSON_PATH = "/path-1";
  public static String EMPTY_JSON_PATH = "";

  public static final Long NODE_GROUP_ID = 1L;
  public static final String NODE_GROUP_NAME = "NG1";
  public static final String NODE_GROUP_DESCRIPTION = "Node Group 1";
  public static final Long NODE_GROUP_DETAIL_ID = 1L;

  public static final Integer NODE_PRIORITY = 1;
  public static final Long SOURCING_ATTRIBUTES_DEFINITION_ID = 1L;
  public static final String SOURCING_RULE_ATTRIBUTES_DEFINITION_NAME = "definition1";
  public static final String SOURCING_RULE_NAME = "sourcingRule1";
  public static final String REQUIRED_ATTRIBUTES = "1,2";
  public static final String REQUIRED_ATTRIBUTES_INCONSISTENT = "1,2,3";
  public static final String OPTIONAL_ATTRIBUTES = "3";
  public static final Long SOURCING_RULE_ID = 1L;
  public static final String SOURCING_RULE = "V1:V2";
  public static final Integer SEQUENCE = 1;
  public static final Integer PAGE_NO = 1;
  public static final Integer PAGE_SIZE = 5;
  public static final String SOURCING_RULE_EXCEPTION_MESSAGE = "Sourcing rule not found";
  public static final JsonNode CUSTOM_ATTRIBUTES =
      JsonNodeFactory.instance.objectNode().put("key1", "value1").put("key2", "value2");

  public static final Long ID = 1L;
  public static final String DEFAULT_GROUP_ID = "DEFAULT";
  public static final SourcingConstraintEnum SOURCING_CONSTRAINT =
      SourcingConstraintEnum.SHIP_COMPLETE_LINE;
  public static final String SOURCING_CONSTRAINT_LABEL = "Ship Complete Line";
  public static final String SOURCING_CONSTRAINT_VALUE_1 = "1";
  public static final String SOURCING_CONSTRAINT_VALUE_2 = "0";
  public static final String REQUIRED_ATTRIBUTES_VALUE = "V1:V2";
  public static final String OPTIONAL_ATTRIBUTES_VALUE = "V3:V4";
  public static final String GROUP_NAME = "group1";
  public static final String GROUP_ID = "1";
  public static final Long OPTIMIZATION_STRATEGY_ID = 1L;
  public static final String OPTIMIZATION_STRATEGY_NAME = "EXP_V1";
  public static final String OPTIMIZATION_STRATEGY_DETAILS = "SPEED,PRIORITY";
  public static final String OPTIMIZATION_STRATEGY_DETAILS_2 = "WEIGHTAGE";

  public static final String UPDATED_OPTIMIZATION_STRATEGY_NAME = "EXP_V2";
  public static final String UPDATED_OPTIMIZATION_STRATEGY_DETAILS = "SPLIT, SPEED";
  public static final Long NUMBER_OF_SOURCING_RULES = 1L;
  public static final Long ATTRIBUTE_ID = 1L;
  public static final Long ATTRIBUTE_ID_2 = 2L;

  public static final String ATTRIBUTE_VALUE = "SERVICE_OPTION";
  public static final String RULE_NAME_UI = "ruleName";
  public static final Long ATTRIBUTE_NAME_ID = 1L;
  public static final String ATTRIBUTE_NAME_UI = "serviceOption";
  public static final String ATTRIBUTE_VALUE_UI = "SDND";
  public static final String ATTRIBUTE_VALUE_UI_2 = "EXPRESS";
  public static final String ORG_ID_1 = "NEXTUPLE_GR";
  public static final String ORG_ID_2 = "NEXTUPLE_GRHR";
  public static final String HOLIDAY_CUTOFF_NAME = "holiday_cutoff_name";
  public static final String HOLIDAY_CUTOFF_NAME2 = "holiday_cutoff_name2";
  public static final String HOLIDAY_CUTOFF_RULE = "EXPRESS:R1";
  public static final String HOLIDAY_CUTOFF_DESCRIPTION = "holiday_cutoff_description";
  public static final HolidayCutoffStatus STATUS = HolidayCutoffStatus.ACTIVE;
  public static final Date START_DATE = new Date();
  public static final Date HOLIDAY_CUTOFF_DATE = new Date();
  public static final Date HOLIDAY_DELIVERY_DATE = new Date();
  public static final Double PRE_CUTOFF_DAYS = 0.0;
  public static final Double DELIVERY_COOL_DOWN_DAYS = 3.0;
  public static final HolidayCutoffDaysType PRE_CUTOFF_DAYS_TYPE =
      HolidayCutoffDaysType.BUSINESS_DAYS;
  public static final HolidayCutoffDaysType DELIVERY_COOL_DOWN_DAYS_TYPE =
      HolidayCutoffDaysType.BUSINESS_DAYS;
  public static final HolidayCutoffStatus DEFAULT_STATUS = HolidayCutoffStatus.ACTIVE;
  public static final Double DEFAULT_DAYS = 0.0;
  public static final HolidayCutoffDaysType DEFAULT_DAYS_TYPE = HolidayCutoffDaysType.BUSINESS_DAYS;
  public static final String HOLIDAY_CUTOFF_REQUIRED_ATTRIBUTES = "EXPRESS:R2";

  public static final Date DATE = new Date();

  private static final PromiseSourcingRuleMapper INSTANCE_PROMISE =
      Mappers.getMapper(PromiseSourcingRuleMapper.class);
  public static final String getNodeNotFoundError =
      "{\n"
          + "    \"timestamp\": \"2022-24-15T11:29:09.449+0000\",\n"
          + "    \"status\": 400,\n"
          + "    \"error\": \"Bad Request\",\n"
          + "    \"message\": \"Node not found\"\n"
          + "}";

  public PromiseSourcingRuleDomainDto getPromiseSourcingRule() {
    PromiseSourcingRuleDomainDto promiseSourcingRule = new PromiseSourcingRuleDomainDto();
    promiseSourcingRule.setPriority(PRIORITY);
    promiseSourcingRule.setOrgId(ORG_ID);
    promiseSourcingRule.setSourceNodes(Collections.singleton(Node1));
    promiseSourcingRule.setDestinationGeoZone(DESTINATION_GEO_ZONE);
    promiseSourcingRule.setServiceOption(SERVICE_OPTION);
    promiseSourcingRule.setAllocationRuleId(ALLOCATION_RULE_ID);
    return promiseSourcingRule;
  }

  public PromiseSourcingRuleDto getPromiseSourcingRuleDto() {
    return INSTANCE_PROMISE.convertToPromiseSourcingRuleDto(getPromiseSourcingRule());
  }

  public List<PromiseSourcingRuleDomainDto> getPromiseSourcingRuleList() {
    List<PromiseSourcingRuleDomainDto> promiseSourcingRuleList = new ArrayList<>();

    PromiseSourcingRuleDomainDto promiseSourcingRule1 = new PromiseSourcingRuleDomainDto();
    promiseSourcingRule1.setPriority(PRIORITY);
    promiseSourcingRule1.setOrgId(ORG_ID);
    promiseSourcingRule1.setSourceNodes(Collections.singleton(Node1));
    promiseSourcingRule1.setDestinationGeoZone(DESTINATION_GEO_ZONE);
    promiseSourcingRule1.setServiceOption(SDND);
    promiseSourcingRule1.setAllocationRuleId(ALLOCATION_RULE_ID);

    PromiseSourcingRuleDomainDto promiseSourcingRule2 = new PromiseSourcingRuleDomainDto();
    promiseSourcingRule2.setPriority(PRIORITY);
    promiseSourcingRule2.setOrgId(ORG_ID);
    promiseSourcingRule2.setSourceNodes(Collections.singleton("Node-2"));
    promiseSourcingRule2.setDestinationGeoZone(DESTINATION_GEO_ZONE);
    promiseSourcingRule2.setServiceOption(STANDARD);
    promiseSourcingRule2.setAllocationRuleId(ALLOCATION_RULE_ID);

    PromiseSourcingRuleDomainDto promiseSourcingRule3 = new PromiseSourcingRuleDomainDto();
    promiseSourcingRule3.setPriority(PRIORITY);
    promiseSourcingRule3.setOrgId(ORG_ID);
    promiseSourcingRule3.setSourceNodes(Collections.singleton("Node-3"));
    promiseSourcingRule3.setDestinationGeoZone(DESTINATION_GEO_ZONE);
    promiseSourcingRule3.setServiceOption(EXPRESS);
    promiseSourcingRule3.setAllocationRuleId(ALLOCATION_RULE_ID);

    PromiseSourcingRuleDomainDto promiseSourcingRule4 = new PromiseSourcingRuleDomainDto();
    promiseSourcingRule4.setPriority(PRIORITY);
    promiseSourcingRule4.setOrgId(ORG_ID);
    promiseSourcingRule4.setSourceNodes(Collections.singleton("Node-4"));
    promiseSourcingRule4.setDestinationGeoZone(DESTINATION_GEO_ZONE);
    promiseSourcingRule4.setServiceOption(NEXTDAY);
    promiseSourcingRule4.setAllocationRuleId(ALLOCATION_RULE_ID);

    Collections.addAll(
        promiseSourcingRuleList,
        promiseSourcingRule1,
        promiseSourcingRule2,
        promiseSourcingRule3,
        promiseSourcingRule4);
    return promiseSourcingRuleList;
  }

  public FetchPromiseSourcingRuleResponse getFetchPromiseSourcingRuleResponse() {
    return FetchPromiseSourcingRuleResponse.builder()
        .serviceOptionSourcingRules(
            Map.of(
                SDND,
                List.of(
                    ServiceOptionInfo.builder()
                        .priority(PRIORITY)
                        .sourceNodes(Collections.singleton(Node1))
                        .build()),
                STANDARD,
                new ArrayList<>(),
                EXPRESS,
                new ArrayList<>(),
                NEXTDAY,
                List.of(
                    ServiceOptionInfo.builder()
                        .priority(PRIORITY)
                        .sourceNodes(Collections.singleton(Node1 + "2"))
                        .build())))
        .build();
  }

  public FetchPromiseSourcingRuleRequest getFetchPromiseSourcingRuleRequest() {
    return FetchPromiseSourcingRuleRequest.builder()
        .orgId(ORG_ID)
        .allocationRuleId(ALLOCATION_RULE_ID)
        .destinationGeoZone(DESTINATION_GEO_ZONE)
        .serviceOptions(List.of(SDND, STANDARD, EXPRESS, NEXTDAY, "UNKNOWN"))
        .build();
  }

  public UpdatePromiseSourcingRuleRequest getUpdatePromiseSourcingRuleRequest() {
    return UpdatePromiseSourcingRuleRequest.builder()
        .sourceNodes(Collections.singleton(Node1))
        .build();
  }

  public CreatePromiseSourcingRuleRequest getPromiseSourcingRuleCreationRequest() {
    return CreatePromiseSourcingRuleRequest.builder()
        .serviceOption(SERVICE_OPTION)
        .priority(PRIORITY)
        .destinationGeoZone(DESTINATION_GEO_ZONE)
        .sourceNodes(Collections.singleton(Node1))
        .allocationRuleId(ALLOCATION_RULE_ID)
        .orgId(ORG_ID)
        .build();
  }

  public List<PromiseSourcingRuleDto> getPromiseSourcingRuleByOrgId() {
    return Collections.singletonList(getPromiseSourcingRuleDto());
  }

  public List<PromiseSourcingRuleDto> getPromiseSourcingRuleByPriority() {
    return Collections.singletonList(getPromiseSourcingRuleDto());
  }

  public NodeResponse getNodeResponse() {
    return NodeResponse.builder()
        .nodeId(NODE_ID)
        .orgId(ORG_ID)
        .street(STREET)
        .bopisEligible(BOPIS_ELIGIBLE)
        .city(CITY)
        .country(COUNTRY)
        .nodeType(NODE_TYPE)
        .isActive(IS_ACTIVE)
        .latitude(LATITUDE)
        .longitude(LONGITUDE)
        .zipCode(POSTAL_CODE)
        .state(PROVINCE)
        .shipToHome(SHIP_TO_TIME)
        .timezone(TIME_ZONE)
        .build();
  }

  public BaseResponse<NodeResponse> getBaseResponseOfNode() {
    return BaseResponse.builder()
        .message("Node details fetched successfully")
        .success(true)
        .payload(getNodeResponse())
        .build();
  }

  public NodeResponse getNodeResponse2() {
    return NodeResponse.builder()
        .nodeId(NODE_ID)
        .orgId(ORG_ID)
        .street(STREET)
        .bopisEligible(BOPIS_ELIGIBLE)
        .city(CITY)
        .country(COUNTRY)
        .nodeType(NODE_TYPE)
        .isActive(false)
        .latitude(LATITUDE)
        .longitude(LONGITUDE)
        .zipCode(POSTAL_CODE)
        .state(PROVINCE)
        .shipToHome(SHIP_TO_TIME)
        .timezone(TIME_ZONE)
        .build();
  }

  public BaseResponse<NodeResponse> getBaseResponseOfNode2() {
    return BaseResponse.builder()
        .message("Node details fetched successfully")
        .success(true)
        .payload(getNodeResponse2())
        .build();
  }

  public BaseResponse<NodeResponse> getBaseResponseOfNode3() {
    return BaseResponse.builder()
        .message("Calendar details added successfully")
        .success(false)
        .payload(getNodeResponse())
        .build();
  }

  public PostalCodeTimezoneDto getPostalCodeTimezoneDto() {
    return PostalCodeTimezoneDto.builder()
        .orgId("ABC")
        .zipCodePrefix("IST")
        .country("COUNTRY")
        .state("STATE")
        .city("CITY")
        .latitude("LATITUDE")
        .longitude("LONGITUDE")
        .timeZone("TIME_ZONE")
        .build();
  }

  public BaseResponse<PostalCodeTimezoneDto> getBaseResponseOfPostalCodeTimezoneDto() {
    BaseResponse<PostalCodeTimezoneDto> response = new BaseResponse<>();
    response.setPayload(getPostalCodeTimezoneDto());
    response.setSuccess(true);
    return response;
  }

  public BaseResponse<PostalCodeTimezoneDto> getBaseResponseOfPostalCodeTimezoneDto2() {
    BaseResponse<PostalCodeTimezoneDto> response = new BaseResponse<>();
    response.setPayload(getPostalCodeTimezoneDto());
    response.setSuccess(false);
    return response;
  }

  public BaseResponse<List<PostalCodeResponse>> getPostalCodeResponse() {
    PostalCodeResponse postalCodeResponse =
        PostalCodeResponse.builder()
            .zipCodePrefix("AAA")
            .zipCode("AAABBB")
            .orgId(ORG_ID)
            .city("DEL")
            .country("CA")
            .timeZone("EST")
            .build();
    return BaseResponse.builder().payload(List.of(postalCodeResponse)).build();
  }

  public BaseResponse<List<PostalCodeResponse>> getEmptyPostalCodeResponse() {
    return BaseResponse.builder().success(false).payload(null).build();
  }

  public SourcingAttributeDomainDto getSourcingAttributeEntity() {
    SourcingAttributeDomainDto sourcingAttributeEntity = new SourcingAttributeDomainDto();
    sourcingAttributeEntity.setId(SOURCING_ATTRIBUTE_ID);
    sourcingAttributeEntity.setOrgId(ORG_ID);
    sourcingAttributeEntity.setAttributeName(ATTRIBUTE_NAME);
    sourcingAttributeEntity.setIsDerived(IS_DERIVED_TRUE);
    sourcingAttributeEntity.setCustomAttributeKey(CUSTOM_ATTRIBUTE_KEY);
    sourcingAttributeEntity.setJsonPath(JSON_PATH);
    sourcingAttributeEntity.setCustomAttributes(CUSTOM_ATTRIBUTES);
    return sourcingAttributeEntity;
  }

  public SourcingAttributeDomainDto getSourcingAttributeEntity2() {
    SourcingAttributeDomainDto sourcingAttributeEntity = new SourcingAttributeDomainDto();
    sourcingAttributeEntity.setId(SOURCING_ATTRIBUTE_ID2);
    sourcingAttributeEntity.setOrgId(ORG_ID);
    sourcingAttributeEntity.setAttributeName(ATTRIBUTE_NAME);
    sourcingAttributeEntity.setIsDerived(IS_DERIVED_TRUE);
    sourcingAttributeEntity.setCustomAttributeKey(CUSTOM_ATTRIBUTE_KEY);
    sourcingAttributeEntity.setJsonPath(JSON_PATH);
    return sourcingAttributeEntity;
  }

  public List<SourcingAttributeDomainDto> getSourcingAttributeDomainDtos() {
    SourcingAttributeDomainDto dto = getSourcingAttributeEntity();
    SourcingAttributeDomainDto dto2 = getSourcingAttributeEntity2();
    List<SourcingAttributeDomainDto> sourcingAttributeDomainDtoList = new ArrayList<>();
    sourcingAttributeDomainDtoList.add(dto2);
    sourcingAttributeDomainDtoList.add(dto);

    return sourcingAttributeDomainDtoList;
  }

  public SourcingAttributeResponse getSourcingAttributeResponse() {
    return SourcingAttributeResponse.builder()
        .id(SOURCING_ATTRIBUTE_ID)
        .orgId(ORG_ID)
        .attributeName(ATTRIBUTE_NAME)
        .isDerived(IS_DERIVED_TRUE)
        .customAttributeKey(CUSTOM_ATTRIBUTE_KEY)
        .jsonPath(EMPTY_JSON_PATH)
        .build();
  }

  public CreateSourcingAttributeRequest getCreateSourcingAttributeRequest() {
    return CreateSourcingAttributeRequest.builder()
        .orgId(ORG_ID)
        .attributeName(ATTRIBUTE_NAME)
        .isDerived(IS_DERIVED_TRUE)
        .customAttributeKey(CUSTOM_ATTRIBUTE_KEY)
        .jsonPath(EMPTY_JSON_PATH)
        .customAttributes(JsonNodeFactory.instance.objectNode().put("key1", "value1"))
        .build();
  }

  public CreateSourcingAttributeRequest getCreateSourcingAttributeRequest2() {
    return CreateSourcingAttributeRequest.builder()
        .orgId(ORG_ID)
        .attributeName(ATTRIBUTE_NAME)
        .isDerived(IS_DERIVED_TRUE)
        .customAttributeKey(EMPTY_CUSTOM_ATTRIBUTE_KEY)
        .jsonPath(JSON_PATH)
        .build();
  }

  public CreateSourcingAttributeRequest getCreateSourcingAttributeRequest3() {
    return CreateSourcingAttributeRequest.builder()
        .orgId(ORG_ID)
        .attributeName(ATTRIBUTE_NAME)
        .isDerived(IS_DERIVED_FALSE)
        .customAttributeKey(CUSTOM_ATTRIBUTE_KEY)
        .jsonPath(EMPTY_JSON_PATH)
        .build();
  }

  public CreateSourcingAttributeRequest getCreateSourcingAttributeRequestValidation() {
    return CreateSourcingAttributeRequest.builder()
        .orgId(ORG_ID)
        .attributeName(ATTRIBUTE_NAME)
        .isDerived(IS_DERIVED_TRUE)
        .customAttributeKey(CUSTOM_ATTRIBUTE_KEY)
        .jsonPath(JSON_PATH)
        .build();
  }

  public NodeGroupDomainDto getNodeGroupEntity() {
    NodeGroupDomainDto nodeGroupEntity = new NodeGroupDomainDto();
    nodeGroupEntity.setId(NODE_GROUP_ID);
    nodeGroupEntity.setOrgId(ORG_ID);
    nodeGroupEntity.setNodeGroupName(NODE_GROUP_NAME);
    nodeGroupEntity.setNodeGroupDescription(NODE_GROUP_NAME);
    nodeGroupEntity.setCustomAttributes(
        JsonNodeFactory.instance.objectNode().put("key1", "value1"));

    return nodeGroupEntity;
  }

  public List<NodeGroupDomainDto> getNodeGroupEntityList() {
    NodeGroupDomainDto nodeGroupEntity1 = new NodeGroupDomainDto();
    nodeGroupEntity1.setId(NODE_GROUP_ID);
    nodeGroupEntity1.setOrgId(ORG_ID);
    nodeGroupEntity1.setNodeGroupName(NODE_GROUP_NAME);
    nodeGroupEntity1.setNodeGroupDescription(NODE_GROUP_NAME);

    NodeGroupDomainDto nodeGroupEntity2 = new NodeGroupDomainDto();
    nodeGroupEntity2.setId(2L);
    nodeGroupEntity2.setOrgId(ORG_ID);
    nodeGroupEntity2.setNodeGroupName("NG2");
    nodeGroupEntity2.setNodeGroupDescription("NG2");

    return List.of(nodeGroupEntity1, nodeGroupEntity2);
  }

  public NodeGroupResponse getNodeGroupResponse() {
    return NodeGroupResponse.builder()
        .id(NODE_GROUP_ID)
        .orgId(ORG_ID)
        .nodeGroupName(NODE_GROUP_NAME)
        .nodeGroupDescription(NODE_GROUP_DESCRIPTION)
        .build();
  }

  public CreateNodeGroupRequest getCreateNodeGroupRequest() {
    return CreateNodeGroupRequest.builder()
        .orgId(ORG_ID)
        .nodeGroupName(NODE_GROUP_NAME)
        .nodeGroupDescription(NODE_GROUP_DESCRIPTION)
        .customAttributes(JsonNodeFactory.instance.objectNode().put("key1", "value1"))
        .build();
  }

  public UpdateNodeGroupRequest getUpdateNodeGroupRequest(String name, String description) {
    return UpdateNodeGroupRequest.builder()
        .nodeGroupName(name)
        .nodeGroupDescription(description)
        .customAttributes(
            JsonNodeFactory.instance.objectNode().put("key1", "value1").put("key2", "value2"))
        .build();
  }

  public NodeGroupResponse getUpdatedNodeGroupResponse(String name, String description) {
    return NodeGroupResponse.builder()
        .id(NODE_GROUP_ID)
        .orgId(ORG_ID)
        .nodeGroupName(name)
        .nodeGroupDescription(description)
        .customAttributes(
            JsonNodeFactory.instance.objectNode().put("key1", "value1").put("key2", "value2"))
        .build();
  }

  public NodePriorityDomainDto getNodePriorityEntity() {
    NodePriorityDomainDto nodeGroupDetailEntity = new NodePriorityDomainDto();
    nodeGroupDetailEntity.setId(NODE_GROUP_DETAIL_ID);
    nodeGroupDetailEntity.setOrgId(ORG_ID);
    nodeGroupDetailEntity.setNodeId(NODE_ID);
    nodeGroupDetailEntity.setPriority(NODE_PRIORITY);
    nodeGroupDetailEntity.setNodeGroupId(NODE_GROUP_ID);
    nodeGroupDetailEntity.setCustomAttributes(
        JsonNodeFactory.instance.objectNode().put("key1", "value1"));

    return nodeGroupDetailEntity;
  }

  public NodePriorityResponse getNodePriorityResponse() {
    return NodePriorityResponse.builder()
        .id(NODE_GROUP_DETAIL_ID)
        .nodeId(NODE_ID)
        .orgId(ORG_ID)
        .priority(NODE_PRIORITY)
        .nodeGroupId(NODE_GROUP_ID)
        .build();
  }

  public NodeGroupDetailsResponse getNodeGroupDetailsListResponse() {
    NodePriorityInfo nodePriorityInfo = new NodePriorityInfo();
    nodePriorityInfo.setPriority(NODE_PRIORITY);
    nodePriorityInfo.setNodeId(NODE_ID);
    return NodeGroupDetailsResponse.builder()
        .orgId(ORG_ID)
        .nodeGroupId(NODE_GROUP_ID)
        .nodeInfo(List.of(nodePriorityInfo))
        .build();
  }

  public NodePriorityRequest getNodePriorityRequest() {
    return NodePriorityRequest.builder()
        .nodeId(NODE_ID)
        .orgId(ORG_ID)
        .priority(NODE_PRIORITY)
        .nodeGroupId(NODE_GROUP_ID)
        .customAttributes(JsonNodeFactory.instance.objectNode().put("key1", "value1"))
        .build();
  }

  public NodePriorityUpdationRequest getNodePriorityUpdationRequest(Integer priority) {
    return NodePriorityUpdationRequest.builder().priority(priority).build();
  }

  public NodePriorityResponse getUpdatedNodePriorityResponse(Integer priority) {
    return NodePriorityResponse.builder()
        .id(NODE_GROUP_DETAIL_ID)
        .nodeId(NODE_ID)
        .orgId(ORG_ID)
        .priority(priority)
        .nodeGroupId(NODE_GROUP_ID)
        .customAttributes(JsonNodeFactory.instance.objectNode().put("key1", "value1"))
        .build();
  }

  public SourcingAttributesDefinitionDomainDto getSourcingRuleAttributesDefinitionEntity(
      SourcingAttributesDefinitionStatus status) {
    SourcingAttributesDefinitionDomainDto sourcingRuleAttributesDefinitionEntity =
        new SourcingAttributesDefinitionDomainDto();
    sourcingRuleAttributesDefinitionEntity.setId(SOURCING_ATTRIBUTES_DEFINITION_ID);
    sourcingRuleAttributesDefinitionEntity.setOrgId(ORG_ID);
    sourcingRuleAttributesDefinitionEntity.setName(SOURCING_RULE_ATTRIBUTES_DEFINITION_NAME);
    sourcingRuleAttributesDefinitionEntity.setReqAttributes(REQUIRED_ATTRIBUTES);
    sourcingRuleAttributesDefinitionEntity.setStatus(status);
    sourcingRuleAttributesDefinitionEntity.setScope(
        SourcingAttributesDefinitionScopeEnum.SOURCING_RULE);
    sourcingRuleAttributesDefinitionEntity.setCustomAttributes(CUSTOM_ATTRIBUTES);
    return sourcingRuleAttributesDefinitionEntity;
  }

  public SourcingAttributesDefinitionDomainDto getSourcingRuleAttributesDefinitionEntity2(
      SourcingAttributesDefinitionStatus status) {
    SourcingAttributesDefinitionDomainDto sourcingRuleAttributesDefinitionEntity =
        new SourcingAttributesDefinitionDomainDto();
    sourcingRuleAttributesDefinitionEntity.setId(SOURCING_ATTRIBUTES_DEFINITION_ID);
    sourcingRuleAttributesDefinitionEntity.setOrgId(ORG_ID);
    sourcingRuleAttributesDefinitionEntity.setName(SOURCING_RULE_ATTRIBUTES_DEFINITION_NAME);
    sourcingRuleAttributesDefinitionEntity.setReqAttributes(REQUIRED_ATTRIBUTES);
    sourcingRuleAttributesDefinitionEntity.setStatus(status);
    sourcingRuleAttributesDefinitionEntity.setScope(
        SourcingAttributesDefinitionScopeEnum.OPTIMIZATION);
    return sourcingRuleAttributesDefinitionEntity;
  }

  public SourcingAttributesDefinitionRequest getSourcingRuleAttributesDefinitionRequest(
      SourcingAttributesDefinitionStatus status) {

    return SourcingAttributesDefinitionRequest.builder()
        .orgId(ORG_ID)
        .name(SOURCING_RULE_ATTRIBUTES_DEFINITION_NAME)
        .reqAttributes(REQUIRED_ATTRIBUTES)
        .status(status)
        .customAttributes(CUSTOM_ATTRIBUTES)
        .build();
  }

  public SourcingAttributesDefinitionResponse getSourcingRuleAttributesDefinitionResponse(
      SourcingAttributesDefinitionStatus status) {

    return SourcingAttributesDefinitionResponse.builder()
        .id(SOURCING_ATTRIBUTES_DEFINITION_ID)
        .orgId(ORG_ID)
        .name(SOURCING_RULE_ATTRIBUTES_DEFINITION_NAME)
        .reqAttributes(REQUIRED_ATTRIBUTES)
        .status(status)
        .customAttributes(CUSTOM_ATTRIBUTES)
        .build();
  }

  public SourcingAttributesDefinitionResponse
      getSourcingRuleAttributesDefinitionResponseWithOptionalAttributes(
          SourcingAttributesDefinitionStatus status) {

    return SourcingAttributesDefinitionResponse.builder()
        .id(SOURCING_ATTRIBUTES_DEFINITION_ID)
        .orgId(ORG_ID)
        .name(SOURCING_RULE_ATTRIBUTES_DEFINITION_NAME)
        .reqAttributes(REQUIRED_ATTRIBUTES)
        .optAttributes(OPTIONAL_ATTRIBUTES)
        .status(status)
        .build();
  }

  public SourcingAttributesDefinitionUpdationRequest
      getSourcingRuleAttributesDefinitionUpdationRequest(
          SourcingAttributesDefinitionStatus status) {

    return SourcingAttributesDefinitionUpdationRequest.builder()
        .optAttributes(OPTIONAL_ATTRIBUTES)
        .status(status)
        .build();
  }

  public SourcingAttributesDefinitionResponse getUpdatedSourcingRuleAttributesDefinitionResponse(
      SourcingAttributesDefinitionStatus status) {

    return SourcingAttributesDefinitionResponse.builder()
        .id(SOURCING_ATTRIBUTES_DEFINITION_ID)
        .orgId(ORG_ID)
        .name(SOURCING_RULE_ATTRIBUTES_DEFINITION_NAME)
        .reqAttributes(REQUIRED_ATTRIBUTES)
        .optAttributes(OPTIONAL_ATTRIBUTES)
        .status(status)
        .build();
  }

  public SourcingAttributesDefinitionDomainDto getUpdatedSourcingRuleAttributesDefinitionEntity(
      SourcingAttributesDefinitionStatus status) {
    SourcingAttributesDefinitionDomainDto sourcingRuleAttributesDefinitionEntity =
        new SourcingAttributesDefinitionDomainDto();
    sourcingRuleAttributesDefinitionEntity.setId(SOURCING_ATTRIBUTES_DEFINITION_ID);
    sourcingRuleAttributesDefinitionEntity.setOrgId(ORG_ID);
    sourcingRuleAttributesDefinitionEntity.setName(SOURCING_RULE_ATTRIBUTES_DEFINITION_NAME);
    sourcingRuleAttributesDefinitionEntity.setReqAttributes(REQUIRED_ATTRIBUTES);
    sourcingRuleAttributesDefinitionEntity.setOptAttributes(OPTIONAL_ATTRIBUTES);
    sourcingRuleAttributesDefinitionEntity.setStatus(status);
    return sourcingRuleAttributesDefinitionEntity;
  }

  public SourcingRulesConfigurationDomainDto getSourcingRulesEntity() {
    SourcingRulesConfigurationDomainDto sourcingRulesConfigurationEntity =
        new SourcingRulesConfigurationDomainDto();
    sourcingRulesConfigurationEntity.setId(SOURCING_RULE_ID);
    sourcingRulesConfigurationEntity.setOrgId(ORG_ID);
    sourcingRulesConfigurationEntity.setSourcingRule(SOURCING_RULE);
    sourcingRulesConfigurationEntity.setSourcingRuleName(SOURCING_RULE_NAME);
    sourcingRulesConfigurationEntity.setSourcingAttributesDefinitionId(
        SOURCING_ATTRIBUTES_DEFINITION_ID);
    sourcingRulesConfigurationEntity.setCustomAttributes(
        JsonNodeFactory.instance.objectNode().put("key1", "value1"));

    return sourcingRulesConfigurationEntity;
  }

  public SourcingRulesConfigurationDomainDto getSourcingRulesEntity2() {
    SourcingRulesConfigurationDomainDto sourcingRulesConfigurationEntity =
        new SourcingRulesConfigurationDomainDto();
    sourcingRulesConfigurationEntity.setId(2L);
    sourcingRulesConfigurationEntity.setOrgId(ORG_ID);
    sourcingRulesConfigurationEntity.setSourcingRule("EXPRESS:EVERYDAY");
    sourcingRulesConfigurationEntity.setSourcingRuleName("sourcingRule2");
    sourcingRulesConfigurationEntity.setSourcingAttributesDefinitionId(
        SOURCING_ATTRIBUTES_DEFINITION_ID);

    return sourcingRulesConfigurationEntity;
  }

  public SourcingRuleDetailsDomainDto getSourcingRuleDetailsEntity() {
    SourcingRuleDetailsDomainDto sourcingRuleDetailsEntity = new SourcingRuleDetailsDomainDto();
    sourcingRuleDetailsEntity.setId(ID);
    sourcingRuleDetailsEntity.setOrgId(ORG_ID);
    sourcingRuleDetailsEntity.setSourcingRuleId(SOURCING_RULE_ID);
    sourcingRuleDetailsEntity.setSequence(SEQUENCE);
    sourcingRuleDetailsEntity.setNodeGroups(NODE_GROUP_ID.toString());
    sourcingRuleDetailsEntity.setCustomAttributes(
        JsonNodeFactory.instance.objectNode().put("key1", "value1"));

    return sourcingRuleDetailsEntity;
  }

  public RulesConfigurationRequest getRulesConfigurationRequest() {
    return RulesConfigurationRequest.builder()
        .orgId(ORG_ID)
        .sourcingRule(SOURCING_RULE)
        .sourcingRuleName(SOURCING_RULE_NAME)
        .nodeGroups(String.valueOf(NODE_GROUP_ID))
        .sequence(SEQUENCE)
        .sourcingAttributesDefinitionId(SOURCING_ATTRIBUTES_DEFINITION_ID)
        .customAttributes(CUSTOM_ATTRIBUTES)
        .build();
  }

  public RulesConfigurationRequest getRulesConfigurationRequestWithInvalidNodeGroup() {
    return RulesConfigurationRequest.builder()
        .orgId(ORG_ID)
        .sourcingRule(SOURCING_RULE)
        .sourcingRuleName(SOURCING_RULE_NAME)
        .nodeGroups("1,2")
        .sequence(SEQUENCE)
        .sourcingAttributesDefinitionId(SOURCING_ATTRIBUTES_DEFINITION_ID)
        .build();
  }

  public SourcingRuleDetails getSourcingRuleDetails() {
    return SourcingRuleDetails.builder()
        .id(SOURCING_RULE_ID)
        .orgId(ORG_ID)
        .sourcingRule(SOURCING_RULE)
        .nodeGroups(String.valueOf(NODE_GROUP_ID))
        .sequence(SEQUENCE)
        .sourcingAttributesDefinitionId(SOURCING_ATTRIBUTES_DEFINITION_ID)
        .customAttributes(CUSTOM_ATTRIBUTES)
        .build();
  }

  public SourcingRuleUpdationRequest getSourcingRuleUpdationRequest(String nodeGroups) {
    return SourcingRuleUpdationRequest.builder().nodeGroups(nodeGroups).build();
  }

  public SourcingRulesConfigurationDomainDto getUpdatedSourcingRulesEntity(String nodeGroups) {
    SourcingRulesConfigurationDomainDto sourcingRulesConfigurationEntity =
        new SourcingRulesConfigurationDomainDto();
    sourcingRulesConfigurationEntity.setId(SOURCING_RULE_ID);
    sourcingRulesConfigurationEntity.setOrgId(ORG_ID);
    sourcingRulesConfigurationEntity.setSourcingRule(SOURCING_RULE);
    sourcingRulesConfigurationEntity.setSourcingAttributesDefinitionId(
        SOURCING_ATTRIBUTES_DEFINITION_ID);

    return sourcingRulesConfigurationEntity;
  }

  public SourcingRuleDetails getUpdatedSourcingRuleDetails(String nodeGroups) {
    return SourcingRuleDetails.builder()
        .id(SOURCING_RULE_ID)
        .orgId(ORG_ID)
        .sourcingRule(SOURCING_RULE)
        .nodeGroups(nodeGroups)
        .sequence(SEQUENCE)
        .sourcingAttributesDefinitionId(SOURCING_ATTRIBUTES_DEFINITION_ID)
        .build();
  }

  public FetchSourcingRulesRequest fetchSourcingRulesRequest(
      SourcingAttributeValuesInfo sourcingAttributeValuesInfo) {
    return FetchSourcingRulesRequest.builder()
        .orgId(ORG_ID)
        .sourcingAttributesDefinitionId(SOURCING_ATTRIBUTES_DEFINITION_ID)
        .sourcingAttributeValuesInfo(sourcingAttributeValuesInfo)
        .build();
  }

  public FetchSourcingRulesResponse fetchSourcingRulesResponse(String message) {
    NodePriorityInfo nodePriorityInfo = new NodePriorityInfo();
    nodePriorityInfo.setNodeId(NODE_ID);
    nodePriorityInfo.setPriority(PRIORITY);
    NodeGroupDetailsInfo nodeGroupDetailsInfo = new NodeGroupDetailsInfo();
    nodeGroupDetailsInfo.setNodeGroupId(NODE_GROUP_ID);
    nodeGroupDetailsInfo.setNodeInfo(List.of(nodePriorityInfo));
    SourcingRulesInfo sourcingRulesInfo =
        SourcingRulesInfo.builder()
            .orgId(ORG_ID)
            .sourcingRule(SOURCING_RULE)
            .sequence(SEQUENCE)
            .nodeGroupDetailsInfo(List.of(nodeGroupDetailsInfo))
            .sourcingRuleName(SOURCING_RULE_NAME)
            .requiredAttributes(getRequiredAttributes())
            .optionalAttributes(getOptionalAttributes())
            .build();
    return FetchSourcingRulesResponse.builder()
        .sourcingRulesInfo(List.of(sourcingRulesInfo))
        .message(message)
        .build();
  }

  public SourcingConstraintDetailsResponse getSourcingConstraintDetails() {
    return SourcingConstraintDetailsResponse.builder()
        .id(ID)
        .orgId(ORG_ID)
        .sourcingConstraint(SOURCING_CONSTRAINT)
        .sourcingConstraintValue(SOURCING_CONSTRAINT_VALUE_1)
        .build();
  }

  public SourcingConstraintRequest getSourcingConstraintRequest() {
    return SourcingConstraintRequest.builder()
        .orgId(ORG_ID)
        .sourcingConstraint(SOURCING_CONSTRAINT)
        .sourcingConstraintValue(SOURCING_CONSTRAINT_VALUE_1)
        .groupId(GROUP_ID)
        .customAttributes(JsonNodeFactory.instance.objectNode().put("key1", "value1"))
        .build();
  }

  public SourcingConstraintDomainDto getSourcingConstraintEntity() {
    SourcingConstraintDomainDto sourcingConstraintEntity = new SourcingConstraintDomainDto();
    sourcingConstraintEntity.setId(ID);
    sourcingConstraintEntity.setOrgId(ORG_ID);
    sourcingConstraintEntity.setGroupId(DEFAULT_GROUP_ID);
    sourcingConstraintEntity.setSourcingConstraint(SOURCING_CONSTRAINT);
    sourcingConstraintEntity.setSourcingConstraintValue(SOURCING_CONSTRAINT_VALUE_1);
    sourcingConstraintEntity.setCustomAttributes(
        JsonNodeFactory.instance.objectNode().put("key1", "value1"));
    return sourcingConstraintEntity;
  }

  public SourcingConstraintUpdationRequest getSourcingConstraintUpdationRequest() {
    return SourcingConstraintUpdationRequest.builder()
        .sourcingConstraintValue(SOURCING_CONSTRAINT_VALUE_2)
        .build();
  }

  public SourcingConstraintDetailsResponse getUpdatedSourcingConstraintDetails() {
    return SourcingConstraintDetailsResponse.builder()
        .id(ID)
        .orgId(ORG_ID)
        .sourcingConstraint(SOURCING_CONSTRAINT)
        .sourcingConstraintValue(SOURCING_CONSTRAINT_VALUE_2)
        .build();
  }

  public SourcingConstraintsResponse getSourcingConstraintsResponse() {
    SourcingConstraintInfo sourcingConstraintInfo = new SourcingConstraintInfo();
    sourcingConstraintInfo.setSourcingConstraint(SOURCING_CONSTRAINT);
    sourcingConstraintInfo.setSourcingConstraintValue(SOURCING_CONSTRAINT_VALUE_1);
    return SourcingConstraintsResponse.builder()
        .orgId(ORG_ID)
        .sourcingConstraintsInfo(List.of(sourcingConstraintInfo))
        .build();
  }

  public SourcingConstraintDomainDto getUpdatedSourcingConstraintEntity() {
    SourcingConstraintDomainDto sourcingConstraintEntity = new SourcingConstraintDomainDto();
    sourcingConstraintEntity.setId(ID);
    sourcingConstraintEntity.setOrgId(ORG_ID);
    sourcingConstraintEntity.setGroupId(DEFAULT_GROUP_ID);
    sourcingConstraintEntity.setSourcingConstraint(SOURCING_CONSTRAINT);
    sourcingConstraintEntity.setSourcingConstraintValue(SOURCING_CONSTRAINT_VALUE_2);
    return sourcingConstraintEntity;
  }

  public SourcingAttributesDefinitionDomainDto
      getSourcingRuleAttributesDefinitionEntityForOptimization(
          SourcingAttributesDefinitionStatus status) {
    SourcingAttributesDefinitionDomainDto sourcingRuleAttributesDefinitionEntity =
        new SourcingAttributesDefinitionDomainDto();
    sourcingRuleAttributesDefinitionEntity.setId(SOURCING_ATTRIBUTES_DEFINITION_ID);
    sourcingRuleAttributesDefinitionEntity.setOrgId(ORG_ID);
    sourcingRuleAttributesDefinitionEntity.setName(SOURCING_RULE_ATTRIBUTES_DEFINITION_NAME);
    sourcingRuleAttributesDefinitionEntity.setReqAttributes(REQUIRED_ATTRIBUTES);
    sourcingRuleAttributesDefinitionEntity.setStatus(status);
    sourcingRuleAttributesDefinitionEntity.setScope(
        SourcingAttributesDefinitionScopeEnum.OPTIMIZATION);
    return sourcingRuleAttributesDefinitionEntity;
  }

  public SourcingConstraintDetailsResponse getSourcingConstraintDetailsResponse() {
    SourcingConstraintDetailsResponse sourcingConstraintDetailsResponse =
        new SourcingConstraintDetailsResponse();
    sourcingConstraintDetailsResponse.setId(ID);
    sourcingConstraintDetailsResponse.setOrgId(ORG_ID);
    sourcingConstraintDetailsResponse.setGroupId(GROUP_ID);
    sourcingConstraintDetailsResponse.setSourcingConstraint(SOURCING_CONSTRAINT);
    sourcingConstraintDetailsResponse.setSourcingConstraintValue(SOURCING_CONSTRAINT_VALUE_1);
    return sourcingConstraintDetailsResponse;
  }

  public GroupDefinitionDomainDto getGroupDefinitionEntity() {
    GroupDefinitionDomainDto groupDefinitionEntity = new GroupDefinitionDomainDto();
    groupDefinitionEntity.setId(ID);
    groupDefinitionEntity.setSourcingAttributesDefinitionId(SOURCING_ATTRIBUTES_DEFINITION_ID);
    groupDefinitionEntity.setReqAttributesValue(REQUIRED_ATTRIBUTES_VALUE);
    groupDefinitionEntity.setGroupName(GROUP_NAME);
    groupDefinitionEntity.setOrgId(ORG_ID);
    groupDefinitionEntity.setCustomAttributes(
        JsonNodeFactory.instance.objectNode().put("key1", "value1").put("key2", "value2"));
    return groupDefinitionEntity;
  }

  public GroupDefinitionRequest getGroupDefinitionRequest() {
    return GroupDefinitionRequest.builder()
        .orgId(ORG_ID)
        .groupName(GROUP_NAME)
        .sourcingAttributesDefinitionId(SOURCING_ATTRIBUTES_DEFINITION_ID)
        .reqAttributesValue(REQUIRED_ATTRIBUTES_VALUE)
        .optionalAttributesValue(OPTIONAL_ATTRIBUTES_VALUE)
        .customAttributes(
            JsonNodeFactory.instance.objectNode().put("key1", "value1").put("key2", "value2"))
        .build();
  }

  public GroupDefinitionResponse getGroupDefinitionResponse() {
    return GroupDefinitionResponse.builder()
        .id(ID)
        .orgId(ORG_ID)
        .groupName(GROUP_NAME)
        .sourcingAttributesDefinitionId(SOURCING_ATTRIBUTES_DEFINITION_ID)
        .reqAttributesValue(REQUIRED_ATTRIBUTES_VALUE)
        .optionalAttributesValue(OPTIONAL_ATTRIBUTES_VALUE)
        .build();
  }

  public GroupDefinitionListResponse getGroupDefinitionListResponse() {
    GroupDefinitionInfo groupDefinitionInfo = new GroupDefinitionInfo();
    groupDefinitionInfo.setGroupName(GROUP_NAME);
    groupDefinitionInfo.setId(ID);
    groupDefinitionInfo.setReqAttributesValue(REQUIRED_ATTRIBUTES_VALUE);
    return GroupDefinitionListResponse.builder()
        .orgId(ORG_ID)
        .sourcingAttributesDefinitionId(SOURCING_ATTRIBUTES_DEFINITION_ID)
        .groupDefinitionInfoList(List.of(groupDefinitionInfo))
        .build();
  }

  public NamedOptimizationStrategyDomainDto getNamedOptimizationStrategyEntity() {
    NamedOptimizationStrategyDomainDto namedOptimizationStrategyEntity =
        new NamedOptimizationStrategyDomainDto();
    namedOptimizationStrategyEntity.setId(OPTIMIZATION_STRATEGY_ID);
    namedOptimizationStrategyEntity.setOrgId(ORG_ID);
    namedOptimizationStrategyEntity.setGroupId(GROUP_ID);
    namedOptimizationStrategyEntity.setOptimizationStrategyName(OPTIMIZATION_STRATEGY_NAME);
    namedOptimizationStrategyEntity.setOptimizationStrategyDetails(OPTIMIZATION_STRATEGY_DETAILS);
    namedOptimizationStrategyEntity.setCustomAttributes(
        JsonNodeFactory.instance.objectNode().put("key1", "value1").put("key2", "value2"));
    return namedOptimizationStrategyEntity;
  }

  public NamedOptimizationStrategyDomainDto getDefaultNamedOptimizationStrategyEntity() {
    NamedOptimizationStrategyDomainDto namedOptimizationStrategyEntity =
        new NamedOptimizationStrategyDomainDto();
    namedOptimizationStrategyEntity.setId(OPTIMIZATION_STRATEGY_ID);
    namedOptimizationStrategyEntity.setOrgId(ORG_ID);
    namedOptimizationStrategyEntity.setGroupId(DEFAULT_GROUP_ID);
    namedOptimizationStrategyEntity.setOptimizationStrategyName(OPTIMIZATION_STRATEGY_NAME);
    namedOptimizationStrategyEntity.setOptimizationStrategyDetails(OPTIMIZATION_STRATEGY_DETAILS);
    return namedOptimizationStrategyEntity;
  }

  public NamedOptimizationStrategyRequest getNamedOptimizationStrategyRequest() {
    return NamedOptimizationStrategyRequest.builder()
        .orgId(ORG_ID)
        .groupId(GROUP_ID)
        .optimizationStrategyName(OPTIMIZATION_STRATEGY_NAME)
        .optimizationStrategyDetails(OPTIMIZATION_STRATEGY_DETAILS)
        .customAttributes(
            JsonNodeFactory.instance.objectNode().put("key1", "value1").put("key2", "value2"))
        .build();
  }

  public OptimizationStrategyUIRequest getOptimizationStrategyUIRequest() {
    return OptimizationStrategyUIRequest.builder()
        .orgId(ORG_ID)
        .sourcingAttributesDefinitionId(String.valueOf(SOURCING_ATTRIBUTES_DEFINITION_ID))
        .optimizationRuleName(OPTIMIZATION_STRATEGY_NAME)
        .strategy(OPTIMIZATION_STRATEGY_DETAILS)
        .requiredAttributes(getAttributeDetailsUIRequest())
        .constraints(getAllConstraintsDto())
        .build();
  }

  public OptimizationStrategyUIRequest getOptimizationStrategyUIRequest2() {
    return OptimizationStrategyUIRequest.builder()
        .orgId(ORG_ID)
        .sourcingAttributesDefinitionId(String.valueOf(SOURCING_ATTRIBUTES_DEFINITION_ID))
        .optimizationRuleName(OPTIMIZATION_STRATEGY_NAME)
        .strategy(OPTIMIZATION_STRATEGY_DETAILS_2)
        .requiredAttributes(getAttributeDetailsUIRequest())
        .constraints(getAllConstraintsDto())
        .build();
  }

  public OptimizationStrategyUIRequest getOptimizationStrategyUIRequest3() {
    return OptimizationStrategyUIRequest.builder()
        .orgId(ORG_ID)
        .sourcingAttributesDefinitionId(String.valueOf(SOURCING_ATTRIBUTES_DEFINITION_ID))
        .optimizationRuleName(OPTIMIZATION_STRATEGY_NAME)
        .strategy(OPTIMIZATION_STRATEGY_DETAILS_2)
        .requiredAttributes(getAttributeDetailsUIRequest())
        .build();
  }

  public OptimizationStrategyUIRequest getOptimizationStrategyUIRequest4() {
    return OptimizationStrategyUIRequest.builder()
        .orgId(ORG_ID)
        .sourcingAttributesDefinitionId(String.valueOf(SOURCING_ATTRIBUTES_DEFINITION_ID))
        .optimizationRuleName(OPTIMIZATION_STRATEGY_NAME)
        .strategy(OPTIMIZATION_STRATEGY_DETAILS_2)
        .requiredAttributes(getAttributeDetailsUIRequest())
        .constraints(new ArrayList<>())
        .build();
  }

  public OptimizationStrategyUIRequest getOptimizationStrategyUIRequest5() {
    return OptimizationStrategyUIRequest.builder()
        .orgId(ORG_ID)
        .sourcingAttributesDefinitionId(String.valueOf(SOURCING_ATTRIBUTES_DEFINITION_ID))
        .optimizationRuleName(OPTIMIZATION_STRATEGY_NAME)
        .strategy(OPTIMIZATION_STRATEGY_DETAILS)
        .requiredAttributes(getAttributeDetailsUIRequest2())
        .constraints(getAllConstraintsDto())
        .build();
  }

  public List<AttributeDetailsUIRequest> getAttributeDetailsUIRequest() {
    List<AttributeDetailsUIRequest> attributeDetailsUIList = new ArrayList<>();
    AttributeDetailsUIRequest attributeDetailsUI =
        AttributeDetailsUIRequest.builder()
            .attributeId(ATTRIBUTE_ID)
            .attributeName(ATTRIBUTE_NAME_UI)
            .attributeValue(ATTRIBUTE_VALUE_UI)
            .build();
    AttributeDetailsUIRequest attributeDetailsUI2 =
        AttributeDetailsUIRequest.builder()
            .attributeId(ATTRIBUTE_ID_2)
            .attributeName(ATTRIBUTE_NAME_UI)
            .attributeValue(ATTRIBUTE_VALUE_UI_2)
            .build();

    attributeDetailsUIList.add(attributeDetailsUI);
    attributeDetailsUIList.add(attributeDetailsUI2);
    return attributeDetailsUIList;
  }

  public List<AttributeDetailsUIRequest> getAttributeDetailsUIRequest2() {
    List<AttributeDetailsUIRequest> attributeDetailsUIList = new ArrayList<>();
    AttributeDetailsUIRequest attributeDetailsUI =
        AttributeDetailsUIRequest.builder()
            .attributeId(ATTRIBUTE_ID)
            .attributeName(ATTRIBUTE_NAME_UI)
            .attributeValue(ATTRIBUTE_VALUE_UI)
            .build();

    attributeDetailsUIList.add(attributeDetailsUI);
    return attributeDetailsUIList;
  }

  public List<AttributeDetailsUIResponse> getAttributeDetailsUIResponse() {
    List<AttributeDetailsUIResponse> attributeDetailsUIList = new ArrayList<>();
    AttributeDetailsUIResponse attributeDetailsUI =
        AttributeDetailsUIResponse.builder()
            .attributeId(ATTRIBUTE_ID)
            .attributeName(ATTRIBUTE_NAME_UI)
            .attributeValue(ATTRIBUTE_VALUE_UI)
            .build();

    attributeDetailsUIList.add(attributeDetailsUI);
    return attributeDetailsUIList;
  }

  public List<AllConstraintUIDto> getAllConstraintsDto() {
    List<AllConstraintUIDto> allConstraintUIDtoList = new ArrayList<>();
    AllConstraintUIDto allConstraintUIDto =
        AllConstraintUIDto.builder()
            .label(SOURCING_CONSTRAINT_LABEL)
            .value(String.valueOf(SOURCING_CONSTRAINT))
            .build();
    allConstraintUIDtoList.add(allConstraintUIDto);
    return allConstraintUIDtoList;
  }

  public NamedOptimizationStrategyResponse getNamedOptimizationStrategyResponse() {
    return NamedOptimizationStrategyResponse.builder()
        .id(OPTIMIZATION_STRATEGY_ID)
        .orgId(ORG_ID)
        .groupId(GROUP_ID)
        .optimizationStrategyName(OPTIMIZATION_STRATEGY_NAME)
        .optimizationStrategyDetails(OPTIMIZATION_STRATEGY_DETAILS)
        .build();
  }

  public OptimizationRuleUIResponse getOptimizationStrategyUIResponse() {
    return OptimizationRuleUIResponse.builder()
        .optimizationRuleId(OPTIMIZATION_STRATEGY_ID)
        .orgId(ORG_ID)
        .sourcingAttributesDefinitionId(String.valueOf(SOURCING_ATTRIBUTES_DEFINITION_ID))
        .optimizationRuleName(OPTIMIZATION_STRATEGY_NAME)
        .requiredAttributes(getAttributeDetailsUIResponse())
        .strategy(OPTIMIZATION_STRATEGY_DETAILS)
        .constraints(getAllConstraintUIResponse())
        .build();
  }

  public NamedOptimizationStrategyUpdationRequest getNamedOptimizationStrategyUpdationRequest() {
    return NamedOptimizationStrategyUpdationRequest.builder()
        .optimizationStrategyName(OPTIMIZATION_STRATEGY_NAME)
        .customAttributes(
            JsonNodeFactory.instance.objectNode().put("key1", "value1").put("key2", "value2"))
        .build();
  }

  public OptimizationRuleUpdationUIRequest getOptimizationRuleUpdationUIRequest() {
    return OptimizationRuleUpdationUIRequest.builder()
        .optimizationRuleName(UPDATED_OPTIMIZATION_STRATEGY_NAME)
        .strategy(UPDATED_OPTIMIZATION_STRATEGY_DETAILS)
        .constraints(
            List.of(
                AllConstraintUIDto.builder()
                    .label(SOURCING_CONSTRAINT.getConstraintName())
                    .value(String.valueOf(SOURCING_CONSTRAINT))
                    .build()))
        .requiredAttributes(
            List.of(
                AttributeDetailsUIRequest.builder()
                    .attributeId(ATTRIBUTE_ID)
                    .attributeName(ATTRIBUTE_NAME)
                    .attributeValue(ATTRIBUTE_VALUE)
                    .build()))
        .build();
  }

  public NamedOptimizationStrategyDomainDto getUpdatedNamedOptimizationStrategyEntity() {
    NamedOptimizationStrategyDomainDto namedOptimizationStrategyEntity =
        new NamedOptimizationStrategyDomainDto();
    namedOptimizationStrategyEntity.setId(OPTIMIZATION_STRATEGY_ID);
    namedOptimizationStrategyEntity.setOrgId(ORG_ID);
    namedOptimizationStrategyEntity.setGroupId(GROUP_ID);
    namedOptimizationStrategyEntity.setOptimizationStrategyName(UPDATED_OPTIMIZATION_STRATEGY_NAME);
    namedOptimizationStrategyEntity.setOptimizationStrategyDetails(OPTIMIZATION_STRATEGY_DETAILS);
    namedOptimizationStrategyEntity.setCustomAttributes(
        JsonNodeFactory.instance.objectNode().put("key1", "value1").put("key2", "value2"));
    return namedOptimizationStrategyEntity;
  }

  public NamedOptimizationStrategyDomainDto getUpdatedNamedOptimizationStrategyEntityUI() {
    NamedOptimizationStrategyDomainDto namedOptimizationStrategyEntity =
        new NamedOptimizationStrategyDomainDto();
    namedOptimizationStrategyEntity.setId(OPTIMIZATION_STRATEGY_ID);
    namedOptimizationStrategyEntity.setOrgId(ORG_ID);
    namedOptimizationStrategyEntity.setGroupId(GROUP_ID);
    namedOptimizationStrategyEntity.setOptimizationStrategyName(UPDATED_OPTIMIZATION_STRATEGY_NAME);
    namedOptimizationStrategyEntity.setOptimizationStrategyDetails(
        UPDATED_OPTIMIZATION_STRATEGY_DETAILS);
    return namedOptimizationStrategyEntity;
  }

  public NamedOptimizationStrategyResponse getUpdatedNamedOptimizationStrategyResponse() {
    return NamedOptimizationStrategyResponse.builder()
        .id(OPTIMIZATION_STRATEGY_ID)
        .orgId(ORG_ID)
        .groupId(GROUP_ID)
        .optimizationStrategyName(UPDATED_OPTIMIZATION_STRATEGY_NAME)
        .optimizationStrategyDetails(OPTIMIZATION_STRATEGY_DETAILS)
        .build();
  }

  public OptimizationRuleUIResponse getUpdatedOptimizationStrategyUIResponse() {
    return OptimizationRuleUIResponse.builder()
        .optimizationRuleId(OPTIMIZATION_STRATEGY_ID)
        .orgId(ORG_ID)
        .sourcingAttributesDefinitionId(String.valueOf(SOURCING_ATTRIBUTES_DEFINITION_ID))
        .optimizationRuleName(UPDATED_OPTIMIZATION_STRATEGY_NAME)
        .requiredAttributes(getAttributeDetailsUIResponse())
        .strategy(UPDATED_OPTIMIZATION_STRATEGY_DETAILS)
        .constraints(getAllConstraintUIResponse())
        .build();
  }

  public NodeGroupWithNodesResponse getNodeGroupWithNodesResponse() {
    NodeGroupWithNodesResponse response = new NodeGroupWithNodesResponse();
    response.setNodeGroupName(NODE_GROUP_NAME);
    response.setNodes(getNodeDetails(2));
    return response;
  }

  public CommonServiceException getNodePriorityInvalidNodeException() {
    Map<String, FieldError> errorMap = new HashMap<>();
    errorMap.put("orgId", FieldError.builder().rejectedValue(ORG_ID).build());
    errorMap.put("ID", FieldError.builder().rejectedValue(NODE_ID).build());
    return new CommonServiceException("Invalid node id", HttpStatus.BAD_REQUEST, 0x1771, errorMap);
  }

  private List<NodeDetail> getNodeDetails(int count) {
    List<NodeDetail> nodeDetails = new ArrayList<>();
    for (int i = 0; i < count; i++) {
      NodeDetail nodeDetail = new NodeDetail("node" + i, 100);
      nodeDetails.add(nodeDetail);
    }
    return nodeDetails;
  }

  public List<NodePriorityDomainDto> getNodePriorityEntityList() {
    NodePriorityDomainDto entity = new NodePriorityDomainDto();
    entity.setId(NODE_GROUP_ID);
    entity.setOrgId(ORG_ID);
    entity.setNodeId(NODE_ID);
    entity.setPriority(PRIORITY);
    return List.of(entity);
  }

  public DeleteNodeGroupsRequest getDeleteNodeGroupsRequest() {
    return new DeleteNodeGroupsRequest(List.of(42L, 43L));
  }

  public List<SourcingRuleDetailsDomainDto> getSourcingRuleConfigurations() {

    SourcingRuleDetailsDomainDto entity = new SourcingRuleDetailsDomainDto();
    entity.setSourcingRuleId(SOURCING_RULE_ID);
    entity.setId(23L);
    entity.setSequence(23);
    entity.setNodeGroups("42, 43");

    SourcingRuleDetailsDomainDto entity2 = new SourcingRuleDetailsDomainDto();
    entity2.setSourcingRuleId(SOURCING_RULE_ID);
    entity2.setId(23L);
    entity2.setSequence(23);
    entity2.setNodeGroups("42");
    return List.of(entity, entity2);
  }

  public SourcingRulesConfigurationDomainDto getSourcingRulesConfigurationEntity() {
    SourcingRulesConfigurationDomainDto entity = new SourcingRulesConfigurationDomainDto();
    entity.setOrgId(ORG_ID);
    entity.setId(SOURCING_RULE_ID);
    entity.setSourcingRule(SERVICE_OPTION);
    entity.setSourcingRuleName(SOURCING_RULE_NAME);
    entity.setSourcingAttributesDefinitionId(SOURCING_ATTRIBUTES_DEFINITION_ID);
    return entity;
  }

  public PageParams getPageParams(
      Optional<Integer> pageNo,
      Optional<Integer> pageSize,
      Optional<String> sortBy,
      Optional<String> sortOrder) {
    PageParams pageParams = new PageParams();
    pageParams.setPageNo(pageNo);
    pageParams.setPageSize(pageSize);
    pageParams.setSortBy(sortBy);
    pageParams.setSortOrder(sortOrder);
    return pageParams;
  }

  public PageResponse<NodeGroupDetailWithPriorityResponse>
      getNodeGroupDetailsWithPriorityResponse() {
    PageResponse<NodeGroupDetailWithPriorityResponse> response = new PageResponse<>();
    NodePriorityInfoV1 nodePriorityInfo =
        NodePriorityInfoV1.builder().nodeId("nodeId").sequence(100).build();
    NodePriorityInfoV1 nodePriorityInfo1 =
        NodePriorityInfoV1.builder().nodeId("nodeId1").sequence(50).build();
    List<NodeGroupDetailWithPriorityResponse> nodeGroupDetailWithPriorityResponses =
        new ArrayList<>();
    NodeGroupDetailWithPriorityResponse nodeGroupDetailWithPriorityResponse =
        NodeGroupDetailWithPriorityResponse.builder()
            .nodeGroupId(1l)
            .nodeGroupName("group_name")
            .orgId("NEXTUPLE")
            .numberOfSourcingRules(10l)
            .associatedNodesInfo(List.of(nodePriorityInfo, nodePriorityInfo1))
            .build();
    nodeGroupDetailWithPriorityResponses.add(nodeGroupDetailWithPriorityResponse);
    response.setData(nodeGroupDetailWithPriorityResponses);
    response.setPagination(
        PaginationAttribute.builder()
            .currentPage(1)
            .totalPages(2)
            .totalRecords((long) nodeGroupDetailWithPriorityResponses.size())
            .next("")
            .previous("")
            .sortBy(TestUtil.SORT_BY)
            .sortOrder(TestUtil.DEFAULT_SORT_ORDER)
            .build());
    return response;
  }

  public List<Long> getNodeGroupEntityIds(int count) {
    List<Long> nodeGroupEntityIds = new ArrayList<>();
    for (int i = 1; i < count; i++) {
      nodeGroupEntityIds.add(Long.valueOf(i));
    }
    return nodeGroupEntityIds;
  }

  public static PageParams getPageParamsWithoutPageSize(
      Optional<Integer> pageNo, Optional<String> sortBy, Optional<String> sortOrder) {
    PageParams pageParams = new PageParams();
    pageParams.setPageNo(pageNo);
    pageParams.setSortBy(sortBy);
    pageParams.setSortOrder(sortOrder);
    return pageParams;
  }

  public UpdateSourcingRuleNodeGroupRequest getUpdateSourcingRuleNodeGroupRequest(
      String id, int sequence) {
    return UpdateSourcingRuleNodeGroupRequest.builder().nodeGroupId(id).sequence(sequence).build();
  }

  public UpdateNodeGroupDetailsResponse getUpdateNodeGroupDetailsResponse(
      Long id, int sequence, int nodes) {
    return UpdateNodeGroupDetailsResponse.builder()
        .nodeGroupId(id)
        .sequence(sequence)
        .nodeGroupName("NG_NAME")
        .numberOfNodes(nodes)
        .build();
  }

  public UpdateSourcingRuleRequest getUpdateSourcingRuleRequest() {
    return UpdateSourcingRuleRequest.builder()
        .sourcingRuleId(SOURCING_RULE_ID)
        .sourcingRuleName("ABC")
        .sourcingAttributesDefinitionId(1l)
        .nodeGroups(
            List.of(
                getUpdateSourcingRuleNodeGroupRequest("1", 1),
                getUpdateSourcingRuleNodeGroupRequest("2", 2)))
        .build();
  }

  public UpdateSourcingAttributeResponse getUpdateSourcingAttributeResponse(
      String id, String name, String value) {
    return UpdateSourcingAttributeResponse.builder()
        .attributeName(name)
        .attributeId(id)
        .attributeValue(value)
        .build();
  }

  public UpdateSourcingRuleConfigurationResponse getUpdateSourcingRuleConfigurationResponse() {
    UpdateSourcingRuleRequest updateSourcingRuleRequest = getUpdateSourcingRuleRequest();
    UpdateSourcingRuleConfigurationResponse updateSourcingRuleConfigurationResponse =
        UpdateSourcingRuleConfigurationResponse.builder()
            .sourcingRuleId(updateSourcingRuleRequest.getSourcingRuleId())
            .sourcingRuleName(updateSourcingRuleRequest.getSourcingRuleName())
            .sourcingAttributesDefinitionId(
                updateSourcingRuleRequest.getSourcingAttributesDefinitionId())
            .orgId(ORG_ID)
            .build();

    List<UpdateSourcingAttributeResponse> reqAttributes =
        List.of(
            getUpdateSourcingAttributeResponse("1", "SA1", "/abc/xyz"),
            getUpdateSourcingAttributeResponse("2", "SA2", "/test1/test2"));

    List<UpdateSourcingAttributeResponse> optAttributes =
        List.of(
            getUpdateSourcingAttributeResponse("3", "SA3", "/test2/test3"),
            getUpdateSourcingAttributeResponse("4", "SA4", "/test3/test4"));
    updateSourcingRuleConfigurationResponse.setRequiredAttributes(reqAttributes);
    updateSourcingRuleConfigurationResponse.setOptionalAttributes(optAttributes);
    List<UpdateNodeGroupDetailsResponse> nodeGroupDetailsResponses =
        List.of(
            getUpdateNodeGroupDetailsResponse(1l, 1, 5),
            getUpdateNodeGroupDetailsResponse(2l, 2, 54));
    updateSourcingRuleConfigurationResponse.setNodes(nodeGroupDetailsResponses);

    return updateSourcingRuleConfigurationResponse;
  }

  public SourcingAttributeDefinitionUIResponse getSourcingAttributeDefinitionUIResponse() {
    SourcingAttributeDefinitionUIResponse response = new SourcingAttributeDefinitionUIResponse();
    response.setOrgId(ORG_ID);
    response.setSourcingAttributesDefinitionId("123");
    response.setRequiredAttributes(getAttributeInfo());
    response.setOptionalAttributes(getAttributeInfo());
    return response;
  }

  public List<AttributeInfo> getAttributeInfo() {
    return List.of(new AttributeInfo("ServiceOption", "12", null));
  }

  public AttributeValuesDomainDto getAttributeValuesEntity() {
    AttributeValuesDomainDto attributeValuesEntity = new AttributeValuesDomainDto();
    attributeValuesEntity.setId(ATTRIBUTE_ID);
    attributeValuesEntity.setNameId(ATTRIBUTE_NAME_ID);
    attributeValuesEntity.setValue(ATTRIBUTE_VALUE);

    return attributeValuesEntity;
  }

  public AllSourcingRulesResponse getCreateSourcingRuleRequest() {
    AllSourcingRulesResponse response = new AllSourcingRulesResponse();
    response.setSourcingAttributesDefinitionId(SOURCING_ATTRIBUTES_DEFINITION_ID);
    response.setSourcingRuleName(SOURCING_RULE_NAME);
    response.setRequiredAttributes(getRequiredAttributes());
    response.setOptionalAttributes(getOptionalAttributes());
    response.setNodes(getNodeGroupInfo(2));
    return response;
  }

  private List<NodeGroupInfo> getNodeGroupInfo(int count) {
    List<NodeGroupInfo> nodeGroupInfos = new ArrayList<>();
    for (int i = 0; i < count; i++) {
      NodeGroupInfo info = new NodeGroupInfo();
      info.setNodeGroupId((long) i);
      info.setNodeGroupName("Node Group " + i);
      info.setNumberOfNodes(3);
      info.setSequence(42);
      nodeGroupInfos.add(info);
    }
    return nodeGroupInfos;
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

  public PageResponse<NodeGroupDetailWithPriorityResponse>
      getNodeGroupDetailWithPriorityResponse() {
    List<NodeGroupDetailWithPriorityResponse> nodeGroupDetailWithPriorityResponse =
        List.of(
            NodeGroupDetailWithPriorityResponse.builder()
                .orgId(ORG_ID)
                .nodeGroupId(NODE_GROUP_ID)
                .nodeGroupName(NODE_GROUP_NAME)
                .numberOfSourcingRules(NUMBER_OF_SOURCING_RULES)
                .build(),
            NodeGroupDetailWithPriorityResponse.builder()
                .orgId(ORG_ID)
                .nodeGroupId(2L)
                .nodeGroupName("NG2")
                .numberOfSourcingRules(NUMBER_OF_SOURCING_RULES)
                .build());
    PageResponse<NodeGroupDetailWithPriorityResponse> response = new PageResponse<>();
    response.setData(nodeGroupDetailWithPriorityResponse);
    response.setPagination(
        PaginationAttribute.builder()
            .currentPage(1)
            .next("baseUrl/ui/node-group/ABC/node-priority?pageNo=2&pageSize=1")
            .previous("")
            .sortBy(TestUtil.SORT_BY)
            .sortOrder(TestUtil.SORT_ORDER_DESC)
            .totalRecords(2L)
            .build());

    return response;
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

  public List<NodePriorityInfo> getNodePriorityInfoList() {
    NodePriorityInfo nodePriorityInfo =
        NodePriorityInfo.builder().nodeId(NODE_ID).priority(PRIORITY).build();
    List<NodePriorityInfo> nodePriorityInfoList = new ArrayList<>();
    nodePriorityInfoList.add(nodePriorityInfo);

    return nodePriorityInfoList;
  }

  public OptimizationRuleUIResponse getOptimizationRuleUIResponse(String AttributeValue) {

    OptimizationRuleUIResponse optimizationRuleUIResponse =
        OptimizationRuleUIResponse.builder()
            .orgId(ORG_ID)
            .optimizationRuleId(OPTIMIZATION_STRATEGY_ID)
            .optimizationRuleName(OPTIMIZATION_STRATEGY_NAME)
            .sourcingAttributesDefinitionId(String.valueOf(SOURCING_ATTRIBUTES_DEFINITION_ID))
            .strategy(OPTIMIZATION_STRATEGY_DETAILS)
            .requiredAttributes(
                List.of(
                    new AttributeDetailsUIResponse(ATTRIBUTE_ID, ATTRIBUTE_NAME, AttributeValue)))
            .constraints(List.of())
            .build();

    return optimizationRuleUIResponse;
  }

  public OptimizationRuleUIResponse getUpdatedOptimizationRuleUIResponse(String AttributeValue) {

    OptimizationRuleUIResponse optimizationRuleUIResponse =
        OptimizationRuleUIResponse.builder()
            .orgId(ORG_ID)
            .optimizationRuleId(OPTIMIZATION_STRATEGY_ID)
            .optimizationRuleName(OPTIMIZATION_STRATEGY_NAME)
            .sourcingAttributesDefinitionId(String.valueOf(SOURCING_ATTRIBUTES_DEFINITION_ID))
            .strategy(OPTIMIZATION_STRATEGY_DETAILS)
            .requiredAttributes(
                List.of(
                    new AttributeDetailsUIResponse(ATTRIBUTE_ID, ATTRIBUTE_NAME, AttributeValue)))
            .constraints(List.of())
            .build();

    return optimizationRuleUIResponse;
  }

  public SourcingAttributeResponse getSourcingAttributeResponseWithServiceOption() {
    return SourcingAttributeResponse.builder()
        .id(SOURCING_ATTRIBUTE_ID)
        .orgId(ORG_ID)
        .attributeName(ATTRIBUTE_NAME_2)
        .isDerived(IS_DERIVED_TRUE)
        .customAttributeKey(CUSTOM_ATTRIBUTE_KEY)
        .jsonPath(EMPTY_JSON_PATH)
        .build();
  }

  public List<AllConstraintUIDto> getAllConstraintUIResponse() {
    List<AllConstraintUIDto> allConstraintUIDtoList = new ArrayList<>();

    for (SourcingConstraintEnum constraintEnum : SourcingConstraintEnum.values()) {
      allConstraintUIDtoList.add(
          new AllConstraintUIDto(constraintEnum.getConstraintName(), constraintEnum.name()));
    }
    return allConstraintUIDtoList;
  }

  public PageResponse<OptimizationRuleUIResponse> getOptimizationRulesPageResponse() {
    PageResponse<OptimizationRuleUIResponse> response = new PageResponse<>();
    OptimizationRuleUIResponse optimizationRuleUIResponse =
        getOptimizationRuleUIResponse(ATTRIBUTE_VALUE);

    response.setData(List.of(optimizationRuleUIResponse));
    response.setPagination(
        PaginationAttribute.builder()
            .currentPage(1)
            .totalPages(2)
            .totalRecords((long) 1)
            .next("")
            .previous("")
            .sortBy(TestUtil.SORT_BY)
            .sortOrder(TestUtil.DEFAULT_SORT_ORDER)
            .build());
    return response;
  }

  public SourcingAttributesDefinitionResponse
      getSourcingRuleAttributesDefinitionResponseWithRequiredAttributeInconsistency(
          SourcingAttributesDefinitionStatus status) {

    return SourcingAttributesDefinitionResponse.builder()
        .id(SOURCING_ATTRIBUTES_DEFINITION_ID)
        .orgId(ORG_ID)
        .name(SOURCING_RULE_ATTRIBUTES_DEFINITION_NAME)
        .reqAttributes(REQUIRED_ATTRIBUTES_INCONSISTENT)
        .status(status)
        .build();
  }

  public AttributeValuesDomainDto getAttributeValuesEntity1(
      long id, long attributeId, String attrValue) {
    AttributeValuesDomainDto attributeValuesEntity = new AttributeValuesDomainDto();
    attributeValuesEntity.setId(id);
    attributeValuesEntity.setNameId(attributeId);
    attributeValuesEntity.setValue(attrValue);

    return attributeValuesEntity;
  }

  public List<SourcingRuleDetailsDomainDto> getSourcingRuleDetailsEntityList() {
    List<SourcingRuleDetailsDomainDto> sourcingRuleDetailsEntityList = new ArrayList<>();
    SourcingRuleDetailsDomainDto sourcingRuleDetailsEntity = new SourcingRuleDetailsDomainDto();
    sourcingRuleDetailsEntity.setId(ID);
    sourcingRuleDetailsEntity.setOrgId(ORG_ID);
    sourcingRuleDetailsEntity.setSourcingRuleId(SOURCING_RULE_ID);
    sourcingRuleDetailsEntity.setSequence(2);
    sourcingRuleDetailsEntity.setNodeGroups("2");

    sourcingRuleDetailsEntityList.add(sourcingRuleDetailsEntity);
    sourcingRuleDetailsEntityList.add(getSourcingRuleDetailsEntity());

    return sourcingRuleDetailsEntityList;
  }

  public DetailedOptimizationStrategyResponse getOptimizationStrategyResponse() {
    return DetailedOptimizationStrategyResponse.builder()
        .groupName(GROUP_NAME)
        .requiredAttributes(getRequiredAttributes())
        .build();
  }

  public HolidayCutoffInfo createHolidayCutoffInfo() {
    return HolidayCutoffInfo.builder()
        .orgId(ORG_ID_1)
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
        .customAttributes(CUSTOM_ATTRIBUTES)
        .build();
  }

  public PageResponseForHolidayCutoff getPageResponseForHolidayCutoff() {
    return PageResponseForHolidayCutoff.builder().data(null).pagination(null).build();
  }

  public HolidayCutoffRequest createHolidayCutoffRequest() {
    return HolidayCutoffRequest.builder()
        .orgId(ORG_ID_1)
        .holidayCutoffName(HOLIDAY_CUTOFF_NAME)
        .holidayCutoffDescription(HOLIDAY_CUTOFF_DESCRIPTION)
        .holidayCutoffRule(HOLIDAY_CUTOFF_RULE)
        .sourcingAttributesDefinitionId(1L)
        .status(ACTIVE)
        .startDate(DATE)
        .holidayCutoffDate(DATE)
        .holidayDeliveryDate(DATE)
        .preCutoffDays(PRE_CUTOFF_DAYS)
        .preCutoffDaysType(PRE_CUTOFF_DAYS_TYPE)
        .deliveryCoolDownDays(DELIVERY_COOL_DOWN_DAYS)
        .deliveryCoolDownDaysType(DELIVERY_COOL_DOWN_DAYS_TYPE)
        .customAttributes(CUSTOM_ATTRIBUTES)
        .build();
  }

  public HolidayCutoffUpdateRequest updateHolidayCutoffRequest() {
    return HolidayCutoffUpdateRequest.builder()
        .holidayCutoffDescription(HOLIDAY_CUTOFF_DESCRIPTION)
        .status(ACTIVE)
        .startDate(DATE)
        .holidayCutoffDate(DATE)
        .holidayDeliveryDate(DATE)
        .preCutoffDays(PRE_CUTOFF_DAYS)
        .preCutoffDaysType(PRE_CUTOFF_DAYS_TYPE)
        .deliveryCoolDownDays(DELIVERY_COOL_DOWN_DAYS)
        .deliveryCoolDownDaysType(DELIVERY_COOL_DOWN_DAYS_TYPE)
        .customAttributes(CUSTOM_ATTRIBUTES)
        .build();
  }

  public HolidayCutoffUpdateRequest updateHolidayCutoffRequestWithStatusField() {
    return HolidayCutoffUpdateRequest.builder().status(ACTIVE).build();
  }

  public HolidayCutoffRequest createHolidayCutoffRequestWithMissingFields() {
    return HolidayCutoffRequest.builder()
        .orgId(ORG_ID_1)
        .holidayCutoffName(HOLIDAY_CUTOFF_NAME)
        .holidayCutoffDescription(HOLIDAY_CUTOFF_DESCRIPTION)
        .holidayCutoffRule(HOLIDAY_CUTOFF_RULE)
        .sourcingAttributesDefinitionId(1L)
        .holidayCutoffDate(DATE)
        .holidayDeliveryDate(DATE)
        .build();
  }

  public HolidayCutoffEntity getHolidayCutoffEntity() {
    HolidayCutoffEntity holidayCutoffEntity = new HolidayCutoffEntity();
    holidayCutoffEntity.setOrgId(ORG_ID_1);
    holidayCutoffEntity.setHolidayCutoffName(HOLIDAY_CUTOFF_NAME);
    holidayCutoffEntity.setHolidayCutoffRule(HOLIDAY_CUTOFF_RULE);
    holidayCutoffEntity.setHolidayCutoffDescription(HOLIDAY_CUTOFF_DESCRIPTION);
    holidayCutoffEntity.setSourcingAttributesDefinitionId(SOURCING_ATTRIBUTES_DEFINITION_ID);
    holidayCutoffEntity.setStatus(STATUS);
    holidayCutoffEntity.setStartDate(START_DATE);
    holidayCutoffEntity.setHolidayCutoffDate(HOLIDAY_CUTOFF_DATE);
    holidayCutoffEntity.setHolidayDeliveryDate(HOLIDAY_DELIVERY_DATE);
    holidayCutoffEntity.setPreCutoffDays(PRE_CUTOFF_DAYS);
    holidayCutoffEntity.setDeliveryCoolDownDays(DELIVERY_COOL_DOWN_DAYS);
    holidayCutoffEntity.setPreCutoffDaysType(PRE_CUTOFF_DAYS_TYPE);
    holidayCutoffEntity.setDeliveryCoolDownDaysType(DELIVERY_COOL_DOWN_DAYS_TYPE);
    holidayCutoffEntity.setCustomAttributes(CUSTOM_ATTRIBUTES);

    return holidayCutoffEntity;
  }

  public HolidayCutoffEntity getHolidayCutoffEntity2() {
    HolidayCutoffEntity holidayCutoffEntity = new HolidayCutoffEntity();
    holidayCutoffEntity.setOrgId(ORG_ID_1);
    holidayCutoffEntity.setHolidayCutoffName(HOLIDAY_CUTOFF_NAME2);
    holidayCutoffEntity.setHolidayCutoffRule(HOLIDAY_CUTOFF_RULE);
    holidayCutoffEntity.setHolidayCutoffDescription(HOLIDAY_CUTOFF_DESCRIPTION);
    holidayCutoffEntity.setSourcingAttributesDefinitionId(SOURCING_ATTRIBUTES_DEFINITION_ID);
    holidayCutoffEntity.setStatus(STATUS);
    holidayCutoffEntity.setStartDate(START_DATE);
    holidayCutoffEntity.setHolidayCutoffDate(HOLIDAY_CUTOFF_DATE);
    holidayCutoffEntity.setHolidayDeliveryDate(HOLIDAY_DELIVERY_DATE);
    holidayCutoffEntity.setPreCutoffDays(PRE_CUTOFF_DAYS);
    holidayCutoffEntity.setDeliveryCoolDownDays(DELIVERY_COOL_DOWN_DAYS);
    holidayCutoffEntity.setPreCutoffDaysType(PRE_CUTOFF_DAYS_TYPE);
    holidayCutoffEntity.setDeliveryCoolDownDaysType(DELIVERY_COOL_DOWN_DAYS_TYPE);

    return holidayCutoffEntity;
  }

  public List<HolidayCutoffEntity> getHolidayCutoffEntityList() {
    return List.of(getHolidayCutoffEntity(), getHolidayCutoffEntity2());
  }

  public List<HolidayCutoffEntity> getHolidayCutoffEmptyList() {
    return List.of();
  }

  public HolidayCutoffEntity getHolidayCutoffEntityWithDefaultFields() {
    HolidayCutoffEntity holidayCutoffEntity = new HolidayCutoffEntity();
    holidayCutoffEntity.setOrgId(ORG_ID_1);
    holidayCutoffEntity.setHolidayCutoffName(HOLIDAY_CUTOFF_NAME);
    holidayCutoffEntity.setHolidayCutoffRule(HOLIDAY_CUTOFF_RULE);
    holidayCutoffEntity.setHolidayCutoffDescription(HOLIDAY_CUTOFF_DESCRIPTION);
    holidayCutoffEntity.setStatus(DEFAULT_STATUS);
    holidayCutoffEntity.setStartDate(new Date());
    holidayCutoffEntity.setSourcingAttributesDefinitionId(SOURCING_ATTRIBUTES_DEFINITION_ID);
    holidayCutoffEntity.setHolidayCutoffDate(HOLIDAY_CUTOFF_DATE);
    holidayCutoffEntity.setHolidayDeliveryDate(HOLIDAY_DELIVERY_DATE);
    holidayCutoffEntity.setPreCutoffDays(DEFAULT_DAYS);
    holidayCutoffEntity.setDeliveryCoolDownDays(DEFAULT_DAYS);
    holidayCutoffEntity.setPreCutoffDaysType(DEFAULT_DAYS_TYPE);
    holidayCutoffEntity.setDeliveryCoolDownDaysType(DEFAULT_DAYS_TYPE);
    holidayCutoffEntity.setCustomAttributes(CUSTOM_ATTRIBUTES);

    return holidayCutoffEntity;
  }

  public HolidayCutoffRulesRequest getHolidayCutoffRulesRequest() {
    SourcingAttributeValuesInfo sourcingAttributeValuesInfo =
        SourcingAttributeValuesInfo.builder()
            .requiredAttributesValue(HOLIDAY_CUTOFF_REQUIRED_ATTRIBUTES)
            .build();
    return HolidayCutoffRulesRequest.builder()
        .orgId(ORG_ID)
        .sourcingAttributesDefinitionId(SOURCING_ATTRIBUTES_DEFINITION_ID)
        .sourcingAttributeValuesInfo(sourcingAttributeValuesInfo)
        .build();
  }

  public HolidayCutoffRulesResponse getHolidayCutoffRulesResponse() {
    return HolidayCutoffRulesResponse.builder()
        .holidayCutoffInfo(List.of(createHolidayCutoffInfo()))
        .build();
  }

  public RulesConfigurationsRequest getRulesConfigurationForMLRequest() {
    RulesConfigurationsRequest request = new RulesConfigurationsRequest();
    request.setOrgId("NEXTUPLE");
    request.setRuleName("Rule-1");
    request.setRule("EXPRESS:Store");
    request.setAttributeDefinitionId(1345L);
    request.setModuleName(RulesConfigurationModuleNameEnum.PROCESSING_TIME);
    request.setScope(SourcingAttributesDefinitionScopeEnum.ML_RULE);
    request.setCustomAttributes(JsonNodeFactory.instance.objectNode().put("key1", "value1"));
    return request;
  }

  public RulesConfigurationsRequest getRulesConfigurationForOptimizationRequest() {
    RulesConfigurationsRequest request = new RulesConfigurationsRequest();
    request.setOrgId("NEXTUPLE");
    request.setRuleName("Rule-1");
    request.setRule("EXPRESS:Store");
    request.setAttributeDefinitionId(1345L);
    request.setModuleName(RulesConfigurationModuleNameEnum.PROCESSING_TIME);
    request.setScope(SourcingAttributesDefinitionScopeEnum.OPTIMIZATION);
    return request;
  }

  public RulesConfigurationDomainDto getExpectedRulesConfigurationDomainDto1() {
    RulesConfigurationDomainDto expectedDomainDto = new RulesConfigurationDomainDto();
    expectedDomainDto.setOrgId("NEXTUPLE");
    expectedDomainDto.setRuleName("Rule-1");
    expectedDomainDto.setRule("EXPRESS:Store");
    expectedDomainDto.setAttributeDefinitionId(1345L);
    expectedDomainDto.setModuleName(RulesConfigurationModuleNameEnum.PROCESSING_TIME);
    expectedDomainDto.setScope(SourcingAttributesDefinitionScopeEnum.ML_RULE);
    expectedDomainDto.setCustomAttributes(
        JsonNodeFactory.instance.objectNode().put("key1", "value1"));
    return expectedDomainDto;
  }

  public RulesConfigurationResponse getExpectedRulesConfigurationResponse1() {
    RulesConfigurationResponse expectedResponse = new RulesConfigurationResponse();
    expectedResponse.setOrgId("NEXTUPLE");
    expectedResponse.setRuleName("Rule-1");
    expectedResponse.setRule("EXPRESS:Store");
    expectedResponse.setAttributeDefinitionId(1345L);
    expectedResponse.setModuleName(RulesConfigurationModuleNameEnum.PROCESSING_TIME);
    expectedResponse.setScope(SourcingAttributesDefinitionScopeEnum.ML_RULE);
    expectedResponse.setCustomAttributes(
        JsonNodeFactory.instance.objectNode().put("key1", "value1"));
    return expectedResponse;
  }

  public SourcingAttributesDefinitionDomainDto getSourcingRuleAttributesDefinitionEntityForMLRULE(
      SourcingAttributesDefinitionStatus status) {
    SourcingAttributesDefinitionDomainDto sourcingRuleAttributesDefinitionEntity =
        new SourcingAttributesDefinitionDomainDto();
    sourcingRuleAttributesDefinitionEntity.setId(SOURCING_ATTRIBUTES_DEFINITION_ID);
    sourcingRuleAttributesDefinitionEntity.setOrgId(ORG_ID);
    sourcingRuleAttributesDefinitionEntity.setName(SOURCING_RULE_ATTRIBUTES_DEFINITION_NAME);
    sourcingRuleAttributesDefinitionEntity.setReqAttributes(REQUIRED_ATTRIBUTES);
    sourcingRuleAttributesDefinitionEntity.setOptAttributes(OPTIONAL_ATTRIBUTES);
    sourcingRuleAttributesDefinitionEntity.setStatus(status);
    sourcingRuleAttributesDefinitionEntity.setScope(SourcingAttributesDefinitionScopeEnum.ML_RULE);
    return sourcingRuleAttributesDefinitionEntity;
  }

  public FetchRuleConfigurationRequest getFetchRuleConfigurationRequest() {
    return FetchRuleConfigurationRequest.builder()
        .orgId("NEXTUPLE")
        .attributeDefinitionId(1345L)
        .attributeValuesInfo(
            SourcingAttributeValuesInfo.builder()
                .requiredAttributesValue("EXPRESS")
                .optionalAttributesValue("Store")
                .build())
        .moduleName(RulesConfigurationModuleNameEnum.PROCESSING_TIME)
        .scope(SourcingAttributesDefinitionScopeEnum.ML_RULE)
        .build();
  }

  public SourcingAttributesDefinitionDomainDto getSourcingRuleAttributesDefinitionForGenericRule(
      SourcingAttributesDefinitionStatus status) {
    return SourcingAttributesDefinitionDomainDto.builder()
        .id(1345L)
        .orgId("NEXTUPLE")
        .name(SOURCING_RULE_ATTRIBUTES_DEFINITION_NAME)
        .reqAttributes("1")
        .optAttributes("2")
        .status(status)
        .scope(SourcingAttributesDefinitionScopeEnum.ML_RULE)
        .build();
  }

  public RulesConfigurationDomainDto getRulesConfigurationDomainDto() {
    return RulesConfigurationDomainDto.builder()
        .ruleName(SOURCING_RULE_NAME)
        .orgId("NEXTUPLE")
        .rule("EXPRESS:Store")
        .attributeDefinitionId(1345L)
        .moduleName(RulesConfigurationModuleNameEnum.PROCESSING_TIME)
        .scope(SourcingAttributesDefinitionScopeEnum.ML_RULE)
        .build();
  }

  public RuleConfigurationParam getRuleConfigurationParam() {
    return RuleConfigurationParam.builder()
        .ruleName(SOURCING_RULE_NAME)
        .orgId("NEXTUPLE")
        .rule("EXPRESS:Store")
        .moduleName(RulesConfigurationModuleNameEnum.PROCESSING_TIME)
        .scope(SourcingAttributesDefinitionScopeEnum.ML_RULE)
        .build();
  }

  public List<HolidayCutoffEntity> getMultipleHolidayCutoffEntities(
      List<String> holidayCutoffRules) {
    List<HolidayCutoffEntity> holidayCutoffEntityList = new ArrayList<>();

    for (int i = 0; i < holidayCutoffRules.size(); i++) {
      HolidayCutoffEntity holidayCutoffEntity = new HolidayCutoffEntity();
      holidayCutoffEntity.setOrgId(ORG_ID_1);
      holidayCutoffEntity.setHolidayCutoffName(HOLIDAY_CUTOFF_NAME + i);
      holidayCutoffEntity.setHolidayCutoffRule(holidayCutoffRules.get(i));
      holidayCutoffEntity.setHolidayCutoffDescription(HOLIDAY_CUTOFF_DESCRIPTION);
      holidayCutoffEntity.setSourcingAttributesDefinitionId(SOURCING_ATTRIBUTES_DEFINITION_ID);
      holidayCutoffEntity.setStatus(STATUS);
      holidayCutoffEntity.setStartDate(START_DATE);
      holidayCutoffEntity.setHolidayCutoffDate(HOLIDAY_CUTOFF_DATE);
      holidayCutoffEntity.setHolidayDeliveryDate(HOLIDAY_DELIVERY_DATE);
      holidayCutoffEntity.setPreCutoffDays(PRE_CUTOFF_DAYS);
      holidayCutoffEntity.setDeliveryCoolDownDays(DELIVERY_COOL_DOWN_DAYS);
      holidayCutoffEntity.setPreCutoffDaysType(PRE_CUTOFF_DAYS_TYPE);
      holidayCutoffEntity.setDeliveryCoolDownDaysType(DELIVERY_COOL_DOWN_DAYS_TYPE);
      holidayCutoffEntity.setCustomAttributes(CUSTOM_ATTRIBUTES);

      holidayCutoffEntityList.add(holidayCutoffEntity);
    }

    return holidayCutoffEntityList;
  }

  public FetchGroupDefinitionRequest getFetchGroupDefinitionRequest() {
    return getFetchGroupDefinitionRequest(REQUIRED_ATTRIBUTES_VALUE, OPTIONAL_ATTRIBUTES_VALUE);
  }

  public FetchGroupDefinitionRequest getFetchGroupDefinitionRequest(
      String requiredAttr, String optionalAttr) {
    return FetchGroupDefinitionRequest.builder()
        .orgId(ORG_ID)
        .sourcingAttributeDefinitionId(SOURCING_ATTRIBUTES_DEFINITION_ID)
        .attributeValuesInfo(
            SourcingAttributeValuesInfo.builder()
                .requiredAttributesValue(requiredAttr)
                .optionalAttributesValue(optionalAttr)
                .build())
        .build();
  }

  public SourcingAttributesDefinitionDomainDto getSourcingRuleAttributesDefinitionDomainDto(
      String reqAttr, String optionalAttr) {
    return SourcingAttributesDefinitionDomainDto.builder()
        .id(ID)
        .reqAttributes(reqAttr)
        .optAttributes(optionalAttr)
        .scope(SourcingAttributesDefinitionScopeEnum.OPTIMIZATION)
        .status(SourcingAttributesDefinitionStatus.ACTIVE)
        .name("ID1")
        .build();
  }

  public List<GroupDefinitionDomainDto> getListOfGroupDefinitionRules() {
    List<GroupDefinitionDomainDto> groupDefinitionDomainDtos = new ArrayList<>();
    String reqAttr = "STANDARD:KITCHEN";
    groupDefinitionDomainDtos.add(getGroupDefinitionRule(reqAttr, null));
    groupDefinitionDomainDtos.add(getGroupDefinitionRule(reqAttr, "SHIP:"));
    groupDefinitionDomainDtos.add(getGroupDefinitionRule(reqAttr, ":CART"));
    groupDefinitionDomainDtos.add(getGroupDefinitionRule(reqAttr, "SHIP:CART"));
    return groupDefinitionDomainDtos;
  }

  private GroupDefinitionDomainDto getGroupDefinitionRule(String reqAttr, String optionalAttr) {
    return GroupDefinitionDomainDto.builder()
        .reqAttributesValue(reqAttr)
        .optionalAttributesValue(optionalAttr)
        .build();
  }

  public List<SourcingRulesConfigurationDomainDto> getSourcingRulesList() {
    List<SourcingRulesConfigurationDomainDto> sourcingRulesConfigurationDomainDtoList =
        new ArrayList<>();
    SourcingRulesConfigurationDomainDto rule1 =
        SourcingRulesConfigurationDomainDto.builder()
            .id(1L)
            .orgId(ORG_ID)
            .sourcingRuleName("Rule1")
            .sourcingRule("STANDARD:KITCHEN::")
            .sourcingAttributesDefinitionId(SOURCING_ATTRIBUTES_DEFINITION_ID)
            .build();
    SourcingRulesConfigurationDomainDto rule2 =
        SourcingRulesConfigurationDomainDto.builder()
            .id(2L)
            .orgId(ORG_ID)
            .sourcingRuleName("Rule2")
            .sourcingRule("STANDARD:KITCHEN:XYZ:")
            .sourcingAttributesDefinitionId(SOURCING_ATTRIBUTES_DEFINITION_ID)
            .build();
    SourcingRulesConfigurationDomainDto rule3 =
        SourcingRulesConfigurationDomainDto.builder()
            .id(3L)
            .orgId(ORG_ID)
            .sourcingRuleName("Rule3")
            .sourcingRule("STANDARD:KITCHEN:ABC:")
            .sourcingAttributesDefinitionId(SOURCING_ATTRIBUTES_DEFINITION_ID)
            .build();
    SourcingRulesConfigurationDomainDto rule4 =
        SourcingRulesConfigurationDomainDto.builder()
            .id(4L)
            .orgId(ORG_ID)
            .sourcingRuleName("Rule4")
            .sourcingRule("STANDARD:KITCHEN::SHIP")
            .sourcingAttributesDefinitionId(SOURCING_ATTRIBUTES_DEFINITION_ID)
            .build();
    SourcingRulesConfigurationDomainDto rule5 =
        SourcingRulesConfigurationDomainDto.builder()
            .id(5L)
            .orgId(ORG_ID)
            .sourcingRuleName("Rule5")
            .sourcingRule("STANDARD:KITCHEN:XYZ:SHIP")
            .sourcingAttributesDefinitionId(SOURCING_ATTRIBUTES_DEFINITION_ID)
            .build();
    SourcingRulesConfigurationDomainDto rule6 =
        SourcingRulesConfigurationDomainDto.builder()
            .id(6L)
            .orgId(ORG_ID)
            .sourcingRuleName("Rule6")
            .sourcingRule("STANDARD:KITCHEN:ABC:SHIP")
            .sourcingAttributesDefinitionId(SOURCING_ATTRIBUTES_DEFINITION_ID)
            .build();
    SourcingRulesConfigurationDomainDto rule7 =
        SourcingRulesConfigurationDomainDto.builder()
            .id(7L)
            .orgId(ORG_ID)
            .sourcingRuleName("Rule7")
            .sourcingRule("STANDARD:KITCHEN:XYZ:PICK")
            .sourcingAttributesDefinitionId(SOURCING_ATTRIBUTES_DEFINITION_ID)
            .build();

    sourcingRulesConfigurationDomainDtoList.add(rule1);
    sourcingRulesConfigurationDomainDtoList.add(rule2);
    sourcingRulesConfigurationDomainDtoList.add(rule3);
    sourcingRulesConfigurationDomainDtoList.add(rule4);
    sourcingRulesConfigurationDomainDtoList.add(rule5);
    sourcingRulesConfigurationDomainDtoList.add(rule6);
    sourcingRulesConfigurationDomainDtoList.add(rule7);
    return sourcingRulesConfigurationDomainDtoList;
  }
}
