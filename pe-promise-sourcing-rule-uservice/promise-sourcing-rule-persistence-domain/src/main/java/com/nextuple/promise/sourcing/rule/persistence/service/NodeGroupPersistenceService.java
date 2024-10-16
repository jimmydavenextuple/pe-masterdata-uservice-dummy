/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.promise.sourcing.rule.persistence.service;

import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.common.service.DomainPersistenceService;
import com.nextuple.promise.sourcing.rule.persistence.domain.NodeGroupDomainDto;
import com.nextuple.promise.sourcing.rule.persistence.domain.key.NodeGroupDomainKey;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NodeGroupPersistenceService
    extends DomainPersistenceService<NodeGroupDomainDto, NodeGroupDomainKey> {
  NodeGroupDomainDto saveNodeGroup(NodeGroupDomainDto nodeGroupDomainDto)
      throws PromiseEngineException;

  Optional<NodeGroupDomainDto> fetchNodeGroupById(Long id) throws PromiseEngineException;

  Optional<NodeGroupDomainDto> fetchNodeGroupByIdAndOrgId(Long id, String orgId)
      throws PromiseEngineException;

  List<NodeGroupDomainDto> fetchNodeGroupByOrgIdAndName(String orgId, String name)
      throws PromiseEngineException;

  List<NodeGroupDomainDto> fetchNodeGroupListByOrgId(String orgId) throws PromiseEngineException;

  void deleteNodeGroupEntity(NodeGroupDomainDto nodeGroupDomainDto) throws PromiseEngineException;

  void deleteNodeGroupEntities(List<Long> nodeGroupIds) throws PromiseEngineException;

  Page<NodeGroupDomainDto> fetchPaginatedNodeGroupListByOrgId(String orgId, Pageable pageRequest)
      throws PromiseEngineException;
}
