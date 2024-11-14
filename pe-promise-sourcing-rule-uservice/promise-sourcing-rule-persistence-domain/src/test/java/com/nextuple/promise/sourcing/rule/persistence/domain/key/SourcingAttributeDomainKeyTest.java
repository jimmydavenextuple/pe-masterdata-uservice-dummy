/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.promise.sourcing.rule.persistence.domain.key;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("SourcingAttributeDomainKey Tests")
class SourcingAttributeDomainKeyTest {

  @Test
  @DisplayName("Builder Test")
  void sourcingAttributeDomainKeyBuilderTest() {
    long expectedId = 123L;

    SourcingAttributeDomainKey key = SourcingAttributeDomainKey.builder().id(expectedId).build();

    assertEquals(expectedId, key.getId());
  }

  @Test
  @DisplayName("AllArgsConstructor Test")
  void sourcingAttributeDomainKeyAllArgsConstructorTest() {
    long expectedId = 456L;

    SourcingAttributeDomainKey key = new SourcingAttributeDomainKey(expectedId);

    assertEquals(expectedId, key.getId());
  }

  @Test
  @DisplayName("Default Constructor Test")
  void sourcingAttributeDomainKeyDefaultConstructorTest() {
    SourcingAttributeDomainKey key = new SourcingAttributeDomainKey();

    assertNull(key.getId());
  }

  @Test
  @DisplayName("Equals and HashCode Test")
  void sourcingAttributeDomainKeyEqualsAndHashCodeTest() {
    SourcingAttributeDomainKey key1 = SourcingAttributeDomainKey.builder().id(1L).build();
    SourcingAttributeDomainKey key2 = SourcingAttributeDomainKey.builder().id(1L).build();

    assertEquals(key1, key2);
    assertEquals(key1.hashCode(), key2.hashCode());
  }

  @Test
  @DisplayName("ToString Test")
  void sourcingAttributeDomainKeyToStringTest() {
    long expectedId = 789L;

    SourcingAttributeDomainKey key = SourcingAttributeDomainKey.builder().id(expectedId).build();

    String expectedToString = "SourcingAttributeDomainKey(id=" + expectedId + ")";
    assertEquals(expectedToString, key.toString());
  }

  @Test
  @DisplayName("Getter and Setter Test")
  void sourcingAttributeDomainKeyGetterSetterTest() {
    SourcingAttributeDomainKey key = new SourcingAttributeDomainKey();

    key.setId(1L);
    assertEquals(1L, key.getId());
  }
}
