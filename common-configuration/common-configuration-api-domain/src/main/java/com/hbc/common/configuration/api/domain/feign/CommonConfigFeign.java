package com.hbc.common.configuration.api.domain.feign;

import com.hbc.common.configuration.api.domain.dto.CommonConfigurationDto;
import com.hbc.common.configuration.api.domain.inbound.CreateCommonConfigurationRequest;
import com.hbc.common.response.BaseResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
    name = "pe-config-common-configuration",
    url =
        "${spring.application.dependencies.common-config:http://pe-config-common-configuration:8080/}")
public interface CommonConfigFeign {

  @PostMapping("common-config/")
  BaseResponse<CommonConfigurationDto> createCommonConfiguration(
      @RequestBody CreateCommonConfigurationRequest baseRequest);

  @PutMapping("common-config/")
  BaseResponse<CommonConfigurationDto> updateCommonConfiguration(
      @RequestBody CreateCommonConfigurationRequest baseRequest);

  @DeleteMapping("common-config/orgId/{orgId}")
  BaseResponse<CommonConfigurationDto> deleteCommonConfiguration(
      @PathVariable String orgId, @RequestParam String type, @RequestParam String key);

  @GetMapping("common-config/orgId/{orgId}")
  BaseResponse<CommonConfigurationDto> fetchValue(
      @PathVariable String orgId, @RequestParam String type, @RequestParam String key);
}
