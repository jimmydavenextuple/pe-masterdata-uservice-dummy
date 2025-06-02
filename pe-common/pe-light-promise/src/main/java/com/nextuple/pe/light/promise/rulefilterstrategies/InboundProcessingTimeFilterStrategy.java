/*
 * Copyright (c) 2025., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */
package com.nextuple.pe.light.promise.rulefilterstrategies;

import static com.nextuple.pe.light.promise.LightPromiseConstants.INBOUND_PROCESSING_TIME_FILTER;
import static com.nextuple.pe.light.promise.LightPromiseConstants.ORG_ID_KEY;

import com.nextuple.neoplatform.tags.pojo.TagPojo;
import com.nextuple.rulecraft.engine.strategies.RuleFilterStrategy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// import org.springframework.stereotype.Component; // NOSONAR
// @Component // NOSONAR
// Commented out due to a raised bug that the Platform team is currently working on.  // NOSONAR
// Once resolved, these lines will be uncommented to make this component available. // NOSONAR
public class InboundProcessingTimeFilterStrategy implements RuleFilterStrategy {

  /**
   * Retrieves the name of the strategy. This name is used to identify the specific rule filter
   * strategy being implemented.
   *
   * @return The name of the strategy, defined as a constant in LightPromiseConstants.
   */
  @Override
  public String getStrategyName() {
    return INBOUND_PROCESSING_TIME_FILTER;
  }

  /**
   * Retrieves filters by generating combinations of tags based on the provided facts. The method
   * ensures the mandatory orgTag is included in all combinations.
   *
   * @param facts A map containing key-value pairs representing facts.
   * @return A list of tag combinations, where each combination is a list of TagPojo objects.
   */
  @Override
  public List<List<TagPojo>> getFilters(Map<String, Object> facts) {
    TagPojo orgTag = createTag(ORG_ID_KEY, facts.get(ORG_ID_KEY));
    if (orgTag == null) {
      return List.of();
    }
    List<TagPojo> optionalTags = getOptionalTags(facts);
    List<List<TagPojo>> result = generateTagCombinations(orgTag, optionalTags);
    return result;
  }

  /**
   * Generates all possible combinations of tags, including the mandatory orgTag. Each combination
   * includes the orgTag and a subset of optionalTags based on bit manipulation.
   *
   * @param orgTag The mandatory tag that must be included in every combination.
   * @param optionalTags The list of optional tags to generate combinations from.
   * @return A list of tag combinations, where each combination is a list of TagPojo objects.
   */
  private static List<List<TagPojo>> generateTagCombinations(
      TagPojo orgTag, List<TagPojo> optionalTags) {
    List<List<TagPojo>> result = new ArrayList<>();
    int totalCombos = 1 << optionalTags.size();
    for (int i = 0; i < totalCombos; i++) {
      List<TagPojo> combo = new ArrayList<>(optionalTags.size() + 1);
      combo.add(orgTag); // Always include mandatory orgTag

      for (int j = 0; j < optionalTags.size(); j++) {
        if ((i & (1 << j)) != 0) {
          combo.add(optionalTags.get(j));
        }
      }
      result.add(combo);
    }
    return result;
  }

  /**
   * Extracts optional tags from the facts map, excluding the mandatory orgId.
   *
   * @param facts The map containing facts to process.
   * @return A list of optional TagPojo objects.
   */
  private List<TagPojo> getOptionalTags(Map<String, Object> facts) {
    // Initialize the list to store optional tags.
    List<TagPojo> optionalTags = new ArrayList<>();

    // Iterate through each entry in the facts map.
    for (Map.Entry<String, Object> fact : facts.entrySet()) {
      if (!"orgId".equals(fact.getKey())
          && fact.getValue() instanceof String) { // Exclude mandatory orgId
        TagPojo tag = createTag(fact.getKey(), fact.getValue());
        if (tag != null) {
          optionalTags.add(tag);
        }
      }
    }
    return optionalTags;
  }

  /**
   * Creates a TagPojo object using the provided key and value. If the value is null, the method
   * returns null.
   *
   * @param key The key to be used for the TagPojo object.
   * @param value The value to be converted to a string and used for the TagPojo object.
   * @return A TagPojo object with the given key and value, or null if the value is null.
   */
  private TagPojo createTag(String key, Object value) {
    return value != null ? new TagPojo(key, value.toString()) : null;
  }
}
