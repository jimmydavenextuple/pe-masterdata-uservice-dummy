package com.nextuple.postal.code.timezone.api.domain.enums;

import lombok.Getter;

@Getter
public enum ErrorCodesEnum {
  ZIP_CODE_EXISTS(0x1772),
  ZIP_CODE_NOT_FOUND(0x1771),
  ZIP_CODE_ASSOCIATION_NOT_FOUND(0x1773),
  CUSTOM_REGION_NOT_FOUND(0x1774),
  CUSTOM_REGION_EXISTS(0x1775),
  CUSTOM_REGION_IDENTIFIER_ASSOCIATION_EXIST(0x1776),
  CUSTOM_REGION_FOR_GIVEN_ZIP_CODE_MESSAGE(0x1777),
  CUSTOM_REGION_NAME_EXISTS(0x1778),
  CUSTOM_REGION_ALREADY_EXISTS_FOR_GIVEN_CODE(0x1779);

  private final Integer errorCode;

  ErrorCodesEnum(Integer errorCode) {
    this.errorCode = errorCode;
  }
}
