package com.nextuple.node.carrier.calendar.cache.spring.feign;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.core.cache.service.GenericFeignService;
import com.nextuple.node.carrier.calendar.cache.domain.NodeCarrierCalendarDaysStatusInfo;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
    name = "master-calendar",
    url = "${spring.application.dependencies.calendar:http://master-calendar:8080/}")
public interface NodeCarrierCalendarFeignImpl
    extends GenericFeignService<String, BaseResponse<List<NodeCarrierCalendarDaysStatusInfo>>> {

  @GetMapping("/{orgId}")
  BaseResponse<List<NodeCarrierCalendarDaysStatusInfo>> get(@PathVariable String orgId);

  @GetMapping("calendar/status/{orgId}")
  BaseResponse<List<NodeCarrierCalendarDaysStatusInfo>> getNodeCarrierCalendar(
      @PathVariable("orgId") String orgId,
      @PathVariable("nodeId") String nodeId,
      @RequestParam("carrierServiceId") String carrierServiceId,
      @RequestParam("serviceOption") String serviceOption);
}
