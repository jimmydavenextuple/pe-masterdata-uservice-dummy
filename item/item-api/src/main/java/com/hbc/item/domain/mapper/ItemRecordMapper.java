package com.hbc.item.domain.mapper;

import com.hbc.item.domain.inbound.ItemCreationRequest;
import com.hbc.item.domain.inbound.ItemCreationRequest.ItemCreationRequestBuilder;
import com.hbc.streams.promising.messages.PromisingRecord;
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
    Map<String, Boolean> serviceOptionEligibilityMap = new HashMap<>();
    serviceOptionEligibilityMap.put("sdndEligible", itemRecord.getSdndEligible());
    serviceOptionEligibilityMap.put("expressEligible", itemRecord.getExpressEligible());
    boolean nextDayEligible =
        itemRecord.getNextdayEligible() == null ? Boolean.FALSE : itemRecord.getNextdayEligible();
    boolean sdndEligibleForDC =
        itemRecord.getSdndEligibleForDC() == null
            ? Boolean.FALSE
            : itemRecord.getSdndEligibleForDC();
    boolean nextDayEligibleForDC =
        itemRecord.getNextdayEligibleForDC() == null
            ? Boolean.FALSE
            : itemRecord.getNextdayEligibleForDC();
    serviceOptionEligibilityMap.put("nextdayEligible", nextDayEligible);
    serviceOptionEligibilityMap.put("sdndEligibleForDC", sdndEligibleForDC);
    serviceOptionEligibilityMap.put("nextdayEligibleForDC", nextDayEligibleForDC);
    itemCreationRequest.serviceOptionEligibilities(serviceOptionEligibilityMap);
  }
}
