package com.nextuple.csvdownload.service;

import com.nextuple.calendar.domain.feign.CalendarFeign;
import com.nextuple.calendar.domain.outbound.CarrierServiceCalendarResponse;
import com.nextuple.calendar.domain.outbound.NodeCalendarResponse;
import com.nextuple.common.context.Logger;
import com.nextuple.common.context.LoggerFactory;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.csvdownload.exception.CarrierServiceException;
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

  public List<NodeCalendarResponse> getNodeCalendar(String orgId, String nodeId) {
    logger.debug("Processing get node calendar by orgId and nodeId");

    BaseResponse<List<NodeCalendarResponse>> response =
        calendarFeign.handleGetNodeCalendar(orgId, nodeId);

    return response.getPayload();
  }
}
