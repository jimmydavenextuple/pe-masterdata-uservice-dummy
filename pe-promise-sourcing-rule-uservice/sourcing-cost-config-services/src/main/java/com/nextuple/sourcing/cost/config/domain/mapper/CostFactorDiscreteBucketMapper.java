/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.sourcing.cost.config.domain.mapper;

import com.nextuple.sourcing.cost.config.domain.entity.CostFactorDiscreteBucketEntity;
import com.nextuple.sourcing.cost.config.dto.CostFactorDiscreteBucketCacheKeyDto;
import com.nextuple.sourcing.cost.config.dto.CostFactorDiscreteBucketDto;
import com.nextuple.sourcing.cost.config.inbound.CostFactorDiscreteBucketRequest;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CostFactorDiscreteBucketMapper {
  CostFactorDiscreteBucketDto convertCostFactorDiscreteBucketEntityToDto(
      CostFactorDiscreteBucketEntity entity);

  CostFactorDiscreteBucketEntity convertCostFactorDiscreteBucketRequestToEntity(
      CostFactorDiscreteBucketRequest request);

  List<CostFactorDiscreteBucketDto> convertCostFactorDiscreteBucketEntityListTODtoList(
      List<CostFactorDiscreteBucketEntity> entityList);

  List<CostFactorDiscreteBucketCacheKeyDto> toCostFactorDiscreteBucketCacheKeyResponseList(
      List<CostFactorDiscreteBucketEntity> costFactorDiscreteBucketEntities);
}
