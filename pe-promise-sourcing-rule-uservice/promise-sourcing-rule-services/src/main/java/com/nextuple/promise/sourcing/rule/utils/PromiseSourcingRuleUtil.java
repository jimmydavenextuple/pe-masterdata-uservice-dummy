/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.promise.sourcing.rule.utils;

import com.nextuple.common.context.Logger;
import com.nextuple.common.context.LoggerFactory;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.pojo.PageParams;
import com.nextuple.common.response.error.FieldError;
import com.nextuple.promise.sourcing.rule.api.domain.enums.SourcingAttributesDefinitionScopeEnum;
import com.nextuple.promise.sourcing.rule.api.domain.enums.SourcingAttributesDefinitionStatus;
import com.nextuple.promise.sourcing.rule.api.domain.pojo.SourcingAttributeValuesInfo;
import com.nextuple.promise.sourcing.rule.persistence.domain.SourcingAttributesDefinitionDomainDto;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;

public class PromiseSourcingRuleUtil {

  PromiseSourcingRuleUtil() {}

  private static final Logger logger = LoggerFactory.getLogger(PromiseSourcingRuleUtil.class);
  private static final String NAME = "name";
  private static final String NODE_GROUP = "nodeGroups";

  private static final String REGEX_FOR_NAME = "^[a-zA-Z0-9_\\s]+$";
  private static final String REGEX_FOR_NODE_GROUP = "^[0-9]+$";
  private static final String REGEX_FOR_ATTRIBUTES_VALUE = "[^,]+";

  private static final String REGEX_FOR_ATTRIBUTES_NAME = "[^:,]+";

  private static final Pattern NAME_PATTERN = Pattern.compile(REGEX_FOR_NAME);
  private static final Pattern NODE_GROUP_PATTERN = Pattern.compile(REGEX_FOR_NODE_GROUP);
  private static final Pattern ATTRIBUTES_VALUE_PATTERN =
      Pattern.compile(REGEX_FOR_ATTRIBUTES_VALUE);
  private static final Pattern ATTRIBUTES_NAME_PATTERN = Pattern.compile(REGEX_FOR_ATTRIBUTES_NAME);

  private static final String DEFAULT_SORT_BY = "id";
  private static final String DEFAULT_SORT_ORDER = "ASC";
  private static final int DEFAULT_PAGE_SIZE = 20;
  private static final String SOURCING_ATTRIBUTES_DEFINITION_ID = "sourcingAttributesDefinitionId";
  private static final String REQ_ATTRIBUTES_VALUE = "requiredAttributesValue";
  private static final String OPT_ATTRIBUTES_VALUE = "optionalAttributesValue";
  private static final String ORG_ID = "orgId";
  private static final String COLON_DELIMITER = ":";

  public static void validateNameFormat(String name) throws CommonServiceException {

    Matcher matcher = NAME_PATTERN.matcher(name);
    if (!matcher.matches()) {
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(NAME, FieldError.builder().rejectedValue(name).build());
      throw new CommonServiceException(
          "Invalid format! Only alphanumeric characters, underscore and whitespace allowed.",
          HttpStatus.BAD_REQUEST,
          0x1771,
          errorMap);
    }
  }

  public static void validateAttributeValuesFormat(String attributesValue)
      throws CommonServiceException {

    Matcher matcher = ATTRIBUTES_VALUE_PATTERN.matcher(attributesValue);
    if (!matcher.matches()) {
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(NAME, FieldError.builder().rejectedValue(attributesValue).build());
      throw new CommonServiceException(
          "Invalid format! All the characters except comma are allowed",
          HttpStatus.BAD_REQUEST,
          0x1771,
          errorMap);
    }
  }

  public static void validateNodeGroup(String nodeGroup) throws CommonServiceException {
    Matcher matcher = NODE_GROUP_PATTERN.matcher(nodeGroup);
    if (!matcher.matches()) {
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(NODE_GROUP, FieldError.builder().rejectedValue(nodeGroup).build());
      throw new CommonServiceException(
          "Invalid format! Only numeric characters allowed.",
          HttpStatus.BAD_REQUEST,
          0x1771,
          errorMap);
    }
  }

  public static Pageable getPageableForEmptyPageSize(PageParams pageParams) {
    Sort sort = Sort.by(DEFAULT_SORT_BY).ascending();
    if (pageParams.getSortBy().isPresent()) {
      String sortOrder = pageParams.getSortOrder().orElse(DEFAULT_SORT_ORDER);
      String sortBy = pageParams.getSortBy().orElse(DEFAULT_SORT_BY);
      sort =
          StringUtils.endsWithIgnoreCase(sortOrder, DEFAULT_SORT_ORDER)
              ? Sort.by(sortBy).ascending()
              : Sort.by(sortBy).descending();
    }
    int pageSize = pageParams.getPageSize().orElse(DEFAULT_PAGE_SIZE);
    int pageNo = pageParams.getPageNo().orElse(1);
    return pageParams.getPageSize().isEmpty()
        ? Pageable.unpaged()
        : PageRequest.of(pageNo - 1, pageSize, sort);
  }

  public static void validateAttributeValueFormat(String attributeName)
      throws CommonServiceException {
    Matcher matcher = ATTRIBUTES_NAME_PATTERN.matcher(attributeName);
    if (!matcher.matches()) {
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(NAME, FieldError.builder().rejectedValue(attributeName).build());
      throw new CommonServiceException(
          "Invalid format! All the characters except colon and comma are allowed.",
          HttpStatus.BAD_REQUEST,
          0x1771,
          errorMap);
    }
  }

  public static void validateSourcingAttributesDefinitionId(
      String reqAttributesValue,
      String optionalAttributesValue,
      Optional<SourcingAttributesDefinitionDomainDto> existingSourcingAttributesDefinitionDto,
      Long sourcingAttributesDefinitionId,
      String message)
      throws CommonServiceException {

    String errorMessage =
        String.format(
            "Invalid sourcing rule attributes definition for OPTIMIZATION scope/ Sourcing rule attributes definition exists but not in ACTIVE status with given sourcingAttributesDefinitionId : %s",
            sourcingAttributesDefinitionId);
    handleInvalidSourcingAttributeDefinition(
        sourcingAttributesDefinitionId,
        SourcingAttributesDefinitionScopeEnum.OPTIMIZATION,
        existingSourcingAttributesDefinitionDto,
        0x1771,
        errorMessage);
    String[] requiredAttributeReferencesList =
        existingSourcingAttributesDefinitionDto.get().getReqAttributes().split("\\s*,\\s*");
    String[] requiredAttributeValuesList = reqAttributesValue.split("\\s*:\\s*");
    int optionalAttributesLength = 0;
    String[] optionalAttributeValuesList = new String[0];
    if (StringUtils.hasLength(existingSourcingAttributesDefinitionDto.get().getOptAttributes())) {
      optionalAttributeValuesList =
          existingSourcingAttributesDefinitionDto.get().getOptAttributes().split("\\s*,\\s*");
      optionalAttributesLength = optionalAttributeValuesList.length;
    }

    String optimizationRuleString = reqAttributesValue;
    String[] attributeValuesList = requiredAttributeValuesList;
    if (StringUtils.hasLength(optionalAttributesValue)) {
      optimizationRuleString = optimizationRuleString + ":" + optionalAttributesValue;
      attributeValuesList =
          Stream.concat(
                  Arrays.stream(requiredAttributeValuesList),
                  Arrays.stream(optionalAttributeValuesList))
              .toArray(String[]::new);
    }

    PromiseSourcingRuleUtil.checkForRequiredAttributesLength(
        optimizationRuleString,
        requiredAttributeReferencesList,
        attributeValuesList,
        "Can't add the group definition as all the required attributes values are not present",
        "attributesValue",
        0x1771);

    PromiseSourcingRuleUtil.checkForTotalAttributesLength(
        optimizationRuleString,
        requiredAttributeReferencesList,
        optionalAttributesLength,
        attributeValuesList,
        "Can't add the optimization rule as length of attributes is more than optional and required attributes combined",
        "attributesValue",
        0x1774);
  }

  public static void handleInvalidSourcingAttributeDefinition(
      Long sourcingAttributesDefinitionId,
      SourcingAttributesDefinitionScopeEnum scope,
      Optional<SourcingAttributesDefinitionDomainDto> existingSourcingAttributesDefinitionDto,
      Integer errorCode,
      String errorMessage)
      throws CommonServiceException {
    if (existingSourcingAttributesDefinitionDto.isEmpty()
        || !(isDefinitionActive(existingSourcingAttributesDefinitionDto.get())
            && hasCorrectScope(scope, existingSourcingAttributesDefinitionDto.get()))) {
      logger.error(errorMessage);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(
          SOURCING_ATTRIBUTES_DEFINITION_ID,
          FieldError.builder().rejectedValue(sourcingAttributesDefinitionId).build());
      throw new CommonServiceException(errorMessage, HttpStatus.BAD_REQUEST, errorCode, errorMap);
    }
  }

  private static boolean hasCorrectScope(
      SourcingAttributesDefinitionScopeEnum scope,
      SourcingAttributesDefinitionDomainDto existingSourcingAttributesDefinitionDto) {
    return scope.equals(existingSourcingAttributesDefinitionDto.getScope());
  }

  private static boolean isDefinitionActive(
      SourcingAttributesDefinitionDomainDto existingSourcingAttributesDefinitionDto) {
    return SourcingAttributesDefinitionStatus.ACTIVE.equals(
        existingSourcingAttributesDefinitionDto.getStatus());
  }

  public static void checkForRequiredAttributesLength(
      String sourcingRule,
      String[] requiredAttributeReferencesList,
      String[] attributeValuesList,
      String errorMessage,
      String errorField,
      Integer errorCode)
      throws CommonServiceException {
    if (attributeValuesList.length < requiredAttributeReferencesList.length) {
      logger.error(errorMessage);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(errorField, FieldError.builder().rejectedValue(sourcingRule).build());
      throw new CommonServiceException(errorMessage, HttpStatus.BAD_REQUEST, errorCode, errorMap);
    }
  }

  public static void checkForTotalAttributesLength(
      String sourcingRule,
      String[] requiredAttributeReferencesList,
      int optionalAttributesLength,
      String[] attributeValuesList,
      String errorMessage,
      String errorField,
      Integer errorCode)
      throws CommonServiceException {
    if (attributeValuesList.length
        > (requiredAttributeReferencesList.length + optionalAttributesLength)) {
      logger.error(errorMessage);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(errorField, FieldError.builder().rejectedValue(sourcingRule).build());
      throw new CommonServiceException(errorMessage, HttpStatus.BAD_REQUEST, errorCode, errorMap);
    }
  }

  public static String getRuleFromRequiredAndOptionalAttributeValues(
      String requiredAttributeValues, String optionalAttributeValues) {
    return StringUtils.hasLength(optionalAttributeValues)
        ? requiredAttributeValues.concat(COLON_DELIMITER).concat(optionalAttributeValues)
        : requiredAttributeValues;
  }

  public static void validateNoRulesFound(
      String orgId,
      Long attributeDefinitionId,
      SourcingAttributeValuesInfo sourcingAttributeValuesInfo,
      List<?> bestRules,
      String errorMessage)
      throws CommonServiceException {
    if (bestRules.isEmpty()) {
      throw new CommonServiceException(
          errorMessage,
          HttpStatus.NOT_FOUND,
          0x1776,
          Map.of(
              SOURCING_ATTRIBUTES_DEFINITION_ID,
              FieldError.builder().rejectedValue(attributeDefinitionId).build(),
              REQ_ATTRIBUTES_VALUE,
              FieldError.builder()
                  .rejectedValue(sourcingAttributeValuesInfo.getRequiredAttributesValue())
                  .build(),
              OPT_ATTRIBUTES_VALUE,
              FieldError.builder()
                  .rejectedValue(sourcingAttributeValuesInfo.getOptionalAttributesValue())
                  .build(),
              ORG_ID,
              FieldError.builder().rejectedValue(orgId).build()));
    }
  }
}
