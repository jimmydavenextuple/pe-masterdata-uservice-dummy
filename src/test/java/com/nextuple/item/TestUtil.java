/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.item;

import java.util.HashMap;
import java.util.Map;

public class TestUtil {

  public static final String ORG_ID = "org-1";
  public static String ITEM_ID = "item-1";
  public static String UOM = "uom-1";

  private static final String SDND_ELIGIBLE = "sdndEligible";
  private static final String SDND_ELIGIBLE_FOR_DC = "sdndEligibleForDC";
  private static final String NEXT_DAY_ELIGIBLE = "nextdayEligible";
  private static final String NEXT_DAY_ELIGIBLE_FOR_DC = "nextdayEligibleForDC";
  private static final String EXPRESS_ELIGIBLE = "expressEligible";

  public static Map<String, Boolean> getServiceOptionEligibitiesMapForTest() {
    Map<String, Boolean> serviceOptionEligibilities = new HashMap<>();
    serviceOptionEligibilities.put(EXPRESS_ELIGIBLE, true);
    serviceOptionEligibilities.put(SDND_ELIGIBLE, true);
    serviceOptionEligibilities.put(SDND_ELIGIBLE_FOR_DC, true);
    serviceOptionEligibilities.put(NEXT_DAY_ELIGIBLE, true);
    serviceOptionEligibilities.put(NEXT_DAY_ELIGIBLE_FOR_DC, true);
    return serviceOptionEligibilities;
  }

  public static Map<String, Boolean> getServiceOptEligiblityMapForExceptionTest() {
    Map<String, Boolean> serviceOptionEligibilities = new HashMap<>();
    serviceOptionEligibilities.put(EXPRESS_ELIGIBLE, false);
    serviceOptionEligibilities.put(SDND_ELIGIBLE, false);
    serviceOptionEligibilities.put(SDND_ELIGIBLE_FOR_DC, false);
    serviceOptionEligibilities.put(NEXT_DAY_ELIGIBLE, false);
    serviceOptionEligibilities.put(NEXT_DAY_ELIGIBLE_FOR_DC, false);
    return serviceOptionEligibilities;
  }

  public Map<String, Object> getJsonProps() {
    Map<String, Object> props = new HashMap<>();
    props.put("key-deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
    props.put(
        "value-deserializer",
        "org.springframework.kafka.support.serializer.ErrorHandlingDeserializer");
    props.put("bootstrap-servers", "localhost:90924");
    props.put("enable-auto-commit", "true");
    props.put("auto-offset-reset", "latest");
    Map<String, Object> properties = new HashMap<>();
    properties.put(
        "spring-deserializer-value-delegate-class",
        "org.springframework.kafka.support.serializer.JsonDeserializer");
    properties.put("spring-json-trusted-packages", "*");
    Map<String, Object> saslProps = new HashMap<>();
    saslProps.put("mechanism", "SCRAM-SHA-512");
    saslProps.put("jaas", Map.of("config", "exampleConfig"));
    properties.put("sasl", saslProps);
    properties.put("security", Map.of("protocol", "SASL_SSL"));
    props.put("properties", properties);

    return props;
  }
}
