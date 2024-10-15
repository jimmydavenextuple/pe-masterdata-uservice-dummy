/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.weightage.configuration.persistence.utils;

import static com.nextuple.weightage.configuration.persistence.utils.WeightageConfigurationPersistenceConstants.*;

import com.nextuple.weightage.configuration.api.domain.inbound.FetchWeightageRequest;
import com.nextuple.weightage.configuration.persistence.domain.WeightageConfigurationDomainDto;
import com.nextuple.weightage.configuration.persistence.entity.WeightageConfigurationEntity;
import com.nextuple.weightage.configuration.persistence.mapper.WeightageConfigurationEntityMapper;
import java.util.List;
import org.mapstruct.factory.Mappers;

public class TestUtil {
  private static final WeightageConfigurationEntityMapper INSTANCE_WEIGHTAGE =
      Mappers.getMapper(WeightageConfigurationEntityMapper.class);

  public WeightageConfigurationDomainDto getWeightageConfigurationDomainDto() {
    WeightageConfigurationDomainDto weightageConfiguration = new WeightageConfigurationDomainDto();
    weightageConfiguration.setOrgId(ORG_ID);
    weightageConfiguration.setKey(KEYS.get(0));
    weightageConfiguration.setType(TYPE);
    weightageConfiguration.setWeightage(WEIGHTAGE);
    return weightageConfiguration;
  }

  public WeightageConfigurationEntity getWeightageConfigurationEntity() {
    return INSTANCE_WEIGHTAGE.toEntity(getWeightageConfigurationDomainDto());
  }

  public FetchWeightageRequest getFetchWeightageRequest() {
    return FetchWeightageRequest.builder().orgId(ORG_ID).type(TYPE).keys(KEYS).build();
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
}
