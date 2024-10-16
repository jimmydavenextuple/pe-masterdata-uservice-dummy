/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.promise.sourcing.rule.persistence.entity.key;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("SourcingRuleDetailsKey Tests")
class SourcingRuleDetailsKeyTest {

  @Test
  @DisplayName("Builder Test")
  void sourcingRuleDetailsKeyBuilderTest() {
    long expectedId = 123L;

    SourcingRuleDetailsKey key = SourcingRuleDetailsKey.builder().id(expectedId).build();

    assertEquals(expectedId, key.getId());
  }

  @Test
  @DisplayName("AllArgsConstructor Test")
  void sourcingRuleDetailsKeyAllArgsConstructorTest() {
    long expectedId = 456L;

    SourcingRuleDetailsKey key = new SourcingRuleDetailsKey(expectedId);

    assertEquals(expectedId, key.getId());
  }

  @Test
  @DisplayName("Default Constructor Test")
  void sourcingRuleDetailsKeyDefaultConstructorTest() {
    SourcingRuleDetailsKey key = new SourcingRuleDetailsKey();

    assertNull(key.getId());
  }

  @Test
  @DisplayName("Equals and HashCode Test")
  void sourcingRuleDetailsKeyEqualsAndHashCodeTest() {
    SourcingRuleDetailsKey key1 = SourcingRuleDetailsKey.builder().id(1L).build();
    SourcingRuleDetailsKey key2 = SourcingRuleDetailsKey.builder().id(1L).build();

    assertEquals(key1, key2);
    assertEquals(key1.hashCode(), key2.hashCode());
  }

  @Test
  @DisplayName("ToString Test")
  void sourcingRuleDetailsKeyToStringTest() {
    long expectedId = 789L;

    SourcingRuleDetailsKey key = SourcingRuleDetailsKey.builder().id(expectedId).build();

    String expectedToString = "SourcingRuleDetailsKey(id=" + expectedId + ")";
    assertEquals(expectedToString, key.toString());
  }

  @Test
  @DisplayName("Getter and Setter Test")
  void sourcingRuleDetailsKeyGetterSetterTest() {
    SourcingRuleDetailsKey key = new SourcingRuleDetailsKey();

    key.setId(1L);
    assertEquals(1L, key.getId());
  }
}
