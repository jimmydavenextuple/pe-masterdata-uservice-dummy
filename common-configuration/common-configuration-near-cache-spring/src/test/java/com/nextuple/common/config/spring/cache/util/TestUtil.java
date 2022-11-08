package com.nextuple.common.config.spring.cache.util;

import com.nextuple.common.configuration.api.domain.dto.CommonConfigurationDto;
import com.nextuple.common.configuration.cache.domain.CommonConfigCacheKey;
import com.nextuple.common.configuration.cache.domain.CommonConfigCacheValue;
import com.nextuple.common.configuration.cache.domain.CommonConfigDetails;
import com.nextuple.common.response.BaseResponse;

public class TestUtil {
  public static final String TYPE = "Node_Id_01";
  public static final String ORG_ID = "Org_Id_01";
  public static final String KEY = "DELIVERY_METHOD";
  public static final String VALUE = "SDND";

  public static final String UNDEFINED = "UNDEFINED";

  public CommonConfigCacheValue getCommonConfigCacheValue() {
    CommonConfigDetails commonConfigDetails = getCommonConfigDetails();
    return CommonConfigCacheValue.builder().commonConfigDetails(getCommonConfigDetails()).build();
  }

  private CommonConfigDetails getCommonConfigDetails() {
    return CommonConfigDetails.builder().orgId(ORG_ID).key(KEY).type(TYPE).value(VALUE).build();
  }

  private CommonConfigurationDto getCommonConfigurationDto() {
    return CommonConfigurationDto.builder().orgId(ORG_ID).key(KEY).type(TYPE).value(VALUE).build();
  }

  public BaseResponse<CommonConfigurationDto> getBaseResponseOfCommonConfiguration() {
    return BaseResponse.builder()
        .message("Common Configuration fetched successfully")
        .payload(getCommonConfigurationDto())
        .build();
  }

  public CommonConfigCacheKey getCommonConfigCacheKey() {
    return CommonConfigCacheKey.builder().orgId(ORG_ID).key(KEY).type(TYPE).build();
  }
}
