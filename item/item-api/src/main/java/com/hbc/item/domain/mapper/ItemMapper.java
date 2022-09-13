package com.hbc.item.domain.mapper;

import com.hbc.item.domain.entity.ItemEntity;
import com.hbc.item.domain.events.ItemMasterEvent;
import com.hbc.item.domain.inbound.ItemCreationRequest;
import com.hbc.item.domain.inbound.ItemCreationRequest.ItemCreationRequestBuilder;
import com.hbc.item.domain.inbound.ItemUpdationRequest;
import com.hbc.item.domain.outbound.ItemResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.springframework.util.ObjectUtils;

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ItemMapper {

  ItemEntity toItemEntity(ItemCreationRequest itemCreationRequest);

  ItemResponse toItemResponse(ItemEntity itemEntity);

  List<ItemResponse> toItemResponseList(List<ItemEntity> itemEntity);

  ItemEntity updateItemEntity(
      ItemUpdationRequest itemUpdationRequest, @MappingTarget ItemEntity existingItemEntity);

  @Mapping(target = "weightUom", source = "weightUOM")
  ItemCreationRequest convertItemMasterEventToItemCreationRequest(ItemMasterEvent itemMasterEvent);

  @AfterMapping
  default void afterMappingItemMasterEvent(
      @MappingTarget ItemCreationRequestBuilder itemCreationRequest,
      ItemMasterEvent itemMasterEvent) {
    Map<String, Boolean> serviceOptionEligibilityMap = new HashMap<>();
    if (!ObjectUtils.isEmpty(itemMasterEvent.getSdndEligible())) {
      serviceOptionEligibilityMap.put("sdndEligible", itemMasterEvent.getSdndEligible());
    }
    if (!ObjectUtils.isEmpty(itemMasterEvent.getExpressEligible())) {
      serviceOptionEligibilityMap.put("expressEligible", itemMasterEvent.getExpressEligible());
    }
    itemCreationRequest.serviceOptionEligibilities(serviceOptionEligibilityMap);
  }
}
