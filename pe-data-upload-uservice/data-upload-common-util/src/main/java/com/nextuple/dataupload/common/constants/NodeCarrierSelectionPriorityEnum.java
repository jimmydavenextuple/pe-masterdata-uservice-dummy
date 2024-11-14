/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.dataupload.common.constants;

import java.util.HashMap;
import java.util.Map;

public enum NodeCarrierSelectionPriorityEnum {
  LATEST("L", "0"),
  EARLIEST("E", "1");

  private static Map<String, NodeCarrierSelectionPriorityEnum> valueMap = new HashMap<>();
  private static Map<String, NodeCarrierSelectionPriorityEnum> keyMap = new HashMap<>();

  static {
    for (final NodeCarrierSelectionPriorityEnum value : NodeCarrierSelectionPriorityEnum.values()) {
      NodeCarrierSelectionPriorityEnum.valueMap.put(value.getValue(), value);
    }
  }

  static {
    for (final NodeCarrierSelectionPriorityEnum key : NodeCarrierSelectionPriorityEnum.values()) {
      NodeCarrierSelectionPriorityEnum.keyMap.put(key.getValue(), key);
    }
  }

  private final String value;
  private final String key;

  private NodeCarrierSelectionPriorityEnum(final String key, final String value) {
    this.key = key;
    this.value = value;
  }

  public static NodeCarrierSelectionPriorityEnum getValue(final String value) {
    return NodeCarrierSelectionPriorityEnum.valueMap.get(value);
  }

  public static NodeCarrierSelectionPriorityEnum getKey(final String key) {
    return NodeCarrierSelectionPriorityEnum.keyMap.get(key);
  }

  public String getValue() {
    return this.value;
  }

  public String getKey() {
    return this.key;
  }
}
