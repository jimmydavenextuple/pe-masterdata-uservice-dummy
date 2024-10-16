/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.promise.sourcing.rule.domain.mapper;

import com.nextuple.promise.sourcing.rule.api.domain.inbound.CreateSourcingAttributeRequest;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.SourcingAttributeResponse;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.edit.sourcing.rules.UpdateSourcingAttributeResponse;
import com.nextuple.promise.sourcing.rule.api.domain.pojo.AttributeInfo;
import com.nextuple.promise.sourcing.rule.persistence.domain.SourcingAttributeDomainDto;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface SourcingAttributeMapper {

  SourcingAttributeDomainDto toSourcingAttributeEntity(
      CreateSourcingAttributeRequest sourcingAttributeRequest);

  SourcingAttributeResponse toSourcingAttributeResponse(
      SourcingAttributeDomainDto sourcingAttributeDomainDto);

  @Mapping(target = "attributeId", source = "sourcingAttributeDomainDto.id")
  @Mapping(target = "attributeValue", source = "sourcingAttributeDomainDto.jsonPath")
  UpdateSourcingAttributeResponse toUpdateSourcingAttributeResponse(
      SourcingAttributeDomainDto sourcingAttributeDomainDto);

  List<UpdateSourcingAttributeResponse> toUpdateSourcingAttributeResponseList(
      List<SourcingAttributeDomainDto> sourcingAttributeEntity);

  List<UpdateSourcingAttributeResponse> convertToUpdateSourcingAttributeResponseList(
      List<AttributeInfo> attributeInfos);
}
