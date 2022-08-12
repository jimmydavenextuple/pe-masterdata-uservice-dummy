package com.hbc.dataupload.service;

import com.hbc.calendar.domain.feign.CalendarFeign;
import com.hbc.calendar.domain.outbound.CarrierServiceCalendarResponse;
import com.hbc.carrier.domain.feign.CarrierFeign;
import com.hbc.carrier.domain.outbound.CarrierServiceResponse;
import com.hbc.common.base.PagePayload;
import com.hbc.dataupload.domain.dto.CarrierTransitDto;
import com.hbc.dataupload.domain.mapper.CarrierTransitTimeMapper;
import com.hbc.transit.domain.dto.TransitTimeEntriesDto;
import com.hbc.transit.domain.feign.TransitFeign;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

  public PagePayload<CarrierTransitDto> getCarrierTransitTimeList(
      String orgId, Integer pageNo, Integer pageSize, String sortBy, Optional<String> sortOrder) {
    PagePayload<CarrierTransitDto> carrierTransitDtoPagePayload = new PagePayload<>();
    List<CarrierTransitDto> responseList = new ArrayList<>();

    PagePayload<CarrierServiceResponse> carrierResponse =
        carrierFeign
            .getCarrierServiceListWithPagination(
                orgId, pageNo, pageSize, sortBy, sortOrder.orElse(""))
            .getPayload();

    List<CarrierServiceResponse> carrierServiceResponseList = carrierResponse.getData();
    for (CarrierServiceResponse carrierServiceResponse : carrierServiceResponseList) {
      List<CarrierServiceCalendarResponse> carrierServiceCalendarResponseList = new ArrayList<>();
      try {
        carrierServiceCalendarResponseList =
            calendarFeign
                .handleGetCarrierServiceCalendar(
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

      CarrierTransitDto carrierTransitDto = new CarrierTransitDto();
      carrierTransitDto.setOrgId(carrierServiceResponse.getOrgId());
      carrierTransitDto.setCarrierId(carrierServiceResponse.getCarrierId());
      carrierTransitDto.setCarrierServiceId(carrierServiceResponse.getCarrierServiceId());
      carrierTransitDto.setCarrierName(carrierServiceResponse.getCarrierName());
      carrierTransitDto.setServiceName(carrierServiceResponse.getServiceName());
      carrierTransitDto.setIsCarrierActive(
          !carrierServiceCalendarResponseList.isEmpty()
              && transitTimeEntries.getTotalRecords() > 0);
      carrierTransitDto.setIsCalendarAssigned(!carrierServiceCalendarResponseList.isEmpty());
      carrierTransitDto.setCarrierServiceCalendars(
          INSTANCE.toCarrierServiceCalendars(carrierServiceCalendarResponseList));
      responseList.add(carrierTransitDto);
    }
    carrierTransitDtoPagePayload.setPagination(carrierResponse.getPagination());
    carrierTransitDtoPagePayload.setData(responseList);
    return carrierTransitDtoPagePayload;
  }
}
