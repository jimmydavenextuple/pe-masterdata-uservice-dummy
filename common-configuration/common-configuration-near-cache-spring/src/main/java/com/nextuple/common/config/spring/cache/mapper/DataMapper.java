package com.nextuple.common.config.spring.cache.mapper;

import com.nextuple.common.configuration.api.domain.dto.CommonConfigurationDto;
import com.nextuple.common.configuration.cache.domain.CommonConfigDetails;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface DataMapper {

  CommonConfigDetails convertToCommonConfigCacheValue(
      CommonConfigurationDto commonConfigurationDto);
}
