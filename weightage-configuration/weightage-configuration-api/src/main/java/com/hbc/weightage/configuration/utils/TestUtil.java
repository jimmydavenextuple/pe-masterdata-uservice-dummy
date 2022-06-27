package com.hbc.weightage.configuration.utils;

import static com.hbc.weightage.configuration.utils.WeightageConfigurationConstants.KEYS;
import static com.hbc.weightage.configuration.utils.WeightageConfigurationConstants.ORG_ID;
import static com.hbc.weightage.configuration.utils.WeightageConfigurationConstants.TYPE;
import static com.hbc.weightage.configuration.utils.WeightageConfigurationConstants.WEIGHTAGE;

import com.hbc.weightage.configuration.api.domain.dto.WeightageConfigurationDto;
import com.hbc.weightage.configuration.api.domain.inbound.CreateWeightageConfigurationRequest;
import com.hbc.weightage.configuration.api.domain.inbound.FetchWeightageRequest;
import com.hbc.weightage.configuration.api.domain.inbound.UpdateWeightageConfigurationRequest;
import com.hbc.weightage.configuration.domain.entity.WeightageConfiguration;
import com.hbc.weightage.configuration.domain.mapper.WeightageConfigurationMapper;
import java.util.HashMap;
import java.util.Map;
import org.mapstruct.factory.Mappers;

public class TestUtil {
  private static final WeightageConfigurationMapper INSTANCE_WEIGHTAGE =
      Mappers.getMapper(WeightageConfigurationMapper.class);

  public WeightageConfiguration getWeightageConfiguration() {
    WeightageConfiguration weightageConfiguration = new WeightageConfiguration();
    weightageConfiguration.setOrgId(ORG_ID);
    weightageConfiguration.setKey(KEYS.get(0));
    weightageConfiguration.setType(TYPE);
    weightageConfiguration.setWeightage(WEIGHTAGE);
    return weightageConfiguration;
  }

  public WeightageConfigurationDto getWeightageConfigurationDto() {
    return INSTANCE_WEIGHTAGE.convertToWeightageConfigurationDto(getWeightageConfiguration());
  }

  public FetchWeightageRequest getFetchWeightageRequest() {
    return FetchWeightageRequest.builder().orgId(ORG_ID).type(TYPE).keys(KEYS).build();
  }

  public Map<String, Float> getFetchWeightageResponse() {
    Map<String, Float> response = new HashMap<>();
    response.put("P1", 100F);
    return response;
  }

  public CreateWeightageConfigurationRequest getCreateWeightageConfigurationRequest() {
    return CreateWeightageConfigurationRequest.builder()
        .orgId(ORG_ID)
        .type(TYPE)
        .key(KEYS.get(0))
        .weightage(WEIGHTAGE)
        .build();
  }

  public UpdateWeightageConfigurationRequest getUpdateWeightageConfigurationRequest() {
    return UpdateWeightageConfigurationRequest.builder()
        .weightage(WEIGHTAGE)
        .key(KEYS.get(0))
        .type(TYPE)
        .build();
  }
}
