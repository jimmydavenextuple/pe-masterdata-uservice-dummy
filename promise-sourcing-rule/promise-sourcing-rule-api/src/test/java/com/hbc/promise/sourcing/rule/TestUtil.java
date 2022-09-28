package com.hbc.promise.sourcing.rule;

import static com.hbc.promise.sourcing.rule.utils.PromiseSourcingRuleConstants.ALLOCATION_RULE_ID;
import static com.hbc.promise.sourcing.rule.utils.PromiseSourcingRuleConstants.DESTINATION_GEO_ZONE;
import static com.hbc.promise.sourcing.rule.utils.PromiseSourcingRuleConstants.EXPRESS;
import static com.hbc.promise.sourcing.rule.utils.PromiseSourcingRuleConstants.NEXTDAY;
import static com.hbc.promise.sourcing.rule.utils.PromiseSourcingRuleConstants.ORG_ID;
import static com.hbc.promise.sourcing.rule.utils.PromiseSourcingRuleConstants.PRIORITY;
import static com.hbc.promise.sourcing.rule.utils.PromiseSourcingRuleConstants.SDND;
import static com.hbc.promise.sourcing.rule.utils.PromiseSourcingRuleConstants.SERVICE_OPTION;
import static com.hbc.promise.sourcing.rule.utils.PromiseSourcingRuleConstants.STANDARD;

import com.hbc.common.response.BaseResponse;
import com.hbc.node.domain.outbound.NodeResponse;
import com.hbc.postal.code.timezone.api.domain.dto.PostalCodeTimezoneDto;
import com.hbc.promise.sourcing.rule.api.domain.dto.PromiseSourcingRuleDto;
import com.hbc.promise.sourcing.rule.api.domain.inbound.CreatePromiseSourcingRuleRequest;
import com.hbc.promise.sourcing.rule.api.domain.inbound.FetchPromiseSourcingRuleRequest;
import com.hbc.promise.sourcing.rule.api.domain.inbound.UpdatePromiseSourcingRuleRequest;
import com.hbc.promise.sourcing.rule.api.domain.outbound.FetchPromiseSourcingRuleResponse;
import com.hbc.promise.sourcing.rule.api.domain.pojo.ServiceOptionInfo;
import com.hbc.promise.sourcing.rule.domain.entity.PromiseSourcingRule;
import com.hbc.promise.sourcing.rule.domain.mapper.PromiseSourcingRuleMapper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.mapstruct.factory.Mappers;

public class TestUtil {
  public static final String ORG_ID = "ABC";
  private static final String NODE_ID = "Node-1";
  public static final String STREET = "street-1";
  public static final String CITY = "city-1";
  public static final String PROVINCE = "province-1";
  public static final String POSTAL_CODE = "33666";
  public static final String COUNTRY = "country-1";
  public static final String LATITUDE = "43.769912";
  public static final String LONGITUDE = "-79.296678";
  public static final String TIME_ZONE = "America/Toronto";
  public static Boolean SHIP_TO_TIME = Boolean.TRUE;
  public static Boolean BOPIS_ELIGIBLE = Boolean.TRUE;
  public static Boolean SDND_ELIGIBLE = Boolean.TRUE;
  public static Boolean EXPRESS_ELIGIBLE = Boolean.TRUE;
  public static String NODE_TYPE = "MFC";
  public static Boolean IS_ACTIVE = Boolean.TRUE;
  public static final String Node1 = "Node-1";
  private static final PromiseSourcingRuleMapper INSTANCE_PROMISE =
      Mappers.getMapper(PromiseSourcingRuleMapper.class);

  public PromiseSourcingRule getPromiseSourcingRule() {
    PromiseSourcingRule promiseSourcingRule = new PromiseSourcingRule();
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

  public List<PromiseSourcingRule> getPromiseSourcingRuleList() {
    List<PromiseSourcingRule> promiseSourcingRuleList = new ArrayList<>();

    PromiseSourcingRule promiseSourcingRule1 = new PromiseSourcingRule();
    promiseSourcingRule1.setPriority(PRIORITY);
    promiseSourcingRule1.setOrgId(ORG_ID);
    promiseSourcingRule1.setSourceNodes(Collections.singleton(Node1));
    promiseSourcingRule1.setDestinationGeoZone(DESTINATION_GEO_ZONE);
    promiseSourcingRule1.setServiceOption(SDND);
    promiseSourcingRule1.setAllocationRuleId(ALLOCATION_RULE_ID);

    PromiseSourcingRule promiseSourcingRule2 = new PromiseSourcingRule();
    promiseSourcingRule2.setPriority(PRIORITY);
    promiseSourcingRule2.setOrgId(ORG_ID);
    promiseSourcingRule2.setSourceNodes(Collections.singleton("Node-2"));
    promiseSourcingRule2.setDestinationGeoZone(DESTINATION_GEO_ZONE);
    promiseSourcingRule2.setServiceOption(STANDARD);
    promiseSourcingRule2.setAllocationRuleId(ALLOCATION_RULE_ID);

    PromiseSourcingRule promiseSourcingRule3 = new PromiseSourcingRule();
    promiseSourcingRule3.setPriority(PRIORITY);
    promiseSourcingRule3.setOrgId(ORG_ID);
    promiseSourcingRule3.setSourceNodes(Collections.singleton("Node-3"));
    promiseSourcingRule3.setDestinationGeoZone(DESTINATION_GEO_ZONE);
    promiseSourcingRule3.setServiceOption(EXPRESS);
    promiseSourcingRule3.setAllocationRuleId(ALLOCATION_RULE_ID);

    PromiseSourcingRule promiseSourcingRule4 = new PromiseSourcingRule();
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
        .postalCode(POSTAL_CODE)
        .province(PROVINCE)
        .shipToHome(SHIP_TO_TIME)
        .timezone(TIME_ZONE)
        .build();
  }

  public BaseResponse<NodeResponse> getBaseResponseOfNode() {
    return BaseResponse.builder()
        .message("Calendar details added successfully")
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
        .postalCode(POSTAL_CODE)
        .province(PROVINCE)
        .shipToHome(SHIP_TO_TIME)
        .timezone(TIME_ZONE)
        .build();
  }

  public BaseResponse<NodeResponse> getBaseResponseOfNode2() {
    return BaseResponse.builder()
        .message("Calendar details added successfully")
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
        .postalCodePrefix("IST")
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
}
