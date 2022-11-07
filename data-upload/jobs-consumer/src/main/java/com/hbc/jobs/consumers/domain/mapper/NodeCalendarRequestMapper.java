package com.hbc.jobs.consumers.domain.mapper;

import com.hbc.calendar.domain.inbound.CalendarRequest;
import com.hbc.calendar.domain.inbound.CarrierServiceCalendarRequest;
import com.hbc.calendar.domain.inbound.NodeCalendarRequest;
import com.hbc.calendar.domain.inbound.NodeCarrierServiceCalendarRequest;
import com.hbc.jobs.framework.common.domain.pojo.CalendarDataUpload;
import com.hbc.jobs.framework.common.domain.pojo.CarrierCalendarUpload;
import com.hbc.jobs.framework.common.domain.pojo.NodeCalendarUpload;
import com.hbc.jobs.framework.common.domain.pojo.PickUpCalendarUpload;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface NodeCalendarRequestMapper {

  NodeCalendarRequest convertToNodeCalendarRequest(NodeCalendarUpload nodeCalendarUpload);

  CarrierServiceCalendarRequest convertToCarrierServiceCalendarRequest(
      CarrierCalendarUpload carrierCalendarUpload);

  CalendarRequest convertToCalendarRequest(CalendarDataUpload calendarDataUpload);

  NodeCarrierServiceCalendarRequest convertToNodeCarrierServiceCalendarRequest(
      PickUpCalendarUpload pickUpCalendarUpload);
}
