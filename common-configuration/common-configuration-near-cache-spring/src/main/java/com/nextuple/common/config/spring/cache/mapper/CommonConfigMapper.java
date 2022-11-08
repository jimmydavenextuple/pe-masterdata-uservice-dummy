package com.nextuple.common.config.spring.cache.mapper;

import com.nextuple.common.configuration.api.domain.dto.CommonConfigurationDto;
import com.nextuple.common.configuration.cache.domain.CommonConfigCacheKey;
import com.nextuple.common.configuration.cache.domain.CommonConfigCacheValue;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.core.cache.mapper.GenericMapper;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

@Component
public class CommonConfigMapper
    implements GenericMapper<
        CommonConfigCacheKey,
        CommonConfigCacheValue,
        String,
        BaseResponse<CommonConfigurationDto>> {

  public static final DataMapper DATA_MAPPER = Mappers.getMapper(DataMapper.class);

  @Override
  public CommonConfigCacheKey requestToCacheKey(String request) {
    return null;
  }

  @Override
  public String cacheKeyToRequest(CommonConfigCacheKey cacheKey) {
    return null;
  }

  @Override
  public CommonConfigCacheValue responseToCacheValue(BaseResponse<CommonConfigurationDto> resp) {
    return CommonConfigCacheValue.builder()
        .commonConfigDetails(DATA_MAPPER.convertToCommonConfigCacheValue(resp.getPayload()))
        .build();
  }

  @Override
  public BaseResponse<CommonConfigurationDto> cacheValueToResponse(
      CommonConfigCacheValue cacheValue) {
    return null;
  }
}
