package com.nextuple.node.calendar.cache.service;

import com.nextuple.calendar.domain.CalendarDaysStatusInfo;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.core.cache.service.GenericFeignService;
import java.util.List;

public interface NodeCalendarFeignService
    extends GenericFeignService<String, BaseResponse<List<CalendarDaysStatusInfo>>> {}
