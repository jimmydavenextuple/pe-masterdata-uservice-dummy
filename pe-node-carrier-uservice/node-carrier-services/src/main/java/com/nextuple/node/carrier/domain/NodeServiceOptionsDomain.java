/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.node.carrier.domain;

import static com.nextuple.common.constants.CommonConstants.NODE_ID;
import static com.nextuple.common.constants.CommonConstants.ORG_ID;
import static com.nextuple.common.constants.CommonConstants.SERVICE_OPTION;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.error.FieldError;
import com.nextuple.node.carrier.domain.entity.NodeServiceOptionEntity;
import com.nextuple.node.carrier.repository.NodeServiceOptionRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NodeServiceOptionsDomain {

  private static final Logger logger = LoggerFactory.getLogger(NodeServiceOptionsDomain.class);

  private final NodeServiceOptionRepository nodeServiceOptionRepository;

  public NodeServiceOptionEntity saveNodeServiceOptionEntity(
      NodeServiceOptionEntity nodeServiceOptionEntity) throws CommonServiceException {
    try {
      return nodeServiceOptionRepository.save(nodeServiceOptionEntity);
    } catch (Exception e) {
      logger.error(String.valueOf(e), "Error while creating node service option");
      throw handleException(
          nodeServiceOptionEntity.getOrgId(),
          nodeServiceOptionEntity.getNodeId(),
          nodeServiceOptionEntity.getServiceOption(),
          "Error while saving the node service option",
          0x1789);
    }
  }

  public Optional<NodeServiceOptionEntity> findNodeServiceOptionEntity(
      String orgId, String nodeId, String serviceOption) throws CommonServiceException {
    try {
      return nodeServiceOptionRepository.findByOrgIdAndNodeIdAndServiceOption(
          orgId, nodeId, serviceOption);
    } catch (Exception e) {
      logger.error(String.valueOf(e), "Unable to find a node service option");
      throw handleException(
          orgId, nodeId, serviceOption, "Error while finding node service option", 0x1790);
    }
  }

  public List<NodeServiceOptionEntity> findByOrgIdAndNodeId(String orgId, String nodeId)
      throws CommonServiceException {
    try {
      return nodeServiceOptionRepository.findByOrgIdAndNodeId(orgId, nodeId);
    } catch (Exception e) {
      throw new CommonServiceException(
          "Error while finding node service option",
          HttpStatus.INTERNAL_SERVER_ERROR,
          0x1792,
          null);
    }
  }

  public void deleteNodeServiceOptionEntityByOrgIdAndNodeIdAndServiceOption(
      String orgId, String nodeId, String serviceOption) throws CommonServiceException {
    try {
      nodeServiceOptionRepository.deleteByOrgIdAndNodeIdAndServiceOption(
          orgId, nodeId, serviceOption);
    } catch (Exception e) {
      logger.error(String.valueOf(e), "Unable to delete the node service option");
      throw handleException(
          orgId, nodeId, serviceOption, "Error while deleting the node service option", 0x1791);
    }
  }

  private CommonServiceException handleException(
      String orgId, String nodeId, String serviceOption, String errorMessage, int errorCode) {
    Map<String, FieldError> errorMap = new HashMap<>();
    errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
    errorMap.put(NODE_ID, FieldError.builder().rejectedValue(nodeId).build());
    errorMap.put(SERVICE_OPTION, FieldError.builder().rejectedValue(serviceOption).build());
    return new CommonServiceException(
        errorMessage, HttpStatus.INTERNAL_SERVER_ERROR, errorCode, errorMap);
  }
}
