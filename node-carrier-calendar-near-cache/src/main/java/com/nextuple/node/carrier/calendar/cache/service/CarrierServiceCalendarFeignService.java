package com.nextuple.node.carrier.calendar.cache.service;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.core.cache.service.GenericFeignService;
import com.nextuple.node.carrier.calendar.cache.domain.NodeCarrierCalendarDaysStatusInfo;
import java.util.List;

public interface CarrierServiceCalendarFeignService
    extends GenericFeignService<String, BaseResponse<List<NodeCarrierCalendarDaysStatusInfo>>> {}
