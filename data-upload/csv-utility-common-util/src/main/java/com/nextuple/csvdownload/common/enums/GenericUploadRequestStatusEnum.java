package com.nextuple.csvdownload.common.enums;

public enum GenericUploadRequestStatusEnum {
  CREATED("CREATED", false),
  ERROR("ERROR", false);

  public final String status;
  public final Boolean canModify;

  GenericUploadRequestStatusEnum(String status, Boolean canModify) {
    this.status = status;
    this.canModify = canModify;
  }

  public String getStatus() {
    return status;
  }
}
