/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.promise.sourcing.rule.persistence.service;

import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.common.service.DomainPersistenceService;
import com.nextuple.promise.sourcing.rule.persistence.domain.NodePriorityDomainDto;
import com.nextuple.promise.sourcing.rule.persistence.domain.key.NodePriorityDomainKey;
import java.util.List;
import java.util.Optional;

public interface NodePriorityPersistenceService
    extends DomainPersistenceService<NodePriorityDomainDto, NodePriorityDomainKey> {

  NodePriorityDomainDto saveNodePriorityEntity(NodePriorityDomainDto nodePriorityDomainDto)
      throws PromiseEngineException;

  Optional<NodePriorityDomainDto> fetchNodePriorityEntityByIdAndOrgId(Long id, String orgId)
      throws PromiseEngineException;

  List<NodePriorityDomainDto> fetchNodePriorityListByOrgIdAndNodeGroupIdAndNodeId(
      String orgId, Long nodeGroupId, String nodeId) throws PromiseEngineException;

  List<NodePriorityDomainDto> fetchNodePriorityListByOrgIdAndNodeGroupId(
      String orgId, Long nodeGroupId) throws PromiseEngineException;

  List<NodePriorityDomainDto> fetchNodePriorityListByNodeIdAndOrgId(String nodeId, String orgId)
      throws PromiseEngineException;

  void deleteNodePriorityEntity(NodePriorityDomainDto nodePriorityDomainDto)
      throws PromiseEngineException;

  List<NodePriorityDomainDto> deleteNodePriorityByOrgIdAndNodeGroupId(
      String orgId, Long nodeGroupId) throws PromiseEngineException;

  List<NodePriorityDomainDto> findByNodeGroupIdsInOrderByPriority(List<Long> nodeGroupEntityIds);

  int countByOrgIdAndNodeGroupId(String orgId, Long id);
}
