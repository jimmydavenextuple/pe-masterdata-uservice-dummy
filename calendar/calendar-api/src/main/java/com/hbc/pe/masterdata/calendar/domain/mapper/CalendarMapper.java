package com.hbc.pe.masterdata.calendar.domain.mapper;

import com.hbc.pe.masterdata.calendar.domain.entity.CalendarEntity;
import com.hbc.pe.masterdata.calendar.domain.entity.CarrierServiceCalendarEntity;
import com.hbc.pe.masterdata.calendar.domain.entity.NodeCalendarEntity;
import com.hbc.pe.masterdata.calendar.domain.entity.NodeCarrierServiceCalendarEntity;
import com.hbc.pe.masterdata.calendar.domain.inbound.CalendarRequest;
import com.hbc.pe.masterdata.calendar.domain.inbound.CarrierServiceCalendarRequest;
import com.hbc.pe.masterdata.calendar.domain.inbound.NodeCalendarRequest;
import com.hbc.pe.masterdata.calendar.domain.inbound.NodeCarrierServiceCalendarRequest;
import com.hbc.pe.masterdata.calendar.domain.outbound.CalendarResponse;
import com.hbc.pe.masterdata.calendar.domain.outbound.CarrierServiceCalendarResponse;
import com.hbc.pe.masterdata.calendar.domain.outbound.NodeCalendarResponse;
import com.hbc.pe.masterdata.calendar.domain.outbound.NodeCarrierServiceCalendarResponse;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CalendarMapper {
  CalendarEntity convertToCalendarEntity(CalendarRequest calendarRequest);

  CalendarResponse convertToCalendarResponse(CalendarEntity calendarEntity);

  NodeCalendarEntity convertToNodeCalendarEntity(NodeCalendarRequest nodeCalendarRequest);

  NodeCalendarResponse convertToNodeCalendarResponse(NodeCalendarEntity nodeCalendarEntity);

  List<NodeCalendarResponse> convertToNodeCalendarResponseList(
      List<NodeCalendarEntity> nodeCalendarEntityList);

  CarrierServiceCalendarEntity convertToCarrierServiceCalendarEntity(
      CarrierServiceCalendarRequest carrierServiceCalendarRequest);

  CarrierServiceCalendarResponse convertToCarrierServiceCalendarResponse(
      CarrierServiceCalendarEntity carrierServiceCalendarEntity);

  List<CarrierServiceCalendarResponse> convertToCarrierServiceCalendarResponseList(
      List<CarrierServiceCalendarEntity> carrierServiceCalendarEntityList);

  NodeCarrierServiceCalendarEntity convertToNodeCarrierServiceCalendarEntity(
      NodeCarrierServiceCalendarRequest nodeCarrierServiceCalendarRequest);

  NodeCarrierServiceCalendarResponse convertToNodeCarrierServiceCalendarResponse(
      NodeCarrierServiceCalendarEntity nodeCarrierServiceCalendarEntity);

  List<NodeCarrierServiceCalendarResponse> convertToNodeCarrierServiceCalendarResponseList(
      List<NodeCarrierServiceCalendarEntity> nodeCarrierServiceCalendarEntityList);
}
