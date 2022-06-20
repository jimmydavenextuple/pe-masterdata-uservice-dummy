package com.nextuple.postal.code.timezone.cache.spring.feign;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.core.cache.service.GenericFeignService;
import com.nextuple.postal.code.timezone.cache.domain.PostalCodeTimezoneDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
    name = "pe-config-postal-code-timezone",
    url = "${spring.application.dependencies.postal:http://pe-config-postal-code-timezone:8080/}")
public interface PostalCodeTimezoneFeignImpl
    extends GenericFeignService<String, BaseResponse<PostalCodeTimezoneDto>> {

  @GetMapping("/{orgId}")
  BaseResponse<PostalCodeTimezoneDto> get(@PathVariable String orgId);

  @GetMapping("/postalCodeTimezone")
  BaseResponse<PostalCodeTimezoneDto> getPostalCodeTimezone(
      @RequestParam String orgId, @RequestParam String postalCodePrefix);
}
