/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.promise.sourcing.rule.persistence.service.impl;

import com.nextuple.common.enums.ApplicationLayer;
import com.nextuple.common.enums.ExceptionCodeMapping;
import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.postgres.service.CommonPersistenceService;
import com.nextuple.promise.sourcing.rule.persistence.domain.NodePriorityDomainDto;
import com.nextuple.promise.sourcing.rule.persistence.domain.key.NodePriorityDomainKey;
import com.nextuple.promise.sourcing.rule.persistence.entity.NodePriorityEntity;
import com.nextuple.promise.sourcing.rule.persistence.entity.key.NodePriorityKey;
import com.nextuple.promise.sourcing.rule.persistence.mapper.NodePriorityEntityMapper;
import com.nextuple.promise.sourcing.rule.persistence.repository.NodePriorityRepository;
import com.nextuple.promise.sourcing.rule.persistence.service.NodePriorityPersistenceService;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NodePriorityPersistenceServiceImpl
    extends CommonPersistenceService<
        NodePriorityDomainDto,
        NodePriorityDomainKey,
        NodePriorityEntity,
        NodePriorityKey,
        NodePriorityRepository,
        NodePriorityEntityMapper>
    implements NodePriorityPersistenceService {

  private static final Logger logger =
      LoggerFactory.getLogger(NodePriorityPersistenceServiceImpl.class);

  @Override
  public NodePriorityDomainDto saveNodePriorityEntity(NodePriorityDomainDto nodePriorityDomainDto)
      throws PromiseEngineException {
    try {
      return save(nodePriorityDomainDto);
    } catch (Exception e) {
      logger.error("Unable to save node group details entity", e);
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_SAVE_FAILED,
          "Unable to save node group details entity");
    }
  }

  @Override
  public Optional<NodePriorityDomainDto> fetchNodePriorityEntityByIdAndOrgId(Long id, String orgId)
      throws PromiseEngineException {
    try {
      return getRepository().findByIdAndOrgId(id, orgId).map(getMapper()::toDomain);
    } catch (Exception e) {
      logger.error("Unable to fetch node group details entity by id and orgId", e);
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_FIND_FAILED,
          "Unable to fetch node group details entity by id and orgId");
    }
  }

  @Override
  public List<NodePriorityDomainDto> fetchNodePriorityListByOrgIdAndNodeGroupIdAndNodeId(
      String orgId, Long nodeGroupId, String nodeId) throws PromiseEngineException {
    try {
      List<NodePriorityEntity> nodePriorityEntityList =
          getRepository().findByOrgIdAndNodeGroupIdAndNodeId(orgId, nodeGroupId, nodeId);
      return getMapper().toDomain(nodePriorityEntityList);
    } catch (Exception e) {
      logger.error(
          "Unable to fetch node group details entity list by orgId , nodeGroupId and nodeId", e);
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_FIND_FAILED,
          "Unable to fetch node group details entity list by orgId , nodeGroupId and nodeId");
    }
  }

  @Override
  public List<NodePriorityDomainDto> fetchNodePriorityListByOrgIdAndNodeGroupId(
      String orgId, Long nodeGroupId) throws PromiseEngineException {
    try {
      List<NodePriorityEntity> nodePriorityEntityList =
          getRepository().findByOrgIdAndNodeGroupId(orgId, nodeGroupId);
      return getMapper().toDomain(nodePriorityEntityList);
    } catch (Exception e) {
      logger.error("Unable to fetch node group details entity list by orgId , nodeGroupId", e);
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_FIND_FAILED,
          "Unable to fetch node group details entity list by orgId , nodeGroupId");
    }
  }

  @Override
  public List<NodePriorityDomainDto> fetchNodePriorityListByNodeIdAndOrgId(
      String nodeId, String orgId) throws PromiseEngineException {
    try {
      List<NodePriorityEntity> nodePriorityEntityList =
          getRepository().findByNodeIdAndOrgId(nodeId, orgId);
      return getMapper().toDomain(nodePriorityEntityList);
    } catch (Exception e) {
      logger.error("Unable to fetch node group details entity list by orgId , nodeId", e);
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_FIND_FAILED,
          "Unable to fetch node group details entity list by orgId: "
              + orgId
              + ", nodeId: "
              + nodeId);
    }
  }

  @Override
  public void deleteNodePriorityEntity(NodePriorityDomainDto nodePriorityDomainDto)
      throws PromiseEngineException {
    try {
      delete(nodePriorityDomainDto);
    } catch (Exception e) {
      logger.error("Unable to delete node group details entity", e);
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_DELETE_FAILED,
          "Unable to delete node group details entity");
    }
  }

  @Override
  public List<NodePriorityDomainDto> deleteNodePriorityByOrgIdAndNodeGroupId(
      String orgId, Long nodeGroupId) throws PromiseEngineException {
    try {
      List<NodePriorityEntity> nodePriorityEntityList =
          getRepository().deleteAllByOrgIdAndNodeGroupId(orgId, nodeGroupId);
      return getMapper().toDomain(nodePriorityEntityList);
    } catch (Exception e) {
      logger.error("Unable to delete node priority entities", e);
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_DELETE_FAILED,
          "Unable to delete node priority entities");
    }
  }

  @Override
  public List<NodePriorityDomainDto> findByNodeGroupIdsInOrderByPriority(
      List<Long> nodeGroupEntityIds) {
    List<NodePriorityEntity> nodePriorityEntityList =
        getRepository().findByNodeGroupIdInOrderByPriority(nodeGroupEntityIds);
    return getMapper().toDomain(nodePriorityEntityList);
  }

  @Override
  public int countByOrgIdAndNodeGroupId(String orgId, Long id) {
    return getRepository().countByOrgIdAndNodeGroupId(orgId, id);
  }
}
