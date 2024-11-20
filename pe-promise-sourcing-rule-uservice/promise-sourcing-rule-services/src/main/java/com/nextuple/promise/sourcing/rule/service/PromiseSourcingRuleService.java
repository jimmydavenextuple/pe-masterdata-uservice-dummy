/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.promise.sourcing.rule.service;

import com.nextuple.common.enums.ApplicationLayer;
import com.nextuple.common.enums.ExceptionCodeMapping;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.common.response.error.FieldError;
import com.nextuple.node.domain.feign.NodeFeign;
import com.nextuple.node.domain.outbound.NodeResponse;
import com.nextuple.postal.code.timezone.api.domain.feign.PostalCodeFeign;
import com.nextuple.postal.code.timezone.api.domain.outbound.PostalCodeResponse;
import com.nextuple.postgres.config.ReaderDS;
import com.nextuple.promise.sourcing.rule.api.domain.dto.PromiseSourcingRuleDto;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.CreatePromiseSourcingRuleRequest;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.FetchPromiseSourcingRuleRequest;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.UpdatePromiseSourcingRuleRequest;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.FetchPromiseSourcingRuleResponse;
import com.nextuple.promise.sourcing.rule.api.domain.pojo.ServiceOptionInfo;
import com.nextuple.promise.sourcing.rule.domain.mapper.PromiseSourcingRuleMapper;
import com.nextuple.promise.sourcing.rule.persistence.domain.PromiseSourcingRuleDomainDto;
import com.nextuple.promise.sourcing.rule.persistence.service.PromiseSourcingRulePersistenceService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class PromiseSourcingRuleService {

  private static final Logger logger = LoggerFactory.getLogger(PromiseSourcingRuleService.class);

  private static final String ORG_ID = "orgId";
  private static final String NODE_ID = "nodeId";
  private static final String SERVICE_OPTION = "serviceOption";
  private static final String INVALID_SERVICE_OPTION = "Invalid ServiceOption";
  private static final String INACTIVE_NODE = "nodeId is not active";
  private static final String INVALID_DEST_GEOZONE = "DestinationGeoZone is not valid";
  private static final PromiseSourcingRuleMapper INSTANCE =
      Mappers.getMapper(PromiseSourcingRuleMapper.class);
  private final PromiseSourcingRulePersistenceService promiseSourcingRulePersistenceService;
  @Autowired private final PostalCodeFeign postalCodeFeign;
  @Autowired private final NodeFeign nodeFeign;

  @Value("#{'${promise.service.options}'.split('\\s*,\\s*')}")
  public Set<String> serviceOptions;

  /**
   * Convert PromiseSourcingRule entity to PromiseSourcingRuleDto with all required processing
   *
   * @param promiseSourcingRule PromiseSourcingRule entity
   * @return PromiseSourcingRuleDto dto
   */
  private PromiseSourcingRuleDto preparePromiseSourcingRuleDto(
      PromiseSourcingRuleDomainDto promiseSourcingRule) {
    return INSTANCE.convertToPromiseSourcingRuleDto(promiseSourcingRule);
  }

  /**
   * Get Service Option Info
   *
   * @param promiseSourcingRule Promise Sourcing Rule
   * @return ServiceOptionInfo object
   */
  private ServiceOptionInfo getServiceOptionInfo(PromiseSourcingRuleDomainDto promiseSourcingRule) {
    var serviceOptionInfo = new ServiceOptionInfo();
    serviceOptionInfo.setSourceNodes(promiseSourcingRule.getSourceNodes());
    serviceOptionInfo.setPriority(promiseSourcingRule.getPriority());
    return serviceOptionInfo;
  }

  /**
   * Fetch Sourcing rule
   *
   * @param baseRequest Fetch Promise Sourcing Rule Request
   * @return FetchPromiseSourcingRuleResponse object
   * @throws PromiseEngineException
   */
  public FetchPromiseSourcingRuleResponse fetchSourcingRule(
      FetchPromiseSourcingRuleRequest baseRequest) throws PromiseEngineException {
    logger.debug("-- inside fetchSourcingRule service --");
    List<PromiseSourcingRuleDomainDto> promiseSourcingRuleList =
        promiseSourcingRulePersistenceService.fetchSourcingRule(baseRequest);
    if (promiseSourcingRuleList.isEmpty()) {
      throw new PromiseEngineException(
          ApplicationLayer.SERVICE_LAYER,
          ExceptionCodeMapping.SERVICE_FIND_FAILED,
          "Promise Sourcing Rules not found!");
    }

    // group by service options
    Map<String, List<ServiceOptionInfo>> serviceOptionPromiseRules =
        promiseSourcingRuleList.stream()
            .collect(
                Collectors.groupingBy(
                    PromiseSourcingRuleDomainDto::getServiceOption,
                    Collectors.mapping(this::getServiceOptionInfo, Collectors.toList())));

    // set an empty list for the service options for which promise rule is not found
    baseRequest
        .getServiceOptions()
        .forEach(
            x -> {
              if (!serviceOptionPromiseRules.containsKey(x)) {
                serviceOptionPromiseRules.put(x, new ArrayList<>());
              }
            });
    return FetchPromiseSourcingRuleResponse.builder()
        .serviceOptionSourcingRules(serviceOptionPromiseRules)
        .build();
  }

  /**
   * Creates new PromiseSourcingRule entity
   *
   * @param baseRequest for Creating Promise Sourcing Rule
   * @return Created Promise Sourcing Rule dto
   * @throws PromiseEngineException
   */
  public PromiseSourcingRuleDto createPromiseSourcingRule(
      CreatePromiseSourcingRuleRequest baseRequest)
      throws PromiseEngineException, CommonServiceException {
    logger.debug("-- inside createPromiseSourcingRule service --");
    validateSourceNode(baseRequest.getSourceNodes());
    if (!StringUtils.hasLength(baseRequest.getAllocationRuleId())) {
      baseRequest.setAllocationRuleId("DEFAULT");
    }

    String destinationGeoZone = baseRequest.getDestinationGeoZone();
    String orgId = baseRequest.getOrgId();
    String serviceOption = baseRequest.getServiceOption();
    String allocationRuleId = baseRequest.getAllocationRuleId();
    Set<String> sourceNodes = baseRequest.getSourceNodes();

    validateNodeIdAndDestinationGeoZoneAndServiceOption(
        orgId, serviceOption, destinationGeoZone, sourceNodes);
    validateSourcingRules(destinationGeoZone, orgId, serviceOption, allocationRuleId, sourceNodes);

    var promiseSourcingRule =
        INSTANCE.convertFromCreatePromiseSourcingRuleRequestToEntity(baseRequest);
    return preparePromiseSourcingRuleDto(
        promiseSourcingRulePersistenceService.savePromiseSourcingRule(promiseSourcingRule));
  }

  public void validateSourceNode(Set<String> sourceNodes) throws CommonServiceException {
    if (sourceNodes.stream().anyMatch(String::isBlank)) {
      throw new CommonServiceException(
          "sourceNodes cannot contain null or an empty string",
          HttpStatus.BAD_REQUEST,
          0x1771,
          null);
    }
  }

  /**
   * Validate if for given parameters , rules exists or not
   *
   * @param destinationGeoZone Destination GeoZone
   * @param orgId Org Id
   * @param serviceOption Service option
   * @param allocationRuleId Allocation Rule Id
   * @param sourceNodes Source Nodes
   * @throws PromiseEngineException
   */
  private void validateSourcingRules(
      String destinationGeoZone,
      String orgId,
      String serviceOption,
      String allocationRuleId,
      Set<String> sourceNodes)
      throws PromiseEngineException {
    List<PromiseSourcingRuleDomainDto> promiseSourcingRules =
        promiseSourcingRulePersistenceService.fetchSourcingRule(
            serviceOption, orgId, allocationRuleId, destinationGeoZone);
    Set<String> definedSourceNodes =
        promiseSourcingRules.stream()
            .parallel()
            .map(PromiseSourcingRuleDomainDto::getSourceNodes)
            .flatMap(Set::stream)
            .collect(Collectors.toSet());

    Set<String> nodesWithAlreadyExistingRules =
        definedSourceNodes.stream().filter(sourceNodes::contains).collect(Collectors.toSet());

    if (!CollectionUtils.isEmpty(nodesWithAlreadyExistingRules)) {
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.SERVICE_INVALID_INPUT,
          "The nodes " + nodesWithAlreadyExistingRules + " are already part of different rules! ");
    }
  }

  private void validateNodeIdAndDestinationGeoZoneAndServiceOption(
      String orgId, String serviceOption, String destinationGeoZone, Set<String> sourceNodes)
      throws CommonServiceException {
    validateNodeId(orgId, serviceOption, sourceNodes);
    validateDestinationGeozone(orgId, serviceOption, destinationGeoZone, sourceNodes);
    if (!serviceOptions.contains(serviceOption)) {
      commonServiceExceptionMethod(
          INVALID_SERVICE_OPTION, sourceNodes, orgId, serviceOption, 0x1772);
    }
  }

  private void validateDestinationGeozone(
      String orgId, String serviceOption, String destinationGeoZone, Set<String> sourceNodes)
      throws CommonServiceException {
    try {
      BaseResponse<List<PostalCodeResponse>> postalCodeResponseList =
          postalCodeFeign.getByPostalCodePrefix(orgId, destinationGeoZone);
      if (CollectionUtils.isEmpty(postalCodeResponseList.getPayload())) {
        commonServiceExceptionMethod(
            INVALID_DEST_GEOZONE, sourceNodes, orgId, serviceOption, 0x1774);
      }
    } catch (RuntimeException e) {
      commonServiceExceptionMethod(INVALID_DEST_GEOZONE, sourceNodes, orgId, serviceOption, 0x1775);
    }
  }

  private void validateNodeId(String orgId, String serviceOption, Set<String> sourceNodes)
      throws CommonServiceException {
    try {
      for (String node : sourceNodes) {
        BaseResponse<NodeResponse> baseResponse = nodeFeign.getNodeDetails(node, orgId);
        if (Boolean.FALSE.equals(baseResponse.getPayload().getIsActive())) {
          commonServiceExceptionMethod(INACTIVE_NODE, sourceNodes, orgId, serviceOption, 0x1771);
        }
      }
    } catch (RuntimeException e) {
      commonServiceExceptionMethod(
          "Node not found with given details", sourceNodes, orgId, serviceOption, 0x1775);
    }
  }

  /**
   * Get Promise Sourcing Rule
   *
   * @param orgId organisation ID using which service will look for Promise Sourcing Rule
   * @param serviceOption Service Option using which service will look for Promise Sourcing Rule
   * @param destinationGeoZone Destination Geo Zone using which service will look for Promise
   *     Sourcing Rule
   * @param allocationRuleId Allocation Rule ID using which service will look for Promise Sourcing
   *     Rule
   * @param priority Priority using which service will look for Promise Sourcing Rule
   * @return Fetched Promise Sourcing Rule dto
   * @throws PromiseEngineException
   */
  @ReaderDS
  public PromiseSourcingRuleDto getPromiseSourcingRule(
      String orgId,
      String serviceOption,
      String destinationGeoZone,
      String allocationRuleId,
      int priority)
      throws PromiseEngineException {
    logger.debug("-- inside getPromiseSourcingRule service --");
    Optional<PromiseSourcingRuleDomainDto> promiseSourcingRule =
        Optional.ofNullable(
            promiseSourcingRulePersistenceService.getPromiseSourcingRule(
                orgId, serviceOption, destinationGeoZone, allocationRuleId, priority));

    if (promiseSourcingRule.isEmpty()) {
      logger.error("-- Promise Sourcing Rule not found --");
      throw new PromiseEngineException(
          ApplicationLayer.SERVICE_LAYER,
          ExceptionCodeMapping.SERVICE_FIND_FAILED,
          "Promise Sourcing Rule not found!");
    }

    return preparePromiseSourcingRuleDto(promiseSourcingRule.get());
  }

  /**
   * Get Promise Sourcing Rules by orgId
   *
   * @param orgId using which service will look for Promise Sourcing Rule
   * @return Fetched Promise Sourcing Rules dto
   * @throws PromiseEngineException
   */
  @ReaderDS
  public List<PromiseSourcingRuleDto> getPromiseSourcingRulesByOrgId(String orgId)
      throws PromiseEngineException {
    logger.debug("-- inside getPromiseSourcingRulesByOrgId service --");
    List<PromiseSourcingRuleDomainDto> promiseSourcingRules =
        promiseSourcingRulePersistenceService.getPromiseSourcingRulesByOrgId(orgId);

    return promiseSourcingRules.stream()
        .map(this::preparePromiseSourcingRuleDto)
        .collect(Collectors.toList());
  }

  /**
   * Get Promise Sourcing Rules by orgId
   *
   * @param priority using which service will look for Promise Sourcing Rule
   * @return Fetched Promise Sourcing Rules dto
   * @throws PromiseEngineException
   */
  @ReaderDS
  public List<PromiseSourcingRuleDto> getPromiseSourcingRulesByPriority(int priority)
      throws PromiseEngineException {
    logger.debug("-- inside getPromiseSourcingRulesByPriority service --");
    List<PromiseSourcingRuleDomainDto> promiseSourcingRules =
        promiseSourcingRulePersistenceService.getPromiseSourcingRulesByPriority(priority);

    return promiseSourcingRules.stream()
        .map(this::preparePromiseSourcingRuleDto)
        .collect(Collectors.toList());
  }

  /**
   * Update Promise Sourcing Rule
   *
   * @param orgId organisation ID using which service will look for Promise Sourcing Rule
   * @param serviceOption Service Option using which service will look for Promise Sourcing Rule
   * @param destinationGeoZone Destination Geo Zone using which service will look for Promise
   *     Sourcing Rule
   * @param allocationRuleId Allocation Rule ID using which service will look for Promise Sourcing
   *     Rule
   * @param priority Priority using which service will look for Promise Sourcing Rule
   * @param baseRequest for Updating Promise Sourcing Rule
   * @return Updated Promise Sourcing Rule dto
   * @throws PromiseEngineException
   */
  public PromiseSourcingRuleDto updatePromiseSourcingRule(
      String orgId,
      String serviceOption,
      String destinationGeoZone,
      String allocationRuleId,
      int priority,
      UpdatePromiseSourcingRuleRequest baseRequest)
      throws PromiseEngineException, CommonServiceException {
    logger.debug("-- inside updatePromiseSourcingRule service --");

    validateNodeIdAndDestinationGeoZoneAndServiceOption(
        orgId, serviceOption, destinationGeoZone, baseRequest.getSourceNodes());
    var promiseSourcingRuleFromDB =
        INSTANCE.convertToPromiseSourcingRuleEntity(
            getPromiseSourcingRule(
                orgId, serviceOption, destinationGeoZone, allocationRuleId, priority));
    logger.info(
        "Response before updation of promise sourcing rule :{}",
        INSTANCE.convertToPromiseSourcingRuleDto(promiseSourcingRuleFromDB));
    INSTANCE.insertValuesFromUpdatePromiseSourcingRuleRequestToEntity(
        baseRequest, promiseSourcingRuleFromDB);

    return preparePromiseSourcingRuleDto(
        promiseSourcingRulePersistenceService.savePromiseSourcingRule(promiseSourcingRuleFromDB));
  }

  /**
   * Delete Promise source rule
   *
   * @param orgId organisation ID using which service will look for Promise Sourcing Rule
   * @param serviceOption Service Option using which service will look for Promise Sourcing Rule
   * @param destinationGeoZone Destination Geo Zone using which service will look for Promise
   *     Sourcing Rule
   * @param allocationRuleId Allocation Rule ID using which service will look for Promise Sourcing
   *     Rule
   * @param priority Priority using which service will look for Promise Sourcing Rule
   * @return Deleted Promise Sourcing Rule dto
   * @throws PromiseEngineException
   */
  public PromiseSourcingRuleDto deletePromiseSourcingRule(
      String orgId,
      String serviceOption,
      String destinationGeoZone,
      String allocationRuleId,
      int priority)
      throws PromiseEngineException {
    logger.debug("-- inside deletePromiseSourcingRule service --");
    var promiseSourcingRuleFromDB =
        INSTANCE.convertToPromiseSourcingRuleEntity(
            getPromiseSourcingRule(
                orgId, serviceOption, destinationGeoZone, allocationRuleId, priority));
    logger.info(
        "Response before deletion of promise sourcing rule :{}",
        INSTANCE.convertToPromiseSourcingRuleDto(promiseSourcingRuleFromDB));
    return preparePromiseSourcingRuleDto(
        promiseSourcingRulePersistenceService.deletePromiseSourcingRule(promiseSourcingRuleFromDB));
  }

  public void commonServiceExceptionMethod(
      String errorMessage,
      Set<String> nodeId,
      String orgId,
      String serviceOption,
      Integer errorCode)
      throws CommonServiceException {
    Map<String, FieldError> errorMap = new HashMap<>();
    errorMap.put(NODE_ID, FieldError.builder().rejectedValue(nodeId).build());
    errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
    errorMap.put(SERVICE_OPTION, FieldError.builder().rejectedValue(serviceOption).build());
    throw new CommonServiceException(errorMessage, HttpStatus.BAD_REQUEST, errorCode, errorMap);
  }
}
