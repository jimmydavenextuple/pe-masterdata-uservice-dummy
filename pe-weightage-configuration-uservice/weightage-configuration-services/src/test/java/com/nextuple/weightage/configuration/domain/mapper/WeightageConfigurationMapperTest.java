/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.weightage.configuration.domain.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nextuple.weightage.configuration.TestUtil;
import com.nextuple.weightage.configuration.api.domain.dto.WeightageConfigurationDto;
import com.nextuple.weightage.configuration.api.domain.inbound.CreateWeightageConfigurationRequest;
import com.nextuple.weightage.configuration.api.domain.inbound.UpdateWeightageConfigurationRequest;
import com.nextuple.weightage.configuration.persistence.domain.WeightageConfigurationDomainDto;
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
  void convertToWeightageConfigurationDomainDto() {
    WeightageConfigurationDto weightageConfigurationDto = testUtil.getWeightageConfigurationDto();
    WeightageConfigurationDomainDto weightageConfiguration =
        INSTANCE.convertToWeightageConfigurationDomainDto(weightageConfigurationDto);
    assertEquals(weightageConfigurationDto.getOrgId(), weightageConfiguration.getOrgId());
  }

  @Test
  void convertToWeightageConfigurationDto() {
    WeightageConfigurationDomainDto weightageConfiguration =
        testUtil.getWeightageConfigurationDomainDto();
    WeightageConfigurationDto weightageConfigurationDto =
        INSTANCE.convertToWeightageConfigurationDto(weightageConfiguration);
    assertEquals(weightageConfiguration.getOrgId(), weightageConfigurationDto.getOrgId());
  }

  @Test
  void convertCreateWeightageConfigurationRequestToWeightageConfigurationDomainDto() {
    CreateWeightageConfigurationRequest createWeightageConfigurationRequest =
        testUtil.getCreateWeightageConfigurationRequest();
    WeightageConfigurationDomainDto weightageConfiguration =
        INSTANCE.convertCreateWeightageConfigurationRequestToWeightageConfigurationDomainDto(
            createWeightageConfigurationRequest);
    assertEquals(
        createWeightageConfigurationRequest.getWeightage(), weightageConfiguration.getWeightage());
  }

  @Test
  void insertValuesFromUpdateWeightageConfigurationRequestToDomainDto() {
    UpdateWeightageConfigurationRequest updateWeightageConfigurationRequest =
        testUtil.getUpdateWeightageConfigurationRequest();
    WeightageConfigurationDomainDto weightageConfiguration =
        testUtil.getWeightageConfigurationDomainDto();

    INSTANCE.insertValuesFromUpdateWeightageConfigurationRequestToDomainDto(
        updateWeightageConfigurationRequest, weightageConfiguration);

    assertEquals(
        updateWeightageConfigurationRequest.getWeightage(), weightageConfiguration.getWeightage());
    assertEquals(updateWeightageConfigurationRequest.getType(), weightageConfiguration.getType());
    assertEquals(updateWeightageConfigurationRequest.getKey(), weightageConfiguration.getKey());
  }
}
