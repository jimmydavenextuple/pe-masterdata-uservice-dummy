package com.nextuple.item.domain.mapper;

import com.nextuple.item.domain.inbound.ItemCreationRequest;
import com.nextuple.item.domain.inbound.ItemCreationRequest.ItemCreationRequestBuilder;
import com.nextuple.item.util.ItemUtils;
import com.nextuple.streams.promising.messages.PromisingRecord;
import java.util.*;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    uses = CharSequenceMapper.class)
public interface ItemRecordMapper {

  @Mapping(target = "weightUom", source = "weightUOM")
  ItemCreationRequest convertItemToItemCreationRequest(PromisingRecord itemRecord);

  @AfterMapping
  default void afterMappingItemRecord(
      @MappingTarget ItemCreationRequestBuilder itemCreationRequest, PromisingRecord itemRecord) {
    Map<String, Boolean> serviceOptionEligibilityMap =
        ItemUtils.getServiceOptionEligibilityMap(itemRecord);
    itemCreationRequest.serviceOptionEligibilities(serviceOptionEligibilityMap);

    Map<String, Boolean> itemFlagMapForInvNodeCompute = new HashMap<>();
    itemFlagMapForInvNodeCompute.put("sdndEligible", itemRecord.getSdndEligible());
    itemFlagMapForInvNodeCompute.put("sdndEligibleForDC", itemRecord.getSdndEligibleForDC());
    itemFlagMapForInvNodeCompute.put("nextdayEligible", itemRecord.getNextdayEligible());
    itemFlagMapForInvNodeCompute.put("nextdayEligibleForDC", itemRecord.getNextdayEligibleForDC());
    Map<String, List<String>> inventoryNodeTypesMap =
        ItemUtils.getInventoryNodeTypeMap(itemFlagMapForInvNodeCompute);
    itemCreationRequest.inventoryNodeTypes(inventoryNodeTypesMap);
  }
}
