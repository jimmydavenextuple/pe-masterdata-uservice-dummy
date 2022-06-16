package com.nextuple.item.domain.mapper;

import com.nextuple.item.domain.entity.ItemEntity;
import com.nextuple.item.domain.inbound.ItemCreationRequest;
import com.nextuple.item.domain.inbound.ItemUpdationRequest;
import com.nextuple.item.domain.outbound.ItemResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ItemMapper {

  ItemEntity toItemEntity(ItemCreationRequest itemCreationRequest);

  ItemResponse toItemResponse(ItemEntity itemEntity);

  ItemEntity updateItemEntity(
      ItemUpdationRequest itemUpdationRequest, @MappingTarget ItemEntity existingItemEntity);
}
