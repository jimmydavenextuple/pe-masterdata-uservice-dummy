/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.promise.sourcing.rule.service;

import com.nextuple.common.context.Logger;
import com.nextuple.common.context.LoggerFactory;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.common.response.error.FieldError;
import com.nextuple.promise.sourcing.rule.api.domain.enums.SourcingConstraintEnum;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.SourcingConstraintRequest;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.SourcingConstraintUpdationRequest;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.SourcingConstraintDetailsResponse;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.SourcingConstraintsResponse;
import com.nextuple.promise.sourcing.rule.api.domain.pojo.SourcingConstraintInfo;
import com.nextuple.promise.sourcing.rule.domain.mapper.SourcingConstraintMapper;
import com.nextuple.promise.sourcing.rule.persistence.domain.GroupDefinitionDomainDto;
import com.nextuple.promise.sourcing.rule.persistence.domain.SourcingConstraintDomainDto;
import com.nextuple.promise.sourcing.rule.persistence.service.GroupDefinitionPersistenceService;
import com.nextuple.promise.sourcing.rule.persistence.service.SourcingConstraintPersistenceService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SourcingConstraintService {

  private static final Logger logger = LoggerFactory.getLogger(SourcingConstraintService.class);

  private static final String ID = "id";
  private static final String GROUP_ID = "groupId";
  private static final String ORG_ID = "orgId";
  private static final String SOURCING_CONSTRAINT = "sourcingConstraint";
  private static final String SOURCING_CONSTRAINT_VALUE = "sourcingConstraintValue";

  private static final List<String> ALLOWED_CONSTRAINT_VALUES = List.of("0", "1");
  private static final SourcingConstraintMapper INSTANCE =
      Mappers.getMapper(SourcingConstraintMapper.class);

  private final SourcingConstraintPersistenceService sourcingConstraintPersistenceService;
  private final GroupDefinitionPersistenceService groupDefinitionPersistenceService;

  private static final String SOURCING_CONSTRAINT_EXCEPTION_MESSAGE =
      "Sourcing Constraint not found";
  private static final String DEFAULT_GROUP_ID = "DEFAULT";

  public SourcingConstraintDetailsResponse processAddSourcingConstraint(
      SourcingConstraintRequest sourcingConstraintRequest)
      throws CommonServiceException, PromiseEngineException {

    validateGroupIdDetails(
        sourcingConstraintRequest.getGroupId(), sourcingConstraintRequest.getOrgId());
    List<SourcingConstraintDomainDto> sourcingConstraintDomainDtoList =
        sourcingConstraintPersistenceService.fetchByOrgIdAndGroupIdAndConstraint(
            sourcingConstraintRequest.getOrgId(),
            sourcingConstraintRequest.getGroupId(),
            sourcingConstraintRequest.getSourcingConstraint());

    if (!sourcingConstraintDomainDtoList.isEmpty()) {
      logger.error(
          "This constraint is already defined for given orgId :{}",
          sourcingConstraintRequest.getOrgId());
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(
          ORG_ID, FieldError.builder().rejectedValue(sourcingConstraintRequest.getOrgId()).build());
      throw new CommonServiceException(
          "This constraint is already defined for given orgId",
          HttpStatus.BAD_REQUEST,
          0x1771,
          errorMap);
    }

    validateSourcingConstraintValue(sourcingConstraintRequest.getSourcingConstraintValue());
    var sourcingConstraintEntity = INSTANCE.toSourcingConstraintEntity(sourcingConstraintRequest);
    return INSTANCE.toSourcingConstraintDetailsResponse(
        sourcingConstraintPersistenceService.saveSourcingConstraintEntity(
            sourcingConstraintEntity));
  }

  private void validateGroupIdDetails(String groupId, String orgId)
      throws PromiseEngineException, CommonServiceException {
    Optional<GroupDefinitionDomainDto> groupDefinitionDomainDto =
        groupDefinitionPersistenceService.fetchGroupDefinitionByIdAndOrgId(
            Long.parseLong(groupId), orgId);
    if (groupDefinitionDomainDto.isEmpty()) {
      logger.error("Group Definition details not found");
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(GROUP_ID, FieldError.builder().rejectedValue(groupId).build());
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      throw new CommonServiceException(
          "Group Definition details not found for the given groupId and orgId",
          HttpStatus.BAD_REQUEST,
          0x1771,
          errorMap);
    }
  }

  private void validateSourcingConstraintValue(String sourcingConstraintValue)
      throws CommonServiceException {

    if (!ALLOWED_CONSTRAINT_VALUES.contains(sourcingConstraintValue)) {
      logger.error("Invalid constraint value :{}", sourcingConstraintValue);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(
          SOURCING_CONSTRAINT_VALUE,
          FieldError.builder().rejectedValue(sourcingConstraintValue).build());
      throw new CommonServiceException(
          "Invalid constraint value", HttpStatus.BAD_REQUEST, 0x1771, errorMap);
    }
  }

  public SourcingConstraintsResponse processFetchSourcingConstraintsList(
      String orgId, String groupId) throws PromiseEngineException {

    List<SourcingConstraintDomainDto> sourcingConstraintDomainDtoList =
        sourcingConstraintPersistenceService.fetchByOrgIdAndGroupId(orgId, groupId);
    if (sourcingConstraintDomainDtoList.isEmpty()) {
      List<SourcingConstraintDomainDto> defaultConstraintList =
          sourcingConstraintPersistenceService.fetchByOrgIdAndGroupId(orgId, DEFAULT_GROUP_ID);
      if (!defaultConstraintList.isEmpty())
        return prepareSourcingConstraintsResponse(orgId, DEFAULT_GROUP_ID, defaultConstraintList);
    }

    return prepareSourcingConstraintsResponse(orgId, groupId, sourcingConstraintDomainDtoList);
  }

  private SourcingConstraintsResponse prepareSourcingConstraintsResponse(
      String orgId,
      String groupId,
      List<SourcingConstraintDomainDto> sourcingConstraintDomainDtoList) {
    List<SourcingConstraintInfo> sourcingConstraintInfoList = new ArrayList<>();
    if (!sourcingConstraintDomainDtoList.isEmpty()) {
      for (SourcingConstraintDomainDto sourcingConstraintDomainDto :
          sourcingConstraintDomainDtoList) {
        SourcingConstraintInfo sourcingConstraintInfo = new SourcingConstraintInfo();
        sourcingConstraintInfo.setSourcingConstraint(
            sourcingConstraintDomainDto.getSourcingConstraint());
        sourcingConstraintInfo.setSourcingConstraintValue(
            sourcingConstraintDomainDto.getSourcingConstraintValue());
        sourcingConstraintInfoList.add(sourcingConstraintInfo);
      }
    }

    return SourcingConstraintsResponse.builder()
        .orgId(orgId)
        .groupId(groupId)
        .sourcingConstraintsInfo(sourcingConstraintInfoList)
        .build();
  }

  public SourcingConstraintDetailsResponse processFetchSourcingConstraintsByIdAndOrgId(
      Long id, String orgId) throws PromiseEngineException, CommonServiceException {
    Optional<SourcingConstraintDomainDto> sourcingConstraintDomainDto =
        sourcingConstraintPersistenceService.getSourcingConstraintEntityByIdAndOrgId(id, orgId);
    if (sourcingConstraintDomainDto.isEmpty()) {
      logger.error(SOURCING_CONSTRAINT_EXCEPTION_MESSAGE);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ID, FieldError.builder().rejectedValue(id).build());
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      throw new CommonServiceException(
          SOURCING_CONSTRAINT_EXCEPTION_MESSAGE, HttpStatus.NOT_FOUND, 0x1771, errorMap);
    }
    return INSTANCE.toSourcingConstraintDetailsResponse(sourcingConstraintDomainDto.get());
  }

  public SourcingConstraintDetailsResponse processUpdateSourcingConstraint(
      String orgId,
      SourcingConstraintEnum sourcingConstraint,
      SourcingConstraintUpdationRequest updationRequest,
      String groupId)
      throws PromiseEngineException, CommonServiceException {
    validateGroupIdDetails(groupId, orgId);
    List<SourcingConstraintDomainDto> sourcingConstraintDomainDtoList =
        sourcingConstraintPersistenceService.fetchByOrgIdAndGroupIdAndConstraint(
            orgId, groupId, sourcingConstraint);
    if (sourcingConstraintDomainDtoList.isEmpty()) {
      logger.error(SOURCING_CONSTRAINT_EXCEPTION_MESSAGE);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(
          SOURCING_CONSTRAINT, FieldError.builder().rejectedValue(sourcingConstraint).build());
      throw new CommonServiceException(
          SOURCING_CONSTRAINT_EXCEPTION_MESSAGE, HttpStatus.NOT_FOUND, 0x1771, errorMap);
    }
    validateSourcingConstraintValue(updationRequest.getSourcingConstraintValue());
    INSTANCE.updateSourcingConstraint(updationRequest, sourcingConstraintDomainDtoList.get(0));
    return INSTANCE.toSourcingConstraintDetailsResponse(
        sourcingConstraintPersistenceService.saveSourcingConstraintEntity(
            sourcingConstraintDomainDtoList.get(0)));
  }

  public SourcingConstraintDetailsResponse processDeleteSourcingConstraintDetails(
      String orgId, SourcingConstraintEnum sourcingConstraint, String groupId)
      throws PromiseEngineException, CommonServiceException {
    List<SourcingConstraintDomainDto> sourcingConstraintEntity =
        sourcingConstraintPersistenceService.fetchByOrgIdAndGroupIdAndConstraint(
            orgId, groupId, sourcingConstraint);
    if (sourcingConstraintEntity.isEmpty()) {
      logger.error(SOURCING_CONSTRAINT_EXCEPTION_MESSAGE);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(
          SOURCING_CONSTRAINT, FieldError.builder().rejectedValue(sourcingConstraint).build());
      throw new CommonServiceException(
          SOURCING_CONSTRAINT_EXCEPTION_MESSAGE, HttpStatus.NOT_FOUND, 0x1771, errorMap);
    }
    var sourcingConstraintDetails =
        INSTANCE.toSourcingConstraintDetailsResponse(sourcingConstraintEntity.get(0));
    sourcingConstraintPersistenceService.deleteSourcingConstraint(sourcingConstraintEntity.get(0));
    return sourcingConstraintDetails;
  }
}
