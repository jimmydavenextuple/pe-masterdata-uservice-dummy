package com.hbc.item.domain.mapper;

import com.hbc.item.ItemRecord;
import com.hbc.item.domain.inbound.ItemCreationRequest;
import com.hbc.item.domain.inbound.ItemCreationRequest.ItemCreationRequestBuilder;
import java.util.HashMap;
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
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    uses = CharSequenceMapper.class)
public interface ItemRecordMapper {

  @Mapping(target = "weightUom", source = "weightUOM")
  ItemCreationRequest convertItemToItemCreationRequest(ItemRecord itemRecord);

  @AfterMapping
  default void afterMappingItemRecord(
      @MappingTarget ItemCreationRequestBuilder itemCreationRequest, ItemRecord itemRecord) {
    Map<String, Boolean> serviceOptionEligibilityMap = new HashMap<>();
    if (!ObjectUtils.isEmpty(itemRecord.getSdndEligible())) {
      serviceOptionEligibilityMap.put("sdndEligible", itemRecord.getSdndEligible());
    }
    if (!ObjectUtils.isEmpty(itemRecord.getExpressEligible())) {
      serviceOptionEligibilityMap.put("expressEligible", itemRecord.getExpressEligible());
    }
    itemCreationRequest.serviceOptionEligibilities(serviceOptionEligibilityMap);
  }
}
