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
import com.nextuple.dataupload.util.CommonDashboardUtil;
import com.nextuple.promise.sourcing.rule.api.domain.enums.SourcingAttributesDefinitionScopeEnum;
import com.nextuple.promise.sourcing.rule.api.domain.feign.SourcingAttributeFeign;
import com.nextuple.promise.sourcing.rule.api.domain.feign.SourcingAttributesDefinitionFeign;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.GenericDetailsResponse;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.GenericPageResponse;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.GenericPaginationAttribute;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.SourcingAttributeResponse;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.SourcingAttributesDefinitionResponse;
import com.nextuple.promise.sourcing.rule.api.domain.pojo.GenericColumnInfoDto;
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
  private final TransferScheduleFeign transferScheduleFeign;
  private final SourcingAttributesDefinitionFeign sourcingAttributesDefinitionFeign;
  private final SourcingAttributeFeign sourcingAttributeFeign;

  private static final String PAGINATION_URL =
      "transfer-schedule/ui/orgId/%s?pageNo=%d&pageSize=%d";

  public GenericPageResponse getTransferScheduleListV2(
      String orgId,
      PageParams pageParams,
      FetchTransferScheduleRequest request,
      Boolean isPagination)
      throws CommonServiceException {
    CommonDashboardUtil.handleInvalidSortOrder(pageParams.getSortOrder().orElse("ASC"));
    GenericDetailsResponse transferResponse = new GenericDetailsResponse();
    GenericPaginationAttribute pagination = new GenericPaginationAttribute();
    List<GenericColumnInfoDto> transferScheduleColumnInfoDtos = new ArrayList<>();
    GenericPageResponse finalResponse = new GenericPageResponse();
    BaseResponse<SourcingAttributesDefinitionResponse> sourcingAttributeDefinitionResponse =
        sourcingAttributesDefinitionFeign.getSourcingAttributesDefinitionInActiveStatus(
            orgId, SourcingAttributesDefinitionScopeEnum.TRANSFER_SCHEDULE_RULE);
    if (sourcingAttributeDefinitionResponse != null
        && sourcingAttributeDefinitionResponse.getPayload() != null) {
      SourcingAttributesDefinitionResponse sourcingAttributeDefinitions =
          sourcingAttributeDefinitionResponse.getPayload();
      List<SourcingAttributeResponse> requiredAttributeList =
          getSourcingAttributes(orgId, sourcingAttributeDefinitions, true);
      List<SourcingAttributeResponse> optionalAttributeList =
          getSourcingAttributes(orgId, sourcingAttributeDefinitions, false);

      BaseResponse<PagePayload<TransferScheduleResponse>> response =
          transferScheduleFeign.fetchTransferSchedule(
              orgId,
              isPagination,
              pageParams.getPageNo().orElse(1),
              pageParams.getPageSize().orElse(10),
              pageParams.getSortBy().orElse(TRANSFER_SCHEDULE_DEFAULT_SORT_BY),
              pageParams.getSortOrder().orElse("ASC"),
              request);

      PagePayload.Pagination paginationConfig = response.getPayload().getPagination();
      List<TransferScheduleResponse> transferScheduleData = response.getPayload().getData();
      pagination.setTotalRecords(Long.valueOf(paginationConfig.getTotalRecords()));
      pagination.setTotalPages(paginationConfig.getTotalPages());
      pagination.setCurrentPage(pageParams.getPageNo().orElse(1));
      pagination.setSortOrder(pageParams.getSortOrder().orElse(TRANSFER_SCHEDULE_DEFAULT_SORT_BY));
      pagination.setSortBy(pageParams.getSortBy().orElse("ASC"));

      List<Map<String, Object>> rows =
          populateRows(transferScheduleData, requiredAttributeList, optionalAttributeList);

      populateHolidayCutoffColumnInfo(
          transferScheduleColumnInfoDtos, requiredAttributeList, optionalAttributeList);

      transferResponse.setColumns(transferScheduleColumnInfoDtos);
      transferResponse.setRows(rows);

      finalResponse.setData(transferResponse);
      setPaginationIfNotEmpty(finalResponse, pagination, transferScheduleData);
    }
    return finalResponse;
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
      String[] attributeIdsFromActiveSourcingAttributesDefinition =
          attributesString.split(",");
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

  private void populateHolidayCutoffColumnInfo(
      List<GenericColumnInfoDto> transferColumnInfoDtos,
      List<SourcingAttributeResponse> requiredAttributesList,
      List<SourcingAttributeResponse> optionalAttributesList) {
    transferColumnInfoDtos.add(getTransferColumnInfoDto("Rule Name", "ruleName", false));
    transferColumnInfoDtos.add(getTransferColumnInfoDto("Origin Node", "sourceNodeId", true));

    transferColumnInfoDtos.add(getTransferColumnInfoDto("Destination Node", "dropoffNodeId", true));
    transferColumnInfoDtos.add(getTransferColumnInfoDto("Pickup", "startTime", false));
    transferColumnInfoDtos.add(getTransferColumnInfoDto("Dropoff", "endTime", false));
    addAttributesToTransferSchedule(transferColumnInfoDtos, requiredAttributesList);
    addAttributesToTransferSchedule(transferColumnInfoDtos, optionalAttributesList);
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
      GenericPageResponse finalResponse,
      GenericPaginationAttribute pagination,
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

      rowEntity.put("sourceNodeId", transferScheduleResponse.getSourceNodeId());
      rowEntity.put("dropoffNodeId", transferScheduleResponse.getDropoffNodeId());
      rowEntity.put("startTime", transferScheduleResponse.getStartTime());
      rowEntity.put("endTime", transferScheduleResponse.getEndTime());
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
    rowEntity.put("ruleName", transferScheduleResponse.getRuleName());
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
