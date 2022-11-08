package com.nextuple.dataupload.helper;

public enum NodeCarrierSelectionPriorityEnum {
  LATEST("L", "0"),
  EARLIEST("E", "1");

  public final String key;
  public final String value;

  private NodeCarrierSelectionPriorityEnum(String key, String value) {
    this.key = key;
    this.value = value;
  }
}
