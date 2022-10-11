package com.hbc.dataupload.service;

import com.hbc.calendar.domain.feign.CalendarFeign;
import com.hbc.calendar.domain.outbound.CalendarResponse;
import com.hbc.calendar.domain.outbound.CarrierServiceCalendarResponse;
import com.hbc.calendar.domain.outbound.NodeCalendarResponse;
import com.hbc.common.base.PagePayload;
import com.hbc.dataupload.domain.dto.CalendarDto;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CalendarDataService {
  private final CalendarFeign calendarFeign;

  public PagePayload<CalendarDto> getCalendarList(
      String orgId, Integer pageNo, Integer pageSize, String sortBy, String sortOrder) {
    PagePayload<CalendarDto> calendarDtoPagePayload = new PagePayload<>();
    List<CalendarDto> responseList = new ArrayList<>();

    PagePayload<CalendarResponse> calendarResponse =
        calendarFeign
            .getCalendarListWithPagination(orgId, pageNo, pageSize, sortBy, sortOrder)
            .getPayload();

    List<CalendarResponse> calendarResponseList = calendarResponse.getData();
    for (CalendarResponse response : calendarResponseList) {
      List<NodeCalendarResponse> nodeCalendarResponseList =
          calendarFeign
              .getNodeCalendars(response.getCalendarId(), response.getOrgId())
              .getPayload();

      List<CarrierServiceCalendarResponse> carrierCalendarResponseList =
          calendarFeign
              .getCarrierCalendars(response.getCalendarId(), response.getOrgId())
              .getPayload();

      responseList.add(
          setCalendarDto(response, nodeCalendarResponseList, carrierCalendarResponseList));
    }

    calendarDtoPagePayload.setPagination(calendarResponse.getPagination());
    calendarDtoPagePayload.setData(responseList);
    return calendarDtoPagePayload;
  }

  private CalendarDto setCalendarDto(
      CalendarResponse calendarResponse,
      List<NodeCalendarResponse> nodeCalendarResponseList,
      List<CarrierServiceCalendarResponse> carrierCalendarResponseList) {
    var calendarDto = new CalendarDto();
    calendarDto.setCalendarId(calendarResponse.getCalendarId());
    calendarDto.setOrgId(calendarResponse.getOrgId());
    calendarDto.setDescription(calendarResponse.getDescription());
    calendarDto.setIsMondayWorking(calendarResponse.getIsMondayWorking());
    calendarDto.setIsTuesdayWorking(calendarResponse.getIsTuesdayWorking());
    calendarDto.setIsWednesdayWorking(calendarResponse.getIsWednesdayWorking());
    calendarDto.setIsThursdayWorking(calendarResponse.getIsThursdayWorking());
    calendarDto.setIsFridayWorking(calendarResponse.getIsFridayWorking());
    calendarDto.setIsSaturdayWorking(calendarResponse.getIsSaturdayWorking());
    calendarDto.setIsSundayWorking(calendarResponse.getIsSundayWorking());
    calendarDto.setExceptionDays(
        calendarResponse.getExceptionDays() == null
            ? new ArrayList<>()
            : calendarResponse.getExceptionDays());
    calendarDto.setIsActive(
        !nodeCalendarResponseList.isEmpty() || !carrierCalendarResponseList.isEmpty());

    return calendarDto;
  }
}
