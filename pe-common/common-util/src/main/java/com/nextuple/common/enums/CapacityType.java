package com.nextuple.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum CapacityType {
  OUTBOUND,
  TRANSPORT,
  RECEIVING;

  @JsonCreator
  public static CapacityType fromString(String key) {
    return CapacityType.valueOf(key.toUpperCase());
  }
}
