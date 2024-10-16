/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.sourcing.cost.config.domain.mapper;

import com.nextuple.sourcing.cost.config.domain.entity.CostFactorAuditLogEntity;
import com.nextuple.sourcing.cost.config.domain.entity.CostFactorEntity;
import com.nextuple.sourcing.cost.config.dto.CostFactorCacheKeyDto;
import com.nextuple.sourcing.cost.config.dto.CostFactorDto;
import com.nextuple.sourcing.cost.config.inbound.CostFactorRequest;
import com.nextuple.sourcing.cost.config.inbound.CostFactorUpdateRequest;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CostFactorMapper {
  CostFactorEntity toCostFactorEntity(CostFactorRequest costFactorRequest);

  CostFactorDto toCostFactorDto(CostFactorEntity costFactorEntity);

  @Mapping(target = "id", ignore = true)
  CostFactorAuditLogEntity toCostFactorAuditLogEntity(CostFactorEntity costFactorEntity);

  void updateCostFactorEntity(
      CostFactorUpdateRequest updateCostFactorRequest,
      @MappingTarget CostFactorEntity costFactorEntity);

  List<CostFactorCacheKeyDto> toCostFactorCacheKeyResponseList(
      List<CostFactorEntity> costFactorEntities);
}
