package com.nextuple.carrier.calendar.cache.spring.feign;

import com.nextuple.carrier.calendar.cache.domain.CarrierCalendarDaysStatusInfo;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.core.cache.service.GenericFeignService;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
    name = "master-calendar",
    url = "${spring.application.dependencies.calendar:http://master-calendar:8080/}")
public interface CarrierServiceCalendarFeignImpl
    extends GenericFeignService<String, BaseResponse<List<CarrierCalendarDaysStatusInfo>>> {

  @GetMapping("/{orgId}")
  BaseResponse<List<CarrierCalendarDaysStatusInfo>> get(@PathVariable String orgId);

  @GetMapping("/calendar/status/{orgId}")
  BaseResponse<List<CarrierCalendarDaysStatusInfo>> getCarrierServiceCalendar(
      @PathVariable("orgId") String orgId,
      @RequestParam("carrierServiceId") String carrierServiceId,
      @RequestParam("serviceOption") String serviceOption);
}
