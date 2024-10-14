/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.promise.sourcing.rule.utils;

import static com.nextuple.promise.sourcing.rule.utils.PromiseSourcingRuleUtil.checkForRequiredAttributesLength;
import static com.nextuple.promise.sourcing.rule.utils.PromiseSourcingRuleUtil.checkForTotalAttributesLength;

import com.nextuple.common.exception.CommonServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

@Slf4j
public class FetchRulesUtil {
  FetchRulesUtil() {}

  public static final String SPLIT_REGEX = "\\s*,\\s*";
  public static final String COLON_SPLIT_REGEX = "\\s*:\\s*";
  public static final String RULE = "rule";
  public static final String REQUIRED_ATTRIBUTES_LENGTH_ERROR_MESSAGE =
      "Can’t add or fetch the rules as all the required attributes are not present";
  public static final String TOTAL_ATTRIBUTES_LENGTH_ERROR_MESSAGE =
      "Can't add or fetch the rules as length of attributes is more than optional and required attributes combined";
  public static final String RULE_NOT_FOUND_ERROR_MESSAGE = "Rule not found with given details";

  public static void validateAttributesDefinitionIdForScope(
      String reqAttrFromDefinition, String optionalAttrFromDefinition, String generatedRule)
      throws CommonServiceException {
    String[] requiredAttributeReferencesList = reqAttrFromDefinition.split(SPLIT_REGEX);
    int optionalAttributesLength =
        StringUtils.hasLength(optionalAttrFromDefinition)
            ? optionalAttrFromDefinition.split(SPLIT_REGEX).length
            : 0;
    String[] attributeValuesList = generatedRule.split(COLON_SPLIT_REGEX);
    checkForRequiredAttributesLength(
        generatedRule,
        requiredAttributeReferencesList,
        attributeValuesList,
        REQUIRED_ATTRIBUTES_LENGTH_ERROR_MESSAGE,
        RULE,
        0x1B5B);
    checkForTotalAttributesLength(
        generatedRule,
        requiredAttributeReferencesList,
        optionalAttributesLength,
        attributeValuesList,
        TOTAL_ATTRIBUTES_LENGTH_ERROR_MESSAGE,
        RULE,
        0x1B5C);
  }
}
