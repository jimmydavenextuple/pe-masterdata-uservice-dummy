/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.jobs.framework.common.domain.enums;

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
