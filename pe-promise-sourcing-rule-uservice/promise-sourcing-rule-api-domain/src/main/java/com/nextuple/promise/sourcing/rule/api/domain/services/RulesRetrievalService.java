/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.promise.sourcing.rule.api.domain.services;

import com.nextuple.promise.sourcing.rule.api.domain.outbound.SourcingAttributesDefinitionResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class RulesRetrievalService<T> {
  public static final String COLON_DELIMITER = ":";

  public abstract String getRule(
      T ruleInfo, SourcingAttributesDefinitionResponse sourcingAttributesDefinitionResponse);

  public List<T> filterAllMatchingRulesByScoring(
      List<T> rulesList,
      String requiredAttrVal,
      String optionalAttrVal,
      int optionalAttrFromDefinitionSize,
      SourcingAttributesDefinitionResponse sourcingAttributeDefinitionResponse) {
    String[] orderRequiredAttrValues = requiredAttrVal.split(COLON_DELIMITER);
    String[] orderOptionalAttrValues =
        Objects.nonNull(optionalAttrVal)
            ? optionalAttrVal.split(COLON_DELIMITER, -1)
            : new String[] {};

    Map<T, Double> ruleScoreMap =
        rulesList.stream()
            .collect(
                Collectors.toMap(
                    Function.identity(),
                    info ->
                        calculateScore(
                            Arrays.stream(
                                    getRule(info, sourcingAttributeDefinitionResponse)
                                        .split(COLON_DELIMITER, -1))
                                .skip(orderRequiredAttrValues.length)
                                .collect(Collectors.joining(COLON_DELIMITER)),
                            orderOptionalAttrValues)));

    // Find the maximum score
    double maxScore =
        ruleScoreMap.values().stream()
            .mapToDouble(Double::doubleValue)
            .max()
            .orElse(Double.MIN_VALUE);

    return maxScore == 0.0
        ? fetchRulesMatchingReqAttr(
            requiredAttrVal,
            optionalAttrFromDefinitionSize,
            rulesList,
            sourcingAttributeDefinitionResponse)
        : fetchRulesByMaxScore(ruleScoreMap, maxScore);
  }

  private static double calculateScore(String ruleOptionalAttr, String[] orderOptionalAttrValues) {
    String[] ruleOptionalAttrValues = ruleOptionalAttr.split(COLON_DELIMITER, -1);
    var optionalAttrSize = orderOptionalAttrValues.length;
    double weight = Math.pow(10, optionalAttrSize);
    double score = 0.0;

    if (isEligibleForScoring(ruleOptionalAttrValues, orderOptionalAttrValues)) {
      for (String attr : ruleOptionalAttrValues) {
        if (!attr.isEmpty()) score += weight;
        optionalAttrSize--;
        weight = Math.pow(10, optionalAttrSize);
      }
    }

    return score;
  }

  private static boolean isEligibleForScoring(
      String[] ruleOptionalAttrValues, String[] orderOptionalAttrValues) {
    var hasCommonAttributeValue = false;
    for (int i = 0;
        i < Math.min(ruleOptionalAttrValues.length, orderOptionalAttrValues.length);
        i++) {
      if (!orderOptionalAttrValues[i].isEmpty() && !ruleOptionalAttrValues[i].isEmpty()) {
        hasCommonAttributeValue = true;
        if (!ruleOptionalAttrValues[i].equals(orderOptionalAttrValues[i])) return false;
      }
    }
    return hasCommonAttributeValue;
  }

  public List<T> fetchRulesMatchingReqAttr(
      String requiredAttrVal,
      int optionalAttrFromDefinitionSize,
      List<T> rulesList,
      SourcingAttributesDefinitionResponse sourcingAttributesDefinitionResponse) {
    String requiredAttrValueRule =
        String.join("", requiredAttrVal, COLON_DELIMITER.repeat(optionalAttrFromDefinitionSize));
    return rulesList.stream()
        .filter(
            info ->
                requiredAttrValueRule.equals(getRule(info, sourcingAttributesDefinitionResponse)))
        .toList();
  }

  private List<T> fetchRulesByMaxScore(Map<T, Double> ruleScoreMap, double maxScore) {
    List<T> filteredList = new ArrayList<>();
    if (maxScore > 0)
      ruleScoreMap.forEach(
          (rule, score) -> {
            if (score == maxScore) {
              filteredList.add(rule);
            }
          });
    return filteredList;
  }
}
