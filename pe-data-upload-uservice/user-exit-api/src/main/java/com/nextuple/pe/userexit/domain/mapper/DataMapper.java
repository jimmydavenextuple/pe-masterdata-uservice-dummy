/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */
package com.nextuple.pe.userexit.domain.mapper;

import com.nextuple.common.userexit.domain.dto.UserExitConfigDataDto;
import com.nextuple.common.userexit.domain.dto.UserExitMetaDataDto;
import com.nextuple.common.userexit.domain.entity.UserExitConfigData;
import com.nextuple.common.userexit.domain.entity.UserExitMetaData;
import com.nextuple.pe.userexit.domain.inbound.CreateConfigDataRequest;
import com.nextuple.pe.userexit.domain.inbound.CreateMetaDataRequest;
import com.nextuple.pe.userexit.domain.inbound.UpdateConfigDataRequest;
import com.nextuple.pe.userexit.domain.inbound.UpdateMetaDataRequest;
import com.nextuple.pe.userexit.domain.outbound.ConfigDataResponse;
import com.nextuple.pe.userexit.domain.outbound.MetaDataResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface DataMapper {

  UserExitMetaData toUserExitMetaData(CreateMetaDataRequest createMetaDataRequest);

  MetaDataResponse toUserExitMetaDataResponse(UserExitMetaData userExitMetaData);

  UserExitConfigData toUserExitConfigData(CreateConfigDataRequest createConfigDataRequest);

  ConfigDataResponse toUserExitConfigDataResponse(UserExitConfigData userExitConfigData);

  UserExitMetaDataDto toUserExitMetaDataDto(UserExitMetaData userExitMetaData);

  UserExitConfigDataDto toUserExitConfigDataDto(UserExitConfigData userExitConfigData);

  void updateConfigDataEntity(
      UpdateConfigDataRequest updateConfigDataRequest,
      @MappingTarget UserExitConfigData userExitConfigData);

  void updateMetaDataEntity(
      UpdateMetaDataRequest updateMetaDataRequest,
      @MappingTarget UserExitMetaData userExitMetaData);
}
