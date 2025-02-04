/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */
package com.nextuple.node.persistence.service;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.pojo.PageParams;
import com.nextuple.common.service.DomainPersistenceService;
import com.nextuple.node.persistence.domain.NodeDomainDto;
import com.nextuple.node.persistence.domain.key.NodeDomainKey;
import com.nextuple.node.persistence.exception.NodeDomainException;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface NodePersistenceService
    extends DomainPersistenceService<NodeDomainDto, NodeDomainKey> {

  NodeDomainDto saveNodeDetails(NodeDomainDto nodeDomainDto) throws NodeDomainException;

  Optional<NodeDomainDto> findNodeByNodeIdAndOrgId(String nodeId, String orgId)
      throws NodeDomainException;

  void deleteNode(NodeDomainDto nodeDomainDto) throws NodeDomainException;

  Page<NodeDomainDto> getNodeByOrgId(
      String orgId, Integer pageNo, Integer pageSize, String sortBy, String sortOrder)
      throws NodeDomainException;

  List<NodeDomainDto> getAllNodeEntities(Integer limit) throws NodeDomainException;

  Page<NodeDomainDto> getAllNodesPaginated(
      Integer pageNo, Integer pageSize, String sortBy, String sortOrder) throws NodeDomainException;

  Page<NodeDomainDto> getNodeByOrgIdV1(String orgId, PageParams pageParams)
      throws NodeDomainException;

  Page<NodeDomainDto> getByNodeTypeAndOrgId(String orgId, String nodeType, PageParams pageParams)
      throws NodeDomainException;

  Page<NodeDomainDto> getByNodeIdInAndOrgId(
      String orgId, List<String> nodeIdsList, PageParams pageParams) throws NodeDomainException;

  Page<NodeDomainDto> getNodesByNodeIdAndNodeTypeAndOrgId(
      String orgId, List<String> nodeIdsList, String nodeType, PageParams pageParams)
      throws NodeDomainException;

  List<String> getAllUniqueNodeTypesByOrgId(String orgId);

  List<NodeDomainDto> findAllNodesFromCustomAttrAndOrgID(
      String customAttr, String customAttrValue, String orgId) throws CommonServiceException;
}
