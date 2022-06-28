package com.hbc.carrier.calendar.cache.service;

import com.hbc.calendar.domain.CalendarDaysStatusInfo;
import com.hbc.common.response.BaseResponse;
import com.hbc.core.cache.service.GenericFeignService;
import java.util.List;

public interface CarrierServiceCalendarFeignService
    extends GenericFeignService<String, BaseResponse<List<CalendarDaysStatusInfo>>> {}
