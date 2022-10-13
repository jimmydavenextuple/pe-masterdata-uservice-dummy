package com.hbc.jobs.dashboard.enums;

import lombok.Getter;

@Getter
public enum ModuleEnum {
  CALENDAR("calendar"),
  CARRIER_SERVICE_CALENDAR("carrier-service-calendar"),
  CARRIER("carrier"),
  NODE_CALENDAR("node-calendar"),
  NODE_CARRIER_SERVICE_CALENDAR("node-carrier-service-calendar"),
  NODE_CARRIER("node-carrier"),
  NODE("node"),
  POSTAL_CODE_TIMEZONE("postal-code-timezone"),
  PROMISE_SOURCING_RULE("promise-sourcing-rule"),
  TRANSIT("transit"),
  WEIGHTAGE("weightage"),
  NODE_CARRIER_SELECTION("node-carrier-selection-upload"),
  NODE_SERVICE_OPTION_BUFFER("node-service-option-buffer"),
  TRANSIT_BUFFER("transit-buffer"),
  EDD_COMPUTATION("edd-computation"),
  UI("ui");

  private final String moduleValue;

  ModuleEnum(String moduleValue) {
    this.moduleValue = moduleValue;
  }
}
