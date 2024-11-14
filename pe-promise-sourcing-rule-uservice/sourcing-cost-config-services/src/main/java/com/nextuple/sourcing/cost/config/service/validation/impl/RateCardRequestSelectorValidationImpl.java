/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.sourcing.cost.config.service.validation.impl;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.error.FieldError;
import com.nextuple.sourcing.cost.config.dto.FilterCostFactorInfoDto;
import com.nextuple.sourcing.cost.config.dto.SelectorCostFactorInfoDto;
import com.nextuple.sourcing.cost.config.dto.costtyypesdto.CostFactorDescriptionDto;
import com.nextuple.sourcing.cost.config.dto.costtyypesdto.SelectorCfInfo;
import com.nextuple.sourcing.cost.config.inbound.CostDefinitionRequest;
import com.nextuple.sourcing.cost.config.outbound.CostTypeValidationResponse;
import com.nextuple.sourcing.cost.config.service.validation.IRateCardRequestValidation;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

@Slf4j
public class RateCardRequestSelectorValidationImpl implements IRateCardRequestValidation {
  private static final String ROW = "row";
  private static final String COLUMN = "column";
  private static final String SELECTOR_CF = "selectorCf";
  private static final String SELECTOR_CF_VALUE = "selectorCfValue";

  @Override
  public void validateCostDefinitionRequest(
      CostDefinitionRequest request, CostTypeValidationResponse response)
      throws CommonServiceException {
    validateSelector(request.getSelector(), response);
    validateRowAndColumn(
        request.getRow(),
        request.getSelector().getSelectorCfValue(),
        response.getSelectorCfInfo(),
        SelectorCfInfo::getRow,
        ROW);
    validateRowAndColumn(
        request.getColumn(),
        request.getSelector().getSelectorCfValue(),
        response.getSelectorCfInfo(),
        SelectorCfInfo::getColumn,
        COLUMN);
    validateFilters(request, response);
  }

  private void validateSelector(
      SelectorCostFactorInfoDto selectorCostFactorInfoDto,
      CostTypeValidationResponse costTypeValidationResponse)
      throws CommonServiceException {
    validateSelectorCf(selectorCostFactorInfoDto, costTypeValidationResponse);
    validateSelectorCfValue(selectorCostFactorInfoDto, costTypeValidationResponse);
  }

  private void validateSelectorCf(
      SelectorCostFactorInfoDto selectorCostFactorInfoDto,
      CostTypeValidationResponse costTypeValidationResponse)
      throws CommonServiceException {
    if (isSelectorCfInValid(selectorCostFactorInfoDto, costTypeValidationResponse)) {
      throwCommonServiceException(
          "Invalid selectorCF for cost type " + costTypeValidationResponse.getCostType(),
          HttpStatus.BAD_REQUEST,
          0x1771,
          SELECTOR_CF,
          selectorCostFactorInfoDto.getSelectorCf());
    }
  }

  private static boolean isSelectorCfInValid(
      SelectorCostFactorInfoDto selectorCostFactorInfoDto,
      CostTypeValidationResponse costTypeValidationResponse) {
    return !selectorCostFactorInfoDto
        .getSelectorCf()
        .equals(costTypeValidationResponse.getSelectorCf());
  }

  private void validateSelectorCfValue(
      SelectorCostFactorInfoDto selectorCostFactorInfoDto,
      CostTypeValidationResponse costTypeValidationResponse)
      throws CommonServiceException {
    if (isSelectorCfValueInValid(selectorCostFactorInfoDto, costTypeValidationResponse)) {
      throwCommonServiceException(
          "Invalid selectorCFValue for selectorCf " + selectorCostFactorInfoDto.getSelectorCf(),
          HttpStatus.BAD_REQUEST,
          0x1771,
          SELECTOR_CF_VALUE,
          selectorCostFactorInfoDto.getSelectorCfValue());
    }
  }

  private static boolean isSelectorCfValueInValid(
      SelectorCostFactorInfoDto selectorCostFactorInfoDto,
      CostTypeValidationResponse costTypeValidationResponse) {
    return costTypeValidationResponse.getSelectorCfInfo().stream()
        .map(SelectorCfInfo::getSelectorCfValue)
        .noneMatch(s -> s.equals(selectorCostFactorInfoDto.getSelectorCfValue()));
  }

  private void validateRowAndColumn(
      String requestFactor,
      String selectorCfValue,
      List<SelectorCfInfo> selectorCfInfo,
      Function<SelectorCfInfo, CostFactorDescriptionDto> descriptionDtoFunction,
      String factorName)
      throws CommonServiceException {
    if (isCostFactorInValid(
        requestFactor, selectorCfValue, selectorCfInfo, descriptionDtoFunction)) {
      throwCommonServiceException(
          "Invalid cost factor in " + factorName,
          HttpStatus.BAD_REQUEST,
          0x1771,
          factorName,
          requestFactor);
    }
  }

  private static boolean isCostFactorInValid(
      String requestFactor,
      String selectorCfValue,
      List<SelectorCfInfo> selectorCfInfo,
      Function<SelectorCfInfo, CostFactorDescriptionDto> descriptionDtoFunction) {
    return selectorCfInfo.stream()
        .filter(info -> info.getSelectorCfValue().equals(selectorCfValue))
        .map(descriptionDtoFunction)
        .noneMatch(
            descriptionDto ->
                (Objects.isNull(descriptionDto) && Objects.isNull(requestFactor))
                    || (!ObjectUtils.isEmpty(descriptionDto)
                        && descriptionDto.getCostFactor().equals(requestFactor)));
  }

  private void validateFilters(
      CostDefinitionRequest request, CostTypeValidationResponse costTypeValidationResponse)
      throws CommonServiceException {
    boolean allFiltersValid =
        costTypeValidationResponse.getSelectorCfInfo().stream()
            .filter(
                selector ->
                    selector
                        .getSelectorCfValue()
                        .equals(request.getSelector().getSelectorCfValue()))
            .anyMatch(selector -> selector.getCostFactors().isEmpty());
    if (!CollectionUtils.isEmpty(request.getFilters())) {
      allFiltersValid =
          request.getFilters().stream()
              .allMatch(
                  requestFilter ->
                      isValidFilter(request, costTypeValidationResponse, requestFilter));
    }
    if (!allFiltersValid) {
      throwCommonServiceException(
          "Invalid filter or filter values", HttpStatus.BAD_REQUEST, 0x1771, null, null);
    }
  }

  private static Boolean isValidFilter(
      CostDefinitionRequest request,
      CostTypeValidationResponse costTypeValidationResponse,
      FilterCostFactorInfoDto requestFilter) {
    return costTypeValidationResponse.getSelectorCfInfo().stream()
        .filter(
            selector ->
                selector.getSelectorCfValue().equals(request.getSelector().getSelectorCfValue()))
        .findFirst()
        .map(
            responseFilter ->
                responseFilter.getCostFactors().stream()
                    .anyMatch(
                        responseFilterValue ->
                            responseFilterValue
                                    .getCostFactor()
                                    .equals(requestFilter.getCostFactor())
                                && responseFilterValue
                                    .getValues()
                                    .contains(requestFilter.getCostFactorValue())))
        .orElse(false);
  }

  private void throwCommonServiceException(
      String errorMessage, HttpStatus status, Integer errorCode, String field, String fieldValue)
      throws CommonServiceException {
    log.error(errorMessage);
    Map<String, FieldError> errorMap = null;
    if (StringUtils.hasLength(field))
      errorMap = Map.of(field, FieldError.builder().rejectedValue(fieldValue).build());
    throw new CommonServiceException(errorMessage, status, errorCode, errorMap);
  }
}
