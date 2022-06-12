package com.nextuple.carrier.calendar.cache.service;

import com.nextuple.carrier.calendar.cache.domain.CarrierCalendarDaysStatusInfo;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.core.cache.service.GenericFeignService;
import java.util.List;

public interface CarrierServiceCalendarFeignService
    extends GenericFeignService<String, BaseResponse<List<CarrierCalendarDaysStatusInfo>>> {}
