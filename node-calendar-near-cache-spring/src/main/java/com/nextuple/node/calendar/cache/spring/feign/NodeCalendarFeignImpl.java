package com.nextuple.node.calendar.cache.spring.feign;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.core.cache.service.GenericFeignService;
import com.nextuple.node.calendar.cache.domain.NodeCalendarDaysStatusInfo;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
    name = "master-calendar",
    url = "${spring.application.dependencies.calendar:http://master-calendar:8080/}")
public interface NodeCalendarFeignImpl
    extends GenericFeignService<String, BaseResponse<List<NodeCalendarDaysStatusInfo>>> {

  @GetMapping
  BaseResponse<List<NodeCalendarDaysStatusInfo>> get(@PathVariable String orgId);

  @GetMapping("/com/nextuple/node/calendar/cache/spring/status/{orgId}")
  BaseResponse<List<NodeCalendarDaysStatusInfo>> getNodeCalendar(
      @PathVariable("orgId") String orgId, @RequestParam("nodeId") String nodeId);
}
