package com.nextuple.weightage.configuration.domain.mapper;

import com.nextuple.weightage.configuration.api.domain.dto.WeightageCacheKeyDto;
import com.nextuple.weightage.configuration.api.domain.dto.WeightageConfigurationDto;
import com.nextuple.weightage.configuration.api.domain.inbound.CreateWeightageConfigurationRequest;
import com.nextuple.weightage.configuration.api.domain.inbound.UpdateWeightageConfigurationRequest;
import com.nextuple.weightage.configuration.domain.entity.WeightageConfiguration;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface WeightageConfigurationMapper {
  WeightageConfiguration convertToWeightageConfigurationEntity(
      WeightageConfigurationDto weightageConfigurationDto);

  WeightageConfigurationDto convertToWeightageConfigurationDto(
      WeightageConfiguration weightageConfiguration);

  WeightageConfiguration convertCreateWeightageConfigurationRequestToWeightageConfigurationEntity(
      CreateWeightageConfigurationRequest createWeightageConfigurationRequest);

  void insertValuesFromUpdateWeightageConfigurationRequestToEntity(
      UpdateWeightageConfigurationRequest updateWeightageConfigurationRequest,
      @MappingTarget WeightageConfiguration weightageConfiguration);

  List<WeightageCacheKeyDto> convertToWeightageCacheKeyDtoList(
      List<WeightageConfiguration> weightageConfigurations);
}
