/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.promise.sourcing.rule.service;

import static com.nextuple.common.constants.CommonConstants.DEFAULT_SORT_ORDER;
import static com.nextuple.common.constants.CommonConstants.DESC_SORT_ORDER;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.common.pojo.PageParams;
import com.nextuple.common.response.error.FieldError;
import com.nextuple.common.util.DateUtil;
import com.nextuple.promise.sourcing.rule.api.domain.enums.HolidayCutoffDaysType;
import com.nextuple.promise.sourcing.rule.api.domain.enums.HolidayCutoffStatus;
import com.nextuple.promise.sourcing.rule.api.domain.enums.SourcingAttributesDefinitionScopeEnum;
import com.nextuple.promise.sourcing.rule.api.domain.enums.SourcingAttributesDefinitionStatus;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.HolidayCutoffRequest;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.HolidayCutoffRulesRequest;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.HolidayCutoffUIRequest;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.HolidayCutoffUpdateRequest;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.HolidayCutoffDetailsResponse;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.HolidayCutoffRulesResponse;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.PageResponseForHolidayCutoff;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.PaginationAttributeForHolidayCutoff;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.SourcingAttributesDefinitionResponse;
import com.nextuple.promise.sourcing.rule.api.domain.pojo.AttributeFilterInfo;
import com.nextuple.promise.sourcing.rule.api.domain.pojo.HolidayCutoffColumnInfoDto;
import com.nextuple.promise.sourcing.rule.api.domain.pojo.HolidayCutoffInfo;
import com.nextuple.promise.sourcing.rule.domain.entity.HolidayCutoffEntity;
import com.nextuple.promise.sourcing.rule.domain.mapper.HolidayCutoffMapper;
import com.nextuple.promise.sourcing.rule.domain.repository.HolidayCutOffRepository;
import com.nextuple.promise.sourcing.rule.persistence.domain.SourcingAttributeDomainDto;
import com.nextuple.promise.sourcing.rule.persistence.domain.SourcingAttributesDefinitionDomainDto;
import com.nextuple.promise.sourcing.rule.persistence.service.SourcingAttributePersistenceService;
import com.nextuple.promise.sourcing.rule.persistence.service.SourcingAttributesDefinitionPersistenceService;
import com.nextuple.promise.sourcing.rule.utils.PromiseSourcingRuleUtil;
import jakarta.transaction.Transactional;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TimeZone;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Slf4j
public class HolidayCutoffService {
  private static final String COLON_DELIMITER = ":";
  private static final HolidayCutoffMapper INSTANCE = Mappers.getMapper(HolidayCutoffMapper.class);
  @Autowired private final HolidayCutOffRepository holidayCutOffRepository;
  private final SourcingAttributesDefinitionPersistenceService
      sourcingAttributesDefinitionPersistenceService;

  private final SourcingAttributePersistenceService sourcingAttributePersistenceService;

  private final SourcingAttributesDefinitionService sourcingAttributesDefinitionService;

  private static final String SOURCING_ATTRIBUTES_DEFINITION_ID = "sourcingAttributesDefinitionId";
  private static final String ORG_ID = "orgId";
  private static final String HOLIDAY_CUTOFF_NAME = "holidayCutoffName";
  private static final String HOLIDAY_CUTOFF_RULE = "holidayCutoffRule";
  private static final String HOLIDAY_CUTOFF_START_DATE = "holidayCutoffStartDate";
  private static final String HOLIDAY_CUTOFF_DATE = "holidayCutoffDate";
  private static final String PRE_CUTOFF_DAYS = "preCutoffDays";
  private static final String HOLIDAY_COOL_DOWN_DAYS = "holidayCoolDownDays";
  private static final String RULE_NAME = "ruleName";
  private static final String RULE_DESCRIPTION = "ruleDescription";
  private static final String END_DATE = "endDate";
  private static final String START_DATE = "startDate";
  private static final String OVERWRITE_DATE = "overwriteDate";
  private static final String HOLIDAY_CUTOFF_WINDOW = "holidayCutoffWindowsInDays";
  private static final HolidayCutoffStatus DEFAULT_STATUS = HolidayCutoffStatus.ACTIVE;
  public static final String COLON_SPLIT_REGEX = "\\s*:\\s*";
  public static final String SPLIT_REGEX = "\\s*,\\s*";
  private static final String REQUIRED_ATTRIBUTES_LENGTH_ERROR_MESSAGE =
      "Can’t add or fetch the holiday cutoff rules as all the required attributes are not present";
  private static final String TOTAL_ATTRIBUTES_LENGTH_ERROR_MESSAGE =
      "Can't add or fetch the holiday cutoff rules as length of attributes is more than optional and required attributes combined";

  private static final String HOLIDAY_CUTOFF_ENTITY_NOT_FOUND_EXCEPTION_MESSAGE =
      "Holiday cutoff data for a given input not found";

  @Value("${holiday-cutoff.days}")
  private Double defaultDays;

  @Value("${holiday-cutoff.days-type}")
  private HolidayCutoffDaysType defaultDaysType;

  private final Map<String, String> sortByMappings =
      Map.of(
          RULE_NAME,
          HOLIDAY_CUTOFF_NAME,
          RULE_DESCRIPTION,
          "holidayCutoffDescription",
          END_DATE,
          HOLIDAY_CUTOFF_DATE,
          START_DATE,
          START_DATE,
          OVERWRITE_DATE,
          "holidayDeliveryDate",
          PRE_CUTOFF_DAYS,
          PRE_CUTOFF_DAYS,
          HOLIDAY_COOL_DOWN_DAYS,
          "deliveryCoolDownDays");

  @Transactional
  public HolidayCutoffInfo createHolidayCutoff(HolidayCutoffRequest holidayCutoffRequest)
      throws CommonServiceException, PromiseEngineException, ParseException {
    checkAndSetDefaults(holidayCutoffRequest);
    validateHolidayCutoffRequest(holidayCutoffRequest);
    var holidayCutoffEntity = INSTANCE.toHolidayCutoffEntity(holidayCutoffRequest);
    return INSTANCE.toHolidayCutoffInfo(holidayCutOffRepository.save(holidayCutoffEntity));
  }

  @Transactional
  public HolidayCutoffInfo updateHolidayCutoff(
      String orgId,
      String holidayCutoffName,
      String holidayCutoffRule,
      HolidayCutoffUpdateRequest holidayCutoffUpdateRequest)
      throws CommonServiceException {
    Optional<HolidayCutoffEntity> holidayCutoffEntity =
        holidayCutOffRepository.findByOrgIdAndHolidayCutoffNameAndHolidayCutoffRule(
            orgId, holidayCutoffName, holidayCutoffRule);
    if (holidayCutoffEntity.isEmpty()) {
      throwCommonServiceException(orgId, holidayCutoffName, holidayCutoffRule);
    }

    Date startDate =
        Objects.nonNull(holidayCutoffUpdateRequest.getStartDate())
            ? holidayCutoffUpdateRequest.getStartDate()
            : holidayCutoffEntity.get().getStartDate();
    Date cutOffDate =
        Objects.nonNull(holidayCutoffUpdateRequest.getHolidayCutoffDate())
            ? holidayCutoffUpdateRequest.getHolidayCutoffDate()
            : holidayCutoffEntity.get().getHolidayCutoffDate();
    Double preCutOffDays =
        Objects.nonNull(holidayCutoffUpdateRequest.getPreCutoffDays())
            ? holidayCutoffUpdateRequest.getPreCutoffDays()
            : holidayCutoffEntity.get().getPreCutoffDays();

    validateStartDateAndHolidayCutOffDate(startDate, cutOffDate);
    validatePreCutoffDays(startDate, cutOffDate, preCutOffDays);

    INSTANCE.updateHolidayCutoffEntity(holidayCutoffUpdateRequest, holidayCutoffEntity.get());
    return INSTANCE.toHolidayCutoffInfo(holidayCutOffRepository.save(holidayCutoffEntity.get()));
  }

  public PageResponseForHolidayCutoff getHolidayCutoffDetailsBasedOnFilters(
      String orgId,
      Boolean isPaginated,
      PageParams pageParams,
      HolidayCutoffUIRequest holidayCutoffUIRequest)
      throws CommonServiceException, PromiseEngineException {

    handleInvalidSortOrder(pageParams.getSortOrder().get());

    HolidayCutoffDetailsResponse response = new HolidayCutoffDetailsResponse();
    PaginationAttributeForHolidayCutoff pagination = new PaginationAttributeForHolidayCutoff();
    List<HolidayCutoffColumnInfoDto> holidayCutoffColumnInfoDtos = new ArrayList<>();
    PageResponseForHolidayCutoff finalResponse = new PageResponseForHolidayCutoff();

    SourcingAttributesDefinitionResponse sourcingAttributesDefinitionResponse =
        checkActiveAttributeDefinition(orgId, holidayCutoffUIRequest);

    if (Objects.nonNull(sourcingAttributesDefinitionResponse)) {

      List<SourcingAttributeDomainDto> requiredAttributesDetailsList =
          extractAttributesList(sourcingAttributesDefinitionResponse, orgId, true);

      List<SourcingAttributeDomainDto> optionalAttributesDetailsList =
          extractAttributesList(sourcingAttributesDefinitionResponse, orgId, false);

      List<List<String>> possibleRuleCombinations =
          findPossibleRuleCombinationsFromFilters(
              holidayCutoffUIRequest, requiredAttributesDetailsList, optionalAttributesDetailsList);

      Page<HolidayCutoffEntity> holidayCutoffEntityList =
          getHolidayCutoffEntityList(
              orgId, holidayCutoffUIRequest, pageParams, isPaginated, possibleRuleCombinations);

      pagination.setTotalRecords(holidayCutoffEntityList.getTotalElements());
      pagination.setTotalPages(holidayCutoffEntityList.getTotalPages());
      pagination.setCurrentPage(pageParams.getPageNo().get());
      pagination.setSortOrder(pageParams.getSortOrder().get());
      pagination.setSortBy(pageParams.getSortBy().get());

      List<Map<String, Object>> rows =
          populateRows(
              holidayCutoffEntityList.getContent(),
              requiredAttributesDetailsList,
              optionalAttributesDetailsList);

      populateHolidayCutoffColumnInfo(
          holidayCutoffColumnInfoDtos,
          requiredAttributesDetailsList,
          optionalAttributesDetailsList);

      response.setColumns(holidayCutoffColumnInfoDtos);
      response.setRows(rows);

      finalResponse.setData(response);
      setPaginationIfNotEmpty(finalResponse, pagination, holidayCutoffEntityList.getContent());
    }

    return finalResponse;
  }

  private List<List<String>> findPossibleRuleCombinationsFromFilters(
      HolidayCutoffUIRequest holidayCutoffUIRequest,
      List<SourcingAttributeDomainDto> requiredAttributesDetailsList,
      List<SourcingAttributeDomainDto> optionalAttributesDetailsList) {
    List<List<String>> requiredAndOptionalFilterValues = new ArrayList<>();
    populateRequiredAndOptionalAttributesFilterValues(
        requiredAttributesDetailsList,
        holidayCutoffUIRequest.getRequiredAttributes(),
        requiredAndOptionalFilterValues);
    populateRequiredAndOptionalAttributesFilterValues(
        optionalAttributesDetailsList,
        holidayCutoffUIRequest.getOptionalAttributes(),
        requiredAndOptionalFilterValues);
    return fetchPossibleRuleCombinations(requiredAndOptionalFilterValues);
  }

  private List<List<String>> fetchPossibleRuleCombinations(
      List<List<String>> requiredAndOptionalFilterValues) {
    List<List<String>> resultLists = new ArrayList<>();
    if (requiredAndOptionalFilterValues.isEmpty()) {
      resultLists.add(new ArrayList<>());
      return resultLists;
    } else {
      List<String> firstList = requiredAndOptionalFilterValues.get(0);
      List<List<String>> remainingLists =
          fetchPossibleRuleCombinations(
              requiredAndOptionalFilterValues.subList(1, requiredAndOptionalFilterValues.size()));
      for (String condition : firstList) {
        for (List<String> remainingList : remainingLists) {
          ArrayList<String> resultList = new ArrayList<>();
          resultList.add(condition);
          resultList.addAll(remainingList);
          resultLists.add(resultList);
        }
      }
    }
    return resultLists;
  }

  private void populateRequiredAndOptionalAttributesFilterValues(
      List<SourcingAttributeDomainDto> attributesDetailsList,
      List<AttributeFilterInfo> attributesFilterInfoList,
      List<List<String>> requiredAndOptionalFilterValues) {
    if (!ObjectUtils.isEmpty(attributesDetailsList)) {
      for (SourcingAttributeDomainDto sourcingAttributeDomainDto : attributesDetailsList) {
        Optional<AttributeFilterInfo> attributeFilterInfo =
            !ObjectUtils.isEmpty(attributesFilterInfoList)
                ? attributesFilterInfoList.stream()
                    .filter(
                        attributeFilter ->
                            attributeFilter
                                .getAttributeName()
                                .equals(sourcingAttributeDomainDto.getAttributeName()))
                    .findAny()
                : Optional.empty();
        if (attributeFilterInfo.isPresent())
          requiredAndOptionalFilterValues.add(attributeFilterInfo.get().getAttributeFilterValues());
        else requiredAndOptionalFilterValues.add(List.of(""));
      }
    }
  }

  private boolean isValidSortOrder(String sortOrder) {
    return DEFAULT_SORT_ORDER.equalsIgnoreCase(sortOrder)
        || DESC_SORT_ORDER.equalsIgnoreCase(sortOrder);
  }

  private void handleInvalidSortOrder(String sortOrder) throws CommonServiceException {
    if (!isValidSortOrder(sortOrder)) {
      HolidayCutoffService.log.error("Invalid sort order");
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put("sortOrder", FieldError.builder().rejectedValue(sortOrder).build());
      throw new CommonServiceException(
          "Invalid sort order, consider giving either ASC or DESC",
          HttpStatus.BAD_REQUEST,
          0x1991,
          errorMap);
    }
  }

  private void setPaginationIfNotEmpty(
      PageResponseForHolidayCutoff finalResponse,
      PaginationAttributeForHolidayCutoff pagination,
      List<HolidayCutoffEntity> holidayCutoffEntityList) {

    if (holidayCutoffEntityList.isEmpty()) {
      finalResponse.setPagination(null);
    } else {
      finalResponse.setPagination(pagination);
    }
  }

  private Page<HolidayCutoffEntity> getHolidayCutoffEntityList(
      String orgId,
      HolidayCutoffUIRequest holidayCutoffUIRequest,
      PageParams pageParams,
      boolean isPaginated,
      List<List<String>> possibleRuleCombinations) {
    Pageable pageable = null;
    if (isPaginated) {

      pageable =
          PageRequest.of(
              pageParams.getPageNo().get() - 1,
              pageParams.getPageSize().get(),
              Sort.Direction.valueOf(pageParams.getSortOrder().get()),
              sortByMappings.getOrDefault(pageParams.getSortBy().get(), HOLIDAY_CUTOFF_NAME));
    } else {
      pageable = PageRequest.of(pageParams.getPageNo().get() - 1, Integer.MAX_VALUE);
    }

    return holidayCutOffRepository.findFilteredHolidayCutoffRules(
        orgId, holidayCutoffUIRequest, possibleRuleCombinations, pageable);
  }

  private List<SourcingAttributeDomainDto> extractAttributesList(
      SourcingAttributesDefinitionResponse sourcingAttributesDefinitionResponse,
      String orgId,
      boolean isRequiredAttributes)
      throws PromiseEngineException {
    List<SourcingAttributeDomainDto> attributesDetailsList = Collections.emptyList();
    String attributesString =
        isRequiredAttributes
            ? sourcingAttributesDefinitionResponse.getReqAttributes()
            : sourcingAttributesDefinitionResponse.getOptAttributes();
    if (Objects.nonNull(attributesString)) {
      List<Long> attributesIdsListFromActiveSourcingAttributeDefinition = new ArrayList<>();

      String[] attributeIdsFromActiveSourcingAttributesDefinition =
          attributesString.split(SPLIT_REGEX);

      for (String attributeId : attributeIdsFromActiveSourcingAttributesDefinition) {
        String attribute = attributeId.trim();
        if (StringUtils.hasLength(attribute)) {
          attributesIdsListFromActiveSourcingAttributeDefinition.add(Long.parseLong(attribute));
        }
      }

      attributesDetailsList =
          sourcingAttributePersistenceService.fetchSourcingAttributeListByOrgIdAndAttributeIds(
              orgId, attributesIdsListFromActiveSourcingAttributeDefinition);

      Collections.sort(
          attributesDetailsList,
          Comparator.comparingInt(
              attribute ->
                  Arrays.asList(attributeIdsFromActiveSourcingAttributesDefinition)
                      .indexOf(String.valueOf(attribute.getId()))));
    }
    return attributesDetailsList;
  }

  private SourcingAttributesDefinitionResponse checkActiveAttributeDefinition(
      String orgId, HolidayCutoffUIRequest holidayCutoffUIRequest)
      throws PromiseEngineException, CommonServiceException {
    SourcingAttributesDefinitionResponse sourcingAttributesDefinitionResponse;
    try {
      sourcingAttributesDefinitionResponse =
          sourcingAttributesDefinitionService.processGetSourcingAttributesDefinitionByIdandOrgId(
              holidayCutoffUIRequest.getSourcingAttributesDefinitionId(), orgId);
      if (!SourcingAttributesDefinitionStatus.ACTIVE.equals(
          sourcingAttributesDefinitionResponse.getStatus())) {
        log.debug("-- Sourcing attribute definition is not active --");
        return null;
      }
    } catch (Exception e) {
      log.debug(
          "-- Sourcing attribute definition not found for given orgId and sourcingAttributesDefinitionId --");
      return null;
    }
    return sourcingAttributesDefinitionResponse;
  }

  private List<Map<String, Object>> populateRows(
      List<HolidayCutoffEntity> holidayCutoffEntityList,
      List<SourcingAttributeDomainDto> requiredAttributesList,
      List<SourcingAttributeDomainDto> optionalAttributesList) {

    List<Map<String, Object>> rows = new ArrayList<>();

    for (HolidayCutoffEntity holidayCutoffEntity : holidayCutoffEntityList) {
      String[] holidayCutoffAttributes =
          holidayCutoffEntity.getHolidayCutoffRule().split(COLON_DELIMITER);

      Map<String, Object> rowEntity = new HashMap<>();
      rowEntity.put(RULE_NAME, holidayCutoffEntity.getHolidayCutoffName());
      rowEntity.put(RULE_DESCRIPTION, holidayCutoffEntity.getHolidayCutoffDescription());
      rowEntity.put(START_DATE, holidayCutoffEntity.getStartDate());
      rowEntity.put(END_DATE, holidayCutoffEntity.getHolidayCutoffDate());
      rowEntity.put(OVERWRITE_DATE, holidayCutoffEntity.getHolidayDeliveryDate());
      rowEntity.put(PRE_CUTOFF_DAYS, holidayCutoffEntity.getPreCutoffDays());
      rowEntity.put(HOLIDAY_COOL_DOWN_DAYS, holidayCutoffEntity.getDeliveryCoolDownDays());

      List<SourcingAttributeDomainDto> combinationOfRequiredAndOptionalAttributes =
          new ArrayList<>();
      combinationOfRequiredAndOptionalAttributes.addAll(requiredAttributesList);
      combinationOfRequiredAndOptionalAttributes.addAll(optionalAttributesList);

      int attributeIndex = 0;
      for (SourcingAttributeDomainDto attribute : combinationOfRequiredAndOptionalAttributes) {
        if (Objects.nonNull(attribute)) {
          if (attributeIndex < holidayCutoffAttributes.length
              && !StringUtils.isEmpty(holidayCutoffAttributes[attributeIndex])) {
            rowEntity.put(attribute.getAttributeName(), holidayCutoffAttributes[attributeIndex]);
          } else {
            rowEntity.put(attribute.getAttributeName(), null);
          }
          attributeIndex++;
        }
      }
      rows.add(rowEntity);
    }

    return rows;
  }

  private void populateHolidayCutoffColumnInfo(
      List<HolidayCutoffColumnInfoDto> holidayCutoffColumnInfoDtos,
      List<SourcingAttributeDomainDto> requiredAttrbiutesDetailsList,
      List<SourcingAttributeDomainDto> optionalAttrbiutesDetailsList) {
    holidayCutoffColumnInfoDtos.add(getHolidayCutoffHeadersInfoDto("Rule Name", RULE_NAME, true));
    holidayCutoffColumnInfoDtos.add(
        getHolidayCutoffHeadersInfoDto("Rule Description", RULE_DESCRIPTION, true));

    addAttributesToHolidayCutoff(holidayCutoffColumnInfoDtos, requiredAttrbiutesDetailsList);
    addAttributesToHolidayCutoff(holidayCutoffColumnInfoDtos, optionalAttrbiutesDetailsList);

    holidayCutoffColumnInfoDtos.add(getHolidayCutoffHeadersInfoDto("Start Date", START_DATE, true));
    holidayCutoffColumnInfoDtos.add(getHolidayCutoffHeadersInfoDto("End Date", END_DATE, true));
    holidayCutoffColumnInfoDtos.add(
        getHolidayCutoffHeadersInfoDto("Overwrite Date", OVERWRITE_DATE, true));
    holidayCutoffColumnInfoDtos.add(
        getHolidayCutoffHeadersInfoDto("Pre-cutoff Days", PRE_CUTOFF_DAYS, true));
    holidayCutoffColumnInfoDtos.add(
        getHolidayCutoffHeadersInfoDto("Holiday Cooldown Days", HOLIDAY_COOL_DOWN_DAYS, true));
  }

  private void addAttributesToHolidayCutoff(
      List<HolidayCutoffColumnInfoDto> holidayCutoffColumnInfoDtos,
      List<SourcingAttributeDomainDto> attributesList) {
    for (SourcingAttributeDomainDto attribute : attributesList) {
      if (attribute != null) {
        holidayCutoffColumnInfoDtos.add(
            getHolidayCutoffHeadersInfoDto(
                attribute.getAttributeName(), attribute.getAttributeName(), false));
      }
    }
  }

  private HolidayCutoffColumnInfoDto getHolidayCutoffHeadersInfoDto(
      String columnName, String columnMeta, boolean isSortable) {
    return HolidayCutoffColumnInfoDto.builder()
        .columnName(columnName)
        .columnMeta(columnMeta)
        .isSortable(isSortable)
        .build();
  }

  public HolidayCutoffInfo fetchHolidayCutoff(
      String orgId, String holidayCutoffName, String holidayCutoffRule)
      throws CommonServiceException {
    Optional<HolidayCutoffEntity> holidayCutoffEntity =
        holidayCutOffRepository.findByOrgIdAndHolidayCutoffNameAndHolidayCutoffRule(
            orgId, holidayCutoffName, holidayCutoffRule);
    if (holidayCutoffEntity.isEmpty()) {
      throwCommonServiceException(orgId, holidayCutoffName, holidayCutoffRule);
    }
    return INSTANCE.toHolidayCutoffInfo(holidayCutoffEntity.get());
  }

  @Transactional
  public HolidayCutoffInfo deleteHolidayCutoff(
      String orgId, String holidayCutoffName, String holidayCutoffRule)
      throws CommonServiceException {
    Optional<HolidayCutoffEntity> holidayCutoffEntity =
        holidayCutOffRepository.findByOrgIdAndHolidayCutoffNameAndHolidayCutoffRule(
            orgId, holidayCutoffName, holidayCutoffRule);
    if (holidayCutoffEntity.isEmpty()) {
      throwCommonServiceException(orgId, holidayCutoffName, holidayCutoffRule);
    }
    holidayCutOffRepository.delete(holidayCutoffEntity.get());
    return INSTANCE.toHolidayCutoffInfo(holidayCutoffEntity.get());
  }

  private static void throwCommonServiceException(
      String orgId, String holidayCutoffName, String holidayCutoffRule)
      throws CommonServiceException {
    log.error(HOLIDAY_CUTOFF_ENTITY_NOT_FOUND_EXCEPTION_MESSAGE);
    Map<String, FieldError> errorMap = new HashMap<>();
    errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
    errorMap.put(
        HOLIDAY_CUTOFF_NAME, FieldError.builder().rejectedValue(holidayCutoffName).build());
    errorMap.put(
        HOLIDAY_CUTOFF_RULE, FieldError.builder().rejectedValue(holidayCutoffRule).build());
    throw new CommonServiceException(
        HOLIDAY_CUTOFF_ENTITY_NOT_FOUND_EXCEPTION_MESSAGE, HttpStatus.NOT_FOUND, 0x1B60, errorMap);
  }

  private void validateHolidayCutoffRequest(HolidayCutoffRequest holidayCutoffRequest)
      throws PromiseEngineException, CommonServiceException {
    validateSourcingAttributesDefinitionIdForHolidayCutoff(
        holidayCutoffRequest.getOrgId(),
        holidayCutoffRequest.getHolidayCutoffRule(),
        holidayCutoffRequest.getSourcingAttributesDefinitionId());
    checkForUniqueRecord(holidayCutoffRequest);
    validateHolidayCutoffRuleAndDates(holidayCutoffRequest);
    validateStartDateAndHolidayCutOffDate(
        holidayCutoffRequest.getStartDate(), holidayCutoffRequest.getHolidayCutoffDate());
    validatePreCutoffDays(
        holidayCutoffRequest.getStartDate(),
        holidayCutoffRequest.getHolidayCutoffDate(),
        holidayCutoffRequest.getPreCutoffDays());
  }

  private void checkAndSetDefaults(HolidayCutoffRequest holidayCutoffRequest)
      throws ParseException {
    if (Objects.isNull(holidayCutoffRequest.getPreCutoffDays()))
      holidayCutoffRequest.setPreCutoffDays(defaultDays);
    if (Objects.isNull(holidayCutoffRequest.getDeliveryCoolDownDays()))
      holidayCutoffRequest.setDeliveryCoolDownDays(defaultDays);
    if (Objects.isNull(holidayCutoffRequest.getPreCutoffDaysType()))
      holidayCutoffRequest.setPreCutoffDaysType(defaultDaysType);
    if (Objects.isNull(holidayCutoffRequest.getDeliveryCoolDownDaysType()))
      holidayCutoffRequest.setDeliveryCoolDownDaysType(defaultDaysType);
    if (Objects.isNull(holidayCutoffRequest.getStatus()))
      holidayCutoffRequest.setStatus(DEFAULT_STATUS);

    if (Objects.isNull(holidayCutoffRequest.getStartDate())) {
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss");
      dateFormat.setTimeZone(TimeZone.getTimeZone(ZoneOffset.UTC));
      SimpleDateFormat dateFormatLocal = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss");

      holidayCutoffRequest.setStartDate(dateFormatLocal.parse(dateFormat.format(new Date())));
    }
  }

  private void checkForUniqueRecord(HolidayCutoffRequest holidayCutoffRequest)
      throws CommonServiceException {
    Optional<HolidayCutoffEntity> holidayCutoffEntity =
        holidayCutOffRepository.findByOrgIdAndHolidayCutoffNameAndHolidayCutoffRule(
            holidayCutoffRequest.getOrgId(),
            holidayCutoffRequest.getHolidayCutoffName(),
            holidayCutoffRequest.getHolidayCutoffRule());
    if (holidayCutoffEntity.isPresent()) {
      log.error(
          "Holiday cutoff with given orgId : {}, holidayCutoffName : {} and holidayCutoffRule : {} is already configured",
          holidayCutoffRequest.getOrgId(),
          holidayCutoffRequest.getHolidayCutoffName(),
          holidayCutoffRequest.getHolidayCutoffRule());
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(
          ORG_ID, FieldError.builder().rejectedValue(holidayCutoffRequest.getOrgId()).build());
      errorMap.put(
          HOLIDAY_CUTOFF_NAME,
          FieldError.builder().rejectedValue(holidayCutoffRequest.getHolidayCutoffName()).build());
      errorMap.put(
          HOLIDAY_CUTOFF_RULE,
          FieldError.builder().rejectedValue(holidayCutoffRequest.getHolidayCutoffRule()).build());
      throw new CommonServiceException(
          "Holiday cutoff with given orgId, holidayCutoffName and holidayCutoffRule is already configured",
          HttpStatus.PRECONDITION_FAILED,
          0x1B5A,
          errorMap);
    }
  }

  private void validateSourcingAttributesDefinitionIdForHolidayCutoff(
      String orgId, String holidayCutoffRule, Long sourcingAttributesDefinitionId)
      throws PromiseEngineException, CommonServiceException {
    SourcingAttributesDefinitionDomainDto existingSourcingAttributesDefinitionDomainDto =
        fetchSourcingAttributeDefinition(orgId, sourcingAttributesDefinitionId);
    String[] requiredAttributeReferencesList =
        existingSourcingAttributesDefinitionDomainDto.getReqAttributes().split(SPLIT_REGEX);
    int optionalAttributesLength =
        StringUtils.hasLength(existingSourcingAttributesDefinitionDomainDto.getOptAttributes())
            ? existingSourcingAttributesDefinitionDomainDto
                .getOptAttributes()
                .split(SPLIT_REGEX)
                .length
            : 0;
    String[] attributeValuesList = holidayCutoffRule.split(COLON_SPLIT_REGEX);
    PromiseSourcingRuleUtil.checkForRequiredAttributesLength(
        holidayCutoffRule,
        requiredAttributeReferencesList,
        attributeValuesList,
        REQUIRED_ATTRIBUTES_LENGTH_ERROR_MESSAGE,
        HOLIDAY_CUTOFF_RULE,
        0x1B5B);
    PromiseSourcingRuleUtil.checkForTotalAttributesLength(
        holidayCutoffRule,
        requiredAttributeReferencesList,
        optionalAttributesLength,
        attributeValuesList,
        TOTAL_ATTRIBUTES_LENGTH_ERROR_MESSAGE,
        HOLIDAY_CUTOFF_RULE,
        0x1B5C);
  }

  private SourcingAttributesDefinitionDomainDto fetchSourcingAttributeDefinition(
      String orgId, Long sourcingAttributesDefinitionId)
      throws PromiseEngineException, CommonServiceException {
    Optional<SourcingAttributesDefinitionDomainDto> existingSourcingAttributesDefinitionDomainDto =
        sourcingAttributesDefinitionPersistenceService
            .getSourcingRuleAttributesDefinitionEntityByIdAndOrgId(
                sourcingAttributesDefinitionId, orgId);

    if (existingSourcingAttributesDefinitionDomainDto.isEmpty()
        || !(existingSourcingAttributesDefinitionDomainDto
                .get()
                .getStatus()
                .equals(SourcingAttributesDefinitionStatus.ACTIVE)
            && existingSourcingAttributesDefinitionDomainDto
                .get()
                .getScope()
                .equals(SourcingAttributesDefinitionScopeEnum.HOLIDAY_CUTOFF))) {
      log.error(
          "Invalid sourcing rule attributes definition for HOLIDAY_CUTOFF scope / Sourcing attributes definition exists but not in ACTIVE status with given sourcingAttributesDefinitionId : {}",
          sourcingAttributesDefinitionId);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(
          SOURCING_ATTRIBUTES_DEFINITION_ID,
          FieldError.builder().rejectedValue(sourcingAttributesDefinitionId).build());
      throw new CommonServiceException(
          "Invalid sourcing attributes definition for HOLIDAY_CUTOFF scope / Sourcing attributes definition exists but not in ACTIVE status",
          HttpStatus.BAD_REQUEST,
          0x1B59,
          errorMap);
    }

    return existingSourcingAttributesDefinitionDomainDto.get();
  }

  private void validateStartDateAndHolidayCutOffDate(Date startDate, Date holidayCutOffDate)
      throws CommonServiceException {
    if (DateUtil.isDateAfter(startDate, holidayCutOffDate)) {
      log.error(
          "Overlap in start date: {} and holiday cutoff date: {}", startDate, holidayCutOffDate);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(
          HOLIDAY_CUTOFF_START_DATE, FieldError.builder().rejectedValue(startDate).build());
      errorMap.put(
          HOLIDAY_CUTOFF_DATE, FieldError.builder().rejectedValue(holidayCutOffDate).build());
      throw new CommonServiceException(
          "Overlap in start date and holiday cutoff date.",
          HttpStatus.BAD_REQUEST,
          0x1B5F,
          errorMap);
    }
  }

  private void validatePreCutoffDays(Date startDate, Date holidayCutOffDate, Double preCutOffDays)
      throws CommonServiceException {
    long holidayCutoffWindowMins = DateUtil.getDifferenceInMinutes(startDate, holidayCutOffDate);
    long holidayCutOffWindowDays = holidayCutoffWindowMins / 1440;
    if (holidayCutoffWindowMins < (preCutOffDays * 1440)) {
      log.error(
          "Pre cut off days: {} should be less than holiday cut off window days: {}",
          preCutOffDays,
          holidayCutoffWindowMins / 1440);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(PRE_CUTOFF_DAYS, FieldError.builder().rejectedValue(preCutOffDays).build());
      errorMap.put(
          HOLIDAY_CUTOFF_WINDOW,
          FieldError.builder().rejectedValue(holidayCutOffWindowDays).build());
      throw new CommonServiceException(
          "Pre-cutoff days configured should be less than holiday cutoff window days",
          HttpStatus.BAD_REQUEST,
          0x1B5E,
          errorMap);
    }
  }

  private void validateHolidayCutoffRuleAndDates(HolidayCutoffRequest holidayCutoffRequest)
      throws CommonServiceException {
    List<HolidayCutoffEntity> holidayCutoffEntityList =
        holidayCutOffRepository.findByOrgIdAndHolidayCutoffRuleAndHolidayCutoffDate(
            holidayCutoffRequest.getOrgId(),
            holidayCutoffRequest.getHolidayCutoffRule(),
            holidayCutoffRequest.getHolidayCutoffDate());
    if (!holidayCutoffEntityList.isEmpty()) {
      log.error(
          "Holiday cutoff rule: {} with same cutoff date: {}  is already configured.",
          holidayCutoffRequest.getHolidayCutoffRule(),
          holidayCutoffRequest.getHolidayCutoffDate());
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(
          HOLIDAY_CUTOFF_RULE,
          FieldError.builder().rejectedValue(holidayCutoffRequest.getHolidayCutoffRule()).build());
      errorMap.put(
          HOLIDAY_CUTOFF_DATE,
          FieldError.builder().rejectedValue(holidayCutoffRequest.getHolidayCutoffDate()).build());
      throw new CommonServiceException(
          "Holiday cutoff rule with same cutoff date is already configured.",
          HttpStatus.PRECONDITION_FAILED,
          0x1B5D,
          errorMap);
    }
  }

  @Transactional
  public HolidayCutoffRulesResponse processFetchHolidayCutoffRules(
      HolidayCutoffRulesRequest holidayCutoffRulesRequest)
      throws PromiseEngineException, CommonServiceException {
    var holidayCutoffRule = getHolidayCutoffRule(holidayCutoffRulesRequest);
    validateSourcingAttributesDefinitionIdForHolidayCutoff(
        holidayCutoffRulesRequest.getOrgId(),
        holidayCutoffRule,
        holidayCutoffRulesRequest.getSourcingAttributesDefinitionId());

    SourcingAttributesDefinitionDomainDto sourcingAttributeDefinition =
        fetchSourcingAttributeDefinition(
            holidayCutoffRulesRequest.getOrgId(),
            holidayCutoffRulesRequest.getSourcingAttributesDefinitionId());
    if (StringUtils.hasLength(sourcingAttributeDefinition.getOptAttributes())) {
      List<HolidayCutoffEntity> holidayCutoffEntityList =
          holidayCutOffRepository
              .findByOrgIdAndSourcingAttributesDefinitionIdAndHolidayCutoffRuleIn(
                  holidayCutoffRulesRequest.getOrgId(),
                  holidayCutoffRulesRequest.getSourcingAttributesDefinitionId(),
                  generateAllHolidayCutoffRuleCombinations(
                      holidayCutoffRulesRequest
                          .getSourcingAttributeValuesInfo()
                          .getRequiredAttributesValue(),
                      holidayCutoffRulesRequest
                          .getSourcingAttributeValuesInfo()
                          .getOptionalAttributesValue()));

      return HolidayCutoffRulesResponse.builder()
          .holidayCutoffInfo(INSTANCE.toHolidayCutoffInfoList(holidayCutoffEntityList))
          .build();
    }

    List<HolidayCutoffEntity> holidayCutoffEntityList = new ArrayList<>();
    Optional<HolidayCutoffEntity> holidayCutoffEntity =
        holidayCutOffRepository.findByOrgIdAndSourcingAttributesDefinitionIdAndHolidayCutoffRule(
            holidayCutoffRulesRequest.getOrgId(),
            holidayCutoffRulesRequest.getSourcingAttributesDefinitionId(),
            holidayCutoffRulesRequest
                .getSourcingAttributeValuesInfo()
                .getRequiredAttributesValue());

    holidayCutoffEntity.ifPresent(holidayCutoffEntityList::add);
    return HolidayCutoffRulesResponse.builder()
        .holidayCutoffInfo(INSTANCE.toHolidayCutoffInfoList(holidayCutoffEntityList))
        .build();
  }

  private List<String> generateAllHolidayCutoffRuleCombinations(
      String requiredAttributesValue, String optionalAttributesValue) {
    List<String> requiredAttributes = Arrays.asList(requiredAttributesValue.split(COLON_DELIMITER));
    List<String> optionalAttributes =
        Objects.nonNull(optionalAttributesValue)
            ? Arrays.asList(optionalAttributesValue.split(COLON_DELIMITER, -1))
            : new ArrayList<>();

    Set<String> combinations = new HashSet<>();
    int optionalSize = optionalAttributes.size();

    // Iterate over all possible combinations of optional attributes
    for (int i = 0; i < (1 << optionalSize); i++) {
      StringBuilder combination = new StringBuilder();
      combination.append(String.join(COLON_DELIMITER, requiredAttributes));

      for (int j = 0; j < optionalSize; j++) {
        combination.append(COLON_DELIMITER);
        // Append the optional attribute if the bit is set and it's not empty
        if ((i & (1 << j)) != 0 && !optionalAttributes.get(j).isEmpty()) {
          combination.append(optionalAttributes.get(j));
        }
      }

      combinations.add(combination.toString());
    }

    return new ArrayList<>(combinations);
  }

  private String getHolidayCutoffRule(HolidayCutoffRulesRequest holidayCutoffRulesRequest) {
    return StringUtils.hasLength(
            holidayCutoffRulesRequest.getSourcingAttributeValuesInfo().getOptionalAttributesValue())
        ? holidayCutoffRulesRequest
            .getSourcingAttributeValuesInfo()
            .getRequiredAttributesValue()
            .concat(COLON_DELIMITER)
            .concat(
                holidayCutoffRulesRequest
                    .getSourcingAttributeValuesInfo()
                    .getOptionalAttributesValue())
        : holidayCutoffRulesRequest.getSourcingAttributeValuesInfo().getRequiredAttributesValue();
  }
}
