/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.promise.sourcing.rule.service;

import static com.nextuple.promise.sourcing.rule.utils.PromiseSourcingRuleUtil.validateAttributeValuesFormat;
import static com.nextuple.promise.sourcing.rule.utils.PromiseSourcingRuleUtil.validateSourcingAttributesDefinitionId;

import com.nextuple.common.context.Logger;
import com.nextuple.common.context.LoggerFactory;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.common.response.error.FieldError;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.GroupDefinitionRequest;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.GroupDefinitionListResponse;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.GroupDefinitionResponse;
import com.nextuple.promise.sourcing.rule.api.domain.pojo.GroupDefinitionInfo;
import com.nextuple.promise.sourcing.rule.domain.mapper.GroupDefinitionMapper;
import com.nextuple.promise.sourcing.rule.persistence.domain.GroupDefinitionDomainDto;
import com.nextuple.promise.sourcing.rule.persistence.domain.NamedOptimizationStrategyDomainDto;
import com.nextuple.promise.sourcing.rule.persistence.domain.SourcingAttributesDefinitionDomainDto;
import com.nextuple.promise.sourcing.rule.persistence.service.GroupDefinitionPersistenceService;
import com.nextuple.promise.sourcing.rule.persistence.service.NamedOptimizationStrategyPersistenceService;
import com.nextuple.promise.sourcing.rule.persistence.service.SourcingAttributesDefinitionPersistenceService;
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
public class GroupDefinitionService {

  private static final Logger logger = LoggerFactory.getLogger(GroupDefinitionService.class);

  private static final String ORG_ID = "orgId";
  private static final String GROUP_NAME = "groupName";

  private static final String ID = "id";

  private static final String SOURCING_ATTRIBUTES_DEFINITION_ID = "sourcingAttributesDefinitionId";
  private static final String REQ_ATTRIBUTES_VALUE = "reqAttributesValue";
  private static final String GROUP_DEFINITION_EXCEPTION_MESSAGE = "Group definition not found";
  private static final String STATUS_INACTIVE_MESSAGE =
      "Can't add the group definition as all the required attributes values are not present";

  private static final GroupDefinitionMapper INSTANCE =
      Mappers.getMapper(GroupDefinitionMapper.class);
  private final GroupDefinitionPersistenceService groupDefinitionPersistenceService;
  private final SourcingAttributesDefinitionPersistenceService
      sourcingAttributesDefinitionPersistenceService;

  private final NamedOptimizationStrategyPersistenceService
      namedOptimizationStrategyPersistenceService;

  public GroupDefinitionResponse processAddGroupDefinition(
      GroupDefinitionRequest groupDefinitionRequest)
      throws PromiseEngineException, CommonServiceException {
    logger.debug("-- inside processAddGroupDefinition service --");
    List<GroupDefinitionDomainDto> groupDefinitionDomainDtos =
        groupDefinitionPersistenceService
            .fetchGroupDefinitionListByOrgIdAndSourcingAttributesDefinitionIdAndReqAttributesValue(
                groupDefinitionRequest.getOrgId(),
                groupDefinitionRequest.getSourcingAttributesDefinitionId(),
                groupDefinitionRequest.getReqAttributesValue());
    if (!groupDefinitionDomainDtos.isEmpty()) {
      logger.error(
          "Group already exists for given orgId :{} , sourcingAttributesDefinitionId : {} and reqAttributesValue :{}",
          groupDefinitionRequest.getOrgId(),
          groupDefinitionRequest.getSourcingAttributesDefinitionId(),
          groupDefinitionRequest.getReqAttributesValue());

      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(
          ORG_ID, FieldError.builder().rejectedValue(groupDefinitionRequest.getOrgId()).build());
      errorMap.put(
          REQ_ATTRIBUTES_VALUE,
          FieldError.builder()
              .rejectedValue(groupDefinitionRequest.getReqAttributesValue())
              .build());
      errorMap.put(
          SOURCING_ATTRIBUTES_DEFINITION_ID,
          FieldError.builder()
              .rejectedValue(groupDefinitionRequest.getSourcingAttributesDefinitionId())
              .build());
      throw new CommonServiceException(
          "Group already exist for given orgId , sourcingAttributesDefinitionId and reqAttributesValue",
          HttpStatus.BAD_REQUEST,
          0X1771,
          errorMap);
    }
    validateGroupName(groupDefinitionRequest.getOrgId(), groupDefinitionRequest.getGroupName());
    Optional<SourcingAttributesDefinitionDomainDto> existingSourcingAttributesDefinitionEntity =
        sourcingAttributesDefinitionPersistenceService
            .getSourcingRuleAttributesDefinitionEntityByIdAndOrgId(
                groupDefinitionRequest.getSourcingAttributesDefinitionId(),
                groupDefinitionRequest.getOrgId());
    validateSourcingAttributesDefinitionId(
        groupDefinitionRequest.getReqAttributesValue(),
        existingSourcingAttributesDefinitionEntity,
        groupDefinitionRequest.getSourcingAttributesDefinitionId(),
        STATUS_INACTIVE_MESSAGE);
    var groupDefinition = INSTANCE.toGroupDefinitionEntity(groupDefinitionRequest);
    return INSTANCE.toGroupDefinitionResponse(
        groupDefinitionPersistenceService.saveGroupDefinition(groupDefinition));
  }

  private void validateGroupName(String orgId, String groupName)
      throws CommonServiceException, PromiseEngineException {
    List<GroupDefinitionDomainDto> groupDefinitionDomainDtos =
        groupDefinitionPersistenceService.fetchGroupDefinitionListByOrgIdAndName(orgId, groupName);
    if (!groupDefinitionDomainDtos.isEmpty()) {
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(GROUP_NAME, FieldError.builder().rejectedValue(groupName).build());
      throw new CommonServiceException(
          "Combination of orgId and groupName should be unique",
          HttpStatus.BAD_REQUEST,
          0X1771,
          errorMap);
    }
  }

  public GroupDefinitionResponse processGetGroupDefinitionByIdAndOrgId(Long id, String orgId)
      throws PromiseEngineException, CommonServiceException {
    logger.debug("-- inside fetchGroupDefinitionById service --");
    Optional<GroupDefinitionDomainDto> groupDefinitionDomainDto =
        groupDefinitionPersistenceService.fetchGroupDefinitionByIdAndOrgId(id, orgId);
    if (groupDefinitionDomainDto.isEmpty()) {
      logger.error(GROUP_DEFINITION_EXCEPTION_MESSAGE);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ID, FieldError.builder().rejectedValue(id).build());
      throw new CommonServiceException(
          GROUP_DEFINITION_EXCEPTION_MESSAGE, HttpStatus.NOT_FOUND, 0x1771, errorMap);
    }

    return INSTANCE.toGroupDefinitionResponse(groupDefinitionDomainDto.get());
  }

  public GroupDefinitionListResponse
      processGetGroupDefinitionListByOrgIdAndSourcingAttributesDefinitionId(
          String orgId, Long sourcingAttributesDefinitionId)
          throws PromiseEngineException, CommonServiceException {
    logger.debug(
        "-- inside processGetGroupDefinitionListByOrgIdAndSourcingAttributesDefinitionId service --");
    List<GroupDefinitionDomainDto> groupDefinitionDomainDtos =
        groupDefinitionPersistenceService
            .fetchGroupDefinitionListByOrgIdAndSourcingAttributesDefinitionId(
                orgId, sourcingAttributesDefinitionId);
    List<GroupDefinitionInfo> groupDefinitionInfoList = new ArrayList<>();
    if (groupDefinitionDomainDtos.isEmpty()) {
      logger.error(GROUP_DEFINITION_EXCEPTION_MESSAGE);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ID, FieldError.builder().rejectedValue(orgId).build());
      throw new CommonServiceException(
          GROUP_DEFINITION_EXCEPTION_MESSAGE, HttpStatus.BAD_REQUEST, 0x1771, errorMap);
    }
    for (GroupDefinitionDomainDto groupDefinitionDomainDto : groupDefinitionDomainDtos) {
      GroupDefinitionInfo groupDefinitionInfo = new GroupDefinitionInfo();
      groupDefinitionInfo.setId(groupDefinitionDomainDto.getId());
      groupDefinitionInfo.setGroupName(groupDefinitionDomainDto.getGroupName());
      groupDefinitionInfo.setReqAttributesValue(groupDefinitionDomainDto.getReqAttributesValue());
      groupDefinitionInfoList.add(groupDefinitionInfo);
    }

    return GroupDefinitionListResponse.builder()
        .orgId(orgId)
        .sourcingAttributesDefinitionId(sourcingAttributesDefinitionId)
        .groupDefinitionInfoList(groupDefinitionInfoList)
        .build();
  }

  public GroupDefinitionResponse processUpdateGroupDefinition(
      GroupDefinitionRequest groupDefinitionRequest)
      throws PromiseEngineException, CommonServiceException {
    logger.debug("-- inside processUpdateGroupDefinition service --");
    validateAttributeValuesFormat(groupDefinitionRequest.getReqAttributesValue());
    List<GroupDefinitionDomainDto> groupDefinitionEntityList =
        groupDefinitionPersistenceService
            .fetchGroupDefinitionListByOrgIdAndSourcingAttributesDefinitionIdAndReqAttributesValue(
                groupDefinitionRequest.getOrgId(),
                groupDefinitionRequest.getSourcingAttributesDefinitionId(),
                groupDefinitionRequest.getReqAttributesValue());
    if (groupDefinitionEntityList.isEmpty()) {
      logger.error(
          "Group definition not found for given orgId :{} , sourcingAttributesDefinitionId : {} and reqAttributesValue :{}",
          groupDefinitionRequest.getOrgId(),
          groupDefinitionRequest.getSourcingAttributesDefinitionId(),
          groupDefinitionRequest.getReqAttributesValue());

      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(
          ORG_ID, FieldError.builder().rejectedValue(groupDefinitionRequest.getOrgId()).build());
      errorMap.put(
          REQ_ATTRIBUTES_VALUE,
          FieldError.builder()
              .rejectedValue(groupDefinitionRequest.getReqAttributesValue())
              .build());
      errorMap.put(
          SOURCING_ATTRIBUTES_DEFINITION_ID,
          FieldError.builder()
              .rejectedValue(groupDefinitionRequest.getSourcingAttributesDefinitionId())
              .build());
      throw new CommonServiceException(
          "Group definition not found for given orgId , sourcingAttributesDefinitionId and reqAttributesValue",
          HttpStatus.BAD_REQUEST,
          0X1771,
          errorMap);
    }
    validateGroupName(groupDefinitionRequest.getOrgId(), groupDefinitionRequest.getGroupName());
    groupDefinitionEntityList.get(0).setGroupName(groupDefinitionRequest.getGroupName());
    return INSTANCE.toGroupDefinitionResponse(
        groupDefinitionPersistenceService.saveGroupDefinition(groupDefinitionEntityList.get(0)));
  }

  public GroupDefinitionResponse processDeleteGroupDefinition(Long id, String orgId)
      throws PromiseEngineException, CommonServiceException {
    logger.debug("-- inside processDeleteGroupDefinition service --");
    Optional<GroupDefinitionDomainDto> groupDefinitionEntity =
        groupDefinitionPersistenceService.fetchGroupDefinitionByIdAndOrgId(id, orgId);
    if (groupDefinitionEntity.isEmpty()) {
      logger.error(GROUP_DEFINITION_EXCEPTION_MESSAGE);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ID, FieldError.builder().rejectedValue(id).build());
      throw new CommonServiceException(
          GROUP_DEFINITION_EXCEPTION_MESSAGE, HttpStatus.NOT_FOUND, 0x1771, errorMap);
    }

    GroupDefinitionDomainDto groupDefinition = groupDefinitionEntity.get();
    List<NamedOptimizationStrategyDomainDto> namedOptimizationStrategyDomainDtos =
        namedOptimizationStrategyPersistenceService.fetchOptimizationStrategyByOrgIdAndGroupId(
            groupDefinition.getOrgId(), String.valueOf(id));
    if (!namedOptimizationStrategyDomainDtos.isEmpty()) {
      namedOptimizationStrategyPersistenceService.deleteOptimizationStrategy(
          namedOptimizationStrategyDomainDtos.get(0));
    }
    var groupDefinitionResponse = INSTANCE.toGroupDefinitionResponse(groupDefinition);
    groupDefinitionPersistenceService.deleteGroupDefinition(groupDefinition);
    return groupDefinitionResponse;
  }

  public GroupDefinitionResponse processGetGroupDefinitionByOrgIdAndId(String orgId, Long id)
      throws PromiseEngineException, CommonServiceException {
    logger.debug("-- inside fetchGroupDefinitionByOrgIdAndId service --");
    Optional<GroupDefinitionDomainDto> groupDefinitionEntity =
        groupDefinitionPersistenceService.fetchGroupDefinitionByIdAndOrgId(id, orgId);
    if (groupDefinitionEntity.isEmpty()) {
      logger.error(GROUP_DEFINITION_EXCEPTION_MESSAGE);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ID, FieldError.builder().rejectedValue(id).build());
      throw new CommonServiceException(
          GROUP_DEFINITION_EXCEPTION_MESSAGE, HttpStatus.NOT_FOUND, 0x1771, errorMap);
    }

    return INSTANCE.toGroupDefinitionResponse(groupDefinitionEntity.get());
  }
}
