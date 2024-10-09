/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.weightage.configuration;

import static com.nextuple.weightage.configuration.utils.WeightageConfigurationConstants.AVAILABILITY;
import static com.nextuple.weightage.configuration.utils.WeightageConfigurationConstants.KEY;
import static com.nextuple.weightage.configuration.utils.WeightageConfigurationConstants.KEYS;
import static com.nextuple.weightage.configuration.utils.WeightageConfigurationConstants.ORG_ID;
import static com.nextuple.weightage.configuration.utils.WeightageConfigurationConstants.TYPE;
import static com.nextuple.weightage.configuration.utils.WeightageConfigurationConstants.WEIGHTAGE;

import com.nextuple.weightage.configuration.api.domain.dto.WeightageCacheKeyDto;
import com.nextuple.weightage.configuration.api.domain.dto.WeightageConfigurationDto;
import com.nextuple.weightage.configuration.api.domain.inbound.CreateWeightageConfigurationRequest;
import com.nextuple.weightage.configuration.api.domain.inbound.FetchWeightageRequest;
import com.nextuple.weightage.configuration.api.domain.inbound.UpdateWeightageConfigurationRequest;
import com.nextuple.weightage.configuration.domain.mapper.WeightageConfigurationMapper;
import com.nextuple.weightage.configuration.persistence.domain.WeightageConfigurationDomainDto;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.mapstruct.factory.Mappers;

public class TestUtil {
  private static final WeightageConfigurationMapper INSTANCE_WEIGHTAGE =
      Mappers.getMapper(WeightageConfigurationMapper.class);

  public WeightageConfigurationDomainDto getWeightageConfigurationDomainDto() {
    WeightageConfigurationDomainDto weightageConfiguration = new WeightageConfigurationDomainDto();
    weightageConfiguration.setOrgId(ORG_ID);
    weightageConfiguration.setKey(KEYS.get(0));
    weightageConfiguration.setType(TYPE);
    weightageConfiguration.setWeightage(WEIGHTAGE);
    return weightageConfiguration;
  }

  public WeightageConfigurationDto getWeightageConfigurationDto() {
    return INSTANCE_WEIGHTAGE.convertToWeightageConfigurationDto(
        getWeightageConfigurationDomainDto());
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

  public List<WeightageConfigurationDomainDto> getWeightageConfigurationList() {
    WeightageConfigurationDomainDto weightageConfiguration1 = new WeightageConfigurationDomainDto();
    weightageConfiguration1.setOrgId(ORG_ID);
    weightageConfiguration1.setKey(KEYS.get(0));
    weightageConfiguration1.setType(TYPE);
    weightageConfiguration1.setWeightage(WEIGHTAGE);

    WeightageConfigurationDomainDto weightageConfiguration2 = new WeightageConfigurationDomainDto();
    weightageConfiguration2.setOrgId(ORG_ID);
    weightageConfiguration2.setKey(KEYS.get(0));
    weightageConfiguration2.setType(AVAILABILITY);
    weightageConfiguration2.setWeightage(WEIGHTAGE);

    return List.of(weightageConfiguration1, weightageConfiguration2);
  }

  public List<WeightageCacheKeyDto> getWeightageCacheKeyDtoList() {
    WeightageCacheKeyDto weightageCacheKeyDto1 =
        WeightageCacheKeyDto.builder().key(KEY).orgId(ORG_ID).type(TYPE).build();

    WeightageCacheKeyDto weightageCacheKeyDto2 =
        WeightageCacheKeyDto.builder().key(KEY).orgId(ORG_ID).type(AVAILABILITY).build();

    return List.of(weightageCacheKeyDto1, weightageCacheKeyDto2);
  }
}
