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

@DisplayName("PromiseSourcingRuleKey Tests")
class PromiseSourcingRuleKeyTest {

  @Test
  @DisplayName("Builder Test")
  void promiseSourcingRuleKeyBuilderTest() {
    String orgId = "ABC";
    String serviceOption = "SDND";
    String destinationGeoZone = "IST";
    String allocationRuleId = "NXT";
    int priority = 1;

    PromiseSourcingRuleKey key =
        PromiseSourcingRuleKey.builder()
            .orgId(orgId)
            .serviceOption(serviceOption)
            .destinationGeoZone(destinationGeoZone)
            .allocationRuleId(allocationRuleId)
            .priority(priority)
            .build();

    assertEquals(orgId, key.getOrgId());
    assertEquals(serviceOption, key.getServiceOption());
    assertEquals(destinationGeoZone, key.getDestinationGeoZone());
    assertEquals(allocationRuleId, key.getAllocationRuleId());
    assertEquals(priority, key.getPriority());
  }

  @Test
  @DisplayName("AllArgsConstructor Test")
  void promiseSourcingRuleKeyAllArgsConstructorTest() {
    String orgId = "XYZ";
    String serviceOption = "EXPRESS";
    String destinationGeoZone = "NYC";
    String allocationRuleId = "ORD";
    int priority = 2;

    PromiseSourcingRuleKey key =
        new PromiseSourcingRuleKey(
            orgId, serviceOption, destinationGeoZone, allocationRuleId, priority);

    assertEquals(orgId, key.getOrgId());
    assertEquals(serviceOption, key.getServiceOption());
    assertEquals(destinationGeoZone, key.getDestinationGeoZone());
    assertEquals(allocationRuleId, key.getAllocationRuleId());
    assertEquals(priority, key.getPriority());
  }

  @Test
  @DisplayName("Default Constructor Test")
  void promiseSourcingRuleKeyDefaultConstructorTest() {
    PromiseSourcingRuleKey key = new PromiseSourcingRuleKey();

    assertNull(key.getOrgId());
    assertNull(key.getServiceOption());
    assertNull(key.getDestinationGeoZone());
    assertNull(key.getAllocationRuleId());
    assertEquals(0, key.getPriority());
  }

  @Test
  @DisplayName("Equals and HashCode Test")
  void promiseSourcingRuleKeyEqualsAndHashCodeTest() {
    PromiseSourcingRuleKey key1 = PromiseSourcingRuleKey.builder().orgId("A").build();
    PromiseSourcingRuleKey key2 = PromiseSourcingRuleKey.builder().orgId("A").build();

    assertEquals(key1, key2);
    assertEquals(key1.hashCode(), key2.hashCode());
  }

  @Test
  @DisplayName("ToString Test")
  void promiseSourcingRuleKeyToStringTest() {
    String orgId = "XYZ";
    String serviceOption = "EXPRESS";
    String destinationGeoZone = "NYC";
    String allocationRuleId = "ORD";
    int priority = 2;

    PromiseSourcingRuleKey key =
        PromiseSourcingRuleKey.builder()
            .orgId(orgId)
            .serviceOption(serviceOption)
            .destinationGeoZone(destinationGeoZone)
            .allocationRuleId(allocationRuleId)
            .priority(priority)
            .build();

    String expectedToString =
        "PromiseSourcingRuleKey(orgId="
            + orgId
            + ", serviceOption="
            + serviceOption
            + ", destinationGeoZone="
            + destinationGeoZone
            + ", allocationRuleId="
            + allocationRuleId
            + ", priority="
            + priority
            + ")";
    assertEquals(expectedToString, key.toString());
  }

  @Test
  @DisplayName("Getter and Setter Test")
  void promiseSourcingRuleKeyGetterSetterTest() {
    PromiseSourcingRuleKey key = new PromiseSourcingRuleKey();

    key.setOrgId("ABC");
    assertEquals("ABC", key.getOrgId());

    key.setServiceOption("SDND");
    assertEquals("SDND", key.getServiceOption());

    key.setDestinationGeoZone("IST");
    assertEquals("IST", key.getDestinationGeoZone());

    key.setAllocationRuleId("NXT");
    assertEquals("NXT", key.getAllocationRuleId());

    key.setPriority(1);
    assertEquals(1, key.getPriority());
  }
}
