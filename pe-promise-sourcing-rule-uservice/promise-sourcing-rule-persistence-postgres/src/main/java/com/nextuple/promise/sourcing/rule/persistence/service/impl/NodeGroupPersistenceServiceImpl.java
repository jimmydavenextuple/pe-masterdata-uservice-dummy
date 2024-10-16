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
import com.nextuple.promise.sourcing.rule.persistence.domain.NodeGroupDomainDto;
import com.nextuple.promise.sourcing.rule.persistence.domain.key.NodeGroupDomainKey;
import com.nextuple.promise.sourcing.rule.persistence.entity.NodeGroupEntity;
import com.nextuple.promise.sourcing.rule.persistence.entity.SourcingRuleDetailsEntity;
import com.nextuple.promise.sourcing.rule.persistence.entity.SourcingRulesConfigurationEntity;
import com.nextuple.promise.sourcing.rule.persistence.entity.key.NodeGroupKey;
import com.nextuple.promise.sourcing.rule.persistence.mapper.NodeGroupEntityMapper;
import com.nextuple.promise.sourcing.rule.persistence.repository.NodeGroupRepository;
import com.nextuple.promise.sourcing.rule.persistence.repository.SourcingRuleDetailsRepository;
import com.nextuple.promise.sourcing.rule.persistence.repository.SourcingRulesConfigurationRepository;
import com.nextuple.promise.sourcing.rule.persistence.service.NodeGroupPersistenceService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NodeGroupPersistenceServiceImpl
    extends CommonPersistenceService<
        NodeGroupDomainDto,
        NodeGroupDomainKey,
        NodeGroupEntity,
        NodeGroupKey,
        NodeGroupRepository,
        NodeGroupEntityMapper>
    implements NodeGroupPersistenceService {

  private static final Logger logger =
      LoggerFactory.getLogger(NodeGroupPersistenceServiceImpl.class);
  private final SourcingRuleDetailsRepository sourcingRuleDetailsRepository;
  private final SourcingRulesConfigurationRepository rulesConfigurationRepository;

  @Override
  public NodeGroupDomainDto saveNodeGroup(NodeGroupDomainDto nodeGroupDomainDto)
      throws PromiseEngineException {
    try {
      return save(nodeGroupDomainDto);
    } catch (Exception e) {
      logger.error("Unable to save node group", e);
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_SAVE_FAILED,
          "Unable to save node group");
    }
  }

  @Override
  public Optional<NodeGroupDomainDto> fetchNodeGroupById(Long id) throws PromiseEngineException {
    try {
      return getRepository().findById(id).map(getMapper()::toDomain);
    } catch (Exception e) {
      logger.error("Unable to find node group", e);
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_FIND_FAILED,
          "Unable to find node group");
    }
  }

  @Override
  public Optional<NodeGroupDomainDto> fetchNodeGroupByIdAndOrgId(Long id, String orgId)
      throws PromiseEngineException {
    try {
      return getRepository().findByIdAndOrgId(id, orgId).map(getMapper()::toDomain);
    } catch (Exception e) {
      logger.error("Unable to find node group and orgId", e);
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_FIND_FAILED,
          "Unable to find node group and orgId");
    }
  }

  @Override
  public List<NodeGroupDomainDto> fetchNodeGroupByOrgIdAndName(String orgId, String name)
      throws PromiseEngineException {
    try {
      List<NodeGroupEntity> nodeGroupEntities =
          getRepository().findByOrgIdAndNodeGroupName(orgId, name);
      return getMapper().toDomain(nodeGroupEntities);
    } catch (Exception e) {
      logger.error("Unable to fetch node groups", e);
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_FIND_FAILED,
          "Unable to fetch node groups");
    }
  }

  @Override
  public List<NodeGroupDomainDto> fetchNodeGroupListByOrgId(String orgId)
      throws PromiseEngineException {
    try {
      List<NodeGroupEntity> nodeGroupEntities = getRepository().findByOrgId(orgId);
      return getMapper().toDomain(nodeGroupEntities);
    } catch (Exception e) {
      logger.error("Unable to fetch node group list by orgId", e);
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_FIND_FAILED,
          "Unable to fetch node group list by orgId");
    }
  }

  @Override
  public void deleteNodeGroupEntity(NodeGroupDomainDto nodeGroupDomainDto)
      throws PromiseEngineException {
    try {
      delete(nodeGroupDomainDto);
      deleteNodeGroupsFromAssociatedSourcingRules(List.of(nodeGroupDomainDto.getId()));
    } catch (Exception e) {
      logger.error("Unable to delete node group", e);
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_DELETE_FAILED,
          "Unable to delete node group");
    }
  }

  @Override
  public void deleteNodeGroupEntities(List<Long> nodeGroupIds) throws PromiseEngineException {
    try {
      getRepository().deleteAllByIdIn(nodeGroupIds);
      deleteNodeGroupsFromAssociatedSourcingRules(nodeGroupIds);
    } catch (Exception e) {
      logger.error("Unable to delete node groups", e);
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_DELETE_FAILED,
          "Unable to delete node groups");
    }
  }

  private void deleteNodeGroupsFromAssociatedSourcingRules(List<Long> nodeGroupIds) {
    for (Long nodeGroupId : nodeGroupIds) {
      List<SourcingRuleDetailsEntity> sourcingRuleDetailsEntities =
          sourcingRuleDetailsRepository.findByNodeGroupsLike("%" + nodeGroupId + "%");
      // need to filter again for cases like %1% => 1,2 and 10,2 also
      sourcingRuleDetailsEntities =
          sourcingRuleDetailsEntities.stream()
              .map(
                  x -> {
                    List<String> nodeGroups = List.of(x.getNodeGroups().split(","));
                    nodeGroups =
                        nodeGroups.stream()
                            .filter(y -> !y.equals(nodeGroupId.toString()))
                            .collect(Collectors.toList());
                    x.setNodeGroups(StringUtils.join(nodeGroups, ","));
                    return x;
                  })
              .collect(Collectors.toList());
      List<SourcingRuleDetailsEntity> sourcingRulesToDelete =
          sourcingRuleDetailsEntities.stream()
              .filter(x -> x.getNodeGroups().isEmpty())
              .collect(Collectors.toList());
      List<SourcingRulesConfigurationEntity> sourcingRulesConfigurationToDelete = new ArrayList<>();

      sourcingRuleDetailsRepository.saveAll(sourcingRuleDetailsEntities);
      sourcingRuleDetailsRepository.deleteAll(sourcingRulesToDelete);

      for (SourcingRuleDetailsEntity rule : sourcingRulesToDelete) {

        Optional<SourcingRulesConfigurationEntity> optionalSourcingRuleConfigurationToDelete =
            rulesConfigurationRepository.findById(rule.getSourcingRuleId());
        var sourcingRuleDetail =
            sourcingRuleDetailsRepository.findByOrgIdAndSourcingRuleId(
                rule.getOrgId(), rule.getSourcingRuleId());
        optionalSourcingRuleConfigurationToDelete.ifPresent(
            sourcingRuleConfigurationToDelete -> {
              if (sourcingRuleDetail.isEmpty()) {
                sourcingRulesConfigurationToDelete.add(sourcingRuleConfigurationToDelete);
              }
            });
      }

      if (!sourcingRulesConfigurationToDelete.isEmpty()) {
        rulesConfigurationRepository.deleteAll(sourcingRulesConfigurationToDelete);
      }
    }
  }

  @Override
  public Page<NodeGroupDomainDto> fetchPaginatedNodeGroupListByOrgId(
      String orgId, Pageable pageRequest) throws PromiseEngineException {
    try {
      return getRepository().findByOrgId(orgId, pageRequest).map(getMapper()::toDomain);
    } catch (Exception e) {
      logger.error("Unable to fetch node group list by orgId", e);
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_FIND_FAILED,
          "Unable to fetch node group list by orgId");
    }
  }
}
