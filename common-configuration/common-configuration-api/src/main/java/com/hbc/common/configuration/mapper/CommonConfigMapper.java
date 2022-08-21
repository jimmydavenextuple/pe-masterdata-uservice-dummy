package com.hbc.common.configuration.mapper;

import com.hbc.common.configuration.api.domain.dto.CommonConfigurationDto;
import com.hbc.common.configuration.api.domain.inbound.CreateCommonConfigurationRequest;
import com.hbc.common.configuration.api.domain.inbound.UpdateCommonConfigurationRequest;
import com.hbc.common.configuration.domain.entity.CommonConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CommonConfigMapper {
  CommonConfigurationDto toCommonMasterConfigurationDto(
      CommonConfiguration globalConfigurationEntity);

  CommonConfiguration toCommonMasterConfiguration(
      CreateCommonConfigurationRequest createGlobalConfigurationRequest);

  CommonConfiguration toCommonMasterConfiguration(
      String orgId, String type, String key, UpdateCommonConfigurationRequest baseRequest);
}
