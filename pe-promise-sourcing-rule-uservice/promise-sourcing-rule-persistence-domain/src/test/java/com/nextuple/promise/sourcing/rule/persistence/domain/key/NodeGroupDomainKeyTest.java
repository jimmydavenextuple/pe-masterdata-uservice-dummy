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

@DisplayName("NodeGroupDomainKey Tests")
class NodeGroupDomainKeyTest {

  @Test
  @DisplayName("Builder Test")
  void nodeGroupDomainKeyBuilderTest() {
    long expectedId = 123L;

    NodeGroupDomainKey key = NodeGroupDomainKey.builder().id(expectedId).build();

    assertEquals(expectedId, key.getId());
  }

  @Test
  @DisplayName("AllArgsConstructor Test")
  void nodeGroupDomainKeyAllArgsConstructorTest() {
    long expectedId = 456L;

    NodeGroupDomainKey key = new NodeGroupDomainKey(expectedId);

    assertEquals(expectedId, key.getId());
  }

  @Test
  @DisplayName("Default Constructor Test")
  void nodeGroupDomainKeyDefaultConstructorTest() {
    NodeGroupDomainKey key = new NodeGroupDomainKey();

    assertNull(key.getId());
  }

  @Test
  @DisplayName("Equals and HashCode Test")
  void nodeGroupDomainKeyEqualsAndHashCodeTest() {
    NodeGroupDomainKey key1 = NodeGroupDomainKey.builder().id(1L).build();
    NodeGroupDomainKey key2 = NodeGroupDomainKey.builder().id(1L).build();

    assertEquals(key1, key2);
    assertEquals(key1.hashCode(), key2.hashCode());
  }

  @Test
  @DisplayName("ToString Test")
  void nodeGroupDomainKeyToStringTest() {
    long expectedId = 789L;

    NodeGroupDomainKey key = NodeGroupDomainKey.builder().id(expectedId).build();

    String expectedToString = "NodeGroupDomainKey(id=" + expectedId + ")";
    assertEquals(expectedToString, key.toString());
  }

  @Test
  @DisplayName("Getter and Setter Test")
  void nodeGroupDomainKeyGetterSetterTest() {
    NodeGroupDomainKey key = new NodeGroupDomainKey();

    key.setId(1L);
    assertEquals(1L, key.getId());
  }
}
