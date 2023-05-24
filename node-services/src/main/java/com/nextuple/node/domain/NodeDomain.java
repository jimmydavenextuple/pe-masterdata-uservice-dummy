/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.node.domain;

import static com.nextuple.common.constants.CommonConstants.DEFAULT_SORT_ORDER;
import static com.nextuple.common.constants.CommonConstants.NODE_DEFAULT_SORT_BY;

import com.nextuple.common.pojo.PageParams;
import com.nextuple.common.pojo.PageProperties;
import com.nextuple.node.domain.dto.NodeDto;
import com.nextuple.node.domain.entity.NodeEntity;
import com.nextuple.node.domain.mapper.NodeMapper;
import com.nextuple.node.domain.outbound.NodeResponse;
import com.nextuple.node.exception.NodeDomainException;
import com.nextuple.node.repository.NodeRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NodeDomain {

  private static final Logger logger = LoggerFactory.getLogger(NodeDomain.class);
  private final NodeRepository nodeRepository;
  private final PageProperties pageProperties;

  public static final NodeMapper INSTANCE = Mappers.getMapper(NodeMapper.class);

  public NodeEntity saveNodeEntity(NodeEntity nodeEntity) throws NodeDomainException {
    try {
      return nodeRepository.save(nodeEntity);
    } catch (Exception e) {
      logger.error(String.valueOf(e), "Unable to save node");
      throw new NodeDomainException(
          "Error while saving the node", nodeEntity.getNodeId(), nodeEntity.getOrgId());
    }
  }

  public Optional<NodeEntity> findNodeByNodeIdAndOrgId(String nodeId, String orgId)
      throws NodeDomainException {
    try {
      return nodeRepository.findByNodeIdAndOrgId(nodeId, orgId);
    } catch (Exception e) {
      logger.error(String.valueOf(e), "Unable to find node");
      throw new NodeDomainException("Error while finding node", nodeId, orgId);
    }
  }

  public void deleteNode(NodeEntity nodeEntity) throws NodeDomainException {
    try {
      nodeRepository.delete(nodeEntity);
    } catch (Exception e) {
      logger.error(String.valueOf(e), "Unable to delete node");
      throw new NodeDomainException(
          "Error while deleting node", nodeEntity.getNodeId(), nodeEntity.getOrgId());
    }
  }

  public Page<NodeDto> getNodeByOrgId(
      String orgId, Integer pageNo, Integer pageSize, String sortBy, String sortOrder)
      throws NodeDomainException {
    try {
      Pageable pageable = null;
      if (sortOrder.equalsIgnoreCase(DEFAULT_SORT_ORDER)) {
        pageable = PageRequest.of(pageNo - 1, pageSize, Sort.by(sortBy).ascending());
      } else {
        pageable = PageRequest.of(pageNo - 1, pageSize, Sort.by(sortBy).descending());
      }
      return nodeRepository.findNodeByOrgId(orgId, pageable).map(INSTANCE::toNodeDto);
    } catch (Exception e) {
      logger.error(String.valueOf(e), "Unable to find node list");
      throw new NodeDomainException("Error while finding node list", null, orgId);
    }
  }

  public Page<NodeResponse> getAllNodesPaginated(
      Integer pageNo, Integer pageSize, String sortBy, String sortOrder)
      throws NodeDomainException {
    try {
      Pageable pageable = null;
      if (sortOrder.equalsIgnoreCase(DEFAULT_SORT_ORDER)) {
        pageable = PageRequest.of(pageNo - 1, pageSize, Sort.by(sortBy).ascending());
      } else {
        pageable = PageRequest.of(pageNo - 1, pageSize, Sort.by(sortBy).descending());
      }
      return nodeRepository.findAll(pageable).map(INSTANCE::toNodeResponse);
    } catch (Exception e) {
      logger.error(String.valueOf(e), "Unable to find node list");
      throw new NodeDomainException("Error while finding node list", null, null);
    }
  }

  public List<NodeEntity> getAllNodeEntities(Integer limit) throws NodeDomainException {
    try {
      return nodeRepository.findAllNodeEntities(limit);
    } catch (Exception e) {
      logger.error(String.valueOf(e), "Unable to fetch the node entities");
      throw new NodeDomainException("Error while fetching all node records", null, null);
    }
  }

  public Page<NodeDto> getNodeByOrgIdV1(String orgId, PageParams pageParams)
      throws NodeDomainException {
    try {
      Pageable pageable = preparePagableBasedOnPageSize(pageParams);
      return nodeRepository.findNodeByOrgId(orgId, pageable).map(INSTANCE::toNodeDto);
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
