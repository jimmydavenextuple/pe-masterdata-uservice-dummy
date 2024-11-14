/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.node.carrier.domain;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.node.carrier.domain.entity.NodeServiceOptionBufferEntity;
import com.nextuple.node.carrier.repository.NodeServiceOptionBufferRepository;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NodeServiceOptionBufferDomain {
  private static final String ERROR_WHILE_SAVING =
      "Error while saving the node service option buffer entity";
  private static final String ERROR_WHILE_FETCHING =
      "Error while fetching the node service option buffer entity";
  private static final String ERROR_WHILE_DELETING =
      "Error while deleting the node service option buffer entity";
  private final NodeServiceOptionBufferRepository nodeServiceOptionBufferRepository;

  public NodeServiceOptionBufferEntity saveNodeServiceOptionBufferEntity(
      NodeServiceOptionBufferEntity entity) throws CommonServiceException {
    try {
      return nodeServiceOptionBufferRepository.save(entity);
    } catch (Exception e) {
      log.error(String.valueOf(e), ERROR_WHILE_SAVING);
      throw new CommonServiceException(
          ERROR_WHILE_SAVING, HttpStatus.INTERNAL_SERVER_ERROR, 0x1870, null);
    }
  }

  public Optional<NodeServiceOptionBufferEntity> findNodeServiceOptionBufferEntityByOrgIdAndId(
      String orgId, Long id) throws CommonServiceException {
    try {
      return nodeServiceOptionBufferRepository.findByOrgIdAndId(orgId, id);
    } catch (Exception e) {
      log.error(String.valueOf(e), ERROR_WHILE_FETCHING);
      throw new CommonServiceException(
          ERROR_WHILE_FETCHING, HttpStatus.INTERNAL_SERVER_ERROR, 0x1871, null);
    }
  }

  public Optional<NodeServiceOptionBufferEntity>
      findByOrgIdAndNodeIdAndServiceOptionAndBufferStartDateAndBufferEndDate(
          String orgId,
          String nodeId,
          String serviceOption,
          Date bufferStartDate,
          Date bufferEndDate)
          throws CommonServiceException {
    try {
      return nodeServiceOptionBufferRepository
          .findByOrgIdAndNodeIdAndServiceOptionAndBufferStartDateAndBufferEndDate(
              orgId, nodeId, serviceOption, bufferStartDate, bufferEndDate);
    } catch (Exception e) {
      log.error(String.valueOf(e), ERROR_WHILE_FETCHING);
      throw new CommonServiceException(
          ERROR_WHILE_FETCHING, HttpStatus.INTERNAL_SERVER_ERROR, 0x1872, null);
    }
  }

  public List<NodeServiceOptionBufferEntity> findByOrgIdAndNodeIdAndServiceOption(
      String orgId, String nodeId, String serviceOption) throws CommonServiceException {
    try {
      return nodeServiceOptionBufferRepository.findByOrgIdAndNodeIdAndServiceOption(
          orgId, nodeId, serviceOption);
    } catch (Exception e) {
      log.error(String.valueOf(e), ERROR_WHILE_FETCHING);
      throw new CommonServiceException(
          ERROR_WHILE_FETCHING, HttpStatus.INTERNAL_SERVER_ERROR, 0x1873, null);
    }
  }

  public void deleteByOrdIdAndId(String orgId, Long id) throws CommonServiceException {
    try {
      nodeServiceOptionBufferRepository.deleteByOrgIdAndId(orgId, id);
    } catch (Exception e) {
      log.error(String.valueOf(e), ERROR_WHILE_DELETING);
      throw new CommonServiceException(
          ERROR_WHILE_DELETING, HttpStatus.INTERNAL_SERVER_ERROR, 0x1874, null);
    }
  }

  public List<NodeServiceOptionBufferEntity> findApplicableBuffers(
      String orgId,
      String nodeId,
      String serviceOption,
      LocalDate requestDate,
      LocalDate requestDatePlusHorizon)
      throws CommonServiceException {
    try {
      return nodeServiceOptionBufferRepository.findApplicableBuffers(
          orgId, nodeId, serviceOption, requestDate, requestDatePlusHorizon);
    } catch (Exception e) {
      log.error(String.valueOf(e), ERROR_WHILE_FETCHING);
      throw new CommonServiceException(
          ERROR_WHILE_FETCHING, HttpStatus.INTERNAL_SERVER_ERROR, 0x1875, null);
    }
  }
}
