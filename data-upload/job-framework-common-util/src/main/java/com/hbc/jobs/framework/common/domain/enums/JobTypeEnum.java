package com.hbc.jobs.framework.common.domain.enums;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum JobTypeEnum {
  UPLOAD_TRANSIT_TIMES(MasterDataModule.TRANSIT),
  UPLOAD_PROCESSING_LEAD_TIMES(MasterDataModule.NODE_CARRIER),
  DELETE_TRANSIT_BUFFER(MasterDataModule.TRANSIT);

  private static final Map<String, JobTypeEnum> jobTypeMap =
      Collections.unmodifiableMap(initialize());
  private final MasterDataModule module;

  private static Map<String, JobTypeEnum> initialize() {
    Map<String, JobTypeEnum> map = new HashMap<>();
    for (JobTypeEnum type : JobTypeEnum.values()) {
      map.put(type.toString(), type);
    }
    return map;
  }

  public static JobTypeEnum getTypeFromString(String type) {
    return jobTypeMap.get(type);
  }
}
