package com.hbc.dataupload.domain.mapper;

import com.hbc.calendar.domain.outbound.CarrierServiceCalendarResponse;
import com.hbc.dataupload.domain.pojo.CarrierServiceCalendars;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CarrierTransitTimeMapper {

  List<CarrierServiceCalendars> toCarrierServiceCalendars(
      List<CarrierServiceCalendarResponse> carrierServiceCalendarResponseList);
}
