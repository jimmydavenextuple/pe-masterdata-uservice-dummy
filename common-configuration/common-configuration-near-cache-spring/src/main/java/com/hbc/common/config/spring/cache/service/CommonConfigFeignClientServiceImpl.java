package com.hbc.common.config.spring.cache.service;

import com.hbc.common.config.spring.cache.feign.CommonConfigFeignImpl;
import com.hbc.common.configuration.api.domain.dto.CommonConfigurationDto;
import com.hbc.common.configuration.cache.domain.CommonConfigCacheKey;
import com.hbc.common.configuration.cache.domain.CommonConfigCacheValue;
import com.hbc.common.configuration.cache.domain.CommonConfigDetails;
import com.hbc.common.response.BaseResponse;
import com.hbc.core.cache.mapper.GenericMapper;
import com.hbc.core.spring.service.AbstractGenericFeignClientServiceImpl;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommonConfigFeignClientServiceImpl
    extends AbstractGenericFeignClientServiceImpl<
        CommonConfigCacheKey,
        CommonConfigCacheValue,
        String,
        BaseResponse<CommonConfigurationDto>> {

  @Autowired CommonConfigFeignImpl commonConfigFeign;

  @Autowired
  GenericMapper<
          CommonConfigCacheKey,
          CommonConfigCacheValue,
          String,
          BaseResponse<CommonConfigurationDto>>
      commonConfigMapper;

  private static final String UNDEFINED = "UNDEFINED";

  @Override
  public CommonConfigCacheValue get(CommonConfigCacheKey key) {
    try {
      BaseResponse<CommonConfigurationDto> response =
          commonConfigFeign.fetchValue(key.getOrgId(), key.getType(), key.getKey());

      if (Objects.isNull(response.getPayload())) {
        var commonConfigDetails =
            CommonConfigDetails.builder()
                .orgId(UNDEFINED)
                .type(UNDEFINED)
                .key(UNDEFINED)
                .value(UNDEFINED)
                .build();
        return CommonConfigCacheValue.builder().commonConfigDetails(commonConfigDetails).build();
      }
      return commonConfigMapper.responseToCacheValue(response);
    } catch (RuntimeException e) {
      return null;
    }
  }
}
