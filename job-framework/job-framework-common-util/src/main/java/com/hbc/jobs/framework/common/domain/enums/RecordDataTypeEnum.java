package com.hbc.jobs.framework.common.domain.enums;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum RecordDataTypeEnum {
  JSON,
  XLSX,
  CSV;

  private static final Map<String, RecordDataTypeEnum> recordDataTypeEnumMap =
      Collections.unmodifiableMap(initialize());

  private static Map<String, RecordDataTypeEnum> initialize() {
    Map<String, RecordDataTypeEnum> map = new HashMap<>();
    for (RecordDataTypeEnum recordDataTypeEnum : RecordDataTypeEnum.values()) {
      map.put(recordDataTypeEnum.toString().toLowerCase(), recordDataTypeEnum);
    }
    return map;
  }

  public static RecordDataTypeEnum getTypeFromString(String type) {
    return recordDataTypeEnumMap.get(type);
  }
}
