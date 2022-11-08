package com.nextuple.pe.masterdata.calendar.domain.mapper;

import com.nextuple.calendar.domain.dto.CarrierCalendarCacheKeyDto;
import com.nextuple.calendar.domain.dto.NodeCalendarCacheKeyDto;
import com.nextuple.calendar.domain.dto.NodeCarrierCalendarCacheKeyDto;
import com.nextuple.calendar.domain.inbound.CalendarRequest;
import com.nextuple.calendar.domain.inbound.CarrierServiceCalendarRequest;
import com.nextuple.calendar.domain.inbound.NodeCalendarRequest;
import com.nextuple.calendar.domain.inbound.NodeCarrierServiceCalendarRequest;
import com.nextuple.calendar.domain.outbound.CalendarResponse;
import com.nextuple.calendar.domain.outbound.CarrierServiceCalendarResponse;
import com.nextuple.calendar.domain.outbound.NodeCalendarResponse;
import com.nextuple.calendar.domain.outbound.NodeCarrierServiceCalendarResponse;
import com.nextuple.pe.masterdata.calendar.domain.entity.CalendarEntity;
import com.nextuple.pe.masterdata.calendar.domain.entity.CarrierServiceCalendarEntity;
import com.nextuple.pe.masterdata.calendar.domain.entity.NodeCalendarEntity;
import com.nextuple.pe.masterdata.calendar.domain.entity.NodeCarrierServiceCalendarEntity;
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

  List<NodeCalendarCacheKeyDto> convertToNodeCalendarCacheKeyDtoList(
      List<NodeCalendarEntity> nodeCalendarEntityList);

  List<CarrierCalendarCacheKeyDto> convertToCarrierCalendarCacheKeyDtoList(
      List<CarrierServiceCalendarEntity> carrierServiceCalendarEntityList);

  List<NodeCarrierCalendarCacheKeyDto> convertToNodeCarrierCalendarCacheKeyDtoList(
      List<NodeCarrierServiceCalendarEntity> nodeCarrierServiceCalendarEntityList);
}
