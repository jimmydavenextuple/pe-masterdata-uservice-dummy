package com.hbc.common.config.spring.cache.feign;

import com.hbc.common.configuration.api.domain.dto.CommonConfigurationDto;
import com.hbc.common.response.BaseResponse;
import com.hbc.core.cache.service.GenericFeignService;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
    name = "pe-config-common-configuration",
    url =
        "${spring.application.dependencies.common-config:http://pe-config-common-configuration:8080/}")
public interface CommonConfigFeignImpl
    extends GenericFeignService<String, BaseResponse<CommonConfigurationDto>> {

  @GetMapping("common-config/get")
  BaseResponse<CommonConfigurationDto> get(String request);

  @GetMapping("common-config/orgId/{orgId}")
  BaseResponse<CommonConfigurationDto> fetchValue(
      @PathVariable String orgId, @RequestParam String type, @RequestParam String key);
}
