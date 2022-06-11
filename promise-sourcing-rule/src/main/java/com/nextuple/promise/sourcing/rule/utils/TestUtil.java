package com.nextuple.promise.sourcing.rule.utils;

import static com.nextuple.promise.sourcing.rule.utils.PromiseSourcingRuleConstants.ALLOCATION_RULE_ID;
import static com.nextuple.promise.sourcing.rule.utils.PromiseSourcingRuleConstants.DESTINATION_GEO_ZONE;
import static com.nextuple.promise.sourcing.rule.utils.PromiseSourcingRuleConstants.EXPRESS;
import static com.nextuple.promise.sourcing.rule.utils.PromiseSourcingRuleConstants.ORG_ID;
import static com.nextuple.promise.sourcing.rule.utils.PromiseSourcingRuleConstants.PRIORITY;
import static com.nextuple.promise.sourcing.rule.utils.PromiseSourcingRuleConstants.SDND;
import static com.nextuple.promise.sourcing.rule.utils.PromiseSourcingRuleConstants.SERVICE_OPTION;
import static com.nextuple.promise.sourcing.rule.utils.PromiseSourcingRuleConstants.STANDARD;

import com.nextuple.promise.sourcing.rule.domain.dto.PromiseSourcingRuleDto;
import com.nextuple.promise.sourcing.rule.domain.entity.PromiseSourcingRule;
import com.nextuple.promise.sourcing.rule.domain.inbound.CreatePromiseSourcingRuleRequest;
import com.nextuple.promise.sourcing.rule.domain.inbound.FetchPromiseSourcingRuleRequest;
import com.nextuple.promise.sourcing.rule.domain.inbound.UpdatePromiseSourcingRuleRequest;
import com.nextuple.promise.sourcing.rule.domain.mapper.PromiseSourcingRuleMapper;
import com.nextuple.promise.sourcing.rule.domain.outbound.FetchPromiseSourcingRuleResponse;
import com.nextuple.promise.sourcing.rule.domain.pojo.ServiceOptionInfo;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.mapstruct.factory.Mappers;

public class TestUtil {
  private final String Node1 = "Node-1";
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
    promiseSourcingRule1.setPriority(PRIORITY);
    promiseSourcingRule1.setOrgId(ORG_ID);
    promiseSourcingRule2.setSourceNodes(Collections.singleton("Node-2"));
    promiseSourcingRule1.setDestinationGeoZone(DESTINATION_GEO_ZONE);
    promiseSourcingRule2.setServiceOption(STANDARD);
    promiseSourcingRule1.setAllocationRuleId(ALLOCATION_RULE_ID);

    PromiseSourcingRule promiseSourcingRule3 = new PromiseSourcingRule();
    promiseSourcingRule1.setPriority(PRIORITY);
    promiseSourcingRule1.setOrgId(ORG_ID);
    promiseSourcingRule3.setSourceNodes(Collections.singleton("Node-3"));
    promiseSourcingRule1.setDestinationGeoZone(DESTINATION_GEO_ZONE);
    promiseSourcingRule3.setServiceOption(EXPRESS);
    promiseSourcingRule1.setAllocationRuleId(ALLOCATION_RULE_ID);

    Collections.addAll(
        promiseSourcingRuleList, promiseSourcingRule1, promiseSourcingRule2, promiseSourcingRule3);
    return promiseSourcingRuleList;
  }

  public FetchPromiseSourcingRuleResponse getFetchPromiseSourcingRuleResponse() {
    return FetchPromiseSourcingRuleResponse.builder()
        .sdnd(
            Collections.singletonList(
                ServiceOptionInfo.builder()
                    .priority(PRIORITY)
                    .sourceNodes(Collections.singleton(Node1))
                    .build()))
        .build();
  }

  public FetchPromiseSourcingRuleRequest getFetchPromiseSourcingRuleRequest() {
    return FetchPromiseSourcingRuleRequest.builder()
        .orgId(ORG_ID)
        .allocationRuleId(ALLOCATION_RULE_ID)
        .destinationGeoZone(DESTINATION_GEO_ZONE)
        .serviceOptions(List.of(SDND, STANDARD, EXPRESS))
        .build();
  }

  public UpdatePromiseSourcingRuleRequest getUpdatePromiseSourcingRuleRequest() {
    return UpdatePromiseSourcingRuleRequest.builder()
        .sourceNodes(Collections.singleton(Node1))
        .destinationGeoZone(DESTINATION_GEO_ZONE)
        .priority(PRIORITY)
        .serviceOption(SERVICE_OPTION)
        .allocationRuleId(ALLOCATION_RULE_ID)
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
}
