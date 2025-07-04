/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.item.domain.mapper;

import com.nextuple.item.domain.events.ItemMasterEvent;
import com.nextuple.item.domain.inbound.ItemBaseRequest;
import com.nextuple.item.domain.inbound.ItemCreationRequest;
import com.nextuple.item.domain.outbound.ItemListResponse;
import com.nextuple.item.domain.outbound.ItemResponse;
import com.nextuple.item.domain.outbound.ItemSubstitutionInfo;
import com.nextuple.item.persistence.domain.ItemDomainDto;
import com.nextuple.item.persistence.domain.ItemSubstitutionDomainDto;
import java.util.List;
import java.util.Optional;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ItemMapper {

  ItemDomainDto toItemEntity(ItemCreationRequest itemCreationRequest);

  ItemResponse toItemResponse(ItemDomainDto itemDomainDto);

  List<ItemResponse> toItemResponseList(List<ItemDomainDto> itemDomainDtoList);

  @Mapping(target = "itemId", source = "alternateItemId")
  @Mapping(target = "uom", source = "alternateUom")
  ItemSubstitutionInfo toItemSubstitute(ItemSubstitutionDomainDto itemSubstitutionResponse);

  List<ItemSubstitutionInfo> toItemSubstituteList(
      List<ItemSubstitutionDomainDto> itemSubstitutionResponses);

  @Mapping(target = "processingTime", ignore = true)
  ItemDomainDto updateItemEntity(
      ItemBaseRequest itemBaseRequest, @MappingTarget ItemDomainDto existingItemDomainDto);

  @Mapping(target = "weightUom", source = "weightUOM")
  ItemCreationRequest convertItemMasterEventToItemCreationRequest(ItemMasterEvent itemMasterEvent);

  ItemListResponse toItemListResponse(ItemDomainDto itemDomainDtoList);

  default Double map(Optional<Double> value) {
    return Optional.ofNullable(value).orElse(Optional.empty()).orElse(null);
  }

  default Optional<Double> mapToOptional(Double value) {
    return Optional.of(value);
  }
}
