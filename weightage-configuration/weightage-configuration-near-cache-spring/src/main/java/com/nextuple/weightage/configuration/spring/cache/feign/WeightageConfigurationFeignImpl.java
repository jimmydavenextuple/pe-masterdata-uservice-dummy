package com.nextuple.weightage.configuration.spring.cache.feign;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.core.cache.service.GenericFeignService;
import com.nextuple.weightage.configuration.cache.domain.FetchWeightageRequest;
import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
    name = "promising-engine-uservice",
    url = "${spring.application.dependencies.weightage:http://localhost:8082/}")
public interface WeightageConfigurationFeignImpl
    extends GenericFeignService<FetchWeightageRequest, BaseResponse<Map<String, Float>>> {

  @PostMapping("/weightage")
  BaseResponse<Map<String, Float>> get(@RequestBody FetchWeightageRequest baseRequest);
}
