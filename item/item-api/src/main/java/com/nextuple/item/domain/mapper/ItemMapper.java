package com.nextuple.item.domain.mapper;

import com.nextuple.item.domain.entity.ItemEntity;
import com.nextuple.item.domain.events.ItemMasterEvent;
import com.nextuple.item.domain.inbound.ItemCreationRequest;
import com.nextuple.item.domain.inbound.ItemCreationRequest.ItemCreationRequestBuilder;
import com.nextuple.item.domain.inbound.ItemUpdationRequest;
import com.nextuple.item.domain.outbound.ItemResponse;
import java.util.HashMap;
import java.util.Map;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
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

  ItemEntity updateItemEntity(
      ItemUpdationRequest itemUpdationRequest, @MappingTarget ItemEntity existingItemEntity);

  ItemCreationRequest convertToItemCreationRequest(ItemMasterEvent itemMasterEvent);

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
