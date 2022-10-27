package com.hbc.jobs.framework.common.domain.enums;

import com.hbc.jobs.framework.common.enums.ModuleEnum;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum JobTypeEnum {
  UPLOAD_TRANSIT_TIMES(ModuleEnum.TRANSIT),
  UPLOAD_PROCESSING_LEAD_TIMES(ModuleEnum.NODE_CARRIER),
  DELETE_TRANSIT_BUFFER(ModuleEnum.TRANSIT),
  TRANSIT_BUFFER_REQUEST(ModuleEnum.TRANSIT),
  UPLOAD_POSTAL_CODE_TIMEZONE(ModuleEnum.POSTAL_CODE_TIMEZONE),
  UPLOAD_NODES(ModuleEnum.NODES),
  UPLOAD_NODE_CARRIER(ModuleEnum.NODE_CARRIER),
  UPLOAD_NODE_CALENDER(ModuleEnum.CALENDAR),
  UPLOAD_CARRIER_SERVICE(ModuleEnum.CARRIER_SERVICE),
  UPLOAD_CARRIER_SERVICE_CALENDER(ModuleEnum.CALENDAR),
  UPLOAD_CALENDER(ModuleEnum.CALENDAR),
  UPLOAD_PICKUP_CALENDER(ModuleEnum.CALENDAR),
  UPLOAD_NODE_SERVICE_OPTION_BUFFER(ModuleEnum.NODE_CARRIER);
  private static final Map<String, JobTypeEnum> jobTypeMap =
      Collections.unmodifiableMap(initialize());
  private final ModuleEnum module;

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
