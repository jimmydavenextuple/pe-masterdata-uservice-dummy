package com.hbc.weightage.configuration.spring.cache.feign;

import com.hbc.common.response.BaseResponse;
import com.hbc.core.cache.service.GenericFeignService;
import com.hbc.weightage.configuration.api.domain.inbound.FetchWeightageRequest;
import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
    name = "pe-config-weightage-configuration",
    url =
        "${spring.application.dependencies.weightage:http://pe-config-weightage-configuration:8080/}")
public interface WeightageConfigurationFeignImpl
    extends GenericFeignService<FetchWeightageRequest, BaseResponse<Map<String, Float>>> {

  @PostMapping("/weightage")
  BaseResponse<Map<String, Float>> get(@RequestBody FetchWeightageRequest baseRequest);
}
