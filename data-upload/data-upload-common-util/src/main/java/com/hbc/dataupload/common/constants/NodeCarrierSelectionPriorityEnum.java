package com.hbc.dataupload.common.constants;

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
