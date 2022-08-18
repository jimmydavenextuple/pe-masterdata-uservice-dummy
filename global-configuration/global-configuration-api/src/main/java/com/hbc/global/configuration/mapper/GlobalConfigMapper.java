package com.hbc.global.configuration.mapper;

import com.hbc.global.configuration.api.domain.dto.GlobalConfigurationDto;
import com.hbc.global.configuration.api.domain.inbound.CreateGlobalConfigurationRequest;
import com.hbc.global.configuration.api.domain.inbound.UpdateGlobalConfigurationRequest;
import com.hbc.global.configuration.domain.entity.GlobalConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface GlobalConfigMapper {
  GlobalConfigurationDto toGlobalConfigurationDto(GlobalConfiguration globalConfigurationEntity);

  GlobalConfiguration toGlobalConfiguration(
      CreateGlobalConfigurationRequest createGlobalConfigurationRequest);

  GlobalConfiguration toGlobalConfiguration(
      String orgId, String type, String key, UpdateGlobalConfigurationRequest baseRequest);
}
