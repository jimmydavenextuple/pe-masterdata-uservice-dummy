/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.node.carrier.domain;

import static com.nextuple.common.constants.CommonConstants.CARRIER_SERVICE_ID;
import static com.nextuple.common.constants.CommonConstants.NODE_ID;
import static com.nextuple.common.constants.CommonConstants.ORG_ID;
import static com.nextuple.common.constants.CommonConstants.SERVICE_OPTION;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.error.FieldError;
import com.nextuple.node.carrier.domain.entity.NodeCarriersEntity;
import com.nextuple.node.carrier.repository.NodeCarriersRepository;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NodeCarriersDomain {

  private static final Logger logger = LoggerFactory.getLogger(NodeCarriersDomain.class);
  private final NodeCarriersRepository nodeCarriersRepository;

  public NodeCarriersEntity saveNodeCarrierEntity(NodeCarriersEntity nodeCarriersEntity)
      throws CommonServiceException {
    try {
      return nodeCarriersRepository.save(nodeCarriersEntity);
    } catch (Exception e) {
      logger.error(String.valueOf(e), "Error while creating node carrier");
      throw handleException(
          nodeCarriersEntity.getOrgId(),
          nodeCarriersEntity.getNodeId(),
          nodeCarriersEntity.getCarrierServiceId(),
          nodeCarriersEntity.getServiceOption(),
          "Error while saving the node carrier",
          0x1783);
    }
  }

  public void deleteNodeCarrierEntityByNodeIdAndOrgIdAndCarrierServiceIdAndServiceOption(
      String orgId, String nodeId, String carrierServiceId, String serviceOption)
      throws CommonServiceException {
    try {
      nodeCarriersRepository.deleteByOrgIdAndNodeIdAndCarrierServiceIdAndServiceOption(
          orgId, nodeId, carrierServiceId, serviceOption);
    } catch (Exception e) {
      logger.error(String.valueOf(e), "Unable to delete node carrier");
      throw handleException(
          orgId,
          nodeId,
          carrierServiceId,
          serviceOption,
          "Error while deleting the node carrier",
          0x1784);
    }
  }

  public List<NodeCarriersEntity> deleteAllNodeCarrierEntityByOrgIdAndNodeId(
      String orgId, String nodeId) throws CommonServiceException {
    try {
      return nodeCarriersRepository.deleteAllByOrgIdAndNodeId(orgId, nodeId);
    } catch (Exception e) {
      logger.error(String.valueOf(e), "Unable to delete node carrier");
      throw new CommonServiceException(
          "Error while deleting the node carrier", HttpStatus.INTERNAL_SERVER_ERROR, 0x1787, null);
    }
  }

  public Optional<NodeCarriersEntity> filterAndGetNodeCarrierDetails(
      String orgId, String nodeId, String carrierServiceId, String serviceOption)
      throws CommonServiceException {
    try {
      return nodeCarriersRepository.findByOrgIdAndNodeIdAndCarrierServiceIdAndServiceOption(
          orgId, nodeId, carrierServiceId, serviceOption);
    } catch (Exception e) {
      logger.error(String.valueOf(e), "Unable to find node carrier details");
      throw handleException(
          orgId,
          nodeId,
          carrierServiceId,
          serviceOption,
          "Error while finding the node carrier",
          0x1785);
    }
  }

  public Optional<NodeCarriersEntity> findNodeCarrierDetails(
      String orgId, String nodeId, String carrierServiceId, String serviceOption)
      throws CommonServiceException {
    try {
      return nodeCarriersRepository.findByOrgIdAndNodeIdAndCarrierServiceIdAndServiceOption(
          orgId, nodeId, carrierServiceId, serviceOption);
    } catch (Exception e) {
      logger.error(String.valueOf(e), "Unable to find node carrier details");
      throw handleException(
          orgId,
          nodeId,
          carrierServiceId,
          serviceOption,
          "Error while finding the node carrier",
          0x1786);
    }
  }

  public List<NodeCarriersEntity> findByOrgIdAndNodeId(String orgId, String nodeId)
      throws CommonServiceException {
    try {
      return nodeCarriersRepository.findByOrgIdAndNodeId(orgId, nodeId);
    } catch (Exception e) {
      logger.error(String.valueOf(e), "Unable to find node carrier details");
      throw new CommonServiceException(
          "Error while finding the node carrier", HttpStatus.INTERNAL_SERVER_ERROR, 0x1787, null);
    }
  }

  public List<NodeCarriersEntity> findNodeCarriersMandatoryByOrgIdAndNodeId(
      String orgId, String nodeId) {
    try {
      return nodeCarriersRepository.findByOrgIdAndNodeId(orgId, nodeId);
    } catch (Exception e) {
      logger.error(String.valueOf(e), "Unable to find node carrier details");
      return Collections.emptyList();
    }
  }

  public List<NodeCarriersEntity> findNodeCarriersListByOrgIdAndNodeIdAndServiceOption(
      String orgId, String nodeId, String serviceOption) throws CommonServiceException {
    try {
      return nodeCarriersRepository.findByOrgIdAndNodeIdAndServiceOption(
          nodeId, orgId, serviceOption);
    } catch (Exception e) {
      logger.error(String.valueOf(e), "Unable to find a node carriers for given combination");
      throw new CommonServiceException("Error while finding the node carriers", null, null, null);
    }
  }

  public List<NodeCarriersEntity> getAllNodeCarriers(Integer limit) throws CommonServiceException {
    try {
      Pageable pageable = PageRequest.of(0, limit);
      Page<NodeCarriersEntity> pageResult = nodeCarriersRepository.findAll(pageable);
      return pageResult.getContent();
    } catch (Exception e) {
      logger.error(String.valueOf(e), "Unable to find all node carriers");
      throw new CommonServiceException(
          "Error while fetching node carriers list", null, null, null, null);
    }
  }

  private CommonServiceException handleException(
      String orgId,
      String nodeId,
      String carrierServiceId,
      String serviceOption,
      String errorMessage,
      int errorCode)
      throws CommonServiceException {
    Map<String, FieldError> errorMap = new HashMap<>();
    errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
    errorMap.put(NODE_ID, FieldError.builder().rejectedValue(nodeId).build());
    errorMap.put(CARRIER_SERVICE_ID, FieldError.builder().rejectedValue(carrierServiceId).build());
    errorMap.put(SERVICE_OPTION, FieldError.builder().rejectedValue(serviceOption).build());
    throw new CommonServiceException(
        errorMessage, HttpStatus.INTERNAL_SERVER_ERROR, errorCode, errorMap);
  }

  public List<NodeCarriersEntity> getAllNodeCarriersByOrgIdCarrierServiceId(
      String orgId, String carrierServiceId) throws CommonServiceException {
    try {
      return nodeCarriersRepository.findByOrgIdAndCarrierServiceId(orgId, carrierServiceId);
    } catch (Exception e) {
      logger.error(
          String.valueOf(e),
          "Error while fetching node carrier details for orgId and carrierServiceId");
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(
          CARRIER_SERVICE_ID, FieldError.builder().rejectedValue(carrierServiceId).build());
      throw new CommonServiceException(
          "Error while fetching node carrier details for orgId and carrierServiceId",
          HttpStatus.INTERNAL_SERVER_ERROR,
          0x1793,
          errorMap);
    }
  }

  public List<NodeCarriersEntity> getAllNodeCarriersByOrgIdNodeIdAndCarrierServiceId(
      String orgId, String nodeId, String carrierServiceId) throws CommonServiceException {
    try {
      return nodeCarriersRepository.findByOrgIdAndNodeIdAndCarrierServiceId(
          orgId, nodeId, carrierServiceId);
    } catch (Exception e) {
      logger.error(
          String.valueOf(e),
          "Error while fetching node carrier details for orgId, nodeId and carrierServiceId");
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(NODE_ID, FieldError.builder().rejectedValue(nodeId).build());
      errorMap.put(
          CARRIER_SERVICE_ID, FieldError.builder().rejectedValue(carrierServiceId).build());
      throw new CommonServiceException(
          "Error while fetching node carrier details for orgId, nodeId and carrierServiceId",
          HttpStatus.INTERNAL_SERVER_ERROR,
          0x1793,
          errorMap);
    }
  }
}
