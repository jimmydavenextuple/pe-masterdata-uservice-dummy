package com.hbc.dataupload.domain.mapper;

import com.hbc.calendar.domain.outbound.NodeCarrierServiceCalendarResponse;
import com.hbc.dataupload.domain.pojo.PickUpCalendar;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface NodeCarrierServiceCalendarMapper {

  PickUpCalendar convertToPickUpCalendar(
      NodeCarrierServiceCalendarResponse nodeCarrierServiceCalendarResponse);

  List<PickUpCalendar> convertToPickUpCalendarList(
      List<NodeCarrierServiceCalendarResponse> nodeCarrierServiceCalendarResponseList);
}
