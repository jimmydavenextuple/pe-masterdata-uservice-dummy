package com.nextuple.common.configuration.mapper;

import com.nextuple.common.configuration.api.domain.dto.CommonConfigurationDto;
import com.nextuple.common.configuration.api.domain.inbound.CreateCommonConfigurationRequest;
import com.nextuple.common.configuration.domain.entity.CommonConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CommonConfigMapper {
  CommonConfigurationDto toCommonConfigurationDto(CommonConfiguration commonConfiguration);

  CommonConfiguration fromCommonConfigurationRequest(
      CreateCommonConfigurationRequest commonConfigurationRequest);
}
