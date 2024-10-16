/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.promise.sourcing.rule.service;

import com.nextuple.common.context.Logger;
import com.nextuple.common.context.LoggerFactory;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.common.pojo.PageParams;
import com.nextuple.common.response.error.FieldError;
import com.nextuple.common.util.PaginationUtil;
import com.nextuple.promise.sourcing.rule.api.domain.enums.SourcingAttributesDefinitionScopeEnum;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.*;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.*;
import com.nextuple.promise.sourcing.rule.api.domain.projection.SourcingRuleByNodeGroupCountProjection;
import com.nextuple.promise.sourcing.rule.domain.mapper.NodeGroupMapper;
import com.nextuple.promise.sourcing.rule.domain.mapper.NodePriorityMapper;
import com.nextuple.promise.sourcing.rule.persistence.domain.NodeGroupDomainDto;
import com.nextuple.promise.sourcing.rule.persistence.domain.NodePriorityDomainDto;
import com.nextuple.promise.sourcing.rule.persistence.service.NodeGroupPersistenceService;
import com.nextuple.promise.sourcing.rule.utils.PromiseSourcingRuleUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class NodeGroupService {

  private static final Logger logger = LoggerFactory.getLogger(NodeGroupService.class);

  private static final String ORG_ID = "orgId";
  private static final String NODE_GROUP_NAME = "nodeGroupName";
  private static final String PAGINATION_URL =
      "/ui/node-group/%s/node-priority?pageNo=%d&pageSize=%d";
  private static final String ID = "id";
  private static final String NODE_GROUP_EXCEPTION_MESSAGE = "Node Group not found";
  private static final NodeGroupMapper INSTANCE = Mappers.getMapper(NodeGroupMapper.class);
  private static final NodePriorityMapper NODE_PRIORITY_MAPPER =
      Mappers.getMapper(NodePriorityMapper.class);
  private final NodeGroupPersistenceService nodeGroupPersistenceService;
  private final NodePriorityService nodePriorityService;
  private final SourcingRulesConfigurationService sourcingRulesConfigurationService;

  @Value("${base-api-url}")
  private String baseApiUrl;

  private final SourcingAttributesDefinitionService sourcingAttributesDefinitionService;

  public NodeGroupResponse createNodeGroup(CreateNodeGroupRequest createNodeGroupRequest)
      throws PromiseEngineException, CommonServiceException {
    logger.debug("-- inside createNodeGroup service --");
    validateNodeGroupName(
        createNodeGroupRequest.getNodeGroupName(), createNodeGroupRequest.getOrgId());

    var nodeGroup = INSTANCE.toNodeGroupEntity(createNodeGroupRequest);
    return INSTANCE.toNodeGroupResponse(nodeGroupPersistenceService.saveNodeGroup(nodeGroup));
  }

  private void validateNodeGroupName(String nodeGroupName, String orgId)
      throws CommonServiceException, PromiseEngineException {
    PromiseSourcingRuleUtil.validateNameFormat(nodeGroupName);
    List<NodeGroupDomainDto> nodeGroupDomainDtos =
        nodeGroupPersistenceService.fetchNodeGroupByOrgIdAndName(orgId, nodeGroupName);
    if (!nodeGroupDomainDtos.isEmpty()) {
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(NODE_GROUP_NAME, FieldError.builder().rejectedValue(nodeGroupName).build());
      throw new CommonServiceException(
          "Combination of orgId and nodeGroupName should be unique",
          HttpStatus.BAD_REQUEST,
          0X1771,
          errorMap);
    }
  }

  private void checkForBlankNodeGroupName(String updateNodeGroupName)
      throws CommonServiceException {
    if (Objects.nonNull(updateNodeGroupName) && updateNodeGroupName.isEmpty()) {
      throw new CommonServiceException(
          "nodeGroupName cannot be blank", HttpStatus.BAD_REQUEST, 0X1771, null);
    }
  }

  public NodeGroupResponse fetchNodeGroupByIdandOrgId(Long id, String orgId)
      throws PromiseEngineException, CommonServiceException {
    logger.debug("-- inside fetchNodeGroupByIdandOrgId service --");
    Optional<NodeGroupDomainDto> nodeGroupDomainDto =
        nodeGroupPersistenceService.fetchNodeGroupByIdAndOrgId(id, orgId);
    if (nodeGroupDomainDto.isEmpty()) {
      logger.error(NODE_GROUP_EXCEPTION_MESSAGE);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ID, FieldError.builder().rejectedValue(id).build());
      throw new CommonServiceException(
          NODE_GROUP_EXCEPTION_MESSAGE, HttpStatus.NOT_FOUND, 0x1771, errorMap);
    }

    return INSTANCE.toNodeGroupResponse(nodeGroupDomainDto.get());
  }

  public NodeGroupResponse updateNodeGroup(
      Long id, String orgId, UpdateNodeGroupRequest updateNodeGroupRequest)
      throws PromiseEngineException, CommonServiceException {
    logger.debug("-- inside updateNodeGroupDetails service --");
    Optional<NodeGroupDomainDto> existingNodeGroupEntity =
        nodeGroupPersistenceService.fetchNodeGroupByIdAndOrgId(id, orgId);
    if (existingNodeGroupEntity.isEmpty()) {
      logger.error(NODE_GROUP_EXCEPTION_MESSAGE);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ID, FieldError.builder().rejectedValue(id).build());
      throw new CommonServiceException(
          NODE_GROUP_EXCEPTION_MESSAGE, HttpStatus.NOT_FOUND, 0x1771, errorMap);
    }

    checkForBlankNodeGroupName(updateNodeGroupRequest.getNodeGroupName());

    if (StringUtils.hasLength(updateNodeGroupRequest.getNodeGroupName())) {
      validateNodeGroupName(
          updateNodeGroupRequest.getNodeGroupName(), existingNodeGroupEntity.get().getOrgId());
    }

    INSTANCE.updateNodeGroupEntity(updateNodeGroupRequest, existingNodeGroupEntity.get());
    return INSTANCE.toNodeGroupResponse(
        nodeGroupPersistenceService.saveNodeGroup(existingNodeGroupEntity.get()));
  }

  @Transactional
  public NodeGroupWithNodesResponse createNodeGroupFromDashboard(
      String orgId, NodeGroupWithNodesResponse nodeGroupWithNodes)
      throws PromiseEngineException, CommonServiceException {
    NodeGroupWithNodesResponse response = new NodeGroupWithNodesResponse();
    CreateNodeGroupRequest request =
        new CreateNodeGroupRequest(orgId, nodeGroupWithNodes.getNodeGroupName(), "");
    NodeGroupResponse nodeGroupResponse = createNodeGroup(request);
    response.setNodeGroupName(nodeGroupResponse.getNodeGroupName());
    response.setNodes(new ArrayList<>());
    if (Objects.isNull(nodeGroupWithNodes.getNodes())) return response;
    try {
      for (NodeDetail nodeDetail : nodeGroupWithNodes.getNodes()) {
        NodePriorityRequest nodePriorityRequest =
            new NodePriorityRequest(
                orgId, nodeDetail.getNodeId(), nodeDetail.getSequence(), nodeGroupResponse.getId());
        NodePriorityResponse nodePriorityResponse =
            nodePriorityService.processAddNodePriorityToNodeGroup(nodePriorityRequest);
        NodeDetail responseNodeDetail =
            new NodeDetail(nodePriorityResponse.getNodeId(), nodePriorityRequest.getPriority());
        response.getNodes().add(responseNodeDetail);
      }
    } catch (CommonServiceException e) {
      logger.error("Error while adding node group", e);
      deleteNodeGroupAndAssociatedNodePriority(orgId, nodeGroupResponse.getId());
      throw e;
    }
    return response;
  }

  @Transactional
  public NodeGroupWithNodesResponse deleteNodeGroupAndAssociatedNodePriority(
      String orgId, Long nodeGroupId) throws PromiseEngineException, CommonServiceException {
    Optional<NodeGroupDomainDto> nodeGroupDomainDtoOptional =
        nodeGroupPersistenceService.fetchNodeGroupById(nodeGroupId);
    if (nodeGroupDomainDtoOptional.isEmpty()) {
      logger.error(NODE_GROUP_EXCEPTION_MESSAGE);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ID, FieldError.builder().rejectedValue(nodeGroupId).build());
      throw new CommonServiceException(
          NODE_GROUP_EXCEPTION_MESSAGE, HttpStatus.NOT_FOUND, 0x1771, errorMap);
    }
    List<NodePriorityDomainDto> nodePriorityDomainDtos =
        nodePriorityService.processRemoveAllNodesFromNodeGroup(orgId, nodeGroupId);
    nodeGroupPersistenceService.deleteNodeGroupEntity(nodeGroupDomainDtoOptional.get());
    NodeGroupWithNodesResponse nodeGroupWithNodesResponse = new NodeGroupWithNodesResponse();
    nodeGroupWithNodesResponse.setNodeGroupName(
        nodeGroupDomainDtoOptional.get().getNodeGroupName());

    List<NodeDetail> nodeDetails = new ArrayList<>();
    for (NodePriorityDomainDto nodePriorityDomainDto : nodePriorityDomainDtos) {
      NodeDetail nodeDetail =
          new NodeDetail(nodePriorityDomainDto.getNodeId(), nodePriorityDomainDto.getPriority());
      nodeDetails.add(nodeDetail);
    }
    nodeGroupWithNodesResponse.setNodes(nodeDetails);
    return nodeGroupWithNodesResponse;
  }

  @Transactional
  public NodeGroupWithNodesResponse editNodeGroupFromDashboard(
      String orgId, Long nodeGroupId, NodeGroupWithNodesResponse nodeGroupWithNodes)
      throws CommonServiceException, PromiseEngineException {
    if (ObjectUtils.isEmpty(nodeGroupWithNodes.getNodes())) {
      throw new CommonServiceException(
          "Node group must have at least one node attached to it",
          HttpStatus.BAD_REQUEST,
          0x1771,
          Map.of(
              "nodes", FieldError.builder().rejectedValue(nodeGroupWithNodes.getNodes()).build()));
    }
    Optional<NodeGroupDomainDto> groupDomainDtoOptional =
        nodeGroupPersistenceService.fetchNodeGroupById(nodeGroupId);
    if (groupDomainDtoOptional.isEmpty()) {
      logger.error(NODE_GROUP_EXCEPTION_MESSAGE);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ID, FieldError.builder().rejectedValue(nodeGroupId).build());
      throw new CommonServiceException(
          NODE_GROUP_EXCEPTION_MESSAGE, HttpStatus.NOT_FOUND, 0x1771, errorMap);
    }
    validateNodeGroupNameWhileUpdating(nodeGroupWithNodes.getNodeGroupName(), orgId, nodeGroupId);
    NodeGroupWithNodesResponse nodeGroupWithNodesResponse = new NodeGroupWithNodesResponse();
    nodeGroupWithNodesResponse.setNodeGroupName(nodeGroupWithNodes.getNodeGroupName());
    nodeGroupWithNodesResponse.setNodes(new ArrayList<>());

    nodePriorityService.deleteNodePriorityAssociatedToNodeGroup(orgId, nodeGroupId);
    List<NodeDetail> nodesToRemove = new ArrayList<>();
    for (NodeDetail nodeDetail : nodeGroupWithNodes.getNodes()) {
      NodePriorityRequest nodePriorityRequest =
          new NodePriorityRequest(
              orgId, nodeDetail.getNodeId(), nodeDetail.getSequence(), nodeGroupId);
      try {
        nodePriorityService.processAddNodePriorityToNodeGroup(nodePriorityRequest);
      } catch (CommonServiceException e) {
        nodesToRemove.add(nodeDetail);
      }
    }
    nodeGroupWithNodes.getNodes().removeAll(nodesToRemove);
    nodeGroupWithNodesResponse.setNodes(nodeGroupWithNodes.getNodes());
    NodeGroupDomainDto nodeGroupDomainDto = groupDomainDtoOptional.get();
    nodeGroupDomainDto.setNodeGroupName(nodeGroupWithNodes.getNodeGroupName());
    nodeGroupPersistenceService.saveNodeGroup(nodeGroupDomainDto);
    return nodeGroupWithNodesResponse;
  }

  private void validateNodeGroupNameWhileUpdating(
      String nodeGroupName, String orgId, Long nodeGroupId)
      throws CommonServiceException, PromiseEngineException {
    PromiseSourcingRuleUtil.validateNameFormat(nodeGroupName);
    List<NodeGroupDomainDto> nodeGroupEntities =
        nodeGroupPersistenceService.fetchNodeGroupByOrgIdAndName(orgId, nodeGroupName);
    if (!nodeGroupEntities.isEmpty() && !nodeGroupEntities.get(0).getId().equals(nodeGroupId)) {
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(NODE_GROUP_NAME, FieldError.builder().rejectedValue(nodeGroupName).build());
      throw new CommonServiceException(
          "Combination of orgId and nodeGroupName should be unique",
          HttpStatus.BAD_REQUEST,
          0X1771,
          errorMap);
    }
  }

  @Transactional
  public void deleteNodeGroupsAndAssociatedNodePriorityByOrgId(
      String orgId, DeleteNodeGroupsRequest request) throws PromiseEngineException {
    logger.debug("--Inside deleteNodeGroupsAndAssociatedNodePriorityByOrgId for orgId: " + orgId);
    for (Long nodeGroupId : request.getNodeGroupIds()) {
      nodePriorityService.processRemoveAllNodesFromNodeGroup(orgId, nodeGroupId);
    }
    nodeGroupPersistenceService.deleteNodeGroupEntities(request.getNodeGroupIds());
  }

  @Transactional(readOnly = true)
  public PageResponse<NodeGroupDetailWithPriorityResponse> fetchNodeGroupsByOrgIdWithNodePriority(
      String orgId, PageParams pageParams) throws PromiseEngineException, CommonServiceException {
    // GET LIST of node group by orgId.
    Pageable pageable = PromiseSourcingRuleUtil.getPageableForEmptyPageSize(pageParams);

    Page<NodeGroupDomainDto> nodeGroupDomainDtos =
        nodeGroupPersistenceService.fetchPaginatedNodeGroupListByOrgId(orgId, pageable);
    List<Long> nodeGroupEntityIds =
        nodeGroupDomainDtos.getContent().stream()
            .map(NodeGroupDomainDto::getId)
            .collect(Collectors.toList());

    // Get active sourcing definition id
    Long sourcingAttributesDefinitionId =
        sourcingAttributesDefinitionService
            .processGetSourcingAttributesDefinitionInActiveStatus(
                orgId, SourcingAttributesDefinitionScopeEnum.SOURCING_RULE)
            .getId();

    // for each node group check active sourcing rule count.
    List<String> nodeGroupIdStrings =
        nodeGroupEntityIds.stream().map(String::valueOf).collect(Collectors.toList());
    List<SourcingRuleByNodeGroupCountProjection> res =
        sourcingRulesConfigurationService.getActiveSourcingRuleCountByNodeGroupIds(
            nodeGroupIdStrings, sourcingAttributesDefinitionId);
    Map<String, Long> sourcingRuleCountMap =
        res.stream()
            .collect(
                Collectors.toMap(
                    SourcingRuleByNodeGroupCountProjection::getNodeGroup,
                    SourcingRuleByNodeGroupCountProjection::getCount));

    // get node info from node priority table
    List<NodePriorityDomainDto> nodePriorityDomainDtoList =
        nodePriorityService.getNodePriorityListByNodeGroupIds(nodeGroupEntityIds);
    Map<Long, List<NodePriorityDomainDto>> nodePriorityEntityMap =
        nodePriorityDomainDtoList.stream()
            .collect(
                Collectors.groupingBy(NodePriorityDomainDto::getNodeGroupId, Collectors.toList()));
    List<NodeGroupDetailWithPriorityResponse> nodeGroupDetailWithPriorityResponses =
        prepareNodeGroupWithPriorityResponse(
            nodeGroupDomainDtos, nodePriorityEntityMap, sourcingRuleCountMap);
    return this.buildNodeGroupPageResponse(
        orgId, nodeGroupDetailWithPriorityResponses, nodeGroupDomainDtos, pageParams);
  }

  private List<NodeGroupDetailWithPriorityResponse> prepareNodeGroupWithPriorityResponse(
      Page<NodeGroupDomainDto> nodeGroupDomainDtos,
      Map<Long, List<NodePriorityDomainDto>> nodePriorityEntityMap,
      Map<String, Long> sourcingRuleCountMap) {
    return nodeGroupDomainDtos.getContent().stream()
        .map(
            nodeGroupDomainDto ->
                NodeGroupDetailWithPriorityResponse.builder()
                    .nodeGroupId(nodeGroupDomainDto.getId())
                    .associatedNodesInfo(
                        nodePriorityEntityMap.get(nodeGroupDomainDto.getId()) == null
                            ? List.of()
                            : NODE_PRIORITY_MAPPER.toNodePriorityInfo(
                                nodePriorityEntityMap.get(nodeGroupDomainDto.getId())))
                    .nodeGroupName(nodeGroupDomainDto.getNodeGroupName())
                    .orgId(nodeGroupDomainDto.getOrgId())
                    .numberOfSourcingRules(
                        sourcingRuleCountMap.containsKey(nodeGroupDomainDto.getId().toString())
                            ? sourcingRuleCountMap.get(nodeGroupDomainDto.getId().toString())
                            : 0)
                    .build())
        .collect(Collectors.toList());
  }

  private PageResponse<NodeGroupDetailWithPriorityResponse> buildNodeGroupPageResponse(
      String orgId,
      List<NodeGroupDetailWithPriorityResponse> nodeGroupDetailWithPriorityResponses,
      Page<NodeGroupDomainDto> nodeGroupDomainDtoList,
      PageParams pageParams) {
    int currentPage =
        pageParams.getPageSize().isEmpty()
            ? 1
            : nodeGroupDomainDtoList.getPageable().getPageNumber() + 1;
    int totalPages = nodeGroupDomainDtoList.getTotalPages();
    Long pageSize =
        nodeGroupDomainDtoList.getPageable().isUnpaged()
            ? nodeGroupDomainDtoList.getTotalElements()
            : nodeGroupDomainDtoList.getPageable().getPageSize();
    String paginationUrl = "%s%s".formatted(baseApiUrl, PAGINATION_URL);
    PageResponse<NodeGroupDetailWithPriorityResponse> response = new PageResponse<>();

    response.setData(nodeGroupDetailWithPriorityResponses);
    String nextUri =
        PaginationUtil.buildUriForPagination(
            currentPage,
            totalPages,
            "next",
            paginationUrl.formatted(orgId, currentPage + 1, pageSize));
    String previousUri =
        PaginationUtil.buildUriForPagination(
            currentPage,
            totalPages,
            "previous",
            paginationUrl.formatted(orgId, currentPage - 1, pageSize));
    response.setPagination(
        PaginationAttribute.builder()
            .currentPage(currentPage)
            .totalPages(totalPages)
            .totalRecords(nodeGroupDomainDtoList.getTotalElements())
            .next(nextUri)
            .previous(previousUri)
            .sortBy(pageParams.getSortBy().orElse(""))
            .sortOrder(pageParams.getSortOrder().orElse(""))
            .build());
    return response;
  }

  public List<NodeGroupDomainDto> getNodeGroupListForOrgId(String orgId)
      throws PromiseEngineException {
    return nodeGroupPersistenceService.fetchNodeGroupListByOrgId(orgId);
  }
}
