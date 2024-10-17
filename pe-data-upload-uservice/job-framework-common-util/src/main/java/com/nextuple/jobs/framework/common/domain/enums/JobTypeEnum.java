/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.jobs.framework.common.domain.enums;

import com.nextuple.jobs.framework.common.enums.ModuleEnum;
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
  UPLOAD_NODE_SERVICE_OPTION_BUFFER(ModuleEnum.NODE_CARRIER),
  UPLOAD_CUSTOM_REGION(ModuleEnum.CUSTOM_REGION),
  UPLOAD_ZONES(ModuleEnum.ZONES),
  UPLOAD_COST_DEFINITION(ModuleEnum.COST_DEFINITION),
  UPLOAD_FROM_NEIP(ModuleEnum.NEIP_RECORD);

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
