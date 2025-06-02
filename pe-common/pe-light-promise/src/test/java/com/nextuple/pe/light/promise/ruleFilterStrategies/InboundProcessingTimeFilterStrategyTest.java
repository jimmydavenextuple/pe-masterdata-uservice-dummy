/*
 * Copyright (c) 2025., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */
package com.nextuple.pe.light.promise.ruleFilterStrategies;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nextuple.neoplatform.tags.pojo.TagPojo;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class InboundProcessingTimeFilterStrategyTest {

  private final InboundProcessingTimeFilterStrategy strategy =
      new InboundProcessingTimeFilterStrategy();

  @Test
  @DisplayName("Test strategy name retrieval")
  void testGetStrategyName() {
    assertEquals("inbound-processing-time-filter", strategy.getStrategyName());
  }

  @Test
  @DisplayName("Test filters with valid facts")
  void testGetFiltersWithValidFacts() {
    Map<String, Object> facts = new HashMap<>();
    facts.put("orgId", "org123");
    facts.put("itemId", "item456");
    facts.put("nodeId", "node789");
    facts.put("supplyType", "supplyTypeABC");

    List<List<TagPojo>> filters = strategy.getFilters(facts);

    assertFalse(filters.isEmpty());
    int expectedSize = (int) Math.pow(2, 3); // 2^number of optional tags
    assertEquals(expectedSize, filters.size());
    for (List<TagPojo> combo : filters) {
      assertTrue(combo.stream().anyMatch(tag -> "orgId".equals(tag.getTagName())));
    }
  }

  @Test
  @DisplayName("Test filters with missing orgId")
  void testGetFiltersWithMissingOrgId() {
    Map<String, Object> facts = new HashMap<>();
    facts.put("itemId", "item456");
    facts.put("nodeId", "node789");
    facts.put("supplyType", "supplyTypeABC");
    List<List<TagPojo>> filters = strategy.getFilters(facts);
    assertTrue(filters.isEmpty());
  }

  @Test
  @DisplayName("Test filters with partial optional tags")
  void testGetFiltersWithPartialOptionalTags() {
    Map<String, Object> facts = new HashMap<>();
    facts.put("orgId", "org123");
    facts.put("itemId", "item456");
    List<List<TagPojo>> filters = strategy.getFilters(facts);
    assertFalse(filters.isEmpty());
    int expectedSize = (int) Math.pow(2, 1); // 2^number of optional tags
    assertEquals(expectedSize, filters.size());
    for (List<TagPojo> combo : filters) {
      assertTrue(combo.stream().anyMatch(tag -> "orgId".equals(tag.getTagName())));
    }
  }

  @Test
  @DisplayName("Test filters with no optional tags")
  void testGetFiltersWithNoOptionalTags() {
    Map<String, Object> facts = new HashMap<>();
    facts.put("orgId", "org123");
    List<List<TagPojo>> filters = strategy.getFilters(facts);
    assertFalse(filters.isEmpty());
    assertEquals(1, filters.size()); // Only one combination with orgTag
    assertTrue(filters.get(0).stream().anyMatch(tag -> "orgId".equals(tag.getTagName())));
  }

  @Test
  @DisplayName("Test filters with empty facts")
  void testGetFiltersWithEmptyFacts() {
    Map<String, Object> facts = new HashMap<>();
    List<List<TagPojo>> filters = strategy.getFilters(facts);
    assertTrue(filters.isEmpty());
  }
}
