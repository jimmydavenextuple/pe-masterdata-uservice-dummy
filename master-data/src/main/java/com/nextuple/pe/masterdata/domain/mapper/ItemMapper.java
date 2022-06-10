package com.nextuple.pe.masterdata.domain.mapper;

import com.nextuple.pe.masterdata.domain.entity.ItemEntity;
import com.nextuple.pe.masterdata.domain.inbound.ItemCreationRequest;
import com.nextuple.pe.masterdata.domain.inbound.ItemUpdationRequest;
import com.nextuple.pe.masterdata.domain.outbound.ItemResponse;
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
