/*
 * Copyright (c) 2025., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */
package com.nextuple.pe.light.promise.ruleFilterStrategies;

import static com.nextuple.pe.light.promise.LightPromiseConstants.INBOUND_PROCESSING_TIME_FILTER;

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
  @Override
  public String getStrategyName() {
    return INBOUND_PROCESSING_TIME_FILTER;
  }

  @Override
  public List<List<TagPojo>> getFilters(Map<String, Object> facts) {
    TagPojo orgTag = createTag("orgId", facts.get("orgId"));
    if (orgTag == null) {
      return List.of(); // orgId is mandatory
    }
    List<TagPojo> optionalTags = new ArrayList<>();

    for (Map.Entry<String, Object> fact : facts.entrySet()) {
      if (!"orgId".equals(fact.getKey())
          && fact.getValue() instanceof String) { // Exclude mandatory orgId
        TagPojo tag = createTag(fact.getKey(), fact.getValue());
        if (tag != null) {
          optionalTags.add(tag);
        }
      }
    }
    List<List<TagPojo>> result = new ArrayList<>();
    int totalCombos = 1 << optionalTags.size();
    for (int i = 0; i < totalCombos; i++) {
      List<TagPojo> combo = new ArrayList<>(optionalTags.size() + 1);
      combo.add(orgTag); // Always include mandatory orgTag
      for (int j = 0; j < optionalTags.size(); j++) { // Include tag if bit is set
        if ((i & (1 << j)) != 0) {
          combo.add(optionalTags.get(j));
        }
      }
      result.add(combo);
    }
    return result;
  }

  private TagPojo createTag(String key, Object value) {
    return value != null ? new TagPojo(key, value.toString()) : null;
  }
}
