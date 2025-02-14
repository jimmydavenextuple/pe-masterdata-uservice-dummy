/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.transit.service.impl;

import static com.nextuple.common.constants.CommonConstants.DEFAULT_SORT_ORDER;
import static com.nextuple.common.constants.CommonConstants.DESC_SORT_ORDER;
import static com.nextuple.common.constants.CommonConstants.ORG_ID;
import static com.nextuple.promise.sourcing.rule.controller.SourcingAttributesDefinitionUIController.logger;

import com.nextuple.common.enums.ApplicationLayer;
import com.nextuple.common.enums.ExceptionCodeMapping;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.common.pojo.PageParams;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.common.response.error.FieldError;
import com.nextuple.node.domain.feign.NodeFeign;
import com.nextuple.node.domain.outbound.NodeResponse;
import com.nextuple.promise.sourcing.rule.api.domain.enums.RulesConfigurationModuleNameEnum;
import com.nextuple.promise.sourcing.rule.api.domain.enums.SourcingAttributesDefinitionScopeEnum;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.RulesConfigurationResponse;
import com.nextuple.promise.sourcing.rule.api.domain.pojo.RuleConfigurationParam;
import com.nextuple.promise.sourcing.rule.service.RulesConfigurationService;
import com.nextuple.transit.domain.inbound.FetchTransferScheduleRequest;
import com.nextuple.transit.domain.inbound.TransferScheduleCreationRequest;
import com.nextuple.transit.domain.inbound.TransferScheduleRequest;
import com.nextuple.transit.domain.mapper.TransferScheduleMapper;
import com.nextuple.transit.domain.outbound.TransferScheduleResponse;
import com.nextuple.transit.persistence.domain.TransferScheduleDomainDto;
import com.nextuple.transit.persistence.service.TransferSchedulePersistenceService;
import com.nextuple.transit.service.TransferScheduleService;
import jakarta.validation.constraints.NotNull;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.joda.time.DateTime;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransferScheduleServiceImpl implements TransferScheduleService {
  private static final Logger logger = LoggerFactory.getLogger(TransferScheduleServiceImpl.class);

  private final TransferSchedulePersistenceService transferSchedulePersistenceService;
  private final RulesConfigurationService ruleConfigurationService;
  private final NodeFeign nodeFeign;
  private static final TransferScheduleMapper INSTANCE =
      Mappers.getMapper(TransferScheduleMapper.class);

  private static final String SOURCE_NODE_ID = "sourceNodeId";
  private static final String DROPOFF_NODE_ID = "dropoffNodeId";
  private static final String SORT_ORDER = "sortOrder";

  @Override
  public TransferScheduleResponse createTransferSchedule(TransferScheduleCreationRequest request)
      throws CommonServiceException, PromiseEngineException {
    validateStartAndEndTime(request.getOrgId(), request.getStartTime(), request.getEndTime());
    validateNodeId(request.getOrgId(), request.getSourceNodeId(), SOURCE_NODE_ID);
    validateNodeId(request.getOrgId(), request.getDropoffNodeId(), DROPOFF_NODE_ID);
    validateRuleDetails(request.getOrgId(), request.getRule(), request.getRuleName());
    var transferScheduleDomainDto = INSTANCE.convertToTransferScheduleEntity(request);
    var entity = transferSchedulePersistenceService.saveTransferSchedule(transferScheduleDomainDto);
    return INSTANCE.convertToTransferScheduleResponse(entity);
  }

  private void validateRuleDetails(String orgId, String rule, String ruleName) throws CommonServiceException{
      if(!(Objects.isNull(rule) || rule.isEmpty()
              || Objects.isNull(ruleName) || ruleName.isEmpty())){
        RuleConfigurationParam ruleConfigurationParam = RuleConfigurationParam.builder()
                .orgId(orgId)
                .rule(rule)
                .ruleName(ruleName)
                .moduleName(RulesConfigurationModuleNameEnum.TRANSFER_SCHEDULE)
                .scope(SourcingAttributesDefinitionScopeEnum.TRANSFER_SCHEDULE_RULE).build();
        try{
          Optional<RulesConfigurationResponse> rulesConfigurationResponseOptional = ruleConfigurationService.fetchRuleByOrgIdAndRuleNameAndRuleAndModuleNameAndScope(ruleConfigurationParam);
          if(rulesConfigurationResponseOptional.isEmpty()){
            throw new PromiseEngineException(ApplicationLayer.SERVICE_LAYER, ExceptionCodeMapping.SERVICE_FIND_FAILED, "Transfer schedule rule not found with rule:" + rule + " and ruleName:" + ruleName);
          }
        }catch(PromiseEngineException e){
          logger.error("Transfer schedule rule not found with rule:" + rule + " and ruleName:" + ruleName);
          throw new CommonServiceException(
                  "Transfer schedule cannot be created with invalid rule or ruleName",
                  HttpStatus.BAD_REQUEST,
                  0x2775,
                  Collections.singletonMap("rule", FieldError.builder().rejectedValue(rule).build()));
        }
    }

      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put("rule", FieldError.builder().rejectedValue(rule).build());
      errorMap.put("ruleName", FieldError.builder().rejectedValue(ruleName).build());
      throw new CommonServiceException(
          "Transfer schedule cannot be created with invalid rule or ruleName",
          HttpStatus.BAD_REQUEST,
          0x2774,
          errorMap);
    }
  }

  private void validateStartAndEndTime(
      String orgId,
      @NotNull(message = "startTime can't be null") DateTime startTime,
      @NotNull(message = "endTime can't be null") DateTime endTime)
      throws CommonServiceException {

    if (startTime.isAfter(endTime)) {
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put("startTime", FieldError.builder().rejectedValue(startTime).build());
      errorMap.put("endTime", FieldError.builder().rejectedValue(endTime).build());
      throw new CommonServiceException(
          "Transfer schedule cannot be created with start time after end time ",
          HttpStatus.BAD_REQUEST,
          0x2773,
          errorMap);
    }
  }

  private void validateNodeId(String orgId, String nodeId, String fieldName)
      throws CommonServiceException {
    try {
      BaseResponse<NodeResponse> sourceNodeResponse = nodeFeign.getNodeDetails(nodeId, orgId);
      if (Boolean.FALSE.equals(sourceNodeResponse.isSuccess())
          || Objects.isNull(sourceNodeResponse.getPayload())) {
        throwNodeNotFoundException(orgId, nodeId, fieldName);
      }
    } catch (Exception e) {
      logger.error("Node not found with node id:" + nodeId, e);
      throwNodeNotFoundException(orgId, nodeId, fieldName);
    }
  }

  private void throwNodeNotFoundException(String orgId, String nodeId, String fieldName)
      throws CommonServiceException {
    Map<String, FieldError> errorMap = new HashMap<>();
    errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
    errorMap.put(fieldName, FieldError.builder().rejectedValue(nodeId).build());
    throw new CommonServiceException(
        "Transfer schedule cannot be created with invalid " + fieldName,
        HttpStatus.BAD_REQUEST,
        0x2772,
        errorMap);
  }

  @Override
  public List<TransferScheduleResponse> fetchTransferSchedules(String orgId, String dropoffNodeId) {
    List<TransferScheduleDomainDto> dtos =
        transferSchedulePersistenceService.fetchUpcomingTransferSchedules(orgId, dropoffNodeId);
    return INSTANCE.convertToTransferScheduleResponseList(dtos);
  }

  @Override
  public TransferScheduleResponse deleteTransferSchedule(TransferScheduleRequest request)
      throws PromiseEngineException, CommonServiceException {
    Date startTime = request.getStartTime().toDate();
    TransferScheduleDomainDto domainDto =
        transferSchedulePersistenceService.deleteTransferSchedule(
            request.getOrgId(), request.getSourceNodeId(), request.getDropoffNodeId(), startTime);
    return INSTANCE.convertToTransferScheduleResponse(domainDto);
  }

  @Override
  public Page<TransferScheduleResponse> fetchTransferScheduleList(
      String orgId,
      Boolean isPaginated,
      PageParams pageParams,
      FetchTransferScheduleRequest request)
      throws CommonServiceException, PromiseEngineException {
    String sortBy = pageParams.getSortBy().orElse(SOURCE_NODE_ID);
    String sortOrder = pageParams.getSortOrder().orElse(DEFAULT_SORT_ORDER);
    validateSortBy(sortBy);
    validateSortOrder(sortOrder);
    Sort sort = Sort.by(Sort.Direction.fromString(sortOrder), sortBy);
    Pageable pageable;
    if (Boolean.TRUE.equals(isPaginated)) {
      pageable =
          PageRequest.of(pageParams.getPageNo().get() - 1, pageParams.getPageSize().get(), sort);
    } else {
      pageable = PageRequest.of(pageParams.getPageNo().get() - 1, Integer.MAX_VALUE, sort);
    }
    return transferSchedulePersistenceService.fetchTransferSchedulesList(orgId, request, pageable);
  }

  private static void validateSortBy(String sortBy) throws CommonServiceException {
    if (!sortBy.equals(SOURCE_NODE_ID) && !sortBy.equals(DROPOFF_NODE_ID)) {
      throw new CommonServiceException(
          "Invalid sortBy field, must be either sourceNodeId or dropoffNodeId",
          HttpStatus.BAD_REQUEST,
          0x4772,
          Collections.singletonMap("sortBy", FieldError.builder().rejectedValue(sortBy).build()));
    }
  }

  private void validateSortOrder(String sortOrder) throws CommonServiceException {
    if (!sortOrder.equalsIgnoreCase(DEFAULT_SORT_ORDER)
        && !sortOrder.equalsIgnoreCase(DESC_SORT_ORDER)) {
      logger.error("Invalid sort order: {}", sortOrder);
      throw new CommonServiceException(
          "Invalid sort order, consider giving either ASC or DESC",
          HttpStatus.BAD_REQUEST,
          0x4773,
          Collections.singletonMap(
              SORT_ORDER, FieldError.builder().rejectedValue(sortOrder).build()));
    }
  }
}
