/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.promise.sourcing.rule.domain.mapper;

import com.nextuple.promise.sourcing.rule.api.domain.inbound.HolidayCutoffRequest;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.HolidayCutoffUpdateRequest;
import com.nextuple.promise.sourcing.rule.api.domain.pojo.HolidayCutoffInfo;
import com.nextuple.promise.sourcing.rule.domain.entity.HolidayCutoffEntity;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface HolidayCutoffMapper {

  HolidayCutoffEntity toHolidayCutoffEntity(HolidayCutoffRequest holidayCutoffRequest);

  HolidayCutoffInfo toHolidayCutoffInfo(HolidayCutoffEntity holidayCutoffEntity);

  List<HolidayCutoffInfo> toHolidayCutoffInfoList(List<HolidayCutoffEntity> holidayCutoffEntities);

  void updateHolidayCutoffEntity(
      HolidayCutoffUpdateRequest holidayCutoffUpdateRequest,
      @MappingTarget HolidayCutoffEntity holidayCutoffEntity);
}
