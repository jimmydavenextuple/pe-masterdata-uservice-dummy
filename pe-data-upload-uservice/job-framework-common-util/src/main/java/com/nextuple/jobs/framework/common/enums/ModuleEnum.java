/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.jobs.framework.common.enums;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

@Getter
public enum ModuleEnum {
  CALENDAR("calendar"),
  CARRIER_SERVICE("carrier-service"),

  CARRIER_SERVICE_CALENDAR("carrier-service-calendar"),
  NODE_CALENDAR("node-calendar"),
  NODE_CARRIER("node-carrier"),
  NODE_CARRIER_SELECTION("node-carrier-selection-upload"),
  NODE_SERVICE_OPTION("node-service-option"),
  NODE_SERVICE_OPTION_BUFFER("node-service-option-buffer"),
  NODES("nodes"),
  PICKUP_CALENDAR("pickup-calendar"),
  POSTAL_CODE_TIMEZONE("postal-code-timezone"),
  PROCESSING_LEAD_TIMES("processing-lead-times"),
  PROMISE_SOURCING_RULE("promise-sourcing-rule"),
  WEIGHTAGE("weightage"),
  TRANSIT_BUFFER("transit-buffer"),
  EDD_COMPUTATION("edd-computation"),
  TRANSIT("transit"),
  UI("ui"),
  UI_LOGS("ui-logs"),
  CUSTOM_REGION("custom-region"),
  ZONES("zones"),
  COST_DEFINITION("cost-definition"),
  NEIP_RECORD("neip");

  private final String moduleValue;
  private static final Map<String, ModuleEnum> moduleEnumMap = new HashMap<>();

  static {
    for (ModuleEnum value : values()) {
      moduleEnumMap.put(value.getModuleValue(), value);
    }
  }

  ModuleEnum(String moduleValue) {
    this.moduleValue = moduleValue;
  }

  public static ModuleEnum getModuleEnum(String moduleValue) {
    return moduleEnumMap.get(moduleValue);
  }
}
