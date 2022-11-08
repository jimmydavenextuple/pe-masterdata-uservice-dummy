package com.nextuple.weightage.configuration.domain.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nextuple.weightage.configuration.TestUtil;
import com.nextuple.weightage.configuration.api.domain.dto.WeightageConfigurationDto;
import com.nextuple.weightage.configuration.api.domain.inbound.CreateWeightageConfigurationRequest;
import com.nextuple.weightage.configuration.api.domain.inbound.UpdateWeightageConfigurationRequest;
import com.nextuple.weightage.configuration.domain.entity.WeightageConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

class WeightageConfigurationMapperTest {

  @InjectMocks private TestUtil testUtil;

  private static final WeightageConfigurationMapper INSTANCE =
      Mappers.getMapper(WeightageConfigurationMapper.class);

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void convertToWeightageConfigurationEntity() {
    WeightageConfigurationDto weightageConfigurationDto = testUtil.getWeightageConfigurationDto();
    WeightageConfiguration weightageConfiguration =
        INSTANCE.convertToWeightageConfigurationEntity(weightageConfigurationDto);
    assertEquals(weightageConfigurationDto.getOrgId(), weightageConfiguration.getOrgId());
  }

  @Test
  void convertToWeightageConfigurationDto() {
    WeightageConfiguration weightageConfiguration = testUtil.getWeightageConfiguration();
    WeightageConfigurationDto weightageConfigurationDto =
        INSTANCE.convertToWeightageConfigurationDto(weightageConfiguration);
    assertEquals(weightageConfiguration.getOrgId(), weightageConfigurationDto.getOrgId());
  }

  @Test
  void convertCreateWeightageConfigurationRequestToWeightageConfigurationEntity() {
    CreateWeightageConfigurationRequest createWeightageConfigurationRequest =
        testUtil.getCreateWeightageConfigurationRequest();
    WeightageConfiguration weightageConfiguration =
        INSTANCE.convertCreateWeightageConfigurationRequestToWeightageConfigurationEntity(
            createWeightageConfigurationRequest);
    assertEquals(
        createWeightageConfigurationRequest.getWeightage(), weightageConfiguration.getWeightage());
  }

  @Test
  void insertValuesFromUpdateWeightageConfigurationRequestToEntity() {
    UpdateWeightageConfigurationRequest updateWeightageConfigurationRequest =
        testUtil.getUpdateWeightageConfigurationRequest();
    WeightageConfiguration weightageConfiguration = testUtil.getWeightageConfiguration();

    INSTANCE.insertValuesFromUpdateWeightageConfigurationRequestToEntity(
        updateWeightageConfigurationRequest, weightageConfiguration);

    assertEquals(
        updateWeightageConfigurationRequest.getWeightage(), weightageConfiguration.getWeightage());
    assertEquals(updateWeightageConfigurationRequest.getType(), weightageConfiguration.getType());
    assertEquals(updateWeightageConfigurationRequest.getKey(), weightageConfiguration.getKey());
  }
}
