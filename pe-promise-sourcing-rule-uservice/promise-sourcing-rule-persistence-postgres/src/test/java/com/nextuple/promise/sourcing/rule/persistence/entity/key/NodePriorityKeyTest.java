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

@DisplayName("NodePriorityKey Tests")
class NodePriorityKeyTest {

  @Test
  @DisplayName("Builder Test")
  void nodePriorityKeyBuilderTest() {
    long expectedId = 123L;

    NodePriorityKey key = NodePriorityKey.builder().id(expectedId).build();

    assertEquals(expectedId, key.getId());
  }

  @Test
  @DisplayName("AllArgsConstructor Test")
  void nodePriorityKeyAllArgsConstructorTest() {
    long expectedId = 456L;

    NodePriorityKey key = new NodePriorityKey(expectedId);

    assertEquals(expectedId, key.getId());
  }

  @Test
  @DisplayName("Default Constructor Test")
  void nodePriorityKeyDefaultConstructorTest() {
    NodePriorityKey key = new NodePriorityKey();

    assertNull(key.getId());
  }

  @Test
  @DisplayName("Equals and HashCode Test")
  void nodePriorityKeyEqualsAndHashCodeTest() {
    NodePriorityKey key1 = NodePriorityKey.builder().id(1L).build();
    NodePriorityKey key2 = NodePriorityKey.builder().id(1L).build();

    assertEquals(key1, key2);
    assertEquals(key1.hashCode(), key2.hashCode());
  }

  @Test
  @DisplayName("ToString Test")
  void nodePriorityKeyToStringTest() {
    long expectedId = 789L;

    NodePriorityKey key = NodePriorityKey.builder().id(expectedId).build();

    String expectedToString = "NodePriorityKey(id=" + expectedId + ")";
    assertEquals(expectedToString, key.toString());
  }

  @Test
  @DisplayName("Getter and Setter Test")
  void nodePriorityKeyGetterSetterTest() {
    NodePriorityKey key = new NodePriorityKey();
    key.setId(1L);
    assertEquals(1L, key.getId());
  }
}
