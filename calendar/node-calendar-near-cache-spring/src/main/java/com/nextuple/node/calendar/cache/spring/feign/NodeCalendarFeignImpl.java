package com.nextuple.node.calendar.cache.spring.feign;

import com.nextuple.calendar.domain.CalendarDaysStatusInfo;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.core.cache.service.GenericFeignService;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
    name = "pe-config-calendar",
    url = "${spring.application.dependencies.calendar:http://pe-config-calendar:8080/}")
public interface NodeCalendarFeignImpl
    extends GenericFeignService<String, BaseResponse<List<CalendarDaysStatusInfo>>> {

  @GetMapping("/{orgId}")
  BaseResponse<List<CalendarDaysStatusInfo>> get(@PathVariable String orgId);

  @GetMapping("/calendar/status/{orgId}")
  BaseResponse<List<CalendarDaysStatusInfo>> getNodeCalendar(
      @PathVariable("orgId") String orgId, @RequestParam("nodeId") String nodeId);
}
