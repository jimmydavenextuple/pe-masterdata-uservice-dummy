package com.nextuple.jobs.framework.common.enums;

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
  UI_LOGS("ui-logs");

  private final String moduleValue;

  ModuleEnum(String moduleValue) {
    this.moduleValue = moduleValue;
  }
}
