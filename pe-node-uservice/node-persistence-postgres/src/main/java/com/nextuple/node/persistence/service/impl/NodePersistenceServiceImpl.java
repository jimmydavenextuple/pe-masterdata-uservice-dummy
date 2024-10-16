/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */
package com.nextuple.node.persistence.service.impl;

import static com.nextuple.common.constants.CommonConstants.DEFAULT_SORT_ORDER;
import static com.nextuple.common.constants.CommonConstants.NODE_DEFAULT_SORT_BY;

import com.nextuple.common.context.Logger;
import com.nextuple.common.context.LoggerFactory;
import com.nextuple.common.pojo.PageParams;
import com.nextuple.common.pojo.PageProperties;
import com.nextuple.node.persistence.domain.NodeDomainDto;
import com.nextuple.node.persistence.domain.key.NodeDomainKey;
import com.nextuple.node.persistence.entity.NodeEntity;
import com.nextuple.node.persistence.entity.key.NodeKey;
import com.nextuple.node.persistence.exception.NodeDomainException;
import com.nextuple.node.persistence.mapper.NodeEntityMapper;
import com.nextuple.node.persistence.repository.NodeRepository;
import com.nextuple.node.persistence.service.NodePersistenceService;
import com.nextuple.postgres.service.CommonPersistenceService;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NodePersistenceServiceImpl
    extends CommonPersistenceService<
        NodeDomainDto, NodeDomainKey, NodeEntity, NodeKey, NodeRepository, NodeEntityMapper>
    implements NodePersistenceService {

  private static final Logger logger = LoggerFactory.getLogger(NodePersistenceServiceImpl.class);

  private final PageProperties pageProperties;

  @Override
  public NodeDomainDto saveNodeDetails(NodeDomainDto nodeDomainDto) throws NodeDomainException {
    try {
      return save(nodeDomainDto);
    } catch (Exception e) {
      logger.error(String.valueOf(e), "Unable to save node");
      throw new NodeDomainException(
          "Error while saving the node", nodeDomainDto.getNodeId(), nodeDomainDto.getOrgId());
    }
  }

  @Override
  public Optional<NodeDomainDto> findNodeByNodeIdAndOrgId(String nodeId, String orgId)
      throws NodeDomainException {
    try {
      return findByKey(NodeDomainKey.builder().orgId(orgId).nodeId(nodeId).build());
    } catch (Exception e) {
      logger.error(String.valueOf(e), "Unable to find node");
      throw new NodeDomainException("Error while finding node", nodeId, orgId);
    }
  }

  @Override
  public void deleteNode(NodeDomainDto nodeDomainDto) throws NodeDomainException {
    try {
      delete(nodeDomainDto);
    } catch (Exception e) {
      logger.error(String.valueOf(e), "Unable to delete node");
      throw new NodeDomainException(
          "Error while deleting node", nodeDomainDto.getNodeId(), nodeDomainDto.getOrgId());
    }
  }

  @Override
  public Page<NodeDomainDto> getNodeByOrgId(
      String orgId, Integer pageNo, Integer pageSize, String sortBy, String sortOrder)
      throws NodeDomainException {
    try {
      Pageable pageable;
      if (sortOrder.equalsIgnoreCase(DEFAULT_SORT_ORDER)) {
        pageable = PageRequest.of(pageNo - 1, pageSize, Sort.by(sortBy).ascending());
      } else {
        pageable = PageRequest.of(pageNo - 1, pageSize, Sort.by(sortBy).descending());
      }
      return getRepository().findNodeByOrgId(orgId, pageable).map(getMapper()::toDomain);
    } catch (Exception e) {
      logger.error(String.valueOf(e), "Unable to find node list");
      throw new NodeDomainException("Error while finding node list", null, orgId);
    }
  }

  public Page<NodeDomainDto> getAllNodesPaginated(
      Integer pageNo, Integer pageSize, String sortBy, String sortOrder)
      throws NodeDomainException {
    try {
      Pageable pageable = null;
      if (sortOrder.equalsIgnoreCase(DEFAULT_SORT_ORDER)) {
        pageable = PageRequest.of(pageNo - 1, pageSize, Sort.by(sortBy).ascending());
      } else {
        pageable = PageRequest.of(pageNo - 1, pageSize, Sort.by(sortBy).descending());
      }
      return getRepository().findAll(pageable).map(getMapper()::toDomain);
    } catch (Exception e) {
      logger.error(String.valueOf(e), "Unable to fetch the node entities");
      throw new NodeDomainException("Error while finding node list", null, null);
    }
  }

  @Override
  public List<NodeDomainDto> getAllNodeEntities(Integer limit) throws NodeDomainException {
    try {
      return getMapper().toDomain(getRepository().findAllNodeEntities(limit));

    } catch (Exception e) {
      logger.error(String.valueOf(e), "Unable to find node list");
      throw new NodeDomainException("Error while fetching all node records", null, null);
    }
  }

  @Override
  public Page<NodeDomainDto> getByNodeTypeAndOrgId(
      String orgId, String nodeType, PageParams pageParams) throws NodeDomainException {
    try {
      String sortOrder = pageParams.getSortOrder().orElse(DEFAULT_SORT_ORDER);
      String sortBy = pageParams.getSortBy().orElse(NODE_DEFAULT_SORT_BY);
      Integer pageNo = pageParams.getPageNo().orElse(pageProperties.getPageNo());
      Integer pageSize = pageParams.getPageSize().orElse(pageProperties.getPageSize());
      Pageable pageable =
          sortOrder.equalsIgnoreCase(DEFAULT_SORT_ORDER)
              ? PageRequest.of(pageNo - 1, pageSize, Sort.by(sortBy).ascending())
              : PageRequest.of(pageNo - 1, pageSize, Sort.by(sortBy).descending());

      return getRepository()
          .findByNodeTypeAndOrgId(nodeType, orgId, pageable)
          .map(getMapper()::toDomain);
    } catch (Exception e) {
      logger.error(String.valueOf(e), "Unable to find node list");
      throw new NodeDomainException("Error while finding node list", nodeType, orgId);
    }
  }

  @Override
  public Page<NodeDomainDto> getByNodeIdInAndOrgId(
      String orgId, List<String> nodeIdsList, PageParams pageParams) throws NodeDomainException {
    try {
      String sortOrder = pageParams.getSortOrder().orElse(DEFAULT_SORT_ORDER);
      String sortBy = pageParams.getSortBy().orElse(NODE_DEFAULT_SORT_BY);
      Integer pageNo = pageParams.getPageNo().orElse(pageProperties.getPageNo());
      Integer pageSize = pageParams.getPageSize().orElse(pageProperties.getPageSize());
      Pageable pageable =
          sortOrder.equalsIgnoreCase(DEFAULT_SORT_ORDER)
              ? PageRequest.of(pageNo - 1, pageSize, Sort.by(sortBy).ascending())
              : PageRequest.of(pageNo - 1, pageSize, Sort.by(sortBy).descending());

      return getRepository()
          .findByNodeIdInAndOrgId(nodeIdsList, orgId, pageable)
          .map(getMapper()::toDomain);
    } catch (Exception e) {
      logger.error(String.valueOf(e), "Unable to find node list");
      throw new NodeDomainException("Error while finding node list", nodeIdsList.toString(), orgId);
    }
  }

  @Override
  public Page<NodeDomainDto> getNodesByNodeIdAndNodeTypeAndOrgId(
      String orgId, List<String> nodeIdsList, String nodeType, PageParams pageParams)
      throws NodeDomainException {
    try {
      String sortOrder = pageParams.getSortOrder().orElse(DEFAULT_SORT_ORDER);
      String sortBy = pageParams.getSortBy().orElse(NODE_DEFAULT_SORT_BY);
      Integer pageNo = pageParams.getPageNo().orElse(pageProperties.getPageNo());
      Integer pageSize = pageParams.getPageSize().orElse(pageProperties.getPageSize());
      Pageable pageable;
      if (sortOrder.equalsIgnoreCase(DEFAULT_SORT_ORDER)) {
        pageable = PageRequest.of(pageNo - 1, pageSize, Sort.by(sortBy).ascending());
      } else {
        pageable = PageRequest.of(pageNo - 1, pageSize, Sort.by(sortBy).descending());
      }
      return getRepository()
          .findByNodeIdInAndNodeTypeAndOrgId(nodeIdsList, nodeType, orgId, pageable)
          .map(getMapper()::toDomain);
    } catch (Exception e) {
      logger.error(String.valueOf(e), "Unable to find node list");
      throw new NodeDomainException("Error while finding node list", nodeIdsList.toString(), orgId);
    }
  }

  @Override
  public List<String> getAllUniqueNodeTypesByOrgId(String orgId) {
    return getRepository().findDistinctNodeTypesByOrgId(orgId);
  }

  public Page<NodeDomainDto> getNodeByOrgIdV1(String orgId, PageParams pageParams)
      throws NodeDomainException {
    try {
      return getRepository()
          .findNodeByOrgId(orgId, preparePagableBasedOnPageSize(pageParams))
          .map(getMapper()::toDomain);
    } catch (Exception e) {
      logger.error(String.valueOf(e), "Unable to find node list");
      throw new NodeDomainException("Error while finding node list", null, orgId);
    }
  }

  private Pageable preparePagableBasedOnPageSize(PageParams pageParams) {

    var sortBy = pageParams.getSortBy().orElse(NODE_DEFAULT_SORT_BY);
    var sortOrder = pageParams.getSortOrder().orElse(DEFAULT_SORT_ORDER);
    Sort sort = Sort.by(sortBy);
    sort =
        StringUtils.equalsIgnoreCase(sortOrder, DEFAULT_SORT_ORDER)
            ? sort.ascending()
            : sort.descending();

    var pageNo = pageParams.getPageNo().orElse(pageProperties.getPageNo()) - 1;
    var pageSize = pageParams.getPageSize().orElse(pageProperties.getPageSize());
    return pageParams.getPageSize().isEmpty()
        ? Pageable.unpaged()
        : PageRequest.of(pageNo, pageSize, sort);
  }
}
