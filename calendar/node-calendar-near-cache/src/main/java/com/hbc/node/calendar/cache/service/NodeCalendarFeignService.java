package com.hbc.node.calendar.cache.service;

import com.hbc.calendar.domain.CalendarDaysStatusInfo;
import com.hbc.common.response.BaseResponse;
import com.hbc.core.cache.service.GenericFeignService;
import java.util.List;

public interface NodeCalendarFeignService
    extends GenericFeignService<String, BaseResponse<List<CalendarDaysStatusInfo>>> {}
