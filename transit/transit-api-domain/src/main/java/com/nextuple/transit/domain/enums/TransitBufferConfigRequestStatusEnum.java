package com.nextuple.transit.domain.enums;

public enum TransitBufferConfigRequestStatusEnum {
  CREATED("CREATED", false),
  INPROGRESS("INPROGRESS", false),
  COMPLETED("COMPLETED", true),
  DELETED("DELETED", false),
  INACTIVE("INACTIVE", false),
  ERROR("ERROR", false);

  public final String status;
  public final Boolean canModify;

  TransitBufferConfigRequestStatusEnum(String status, Boolean canModify) {
    this.status = status;
    this.canModify = canModify;
  }

  public String getStatus() {
    return status;
  }
}
