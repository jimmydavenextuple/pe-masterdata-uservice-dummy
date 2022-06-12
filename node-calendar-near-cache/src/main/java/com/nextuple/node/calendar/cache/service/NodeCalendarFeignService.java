package com.nextuple.node.calendar.cache.service;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.core.cache.service.GenericFeignService;
import com.nextuple.node.calendar.cache.domain.NodeCalendarDaysStatusInfo;
import java.util.List;

public interface NodeCalendarFeignService
    extends GenericFeignService<String, BaseResponse<List<NodeCalendarDaysStatusInfo>>> {}
