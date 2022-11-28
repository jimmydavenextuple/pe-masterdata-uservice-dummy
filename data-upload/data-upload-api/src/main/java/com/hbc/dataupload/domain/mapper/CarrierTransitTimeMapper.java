package com.hbc.dataupload.domain.mapper;

import com.hbc.calendar.domain.outbound.CarrierServiceCalendarResponse;
import com.hbc.dataupload.domain.pojo.CarrierServiceCalendar;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CarrierTransitTimeMapper {

  CarrierServiceCalendar toCarrierServiceCalendars(
      CarrierServiceCalendarResponse carrierServiceCalendarResponse);
}
