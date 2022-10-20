package com.hbc.csvdownload.service;

import com.hbc.calendar.domain.feign.CalendarFeign;
import com.hbc.calendar.domain.outbound.CarrierServiceCalendarResponse;
import com.hbc.common.context.Logger;
import com.hbc.common.context.LoggerFactory;
import com.hbc.common.response.BaseResponse;
import com.hbc.csvdownload.exception.CarrierServiceException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
@RequiredArgsConstructor
public class CalenderService {

  private final CalendarFeign calendarFeign;

  private final Logger logger = LoggerFactory.getLogger(CalenderService.class);

  public List<CarrierServiceCalendarResponse> getCarrierServiceCalender(
      String orgId, String carrierServiceId) throws CarrierServiceException {
    logger.debug("Processing get Carrier service by orgId");

    BaseResponse<List<CarrierServiceCalendarResponse>> response =
        calendarFeign.getCarrierServiceCalendar(orgId, carrierServiceId);
    if (response != null && !CollectionUtils.isEmpty(response.getPayload())) {
      return response.getPayload();
    } else {
      logger.error("Carrier Service Calender does not exist for given orgId");
      throw new CarrierServiceException(
          "Carrier Service Calender does not exist for given orgId", orgId);
    }
  }
}
