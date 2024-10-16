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
import com.nextuple.promise.sourcing.rule.persistence.domain.SourcingAttributesDefinitionDomainDto;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
  private static final String REQ_ATTRIBUTES_VALUE = "reqAttributesValue";
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
      Optional<SourcingAttributesDefinitionDomainDto> existingSourcingAttributesDefinitionDto,
      Long sourcingAttributesDefinitionId,
      String message)
      throws CommonServiceException {

    if (existingSourcingAttributesDefinitionDto.isEmpty()
        || !(existingSourcingAttributesDefinitionDto
                .get()
                .getStatus()
                .equals(SourcingAttributesDefinitionStatus.ACTIVE)
            && existingSourcingAttributesDefinitionDto
                .get()
                .getScope()
                .equals(SourcingAttributesDefinitionScopeEnum.OPTIMIZATION))) {
      logger.error(
          "Invalid sourcing rule attributes definition for OPTIMIZATION scope/ Sourcing rule attributes definition exists but not in ACTIVE status with given sourcingAttributesDefinitionId : {}",
          sourcingAttributesDefinitionId);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(
          SOURCING_ATTRIBUTES_DEFINITION_ID,
          FieldError.builder().rejectedValue(sourcingAttributesDefinitionId).build());
      throw new CommonServiceException(
          "Invalid sourcing attributes definition for OPTIMIZATION scope/ Sourcing  attributes definition exists but not in ACTIVE status",
          HttpStatus.BAD_REQUEST,
          0x1771,
          errorMap);
    }
    String[] requiredAttributeReferencesList =
        existingSourcingAttributesDefinitionDto.get().getReqAttributes().split("\\s*,\\s*");
    String[] requiredAttributeValuesList = reqAttributesValue.split("\\s*:\\s*");
    if (requiredAttributeValuesList.length < requiredAttributeReferencesList.length) {
      logger.error(
          "Can't add the group definition as all the required attributes values are not present");
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(
          REQ_ATTRIBUTES_VALUE, FieldError.builder().rejectedValue(reqAttributesValue).build());
      throw new CommonServiceException(message, HttpStatus.BAD_REQUEST, 0x1771, errorMap);
    }
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
}
