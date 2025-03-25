/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.dataupload.service;

import static com.nextuple.dataupload.controller.TransferSchedulesController.TRANSFER_SCHEDULE_DEFAULT_SORT_BY;

import com.nextuple.common.base.PagePayload;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.pojo.PageParams;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.common.util.PaginationUtil;
import com.nextuple.dataupload.common.outbound.GenericColumnInfoDto;
import com.nextuple.dataupload.common.outbound.GenericPaginatedTableResponse;
import com.nextuple.dataupload.common.outbound.GenericTableDetails;
import com.nextuple.dataupload.util.CommonDashboardUtil;
import com.nextuple.promise.sourcing.rule.api.domain.enums.SourcingAttributesDefinitionScopeEnum;
import com.nextuple.promise.sourcing.rule.api.domain.feign.SourcingAttributeFeign;
import com.nextuple.promise.sourcing.rule.api.domain.feign.SourcingAttributesDefinitionFeign;
import com.nextuple.promise.sourcing.rule.api.domain.feign.SourcingRulesConfigurationFeign;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.SourcingAttributeResponse;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.SourcingAttributesDefinitionResponse;
import com.nextuple.transit.domain.feign.TransferScheduleFeign;
import com.nextuple.transit.domain.inbound.FetchTransferScheduleRequest;
import com.nextuple.transit.domain.outbound.TransferScheduleResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransferScheduleService {
  public static final String RULE_NAME = "ruleName";
  public static final String SOURCE_NODE_ID = "sourceNodeId";
  public static final String DROPOFF_NODE_ID = "dropoffNodeId";
  public static final String START_TIME = "startTime";
  public static final String END_TIME = "endTime";
  public static final String RULE_NAME_COLUMN = "Rule Name";
  public static final String ORIGIN_NODE_COLUMN = "Origin Node";
  public static final String DESTINATION_NODE_COLUMN = "Destination Node";
  public static final String PICKUP_COLUMN = "Pickup";
  public static final String DROPOFF_COLUMN = "Dropoff";
  private final TransferScheduleFeign transferScheduleFeign;
  private final SourcingAttributesDefinitionFeign sourcingAttributesDefinitionFeign;
  private final SourcingAttributeFeign sourcingAttributeFeign;
  private final SourcingRulesConfigurationFeign sourcingRulesConfigurationFeign;

  private static final String PAGINATION_URL =
      "transfer-schedule/ui/orgId/%s?pageNo=%d&pageSize=%d";

  public GenericPaginatedTableResponse getTransferScheduleListV2(
      String orgId,
      PageParams pageParams,
      FetchTransferScheduleRequest request,
      Boolean isPagination)
      throws CommonServiceException {
    CommonDashboardUtil.handleInvalidSortOrder(pageParams.getSortOrder().orElse("ASC"));
    GenericTableDetails transferScheduleResponse = new GenericTableDetails();
    PagePayload.Pagination pagination = new PagePayload.Pagination();
    List<GenericColumnInfoDto> transferScheduleColumnInfoDtos = new ArrayList<>();
    GenericPaginatedTableResponse finalResponse = new GenericPaginatedTableResponse();
    BaseResponse<SourcingAttributesDefinitionResponse> sourcingAttributeDefinitionResponse =
        getSourcingAttributesDefinitionInActiveStatus(orgId);
    if (sourcingAttributeDefinitionResponse != null
        && sourcingAttributeDefinitionResponse.getPayload() != null) {
      SourcingAttributesDefinitionResponse sourcingAttributeDefinitions =
          sourcingAttributeDefinitionResponse.getPayload();
      List<SourcingAttributeResponse> requiredAttributeList =
          getSourcingAttributes(orgId, sourcingAttributeDefinitions, true);

      List<SourcingAttributeResponse> optionalAttributeList =
          getSourcingAttributes(orgId, sourcingAttributeDefinitions, false);
      request.setSourcingAttributeId(sourcingAttributeDefinitions.getId());
      BaseResponse<PagePayload<TransferScheduleResponse>> response =
          getTransferSchedulesBasedOnFilters(orgId, pageParams, request, isPagination);
      List<TransferScheduleResponse> transferScheduleResponseList = response.getPayload().getData();
      preparePaginationParams(pageParams, response, pagination, orgId);
      List<Map<String, Object>> transferScheduleRows =
          populateRows(transferScheduleResponseList, requiredAttributeList, optionalAttributeList);
      populateTransferScheduleColumnInfo(
          transferScheduleColumnInfoDtos, requiredAttributeList, optionalAttributeList);
      transferScheduleResponse.setColumns(transferScheduleColumnInfoDtos);
      transferScheduleResponse.setRows(transferScheduleRows);
      finalResponse.setData(transferScheduleResponse);
      setPaginationIfNotEmpty(finalResponse, pagination, transferScheduleResponseList);
    }
    return finalResponse;
  }

  private void preparePaginationParams(
      PageParams pageParams,
      BaseResponse<PagePayload<TransferScheduleResponse>> response,
      PagePayload.Pagination pagination,
      String orgId) {
    PagePayload.Pagination paginationConfig = response.getPayload().getPagination();
    pagination.setTotalRecords(paginationConfig.getTotalRecords());
    pagination.setTotalPages(paginationConfig.getTotalPages());
    pagination.setCurrentPage(pageParams.getPageNo().orElse(1));
    pagination.setSortOrder(pageParams.getSortOrder().orElse(TRANSFER_SCHEDULE_DEFAULT_SORT_BY));
    pagination.setSortBy(pageParams.getSortBy().orElse("ASC"));
    String nextUri =
        PaginationUtil.buildUriForPagination(
            paginationConfig.getCurrentPage(),
            paginationConfig.getTotalPages(),
            "next",
            PAGINATION_URL.formatted(
                orgId, paginationConfig.getCurrentPage() + 1, pageParams.getPageSize().orElse(10)));
    String previousUri =
        PaginationUtil.buildUriForPagination(
            paginationConfig.getCurrentPage(),
            paginationConfig.getTotalPages(),
            "previous",
            PAGINATION_URL.formatted(
                orgId, paginationConfig.getCurrentPage() - 1, pageParams.getPageSize().orElse(10)));
    pagination.setPrevious(previousUri);
    pagination.setNext(nextUri);
  }

  private BaseResponse<PagePayload<TransferScheduleResponse>> getTransferSchedulesBasedOnFilters(
      String orgId,
      PageParams pageParams,
      FetchTransferScheduleRequest request,
      Boolean isPagination) {
    return transferScheduleFeign.fetchTransferSchedule(
        orgId,
        isPagination,
        pageParams.getPageNo().orElse(1),
        pageParams.getPageSize().orElse(10),
        pageParams.getSortBy().orElse(TRANSFER_SCHEDULE_DEFAULT_SORT_BY),
        pageParams.getSortOrder().orElse("ASC"),
        request);
  }

  private BaseResponse<SourcingAttributesDefinitionResponse>
      getSourcingAttributesDefinitionInActiveStatus(String orgId) {
    return sourcingAttributesDefinitionFeign.getSourcingAttributesDefinitionInActiveStatus(
        orgId, SourcingAttributesDefinitionScopeEnum.TRANSFER_SCHEDULE_RULE);
  }

  private List<SourcingAttributeResponse> getSourcingAttributes(
      String orgId,
      SourcingAttributesDefinitionResponse sourcingAttributesDefinitionResponse,
      boolean required) {
    String attributesString =
        required
            ? sourcingAttributesDefinitionResponse.getReqAttributes()
            : sourcingAttributesDefinitionResponse.getOptAttributes();
    List<SourcingAttributeResponse> sourcingAttributeList = new ArrayList<>();
    if (Objects.nonNull(attributesString)) {
      String[] attributeIdsFromActiveSourcingAttributesDefinition = attributesString.split(",");
      for (String attributeId : attributeIdsFromActiveSourcingAttributesDefinition) {
        Long attribute = Long.parseLong(attributeId.trim());
        BaseResponse<SourcingAttributeResponse> sourcingAttribute =
            sourcingAttributeFeign.getSourcingAttributeByOrgIdAndId(orgId, attribute);
        if (sourcingAttribute != null && sourcingAttribute.getPayload() != null) {
          sourcingAttributeList.add(sourcingAttribute.getPayload());
        }
      }
    }
    return sourcingAttributeList;
  }

  private GenericColumnInfoDto getTransferColumnInfoDto(
      String columnName, String columnMeta, boolean isSortable) {
    return GenericColumnInfoDto.builder()
        .columnName(columnName)
        .columnMeta(columnMeta)
        .isSortable(isSortable)
        .build();
  }

  private void populateTransferScheduleColumnInfo(
      List<GenericColumnInfoDto> transferColumnInfoDtos,
      List<SourcingAttributeResponse> requiredAttributesList,
      List<SourcingAttributeResponse> optionalAttributesList) {
    transferColumnInfoDtos.add(getTransferColumnInfoDto(RULE_NAME_COLUMN, RULE_NAME, false));
    transferColumnInfoDtos.add(getTransferColumnInfoDto(ORIGIN_NODE_COLUMN, SOURCE_NODE_ID, true));

    transferColumnInfoDtos.add(
        getTransferColumnInfoDto(DESTINATION_NODE_COLUMN, DROPOFF_NODE_ID, true));
    addAttributesToTransferSchedule(transferColumnInfoDtos, requiredAttributesList);
    addAttributesToTransferSchedule(transferColumnInfoDtos, optionalAttributesList);
    transferColumnInfoDtos.add(getTransferColumnInfoDto(PICKUP_COLUMN, START_TIME, false));
    transferColumnInfoDtos.add(getTransferColumnInfoDto(DROPOFF_COLUMN, END_TIME, false));
  }

  private void addAttributesToTransferSchedule(
      List<GenericColumnInfoDto> transferScheduleColumnInfoDtos,
      List<SourcingAttributeResponse> attributesList) {
    for (SourcingAttributeResponse attribute : attributesList) {
      if (attribute != null) {
        transferScheduleColumnInfoDtos.add(
            getTransferColumnInfoDto(
                attribute.getAttributeName(), attribute.getAttributeName(), false));
      }
    }
  }

  private void setPaginationIfNotEmpty(
      GenericPaginatedTableResponse finalResponse,
      PagePayload.Pagination pagination,
      List<TransferScheduleResponse> transferScheduleResponses) {

    if (CollectionUtils.isEmpty(transferScheduleResponses)) {
      finalResponse.setPagination(null);
    } else {
      finalResponse.setPagination(pagination);
    }
  }

  private List<Map<String, Object>> populateRows(
      List<TransferScheduleResponse> transferScheduleList,
      List<SourcingAttributeResponse> requiredAttributesList,
      List<SourcingAttributeResponse> optionalAttributesList) {

    List<Map<String, Object>> rows = new ArrayList<>();

    for (TransferScheduleResponse transferScheduleResponse : transferScheduleList) {

      Map<String, Object> rowEntity = new HashMap<>();
      boolean isRuleProvided =
          isIsRuleProvided(
              transferScheduleResponse.getRule(), transferScheduleResponse.getRuleName());

      rowEntity.put(SOURCE_NODE_ID, transferScheduleResponse.getSourceNodeId());
      rowEntity.put(DROPOFF_NODE_ID, transferScheduleResponse.getDropoffNodeId());
      rowEntity.put(START_TIME, transferScheduleResponse.getStartTime());
      rowEntity.put(END_TIME, transferScheduleResponse.getEndTime());
      if (isRuleProvided) {
        String[] ruleAttributes = transferScheduleResponse.getRule().split(":");
        populateRuleRelatedColumns(
            requiredAttributesList,
            optionalAttributesList,
            transferScheduleResponse,
            rowEntity,
            ruleAttributes);
      }
      rows.add(rowEntity);
    }
    return rows;
  }

  private static void populateRuleRelatedColumns(
      List<SourcingAttributeResponse> requiredAttributesList,
      List<SourcingAttributeResponse> optionalAttributesList,
      TransferScheduleResponse transferScheduleResponse,
      Map<String, Object> rowEntity,
      String[] ruleAttributes) {
    rowEntity.put(RULE_NAME, transferScheduleResponse.getRuleName());
    List<SourcingAttributeResponse> combinationOfRequiredAndOptionalAttributes = new ArrayList<>();
    combinationOfRequiredAndOptionalAttributes.addAll(requiredAttributesList);
    combinationOfRequiredAndOptionalAttributes.addAll(optionalAttributesList);

    int attributeIndex = 0;
    for (SourcingAttributeResponse attribute : combinationOfRequiredAndOptionalAttributes) {
      if (Objects.nonNull(attribute)) {
        if (attributeIndex < ruleAttributes.length
            && StringUtils.hasLength(ruleAttributes[attributeIndex])) {
          rowEntity.put(attribute.getAttributeName(), ruleAttributes[attributeIndex]);
        } else {
          rowEntity.put(attribute.getAttributeName(), null);
        }
        attributeIndex++;
      }
    }
  }

  private boolean isIsRuleProvided(String rule, String ruleName) {
    return !(Objects.isNull(rule)
        || rule.isEmpty()
        || Objects.isNull(ruleName)
        || ruleName.isEmpty());
  }

  public PagePayload<TransferScheduleResponse> getTransferScheduleList(
      String orgId, PageParams pageParams, FetchTransferScheduleRequest request) {
    Integer pageNo = pageParams.getPageNo().orElse(1);
    Integer pageSize = pageParams.getPageSize().orElse(10);
    String sortBy = pageParams.getSortBy().orElse(TRANSFER_SCHEDULE_DEFAULT_SORT_BY);
    String sortOrder = pageParams.getSortOrder().orElse("ASC");

    BaseResponse<PagePayload<TransferScheduleResponse>> response =
        transferScheduleFeign.fetchTransferSchedule(
            orgId, true, pageNo, pageSize, sortBy, sortOrder, request);
    updatePaginationUrls(orgId, pageParams, response);

    return response.getPayload();
  }

  private static void updatePaginationUrls(
      String orgId,
      PageParams pageParams,
      BaseResponse<PagePayload<TransferScheduleResponse>> response) {
    String nextUri =
        PaginationUtil.buildUriForPagination(
            response.getPayload().getPagination().getCurrentPage(),
            response.getPayload().getPagination().getTotalPages(),
            "next",
            PAGINATION_URL.formatted(
                orgId,
                response.getPayload().getPagination().getCurrentPage() + 1,
                pageParams.getPageSize().orElse(10)));
    String previousUri =
        PaginationUtil.buildUriForPagination(
            response.getPayload().getPagination().getCurrentPage(),
            response.getPayload().getPagination().getTotalPages(),
            "previous",
            PAGINATION_URL.formatted(
                orgId,
                response.getPayload().getPagination().getCurrentPage() - 1,
                pageParams.getPageSize().orElse(10)));

    response.getPayload().getPagination().setNext(nextUri);
    response.getPayload().getPagination().setPrevious(previousUri);
  }
}
