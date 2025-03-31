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

import com.nextuple.common.enums.ApplicationLayer;
import com.nextuple.common.enums.ExceptionCodeMapping;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.common.pojo.PageParams;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.common.response.error.FieldError;
import com.nextuple.common.util.ObjectUtil;
import com.nextuple.master.data.integration.enums.ActionEnum;
import com.nextuple.node.domain.feign.NodeFeign;
import com.nextuple.node.domain.outbound.NodeResponse;
import com.nextuple.promise.sourcing.rule.api.domain.enums.RulesConfigurationModuleNameEnum;
import com.nextuple.promise.sourcing.rule.api.domain.enums.SourcingAttributesDefinitionScopeEnum;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.RulesConfigurationResponse;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.SourcingAttributesDefinitionResponse;
import com.nextuple.promise.sourcing.rule.api.domain.pojo.RuleConfigurationParam;
import com.nextuple.promise.sourcing.rule.service.RulesConfigurationService;
import com.nextuple.promise.sourcing.rule.service.SourcingAttributesDefinitionService;
import com.nextuple.transit.domain.inbound.FetchTransferScheduleRequest;
import com.nextuple.transit.domain.inbound.TransferScheduleBatchRequest;
import com.nextuple.transit.domain.inbound.TransferScheduleConsumerRequest;
import com.nextuple.transit.domain.inbound.TransferScheduleCreationRequest;
import com.nextuple.transit.domain.inbound.TransferScheduleDeleteRequest;
import com.nextuple.transit.domain.inbound.TransferScheduleRangeRequest;
import com.nextuple.transit.domain.inbound.TransferScheduleRequest;
import com.nextuple.transit.domain.mapper.TransferScheduleMapper;
import com.nextuple.transit.domain.outbound.TransferScheduleBatchResponse;
import com.nextuple.transit.domain.outbound.TransferScheduleConsumerResult;
import com.nextuple.transit.domain.outbound.TransferScheduleRangeResponse;
import com.nextuple.transit.domain.outbound.TransferScheduleResponse;
import com.nextuple.transit.persistence.domain.TransferScheduleDomainDto;
import com.nextuple.transit.persistence.domain.TransferScheduleDomainRequest;
import com.nextuple.transit.persistence.service.TransferSchedulePersistenceService;
import com.nextuple.transit.service.TransferScheduleService;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import kotlin.Pair;
import lombok.RequiredArgsConstructor;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
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
  private final SourcingAttributesDefinitionService sourcingAttributesDefinitionService;
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

  @Override
  @Transactional
  public TransferScheduleBatchResponse batchTransferSchedules(
      TransferScheduleBatchRequest transferScheduleBatchRequest, String orgId)
      throws PromiseEngineException {
    List<TransferScheduleConsumerResult> results = new ArrayList<>();

    Map<ActionEnum, List<TransferScheduleConsumerRequest>> actionMap =
        transferScheduleBatchRequest.getTransferScheduleConsumerRequests().stream()
            .filter(
                req -> {
                  boolean isValid = Objects.nonNull(req.getAction());
                  if (!isValid) {
                    results.add(
                        TransferScheduleConsumerResult.builder()
                            .index(req.getIndex())
                            .success(false)
                            .message("Action type cannot be null")
                            .statusCode(400)
                            .build());
                    return false;
                  }
                  return true;
                })
            .collect(Collectors.groupingBy(TransferScheduleConsumerRequest::getAction));

    for (Map.Entry<ActionEnum, List<TransferScheduleConsumerRequest>> mapEntry :
        actionMap.entrySet()) {
      switch (mapEntry.getKey()) {
        case ActionEnum.CREATE:
          results.addAll(
              batchCreateTransferSchedule(
                  actionMap.get(mapEntry.getKey()),
                  orgId,
                  transferScheduleBatchRequest.getApplyValidation()));
          break;
        case ActionEnum.DELETE:
          results.addAll(batchDeleteTransferSchedule(actionMap.get(mapEntry.getKey())));
          break;
        default:
          results.addAll(
              actionMap.get(mapEntry.getKey()).stream()
                  .map(
                      request ->
                          TransferScheduleConsumerResult.builder()
                              .index(request.getIndex())
                              .success(false)
                              .message("Invalid action type")
                              .statusCode(400)
                              .build())
                  .toList());
          break;
      }
    }

    return TransferScheduleBatchResponse.builder()
        .totalCount(transferScheduleBatchRequest.getTransferScheduleConsumerRequests().size())
        .successCount(
            (int) results.stream().filter(TransferScheduleConsumerResult::getSuccess).count())
        .failureCount((int) results.stream().filter(result -> !result.getSuccess()).count())
        .results(results)
        .build();
  }

  private List<TransferScheduleConsumerResult> batchDeleteTransferSchedule(
      List<TransferScheduleConsumerRequest> requests) {
    List<TransferScheduleConsumerResult> results = new ArrayList<>();
    List<TransferScheduleDomainDto> domainDto;
    try {
      List<TransferScheduleDeleteRequest> transferScheduleDeleteRequest = new ArrayList<>();
      for (TransferScheduleConsumerRequest request : requests) {
        TransferScheduleDeleteRequest deleteRequest = new TransferScheduleDeleteRequest();
        deleteRequest.setOrgId(request.getOrgId());
        deleteRequest.setSourceNodeId(request.getSourceNodeId());
        deleteRequest.setDropoffNodeId(request.getDropoffNodeId());
        deleteRequest.setStartTime(request.getStartTime().toDate());
        transferScheduleDeleteRequest.add(deleteRequest);
      }
      domainDto =
          transferSchedulePersistenceService.deleteTransferSchedules(transferScheduleDeleteRequest);
    } catch (Exception e) {
      requests.forEach(
          request -> {
            TransferScheduleConsumerResult result = new TransferScheduleConsumerResult();
            result.setIndex(request.getIndex());
            result.setSuccess(false);
            result.setMessage("Error while deleting transfer schedule");
            result.setStatusCode(500);
            results.add(result);
          });
      return results;
    }

    for (TransferScheduleConsumerRequest request : requests) {
      TransferScheduleConsumerResult result = new TransferScheduleConsumerResult();
      result.setIndex(request.getIndex());
      if (domainDto.stream()
          .anyMatch(
              dto ->
                  Objects.equals(dto.getOrgId(), request.getOrgId())
                      && dto.getSourceNodeId().equals(request.getSourceNodeId())
                      && dto.getDropoffNodeId().equals(request.getDropoffNodeId())
                      && (new DateTime(dto.getStartTime(), DateTimeZone.UTC)
                          .isEqual(request.getStartTime().withZone(DateTimeZone.UTC))))) {
        result.setSuccess(true);
        result.setMessage("Transfer schedule deleted successfully");
        result.setStatusCode(200);
      } else {
        result.setSuccess(false);
        result.setMessage("Transfer schedule not found");
        result.setStatusCode(404);
      }
      results.add(result);
    }
    return results;
  }

  private List<TransferScheduleConsumerResult> batchCreateTransferSchedule(
      List<TransferScheduleConsumerRequest> transferScheduleBatchRequest,
      String orgId,
      Boolean applyValidation) {
    List<TransferScheduleConsumerResult> results = new ArrayList<>();
    Set<String> uniqueNodeList = new HashSet<>();
    Set<Pair<String, String>> uniqueRulePair = new HashSet<>();
    List<String> validNodes;
    List<Pair<String, String>> invalidRules = new ArrayList<>();

    if (Boolean.TRUE.equals(applyValidation)) {
      transferScheduleBatchRequest.forEach(
          request -> {
            uniqueNodeList.add(request.getDropoffNodeId());
            uniqueNodeList.add(request.getSourceNodeId());
            uniqueRulePair.add(new Pair<>(request.getRule(), request.getRuleName()));
          });
      validNodes =
          nodeFeign.checkIfNodesExist(uniqueNodeList.stream().toList(), orgId).getPayload();
      for (Pair<String, String> rulePair : uniqueRulePair) {
        try {
          validateRuleDetails(orgId, rulePair.getFirst(), rulePair.getSecond());
        } catch (Exception e) {
          invalidRules.add(rulePair);
        }
      }
    } else {
      validNodes = new ArrayList<>();
    }

    List<TransferScheduleConsumerRequest> validRequests =
        filterValidRequests(
            transferScheduleBatchRequest,
            orgId,
            applyValidation,
            validNodes,
            invalidRules,
            results);

    try {
      transferSchedulePersistenceService.saveTransferSchedules(
          INSTANCE.convertToTransferScheduleEntityList(validRequests));
      results.forEach(
          result -> {
            if (result.getStatusCode() == -1) {
              result.setSuccess(true);
              result.setMessage("Transfer schedule created successfully");
              result.setStatusCode(200);
            }
          });
    } catch (Exception e) {
      logger.error("Error while saving batch transfer schedules", e);
      results.forEach(
          result -> {
            if (result.getStatusCode() == -1) {
              result.setSuccess(false);
              result.setMessage("Error while saving transfer schedule");
              result.setStatusCode(500); // Considered as internal server error
            }
          });
    }

    return results;
  }

  private static List<TransferScheduleConsumerRequest> filterValidRequests(
      List<TransferScheduleConsumerRequest> transferScheduleBatchRequest,
      String orgId,
      Boolean applyValidation,
      List<String> validNodes,
      List<Pair<String, String>> invalidRules,
      List<TransferScheduleConsumerResult> results) {
    return transferScheduleBatchRequest.stream()
        .filter(
            request -> {
              if (Boolean.TRUE.equals(applyValidation)) {
                boolean isValid =
                    (Objects.equals(orgId, request.getOrgId()))
                        && validNodes.contains(request.getDropoffNodeId())
                        && validNodes.contains(request.getSourceNodeId())
                        && !request.getStartTime().isAfter(request.getEndTime())
                        && !invalidRules.contains(
                            new Pair<>(request.getRule(), request.getRuleName()));
                if (!isValid) {
                  results.add(
                      TransferScheduleConsumerResult.builder()
                          .index(request.getIndex())
                          .success(false)
                          .message("Rule or node or time validation failed")
                          .statusCode(400) // Considered as bad request with no retry needed
                          .build());
                  return false;
                }
              }
              results.add(
                  TransferScheduleConsumerResult.builder()
                      .index(request.getIndex())
                      .statusCode(-1)
                      .build());
              return true;
            })
        .toList();
  }

  private void validateRuleDetails(String orgId, String rule, String ruleName)
      throws CommonServiceException {
    if (!(Objects.isNull(rule)
        || rule.isEmpty()
        || Objects.isNull(ruleName)
        || ruleName.isEmpty())) {
      RuleConfigurationParam ruleConfigurationParam =
          RuleConfigurationParam.builder()
              .orgId(orgId)
              .rule(rule)
              .ruleName(ruleName)
              .moduleName(RulesConfigurationModuleNameEnum.TRANSFER_SCHEDULE)
              .scope(SourcingAttributesDefinitionScopeEnum.TRANSFER_SCHEDULE_RULE)
              .build();
      try {
        Optional<RulesConfigurationResponse> rulesConfigurationResponseOptional =
            ruleConfigurationService.fetchRuleByOrgIdAndRuleNameAndRuleAndModuleNameAndScope(
                ruleConfigurationParam);
        if (rulesConfigurationResponseOptional.isEmpty()) {
          throw new PromiseEngineException(
              ApplicationLayer.SERVICE_LAYER,
              ExceptionCodeMapping.SERVICE_FIND_FAILED,
              "Transfer schedule rule not found with rule:" + rule + " and ruleName:" + ruleName);
        }
      } catch (PromiseEngineException e) {
        logger.error(
            String.format(
                "Transfer schedule rule not found with rule: %s and ruleName: %s", rule, ruleName));
        throw new CommonServiceException(
            "Transfer schedule cannot be created with invalid rule or ruleName",
            HttpStatus.BAD_REQUEST,
            0x2775,
            Collections.singletonMap("rule", FieldError.builder().rejectedValue(rule).build()));
      }
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

    if (!ObjectUtil.isNull(request.getIsSourcingAttributeEnabled())
        && Boolean.TRUE.equals(request.getIsSourcingAttributeEnabled())) {
      List<RulesConfigurationResponse> ruleConfigs =
          Objects.isNull(request.getSourcingAttributeId())
              ? Collections.emptyList()
              : ruleConfigurationService
                  .fetchRuleByOrgIdAndAttributeDefinitionIdAndModuleNameAndScope(
                      orgId,
                      request.getSourcingAttributeId(),
                      RulesConfigurationModuleNameEnum.TRANSFER_SCHEDULE,
                      SourcingAttributesDefinitionScopeEnum.TRANSFER_SCHEDULE_RULE);
      List<Pair<String, String>> ruleInfo = prepareRuleInfo(ruleConfigs);
      request.setRuleInfo(ruleInfo);
    }

    return transferSchedulePersistenceService.fetchTransferSchedulesList(orgId, request, pageable);
  }

  private List<Pair<String, String>> prepareRuleInfo(List<RulesConfigurationResponse> ruleConfigs) {
    List<Pair<String, String>> ruleInfoList = new ArrayList<>();
    for (RulesConfigurationResponse ruleConfig : ruleConfigs) {
      ruleInfoList.add(new Pair<>(ruleConfig.getRuleName(), ruleConfig.getRule()));
    }
    return ruleInfoList;
  }

  @Override
  public List<TransferScheduleRangeResponse> fetchTransferSchedulesInRange(
      TransferScheduleRangeRequest request) {
    if (Objects.isNull(request.getRule()) || Objects.isNull(request.getRuleName())) {
      try {
        SourcingAttributesDefinitionResponse sourcingAttributesDefinitionResponse =
            sourcingAttributesDefinitionService
                .processGetSourcingAttributesDefinitionInActiveStatus(
                    request.getOrgId(),
                    SourcingAttributesDefinitionScopeEnum.TRANSFER_SCHEDULE_RULE);
        if (Objects.nonNull(sourcingAttributesDefinitionResponse)) return Collections.emptyList();
      } catch (Exception e) {
        logger.error("Failed to fetch sourcing attributes definition in active status", e);
      }
    }
    // start time bound is the maximum allowed start time for the transfer schedule
    Date startTimeBound = null;
    Date startTimeLowerBound = null;
    if (Objects.nonNull(request.getStartTime())) {
      int horizonDays = Objects.nonNull(request.getHorizonDays()) ? request.getHorizonDays() : 0;
      DateTime startTimeDateTime = request.getStartTime().plusDays(horizonDays);
      startTimeBound = startTimeDateTime.withZone(DateTimeZone.UTC).toDate();
      startTimeLowerBound = request.getStartTime().withZone(DateTimeZone.UTC).toDate();
    }
    // end time bound is the minimum allowed end time for the transfer schedule
    Date endTimeBound = null;
    Date endTimeUpperBound = null;
    if (Objects.nonNull(request.getEndTime())) {
      int pastDays = Objects.nonNull(request.getPastDays()) ? request.getPastDays() : 0;
      DateTime endTimeDateTime = request.getEndTime().minusDays(pastDays);
      endTimeBound = endTimeDateTime.withZone(DateTimeZone.UTC).toDate();
      endTimeUpperBound = request.getEndTime().withZone(DateTimeZone.UTC).toDate();
    }
    TransferScheduleDomainRequest domainRequest =
        TransferScheduleDomainRequest.builder()
            .orgId(request.getOrgId())
            .rule(request.getRule())
            .ruleName(request.getRuleName())
            .dropoffNodeId(request.getDropoffNodeId())
            .startTimeLowerBound(startTimeLowerBound)
            .startTimeUpperBound(startTimeBound)
            .endTimeLowerBound(endTimeBound)
            .endTimeUpperBound(endTimeUpperBound)
            .exclusive(request.getExclusive())
            .build();

    List<TransferScheduleDomainDto> dtos =
        transferSchedulePersistenceService.fetchTransferSchedulesInRange(domainRequest);
    return convertDtos(dtos);
  }

  private List<TransferScheduleRangeResponse> convertDtos(List<TransferScheduleDomainDto> dtos) {
    List<TransferScheduleRangeResponse> responses = new ArrayList<>();
    for (TransferScheduleDomainDto dto : dtos) {
      TransferScheduleRangeResponse response =
          TransferScheduleRangeResponse.builder()
              .id(dto.getId())
              .orgId(dto.getOrgId())
              .sourceNodeId(dto.getSourceNodeId())
              .dropoffNodeId(dto.getDropoffNodeId())
              .startTime(
                  dto.getStartTime() != null
                      ? OffsetDateTime.ofInstant(dto.getStartTime().toInstant(), ZoneOffset.UTC)
                      : null)
              .endTime(
                  dto.getEndTime() != null
                      ? OffsetDateTime.ofInstant(dto.getEndTime().toInstant(), ZoneOffset.UTC)
                      : null)
              .rule(dto.getRule())
              .ruleName(dto.getRuleName())
              .build();
      responses.add(response);
    }
    return responses;
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
