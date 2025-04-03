/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.node.service;

import static com.nextuple.common.constants.CommonConstants.DEFAULT_SORT_ORDER;
import static com.nextuple.common.constants.CommonConstants.DESC_SORT_ORDER;
import static com.nextuple.common.constants.CommonConstants.NODE_DEFAULT_SORT_BY;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.pojo.PageParams;
import com.nextuple.common.pojo.PageProperties;
import com.nextuple.common.response.error.FieldError;
import com.nextuple.node.config.NodeTenantBasedDBConfig;
import com.nextuple.node.domain.dto.NodeCacheKeyDto;
import com.nextuple.node.domain.dto.NodeDto;
import com.nextuple.node.domain.inbound.NodeRequest;
import com.nextuple.node.domain.inbound.NodeUpdationRequest;
import com.nextuple.node.domain.mapper.NodeMapper;
import com.nextuple.node.domain.outbound.NodeResponse;
import com.nextuple.node.domain.outbound.NodeTypesResponse;
import com.nextuple.node.persistence.domain.NodeDomainDto;
import com.nextuple.node.persistence.exception.NodeDomainException;
import com.nextuple.node.persistence.service.NodePersistenceService;
import com.nextuple.postgres.config.ReaderDS;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.joda.time.DateTimeZone;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

@RequiredArgsConstructor
@Service
public class NodeService {

  private static final Logger logger = LoggerFactory.getLogger(NodeService.class);

  private final NodePersistenceService nodePersistenceService;
  private final PageProperties pageProperties;
  private static final String ORG_ID = "orgId";
  private static final String NODE_ID = "nodeId";
  private static final String NODE_TYPE = "nodeType";
  private static final String SORT_ORDER = "sortOrder";
  private static final String START_WORKING_TIME = "startWorkingTime";
  private static final String LAST_WORKING_TIME = "lastWorkingTime";
  private static final String TIME_FORMAT = "([01]?[\\d]|2[0-3]):[0-5][\\d]";

  private final NodeTenantBasedDBConfig nodeTenantBasedDBConfig;

  public static final NodeMapper INSTANCE = Mappers.getMapper(NodeMapper.class);

  private static final String NODE_EXCEPTION_MESSAGE = "Node not found with given details";
  private static final String NODE_TYPE_EXCEPTION_MESSAGE = "Invalid Node Type Found";
  private static final String TIMEZONE_EXCEPTION_MESSAGE = "Invalid Timezone Found";
  private static final String COUNTRY_EXCEPTION_MESSAGE = "Invalid Country Found";

  public NodeResponse createNode(NodeRequest nodeRequest)
      throws CommonServiceException, NodeDomainException {
    validateNodeTypeTimezoneAndCountry(
        nodeRequest.getNodeType(),
        nodeRequest.getTimezone(),
        nodeRequest.getCountry(),
        nodeRequest.getOrgId());
    if (Objects.nonNull(nodeRequest.getStartWorkingTime())
        || Objects.nonNull(nodeRequest.getLastWorkingTime())) {
      validateStartAndLastWorkingTime(
          nodeRequest.getStartWorkingTime(), nodeRequest.getLastWorkingTime());
    }
    var nodeDetails = INSTANCE.nodeRequestToNodeEntity(nodeRequest);

    return INSTANCE.toNodeResponse(nodePersistenceService.saveNodeDetails(nodeDetails));
  }

  public NodeResponse updateNodeDetails(
      String nodeId, String orgId, NodeUpdationRequest nodeUpdationRequest)
      throws NodeDomainException, CommonServiceException {
    validateNodeTypeTimezoneAndCountry(
        nodeUpdationRequest.getNodeType(),
        nodeUpdationRequest.getTimezone(),
        nodeUpdationRequest.getCountry(),
        orgId);
    Optional<NodeDomainDto> existingNodeDetails =
        nodePersistenceService.findNodeByNodeIdAndOrgId(nodeId, orgId);

    if (existingNodeDetails.isEmpty()) {
      logger.error(NODE_EXCEPTION_MESSAGE);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(NODE_ID, FieldError.builder().rejectedValue(nodeId).build());
      throw new CommonServiceException(
          NODE_EXCEPTION_MESSAGE, HttpStatus.NOT_FOUND, 0x1771, errorMap);
    }
    if (Objects.nonNull(nodeUpdationRequest.getStartWorkingTime())
        || Objects.nonNull(nodeUpdationRequest.getLastWorkingTime())) {
      validateStartAndLastWorkingTime(
          nodeUpdationRequest.getStartWorkingTime(), nodeUpdationRequest.getLastWorkingTime());
    }
    logger.info("Response before updation of node :{}", existingNodeDetails.get());
    INSTANCE.updateNodeDetails(nodeUpdationRequest, existingNodeDetails.get());
    return INSTANCE.toNodeResponse(
        nodePersistenceService.saveNodeDetails(existingNodeDetails.get()));
  }

  @ReaderDS
  public NodeResponse getNodeDetails(String nodeId, String orgId)
      throws NodeDomainException, CommonServiceException {

    Optional<NodeDomainDto> nodeDetails =
        nodePersistenceService.findNodeByNodeIdAndOrgId(nodeId, orgId);

    if (nodeDetails.isEmpty()) {
      logger.error(NODE_EXCEPTION_MESSAGE);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(NODE_ID, FieldError.builder().rejectedValue(nodeId).build());
      throw new CommonServiceException(
          NODE_EXCEPTION_MESSAGE, HttpStatus.NOT_FOUND, 0x1771, errorMap);
    }
    return INSTANCE.toNodeResponse(nodeDetails.get());
  }

  public NodeResponse deleteNode(String nodeId, String orgId)
      throws NodeDomainException, CommonServiceException {

    Optional<NodeDomainDto> nodeDetails =
        nodePersistenceService.findNodeByNodeIdAndOrgId(nodeId, orgId);

    if (nodeDetails.isEmpty()) {
      logger.error(NODE_EXCEPTION_MESSAGE);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(NODE_ID, FieldError.builder().rejectedValue(nodeId).build());
      throw new CommonServiceException(
          NODE_EXCEPTION_MESSAGE, HttpStatus.NOT_FOUND, 0x1771, errorMap);
    }
    logger.info("Response before deletion of node :{}", INSTANCE.toNodeResponse(nodeDetails.get()));
    var nodeResponse = INSTANCE.toNodeResponse(nodeDetails.get());
    nodePersistenceService.deleteNode(nodeDetails.get());
    return nodeResponse;
  }

  @ReaderDS
  public Page<NodeDto> getNodeListByOrgId(
      String orgId, String nodeIds, String nodeType, PageParams pageParams)
      throws NodeDomainException, CommonServiceException {
    String sortOrder = pageParams.getSortOrder().orElse(DEFAULT_SORT_ORDER);
    validateSortOrder(sortOrder);
    List<String> nodeIdsList =
        StringUtils.hasLength(nodeIds) ? Arrays.asList(nodeIds.split(",")) : null;
    Page<NodeDomainDto> nodeDomainDtos =
        getNodeDomainDtos(orgId, nodeIdsList, nodeType, pageParams);
    return nodeDomainDtos.map(INSTANCE::toNodeDto);
  }

  private void validateSortOrder(String sortOrder) throws CommonServiceException {
    if (!sortOrder.equalsIgnoreCase(DEFAULT_SORT_ORDER)
        && !sortOrder.equalsIgnoreCase(DESC_SORT_ORDER)) {
      logger.error("Invalid sort order: {}", sortOrder);
      throw new CommonServiceException(
          "Invalid sort order, consider giving either ASC or DESC",
          HttpStatus.BAD_REQUEST,
          0x1771,
          Collections.singletonMap(
              SORT_ORDER, FieldError.builder().rejectedValue(sortOrder).build()));
    }
  }

  private Page<NodeDomainDto> getNodeDomainDtos(
      String orgId, List<String> nodeIdsList, String nodeType, PageParams pageParams)
      throws NodeDomainException {
    String sortOrder = pageParams.getSortOrder().orElse(DEFAULT_SORT_ORDER);
    String sortBy = pageParams.getSortBy().orElse(NODE_DEFAULT_SORT_BY);
    int pageNo = pageParams.getPageNo().orElse(pageProperties.getPageNo());
    int pageSize = pageParams.getPageSize().orElse(pageProperties.getPageSize());

    if (nodeIdsList == null && nodeType == null) {
      return nodePersistenceService.getNodeByOrgId(orgId, pageNo, pageSize, sortBy, sortOrder);
    } else if (nodeIdsList == null) {
      return nodePersistenceService.getByNodeTypeAndOrgId(orgId, nodeType, pageParams);
    } else if (nodeType == null) {
      return nodePersistenceService.getByNodeIdInAndOrgId(orgId, nodeIdsList, pageParams);
    } else {
      return nodePersistenceService.getNodesByNodeIdAndNodeTypeAndOrgId(
          orgId, nodeIdsList, nodeType, pageParams);
    }
  }

  @ReaderDS
  public Page<NodeDto> getAllNodes(
      Integer pageNo, Integer pageSize, String sortBy, String sortOrder)
      throws NodeDomainException {
    Page<NodeDomainDto> nodeDomainDtos =
        nodePersistenceService.getAllNodesPaginated(pageNo, pageSize, sortBy, sortOrder);
    return nodeDomainDtos.map(INSTANCE::toNodeDto);
  }

  public List<NodeCacheKeyDto> getAllNodeCacheKeys(Integer limit) throws NodeDomainException {
    var nodeDetailsList = nodePersistenceService.getAllNodeEntities(limit);

    return INSTANCE.toNodeCacheKeyResponseList(nodeDetailsList);
  }

  private void validateNodeTypeTimezoneAndCountry(
      String nodeType, String timezone, String country, String orgId)
      throws CommonServiceException {
    Set<String> nodeTypes = nodeTenantBasedDBConfig.getNodeTypes(orgId);
    if (!ObjectUtils.isEmpty(nodeType) && !nodeTypes.contains(nodeType.toUpperCase()))
      throwCommonServiceException(NODE_TYPE, nodeType, NODE_TYPE_EXCEPTION_MESSAGE);
    if (!ObjectUtils.isEmpty(country) && !Set.of(Locale.getISOCountries()).contains(country))
      throwCommonServiceException("country", country, COUNTRY_EXCEPTION_MESSAGE);
    if (!ObjectUtils.isEmpty(timezone) && !DateTimeZone.getAvailableIDs().contains(timezone))
      throwCommonServiceException("timezone", timezone, TIMEZONE_EXCEPTION_MESSAGE);
  }

  private void throwCommonServiceException(String field, String fieldValue, String errorMessage)
      throws CommonServiceException {
    logger.error(errorMessage);
    Map<String, FieldError> errorMap = new HashMap<>();
    errorMap.put(field, FieldError.builder().rejectedValue(fieldValue).build());
    throw new CommonServiceException(errorMessage, HttpStatus.BAD_REQUEST, 0x1771, errorMap);
  }

  @ReaderDS
  public Page<NodeDto> getNodeListByOrgIdV1(String orgId, PageParams pageParams)
      throws NodeDomainException, CommonServiceException {

    var sortOrder = pageParams.getSortOrder().orElse(DEFAULT_SORT_ORDER);
    var isValidSortOrder =
        sortOrder.equalsIgnoreCase(DEFAULT_SORT_ORDER)
            || sortOrder.equalsIgnoreCase(DESC_SORT_ORDER);

    if (!isValidSortOrder) {
      logger.error("Invalid sort order");
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(SORT_ORDER, FieldError.builder().rejectedValue(sortOrder).build());
      throw new CommonServiceException(
          "Invalid sort order, consider giving either ASC or DESC",
          HttpStatus.BAD_REQUEST,
          0x1771,
          errorMap);
    }

    Page<NodeDomainDto> nodeDomainDtos = nodePersistenceService.getNodeByOrgIdV1(orgId, pageParams);
    return nodeDomainDtos.map(INSTANCE::toNodeDto);
  }

  private void validateStartAndLastWorkingTime(String startWorkingTime, String lastWorkingTime)
      throws CommonServiceException {
    if (ObjectUtils.isEmpty(startWorkingTime) && ObjectUtils.isEmpty(lastWorkingTime)) {
      return;
    }
    if (ObjectUtils.isEmpty(startWorkingTime) && !ObjectUtils.isEmpty(lastWorkingTime)
        || !ObjectUtils.isEmpty(startWorkingTime) && ObjectUtils.isEmpty(lastWorkingTime)) {
      logger.error(
          "Either both startWorkingTime and lastWorkingTime should be present or neither should be present.");
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(
          START_WORKING_TIME, FieldError.builder().rejectedValue(startWorkingTime).build());
      errorMap.put(LAST_WORKING_TIME, FieldError.builder().rejectedValue(lastWorkingTime).build());
      throw new CommonServiceException(
          "Either both startWorkingTime and lastWorkingTime should be present or neither should be present.",
          HttpStatus.BAD_REQUEST,
          0x1771,
          errorMap);
    }
    validateTimeFormat(startWorkingTime, lastWorkingTime);
  }

  private void validateTimeFormat(String startWorkingTime, String lastWorkingTime)
      throws CommonServiceException {
    if (!startWorkingTime.matches(TIME_FORMAT)) {
      logger.error("startWorkingTime is invalid.");
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(
          START_WORKING_TIME, FieldError.builder().rejectedValue(startWorkingTime).build());
      throw new CommonServiceException(
          "startWorkingTime is invalid.", HttpStatus.BAD_REQUEST, 0x1771, errorMap);
    }
    if (!lastWorkingTime.matches(TIME_FORMAT)) {
      logger.error("lastWorkingTime is invalid.");
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(LAST_WORKING_TIME, FieldError.builder().rejectedValue(lastWorkingTime).build());
      throw new CommonServiceException(
          "lastWorkingTime is invalid.", HttpStatus.BAD_REQUEST, 0x1771, errorMap);
    }
  }

  public NodeTypesResponse getAllNodeTypesByOrgId(String orgId) {
    List<String> nodeTypes = nodePersistenceService.getAllUniqueNodeTypesByOrgId(orgId);
    return NodeTypesResponse.builder().nodeTypes(nodeTypes).build();
  }

  public List<String> checkNodesExistByNodeIdsAndOrgId(List<String> nodeIds, String orgId) {
    return nodePersistenceService.checkNodesExistByNodeIdsAndOrgId(nodeIds, orgId);
  }
}
