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
import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.common.response.error.FieldError;
import com.nextuple.promise.sourcing.rule.api.domain.pojo.AttributeInfo;
import com.nextuple.promise.sourcing.rule.persistence.domain.SourcingAttributeDomainDto;
import com.nextuple.promise.sourcing.rule.persistence.service.AttributeValuesPersistenceService;
import com.nextuple.promise.sourcing.rule.persistence.service.SourcingAttributePersistenceService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@RequiredArgsConstructor
@Service
public class FetchRulesUtil {

  private final AttributeValuesPersistenceService attributeValuesPersistenceService;
  private final SourcingAttributePersistenceService sourcingAttributePersistenceService;

  private static final String ID = "id";
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

  public void getRequiredAttributeDetails(
      String orgId,
      List<AttributeInfo> reqAttributeList,
      Set<String> uniqueReqAttributes,
      String[] sourcingRuleValues,
      String[] reqAttributes)
      throws PromiseEngineException, CommonServiceException {
    for (int attrKey = 0;
        attrKey < reqAttributes.length && attrKey < sourcingRuleValues.length;
        attrKey++) {
      if (uniqueReqAttributes.contains(reqAttributes[attrKey])) continue;
      uniqueReqAttributes.add(reqAttributes[attrKey]);
      // Get sourcingAttribute values for each reqAttribute
      Optional<SourcingAttributeDomainDto> sourcingAttributeEntity =
          sourcingAttributePersistenceService.getSourcingAttributeById(
              Long.parseLong(reqAttributes[attrKey]));
      handleAttributeMappingNotFound(
          sourcingAttributeEntity,
          "No mapping for the required attribute found in sourcing attribute",
          orgId);
      AttributeInfo info =
          AttributeInfo.builder()
              .attributeId(reqAttributes[attrKey])
              .attributeValue(sourcingRuleValues[attrKey])
              .attributeName(sourcingAttributeEntity.get().getAttributeName())
              .build();

      reqAttributeList.add(info);
    }
  }

  private static void handleAttributeMappingNotFound(
      Optional<SourcingAttributeDomainDto> sourcingAttributeEntity, String s, String orgId)
      throws CommonServiceException {
    if (sourcingAttributeEntity.isEmpty()) {
      log.error(s);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ID, FieldError.builder().rejectedValue(orgId).build());
      throw new CommonServiceException(s, HttpStatus.NOT_FOUND, 0x1771, errorMap);
    }
  }

  public void getOptionalAttributeDetails(
      String orgId,
      List<AttributeInfo> optAttributeList,
      Set<String> uniqueOptAttributes,
      String[] sourcingRuleValues,
      String[] reqAttributes,
      String[] optAttributes)
      throws PromiseEngineException, CommonServiceException {
    int optAttr = 0;

    for (String optAttribute : optAttributes) {
      if (uniqueOptAttributes.contains(optAttribute) || optAttribute.equals("")) continue;
      uniqueOptAttributes.add(optAttribute);
      Optional<SourcingAttributeDomainDto> sourcingAttributeEntity =
          sourcingAttributePersistenceService.getSourcingAttributeById(
              Long.parseLong(optAttribute));
      handleAttributeMappingNotFound(
          sourcingAttributeEntity,
          "No mapping for the optional attribute found in sourcing attribute",
          orgId);

      String attributeValue;
      if (sourcingRuleValues.length == reqAttributes.length
          || optAttr >= sourcingRuleValues.length - reqAttributes.length) {
        attributeValue = "";
      } else {
        attributeValue = sourcingRuleValues[reqAttributes.length + optAttr];
      }
      optAttr++;

      AttributeInfo info =
          AttributeInfo.builder()
              .attributeId(optAttribute)
              .attributeValue(attributeValue)
              .attributeName(sourcingAttributeEntity.get().getAttributeName())
              .build();

      optAttributeList.add(info);
    }
  }
}
