/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.node.carrier.service.impl;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.error.FieldError;
import com.nextuple.common.util.DateValidationUtil;
import com.nextuple.node.carrier.config.NodeCarrierTenantBasedDBConfig;
import com.nextuple.node.carrier.domain.NodeServiceOptionBufferDomain;
import com.nextuple.node.carrier.domain.NodeServiceOptionsDomain;
import com.nextuple.node.carrier.domain.entity.NodeServiceOptionBufferEntity;
import com.nextuple.node.carrier.domain.entity.NodeServiceOptionEntity;
import com.nextuple.node.carrier.domain.inbound.NodeServiceOptionBufferDeleteRequest;
import com.nextuple.node.carrier.domain.inbound.NodeServiceOptionBufferRequest;
import com.nextuple.node.carrier.domain.inbound.NodeServiceOptionBufferUpdateRequest;
import com.nextuple.node.carrier.domain.mapper.NodeServiceOptionBufferMapper;
import com.nextuple.node.carrier.domain.outbound.NodeServiceOptionBufferResponse;
import com.nextuple.node.carrier.service.NodeServiceOptionBufferService;
import com.nextuple.node.domain.feign.NodeFeign;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.mapstruct.factory.Mappers;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class NodeServiceOptionBufferServiceImpl implements NodeServiceOptionBufferService {
  private static final Long NO_EXISTING_ID = null;
  private final NodeServiceOptionBufferDomain nodeServiceOptionBufferDomain;
  private final NodeFeign nodeFeign;
  private final DateValidationUtil dateValidationUtil;
  private final NodeCarrierTenantBasedDBConfig nodeCarrierTenantBasedDBConfig;
  private final NodeServiceOptionsDomain nodeServiceOptionsDomain;
  private final NodeServiceOptionBufferMapper INSTANCE =
      Mappers.getMapper(NodeServiceOptionBufferMapper.class);
  private static final String ORG_ID = "orgId";
  private static final String NODE_ID = "nodeId";
  private static final String SERVICE_OPTION = "serviceOption";
  private static final String BUFFER_START_DATE = "bufferStartDate";
  private static final String BUFFER_END_DATE = "bufferEndDate";
  private static final String BUFFER_HOURS = "bufferHours";

  @Override
  @Transactional
  public NodeServiceOptionBufferResponse createNodeServiceOptionBuffer(
      NodeServiceOptionBufferRequest nodeServiceOptionBufferRequest) throws CommonServiceException {
    validateNodeServiceOption(nodeServiceOptionBufferRequest);
    validateBufferHours(nodeServiceOptionBufferRequest.getBufferHours());
    dateValidationUtil.validateBufferStartAndEndDate(
        nodeServiceOptionBufferRequest.getBufferStartDate(),
        nodeServiceOptionBufferRequest.getBufferEndDate());
    checkOverlapsWithExistingBuffers(
        INSTANCE.toNodeServiceOptionBufferEntity(nodeServiceOptionBufferRequest), NO_EXISTING_ID);
    return INSTANCE.toNodeServiceOptionBufferResponse(
        nodeServiceOptionBufferDomain.saveNodeServiceOptionBufferEntity(
            INSTANCE.toNodeServiceOptionBufferEntity(nodeServiceOptionBufferRequest)));
  }

  private void validateNodeServiceOption(
      NodeServiceOptionBufferRequest nodeServiceOptionBufferRequest) throws CommonServiceException {
    Optional<NodeServiceOptionEntity> existingNodeServiceOptionEntity =
        nodeServiceOptionsDomain.findNodeServiceOptionEntity(
            nodeServiceOptionBufferRequest.getOrgId(),
            nodeServiceOptionBufferRequest.getNodeId(),
            nodeServiceOptionBufferRequest.getServiceOption());
    if (existingNodeServiceOptionEntity.isEmpty()) {
      log.error(
          "Node Service Option details not found for the given buffer by orgId {}, nodeId {} and serviceOption {}",
          nodeServiceOptionBufferRequest.getOrgId(),
          nodeServiceOptionBufferRequest.getNodeId(),
          nodeServiceOptionBufferRequest.getServiceOption());
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(
          NODE_ID,
          FieldError.builder().rejectedValue(nodeServiceOptionBufferRequest.getNodeId()).build());
      errorMap.put(
          ORG_ID,
          FieldError.builder().rejectedValue(nodeServiceOptionBufferRequest.getOrgId()).build());
      errorMap.put(
          SERVICE_OPTION,
          FieldError.builder()
              .rejectedValue(nodeServiceOptionBufferRequest.getServiceOption())
              .build());
      throw new CommonServiceException(
          "Node Service Option details not found for the given buffer",
          HttpStatus.NOT_FOUND,
          0x1779,
          errorMap);
    }
  }

  @Override
  public NodeServiceOptionBufferResponse fetchNodeServiceOptionBuffer(String orgId, Long id)
      throws CommonServiceException {
    Optional<NodeServiceOptionBufferEntity> entity = getNodeServiceOptionBufferEntity(orgId, id);
    return INSTANCE.toNodeServiceOptionBufferResponse(entity.get());
  }

  @Override
  public List<NodeServiceOptionBufferResponse> fetchApplicableNodeServiceOptionBuffers(
      String orgId, String nodeId, String serviceOption, LocalDate requestDate, Integer horizonDays)
      throws CommonServiceException {
    List<NodeServiceOptionBufferEntity> entities =
        nodeServiceOptionBufferDomain.findApplicableBuffers(
            orgId, nodeId, serviceOption, requestDate, requestDate.plusDays(horizonDays));
    return INSTANCE.toNodeServiceOptionBufferResponseList(entities);
  }

  @Override
  public NodeServiceOptionBufferResponse updateNodeServiceOptionBuffer(
      String orgId, Long id, NodeServiceOptionBufferUpdateRequest updateRequest)
      throws CommonServiceException {
    validateBufferHours(updateRequest.getBufferHours());
    Optional<NodeServiceOptionBufferEntity> existingBufferEntity =
        getNodeServiceOptionBufferEntity(orgId, id);
    dateValidationUtil.validateBufferStartAndEndDate(
        updateRequest.getBufferStartDate(), updateRequest.getBufferEndDate());
    if (Objects.nonNull(updateRequest.getBufferStartDate())
        && Objects.nonNull(updateRequest.getBufferEndDate())) {
      NodeServiceOptionBufferEntity updateEntity = new NodeServiceOptionBufferEntity();
      updateEntity.setOrgId(orgId);
      updateEntity.setNodeId(existingBufferEntity.get().getNodeId());
      updateEntity.setServiceOption(existingBufferEntity.get().getServiceOption());
      updateEntity.setBufferStartDate(updateRequest.getBufferStartDate());
      updateEntity.setBufferEndDate(updateRequest.getBufferEndDate());
      checkOverlapsWithExistingBuffers(updateEntity, id);
    }
    INSTANCE.updateNodeServiceOptionBufferEntityFromRequest(
        updateRequest, existingBufferEntity.get());
    return INSTANCE.toNodeServiceOptionBufferResponse(
        nodeServiceOptionBufferDomain.saveNodeServiceOptionBufferEntity(
            existingBufferEntity.get()));
  }

  @Override
  @Transactional
  public NodeServiceOptionBufferResponse deleteNodeServiceOptionBuffer(
      NodeServiceOptionBufferDeleteRequest nodeServiceOptionBufferDeleteRequest)
      throws CommonServiceException {
    Optional<NodeServiceOptionBufferEntity> existingEntity =
        nodeServiceOptionBufferDomain
            .findByOrgIdAndNodeIdAndServiceOptionAndBufferStartDateAndBufferEndDate(
                nodeServiceOptionBufferDeleteRequest.getOrgId(),
                nodeServiceOptionBufferDeleteRequest.getNodeId(),
                nodeServiceOptionBufferDeleteRequest.getServiceOption(),
                nodeServiceOptionBufferDeleteRequest.getBufferStartDate(),
                nodeServiceOptionBufferDeleteRequest.getBufferEndDate());
    if (existingEntity.isEmpty()) {
      throwEntityNotFoundException(nodeServiceOptionBufferDeleteRequest);
    }
    nodeServiceOptionBufferDomain.deleteByOrdIdAndId(
        existingEntity.get().getOrgId(), existingEntity.get().getId());
    return INSTANCE.toNodeServiceOptionBufferResponse(existingEntity.get());
  }

  @Override
  @Transactional
  public NodeServiceOptionBufferResponse deleteNodeServiceOptionBufferByOrgIdAndId(
      String orgId, Long id) throws CommonServiceException {
    Optional<NodeServiceOptionBufferEntity> existingEntity =
        getNodeServiceOptionBufferEntity(orgId, id);
    nodeServiceOptionBufferDomain.deleteByOrdIdAndId(
        existingEntity.get().getOrgId(), existingEntity.get().getId());
    return INSTANCE.toNodeServiceOptionBufferResponse(existingEntity.get());
  }

  @Override
  public List<NodeServiceOptionBufferResponse> getBuffersByOrgIdAndNodeIdAndServiceOption(
      String orgId, String nodeId, String serviceOption) throws CommonServiceException {
    List<NodeServiceOptionBufferEntity> entities =
        nodeServiceOptionBufferDomain.findByOrgIdAndNodeIdAndServiceOption(
            orgId, nodeId, serviceOption);
    return INSTANCE.toNodeServiceOptionBufferResponseList(entities);
  }

  private Optional<NodeServiceOptionBufferEntity> getNodeServiceOptionBufferEntity(
      String orgId, Long id) throws CommonServiceException {
    Optional<NodeServiceOptionBufferEntity> entity =
        nodeServiceOptionBufferDomain.findNodeServiceOptionBufferEntityByOrgIdAndId(orgId, id);
    if (entity.isEmpty()) {
      throwEntityNotFoundException(orgId, id);
    }
    return entity;
  }

  private void checkOverlapsWithExistingBuffers(
      NodeServiceOptionBufferEntity nodeServiceOptionBufferEntity, Long existingId)
      throws CommonServiceException {
    List<NodeServiceOptionBufferEntity> entities =
        nodeServiceOptionBufferDomain.findByOrgIdAndNodeIdAndServiceOption(
            nodeServiceOptionBufferEntity.getOrgId(),
            nodeServiceOptionBufferEntity.getNodeId(),
            nodeServiceOptionBufferEntity.getServiceOption());
    boolean hasOverlap =
        entities.stream()
            .filter(entity -> Objects.isNull(existingId) || !entity.getId().equals(existingId))
            .anyMatch(
                entity ->
                    isBufferWindowOverlap(
                        entity,
                        nodeServiceOptionBufferEntity.getBufferStartDate(),
                        nodeServiceOptionBufferEntity.getBufferEndDate()));
    if (hasOverlap) {
      throwOverlappingBuffersException(
          nodeServiceOptionBufferEntity.getBufferStartDate(),
          nodeServiceOptionBufferEntity.getBufferEndDate());
    }
  }

  private void validateBufferHours(Double bufferHours) throws CommonServiceException {
    if (bufferHours != null && bufferHours < 0) {
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(BUFFER_HOURS, FieldError.builder().rejectedValue(bufferHours).build());
      throw new CommonServiceException(
          "bufferHours cannot be negative", HttpStatus.BAD_REQUEST, 0x1780, null);
    }
  }

  private boolean isBufferWindowOverlap(
      NodeServiceOptionBufferEntity existingBuffer,
      Date newBufferStartDate,
      Date newBufferEndDate) {

    DateTime newStart = new DateTime(newBufferStartDate, DateTimeZone.UTC);
    DateTime newEnd = new DateTime(newBufferEndDate, DateTimeZone.UTC);

    DateTime existingStart = new DateTime(existingBuffer.getBufferStartDate(), DateTimeZone.UTC);
    DateTime existingEnd = new DateTime(existingBuffer.getBufferEndDate(), DateTimeZone.UTC);

    boolean startOverlap = newStart.isBefore(existingEnd) && newEnd.isAfter(existingStart);
    boolean endOverlap = newEnd.isAfter(existingStart) && newStart.isBefore(existingEnd);
    boolean containsNewBuffer = existingStart.isBefore(newStart) && existingEnd.isAfter(newEnd);
    boolean containedInNewBuffer = newStart.isBefore(existingStart) && newEnd.isAfter(existingEnd);

    boolean sameStartAsExistingEnd = newStart.isEqual(existingEnd);
    boolean sameEndAsExistingStart = newEnd.isEqual(existingStart);
    boolean sameWindow = newStart.isEqual(existingStart) && newEnd.isEqual(existingEnd);

    return startOverlap
        || endOverlap
        || containsNewBuffer
        || containedInNewBuffer
        || sameStartAsExistingEnd
        || sameEndAsExistingStart
        || sameWindow;
  }

  private static void throwEntityNotFoundException(String orgId, Long id)
      throws CommonServiceException {
    Map<String, FieldError> errorMap = new HashMap<>();
    errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
    errorMap.put("id", FieldError.builder().rejectedValue(id).build());
    throw new CommonServiceException(
        "Node service option buffer not found for given orgId and Id",
        HttpStatus.NOT_FOUND,
        0x1781,
        errorMap);
  }

  private static void throwEntityNotFoundException(
      NodeServiceOptionBufferDeleteRequest nodeServiceOptionBufferDeleteRequest)
      throws CommonServiceException {
    Map<String, FieldError> errorMap = new HashMap<>();
    errorMap.put(
        ORG_ID,
        FieldError.builder()
            .rejectedValue(nodeServiceOptionBufferDeleteRequest.getOrgId())
            .build());
    errorMap.put(
        NODE_ID,
        FieldError.builder()
            .rejectedValue(nodeServiceOptionBufferDeleteRequest.getNodeId())
            .build());
    errorMap.put(
        SERVICE_OPTION,
        FieldError.builder()
            .rejectedValue(nodeServiceOptionBufferDeleteRequest.getServiceOption())
            .build());
    errorMap.put(
        BUFFER_START_DATE,
        FieldError.builder()
            .rejectedValue(nodeServiceOptionBufferDeleteRequest.getBufferStartDate())
            .build());
    errorMap.put(
        BUFFER_END_DATE,
        FieldError.builder()
            .rejectedValue(nodeServiceOptionBufferDeleteRequest.getBufferEndDate())
            .build());
    throw new CommonServiceException(
        "Node service option buffer not found for given details",
        HttpStatus.NOT_FOUND,
        0x1782,
        errorMap);
  }

  private static void throwOverlappingBuffersException(Date bufferStartDate, Date bufferEndDate)
      throws CommonServiceException {
    Map<String, FieldError> errorMap = new HashMap<>();
    errorMap.put(BUFFER_START_DATE, FieldError.builder().rejectedValue(bufferStartDate).build());
    errorMap.put(BUFFER_END_DATE, FieldError.builder().rejectedValue(bufferEndDate).build());
    throw new CommonServiceException(
        "Node Service Option Buffer window already exists or overlaps",
        HttpStatus.PRECONDITION_FAILED,
        0x1783,
        errorMap);
  }
}
