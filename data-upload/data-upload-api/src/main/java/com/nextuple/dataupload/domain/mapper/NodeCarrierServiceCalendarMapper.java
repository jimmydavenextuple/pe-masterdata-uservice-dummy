package com.nextuple.dataupload.domain.mapper;

import com.nextuple.calendar.domain.outbound.NodeCarrierServiceCalendarResponse;
import com.nextuple.dataupload.domain.pojo.PickUpCalendar;
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
