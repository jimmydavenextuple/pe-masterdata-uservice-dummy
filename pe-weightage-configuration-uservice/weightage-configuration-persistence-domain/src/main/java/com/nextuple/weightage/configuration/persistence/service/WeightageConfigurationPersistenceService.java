/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.weightage.configuration.persistence.service;

import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.common.service.DomainPersistenceService;
import com.nextuple.weightage.configuration.api.domain.inbound.FetchWeightageRequest;
import com.nextuple.weightage.configuration.persistence.domain.WeightageConfigurationDomainDto;
import com.nextuple.weightage.configuration.persistence.domain.key.WeightageConfigurationDomainKey;
import java.util.List;

public interface WeightageConfigurationPersistenceService
    extends DomainPersistenceService<
        WeightageConfigurationDomainDto, WeightageConfigurationDomainKey> {
  List<WeightageConfigurationDomainDto> fetchWeightage(FetchWeightageRequest baseRequest)
      throws PromiseEngineException;

  WeightageConfigurationDomainDto saveWeightageConfiguration(
      WeightageConfigurationDomainDto weightageConfiguration) throws PromiseEngineException;

  WeightageConfigurationDomainDto getWeightageConfiguration(String orgId, String type, String key)
      throws PromiseEngineException;

  List<WeightageConfigurationDomainDto> getWeightageConfigurationsByKey(String key)
      throws PromiseEngineException;

  WeightageConfigurationDomainDto deleteWeightageConfiguration(
      WeightageConfigurationDomainDto weightageConfiguration) throws PromiseEngineException;

  List<WeightageConfigurationDomainDto> getAllWeightageConfiguration(Integer limit)
      throws PromiseEngineException;
}
