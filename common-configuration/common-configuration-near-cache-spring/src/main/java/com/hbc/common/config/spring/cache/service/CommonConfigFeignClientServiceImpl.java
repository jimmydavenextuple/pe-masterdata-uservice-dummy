package com.hbc.common.config.spring.cache.service;

import com.hbc.common.config.spring.cache.feign.CommonConfigFeignImpl;
import com.hbc.common.configuration.api.domain.dto.CommonConfigurationDto;
import com.hbc.common.configuration.cache.domain.CommonConfigCacheKey;
import com.hbc.common.configuration.cache.domain.CommonConfigCacheValue;
import com.hbc.common.response.BaseResponse;
import com.hbc.core.cache.mapper.GenericMapper;
import com.hbc.core.spring.service.AbstractGenericFeignClientServiceImpl;
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

  @Override
  public CommonConfigCacheValue get(CommonConfigCacheKey key) {
    try {
      return commonConfigMapper.responseToCacheValue(
          commonConfigFeign.fetchValue(key.getOrgId(), key.getType(), key.getKey()));
    } catch (RuntimeException e) {
      return null;
    }
  }
}
