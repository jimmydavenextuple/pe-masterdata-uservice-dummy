package com.hbc.dataupload.service;

import com.hbc.calendar.domain.feign.CalendarFeign;
import com.hbc.calendar.domain.outbound.CarrierServiceCalendarResponse;
import com.hbc.carrier.domain.feign.CarrierFeign;
import com.hbc.carrier.domain.outbound.CarrierServiceResponse;
import com.hbc.common.base.PagePayload;
import com.hbc.dataupload.domain.dto.CarrierTransitDto;
import com.hbc.dataupload.domain.mapper.CarrierTransitTimeMapper;
import com.hbc.postgres.config.ReaderDS;
import com.hbc.transit.domain.dto.TransitTimeEntriesDto;
import com.hbc.transit.domain.feign.TransitFeign;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CarrierTransitTimeService {

  private final CarrierFeign carrierFeign;

  private final CalendarFeign calendarFeign;

  private final TransitFeign transitFeign;

  public static final CarrierTransitTimeMapper INSTANCE =
      Mappers.getMapper(CarrierTransitTimeMapper.class);

  @ReaderDS
  public PagePayload<CarrierTransitDto> getCarrierTransitTimeList(
      String orgId, Integer pageNo, Integer pageSize, String sortBy, String sortOrder) {
    PagePayload<CarrierTransitDto> carrierTransitDtoPagePayload = new PagePayload<>();
    List<CarrierTransitDto> responseList = new ArrayList<>();

    PagePayload<CarrierServiceResponse> carrierResponse =
        carrierFeign
            .getCarrierServiceListWithPagination(orgId, pageNo, pageSize, sortBy, sortOrder)
            .getPayload();

    List<CarrierServiceResponse> carrierServiceResponseList = carrierResponse.getData();
    for (CarrierServiceResponse carrierServiceResponse : carrierServiceResponseList) {
      List<CarrierServiceCalendarResponse> carrierServiceCalendarResponseList = new ArrayList<>();
      try {
        carrierServiceCalendarResponseList =
            calendarFeign
                .getCarrierServiceCalendar(
                    carrierServiceResponse.getOrgId(), carrierServiceResponse.getCarrierServiceId())
                .getPayload();
      } catch (RuntimeException e) {
        log.error("Empty Carrier Service Calendar Response List");
      }

      TransitTimeEntriesDto transitTimeEntries =
          transitFeign
              .getTransitTimeEntries(
                  carrierServiceResponse.getOrgId(), carrierServiceResponse.getCarrierServiceId())
              .getPayload();

      responseList.add(
          setCarrierTransitDto(
              carrierServiceResponse, carrierServiceCalendarResponseList, transitTimeEntries));
    }
    carrierTransitDtoPagePayload.setPagination(carrierResponse.getPagination());
    carrierTransitDtoPagePayload.setData(responseList);
    return carrierTransitDtoPagePayload;
  }

  private CarrierTransitDto setCarrierTransitDto(
      CarrierServiceResponse carrierServiceResponse,
      List<CarrierServiceCalendarResponse> carrierServiceCalendarResponseList,
      TransitTimeEntriesDto transitTimeEntries) {
    Optional<CarrierServiceCalendarResponse> carrierServiceCalendarResponse =
        getActiveCarrierCalendar(carrierServiceCalendarResponseList);

    var carrierTransitDto = new CarrierTransitDto();
    carrierTransitDto.setOrgId(carrierServiceResponse.getOrgId());
    carrierTransitDto.setCarrierId(carrierServiceResponse.getCarrierId());
    carrierTransitDto.setCarrierServiceId(carrierServiceResponse.getCarrierServiceId());
    carrierTransitDto.setCarrierName(carrierServiceResponse.getCarrierName());
    carrierTransitDto.setServiceName(carrierServiceResponse.getServiceName());
    carrierTransitDto.setIsCarrierActive(
        !carrierServiceCalendarResponseList.isEmpty() && transitTimeEntries.getTotalRecords() > 0);
    carrierTransitDto.setIsCalendarAssigned(!carrierServiceCalendarResponseList.isEmpty());
    carrierServiceCalendarResponse.ifPresent(
        calendarResponse ->
            carrierTransitDto.setCarrierServiceCalendar(
                INSTANCE.toCarrierServiceCalendars(calendarResponse)));
    return carrierTransitDto;
  }

  private Optional<CarrierServiceCalendarResponse> getActiveCarrierCalendar(
      List<CarrierServiceCalendarResponse> carrierServiceCalendarResponseList) {
    String presentDate = getPresentDate();

    Optional<CarrierServiceCalendarResponse> activeCarrierCalendar =
        carrierServiceCalendarResponseList.stream()
            .filter(x -> x.getEffectiveDate().compareTo(presentDate) <= 0)
            .max(Comparator.comparing(CarrierServiceCalendarResponse::getEffectiveDate));

    if (activeCarrierCalendar.isEmpty()) {
      activeCarrierCalendar =
          carrierServiceCalendarResponseList.stream()
              .filter(x -> x.getEffectiveDate().compareTo(presentDate) > 0)
              .min(Comparator.comparing(CarrierServiceCalendarResponse::getEffectiveDate));
    }

    return activeCarrierCalendar;
  }

  private String getPresentDate() {
    var dt = new DateTime();
    var dtWithUTC = dt.withZone(DateTimeZone.forID("UTC"));

    return dtWithUTC.toString("yyyy-MM-dd");
  }
}
